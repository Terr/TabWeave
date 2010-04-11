package nl.terr.weave.impl;

import nl.sanderborgman.http.HttpRequest;
import nl.terr.weave.SyncWeave;
import nl.terr.weave.exception.WeaveException;

import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class SyncWeaveImpl implements SyncWeave {
    
    private String sServerUrl;
    private String sSecret;
    private String sUsername;
    private String sPassword;
    
    public SyncWeaveImpl() {
    }
    
    public SyncWeaveImpl(String sServerUrl) {
        this.sServerUrl = sServerUrl;
    }
    
    public SyncWeaveImpl(String sServerUrl, String sUsername, String sPassword) {
        this.sServerUrl = sServerUrl;
        this.sUsername  = sUsername;
        this.sPassword  = sPassword;
    }
    
    public SyncWeaveImpl(String sServerUrl, String sUsername, String sPassword, String sSecret) {
        this.sServerUrl = sServerUrl;
        this.sUsername  = sUsername;
        this.sPassword  = sPassword;
        this.sSecret    = sSecret;
    }
    
    public String getSyncApiUrl() {
        return this.sServerUrl + VERSION + "/" + this.sUsername;
    }

    @Override
    public void setSecret(String sSecret) {
        this.sSecret    = sSecret;
    }

    @Override
    public void setServerUrl(String sServerUrl) {
        this.sServerUrl = sServerUrl;
    }

    @Override
    public JSONArray getCollection(String sCollection) 
        throws WeaveException {
        
        DefaultHttpClient client    = new DefaultHttpClient();
        String sUrl                 = getSyncApiUrl() + "/storage/" + sCollection;
        String response             = "";
        JSONArray jsonArray         = new JSONArray();
        
        try {
            response   = HttpRequest.get(client, sUrl, this.sUsername, this.sPassword);
            
            jsonArray   = new JSONArray(response);
            
            Log.d("getCollection", response);
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        client.getConnectionManager().shutdown();
        
        return jsonArray;
    }
    
    public JSONObject getItem(String sCollection, String sId)
        throws WeaveException {
        
        DefaultHttpClient client    = new DefaultHttpClient();
        String sUrl                 = getSyncApiUrl() + "/storage/" + sCollection + "/" + sId;
        String response             = "";
        JSONObject jsonObject       = new JSONObject();
        
        try {
            response   = HttpRequest.get(client, sUrl, this.sUsername, this.sPassword);
            
            jsonObject   = new JSONObject(response);
            
            Log.d("getItem", response);
            
        } catch (JSONException e) {
            Log.d("getItem", "Response not in JSON Object format");
            
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        client.getConnectionManager().shutdown();
        
        return jsonObject;
    }

    @Override
    public void setUsername(String sUsername) {
        this.sUsername  = sUsername;
    }

    @Override
    public void setPassword(String sPassword) {
        this.sPassword  = sPassword;
    }
}