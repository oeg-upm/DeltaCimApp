var yasqe = YASQE(document.getElementById("yasqe-div"), {
	sparql: {
		showQueryButton: true,
		endpoint: '/api/sparql',
		requestMethod: "GET"
	}
});
var yasr = YASR(document.getElementById("yasr-div"), {
	// This prettifies the URLs in the response
	getUsedPrefixes: yasqe.getPrefixesFromQuery
});
/*
<div class="card bg-success text-white">
    <div class="card-body">Success card</div>
  </div>
  */
// Link yasqe and wasr
yasqe.options.sparql.callbacks.complete = yasr.setResponse;

