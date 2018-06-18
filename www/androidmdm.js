/*global cordova, module, console*/
module.exports = {
    addRestrictionListener: function (callback) {
        "use strict";
        cordova.exec(callback, callback, "AndroidMDM", "addRestrictionListener", []);
    },

    getAppRestrictions: function (callback) {
        "use strict";
        cordova.exec(callback, callback, "AndroidMDM", "getAppRestrictions", []);
    },

    getManifestRestrictions: function (callback) {
        "use strict";
        cordova.exec(callback, callback, "AndroidMDM", "getManifestRestrictions", []);
    }
};
