var handleAddressChoice = function (chosenAddress) {
    let address_id   = 'address_id',
				subunit_id   = 'subunit_id',
				// location_id = 'location_id',
				addressInput = document.getElementById(addressId),
				addressInput2 = document.getElementById('addr_id'),
				city = document.getElementById("city"),
				zip = document.getElementById('zip'),				
				subunitInput = document.getElementById(subunitId),
				// locationId  = document.getElementById(location_id),
				latitude  = document.getElementById('latitude'),
				longitude  = document.getElementById('longitude'),
				jurisdiction = document.getElementById('jurisdiction'),
				address_info  = document.getElementById('address_info');
		// address_display = document.getElementById('address_display');
    // console.log(chosenAddress);
    addressInput.value = chosenAddress.id;
    zip.value = chosenAddress.zip;
    city.value = chosenAddress.city;
    jurisdiction.value = chosenAddress.jurisdiction_name;
    address_info.textContent +=' '+chosenAddress.city+' '+chosenAddress.zip;
		// address_display.style.display = "block";
    // An example of checking for a chosen subunit
    //
    // This just appends the subunit name to the displayed address
    // string. But it's entirely up to you to decide what address
    // information your application needs.
    if (chosenAddress.subunit) {
				latitude.value=chosenAddress.subunit.latitude;
				longitude.value=chosenAddress.subunit.longitude;
        subunitInput.value = chosenAddress.subunit.id;
				// locationId.value=chosenAddress.subunit.location_id ? chosenAddress.subunit.location_id:'';
        // display.innerHTML += ' ' + chosenAddress.subunit.type_code
				//    +  ' ' + chosenAddress.subunit.identifier;
				addressInput2.value = chosenAddress.streetAddress + ' '+chosenAddress.subunit.type_code +  ' ' + chosenAddress.subunit.identifier;
    }
    else{
				latitude.value = chosenAddress.latitude;
				longitude.value = chosenAddress.longitude;
				addressInput2.value = chosenAddress.streetAddress;
    }
};
var handleAddressChoice2 = function (chosenAddress) {
    let address_id   = 'address_id',
				// subunit_id   = 'subunit_id',
				// location_id = 'location_id',
				addressInput = window.opener.document.getElementById('addressId'),
				addressInput2 = window.opener.document.getElementById('addr_id'),
				zip = window.opener.document.getElementById('zip'),
				city = window.opener.document.getElementById("city"),
				state = window.opener.document.getElementById("state"),
				subunitInput = window.opener.document.getElementById('subunitId'),
				// locationId  = document.getElementById(location_id),
				latitude  = window.opener.document.getElementById('latitude'),
				longitude  = window.opener.document.getElementById('longitude'),
				jurisdiction = window.opener.document.getElementById('jurisdiction'),
				display   = window.opener.document.getElementById(address_id + '-display');
    // console.log(chosenAddress);
    // addressInput.value = chosenAddress.id;
    zip.value = chosenAddress.zip;
    city.value = chosenAddress.city;
    jurisdiction.value = chosenAddress.jurisdiction_name;
    state.value = "IN";
    // display.innerHTML  = chosenAddress.streetAddress;
    // An example of checking for a chosen subunit
    //
    // This just appends the subunit name to the displayed address
    // string. But it's entirely up to you to decide what address
    // information your application needs.
    if (chosenAddress.subunit) {
				latitude.value=chosenAddress.subunit.latitude;
				longitude.value=chosenAddress.subunit.longitude;
        subunitInput.value = chosenAddress.subunit.id;
				addressInput.value = chosenAddress.id;;
				// locationId.value=chosenAddress.subunit.location_id ? chosenAddress.subunit.location_id:'';
        // display.innerHTML += ' ' + chosenAddress.subunit.type_code
				//    +  ' ' + chosenAddress.subunit.identifier;
				addressInput2.value = chosenAddress.streetAddress + ' '+chosenAddress.subunit.type_code +  ' ' + chosenAddress.subunit.identifier;
    }
    else{
				latitude.value = chosenAddress.latitude;
				longitude.value = chosenAddress.longitude;
				addressInput2.value = chosenAddress.streetAddress;
				addressInput.value = chosenAddress.id;;
    }
};


