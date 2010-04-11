package nl.terr.weave;

import nl.terr.weave.exception.WeaveException;

import org.json.JSONArray;
import org.json.JSONObject;

public interface SyncWeave {
    
    public static String VERSION    = "1.0";
    public static String KEY_TITLE  = "Title";
    public static String KEY_URL    = "URL";
    public static String KEY_ROWID  = "_id";
    
    public JSONArray getCollection(String sCollection) 
        throws WeaveException;
    
    public JSONObject getItem(String sCollection, String sId)
        throws WeaveException;

    public void setUsername(String sUsername);
    
    public void setPassword(String sPassword);
    
    public void setSecret(String sSecret);

    public void setServerUrl(String sServerUrl);
}