package cim.repository.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.servlet.http.HttpServletRequest;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import cim.repository.ConfigTokens;
import cim.repository.DeltaCimApplication;
import cim.repository.model.P2PMessage;
import cim.xmpp.CimParsingExceptionCallback;
import cim.xmpp.P2PMessageListener;
import cim.xmpp.factory.P2PMessageFactory;

@Service
public class XMPPService {

	// -- Attributes

	private P2PMessageFactory messageService;
	private AbstractXMPPConnection connection;
	private ChatManager chatManagerReceiver;
	private Logger log = Logger.getLogger(XMPPService.class.getName());

	// -- Constructor

	public XMPPService() {
		// empty
		messageService = new P2PMessageFactory();
	}


	// -- Methods

	public Boolean isConnected() {
		return this.connection!=null && this.connection.isConnected();
	}

	public void sendPresence() {
		if(isConnected()) {
			Presence presence = new Presence(Presence.Type.available, "online",127, Presence.Mode.available);
			try {
				connection.sendStanza(presence);
			} catch (Exception e) {
				log.severe(">"+e.getMessage());	

			}
		}
	}

	public void logout() {
		connection.disconnect();
	}

	public void connect(String username, String password, String xmppDomain, String host, int port) {
		try {
			// 0. Reading the certificates
			String certificates = ConfigTokens.P2P_CONFIG_CERTIFICATES_FOLDER;

			// 1. Configuring the XMPPT connection
			XMPPTCPConnectionConfiguration.Builder build =  XMPPTCPConnectionConfiguration.builder()
					.setUsernameAndPassword(username, password)
					.setXmppDomain(xmppDomain)
					.setHost(host)
					.setResource(username)
					.setSendPresence(true)
					.enableDefaultDebugger()
					.setPort(port)
					//.setKeystorePath(certificates)
					.setCustomSSLContext(getSSLContext())
					.setSecurityMode(ConnectionConfiguration.SecurityMode.required);

			//1.2 Add the certificates to the configuration

			
			XMPPTCPConnectionConfiguration config = build.build();

			log.info("Peer configured");
			log.info("Connecting to "+host+":"+port);
			connection = new XMPPTCPConnection(config);
			connection.connect();
			connection.login();// username, password
			log.info("Login successful: "+String.valueOf(connection.isAuthenticated()));
			log.info("SASL Mechanism used: "+connection.getUsedSaslMechansism());
			log.info("Connection secured: "+connection.isSecureConnection());
			log.info("Peer "+DeltaCimApplication.getUsername()+" logged"); 


			// 2. Create message handler and stanza handler
			createMessageHandlers();	
			log.info("Peer message handlers established"); 
			connection.setParsingExceptionCallback(new CimParsingExceptionCallback());

		}catch(Exception e) {
			log.info("Peer exception");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	/**
	 * setKeyStorePath dont work, so we must implement this method in order to handle the certificates
	 */
    private SSLContext getSSLContext() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, KeyManagementException {
        char[] JKS_PASSWORD = "changeit".toCharArray();
        char[] KEY_PASSWORD = "changeit".toCharArray();

        KeyStore keyStore = KeyStore.getInstance("JKS");
        //Change "\\cacerts" in order to be dynamic
        InputStream is = new FileInputStream(ConfigTokens.P2P_CONFIG_CERTIFICATES_FOLDER + "\\cacerts");
        keyStore.load(is, JKS_PASSWORD);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, KEY_PASSWORD);
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
		chatManagerReceiver.addIncomingListener(new P2PMessageListener());
		chatManagerReceiver.setXhmtlImEnabled(true);

		//connection.addSyncStanzaListener(new StanzaP2PMessageListener(), StanzaTypeFilter.MESSAGE);
	}

	public DeferredResult<String> sendMessage(HttpServletRequest request, Map<String, String> headers, String payload) {
		P2PMessage p2pMessage = messageService.createP2PRequestMessage(request, headers);
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
						P2PMessage p2pResponse = messageService.createP2PMessageFromJson(message.getBody());
						log.info("Response received");
						if(p2pResponse.getRequest()==null) {
							// X.1.A If message is contains no message return error message
							log.severe("ERROR!: Received a P2PMessage that contains no data");
							p2pResponse = messageService.createP2PMessage(DeltaCimApplication.getUsername(), from.toString(), ConfigTokens.ERROR_JSON_MESSAGES_2);
							p2pResponse.setError(true);
						}
						// X.2 Send to front-end response and copy the message
						messageService.save(p2pResponse);
						response.setResult(p2pResponse.getMessage());

					} catch (Exception e) {
						//e.printStackTrace();
					}
				}
			});

			// 2. Send the p2p request message
			sendMessage(chatManager, jid, p2pMessage);
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
			String content = messageService.fromP2PMessageToB64(p2pMessage);
			System.out.println("Sending to "+jid.asEntityBareJidString()+"\n\tContent:"+content);
			chat.send(content);
			// 2. Store the message sent
			messageService.save(p2pMessage);
		} catch (NotConnectedException e) {
			log.severe("ERROR: Peer client is not connected!");
		} catch (InterruptedException e) {
			log.severe("ERROR: Connection was interrupted!");
			Thread.currentThread().interrupt();

		}
		log.info("Message sent");
	}





}