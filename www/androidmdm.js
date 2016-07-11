/*global cordova, module*/

module.exports = {
    greet: function (name, successCallback, errorCallback) {
        console.log("androidmdm.js function start");
        console.log(arguments);

        cordova.exec(successCallback, errorCallback, "AndroidMDM", "greet", [name]);
        console.log("androidmdm.js function end");
    }
};
