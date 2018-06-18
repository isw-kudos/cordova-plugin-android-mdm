package au.net.isw.androidmdm;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.RestrictionEntry;
import android.content.RestrictionsManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class AndroidMDM extends CordovaPlugin{
    final Boolean DEBUG = BuildConfig.DEBUG;
    final String TAG = "AndroidMDMPlugin";

    CallbackContext restrictionChangeCallbacks;
    BroadcastReceiver restrictionsReceiver;

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        final String funcName = "execute";
        if(DEBUG)Log.v(TAG, funcName + " start ");
        if(DEBUG)Log.d(TAG, "execute with action: " + action);
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP){
          Log.w(TAG, "requires Android API level "+android.os.Build.VERSION_CODES.LOLLIPOP+", this device is running " + android.os.Build.VERSION.SDK_INT + ". Managed config not loaded.");
          callbackContext.error("");
          return false;
        }

        final List<String> actions = Arrays.asList("addRestrictionListener", "getAppRestrictions", "getManifestRestrictions");
        switch(actions.indexOf(action)){
            case 0:
                addRestrictionsListener(callbackContext);
                callbackContext.success();
                return true;
            case 1:
                callbackContext.success(getAppRestrictions());
                return true;
            case 2:
                callbackContext.success(getManifestRestrictions());
                return true;
            default:
                Log.e(TAG, "AndroidMDM.execute called with bad action");
                callbackContext.error("AndroidMDM.execute called with bad action");
                return false;
        }
    }

    private void addRestrictionsListener(CallbackContext callbackContext){
        if(DEBUG)Log.v(TAG, "addRestrictionsListener start");
        restrictionChangeCallbacks = callbackContext;
        restrictionsReceiver = new BroadcastReceiver() {
            @Override public void onReceive(Context context, Intent intent) {
                if(DEBUG)Log.d(TAG, "onReceive: " + intent.getAction());
                JSONObject restrictions = AndroidMDM.this.getAppRestrictions();
                AndroidMDM.this.restrictionChangeCallbacks.success(restrictions);
            }
        };

        //Register to receive broadcasts for restriction change.
        //This would only trigger if the admin pushed and the device received some configuration while the app is in use.
        //Might be hard to test.
        getContext().registerReceiver(restrictionsReceiver, new IntentFilter(Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED));
        if(DEBUG)Log.v(TAG, "addRestrictionsListener end");
    }
    //Check for changes to restrictions

    /**
     * Get app restrictions as set by Device Policy Controller(DPC)/Enterprise Mobility Management (EMM) provider
     * If a value is not specified by DCP/EMM, it will not appear in returned JSON
     *
     * Note:
     * The managed configurations Bundle contains one item for every configuration that has been *explicitly* set by a managed configurations provider.
     * However, you cannot assume that a configuration will be present in the bundle just because you defined a default value in the managed configurations XML file.
     */
    private JSONObject getAppRestrictions(){
        if(DEBUG)Log.v(TAG, "getAppRestrictions start");
        JSONObject rests = new JSONObject(); //JSON Object of the restrictions set. Just keys/values.

        try{
            RestrictionsManager restMgr = (RestrictionsManager) getActivity().getSystemService(Context.RESTRICTIONS_SERVICE);
            Bundle appRests = restMgr.getApplicationRestrictions();
            if(DEBUG)Log.d(TAG, "hasRestrictionsProvider: " + restMgr.hasRestrictionsProvider());
            if(DEBUG)Log.d(TAG, "AppRestrictions: " + appRests.toString());
            if(DEBUG){
                for (String key: appRests.keySet())
                {
                    Log.v (TAG, key + " is a key in the bundle");
                }
                Log.v (TAG, "done printing keys");
            }
            if(appRests.keySet().size() < 1) return rests;      //RETURN if no app restrictions
            if(DEBUG)Log.v(TAG, "appRests keyset size: " + appRests.keySet().size());
            JSONObject manRests = getManifestRestrictions();
            Iterator<String> manKeys = manRests.keys();
            if(DEBUG)Log.v(TAG, "manRests length: " + manRests.length());
            while(manKeys.hasNext()){
                String key = manKeys.next();
                if(DEBUG)Log.v(TAG, "key: " + key);
                if(appRests.containsKey(key)){
                    JSONObject manRest = manRests.getJSONObject(key);
                    if(DEBUG)Log.v(TAG, "manRest: " + manRest.toString());
                    if(DEBUG)Log.v(TAG, "type: " + manRest.getInt("type"));
                    switch(manRest.getInt("type")){
                        case RestrictionEntry.TYPE_BOOLEAN:
                            rests.put(key, appRests.getBoolean(key));
                            break;
                        case RestrictionEntry.TYPE_BUNDLE:break;//TODO
                        case RestrictionEntry.TYPE_BUNDLE_ARRAY:break;//TODO
                        case RestrictionEntry.TYPE_CHOICE:
                            rests.put(key, appRests.getStringArray(key));
                            break;
                        case RestrictionEntry.TYPE_INTEGER:
                            rests.put(key, appRests.getInt(key));
                            break;
                        case RestrictionEntry.TYPE_MULTI_SELECT:
                            rests.put(key, appRests.getStringArray(key));
                            break;
                        case RestrictionEntry.TYPE_NULL: //Same as string
                        case RestrictionEntry.TYPE_STRING:
                            rests.put(key, appRests.getString(key));
                            break;
                        default: throw new Exception("IMPOSSIBRU");
                    }
                }
            }
        }catch(Exception e){
            Log.e(TAG, e.getLocalizedMessage());
        }
        if(DEBUG)Log.v(TAG, "getAppRestrictions end return : " + rests.toString());
        return rests;
    }

    /**
     * Get restrictions from restrictions configuration set in AndroidManifest.
     * @return
     * @author mdonnellon
     */
    private JSONObject getManifestRestrictions(){
        final String funcName = "getManifestRestrictions";
        if(DEBUG)Log.v(TAG, funcName + " start");
        JSONObject restsObj = new JSONObject();
        try {
            RestrictionsManager restMgr = (RestrictionsManager) getActivity().getSystemService(Context.RESTRICTIONS_SERVICE);

            List<RestrictionEntry> manRests = restMgr.getManifestRestrictions(getContext().getPackageName());
            for (RestrictionEntry entry : manRests ) {
                JSONObject rest = restrictionEntryToJSON(entry);
                restsObj.put(rest.getString("key"), rest);
             }
        } catch (Exception e){
            Log.e(TAG, e.getLocalizedMessage());
        }
        if(DEBUG)Log.v(TAG, funcName + " end");
        return restsObj;
    }

    /**
     *  Process a RestrictionEntry object into JSON.
     *  Called recursively on bundle types.
     *
     * @param entry
     * @return JSON representation of the RestrictionEntry
     * @throws JSONException
     * @author mdonnellon
     */
    private JSONObject restrictionEntryToJSON(RestrictionEntry entry) throws JSONException{
        final String funcName = "restrictionEntryToJSON";
        if(DEBUG)Log.v(TAG, funcName + " start");
        JSONObject rest = new JSONObject();
        rest.put("key", entry.getKey());
        rest.put("title", entry.getTitle());
        rest.put("type", entry.getType());
        rest.put("description", entry.getDescription());

        switch (entry.getType()){
            case RestrictionEntry.TYPE_BOOLEAN:
                rest.put("defaultValue", entry.getSelectedState());
                break;
            case RestrictionEntry.TYPE_BUNDLE:break;//TODO
            case RestrictionEntry.TYPE_BUNDLE_ARRAY:break;//TODO
            case RestrictionEntry.TYPE_CHOICE:
                rest.put("defaultValue", entry.getChoiceEntries());
                //TODO: This doesn't look right. Is there choiceEntries and defaults?
                break;
            case RestrictionEntry.TYPE_INTEGER:
                rest.put("defaultValue", entry.getIntValue());
                break;
            case RestrictionEntry.TYPE_MULTI_SELECT:
                //TODO: This doesn't look right.
                //Should it have available options and defaults?
                rest.put("defaultValue", entry.getAllSelectedStrings());
                break;
            case RestrictionEntry.TYPE_NULL: //Same as string
            case RestrictionEntry.TYPE_STRING:
                rest.put("defaultValue", entry.getSelectedString());
                break;
            default: rest.put("defaultValue", "error");
        }
        if(DEBUG)Log.v(TAG, funcName + " end");
        return rest;
    }

    public Context getContext(){
        return getActivity();
    }
    public Activity getActivity(){
        return this.cordova.getActivity();
    }

    /**
     * Called when the system is about to start resuming a previous activity.
     * Unregister for receiving the restrictions changed event. Reregisters in onResume.
     *
     * @param multitasking		Flag indicating if multitasking is turned on for app
     */
    @Override
    public void onPause(boolean multitasking) {
        if(DEBUG)Log.v(TAG, "onPause start"); //What if the Cordova developer doesn't want to unregister?
        if (restrictionChangeCallbacks != null && restrictionsReceiver != null ){
            getContext().unregisterReceiver(restrictionsReceiver);
        }
        if(DEBUG)Log.v(TAG, "onPause end");
    }

    /**
     * Called when the activity will start interacting with the user.
     * Reregister to receive restriction changed events
     *
     * @param multitasking		Flag indicating if multitasking is turned on for app
     */
    @Override
    public void onResume(boolean multitasking) {
        if(DEBUG)Log.v(TAG, "onPause start");
        if (restrictionChangeCallbacks != null && restrictionsReceiver != null ){
            getContext().registerReceiver(restrictionsReceiver, new IntentFilter(Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED));
        }
        if(DEBUG)Log.v(TAG, "onPause end");
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
