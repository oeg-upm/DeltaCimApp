package cim.repository.service;

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

import cim.repository.ConfigTokens;

/**
 * 
 * @author jcano
 *
 */

@Service
public class LoginService {
	
	private AbstractXMPPConnection connection;
	
	private int port = 5222;
	private String host = "jcano.ddns.net";
	private String xmppdomain = "jcano.ddns.net";
	
	public LoginService() {
		
	}

	public void connect(String username, String password) {
		
		
		try {
			XMPPTCPConnectionConfiguration.Builder buildConnection = XMPPTCPConnectionConfiguration.builder()
					.setUsernameAndPassword(username, password)
					.setXmppDomain(xmppdomain)
					.setHost(host)
					.setPort(port)
					.setResource(username)
					.setSendPresence(true)
					.setCustomSSLContext(getSSLContext())
					.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
			
			connection = new XMPPTCPConnection(buildConnection.build());
			connection.connect();
			connection.login();
			System.out.println(connection.isConnected());
			System.out.println("All ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	public void disconnect() {
		connection.disconnect();
	}
	
	
	public boolean isLogged() {
		try {
		if(connection == null || connection.isConnected())
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
    private SSLContext getSSLContext() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, KeyManagementException {
        char[] JKS_PASSWORD = "changeit".toCharArray();
        char[] KEY_PASSWORD = "changeit".toCharArray();

        KeyStore keyStore = KeyStore.getInstance("JKS");
        InputStream is = new FileInputStream(ConfigTokens.P2P_CONFIG_CACERT_FOLDER);
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
