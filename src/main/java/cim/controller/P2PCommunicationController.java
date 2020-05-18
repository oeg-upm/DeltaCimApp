package cim.controller;

import java.util.Map;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import cim.service.XMPPService;
import cim.xmpp.factory.RequestsFactory;

@RestController
@RequestMapping("/delta/**")
public class P2PCommunicationController extends AbstractController{
	
	@Autowired
	@Singleton
	public XMPPService p2pService;
	//private Logger log = Logger.getLogger(RequestController.class.getName());

	// -- GET method
	
	 @RequestMapping(method = RequestMethod.GET, produces= {"text/html", "text/rdf+n3", "text/n3", "text/ntriples", "text/rdf+ttl", "text/rdf+nt", "text/plain", "text/rdf+turtle", "text/turtle", "application/turtle", "application/x-turtle", "application/x-nice-turtle", "application/json", "application/odata+json", "application/ld+json", "application/x-trig", "application/rdf+xml"})
	 @ResponseBody
	 public DeferredResult<String> getResource(@RequestHeader Map<String, String> headers,  final HttpServletRequest request, HttpServletResponse response){
		 prepareResponse(response);
		 if(p2pService.isConnected()) {
			 return p2pService.sendMessage(request, RequestsFactory.extractHeaders(request), "", response);
		 }else {
			 return null;
		 }
	 }
	 
	// -- POST method
		
	 @RequestMapping(method = RequestMethod.POST, produces= {"text/html", "text/rdf+n3", "text/n3", "text/ntriples", "text/rdf+ttl", "text/rdf+nt", "text/plain", "text/rdf+turtle", "text/turtle", "application/turtle", "application/x-turtle", "application/x-nice-turtle", "application/json", "application/odata+json", "application/ld+json", "application/x-trig", "application/rdf+xml"})
	 @ResponseBody
	 public DeferredResult<String> postResource(@RequestHeader Map<String, String> headers, @RequestBody(required = true) String payload, final HttpServletRequest request, HttpServletResponse response){
		 prepareResponse(response);
		 if(p2pService.isConnected()) {
			 return p2pService.sendMessage(request, RequestsFactory.extractHeaders(request), payload, response);
		 }else {
			 return null;
		 }
	 }	

	
	// -- Logout when shutting up
		
		@PreDestroy
	    public void logOut() {
	        p2pService.disconnect();
	    }

		/*
		@Scheduled(fixedDelay = 30000)
		private void available() {
			if(!p2pService.isConnected()) {
					log.warning("Connection lost!");
					p2pService.logout();
					log.warning("Reconnecting...");
					p2pService.connect(DeltaCimRepositoryApplication.getUsername(), DeltaCimRepositoryApplication.getPassword(), DeltaCimRepositoryApplication.getXmppDomain(), DeltaCimRepositoryApplication.getHost(), DeltaCimRepositoryApplication.getPort());
				
					if(p2pService.isConnected()) {
						log.info("Successfully re-connected");
						p2pService.sendPresence();
					}else {
						log.severe("System down");
						System.exit(-1);
					}
			}else {
				p2pService.sendPresence();
			}
		}*/
}
