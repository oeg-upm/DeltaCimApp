package cim.xmpp.factory;

import cim.ConfigTokens;
import cim.model.XmppUser;

public class XmppFactory {

	
	private XmppFactory() {
		
	}
	
	
	public static XmppUser createDefaultXmpp() {
		XmppUser xmpp = new XmppUser();
		xmpp.setUsername(ConfigTokens.P2P_CONFIG_USERNAME);
		xmpp.setPassword(ConfigTokens.P2P_CONFIG_PASS);
		xmpp.setXmppDomain(ConfigTokens.P2P_CONFIG_XMPP_DOMAIN);
		xmpp.setHost(ConfigTokens.P2P_CONFIG_HOST);
		xmpp.setPort(Integer.parseInt(ConfigTokens.P2P_CONFIG_PORT));
		xmpp.setFileCA(ConfigTokens.P2P_CONFIG_CACERT_FOLDER);
		return xmpp;
	}
	
	
	
}
