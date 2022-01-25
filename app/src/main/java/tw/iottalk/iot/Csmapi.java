package tw.iottalk.iot;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Csmapi {
//    private String ENDPOINT;
    private static String csmapi_log_tag = "csmapi";

//    public Csmapi(){
//
//    }

//    public Csmapi(String endpoint){
//        this.ENDPOINT = endpoint;
//    }
    public static class CsmError extends Exception {
        String msg;
        public CsmError (String message) {
            super(message);
        }
        public CsmError (String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static String register(String url, String id, JSONObject profile) throws CsmError {
        try {
            String _url = url + "/" + id;
            Log.d(csmapi_log_tag,String.format("register(): device url %s", _url));
            JSONObject _profile = new JSONObject();
            _profile.put("profile", profile);
            Http.Response res = Http.post(_url, _profile);
            if (res.status_code != 200) {
                Log.e(csmapi_log_tag,String.format("register(): Response from %s", _url));
                Log.e(csmapi_log_tag,String.format("register(): Response Code: %d", res.status_code));
                Log.e(csmapi_log_tag,String.format("register(): %s", res.body));
                throw new CsmError(res.body);
            }
            return res.body;
        } catch (JSONException | InterruptedIOException e) {
            e.printStackTrace();
            Log.e(csmapi_log_tag, String.format("csmapi register failed: %s", e));
        }

        return "";
    }

    public static boolean deregister(String url, String id) throws CsmError, InterruptedIOException {
        try {
            String _url = url +"/"+ id;
            Log.d(csmapi_log_tag,String.format("deregister(): device url %s", _url));
            Http.Response res = Http.delete(_url);
            if (res.status_code != 200) {
                Log.e(csmapi_log_tag,String.format("deregister(): Response from %s", _url));
                Log.e(csmapi_log_tag,String.format("deregister(): Response Code: %d", res.status_code));
                Log.e(csmapi_log_tag,String.format("deregister(): %s", res.body));
                throw new CsmError(res.body);
            }
            return true;
        } catch (NullPointerException e) {
            Log.e(csmapi_log_tag, String.format("csmapi deregister failed: %s", e));
        }
        return false;
    }

    public static boolean push(String url, String id, String df_name, JSONArray data) throws CsmError {
        try {
//            Log.d(csmapi_log_tag,String.format("%s pushing to %s",id,url));
            JSONObject obj = new JSONObject();
            obj.put("data", data);
            String _url = String.format("%s/%s/%s",url,id,df_name);
            Http.Response res = Http.put(_url, obj);
            if (res.status_code != 200) {
                Log.e(csmapi_log_tag,String.format("push(): Response from %s", _url));
                Log.e(csmapi_log_tag,String.format("push(): Response Code: %d", res.status_code));
                Log.e(csmapi_log_tag,String.format("push(): %s", res.body));
                throw new CsmError(res.body);
            }
            return true;
        } catch (NullPointerException | JSONException | InterruptedIOException e) {
            Log.e(csmapi_log_tag,String.format("csmapi push failed: %s", e));
        }
        return false;
    }

    public static JSONArray pull(String url, String id, String df_name) throws CsmError {
        try {
//            Log.d(csmapi_log_tag,String.format("%s pulling to %s",id,url));
            String _url = String.format("%s/%s/%s",url,id,df_name);
            Http.Response res = Http.get(_url);
            if (res.status_code != 200) {
                Log.e(csmapi_log_tag,String.format("pull(): Response from %s", _url));
                Log.e(csmapi_log_tag,String.format("pull(): Response Code: %d", res.status_code));
                Log.e(csmapi_log_tag,String.format("pull(): %s", res.body));
                throw new CsmError(res.body);
            }
            JSONObject tmp = new JSONObject(res.body);
            return tmp.getJSONArray("samples");

        } catch (NullPointerException | InterruptedIOException | JSONException e) {
            Log.e(csmapi_log_tag,String.format("csmapi pull failed: %s", e));
        }
        return null;
    }

    public JSONObject tree (String url) throws CsmError {
        try {
            String api = "/tree";
            String _url = String.format("%s%s",url,api);
            Http.Response res = Http.get(_url);
            if (res.status_code != 200) {
                Log.e(csmapi_log_tag,String.format("tree(): Response from %s", url));
                Log.e(csmapi_log_tag,String.format("tree(): Response Code: %d", res.status_code));
                Log.e(csmapi_log_tag,String.format("tree(): %s", res.body));
                throw new CsmError(res.body);
            }
            return new JSONObject(res.body);

        } catch (NullPointerException | JSONException | InterruptedIOException e) {
            Log.e(csmapi_log_tag,String.format("csmapi tree failed: %s", e));
        }
        return null;
    }

    static public boolean register(String mac_addr, JSONObject profile) throws JSONException, InterruptedIOException {
        try {
            String url = "http://5.iottalk.tw:9999" + "/" + mac_addr;
            Log.d("csmapi", "URL is " + url);
            JSONObject tmp = new JSONObject();
            tmp.put("profile", profile);
            Http.Response res = Http.post(url, tmp);
            if (res.status_code != 200) {
                Log.e("csmapi", "register code isn,t 200");
                Log.e("csmapi", "register(): Response from " + url);
                Log.e("csmapi", "register(): Response Code:" + res.status_code);
                Log.e("csmapi", "register(): Body" + res.body);
                //logging("register(): Response from %s", url);
                //logging("register(): Response Code: %d", res.status_code);
                //logging("register(): %s", res.body);
                //throw new CSMError(res.body);
            }
            return true;
        } catch (NullPointerException e) {
            //logging("pull(): %s", e);
            Log.e("csmapi", "pull():" + e);
        }
        return false;
    }


    static private class Http {
        static public class Response {
            public String body;
            public int status_code;
            public Response (String body, int status_code) {
                this.body = body;
                this.status_code = status_code;
            }
        }

        static public Response get (String url_str) throws InterruptedIOException {
            return request("GET", url_str, null);
        }

        static public Response post (String url_str, JSONObject post_body) throws InterruptedIOException {
            return request("POST", url_str, post_body.toString());
        }

        static public Response delete (String url_str) throws InterruptedIOException {
            return request("DELETE", url_str, null);
        }

        static public Response put (String url_str, JSONObject put_body) throws InterruptedIOException {
            return request("PUT", url_str,  put_body.toString());
        }

        static private Response request (String method, String url_str, String request_body) throws InterruptedIOException {
            try {
                URL url = new URL(url_str);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod(method);


                if (method.equals("POST") || method.equals("PUT")) {
                    connection.setDoOutput(true);	// needed, even if method had been set to POST
                    connection.setRequestProperty("Content-Type", "application/json");
                    OutputStream os = connection.getOutputStream();
                    os.write(request_body.getBytes());
                }

                int status_code = connection.getResponseCode();
                InputStream in;

                if(status_code >= HttpURLConnection.HTTP_BAD_REQUEST) {
                    in = new BufferedInputStream(connection.getErrorStream());
                } else {
                    in = new BufferedInputStream(connection.getInputStream());
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String body = "";
                String line;
                while ((line = reader.readLine()) != null) {
                    body += line + "\n";
                }
                connection.disconnect();
                reader.close();
                return new Response(body, status_code);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return new Response("MalformedURLException", 400);
            } catch (InterruptedIOException e) {
                e.printStackTrace();
                throw e;
            } catch (IOException e) {
                e.printStackTrace();
                return new Response("IOException", 400);
            }
        }
    }
}
