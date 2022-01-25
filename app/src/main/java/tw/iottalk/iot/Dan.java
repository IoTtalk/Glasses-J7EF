package tw.iottalk.iot;


import static java.util.UUID.randomUUID;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InterruptedIOException;

public class Dan extends Thread{
    private String endpoint;
    private String id;
    private String state = "SUSPEND";
//    private String state = "RESUME";
    private JSONObject profile;
    private String dan_log_tag = "DAN";

    public Dan(String endpoint, String d_id, JSONObject profile){
        this.endpoint = endpoint;
        if (d_id.isEmpty()){
            this.id = rand_id();
        }else{
            this.id = d_id;
        }
        this.profile = profile;
    }

    private void ControlChannel(){
        //TODO: implement control channel
    }

    public void register_device(){
        try {
            Log.d(dan_log_tag, "This device is registering.");
            Csmapi.register(endpoint, id, profile);
            Log.d(dan_log_tag, "This device has successfully registered.");
        } catch (Csmapi.CsmError csmError) {
            csmError.printStackTrace();
        }

        //TODO: implement control channel
    }

    public boolean deregister(){
        try {
            Csmapi.deregister(endpoint, id);
            Log.d(dan_log_tag, "This device has successfully deregistered.");
            return true;
        } catch (InterruptedIOException | Csmapi.CsmError e) {
            e.printStackTrace();
        }

        return false;
    }

    public JSONArray pull(String df_name){
        if (state.equals("RESUME")) {
            try {
                JSONArray data = Csmapi.pull(endpoint, id, df_name);
                if(data != null){
                    return data.getJSONArray(0).getJSONArray(1);
                }
            } catch (Csmapi.CsmError | JSONException csmError) {
                csmError.printStackTrace();
            }
        }
        else
            return null;
        return null;
    }

    public boolean push(String df_name, JSONArray data){
        try {
            return Csmapi.push(endpoint, id, df_name, data);
        } catch (Csmapi.CsmError csmError) {
            csmError.printStackTrace();
        }
        return false;
    }

    private String rand_id(){
        return randomUUID().toString();
    }


}
