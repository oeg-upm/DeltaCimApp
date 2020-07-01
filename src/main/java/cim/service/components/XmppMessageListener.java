package cim.service.components;

import java.util.logging.Logger;

import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.EntityBareJid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import cim.ConfigTokens;
import cim.factory.P2PMessageFactory;
import cim.factory.PayloadsFactory;
import cim.model.P2PMessage;
import cim.model.XmppUser;
import cim.repository.AclRepository;
import cim.service.ACLService;
import helio.framework.objects.Tuple;


@Component
public class XmppMessageListener implements IncomingChatMessageListener {

	private static Logger log = Logger.getLogger(XmppMessageListener.class.getName());
	
	@Autowired
	protected RequestProcessor fetcher;
	@Autowired
	protected ACLService aclService;
	
	private String xmppUser;
	
	@Override
	public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
		String xmppUser = from.asEntityBareJidString().substring(0, from.toString().indexOf("@"));
		// X.1 Cast message received to a P2PMessage
		P2PMessage incomingMessage = P2PMessageFactory.createP2PMessageFromJson(message.getBody());
		try {
			if(!incomingMessage.getDestroyAfterReading()) {
				log.info("[Listener] Request message received");
				log.info("Received from " + from.asEntityBareJidString());
				if (aclService.isAuthorized(xmppUser.trim(), incomingMessage)) {
					// By default initialize the response as error
					String response = prepareCIMErrorResponse(from.toString(), PayloadsFactory.getUnauthorisedCIMErrorPayload());
					if (!incomingMessage.getError()) {
						if(isRequestP2PMessage(incomingMessage)) 
							response = processDataRequest(incomingMessage, from.toString());
						chat.send(response);
					}
				} else {
					String response = prepareCIMErrorResponse(from.toString(), PayloadsFactory.getUnauthorisedCIMErrorPayload());
					chat.send(response);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.severe("[Listener] ERROR: An error ocurred processing a request message");
		}
	}
	
	
	private String processDataRequest(P2PMessage incomingMessage, String remoteXmppUser) {
		// X.1 Create response message
		P2PMessage response = null;

		// X.1.A if message was a request fetch data from third-part service and answer
		Tuple<String, Integer> responseMessage = fetcher.fetchData(incomingMessage);
		response = P2PMessageFactory.createP2PMessage(xmppUser, remoteXmppUser, responseMessage.getFirstElement());
		response.setResponseCode(responseMessage.getSecondElement());
		if(responseMessage.getSecondElement()>299)
			response.setError(true); // otherwise it will generate an infinite loop

		return response.toJsonString();
	}
	
	
	
	private String prepareCIMErrorResponse(String remoteUser, Tuple<String,Integer> errorPayladMessage) {
		Tuple<String,Integer> rawResponse = errorPayladMessage;
		P2PMessage response = P2PMessageFactory.createP2PMessage(xmppUser, remoteUser, rawResponse.getFirstElement());
		response.setError(true); // otherwise it will generate an infinite loop
		response.setDestroyAfterReading(true);
		response.setResponseCode(rawResponse.getSecondElement());
		return response.toJsonString();
	}
	
	
	private boolean isRequestP2PMessage(P2PMessage incomingMessage) {
		return incomingMessage.getRequest()!=null && !incomingMessage.getRequest().isEmpty();
	}

	public void setXmppUser(String xmppUser) {
		this.xmppUser = xmppUser;
	}
}
