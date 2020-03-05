clicked_button_id="";

var icons = {
    header:"ui-icon-circle-plus",
    activeHeader:"ui-icon-circle-minus"
};

 
var popupWin;
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
