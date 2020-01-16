package cim.xmpp;

import java.util.logging.Logger;

import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.EntityBareJid;

import cim.ConfigTokens;
import cim.DeltaCimApplication;
import cim.model.P2PMessage;
import cim.objects.DataFetcher;
import cim.xmpp.factory.P2PMessageFactory;

public class P2PMessageListener implements IncomingChatMessageListener {



	private P2PMessageFactory messageService;
	
	public P2PMessageListener() {
		messageService = new P2PMessageFactory();
	}
	
	private Logger log = Logger.getLogger(P2PMessageListener.class.getName());
	


	
	
	@Override
	public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
		if(messageService==null)
			messageService = new P2PMessageFactory();
        try {
	    	  	// X.1 Cast message received to a P2PMessage
			System.out.println("Received from "+from.asEntityBareJidString());//+"\n\tContent:"+message.getBody());

			P2PMessage incomingMessage = messageService.createP2PMessageFromJson(message.getBody());
			log.info("[Listener]Request message received");
			
			if(!incomingMessage.getError()) {
				// X.1 Create response message
				P2PMessage response = null;
				if(isRequestP2PMessage(incomingMessage)) {
					// X.1.A if message was a request fetch data from third-part service and answer
					DataFetcher fetcher = new DataFetcher(); 
					String responseMessage = fetcher.fetchData(incomingMessage);
					response = messageService.createP2PMessage(DeltaCimApplication.getUsername(), from.toString(), responseMessage);
				}else {
					// X.1.A Otherwise send error message
					log.severe("[Listener] Received a P2PMessage that is not a request of data");
					response = messageService.createP2PMessage(DeltaCimApplication.getUsername(), from.toString(), ConfigTokens.ERROR_JSON_MESSAGES_1);
					response.setError(true); // otherwise it will generate an infinite loop
				}
				messageService.save(response);
				
				
				chat.send(messageService.fromP2PMessageToB64(response));
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
