package cim.controller.xmpp;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import cim.ConfigTokens;
import cim.controller.AbstractSecureController;
import cim.factory.PayloadsFactory;
import cim.factory.RequestsFactory;
import cim.model.BridgingRule;
import cim.service.BridgingService;
import cim.service.VirtualisationService;
import cim.service.XMPPService;
import helio.framework.objects.RDF;
import helio.framework.objects.Tuple;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/delta/**")
public class XmppCommunicationController extends AbstractSecureController{
	
	@Autowired
	public XMPPService p2pService;
	@Autowired
	public BridgingService bridgingService;
	@Autowired
	public VirtualisationService virtualisationService;
	
	private Logger log = Logger.getLogger(XmppCommunicationController.class.getName());

	// -- GET method
	@ApiOperation(value = "Sends a request thorugh the peer-to-peer network")
	@ApiResponses(value = {
	        @ApiResponse(code = 409, message = "CIM is currently disconnected")
	 })
	 @RequestMapping(method = RequestMethod.GET, produces={"application/json", "application/ld+json"})
	 @ResponseBody
	 public DeferredResult<String> getResource(@RequestHeader Map<String, String> headers,  final HttpServletRequest request, HttpServletResponse response){
		 prepareResponseOK(response);
		 DeferredResult<String> defferredResponse = new DeferredResult<>();
		 if(authenticated(request)) {
			 if(p2pService.isConnected()) {
				 defferredResponse = p2pService.sendMessage(request, RequestsFactory.extractHeaders(request), "", response);
			 }else {
				 Tuple<String, Integer> responsePayload = PayloadsFactory.getCIMDisconnectedPayload();
				 defferredResponse.setResult(responsePayload.getFirstElement());
				 response.setStatus(responsePayload.getSecondElement());
			 }
		 }else{
			 response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		 }
		 return defferredResponse;
	 }
	 
	// -- POST method
	@ApiOperation(value = "Sends a request thorugh the peer-to-peer network")
	@ApiResponses(value = {
	        @ApiResponse(code = 409, message = "CIM is currently disconnected"),
	        @ApiResponse(code = 418, message = "CIM is currently disconnected"),
	 })
	@RequestMapping(method = RequestMethod.POST, produces = { "application/json", "application/odata+json" })
	@ResponseBody
	public DeferredResult<String> postResource(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = true) String payload) {
		prepareResponseOK(response);
		 DeferredResult<String> defferredResponse = new DeferredResult<>();
		 if(authenticated(request)) {
			
			 if(p2pService.isConnected()) {
				 // Normalise payload if requited to Json-LD + Ontology
				 RDF normalisedPayload = normalisePayload(payload, retrievePath(request));
				 if(normalisedPayload!=null) {
					 defferredResponse = p2pService.sendMessage(request, RequestsFactory.extractHeaders(request), normalisedPayload.toString(ConfigTokens.DEFAULT_RDF_SERIALISATION), response);
				 }else{
					 Tuple<String, Integer> responsePayload = PayloadsFactory.getInteroperabilityErrorPayload();
					 defferredResponse.setResult(responsePayload.getFirstElement());
					 response.setStatus(responsePayload.getSecondElement());
				 }
			 }else {
				 Tuple<String, Integer> responsePayload = PayloadsFactory.getCIMDisconnectedPayload();
				 defferredResponse.setResult(responsePayload.getFirstElement());
				 response.setStatus(responsePayload.getSecondElement());
			 }
		 }else{
			 response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			 defferredResponse.setResult(null);
		 }
		 return defferredResponse;

	}

	private RDF normalisePayload(String payload, String xmppRemotePath) {
		RDF normalisedData = new RDF(); 
		try {
			Model model = ModelFactory.createDefaultModel();
			model.read(IOUtils.toInputStream(payload, "UTF-8"), null, ConfigTokens.DEFAULT_RDF_SERIALISATION);
			normalisedData.getRDF().add(model);
		} catch(Exception e) {
			e.printStackTrace();
			log.warning("Provided payload is not expressed in JSON-LD, looking for interoperability module to adapt data");
			Optional<BridgingRule> ruleOptional = bridgingService.findByXmppPatternMatch(xmppRemotePath);
			if(ruleOptional.isPresent()) {
				BridgingRule rule = ruleOptional.get();
				rule.updateMappingContent(); // update mappings
				String mapping = rule.getReadingMapping();
				if(mapping!=null) {
					normalisedData = virtualisationService.virtualiseData(payload, mapping);
				}else {
					log.severe("Bridging rule found, but has no reading mapping associated");
					log.severe(rule.toString());
				}
			}else {
				log.warning("No interoperability module found for adapting the data");
			}
		}finally {
			if(!payload.isEmpty() && normalisedData!=null && normalisedData.getRDF().isEmpty()) {
				normalisedData = null;
			}
		}
		return normalisedData;
	}
	
	 private static String retrievePath(HttpServletRequest request) {
		 String urlToken = ConfigTokens.URL_TOKEN;
		 String path = request.getRequestURL().toString();
		 path = path.substring(path.indexOf("://")+3, path.length());
		 String remotePath = path.substring(path.indexOf(urlToken)+urlToken.length());
		 if(remotePath.startsWith("/"))
			 remotePath = remotePath.substring(1);
		 return remotePath;
	 }

	
}
