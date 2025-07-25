
clicked_button_id = "";
var popupWin;
var icons = {
  header: "ui-icon-circle-plus",
  activeHeader: "ui-icon-circle-minus",
};
document.addEventListener('DOMContentLoaded', function () {
    const options = document.getElementsByClassName("option");
    for(let i=0;i<options.length;i++){
	options[i].addEventListener('change',verifyAnswer);
    }
});
function verifyAnswer(){
    var radios = document.getElementsByTagName('input');
    var answer_cnt = 0;
    for (var i = 0; i < radios.length; i++) {
	if (radios[i].type === 'radio' && radios[i].checked) {
	    var name = radios[i].getAttribute("name");	    
            var value = radios[i].value;
	    if(value === "Next"){
		document.getElementById(name).style.display = 'none';
		answer_cnt = answer_cnt*1+1*1;
	    }
	    else{
		// showing the div that has stop text
		document.getElementById(name).style.display = 'block';
	    }
	}
    }
    console.log(" cnt "+answer_cnt);
    if(answer_cnt*1 < 1*4){
	document.getElementById("moreInfo").style.display = 'block';
	document.getElementById("finalStep").style.display = 'none';	
    }
    if(answer_cnt*1 == 1*4){
	document.getElementById("moreInfo").style.display = 'none';
	document.getElementById("q1").style.display = 'none';
	document.getElementById("q2").style.display = 'none';
	document.getElementById("q3").style.display = 'none';
	document.getElementById("q4").style.display = 'none';
	document.getElementById("finalStep").style.display = 'block';	
    }
    
}
function popwit(url, name) {
    var args =
	"top=200,left=200,height=500,width=400,toolbar=0,menubar=0,location=0";
    if (typeof popupWin != "object" || popupWin.closed) {
	popupWin = window.open(url, name, args);
    } else {
	popupWin.location.href = url;
    }
    if (window.focus) {
	popupWin.focus();
    }
    return false;
};
function verifyConfirm() {
    var email = document.getElementById("email");
    var val = email.value;
    var val2 = document.getElementById("email2").value;
    if (val.trim() == "" || val2.trim() == "") {
	alert("You need to provide valid email");
	return false;
    }
    if (val.trim() != val2.trim()) {
	alert("The two emails do not match");
	return false;
    }
}
/**
document.addEventListener('DOMContentLoaded',function () {
    document.querySelectorAll('input.abtn').forEach(link => {
	link.addEventListener('click', (e) => {
	    // Retrieve href and store in targetUrl variable
	    let targetUrl = e.currentTarget.href; // currentTarget
	    // Output value of targetUrl to console
	    console.log('A link with target URL ' + targetUrl + 'was clicked');
	});
    });
    
});
*/
// need visit check browser time picker use input time instead
/**
document.addEventListener('DOMContentLoaded', function () {
  $("input.timepicker").timepicker({
    timeFormat: "hh:mm p",
    interval: 15,
    startTime: "12:00 AM",
    dynamic: false,
    dropdown: true,
    scrollbar: true,
  });
});
*/
document.addEventListener('DOMContentLoaded',function () {
    if(document.getElementById("value_id")){
	const val_input = document.getElementById("value_id");
	val_input.addEventListener('change',verifyTotalValue);
    }
});

//  
function verifyTotalValue() {
    var val_elem = document.getElementById("value_id");
    if(val_elem){
	var val = val_elem.value;    
	var old_val = document.getElementById("old_value_id").value;
	var balance = document.getElementById("balance_id").value;
	var max_val = document.getElementById("max_total_id").value;
   
	//
	// if max_val is set to 0 this means there is not limit
	//
	if (max_val == 0) return true;
	if (typeof val != 'number') {
	    alert("You need to provide a valid value ");
	    val_elem.focus();
	    return false;
	}
   
	if (val * 1 > 0) {
	    var new_total =
		parseFloat(balance) + parseFloat(val) - parseFloat(old_val);
	    if (new_total * 1 > max_val * 1) {
		alert("Current balance of  $" +new_total +
		      " is greater than the max allowed of  $" +
		  max_val);
		val_elem.focus();
		return false;
	    }
	}
	else {
	    if (val.trim() == "" || val * 1 == 0) {
		alert("You need to provide a value for the damage ");
		val_elem.focus();
	    return false;
	    }
	    var new_total = parseFloat(balance) + parseFloat(val) - parseFloat(old_val);
	    if (new_total * 1 > max_val * 1) {
		alert(
		    "Current balance of  $" +
			new_total +
			" is greater than the max allowed of  $" +
			max_val
		);
		val_elem.focus();
		return false;
	    }
	}
    }
    return true;
}
function verifyIncidentInput() { 
    if(document.getElementById("start_date")){
	var start_date = document.getElementById("start_date").value;
    
	var start_time = document.getElementById("start_time").value;
	var date_description = document.getElementById("date_description").value;
	var end_date = document.getElementById("end_date").value;
	var end_time = document.getElementById("end_time").value;    
	if (start_date === "" && start_time === "" && date_description === "") {
	    alert("Please provide incident date and time");
	    document.getElementById("start_date").focus();
	    return false;
	}
	if (start_date != "" && start_time === "") {
	    alert("Please provide incident start time");
	    document.getElementById("start_time").focus();
	    return false;
	}
	if (end_date != "" && end_time === "") {
	    alert("Please provide incident end time");
	    document.getElementById("end_time");
	    return false;
	}
	var details = document.getElementById("detail_id").value;
	if (details === "") {
	    alert("Please provide incident details");
	    document.getElementById("detail_id").focus();
	    return false;
	}
	return true;
    }
}
function verifyAddress() {
    if(document.getElementById("addr_id")){
	var addr = document.getElementById("addr_id").value;
	if (addr === "") {
	    alert("Please provide incident address");
	    document.getElementById("addr_id").focus();
	    return false;
	}
	var city = document.getElementById("city").value;
	if (city === "") {
	    alert("Please provide city");
	    return false;
	}
	if (city != "Bloomington") {
	    alert("Your address is not in Bloomington");
	    return false;
	}
	var jurisdiction =document.getElementById("jurisdiction").value;
	if (jurisdiction != "" && jurisdiction != "Bloomington") {
	    alert("Your address is not in Bloomington jurisdiction");
	    return false;
	}
	var state = document.getElementById("state").value;
	if (state === "") {
	    alert("Please provide state");
	    document.getElementById("state").focus();
	    return false;
	}
	var zip = document.getElementById("zip").value;
	if (zip === "") {
	    alert("Please provide zip code");
	    return false;
	}
    }
    return true;
}
 
function verifyFraudInput() {
    if(document.getElementById("fraudType")){
	var type = document.getElementById("fraudType").value;
	if (type === "") {
	    alert("Please select the fraud type");
	    document.getDocumentById("fraudType").focus();
	    return false;
	}
	var typeText = document.getElementById("fraudType").text;    
	if (typeText == "Other Specify") {
	    var otherType = document.getDocumentById("otherType").value;
	    if (otherType === "") {
		alert("Please specify other fraud type");
		document.getDocumentById("otherType").focus();
		return false;
	    }
	}
    }
    return true;
}
function handelRelatedFraud() {
    if(document.getElementById("fraudType")){    
	var typeVal = document.getElementById("fraudType").value;
	if (typeVal === "") {
	    return;
	}
	if (typeVal == 1 || typeVal == 3 || typeVal == 4) {
	    document.getElementById("type_other").style.visibility = 'hidden';
	    document.getElementById("personal").style.visibility = 'hidden';
	    document.getElementById("account").style.visibility = 'visible';
	} else if (typeVal == 2 || typeVal == 5 || typeVal == 6) {
	    document.getElementById("type_other").style.visibility = 'hidden';
	    document.getElementById("personal").style.visibility = 'visible';
	    document.getElementById("account").style.visibility = 'hidden';
	} else {
	    // for other	
	    document.getElementById("type_other").style.visibility = 'visible';
	    document.getElementById("personal").style.visibility = 'visible';
	    document.getElementById("account").style.visibility = 'visible';	
	    
	}
    }
};
function popup(mylink, windowTitle) {
    if (!window.focus) return true;
    var href;
    if (typeof mylink == "string") href = mylink;
    else href = mylink.href;
    window.open(href, windowTitle, "width=500,height=400,scrollbars=yes");
    return false;
};


