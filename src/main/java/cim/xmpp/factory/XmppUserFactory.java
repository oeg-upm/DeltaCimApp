package cim.xmpp.factory;

import java.util.logging.Logger;
import cim.ConfigTokens;
import cim.model.XmppUser;

/**
 * This class is a Factory that generates a default {@link XmppUser}
 */
public class XmppUserFactory {

	//-- Attributes
	
	private XmppUserFactory() {
		// empty: hides the public constructor
	}
	
	/**
	 * This method creates a default {@link XmppUser}
	 * @return a {@link XmppUser} with empty user and password, but with the default domain, host, and port
	 */
	public static XmppUser createDefaultXmpp() {
		XmppUser xmpp = new XmppUser();
		xmpp.setUsername(ConfigTokens.DEFAULT_USER);
		xmpp.setPassword(ConfigTokens.DEFAULT_PASSWORD);
		xmpp.setXmppDomain(ConfigTokens.DEFAULT_DOMAIN);
		xmpp.setHost(ConfigTokens.DEFAULT_HOST);
		xmpp.setPort(Integer.parseInt(ConfigTokens.DEFAULT_PORT));
		xmpp.setFileCA(ConfigTokens.P2P_CONFIG_CACERT_FOLDER);
		return xmpp;
	}	
	
}
