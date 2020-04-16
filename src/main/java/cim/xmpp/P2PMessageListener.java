package cim.xmpp;

import java.util.logging.Logger;

import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jxmpp.jid.EntityBareJid;


import cim.ConfigTokens;
import cim.model.P2PMessage;
import cim.objects.DataFetcher;
import cim.service.ACLService;
import cim.service.XMPPService;
import cim.xmpp.factory.P2PMessageFactory;
import helio.framework.objects.Tuple;


public class P2PMessageListener implements IncomingChatMessageListener {

	private static Logger log = Logger.getLogger(P2PMessageListener.class.getName());
	
	
	@Override
	public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
		String P2PUser = from.asEntityBareJidString().substring(0, from.toString().indexOf("@"));
		Type[] types = message.getType().values();
		
		for(Type type:types) {
			System.out.println(">>>>"+type);
		}
		System.out.println(">>>####:"+message.getType().valueOf("groupchat"));
		try {
			if (ACLService.isAuthorized(P2PUser.trim())) {
				// X.1 Cast message received to a P2PMessage
				System.out.println("Received from " + from.asEntityBareJidString());// +"\n\tContent:"+message.getBody());

				P2PMessage incomingMessage = P2PMessageFactory.createP2PMessageFromJson(message.getBody());
				log.info("[Listener]Request message received");

				if (!incomingMessage.getError()) {
					// X.1 Create response message
					P2PMessage response = null;
					if (isRequestP2PMessage(incomingMessage)) {
						// X.1.A if message was a request fetch data from third-part service and answer
						DataFetcher fetcher = new DataFetcher();
						Tuple<String, Integer> responseMessage = fetcher.fetchData(incomingMessage);
						response = P2PMessageFactory.createP2PMessage(XMPPService.p2pUsername, from.toString(), responseMessage.getFirstElement());
						if(responseMessage.getSecondElement()!=200)
							response.setError(true); // otherwise it will generate an infinite loop
						
					} else {
						// X.1.A Otherwise send error message
						log.severe("[Listener] Received a P2PMessage that is not a request of data");
						response = P2PMessageFactory.createP2PMessage(XMPPService.p2pUsername, from.toString(), ConfigTokens.ERROR_JSON_MESSAGES_1);
						response.setError(true); // otherwise it will generate an infinite loop
					}
					chat.send(P2PMessageFactory.fromP2PMessageToB64(response));
				}
			} else {
				P2PMessage response = P2PMessageFactory.createP2PMessage(XMPPService.p2pUsername, from.toString(),
						ConfigTokens.ERROR_JSON_MESSAGES_3);
				response.setError(true); // otherwise it will generate an infinite loop
				chat.send(P2PMessageFactory.fromP2PMessageToB64(response));
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.severe("[Listener] ERROR: An error ocurred processing a request message");
		}
	}
	
	
	private boolean isRequestP2PMessage(P2PMessage incomingMessage) {
		return incomingMessage.getRequest()!=null && !incomingMessage.getRequest().isEmpty();
	}

}
