
(function(dynam){
	
	"use strict";
	
    var lib = dynam.lib, 
		dwtEnv = {},
		detect = lib.detect,
		navInfo = dynam.navInfo;
	
	dynam.dwtEnv = dwtEnv;
	dwtEnv.navInfo = navInfo;

	lib.ready(function() {
		if(!dwtEnv.install)
			dwtEnv.install = {};

		lib.mix(dwtEnv.install, {
			_divInstallBody: 'dcs-InstallBody',
			_dlgInstall: false
		});
	});

	dwtEnv.ShowDialog = function (_dialogWidth, _dialogHeight, dlgNode) {

		var install = dwtEnv.install, msgContainer;
		if (!lib.get('dynamsoft_waiting')) {

			var p = document.createElement('div');
			p.className = 'dynamsoft-dialog-wrap';
			p.style.width = '100%';

			msgContainer =
				['<div id="dynamsoft_waiting" class="dynamsoft-dialog dynamsoft-dialog2">',
					'<div id="', install._divInstallBody, '" class="dynamsoft-dcs-dialog-body"></div></div>'];

			p.innerHTML = msgContainer.join('');
			document.body.appendChild(p);

			install._dlgInstall = p;
			lib.dialog.setup(install._dlgInstall);
		}


		var dlg = lib.one('.dynamsoft-dialog');
		if (dlg && dlg.getEL()) {
			dlg[0].style.width = _dialogWidth + 'px';

			if (_dialogHeight && _dialogHeight > 0) {
				dlg[0].style.height = _dialogHeight + 'px';
			}
		}

		if(dlgNode) {
			dlgNode.parentElement.remove(dlgNode);
			lib.get(install._divInstallBody).appendChild(dlgNode);
		}


		var wrapDiv = lib.get('dynamsoft_waiting');
		wrapDiv.style.width = _dialogWidth;

		if (install._dlgInstall.open) {
			install._dlgInstall.close();
		}

		install._dlgInstall.showModal();

	};

	dwtEnv.CloseDialog = function () {

		var dlgInstall = dwtEnv.install._dlgInstall;
		if (dlgInstall) {

			if (dlgInstall.open)
				dlgInstall.close();

			document.body.removeChild(dlgInstall);

			dwtEnv.install._dlgInstall = false;
		}
		
	};

})(dynamsoft);
