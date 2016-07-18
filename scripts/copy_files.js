#!/usr/bin/env node

// Slightly modified verion of the "Copy Icons and Splashscreens" script posted by Dan Moore
// http://devgirl.org/2013/11/12/three-hooks-your-cordovaphonegap-project-needs/
// Updated to run on Cordova 6.2

//
// This hook copies various resource files
// from our version control system directories
// into the appropriate platform specific location
//


// configure all the files to copy.
// Key of object is the source file,
// value is the destination location.
// Paths are relative to project root.
// It's fine to put all platforms' icons
// and splash screen files here, even if
// we don't build for all platforms
// on each developer's box.
module.exports = function(context){
  var filestocopy = [{
      "resources/android/res/xml/app_restrictions.xml" : "platforms/android/res/xml/app_restrictions.xml"
  } ];

  // no need to configure below
  var fs = require('fs');
  var path = require('path');
  var rootdir = context.opts.projectRoot;

  filestocopy.forEach(function(obj) {
      Object.keys(obj).forEach(function(key) {
          var val = obj[key];
          var srcfile = path.join(rootdir, key);
          var destfile = path.join(rootdir, val);
          console.log("copying "+srcfile+" to "+destfile);
          var destdir = path.dirname(destfile);
          if (fs.existsSync(srcfile) && fs.existsSync(destdir)) {
              fs.createReadStream(srcfile).pipe(
                 fs.createWriteStream(destfile));
          }
      });
  });
};
