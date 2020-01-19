package cim.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.springframework.stereotype.Service;

import cim.ConfigTokens;

/**
 * 
 * @author jcano
 *
 */

@Service
public class ConnectionService {
	
	private AbstractXMPPConnection connection;

	
//	private int port = 5222;
//	private String host = "jcano.ddns.net";
//	private String xmppdomain = "jcano.ddns.net";
	
	public ConnectionService() {
		
	}

	public void connect(String username, String password, String xmppDomain, String host, int port, String caFile) {
		
		try {
			System.out.println("Trying connection");

			XMPPTCPConnectionConfiguration.Builder buildConnection = XMPPTCPConnectionConfiguration.builder()
					.setUsernameAndPassword(username, password)
					.setXmppDomain(xmppDomain)
					.setHost(host)
					.setPort(port)
					.setResource(username)
					.setSendPresence(true)
					.setCustomSSLContext(getSSLContext(caFile))
					.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
			
			connection = new XMPPTCPConnection(buildConnection.build());
			connection.connect();
			connection.login();
			System.out.println("All ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
//	public void checkUsername(String username) {
//		
//		UserSearchManager search = new UserSearchManager(mXMPPConnection);
//		Form searchForm = search.getSearchForm("search." + mXMPPConnection.getServiceName());
//
//		Form answerForm = searchForm.createAnswerForm();
//		answerForm.setAnswer("Username", true);
//		answerForm.setAnswer("search", username);
//		ReportedData data = search.getSearchResults(answerForm, "search." + mXMPPConnection.getServiceName());
//
//		if (data.getRows() != null) {
//		    for (ReportedData.Row row: data.getRows()) {
//		        for (String value: row.getValues("jid")) {
//		            Log.i("Iteartor values......", " " + value);
//		        }
//		    }
//		    Toast.makeText(_service, "Username Exists", Toast.LENGTH_SHORT).show();
//		}		
//	}
	
	public boolean disconnect() {
		connection.disconnect();
		return connection.isConnected();
	}
	
	
	public boolean isConnected() {
		try {
		if(connection != null && connection.isConnected())
			return true;
		else
			return false;
		}catch (Exception e) {
			e.getStackTrace();
			return false;
		}
	}
	
	/**
	 * AUXILIAR METHODS
	 */
	
	/**
	 * setKeyStorePath dont work, so we must implement this method in order to handle the certificates
	 */
    private SSLContext getSSLContext(String caFile) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, KeyManagementException {
        char[] JKS_PASSWORD = "changeit".toCharArray();
        char[] KEY_PASSWORD = "changeit".toCharArray();
        caFile = ConfigTokens.P2P_CONFIG_CACERT_FOLDER;
        
        KeyStore keyStore = KeyStore.getInstance("JKS");
        InputStream is = new FileInputStream(caFile);
        keyStore.load(is, JKS_PASSWORD);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, KEY_PASSWORD);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new java.security.SecureRandom());
        return sc;
    }

}
