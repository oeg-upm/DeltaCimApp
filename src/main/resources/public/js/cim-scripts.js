function logout(){
    $.ajax({
            type : 'POST',
            url: "/api/logout",
            success: function(data) {
              window.location.href = "/";
            }, error: function (xhr, ajaxOptions, thrownError) {
            	alert("Error loging out!");
		    }
    });
}
