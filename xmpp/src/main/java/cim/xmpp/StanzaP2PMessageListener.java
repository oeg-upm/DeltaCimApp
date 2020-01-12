package cim.xmpp;

import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;

import java.util.logging.Logger;

import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jxmpp.jid.Jid;
import org.springframework.beans.factory.annotation.Autowired;

import cim.repository.ConfigTokens;
import cim.repository.DeltaCimApplication;
import cim.repository.model.P2PMessage;
import cim.repository.objects.DataFetcher;
import cim.xmpp.factory.P2PMessageFactory;

public class StanzaP2PMessageListener implements StanzaListener{

	@Autowired
	private P2PMessageFactory messageService;
	
	private Logger log = Logger.getLogger(StanzaP2PMessageListener.class.getName());
		
	public StanzaP2PMessageListener() {
		//
	}
	
	@Override
	public void processStanza(Stanza packet) throws NotConnectedException, InterruptedException, NotLoggedInException {
		  Message message = (Message) packet;
          Jid from = message.getFrom();
          try {
	    	  	// X.1 Cast message received to a P2PMessage
			P2PMessage incomingMessage = messageService.createP2PMessageFromJson(message.getBody());
			log.info("[Stanza Listener] Request message received");
			
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
					log.severe("[Stanza Listener] Received a P2PMessage that is not a request of data");
					response = messageService.createP2PMessage(DeltaCimApplication.getUsername(), from.toString(), ConfigTokens.ERROR_JSON_MESSAGES_1);
					response.setError(true); // otherwise it will generate an infinite loop
				}
				messageService.save(response);
				
				
				//TODO: HOW TO SEND BACK THE RESPONSEchat.send(messageService.fromP2PMessageToJSon(response));
			}
		} catch (Exception e) {		
			e.printStackTrace();
			log.severe("[Stanza Listener] ERROR: An error ocurred processing a request message");
		}
	}
	
	
	private boolean isRequestP2PMessage(P2PMessage incomingMessage) {
		return incomingMessage.getRequest()!=null && !incomingMessage.getRequest().isEmpty();
	}

}
