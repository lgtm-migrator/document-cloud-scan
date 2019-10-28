// --------------- task json result functionality ---------------



// https://stackoverflow.com/questions/9975707/use-jquery-select-to-select-contents-of-a-div
jQuery.fn.selectText = function () {
    this.find('input').each(function () {
        if ($(this).prev().length == 0 || !$(this).prev().hasClass('p_copy')) {
            $('<p class="p_copy" style="position: absolute; z-index: -1;"></p>').insertBefore($(this));
        }
        $(this).prev().html($(this).val());
    });
    var doc = document;
    var element = this[0];
    var range;
    if (doc.body.createTextRange) {
        range = document.body.createTextRange();
        range.moveToElementText(element);
        range.select();
    } else if (window.getSelection) {
        var selection = window.getSelection();
        range = document.createRange();
        range.selectNodeContents(element);
        selection.removeAllRanges();
        selection.addRange(range);
    }
};

$(document).on('dblclick', '#preTaskJson, #preTaskJsonMobile', function () {
    $(this).selectText();
});


function handleArrayStringVal(val, aryAllowedVal) {
    // remove [ and ] if exist
    if (val.indexOf('[') == 0) val = val.substring(1);
    if (val.lastIndexOf(']') == val.length - 1) val = val.substring(0, val.length - 1);

    if (val.trim() == '') return '[]';

    var aryVal = val.split(',');
    var aryValidVal = [];

    for (var i = 0; i < aryVal.length; i++) {
        aryVal[i] = aryVal[i].trim();

        // remove ' or "
        if (aryVal[i].indexOf('"') == 0 || aryVal[i].indexOf("'") == 0) aryVal[i] = aryVal[i].substring(1);
        if (aryVal[i].lastIndexOf('"') == aryVal[i].length - 1 || aryVal[i].lastIndexOf("'") == aryVal[i].length - 1) aryVal[i] = aryVal[i].substring(0, aryVal[i].length - 1);

        var matchedVal = isArrayContainsSpecificVal(aryVal[i], aryAllowedVal);

        if (matchedVal !== null) aryValidVal.push(matchedVal);
    }

    if (!aryValidVal.length) return null;

    var strRest = '[';
    for (var i = 0; i < aryValidVal.length; i++) {
        strRest += "'" + aryValidVal[i] + "', ";
    }

    if (strRest.lastIndexOf(", ") == strRest.length - 2) strRest = strRest.substring(0, strRest.length - 2);

    return strRest + ']';
}

function handleNumberVal(val, iMin, iMax) {
    var curVal;

    try {
        curVal = Math.round(val);
    } catch (e) {
        curVal = NaN;
    }

    if (isNaN(curVal)) return null;

    if (curVal < iMin) return null;

    if (iMax === undefined) return curVal;

    if (curVal > iMax) return null;

    return curVal;
}

function handlerStringVal(val, aryAllowedVal) {
    if (val.indexOf('"') == 0 || val.indexOf("'") == 0) val = val.substring(1);
    if (val.lastIndexOf('"') == val.length - 1 || val.lastIndexOf("'") == val.length - 1) val = val.substring(0, val.length - 1);

    var matchedVal = isArrayContainsSpecificVal(val, aryAllowedVal);

    if (matchedVal !== null) return matchedVal;

    return null;
}

function emptySelection() {
    if (window.getSelection) {
        if (window.getSelection().empty) {  // Chrome
            window.getSelection().empty();
        } else if (window.getSelection().removeAllRanges) {  // Firefox
            window.getSelection().removeAllRanges();
        }
    } else if (document.selection) {  // IE
        document.selection.empty();
    }
}