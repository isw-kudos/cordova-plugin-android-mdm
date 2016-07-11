package au.net.isw.androidmdm;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import android.util.Log;

public class AndroidMDM extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        Log.e("AndroidMDM", "log from androidmdm");
        if (action.equals("greet")) {
            Log.e("AndroidMDM", " if with action " + action);
            String name = data.getString(0);
            String message = "Hello, " + name;
            callbackContext.success(message);

            return true;

        } else {
            Log.e("AndroidMDM", "else with action " + action);
            callbackContext.success("action not matched");
            return false;

        }
    }
}
