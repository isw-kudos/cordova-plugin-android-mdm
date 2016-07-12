package au.net.isw.androidmdm;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import android.util.Log;

public class AndroidMDM extends CordovaPlugin {
    final String TAG = "AndroidMDMPlugin";

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, "execute() with action: " + action);
        if (action.equals("greet")) {
            Log.d(TAG, " if with action " + action);
            String name = data.getString(0);
            String message = "Hello, " + name;
            callbackContext.success(message);

            return true;

        } else {
            Log.d(TAG, "else with action " + action);
            callbackContext.success("action not matched");
            return false;

        }
    }
}
