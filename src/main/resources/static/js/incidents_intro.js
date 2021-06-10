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
    $('div.question').each(function () {
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
						alert(answer);
						$('#next_div').hide();		  		  
						$('#moreInfo').show();		  
				}
				else{
						$('#moreInfo').hide();
						$('#next_div').show();
						$('#typeOptions').hide();
				}
    })
    var totalQuestions = $('.questions').length;
    var currentQuestion = 0;
	  $question = $('.question');
    $question.hide();
    $($question.get(currentQuestion)).fadeIn();
    $('#next').click(function(){
	$($question.get(currentQuestion)).fadeOut(function(){
	    currentQuestion = currentQuestion + 1;
	    if(currentQuestion == totalQuestions){
		// if the last question 
		$("#moreInfo").show();
	    }else{
		$($question.get(currentQuestion)).fadeIn();
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
