'use strict';

var autoRefresh = false;

function getSelectedScanner() {
  var scanner = false, scannerId = $('#scannersTable tbody .table-success').attr('id');
  if(scannerId) {
	  scanner = dynamsoft.twainCloud.getScannerById(scannerId);
  }
  return scanner;
}

function loadScanners() {
  var authorizationToken = localStorage.getItem('authorization_token');

  dynamsoft.twainCloud.getScanners(authorizationToken)
        .then(function (data) {

            // fill scanners table
          var rows = [];
          data.forEach(function(scanner) {
			rows.push('<tr id="' + scanner.id + '">');
			rows.push('<td>' + scanner.name + '</td>');
			rows.push('<td>' + (scanner.manufacturer ? scanner.manufacturer : '&nbsp;')+ '</td>');
			rows.push('<td>' + (scanner.model ? scanner.model : '&nbsp;') + '</td>');
			rows.push('<td>' + (scanner.connection_state ? scanner.connection_state : '&nbsp;') + '</td>');
			rows.push('</tr>');
          });
          $('#scannersTable tbody').html(rows.join(''));

            // resubscribe new rows
          $('#scannersTable > tbody > tr').click(function(event) {
            $('#scannersTable > tbody > tr').removeClass('table-success');
            $(event.currentTarget).addClass('table-success');
			
			$('#startSession').attr('disabled',false);
			$('#deleteScanner').attr('disabled',false);
          });
        })
        .fail(function (error) {
          if(autoRefresh) {
            dynamsoft.twainCloud.refreshToken(loadScanners);
          } else {
            log('Unauthorized: ' + JSON.stringify(error));
          }
        });
}

function getTwainCloud() {
	var twain = false,
		objTwainCloud = false,
		scannerId = $('#scannersTable tbody .table-success').attr('id');

	if (scannerId && scannerId != '') {
		twain = dynamsoft.twainCloud.getScannerById(scannerId);
	}

	if (twain && scannerId) {

		objTwainCloud = dynamsoft.twainCloud.scannerMap[scannerId];

		if (!objTwainCloud) {
			objTwainCloud = new TwainCloud(scannerId);
			dynamsoft.twainCloud.scannerMap[scannerId] = objTwainCloud;
		}
	}

	return objTwainCloud;
}

function startSession() {
  var scanner = getSelectedScanner();

  if (scanner) {
	var twain = new TwainCloud();

    twain.startSession(scanner)
      .then(function() {
		$('#sendTask').attr('disabled',false);
		$('#startCapturing').attr('disabled',false);
		$('#stopCapturing').attr('disabled',false);
		$('#closeSession').attr('disabled',false);
		$('#waitForEvents').attr('disabled',false);
		$('#readImageBlockMetadata').attr('disabled',false);
		$('#releaseImageBlocks').attr('disabled',false);
		
		$('#readImageBlock').attr('disabled',false);
      })
      .fail(function(error) {
        if(autoRefresh) {
          dynamsoft.twainCloud.refreshToken(startSession);
        } else {
          log('Unauthorized: ' + JSON.stringify(error));
        }
      });
  }

}

function deleteScanner() {
	var twain = getTwainCloud();
	if (twain) {
		twain.deleteScanner().then(loadScanners);
	}
}


function initializeAuthorizedPage() {
	dynamsoft.dwtEnv.CloseDialog();
	loadScanners();
}

function initializeUnauthorizedPage() {
	dynamsoft.dwtEnv.CloseDialog();
	console.log('initializeUnauthorizedPage');
}

$(function () {

	$('#gotoHome').on('click', function (event) {
		setTimeout(function () {
			location.href = 'index.html';
		}, 100);
	});

	$('#logout').on('click', function(event) {
		logout();
	});

	$('#refreshScanners').on('click', loadScanners);
	$('#deleteScanner').on('click', deleteScanner);

	processQueryAuth(initializeAuthorizedPage, initializeUnauthorizedPage);
});
