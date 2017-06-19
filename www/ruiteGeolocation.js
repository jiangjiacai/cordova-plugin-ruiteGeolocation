var exec = require('cordova/exec');

var myLocationFunc = function () {
}

myLocationFunc.prototype.getCurrentPosition = function (success, error, arg0) {
    exec(success, error, "ruiteGeolocation", "getCurrentPosition", [arg0]);
}

exports.coolMethod = function (arg0, success, error) {
    exec(success, error, "ruiteGeolocation", "coolMethod", [arg0]);
};

var FUNC = new myLocationFunc();
module.exports = FUNC;