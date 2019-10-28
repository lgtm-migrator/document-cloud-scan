'use strict';

var autoRefresh = false;

function loadScanners() {
  var authorizationToken = localStorage.getItem('authorization_token');

  dynamsoft.twainCloud.getScanners(authorizationToken)
        .then(function (data) {

            // fill scanners table
          var rows = '';
          data.forEach(function(twain) {
            rows += '<option value="' + twain.id + '">' + twain.name + '</option>';
          });
		  $('#tbSource').html(rows);

        })
        .fail(function (error) {
          if(autoRefresh) {
            dynamsoft.twainCloud.refreshToken(loadScanners);
          } else {
            log('Unauthorized: ' + JSON.stringify(error));
          }
        });
}
var imgData, arrImgData;

function makeAsyncRequest(url, callback)
{
	var httpRequest = new XMLHttpRequest();
	if (httpRequest.overrideMimeType) {
		httpRequest.overrideMimeType('application/json');
	}

	if(callback) {
		httpRequest.onreadystatechange = function () {
			if (httpRequest.readyState == 4
					&& httpRequest.status == 200)
				callback(httpRequest.response);
		};
	}

	httpRequest.open('GET', url, true);
	httpRequest.send();
}

function readBlobAsDataURL(blob, callback) {
    var a = new FileReader();
    a.onload = function(e) {callback && callback(e.target.result);};
    a.readAsDataURL(blob);
}


function getTwainCloud() {
	var twain = false, 
		objTwainCloud = false,
		scannerId = $('#tbSource').val();
		
	if(scannerId && scannerId != '') {
		twain = dynamsoft.twainCloud.getScannerById(scannerId);
	}

	if(twain && scannerId) {
		
		objTwainCloud = dynamsoft.twainCloud.scannerMap[scannerId];
	
		if(!objTwainCloud)
		{
			objTwainCloud = new TwainCloud(scannerId);
			dynamsoft.twainCloud.scannerMap[scannerId] = objTwainCloud;
		}
	}

	return objTwainCloud;
}

var isFirst = true;
function doScan(tdt, funFinishedScan) {
	var twain = getTwainCloud(), tdtJson;

	if(isFirst) {

		dynamsoft.twainCloud.objImageManager.setDisplayFun(displayPDF);
		
		var objScroll = document.getElementById('dwtScroll');
		
		dynamsoft.twainCloud.objScroll = new dynamsoft_scrollbar(
			objScroll, false, 0, function(index){
				console.log('current index: ' + index);
				setTimeout(function(){
					dynamsoft.twainCloud.objImageManager.go(index);
				},100);
			});
		
		isFirst = false;
	}

  console.log('tdt: ' + tdt);
  
  try{
	  tdtJson = JSON.parse(tdt);
  }catch(err){
	  console.log('tdt json error: ' + tdt);
	  return;
  }
  
  if (twain) {
	var funFailed = function(error) {
			log('Unauthorized: ' + JSON.stringify(error));
			funFinishedScan();

			closeSession(function(err){
				if(!err) {
					releaseImageBlocks();
				}
			});
		};

	twain.method_handlers['evt_imageBlocks'] = function(blockNum, callbackContinueEvent){
		log('evt_imageBlocks:' + blockNum);
		
		var readImageCallback = function(oBody) {
			
			var newDeferred = $.Deferred();
			
			console.log('evt_imageBlocks read');
			console.log(oBody);
			if(oBody.results.success) {
				var blockUrl = oBody.results.imageBlockUrl;
				if (blockUrl && blockUrl != '') {

					twain.readImageBinBlockByUrl(blockUrl).then(function(_bin, _io){
						
						if(_bin instanceof ArrayBuffer) {
							imgData = _bin;
							arrImgData = _bin;
						} else {
							var _json = JSON.parse(_bin);
							imgData = _json.data?_json.data:[];
							arrImgData = _io.responseText;
						}
						
						dynamsoft.twainCloud.objImageManager.add(imgData);
						
						console.log(blockUrl);
						console.log(imgData);
						console.log(dynamsoft.twainCloud.objImageManager.length);
						
						newDeferred.resolve();
					})
					.fail(function(e){
						newDeferred.reject(e);
					});
					
				} else {
					// blockUrl error
					newDeferred.reject('invalid image block url');
				}
			} else {
				// readImageBlock error
				newDeferred.reject('readImageBlock error');
			}
			
			return newDeferred.promise();
			
		}, taskAdd = function(tasks, index){
			tasks.push(function(){
				
				var newDeferred1 = $.Deferred();
				twain.readImageBlock(index).then(readImageCallback).then(function(){
					newDeferred1.resolve();
				});
				return newDeferred1.promise();
			});			
		}, app = function (count){
			var tasks = [];

			for (var i = 1; i <= count; i++) {
				taskAdd(tasks, i);
			}
			
			console.log(tasks.length);
			
			return tasks.reduce(function(prev,next){
				return prev.then(next);
			}, $.Deferred().resolve());
		}

		$.when(app(blockNum)).done(function(oBody) {
			console.log('readImageBlock: all done.');
			console.log(oBody);

			twain.method_handlers['evt_imageBlocks'] = null;
			setTimeout(releaseImageBlocks, 300);
		 })
        .fail(funFailed);
	};

    twain.startSession()
      .then(function() {

		  setTimeout(function () {
			  twain_cloud_dwt_waitForEvents(twain);
		  }, 300);

		  twain.sendTask(tdtJson)
		  .then(function () {

			  twain.startCapturing()
			  .then(function() {
				  funFinishedScan();
			  })
			  .fail(funFailed);
		  })
		  .fail(funFailed);
      })
      .fail(funFailed);
  }

}

function closeSession(callback) {
	var twain = getTwainCloud();

	if (twain) {

		twain.closeSession()
			  .then(function() {
				  if(callback)
					callback();
			  })
			 .fail(function(error) {
				  log('Unauthorized: ' + JSON.stringify(error));
				  if(callback)
					callback(error);
			  });
	} else {
		if(callback)
			callback('no twain.');
	}
}

function startSession() {
  var twain = getTwainCloud();

  if (twain) {

    twain.startSession()
      .then(function(sessionId) {
		  
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

function sendTask() {
  var twain = getTwainCloud();

  if (twain) {
    twain.sendTask()
      .then(function(session) {
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

function startCapturing() {
  var twain = getTwainCloud();

  if (twain) {
	console.log(twain['status']);
	  
    twain.startCapturing()
      .then(function(session) {
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

function stopCapturing(callback) {
  var twain = getTwainCloud();

  if (twain) {
    twain.stopCapturing()
      .then(function(session) {
		  if(callback)
			  callback();
      })
      .fail(function(error) {
          log('Unauthorized: ' + JSON.stringify(error));
		  if(callback)
			  callback(error);
      });
  }

}



function waitForEvents() {
  var twain = getTwainCloud();

  if (twain) {
    twain_cloud_dwt_waitForEvents(twain);
  }

}

function readImageBlockMetadata() {
  var twain = getTwainCloud();

  if (twain) {
    twain.readImageBlockMetadata()
      .then(function(session) {
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

function readImageBlock(index) {
  var twain = getTwainCloud();

  if (twain) {
    return twain.readImageBlock(index)
      .then(function(session) {
		  resolve(session);
      })
      .fail(function(error) {
        if(autoRefresh) {
          dynamsoft.twainCloud.refreshToken(startSession);
        } else {
          log('Unauthorized: ' + JSON.stringify(error));
        }
      });
  }

  return new Promise();
}

function releaseImageBlocks(callback) {
  var twain = getTwainCloud();

	console.log('== start releaseImageBlocks ==');
  if (twain) {
    twain.releaseImageBlocks(1, 1)
      .then(function(session) {
		  if(callback)
			callback();
      })
      .fail(function(error) {
          log('Unauthorized: ' + JSON.stringify(error));
		  if(callback)
			callback(error);
      });
  }

}


function initializeAuthorizedPage() {
	dynamsoft.dwtEnv.CloseDialog();
	$('.dwt-login-content').hide();
	loadScanners();
}

function initializeUnauthorizedPage() {

	$('.dwt-login-content').show();
	dynamsoft.dwtEnv.ShowDialog(400, 247, dynamsoft.lib.get('dwtLogin'));

	var frmLogin = dynamsoft.lib.get('frmLogin');
	if (frmLogin) {
		frmLogin['name'].focus();
	}

}

$(function () {
	window.onbeforeunload = function(event) {
		closeScan();
	};
	
	$(".dwt-login-name").keypress(function (e) {
		if (e.which == 13) {
			var frmLogin = dynamsoft.lib.get('frmLogin');
			frmLogin['password'].focus();
		}
	});
	$(".dwt-login-password").keypress(function (e) {
		if (e.which == 13) {
			var btnTCLogin = dynamsoft.lib.get('btnTCLogin');
			btnTCLogin.focus();
			btnTCLogin.click();
		}
	});
	$('#btnTCLogin').on('click', function (event) {

		$('#divLoginError').html('&nbsp;');
		$.ajax({
			type: 'POST',
			url: '/api/user',
			data: $('#frmLogin').serialize(),
			success: function (ret) {
				if (ret && ret.token) {
					//success
					dynamsoft.twainCloud.saveAuthTokens(ret.token, ret.refreshToken);
					window.history.replaceState({ authorization_token: '' }, '');

					initializeAuthorizedPage();
				} else {
					// failed
					setTimeout(function () {
						$('#divLoginError').html('Error: Incorrect name or password.');
					}, 500);
					
				}
			}
		});

	});

  $('#refreshScanners').on('click', loadScanners);
  $('#gotoScanners').on('click', function(event) {
    location.href = 'index_src.html' + location.search;
  });
  $('#logout').on('click', function(event) {
    logout();
  });

  processQueryAuth(initializeAuthorizedPage, initializeUnauthorizedPage);
});

function closeScan() {
	closeSession(function(err){
		console.log(err);
		releaseImageBlocks();
	});
}

function displayPDF(_imgData){
	if(_imgData == null) {
		var canvas = document.getElementById('the-canvas');
		var context = canvas.getContext('2d');
		context.clearRect(0,0,canvas.width,canvas.height);
		return;
	}
  pdfjsLib.getDocument({data: _imgData}).then(function (pdf) {
	console.log('PDF page count:' + pdf.numPages);
    // Fetch the first page.
    pdf.getPage(1).then(function getPageHelloWorld(page) {

      var scale = (560-4) / page.getViewport(1.0).width;
      var viewport = page.getViewport(scale);
	  
      var canvas = document.getElementById('the-canvas');
      var context = canvas.getContext('2d');
      canvas.height = viewport.height;
      canvas.width = viewport.width;
	  
	  $('.ViewerWrapper').height(viewport.height);
	  $('.ViewerContainer').height(viewport.height);
	  
	  if(dynamsoft.twainCloud.objImageManager.images.length <= 1) {
		$('.ViewerScroll').hide();
	  } else {
		var max = dynamsoft.twainCloud.objImageManager.images.length-1;

		dynamsoft.twainCloud.objScroll.max = max;		
		$('#dwtScroll').height(viewport.height * (1 / (max + 1)));

		$('.ViewerScroll').show();
		$('.ViewerScroll').height(viewport.height);
		
		// re-caculate pos
		var curPos = dynamsoft.twainCloud.objImageManager.curIndex;
		
		// vertical
		$('#dwtScroll').css('top', curPos * viewport.height * (1 / (max + 1)) + 'px');
		
	  }
	  
      var renderContext = {
        canvasContext: context,
        viewport: viewport
      };
      page.render(renderContext);
    });
  });
}


function removeImage() {
	dynamsoft.twainCloud.objImageManager.remove();
}