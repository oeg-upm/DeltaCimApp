var yasqe_feration = YASQE(document.getElementById("yasqe_feration-div"), {
	sparql: {
		showQueryButton: true,
		endpoint: '/api/cloud/sparql',
		requestMethod: "GET"
	}
});
var yasr_federation = YASR(document.getElementById("yasr_federation-div"), {
	// This prettifies the URLs in the response
	getUsedPrefixes: yasqe_feration.getPrefixesFromQuery
});
// Link yasqe_feration and wasr
yasqe_feration.options.sparql.callbacks.complete = yasr_federation.setResponse;
