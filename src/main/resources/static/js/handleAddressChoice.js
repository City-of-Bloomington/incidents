var handleAddressChoice = function (chosenAddress) {
    let address_id   = 'address_id',
	// subunit_id   = 'subunit_id',
	// location_id = 'location_id',
	addressInput = document.getElementById(address_id),
	addressInput2 = document.getElementById('addr_id'),
	zip = document.getElementById('zip'),
	city = document.getElementById("city"),
	// subunitInput = document.getElementById(subunit_id),
	// locationId  = document.getElementById(location_id),
	latitude  = document.getElementById('latitude'),
	longitude  = document.getElementById('longitude'),		
	display   = document.getElementById(address_id + '-display');
    // console.log(chosenAddress);
    addressInput.value = chosenAddress.id;
    zip.value = chosenAddress.zip;
    city.value = chosenAddress.city;
    // display.innerHTML  = chosenAddress.streetAddress;
    // An example of checking for a chosen subunit
    //
    // This just appends the subunit name to the displayed address
    // string. But it's entirely up to you to decide what address
    // information your application needs.
    if (chosenAddress.subunit) {
	latitude.value=chosenAddress.subunit.latitude;
	longitude.value=chosenAddress.subunit.longitude;
        // subunitInput.value = chosenAddress.subunit.id;
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


