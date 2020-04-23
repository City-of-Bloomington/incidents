function verifyConfirm(){
    var val = document.getElementById("email").value;
    var val2 = document.getElementById("email2").value;
    if(val.trim() == '' || val2.trim() == ''){
	alert("You need to provide valid email");
	return false;
    }
    if(val.trim() != val2.trim()){
	alert("The two emails do not match");
	return false;
    }
    return true;
}
$(document).ready(function () {
    $('div.questions').each(function () {
	$(this).find('input:radio').prop('checked', false);
    });
    $('#next_div').hide();
    $('#moreInfo').hide();
});
$(document).ready(function(){
    answers = new Object();
    $('.option').change(function(){
	var answer = ($(this).attr('value'))
	var question = ($(this).attr('name'))
	answers[question] = answer;
	if(answer == 'finalStep'){
	    $('#finalStep').show();
	    $('#next_div').hide();
	    $('#moreInfo').hide();
	    $('#lastQuestion').hide();
	}	      
	else if(answer != 'Next'){
	    $('#next_div').hide();		  		  
	    alert (answer); // stop no more
	    $('#moreInfo').show();		  
	}
	else{
	    $('#moreInfo').hide();
	    $('#next_div').show();
	}
    })
    var totalQuestions = $('.questions').length;
    var currentQuestion = 0;
	  $questions = $('.questions');
    $questions.hide();
    $($questions.get(currentQuestion)).fadeIn();
    $('#next').click(function(){
	$($questions.get(currentQuestion)).fadeOut(function(){
	    currentQuestion = currentQuestion + 1;
	    if(currentQuestion == totalQuestions){
		// if the last question 
		$("#moreInfo").show();
	    }else{
		$($questions.get(currentQuestion)).fadeIn();
		$('#next_div').hide();
		$("#moreInfo").hide();		      
	    }
	});
    });
});

function resetForms() {
    for (i = 0; i < document.forms.length; i++) {
	document.forms[i].reset();
    }
    window.location.reload();
}
