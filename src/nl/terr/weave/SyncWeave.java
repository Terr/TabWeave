package nl.terr.weave;

import info.elebescond.weave.exception.WeaveException;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.NoSuchPaddingException;

import org.json.JSONArray;
import org.json.JSONException;
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

    public void setServerUrl(String sServerUrl);
    
    public void setPassphrase(String sPassphrase);
    
    public byte[] getPrivateKey()
        throws WeaveException, JSONException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException, IOException;
    
    public byte[] getSymmetricKey(byte[] bytePrivateKeyDecrypted)
        throws WeaveException, JSONException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException, IOException, InvalidKeySpecException;
}