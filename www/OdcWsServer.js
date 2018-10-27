var exec = require('cordova/exec');

exports.startServer = function (ip, port, success, error) {
    exec(success, error, "OdcWsServer", "startServer", [ip, port]);
};

exports.sendColor = function (color, success, error) {
    exec(success, error, "OdcWsServer", "sendColor", [color]);
};