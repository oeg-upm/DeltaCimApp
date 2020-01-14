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
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.springframework.stereotype.Service;

import cim.repository.ConfigTokens;


@Service
public class LoginService {
	
	private AbstractXMPPConnection connection;
	
	private final int port = 5222;
	private final String host = "jcano.ddns.net";
	
	public void connect(String username, String passwd) {
		
		try {
			XMPPTCPConnectionConfiguration.Builder buildConnection = XMPPTCPConnectionConfiguration.builder()
					.setUsernameAndPassword(username, passwd)
					.setHost(host)
					.setPort(port)
					.setResource(username)
					.setSendPresence(true)
					.setCustomSSLContext(getSSLContext())
					.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
			
			connection = new XMPPTCPConnection(buildConnection.build());
			connection.connect();
			connection.login();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	public void disconnect() {
		connection.disconnect();
	}
	
	
	public boolean isConnected() {
		if(this.connection.isConnected())
			return true;
		else
			return false;
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
