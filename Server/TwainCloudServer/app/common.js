'use strict';


function getPathFromUrl(url) {
	return url.split(/[?#]/)[0];
}

function getQueryParams(qs) {
	qs = qs.split('+').join(' ');
	var params = {},
		tokens,
		re = /[?&]?([^=]+)=([^&]*)/g;

	while (tokens = re.exec(qs)) {
		params[decodeURIComponent(tokens[1])] = decodeURIComponent(tokens[2]);
	}
	return params;
}

function isAuthorized() {
	return (getAuthToken() && getRefreshToken());
}

function getAuthToken() {
	return localStorage.getItem('authorization_token');
}

function getRefreshToken() {
	return localStorage.getItem('refresh_token');
}

function logout() {
	dynamsoft.twainCloud.clearAuthTokens();
	window.location.href = getPathFromUrl(window.location.href);
}

function processQueryAuth(callback, errorCallback) {
	var success = false;
	var query = getQueryParams(document.location.search);
	if (query.error){
		log(query.error);
		dynamsoft.twainCloud.clearAuthTokens();
	} else {
		var aToken = getAuthToken();
		var rToken = getRefreshToken();

		if (aToken && rToken) {
			dynamsoft.twainCloud.saveAuthTokens(aToken, rToken);
			window.history.replaceState({authorization_token: ''}, '');
			success = true;
		} else {
			dynamsoft.twainCloud.clearAuthTokens();
		}
	}

	if (success && callback) {
		callback();
	} else if (errorCallback) {
		errorCallback();
	}
}