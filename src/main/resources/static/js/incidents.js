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
	// minTime: '1',
	// maxTime: '12:00pm',
	// defaultTime: '11:00 PM',
	startTime: '10:00 PM',
	dynamic: false,
	dropdown: true,
	scrollbar: true
    });
});
$('#value_id').change(function(){
    var old_val = $('#old_value_id').val();
    var $val_obj = $('#value_id');
    var val = val_obj.val();
    var balance = $('#balance_id').val();    
    var max_val = $('#max_total_id').val();
    if(!$val_obj.prop('required')){
	// nothing
	alert (" not required ");
    }
    else{
	alert("checking value ");
	if(!$.isNumeric(val)){
	    alert("You need to provide a value ");
	    $val_obj.focus();
	    return;
	}
	var new_total = parseFloat(balance)+parseFloat(val) - parseFloat(old_val);
	if(parseFloat(new_total) > parseFlaot(max_val) ){
	    alert("Current balance of  $"+new_total+" is greater than the max allowed of  $"+max_val);
	    $('#value_id').focus();
	    return;
	}
    }
});


