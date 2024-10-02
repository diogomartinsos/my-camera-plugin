var exec = require("cordova/exec");
var PLUGIN_NAME = "MyCameraPlugin";

exports.openCamera = function (success, error, args) {
	exec(success, error, PLUGIN_NAME, "openCamera", []);
};
