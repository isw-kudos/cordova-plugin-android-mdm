package com.example.plugin;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class AndroidMDM extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        Log.d("AndroidMDM", "log from androidmdm");
        if (action.equals("greet")) {
            Log.d("AndroidMDM", " if with action " + action);
            String name = data.getString(0);
            String message = "Hello, " + name;
            callbackContext.success(message);

            return true;

        } else {
            Log.d("AndroidMDM", "else with action " + action);
            return false;

        }
    }
}
