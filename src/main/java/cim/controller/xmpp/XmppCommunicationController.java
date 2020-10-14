package cim.controller.xmpp;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cim.ConfigTokens;
import cim.controller.AbstractSecureController;
import cim.factory.PayloadsFactory;
import cim.factory.RequestsFactory;
import cim.model.enums.Method;
import cim.service.BridgingService;
import cim.service.ValidationService;
import cim.service.VirtualisationService;
import cim.service.XMPPService;
import helio.framework.objects.RDF;
import helio.framework.objects.Tuple;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;


@RestController
@RequestMapping("/delta/**")
public class XmppCommunicationController extends AbstractSecureController{
	
	@Autowired
	public XMPPService p2pService;
	@Autowired
	public ValidationService validationService;

	@Autowired
	public BridgingService bridgingService;
	@Autowired
	public VirtualisationService virtualisationService;
	
	
	// -- GET method
	@ApiOperation(value = "Sends a request thorugh the peer-to-peer network")
	@ApiResponses(value = {
	        @ApiResponse(code = 202, message = "Data has been correctly exchanged but a validation report was created since it did not pass the validation process")
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
	        @ApiResponse(code = 202, message = "Data has been correctly exchanged but a validation report was created since it did not pass the validation process")
	 })
	@RequestMapping(method = {RequestMethod.POST, RequestMethod.PATCH, RequestMethod.PUT, RequestMethod.DELETE}, produces = { "application/json", "application/odata+json" })
	@ResponseBody
	public DeferredResult<String> postResource(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String payload) {
		prepareResponseOK(response);
		 DeferredResult<String> defferredResponse = new DeferredResult<>();
		 if(authenticated(request)) {
			 if(p2pService.isConnected()) {
				 // Normalise payload if requited to Json-LD + Ontology
				 RDF normalisedPayload = virtualisationService.normalisePayload(payload, retrievePath(request), Method.GET.toString());
				 if(normalisedPayload!=null) {
					 //TODO: validate the normalisedPayload?
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
