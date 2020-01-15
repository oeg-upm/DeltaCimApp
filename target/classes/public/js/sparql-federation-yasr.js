var yasqe = YASQE(document.getElementById("yasqe-div"), {
	sparql: {
		showQueryButton: true,
		endpoint: '/api/cloud/sparql',
		requestMethod: "GET"
	}
});
var yasr = YASR(document.getElementById("yasr-div"), {
	// This prettifies the URLs in the response
	getUsedPrefixes: yasqe.getPrefixesFromQuery
});
// Link yasqe and wasr
yasqe.options.sparql.callbacks.complete = yasr.setResponse;
