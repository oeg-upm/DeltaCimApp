function deleteRoute(routeId){
	var url = "/api/route";
	var xhr = new XMLHttpRequest();
	xhr.open("DELETE", url, true);
	xhr.onload = function () {
		location.reload();
	}
	xhr.send(routeId);
}

function validateReport(routeAddress, format){
	var url = "/api/validation/endpoint?address="+routeAddress+"&format="+format;
	var xhr = new XMLHttpRequest();
	xhr.open("GET", url, true);
	xhr.onreadystatechange = function (aEvt) {
		if (xhr.readyState == 4) {
			if(xhr.status == 202){
				var responseData = JSON.parse(xhr.responseText);
				$("#reportResult").empty();
				$("#reportResult").append("<p>"+responseData.report+"</p>");
				$("#reportModal").modal();
			}else{
				console.log("Error loading page\n");
			}
		}
	};
	xhr.send(null);
}

function deleteReport(reportId){
	var url = "/api/validation/report";
	var xhr = new XMLHttpRequest();
	xhr.open("DELETE", url, true);
	xhr.onload = function () {
		location.reload();
	}
	xhr.send(reportId);
}

function deleteAcl(usernameId){
	var url = "/api/acl";
	var xhr = new XMLHttpRequest();
	xhr.open("DELETE", url, true);
	xhr.onload = function () {
		location.reload();
	}
	xhr.send(usernameId);
}

function deleteCimUser(userId){
	var url = "/api/cimuser";
	var xhr = new XMLHttpRequest();
	xhr.open("DELETE", url, true);
	xhr.onload = function () {
		location.reload();
	}
	console.log(userId);
	xhr.send(userId);
}

function xmppDisconnect(){
	var url = "/api/disconnect";
	var xhr = new XMLHttpRequest();
	var x = false;
	xhr.open("GET", url, true);
	xhr.onload = function (aEvt) {
		if (xhr.readyState == 4) {
			if(xhr.status == 400){
				$("#disconnectResult").empty();
				$("#disconnectResult").append("<p>Not connected</p>");
				$("#disconnectModal").modal();
				x=true;
				console.log("No desconectado\n");
			}else if (xhr.status == 407){
				$("#disconnectResult").empty();
				$("#disconnectResult").append("<p>Not authorized</p>");
				$("#disconnectModal").modal();
			}else if(xhr.status == 200){
				console.log("Desconectado\n");
			}
		}	
		if(!x){
			location.reload();
		}
	};	
	console.log("No desconectado\n");
	xhr.send(null);
}
