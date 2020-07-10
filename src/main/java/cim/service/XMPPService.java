package cim.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManagerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jivesoftware.smack.ConnectionConfiguration;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.sasl.SASLErrorException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import cim.ConfigTokens;
import cim.exceptions.CimParsingExceptionCallback;
import cim.factory.P2PMessageFactory;
import cim.factory.PayloadsFactory;
import cim.factory.StringFactory;
import cim.model.P2PMessage;
import cim.model.ValidationReport;
import cim.model.XmppUser;
import cim.model.enums.ConnectionStatus;
import cim.repository.XmppUserRepository;
import cim.service.components.XmppMessageListener;
import helio.framework.objects.Tuple;

@Service
public class XMPPService {

	// -- Attributes
	
	@Autowired
	public XmppUserRepository xmppRepository;
	@Autowired
	public XmppMessageListener xmppMessageListener;
	@Autowired
	public ACLService aclService;
	@Autowired
	public ValidationService validationService;
	private AbstractXMPPConnection connection;
	protected ChatManager chatManagerReceiver;
	private Logger log = Logger.getLogger(XMPPService.class.getName());

	
	// -- Constructor

	public XMPPService() {
		// empty
	}


	// -- Getters & Setters
	
	public void updateXmppUser(XmppUser xmppUser) {
		xmppRepository.deleteAll();
		xmppRepository.save(xmppUser);
	}

	public XmppUser getXmppUser() {
		XmppUser result = null;
		List<XmppUser> xmppUsers = xmppRepository.findAll();
		if(xmppUsers.size() == 1) {
			result = xmppUsers.get(0);
		}
		return result;
	}

	// -- Methods

	
	public boolean isConnected() {
		boolean result = false;
		try {
			result = connection != null && connection.isConnected();
		}catch (Exception e) {
			e.getStackTrace();
		}
		return result;
	}

	public void disconnect() {
		if(connection!=null)
			connection.disconnect();
	}
	
	public ConnectionStatus connect() {
		ConnectionStatus status = ConnectionStatus.ALREADY_CONNECTED;
		if(!isConnected()) {
			XmppUser user = getXmppUser();
			if(user!=null) {
				status = connect(user.getUsername(), user.getPassword(), user.getXmppDomain(), user.getHost(), user.getPort());
			}else {
				status = ConnectionStatus.INCORRECT_XMPP_USER_CREDENTIALS;
			}
		}
		return status;
	}
	
	private ConnectionStatus connect(String username, String password, String xmppServerDomain, String host, int port) {
		ConnectionStatus status = ConnectionStatus.DISCONNECTED;
		try {
			// 0. Reading the certificates
			String certificates = ConfigTokens.P2P_CONFIG_CACERT_FOLDER;

			// 1. Configuring the XMPPT connection
			XMPPTCPConnectionConfiguration.Builder build =  XMPPTCPConnectionConfiguration.builder()
					.setUsernameAndPassword(username, password)
					.setXmppDomain(xmppServerDomain)
					.setHost(host)
					.setResource(username)
					.setSendPresence(true)
					.enableDefaultDebugger()
					.setPort(port)
					.setCustomSSLContext(getSSLContext(certificates))
					.setSecurityMode(ConnectionConfiguration.SecurityMode.required);

			//1.2 Add the certificates to the configuration
			addCertificatesToConfiguration(build, host, port, username); 

			// 2. Create message handler and stanza handler
			createMessageHandlers();	
			log.info("Peer message handlers established"); 
			connection.setParsingExceptionCallback(new CimParsingExceptionCallback());

			// 3. Update static user
			status = ConnectionStatus.CONNECTED;
			xmppMessageListener.setXmppUser(username); // update current xmpp user in the listener
		}catch(FileNotFoundException e) {
			status = ConnectionStatus.CERTIFICATE_NOT_FOUND;
			log.severe(e.toString());
			connection = null;
		}catch (SSLHandshakeException e) {
			status = ConnectionStatus.BAD_CERTIFICATE;
			log.severe(e.toString());
			connection = null;
		}catch(SASLErrorException e) {
			status = ConnectionStatus.INCORRECT_XMPP_USER_CREDENTIALS;
			log.severe(e.toString());
			connection = null;
		}catch(IOException e) {
			status = ConnectionStatus.INCORRECT_CERTIFICATE_PASSWORD;
			log.severe(e.toString());
			connection = null;
		}catch(Exception e) {
			status = ConnectionStatus.ERROR_CONNECTING_WITH_OPENFIRE;
			log.severe(e.toString());
			connection = null;
		}
		return status;
	}
	
	private void addCertificatesToConfiguration(XMPPTCPConnectionConfiguration.Builder build, String host, int port, String username) throws SmackException, IOException, XMPPException, InterruptedException {
		
		String message = StringFactory.concatenateStrings("Peer configured, connecting to ",host,":",port);
		log.info(message);
		XMPPTCPConnectionConfiguration config = build.build();
		connection = new XMPPTCPConnection(config);
		connection.connect();
		connection.login();
		
		message = StringFactory.concatenateStrings("Login successful: ",String.valueOf(connection.isAuthenticated()));
		log.info(message);
		message = StringFactory.concatenateStrings("SASL Mechanism used: ",connection.getUsedSaslMechansism());
		log.info(message);
		message = StringFactory.concatenateStrings("Connection secured: ",connection.isSecureConnection());
		log.info(message);
		message = StringFactory.concatenateStrings("Peer ",username," logged");
		log.info(message); 
	}
	

	/**
	 * setKeyStorePath dont work, so we must implement this method in order to handle the certificates
	 */
	private SSLContext getSSLContext(String caFile) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, KeyManagementException {
		char[] jksPassword = ConfigTokens.PASSWORD_CERT.toCharArray();
		char[] keyPassword = ConfigTokens.PASSWORD_CERT.toCharArray();

		KeyStore keyStore = KeyStore.getInstance("JKS");
		InputStream is = new FileInputStream(caFile);
		keyStore.load(is, jksPassword);
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(keyStore, keyPassword);
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(keyStore);
		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new java.security.SecureRandom());
		return sc;
	}

	/**
	 * This method instantiates a message listener that will receive {@link P2PMessages} with requests isRequestMessage()==true
	 */
	private void createMessageHandlers() {
		// 1. Create chat manager & append incoming message listener
		chatManagerReceiver = ChatManager.getInstanceFor(connection);
		chatManagerReceiver.addIncomingListener(xmppMessageListener);
		chatManagerReceiver.setXhmtlImEnabled(true);
	}

	public DeferredResult<String> sendMessage(HttpServletRequest request, Map<String, String> headers, String payload, HttpServletResponse controllerResponse) {
		P2PMessage p2pMessage = P2PMessageFactory.createP2PRequestMessage(request, headers, getXmppUser().getUsername(), getXmppUser().getXmppDomain());
		p2pMessage.setMessage(payload);
		DeferredResult<String> response = new DeferredResult<>();
		String receiverId = p2pMessage.getReceiver();

		try {
			// 1. Create a chat with the receiver
			EntityBareJid jid = JidCreate.entityBareFrom(receiverId);
			ChatManager chatManager = ChatManager.getInstanceFor(connection);
			chatManager.setXhmtlImEnabled(true);
			// 2. Append a listener to catch the response
			chatManager.addIncomingListener(new IncomingChatMessageListener() {
				@Override
				public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
					try {
						// X.1. Check that received message is actually a JSON that fits the P2PMessage
						P2PMessage p2pResponse = P2PMessageFactory.createP2PMessageFromJson(message.getBody());
						log.info("Response received");
						if(p2pResponse.getRequest()==null) {
							// X.1.A If message is contains no message return error message
							log.severe("ERROR!: Received a P2PMessage that contains no data");
							p2pResponse = P2PMessageFactory.createP2PMessage(xmppRepository.findAll().get(0).getUsername(), from.toString(), ConfigTokens.ERROR_JSON_MESSAGES_2);
							p2pResponse.setError(true);
						}
						System.out.println("---------->"+p2pResponse.getMessage());
						// X.2 Send to front-end response and copy the message
						response.setResult(p2pResponse.getMessage());
						controllerResponse.setStatus(p2pResponse.getResponseCode());
						ValidationReport report = validationService.generateValidationReport(p2pResponse.getMessage(), p2pMessage.getRequest());
						 if(report!=null) // means there was a validation error
							 controllerResponse.setStatus(202);
						
					} catch (Exception e) {
						log.severe(e.toString());
					}
				}
			});

			// 2. Send the p2p request message
			if(aclService.isAuthorized(receiverId, p2pMessage)) {
				sendMessage(chatManager, jid, p2pMessage);
			}else {
				Tuple<String,Integer> responseTuple = PayloadsFactory.getUnauthorisedCIMErrorPayloadSending();
				response.setResult(responseTuple.getFirstElement());
				controllerResponse.setStatus(responseTuple.getSecondElement());
			}
		} catch (XmppStringprepException  e) {
			log.severe("ERROR: An error ocurred when connecting with the receiver");
		}
		return response;
	}


	/**
	 * This method sends a {@link P2PMessage} to the {@link EntityBareJid} receiver using the {@link ChatManager}, then stores a copy in the reposutory 
	 * @param chatManager A chat manager to handle the connection
	 * @param jid The identifier of the receiver
	 * @param p2pMessage The message to send
	 */
	private void sendMessage(ChatManager chatManager, EntityBareJid jid, P2PMessage p2pMessage) {
		Chat chat = chatManager.chatWith(jid);
		try {
			// 1. Send the message
			String content = p2pMessage.toJsonString();
			String logMessage = StringFactory.concatenateStrings("Sending to ",jid.asEntityBareJidString(),"\n\tContent:",content);
			log.info(logMessage);
			chat.send(content);

		} catch (NotConnectedException e) {
			log.severe("ERROR: Peer client is not connected!");
		} catch (InterruptedException e) {
			log.severe("ERROR: Connection was interrupted!");
			Thread.currentThread().interrupt();
		}
		log.info("Message sent");
	}



}
