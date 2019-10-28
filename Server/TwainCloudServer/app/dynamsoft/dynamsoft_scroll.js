function dynamsoft_addEvt(target,type,handler){
    if(target.addEventListener){
        target.addEventListener(type,handler,false);
    }else{
        target.attachEvent('on'+type,function(event){
            return handler.call(target,event);
        });
    }
}

function dynamsoft_scrollbar(ele, isHorizontal, max, funResult){
    var _this=this;
	
	_this.x0 = 0;
	_this.y0 = 0;
	_this.x1 = 0;
	_this.y1 = 0;
	_this.isMoving = false;
	_this.radio = 1.0;
	_this.max = max;

	var mouseDownHorizontalHandler = function(e){
			e = e || event;
			_this.x0 = ele.offsetLeft;
			_this.y0 = ele.offsetTop;
			_this.x1 = e.clientX;
			_this.y1 = e.clientY;
			_this.isMoving = true;
			_this.minslideSize = 18;
			
			var diff;
			diff = (ele.parentNode.offsetWidth - ele.offsetWidth);
			if(diff == 0)
				_this.ratio = 1.0;
			else
				_this.ratio = _this.max/diff;
		},
		
		mouseMoveHorizontalHandler = function(e){
			if(!_this.isMoving){
				return;
			}
			e = e || event;
			
			var VAL_X = _this.x0 + (e.clientX - _this.x1);
			if(VAL_X < 0){VAL_X = 0;}
			var XMax = parseInt(ele.parentNode.clientWidth) - ele.offsetWidth;
			if(VAL_X > XMax){VAL_X = XMax;}
			ele.style.left = VAL_X + 'px';
		},
		
		mouseUpHorizontalHandler = function(e){
			
			if(_this.isMoving && funResult) {
				funResult(Math.round(_this.ratio * parseInt(ele.style.left)));
			}
			_this.isMoving = false;
			
			if(ele.releaseCapture){
				ele.releaseCapture();
			}   
		},
		
		mouseDownVerticalHandler = function(e){
			e = e || event;
			_this.x0 = ele.offsetLeft;
			_this.y0 = ele.offsetTop;
			_this.x1 = e.clientX;
			_this.y1 = e.clientY;
			_this.isMoving = true;
			_this.minslideSize = 18;
			
			var diff;
			// var disY = e.clientY - ele.offsetTop;
			diff = (ele.parentNode.offsetHeight - ele.offsetHeight);
			if(diff == 0)
				_this.ratio = 1.0;
			else
				_this.ratio = _this.max/diff;
		},
		
		mouseMoveVerticalHandler = function(e){
			if(!_this.isMoving){
				return;
			}
			e = e || event;
			
			var VAL_Y = _this.y0 + (e.clientY - _this.y1);
			if(VAL_Y < 0){VAL_Y = 0;}
			var YMax = parseInt(ele.parentNode.clientHeight) - ele.offsetHeight;
			if(VAL_Y > YMax){VAL_Y = YMax;}
			ele.style.top = VAL_Y + 'px';
		},
		
		mouseUpVerticalHandler = function(e){
			
			if(_this.isMoving && funResult) {

				funResult(Math.round(_this.ratio * parseInt(ele.style.top)));

			}
			_this.isMoving = false;
			
			if(ele.releaseCapture){
				ele.releaseCapture();
			}   
		},
		
		preventDefaultHandler = function(e){
			e = e || event;
			if(e.preventDefault){
				e.preventDefault();
			}else{
				e.returnValue = false;
			}
			
			if(ele.setCapture){
				ele.setCapture();
			}
		};
		
	if(isHorizontal) {
		dynamsoft_addEvt(ele,'mousedown',mouseDownHorizontalHandler);
		dynamsoft_addEvt(document,'mousemove',mouseMoveHorizontalHandler);
		dynamsoft_addEvt(document,'mouseup',mouseUpHorizontalHandler);
	} else {
		dynamsoft_addEvt(ele,'mousedown',mouseDownVerticalHandler);
		dynamsoft_addEvt(document,'mousemove',mouseMoveVerticalHandler);
		dynamsoft_addEvt(document,'mouseup',mouseUpVerticalHandler);
	}
	
    dynamsoft_addEvt(ele,'mousedown',preventDefaultHandler);
};
