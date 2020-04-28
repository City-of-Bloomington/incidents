clicked_button_id="";
var popupWin;
var icons = {
    header:"ui-icon-circle-plus",
    activeHeader:"ui-icon-circle-minus"
};

function popwit(url, name) {
		var args = "top=200,left=200,height=300,width=350,toolbar=0,menubar=0,location=0";
    if(typeof(popupWin) != "object" || popupWin.closed)  { 
        popupWin =  window.open(url, name, args); 
    } 
    else{ 
        popupWin.location.href = url; 
    }
		if (window.focus) {popupWin.focus()}
		return false;		
 }
function verifyConfirm(){
    var email = document.getElementById("email");
    var val = email.value;
    var val2 = document.getElementById("email2").value;
    if(val.trim() == '' || val2.trim() == ''){
	alert("You need to provide valid email");
	return false;
    }
    if(val.trim() != val2.trim()){
	alert("The two emails do not match");
	return false;
    }

}

