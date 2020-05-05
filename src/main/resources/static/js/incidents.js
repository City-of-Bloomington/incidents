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
$(document).ready(function(){
    $('input.timepicker').timepicker({
	timeFormat: 'hh:mm p',
	interval: 15,
	startTime: '10:00 PM',
	dynamic: false,
	dropdown: true,
	scrollbar: true
    });
});
$(document).ready(function(){
    $('#value_id').change(verifyTotalValue);
});

function verifyTotalValue(){
    var old_val = $('#old_value_id').val();
    var val = $('#value_id').val();
    var balance = $('#balance_id').val();
    var max_val = $('#max_total_id').val();
    if(!$.isNumeric(val)){
	alert("You need to provide a valid value ");
	$('#value_id').focus();
	return false;
    }
    if(!$(this).prop('required')){
	// not required but what if the user enter a value
	// still we need to check for the total allowed
	if(val*1 > 0){
	    var new_total = parseFloat(balance)+parseFloat(val) - parseFloat(old_val);
	    if((new_total*1) > (max_val*1)){
		alert("Current balance of  $"+new_total+" is greater than the max allowed of  $"+max_val);
		$('#value_id').focus();
		return false;
	    }
	}
    }
    else{
	if(val.trim() == '' || val*1 == 0){
	    alert("You need to provide a value for the damage ");
	    $('#value_id').focus();
	    return false;
	}
	var new_total = parseFloat(balance)+parseFloat(val) - parseFloat(old_val);
	if((new_total*1) > (max_val*1)){
	    alert("Current balance of  $"+new_total+" is greater than the max allowed of  $"+max_val);
	    $('#value_id').focus();
	    return false;
	}
    }
    return true;
}

