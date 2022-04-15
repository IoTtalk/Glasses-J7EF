package tw.iottalk.iot;


import static java.util.UUID.randomUUID;

import android.os.SystemClock;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InterruptedIOException;
import java.util.Objects;

public class Dan extends Thread{
    private String endpoint;
    private String id;
    private String state = "SUSPEND";   // Change to "RESUME", if you want auto rebind and push.
    private JSONObject profile;
    private String dan_log_tag = "DAN";
    private Thread ctrlChannelThread;

    public Dan(String endpoint, String d_id, JSONObject profile){
        this.endpoint = endpoint;
        if (d_id.isEmpty()) {
            this.id = rand_id();
        }else {
            this.id = d_id;
        }
        this.profile = profile;
    }

    private void ControlChannel() throws InterruptedException, JSONException {
        JSONArray ch;
        String controlChannelTimestamp = "";
        String cmd = "";
        while (true) {
            SystemClock.sleep(2000);
            try {
                ch = Csmapi.pull(endpoint, id, "__Ctl_O__");
                if (ch != null && ch.length() > 0) {
                    if (controlChannelTimestamp.equals(ch.getJSONArray(0).getString(0))) continue;

                    controlChannelTimestamp = ch.getJSONArray(0).getString(0);
                    cmd = ch.getJSONArray(0).getJSONArray(1).getString(0);
                    if (Objects.equals(cmd, "RESUME")) {
                        state = "RESUME";
                    } else if (Objects.equals(cmd, "SUSPEND")) {
                        state = "SUSPEND";
                    } else if (Objects.equals(cmd, "SET_DF_STATUS")) {
                        JSONArray data = new JSONArray();
                        JSONObject obj = new JSONObject().put("cmd_params",
                                ch.getJSONArray(0).getJSONArray(1).getJSONObject(1).getJSONArray("cmd_params"));
                        data.put(0,"SET_DF_STATUS_RSP");
                        data.put(1, obj);
                        Csmapi.push(endpoint, id, "__Ctl_I__",data);
                        // TODO: implement update profile.
                    }

                }
            } catch (Csmapi.CsmError | JSONException csmError) {
                csmError.printStackTrace();
            }
        }
    }

    public void register_device() {
        try {
            Log.d(dan_log_tag, "This device is registering.");
            Csmapi.register(endpoint, id, profile);
            Log.d(dan_log_tag, "This device has successfully registered.");
            if (ctrlChannelThread == null) {
                ctrlChannelThread = new Thread(() -> {
                    try {
                        ControlChannel();
                        Log.d(dan_log_tag, "Control Channel successfully running.");
                    } catch (InterruptedException | JSONException e) {
                        e.printStackTrace();
                    }
                });
                ctrlChannelThread.start();
            }
        } catch (Csmapi.CsmError csmError) {
            csmError.printStackTrace();
        }
    }

    public boolean deregister() {
        try {
            Csmapi.deregister(endpoint, id);
            Log.d(dan_log_tag, "This device has successfully deregistered.");
            return true;
        } catch (InterruptedIOException | Csmapi.CsmError e) {
            e.printStackTrace();
        }

        return false;
    }

    public JSONArray pull(String df_name) {
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

    public boolean push(String df_name, JSONArray data) {
        if (state.equals("RESUME")) {
            try {
                return Csmapi.push(endpoint, id, df_name, data);
            } catch (Csmapi.CsmError csmError) {
                csmError.printStackTrace();
            }
        }
        return false;
    }

    private String rand_id() {
        return randomUUID().toString();
    }


}
