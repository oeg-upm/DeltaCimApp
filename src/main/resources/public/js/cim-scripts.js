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