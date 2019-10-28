///
/// description:
///    required js: jQuery.js, common.js
///
'use strict';

var dynamsoft = dynamsoft || {};


function log(message) {
  console.log(message);
}

function TwainCloud(id) {
	var _this = this;
	
	_this.id = id;
	_this.privet_token = false;
	_this.requestId = false;
	_this.method_handlers = {};
	_this['status'] = 'ready';
}

(function() {
		
	function ImageManager() {
		this.curIndex=-1;
		this.images=[];
		this.onScroll=false;
	}
	ImageManager.prototype.setDisplayFun = function(funDisplay) {
		this.funDisplay = funDisplay;
	};

	ImageManager.prototype.add = function(imgData) {
		this.images.push(imgData);
		this.curIndex = this.images.length-1;
		this.refresh();
		if(this.onScroll) {
			this.onScroll(1);
		}
	};

	ImageManager.prototype.refresh = function() {
		if(this.curIndex > this.images.length-1)
			this.curIndex = this.images.length-1;
		
		if(this.funDisplay){

			if(this.curIndex>=0) {
				this.funDisplay(this.images[this.curIndex]);
			} else {
				this.funDisplay(null);
			}

		}
	};

	ImageManager.prototype.remove = function() {
		this.images.splice(this.curIndex,1);
		this.refresh();
	};

	ImageManager.prototype.go = function(index) {
		this.curIndex = index;
		if(this.curIndex < 0)
			this.curIndex = 0;
		
		this.refresh();
	};

	ImageManager.prototype.previous = function() {
		this.curIndex--;
		if(this.curIndex < 0)
			this.curIndex = 0;
		
		this.refresh();
	};

	ImageManager.prototype.next = function() {
		this.curIndex++;
		this.refresh();
	};

	function guid() {
		function s4() {
			return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
		}
		return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
	}
	
	function sendRequestToScan(verb, url, privet_token, send_data, dataType) {
		var auth_token = localStorage.getItem('authorization_token'), 
			cfg = {
				method: verb,
				url: url,
				dataType:'json',
				headers: {
					'authorization': auth_token, 
					'x-privet-token': privet_token
				}
			};
		
		if(dataType)
			cfg.dataType = dataType;
		
		if(verb=='POST') {
			cfg.contentType = 'application/json';
			cfg.data = send_data;	
		}
		
		return $.ajax(cfg).promise();
	}
  
	dynamsoft.twainCloud = {
		apiEndpoint: '/api',
		
		objImageManager: new ImageManager(),

		scanners : [],
		scannerMap: {},

		guid: guid,
		sendRequestToScan: sendRequestToScan,
		// [not used]
		// for google / facebook login
		login : function (provider, origin, query, bOpen) {
			log('Login...');

			var signinUrl = dynamsoft.twainCloud.apiEndpoint + '/authentication/signin/' + provider + '?t=1';
			if (origin) {
				signinUrl += '&origin=' + encodeURIComponent(origin);
			}
			
			if (query) {
				signinUrl += '&query=' + encodeURIComponent($.param(query));
			}
			
			if(bOpen) {
				window.open(signinUrl);
			} else {
				window.location.href = signinUrl;  
			}
		},
		
		closeConnect: function() {
		
			dynamsoft.twainCloud.scannerMap = {};
			
		},

		getScanners : function(token) {
			
		    log('Get Scanners...');

			var deferred_getScanners = $.Deferred();

			if (token) {
				
				dynamsoft.twainCloud.closeConnect();
				
				sendRequest('GET', dynamsoft.twainCloud.apiEndpoint + '/scanners?t=' + guid(), token)
				.done(function (data) {
					log(data);

					dynamsoft.twainCloud.scanners = data;
					deferred_getScanners.resolve(data);
				})
				.fail(function (error) {
					deferred_getScanners.reject(error);
				});
			} else {
				log('Missing authentication token');
				deferred_getScanners.reject('Missing authentication token');
			}

			return deferred_getScanners.promise();
		},

		getScannerById : function (id) {
			var scanners = dynamsoft.twainCloud.scanners;
			for(var i = 0; i < scanners.length; ++i) {
				if (scanners[i].id === id)
					return scanners[i];
			}
		},
	
		refreshToken : function (callback) {
			log('Refresh token...');

			// refresh token v2.0 change to POST
			$.ajax({
				method: 'POST',
				url: dynamsoft.twainCloud.apiEndpoint + '/refresh/' + getRefreshToken()
			})
			.done(function (data) {
				if (data.errorMessage) {
					log(data.errorMessage);
				} else {
					dynamsoft.twainCloud.saveAuthTokens(data.authorizationToken , data.refreshToken);
					if(callback)
						callback();
				}
			})
			.fail(function (error) {
				log('Unauthorized: ' + JSON.stringify(error));
			});
		},
		
		saveAuthTokens : function (authorization_token, refresh_token) {

			// Save token to local storage for later use
			if(authorization_token) {
				localStorage.setItem('authorization_token', authorization_token);
			}
			if(refresh_token) {
				localStorage.setItem('refresh_token', refresh_token);
			}

			log('authorization_token:'+ getAuthToken());
			log('refresh_token:' + getRefreshToken());
		},

		clearAuthTokens : function () {
			localStorage.removeItem('authorization_token');
			localStorage.removeItem('refresh_token');
		},

		actions : {
			'Default' : {"actions":[{"action":"configure"}]},
			'B&W': {"actions":[{"action":"configure","streams":[{"sources":[{"source":"any","pixelFormats":[{"pixelFormat":"bw1","attributes":[]}]}]}]}]}
			
		}

	};

	function sendRequest(verb, url, token, data) {
		  
		return $.ajax({
			method: verb,
			url: url,
			headers: { 
				"authorization": token
			},
			data: data
		}).promise();
	}

  
  TwainCloud.prototype.claim = function claim(token, scannerId, registrationToken) {
    var deferred_claim = $.Deferred();

    if (token) {
		log('Loading...');

		var claimEndpoint = dynamsoft.twainCloud.apiEndpoint + '/claim';
		sendRequest('POST', claimEndpoint, token, {
			scannerId: scannerId,
			registrationToken: registrationToken
		})
		.done(function (data) {
		  log(data);
		  deferred_claim.resolve(data);
		})
		.fail(function (error) {
		  deferred_claim.reject(error);
		});
		
    } else {
		log('Missing authentication token');
		deferred_claim.reject('Missing authentication token');
    }

    return deferred_claim.promise();
  };

  
  TwainCloud.prototype.getPrivetToken = function () {

	var twain = this, deferred_getPrivetToken = $.Deferred();
	if(twain.privet_token) {
		deferred_getPrivetToken.resolve(twain.privet_token);
		return deferred_getPrivetToken;
	} else {
		var requestId = guid();
		
		var apiEndpoint = dynamsoft.twainCloud.apiEndpoint,
			urlUser = apiEndpoint + '/user?protocol=websocket&t=' + guid(),
			urlPrivetInfoEx = apiEndpoint + '/scanners/' + twain.id + '/infoex?t=' + guid();

		twain.privet_token = false;
		twain.requestId = requestId;

		sendRequestToScan('GET', urlPrivetInfoEx, '', null).then(function (oBody) {

			if (oBody['error'] != null && oBody['error'] != '') {
				log('infoex response error: ' + oBody['error']);
				deferred_getPrivetToken.reject('[getPrivetToken()] infoex error: ' + oBody['error']);
				return;
			}

			log('infoex response: ' + JSON.stringify(oBody));
			if (oBody['x-privet-token']) {
				if (twain.privet_token != oBody['x-privet-token']) {
					twain.privet_token = oBody['x-privet-token'];
				}

				deferred_getPrivetToken.resolve(twain.privet_token);
			} else {
				deferred_getPrivetToken.reject('[getPrivetToken()] failed to get x-privet-token from /infoex payload.');
			}
		}).fail(function (error) {
			log('failed to get scan info: ' + JSON.stringify(error));
			deferred_getPrivetToken.reject(error);
		});
		
	}
	
	return deferred_getPrivetToken.promise();
  };

  TwainCloud.prototype.startSession = function () {
	  
    var twain = this,
		deferred_session = $.Deferred(),
		auth_token = localStorage.getItem('authorization_token');
		
    if (twain.id && auth_token) {
		
		console.log('current satus:' + twain['status']);
	
		twain.getPrivetToken().then(function (strPrivetToken) {

			var sessionEndpoint = dynamsoft.twainCloud.apiEndpoint + '/scanners/' + twain.id + '/twaindirect/session';
			sendRequestToScan('POST', sessionEndpoint, strPrivetToken, JSON.stringify({

				'kind': 'twainlocalscanner',
				'commandId': guid(),
				'method': 'createSession'

			}))
			.then(function (strBody) {
				
				console.log(strBody);
				
				var oBody;
				try {
					oBody = JSON.parse(strBody);
				} catch(e1) {
					
				}
				
				if(oBody && oBody.results && oBody.results.success) {
					
					var sessionId = oBody.results.session.sessionId,
						revision = oBody.results.session.revision,
						state = oBody.results.session.state;
						
					log('[SESSION] session id: ' + sessionId);
					log('[SESSION] startSession - session revision: ' + revision);
					twain.sessionId = sessionId;
					twain.revision = revision;
					
					deferred_session.resolve(sessionId);
					
					
				} else {
					
					deferred_session.reject(strBody);
				}
			})
			.fail(function (error) {
				deferred_session.reject(error);
			});
			  
		}).fail(function (error) {
			  log('Unauthorized: ' + JSON.stringify(error));
			  deferred_session.reject(error);
		});
		
	} else {
		
		deferred_session.reject('unauthorized');
    }

    return deferred_session.promise();
  };

  TwainCloud.prototype.sendTask = function (tdt) {
	// {"kind":"twainlocalscanner","commandId":"xxxx",
	//    "method":"sendTask",
	//    "params":{"sessionId":"xxxx","task":{"actions":[{"action":"configure"}]}}
	// }
	
	//  dynamsoft.twainCloud.actions['B&W']

    var twain = this,
		deferred_sendTask = $.Deferred(),
		auth_token = localStorage.getItem('authorization_token'),
		sessionId, privet_token;
	
	{
		sessionId = twain.sessionId;
		privet_token = twain.privet_token;
	}
		
    if (auth_token && privet_token && sessionId) {

		var sessionEndpoint = dynamsoft.twainCloud.apiEndpoint + '/scanners/' + twain.id + '/twaindirect/session',
			_params = {
				'sessionId': sessionId,
				'task': tdt ? tdt : dynamsoft.twainCloud.actions['B&W'] 
			};

		sendRequestToScan('POST', sessionEndpoint, privet_token, JSON.stringify({
			  
			'kind': 'twainlocalscanner',
			'commandId': guid(),
			'method': 'sendTask',
			'params': _params
			
		}))
		.then(function (strBody) {
			
			console.log(strBody);
			var oBody;
			try {
				oBody = JSON.parse(strBody);
			} catch(e1) {
				
			}
			
			if(oBody && oBody.results && oBody.results.success) {
				var revision = oBody.results.session.revision;
				log('[SESSION] sendTask - session revision: ' + revision);
				twain.revision = revision;
				deferred_sendTask.resolve();
				
			} else {
				deferred_sendTask.reject(strBody);
				
			}
		})
		.fail(function (error) {
			deferred_sendTask.reject(error);
			
		});
	} else {
		if(!auth_token)
			deferred_sendTask.reject('unauthorized');
		else if(!privet_token)
			deferred_sendTask.reject('no x-privet-token');
		else if(!sessionId)
			deferred_sendTask.reject('no sessionId');
		else
			deferred_sendTask.reject();
		
    }

    return deferred_sendTask.promise();
	
  };
  
  TwainCloud.prototype.startCapturing = function () {
	// {"kind":"twainlocalscanner","commandId":"xxxx",
	//    "method":"startCapturing",
	//    "params":{"sessionId":"xxxxx"}
	// }

    var twain = this,
		deferred_startCapturing = $.Deferred(),
		auth_token = localStorage.getItem('authorization_token'),
		sessionId, privet_token;
	
	{
		sessionId = twain.sessionId;
		privet_token = twain.privet_token;
	}
		
    if (auth_token && privet_token && sessionId) {

		var sessionEndpoint = dynamsoft.twainCloud.apiEndpoint + '/scanners/' + twain.id + '/twaindirect/session',
			_params = {
				'sessionId': sessionId
			};

		sendRequestToScan('POST', sessionEndpoint, privet_token, JSON.stringify({
			  
			'kind': 'twainlocalscanner',
			'commandId': guid(),
			'method': 'startCapturing',
			'params': _params
			
		}))
		.then(function (strBody) {
			
			console.log(strBody);
			var oBody;
			try {
				oBody = JSON.parse(strBody);
			} catch(e1) {
				
			}
			
			if(oBody && oBody.results && oBody.results.success) {
				var revision = oBody.results.session.revision;
				log('[SESSION] startCapturing - session revision: ' + revision);
				twain.revision = revision;
				deferred_startCapturing.resolve();
				
				
			} else {
					
				deferred_startCapturing.reject(strBody);
				
			}
		})
		.fail(function (error) {
			deferred_startCapturing.reject(error);
			
		});
	} else {
		if(!auth_token)
			deferred_startCapturing.reject('unauthorized');
		else if(!privet_token)
			deferred_startCapturing.reject('no x-privet-token');
		else if(!sessionId)
			deferred_startCapturing.reject('no sessionId');
		else
			deferred_startCapturing.reject();
		
    }

    return deferred_startCapturing.promise();
				
  };
  
  TwainCloud.prototype.stopCapturing = function () {
	// {"kind":"twainlocalscanner","commandId":"xxxx",
	//    "method":"stopCapturing",
	//    "params":{"sessionId":"xxxxx"}
	// }

    var twain = this, 
		deferred_stopCapturing = $.Deferred(),
		auth_token = localStorage.getItem('authorization_token'),
		sessionId, privet_token;
	
	{
		sessionId = twain.sessionId;
		privet_token = twain.privet_token;
	}
		
    if (twain && auth_token && privet_token && sessionId) {

		var sessionEndpoint = dynamsoft.twainCloud.apiEndpoint + '/scanners/' + twain.id + '/twaindirect/session',
			_params = {
				'sessionId': sessionId
			};

		sendRequestToScan('POST', sessionEndpoint, privet_token, JSON.stringify({
			  
			'kind': 'twainlocalscanner',
			'commandId': guid(),
			'method': 'stopCapturing',
			'params': _params
			
		}))
		.then(function (strBody) {
			
			var oBody;
			try {
				oBody = JSON.parse(strBody);
			} catch(e1) {
				
			}
			
			if(oBody && oBody.results && oBody.results.success) {
				
				console.log(strBody);
				
				var revision = oBody.results.session.revision;
				var curState = oBody.results.state;
				log('[SESSION] stopCapturing - session revision: ' + revision);
				twain.revision = revision;
				
				twain['status'] = curState;

				deferred_stopCapturing.resolve();
				
			} else {

				log('[SESSION] stopCapturing - error: ' + strBody);
				deferred_stopCapturing.reject('stop capturing failed.');
				
			}
		})
		.fail(function (error) {
			deferred_stopCapturing.reject(error);
			
		});
	} else {
		if(!auth_token)
			deferred_stopCapturing.reject('unauthorized');
		else if(!privet_token)
			deferred_stopCapturing.reject('no x-privet-token');
		else if(!sessionId)
			deferred_stopCapturing.reject('no sessionId');
		else
			deferred_stopCapturing.reject();
		
    }

    return deferred_stopCapturing.promise();
				
  };
  
  
  TwainCloud.prototype.readImageBlockMetadata = function (blockNum) {
	// {"kind":"twainlocalscanner","commandId":"xxxx",
	//    "method":"readImageBlockMetadata",
	//    "params":{"sessionId":"xxxx","imageBlockNum":1}
	// }
	
    var twain = this,
		deferred_readMetadata = $.Deferred(),
		auth_token = localStorage.getItem('authorization_token'),
		sessionId, privet_token;
	
	{
		sessionId = twain.sessionId;
		privet_token = twain.privet_token;
	}
		
    if (twain && auth_token && privet_token && sessionId) {
		var imageBlockNum = blockNum;
		if(!imageBlockNum)
			imageBlockNum = 1;
		
		var sessionEndpoint = dynamsoft.twainCloud.apiEndpoint + '/scanners/' + twain.id + '/twaindirect/session',
			_params = {
				'sessionId': sessionId,
				'imageBlockNum': imageBlockNum
			};
			
		sendRequestToScan('POST', sessionEndpoint, privet_token, JSON.stringify({
			  
			'kind': 'twainlocalscanner',
			'commandId': guid(),
			'method': 'readImageBlockMetadata',
			'params': _params
			
		}))
		.then(function (strBody) {
			
			console.log(strBody);
			var oBody;
			try {
				oBody = JSON.parse(strBody);
			} catch(e1) {
				
			}
			
			if(oBody && oBody.results && oBody.results.success) {
				var revision = oBody.results.session.revision;
				log('[SESSION] readImageBlockMetadata - session revision: ' + revision);
				twain.revision = revision;
				deferred_readMetadata.resolve();
				
			} else {
				
				deferred_readMetadata.reject(strBody);
				
			}
		})
		.fail(function (error) {
			deferred_readMetadata.reject(error);
			
		});
	} else {
		if(!auth_token)
			deferred_readMetadata.reject('unauthorized');
		else if(!privet_token)
			deferred_readMetadata.reject('no x-privet-token');
		else if(!sessionId)
			deferred_readMetadata.reject('no sessionId');
		else
			deferred_readMetadata.reject();
		
    }

    return deferred_readMetadata.promise();
	
	
  };
  
  

  TwainCloud.prototype.readImageBlock = function (blockNum) {
	// {"kind":"twainlocalscanner","commandId":"xxxx",
	//    "method":"readImageBlock",
	//    "params":{"sessionId":"xxxx","withMetadata":true,"imageBlockNum":1}
	// }

    var twain = this,
		deferred_readImageBlock = $.Deferred(),
		auth_token = localStorage.getItem('authorization_token'),
		sessionId, privet_token;
	
	{
		sessionId = twain.sessionId;
		privet_token = twain.privet_token;
	}
		
    if (auth_token && privet_token && sessionId) {
		var imageBlockNum = blockNum;
		if(!imageBlockNum)
			imageBlockNum = 1;

		var sessionEndpoint = dynamsoft.twainCloud.apiEndpoint + '/scanners/' + twain.id + '/twaindirect/session',
			_params = {
				'sessionId': sessionId,
				'withMetadata':true,
				'imageBlockNum': imageBlockNum
			};
			
		sendRequestToScan('POST', sessionEndpoint, privet_token, JSON.stringify({
			  
			'kind': 'twainlocalscanner',
			'commandId': guid(),
			'method': 'readImageBlock',
			'params': _params
			
		}))
		.then(function (strBody) {
			var oBody;
			
			console.log(strBody);
			if(null != strBody) {
				try {
					oBody = JSON.parse(strBody);
				} catch(e1) {
					
				}

				if(oBody && oBody.results && oBody.results.success) {
					var revision = oBody.results.session.revision;
					log('[SESSION] readImageBlock - session revision: ' + revision);
					twain.revision = revision;
					deferred_readImageBlock.resolve(oBody);
					
				} else {

					deferred_readImageBlock.reject(strBody);
					
				}
			}
		})
		.fail(function (error) {
			deferred_readImageBlock.reject(error);
			
		});
	} else {
		if(!auth_token)
			deferred_readImageBlock.reject('unauthorized');
		else if(!privet_token)
			deferred_readImageBlock.reject('no x-privet-token');
		else if(!sessionId)
			deferred_readImageBlock.reject('no sessionId');
		else
			deferred_readImageBlock.reject();
		
    }

    return deferred_readImageBlock.promise();
  };
  
  TwainCloud.prototype.readImageBinBlockByUrl = function (blockUrl) {
	    //scanners/{scannerId}/binblocks/{blockId}

    var twain = this,
		deferred = $.Deferred(),
		auth_token = localStorage.getItem('authorization_token'),
		sessionId, privet_token;
	
    if (auth_token) {
		var imageBlockNum = 1;
		
			var urlBlock = blockUrl + '?t=' + guid();
			
			try{
				var xhr = new XMLHttpRequest();
				
				xhr.open('GET', urlBlock, true);
				
				xhr.setRequestHeader("Content-Type", 'text/plain; charset=x-user-defined');
				if (xhr.overrideMimeType) {
					xhr.overrideMimeType('application/octet-stream');
				}
				
				xhr.responseType = "arraybuffer";
				xhr.setRequestHeader("x-privet-token", ""); 
				xhr.setRequestHeader("authorization", auth_token); 
				xhr.setRequestHeader("x-twain-cloud-request-id", guid()); 

				xhr.onload = function () {
					deferred.resolve(this.response, this);
				}  
				xhr.send();  
			}catch(errAjax){
				deferred.reject('ajax error.');
			}
	} else {
		if(!auth_token)
			deferred.reject('unauthorized');
		else
			deferred.reject();
    }

    return deferred.promise();
  };
  
  TwainCloud.prototype.readImageBlockById = function (blockId) {
	    //scanners/{scannerId}/blocks/{blockId}

    var twain = this,
		deferred = $.Deferred(),
		auth_token = localStorage.getItem('authorization_token'),
		sessionId, privet_token;
	
    if (auth_token) {
		var imageBlockNum = 1;
		
		var urlBlock = dynamsoft.twainCloud.apiEndpoint + '/scanners/' + twain.id + '/blocks/' + blockId + '?t=' + guid();

		sendRequestToScan('GET', urlBlock, '', null)
		.then(function (data) {
			deferred.resolve(data);
		})
		.fail(function (error) {
			deferred.reject(error);
		});
	} else {
		if(!auth_token)
			deferred.reject('unauthorized');
		else
			deferred.reject();
    }

    return deferred.promise();
  };
  
  
  TwainCloud.prototype.releaseImageBlocks = function (a_lImageBlockNum, a_lLastImageBlockNum) {
	// {"kind":"twainlocalscanner","commandId":"xxxx",
	//    "method":"releaseImageBlocks",
	//    "params":{"sessionId":"xxxx","imageBlockNum":1,"lastImageBlockNum":1}
	// }

    var twain = this,
		deferred_releaseImageBlocks = $.Deferred(),
		auth_token = localStorage.getItem('authorization_token'),
		lImageBlockNum = a_lImageBlockNum, lLastImageBlockNum = a_lLastImageBlockNum,
		sessionId, privet_token;
		
	if(!lImageBlockNum)
		lImageBlockNum = 1;
	if(!lLastImageBlockNum)
		lLastImageBlockNum = 1;
	
	{
		sessionId = twain.sessionId;
		privet_token = twain.privet_token;
	}
		
    if (auth_token && privet_token && sessionId) {

		var sessionEndpoint = dynamsoft.twainCloud.apiEndpoint + '/scanners/' + twain.id + '/twaindirect/session',
			_params = {
				'sessionId': sessionId,
				'imageBlockNum':lImageBlockNum,
				'lastImageBlockNum':lLastImageBlockNum
			};
					
		sendRequestToScan('POST', sessionEndpoint, privet_token, JSON.stringify({
			  
			'kind': 'twainlocalscanner',
			'commandId': guid(),
			'method': 'releaseImageBlocks',
			'params': _params
			
		}))
		.then(function (strBody) {
			
			console.log(strBody);
			var oBody;
			try {
				oBody = JSON.parse(strBody);
			} catch(e1) {
				
			}
			
			if(oBody && oBody.results && oBody.results.success) {
				var revision = oBody.results.session.revision;
				var curState = oBody.results.state;
				
				log('[SESSION] releaseImageBlocks - session revision: ' + revision);
				twain.revision = revision;
				twain['status'] = curState;
				
				if(curState === 'closed') {
					dynamsoft.twainCloud.scannerMap[twain.id] = false;
				}
				
				deferred_releaseImageBlocks.resolve();
				
			} else {
				
				deferred_releaseImageBlocks.reject(strBody);
				
			}
		})
		.fail(function (error) {
			deferred_releaseImageBlocks.reject(error);
			
		});
	} else {
		if(!auth_token)
			deferred_releaseImageBlocks.reject('unauthorized');
		else if(!privet_token)
			deferred_releaseImageBlocks.reject('no x-privet-token');
		else if(!sessionId)
			deferred_releaseImageBlocks.reject('no sessionId');
		else
			deferred_releaseImageBlocks.reject();
		
    }

    return deferred_releaseImageBlocks.promise();
  };
  
  
  TwainCloud.prototype.closeSession = function (twain, bForceClose) {
	  
    var twain = this, 
		deferred_close = $.Deferred(),
		auth_token = localStorage.getItem('authorization_token'),
		sessionId, privet_token;
	
	{
		sessionId = twain.sessionId;
		privet_token = twain.privet_token;
	}
		
    if (auth_token && privet_token && sessionId) {
		
		var stat = twain['status'],
			blockNum = twain['blockNums'],
			funFailed = function(error) {
				deferred_close.reject(error);
			  };
			
		if(stat === 'capturing' || stat === 'draining') {
			
			twain._closeSession(twain).then(function() {
				twain.releaseImageBlocks(1, 1).then(function() {
					twain['status'] = 'noSession';
					deferred_close.resolve();
				 })
				 .fail(funFailed);
					 
			 })
			 .fail(funFailed);

		} else if(stat === 'closed') {
			twain.releaseImageBlocks().then(function() {
					twain['status'] = 'noSession';
					deferred_close.resolve();
				 })
				 .fail(funFailed);
		} else if(stat === 'ready') {
			twain._closeSession(twain).then(function() {
					twain['status'] = 'noSession';
					deferred_close.resolve();
				 })
				 .fail(funFailed);
		} else if(stat === 'noSession') {
			//none
			twain['status'] = 'noSession';
			deferred_close.resolve();
		} else {
			console.log('close with unknown state...');
			twain._closeSession(twain).then(function () {
				twain.releaseImageBlocks(1, 1).then(function () {
					twain['status'] = 'noSession';
					deferred_close.resolve();
				}).fail(funFailed);

			}).fail(funFailed);
		}
	} else {
		if(!auth_token)
			deferred_close.reject('unauthorized');
		else if(!privet_token)
			deferred_close.reject('no x-privet-token');
		else if(!sessionId)
			deferred_close.reject('no sessionId');
		else
			deferred_close.reject();
    }

    return deferred_close.promise();
	
  }
  
  TwainCloud.prototype._closeSession = function () {
	// {"kind":"twainlocalscanner","commandId":"xxxx",
	//    "method":"closeSession",
	//    "params":{"sessionId":"xxxx"}
	// }

    var twain = this,
		deferred_closeSession = $.Deferred(),
		auth_token = localStorage.getItem('authorization_token'),
		sessionId, privet_token;
	
	{
		sessionId = twain.sessionId;
		privet_token = twain.privet_token;
	}
		
    if (auth_token && privet_token && sessionId) {

		var sessionEndpoint = dynamsoft.twainCloud.apiEndpoint + '/scanners/' + twain.id + '/twaindirect/session',
			_params = {
				'sessionId': sessionId
			};
			
		sendRequestToScan('POST', sessionEndpoint, privet_token, JSON.stringify({
			  
			'kind': 'twainlocalscanner',
			'commandId': guid(),
			'method': 'closeSession',
			'params': _params
			
		}))
		.then(function (strBody) {
			
			console.log(strBody);
			var oBody;
			try {
				oBody = JSON.parse(strBody);
			} catch(e1) {
				
			}
			
			if(oBody && oBody.results && oBody.results.success) {
				var revision = oBody.results.session.revision;
				var curState = oBody.results.state;
				
				log('[SESSION] closeSession - session revision: ' + revision);
				twain.revision = revision;
				twain['status'] = curState;
				
				if(curState === 'noSession') {
					dynamsoft.twainCloud.scannerMap[twain.id] = false;
				}

				deferred_closeSession.resolve();
				
			} else {
				deferred_closeSession.reject(strBody);
				
			}
		})
		.fail(function (error) {
			deferred_closeSession.reject(error);
			
		});
	} else {
		if(!auth_token)
			deferred_closeSession.reject('unauthorized');
		else if(!privet_token)
			deferred_closeSession.reject('no x-privet-token');
		else if(!sessionId)
			deferred_closeSession.reject('no sessionId');
		else
			deferred_closeSession.reject();
		
    }

    return deferred_closeSession.promise();
  };
  
  TwainCloud.prototype.deleteScanner = function deleteScanner() {

	
    var twain = this,
		deferred = $.Deferred(),
		auth_token = localStorage.getItem('authorization_token');
		
    if (auth_token) {
		var deleteScannerEndpoint = dynamsoft.twainCloud.apiEndpoint + '/scanners/' + twain.id;
		
		sendRequest('DELETE', deleteScannerEndpoint, auth_token)
        .done(function (data) {
			twain.sessionId = null;
			deferred.resolve(data);
        })
        .fail(function (error) {
			deferred.reject(error);
        });
		
    } else {

		deferred.reject('unauthorized');
    }

    return deferred.promise();
  };


})();

  
  function twain_cloud_dwt_waitForEvents(twain) {
	// {"kind":"twainlocalscanner","commandId":"xxxx",
	//    "method":"waitForEvents",
	//    "params":{"sessionId":"xxxxx","sessionRevision":4}
	// }

    var auth_token = localStorage.getItem('authorization_token'),
		sessionId, privet_token;
	
	{
		sessionId = twain.sessionId;
		privet_token = twain.privet_token;
	}
		
    if (auth_token && privet_token && sessionId) {
		var revision = twain.revision;
		
		if(!revision)
			revision = 1;
		
		var sessionEndpoint = dynamsoft.twainCloud.apiEndpoint + '/scanners/' + twain.id + '/twaindirect/session',
			_params = {
				'sessionId': sessionId,
				'sessionRevision': revision
			};

		dynamsoft.twainCloud.sendRequestToScan('POST', sessionEndpoint, privet_token, JSON.stringify({
			  
			'kind': 'twainlocalscanner',
			'commandId': dynamsoft.twainCloud.guid(),
			'method': 'waitForEvents',
			'params': _params
			
		}))
		.then(function (strBody) {
			
			/// "events":[{"event":"timeout", sessionTimedOut
			///            "session":{"sessionId":"xxxx","revision":4,"state":"capturing",
			///            "status":{"success":true,"detected":"nominal"},"doneCapturing":false,"imageBlocksDrained":false,"imageBlocks":[]}}]}
			
			console.log(strBody);
			var oBody = false, bTimedOut = false, hasBlocks = false;
			
			if(strBody != null && strBody != '') {
				oBody = JSON.parse(strBody);
			}
			
			if(oBody && oBody.results && oBody.results.success) {
				var evts = oBody.results['events'];
				for(var i=0; i<evts.length; i++){
					console.log(evts[i]['event']);
					if(evts[i]['event'] == 'imageBlocks')
					{
						twain['blockNums'] = evts[i]['session'].imageBlocks.length;
						hasBlocks = true;
						break;
					} else if(evts[i]['event'] == 'sessionTimedOut' || evts[i]['event'] == 'timeout') {
						bTimedOut = true;
						break;
					}
				}
			}
			
			if (bTimedOut) {
				console.log('timed out --> close session.');
				// close session xxx
				twain.closeSession(twain, true).then(function () {
					console.log('--> session closed.');
				});
			} else if (hasBlocks) {

				console.log('get a image block event. start get image block.');

				if (typeof (twain.method_handlers['evt_imageBlocks']) === 'function') {
					twain.method_handlers['evt_imageBlocks'](twain['blockNums']);
				}
			} else {
				console.log('no events. start get event again.');
				setTimeout(function () {
					twain_cloud_dwt_waitForEvents(twain);
				}, 300);
			}
		})
		.fail(function (error) {
			console.log(error);
		});
	}
  };