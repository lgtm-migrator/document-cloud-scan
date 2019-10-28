

function initImgProcessingCkbs() {
    var $ckbs = $('.div-img-processing input[type=checkbox]');

    $ckbs.prop('checked', false);
    $ckbs.attr('data-member-value', '0');
}

function showMask(){     
	$("#mask").css("height",$(document).height());
	$("#mask").css("width",$(document).width());
	$("#mask").show();
}
function hideMask(){
	$("#mask").hide();
}

var $doc = $(document);
$doc.ready(function () {

	
    initImgProcessingCkbs();


    $('.numb-int-validation').val('');

	$doc.on('click', '#btnCloseScan', function () {
		closeScan();
	});

	$doc.on('click', '#btnRemove', function () {
		removeImage && removeImage();
	});
	$doc.on('click', '#btnScan', function () {
		emptySelection();

		var strSource = 'any', // $('input[name=rdSource]:checked').val(), 
		strPixelFormat = $('input[name=rdPixelType]:checked').val(),
		strResolution = $('#tbResolution option:selected').text(),
		//bCheckCropping = $('#chkCropping').checked,
		//bCheckRotation = $('#chkRotation').checked,
		bCheckAutomaticDeskew = $('#chkAutomaticDeskew').prop('checked'),
		bCheckDiscardBlankImages = $('#chkDiscardBlankImages').prop('checked');
		
		bDuplex = $('#rdDuplex').prop('checked');
		bAutoFeeder = $('#rdFeeder').prop('checked');
		
		if(bDuplex) {
			strSource = 'feederRear';
		} else {
			if(bAutoFeeder) {
				strSource = 'feederFront';
			}
		}

		var result = [
			'{',
			'"actions":[{',
			'"action":"configure",',
			'"streams":[{',
			'"sources":[{',
			'"source":"',strSource,'",',
			'"pixelFormats":[{',
			'"pixelFormat":"',strPixelFormat,'",',
			'"attributes":['
		];
		

		result.push(genAttribute('resolution', strResolution));	
		result.push(',');
		result.push(genAttribute('automaticDeskew', bCheckAutomaticDeskew?'on':'off'));
		result.push(',');
		result.push(genAttribute('discardBlankImages', bCheckDiscardBlankImages?'on':'off'));

		if(strPixelFormat == 'bw1'){
			result.push(',');
			result.push(genCompression_G4());
		} else if (strPixelFormat == 'rgb24') {
			result.push(',');
			result.push(genCompression_JPG());
		} else {
			result.push(',');
			result.push(genCompression_Auto());
		}

		//result.push(',');
		//result.push(genWidth(300));
		
		//if(bCheckCropping) {
		//	result.push(',');
		//	result.push(genAttribute('cropping', 'automatic'));
		//}
			
		//if(bCheckRotation){
		//	result.push(',');
		//	result.push(genAttribute('rotation', 'automatic'));
		//}
		
		result.push(']}]}]}]}]}');
		
		var tdt = result.join('');
		
		
		$('#btnScan').attr('disabled', true);
		$('#btnRemove').attr('disabled', true);
		showMask();
		doScan(tdt, function(){
			hideMask();
			$('#btnScan').attr('disabled', false);
			$('#btnRemove').attr('disabled', false);
		});
	});
	
});


function genAttribute(strName, strValue) {
	return [
			'{"attribute":"',
			strName,
			'","values":[{"value":"',
			strValue,
			'"}]}'
		].join('');
}

function genCompression_G4() {
	return '{"attribute":"compression","values":[{"value":"group4"}]}';
}
function genCompression_JPG() {
	return '{"attribute":"compression","values":[{"value":"jpeg"}]}';
}
function genCompression_Auto() {
	return '{"attribute":"compression","values":[{"value":"autoVersion1"}]}';
}

function genWidth(iWidth) {
	return ['{"attribute":"width","values":[{"value":"',iWidth,'"}]}'].join('');
}

