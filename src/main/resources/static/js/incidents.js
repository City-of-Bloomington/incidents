clicked_button_id = "";
var popupWin;
var icons = {
  header: "ui-icon-circle-plus",
  activeHeader: "ui-icon-circle-minus",
};

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
}
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
$(document).ready(function () {
  $("input.timepicker").timepicker({
    timeFormat: "hh:mm p",
    interval: 15,
    startTime: "12:00 AM",
    dynamic: false,
    dropdown: true,
    scrollbar: true,
  });
});
$(document).ready(function () {
  $("#value_id").change(verifyTotalValue);
});

function verifyTotalValue() {
  var old_val = $("#old_value_id").val();
  var val = $("#value_id").val();
  var balance = $("#balance_id").val();
  var max_val = $("#max_total_id").val();
  //
  // if max_val is set to 0 this means there is not limit
  //
  if (max_val == 0) return true;
  if (!$.isNumeric(val)) {
    alert("You need to provide a valid value ");
    $("#value_id").focus();
    return false;
  }
  if (!$(this).prop("required")) {
    // not required but what if the user enter a value
    // still we need to check for the total allowed
    if (val * 1 > 0) {
      var new_total =
        parseFloat(balance) + parseFloat(val) - parseFloat(old_val);
      if (new_total * 1 > max_val * 1) {
        alert(
          "Current balance of  $" +
            new_total +
            " is greater than the max allowed of  $" +
            max_val
        );
        $("#value_id").focus();
        return false;
      }
    }
  } else {
    if (val.trim() == "" || val * 1 == 0) {
      alert("You need to provide a value for the damage ");
      $("#value_id").focus();
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
      $("#value_id").focus();
      return false;
    }
  }
  return true;
}
function verifyIncidentInput() {
  var start_date = $("#start_date").val();
  var start_time = $("#start_time").val();
  var date_description = $("#date_description").val().trim();
  var end_date = $("#end_date").val();
  var end_time = $("#end_time").val();
  if (start_date === "" && start_time === "" && date_description === "") {
    alert("Please provide incident date and time");
    $("#start_date").focus();
    return false;
  }
  if (start_date != "" && start_time === "") {
    alert("Please provide incident start time");
    $("#start_time").focus();
    return false;
  }
  if (end_date != "" && end_time === "") {
    alert("Please provide incident end time");
    $("#end_time").focus();
    return false;
  }
  var details = $("#details_id").val();
  if (details === "") {
    alert("Please provide incident details");
    $("#detail_id").focus();
    return false;
  }
  return true;
}
function verifyBusinessIncidentInput() {
		var start_date = $("#start_date").val();
		var start_time = $("#start_time").val();
		var date_description = $("#date_description").val().trim();
		var end_date = $("#end_date").val();
		var end_time = $("#end_time").val();
		var type = $("incident_type").val();
		if (start_date === "" && start_time === "" && date_description === "") {
				alert("Please provide incident date and time");
				$("#start_date").focus();
				return false;
		}
		if (start_date != "" && start_time === "") {
				alert("Please provide incident start time");
				$("#start_time").focus();
				return false;
		}
		if (end_date != "" && end_time === "") {
				alert("Please provide incident end time");
				$("#end_time").focus();
				return false;
		}
		if(type == ''){
				alert("Please provide incident type");
				$("#incident_type").focus();
				return false;
		}
		var details = $("#details_id").val();
		if (details === "") {
				alert("Please provide incident details");
				$("#detail_id").focus();
				return false;
		}
		return true;
}
function verifyAddress() {
  var addr = $("#addr_id").val();
  if (addr === "") {
    alert("Please provide incident address");
    $("#addr_id").focus();
    return false;
  }
  var city = $("#city").val();
  if (city === "") {
    alert("Please provide city");
    return false;
  }
  if (city != "Bloomington") {
    alert("Your address is not in Bloomington");
    return false;
  }
  var jurisdiction = $("#jurisdiction").val();
  if (jurisdiction != "" && jurisdiction != "Bloomington") {
    alert("Your address is not in Bloomington jurisdiction");
    return false;
  }
  var state = $("#state").val();
  if (state === "") {
    alert("Please provide state");
    $("#state").focus();
    return false;
  }
  var zip = $("#zip").val();
  if (zip === "") {
    alert("Please provide zip code");
    return false;
  }
  return true;
}
function verifyFraudInput() {
  var type = $("#fraudType option:selected").val();
  if (type === "") {
    alert("Please select the fraud type");
    $("#fraudType").focus();
    return false;
  }
  var typeText = $("#fraudType option:selected").text();
  if (typeText == "Other Specify") {
    otherType = $("#otherType").val();
    if (otherType === "") {
      alert("Please specify other fraud type");
      $("#otherType").focus();
      return false;
    }
  }
  return true;
}
function handelRelatedFraud() {
  var typeVal = $("#fraudType option:selected").val();
  if (typeVal === "") {
    return;
  }
  if (typeVal == 1 || typeVal == 3 || typeVal == 4) {
    $("#type_other").hide();
    $("#personal").hide();
    $("#account").show();
  } else if (typeVal == 2 || typeVal == 5 || typeVal == 6) {
    $("#type_other").hide();
    $("#personal").show();
    $("#account").hide();
  } else {
    // for other
    $("#type_other").show();
    $("#personal").show();
    $("#account").show();
  }
}

$(document).ready(function () {
  $("#addr_id").autocomplete(
    {
      source: "/incidents/addressService",
      minLength: 5,
      dataType: "json",
      delay: 100,
      select: function (event, ui) {
        if (ui.item) {
          $("#address-full")
            .css({ display: "block" })
            .text(
              ui.item.address +
                " " +
                ui.item.city +
                ", " +
                ui.item.state +
                " " +
                ui.item.zip
            );
          $("#addressId").val(ui.item.address_id);
          $("#name").val(ui.item.streetAddress);
          $("#latitude").val(ui.item.latitude);
          $("#longitude").val(ui.item.longitude);
          $("#city").val(ui.item.city);
          $("#zip").val(ui.item.zip);
          $("#state").val(ui.item.state);
          $("#jurisdiction").val(ui.item.jurisdiction_name);
          if (ui.item.subunit_id) {
            $("#subunitId").val(ui.item.subunit_id);
          } else {
            $("#subunitId").val("");
          }
        }
      },
    },
    "appendTo",
    ".autocomplete-wrapper"
  );
});

function popup(mylink, windowTitle) {
  if (!window.focus) return true;
  var href;
  if (typeof mylink == "string") href = mylink;
  else href = mylink.href;
  window.open(href, windowTitle, "width=500,height=400,scrollbars=yes");
  return false;
}
