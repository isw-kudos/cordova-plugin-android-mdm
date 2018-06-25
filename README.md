# Cordova Plugin for Configuration from Android for Work

## How to Use
Create an XML file at `<project root>/resources/android/app_restrictions.xml` which describes the restrictions you want to make available in your app. See the XML definition [here](https://developer.android.com/work/managed-configurations.html).

NOTE: This plugin does not yet come with a way to create resource strings for the description attribute and use of a string resource for the description attribute is compulsory if used. So, you can not yet use the description attribute unless you figure out your own way of adding entries to `platform/android/res/xml/strings.xml` for you to reference.

`app_restrictions.xml` is automatically copied into the corresponding directory under platform after a Cordova prepare.
If you want to keep the file elsewhere, ensure `app_restrictions.xml` is copied to `platform/android/app/src/main/res/xml/` or at least exists before attempting to compile. `platform/android/app/src/main/res/xml/app_restrictions.xml` may be deleted when preparing.
This plugin will automatically add the required reference to `app_restrictions.xml` in `AndroidManifest.xml`.

In your Cordova JS you can use the below functions to get your application restrictions.

## Supported Restriction Types
String, Integer, Boolean, Null(Hidden String)

TODO: Bundle, Bundle Array, Choice, Multi Select


## Available functions:
### addRestrictionListener(function callback)
###### Not yet tested
Set a callback function to be called whenever the application receives the ACTION_APPLICATION_RESTRICTIONS_CHANGED intent.
This should be set in your onDeviceReady function or similar.
Listening for this intent is paused when onPause occurs and resumed when onResume occurs within native code.
The callback will receive the new restrictions as a parameter, identical to the getAppRestrictions response.

### getAppRestrictions(function callback)
Get a JSONObject of the keys/values for all of the restrictions that have been set the Device Policy Controller/EMM/MDM/Whatever.
This function should be used when your application starts and resumes to check for changes to restrictions. The restrictionListener callback will not be called when the application is paused.
Will return an empty object if the app has not had any restrictions set.
The JSONObject will be passed to the callback function as a parameter.

### getManifestRestrictions(function callback)
Get a JSONObject which describes each of the restriction entries from app_restrictions.xml, indexed by key.
Each restriction has key, title, type, description and defaultValue.

## Versions
Use version 0.0.1 for cordova-android < 7. The only change for 0.2.0 is the location of the AndroidManifest.xml and support for cordova-android 7 and up.

## References
[AppConfig Community](https://www.appconfig.org/android/)

[Set up Managed Configurations | Andoid Developers ](https://developer.android.com/work/managed-configurations.html)

[RestrictionsManager | Android Developers ](https://developer.android.com/reference/android/content/RestrictionsManager.html)
