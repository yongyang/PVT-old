
function showVariables(ctxPath) {
	$("#dialog").dialog({
		dialogClass: "helpVarCls",
		position: { my: "right top", at: "right top", of: $("#form-tool") }
	});
    $.ajax({
        type: "GET",
        url: ctxPath + "/rest/env/variables",
        success: function(data, textStatus){
            $("#dialogContent").html(data);
        },
        error: function() {
      	  $("#dialogContent").html("Error to Retrieve PVT Variables.");
        }
    });
}