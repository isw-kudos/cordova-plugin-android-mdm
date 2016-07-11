/*global cordova, module*/

module.exports = {
    greet: function (name, successCallback, errorCallback) {
        console.log("androidmdm.js");
        cordova.exec(successCallback, errorCallback, "Hello", "greet", [name]);
    }
};
