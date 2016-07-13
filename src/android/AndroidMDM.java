package au.net.isw.androidmdm;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.RestrictionEntry;
import android.content.RestrictionsManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

public class AndroidMDM extends CordovaPlugin {
    final String TAG = "AndroidMDMPlugin";

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, "execute() with action: " + action);
        final List<String> actions = Arrays.asList("logRestrictions", "greet");
        switch(actions.indexOf(action)){
            case 0:
                callbackContext.success(logRestrictions());
                return true;
            case 1:
                String name = data.getString(0);
                String message = "Hello, " + name;
                callbackContext.success(message);
                return true;
            default:
                Log.e(TAG, "AndroidMDM.execute called with bad action");
                callbackContext.error("AndroidMDM.execute called with bad action");
                return false;
        }
    }

    private String logRestrictions(){
        final String funcName = "logRestrictions";
        Log.d(TAG, funcName + " start");
        String returnStr = "";
        RestrictionsManager restMgr = (RestrictionsManager) getActivity().getSystemService(Context.RESTRICTIONS_SERVICE);

        returnStr += "AppRestrictions: ";
        Bundle appRests = restMgr.getApplicationRestrictions();
        if(appRests!=null) returnStr += appRests.toString();
        else returnStr += "null";

        returnStr += "\nManifestRestrictions: ";
        List<RestrictionEntry> manRests = restMgr.getManifestRestrictions(getContext().getPackageName());
        if(manRests!=null) returnStr += manRests.toString();
        else returnStr += "null";

        returnStr += "\n\nhasRestrictionsProvider: " + restMgr.hasRestrictionsProvider();

        Log.d(TAG, returnStr);
        return returnStr;
    }

    public Context getContext(){
        return getActivity();
    }
    public Activity getActivity(){
        return this.cordova.getActivity();
    }

    /**
     * Called when the system is about to start resuming a previous activity.
     *
     * @param multitasking		Flag indicating if multitasking is turned on for app
     */
    @Override
    public void onPause(boolean multitasking) {
    }

    /**
     * Called when the activity will start interacting with the user.
     *
     * @param multitasking		Flag indicating if multitasking is turned on for app
     */
    @Override
    public void onResume(boolean multitasking) {
    }

    /**
     * Called when the activity is becoming visible to the user.
     */
    @Override
    public void onStart() {
    }

    /**
     * Called when the activity is no longer visible to the user.
     */
    @Override
    public void onStop() {
    }

    /**
     * Called when the activity receives a new intent.
     */
    @Override
    public void onNewIntent(Intent intent) {
    }

    /**
     * The final call you receive before your activity is destroyed.
     */
    @Override
    public void onDestroy() {
    }
}
