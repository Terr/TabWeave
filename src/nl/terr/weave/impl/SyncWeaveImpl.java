package nl.terr.weave.impl;

import info.elebescond.weave.exception.WeaveException;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.NoSuchPaddingException;

import nl.sanderborgman.http.HttpRequest;
import nl.terr.weave.CryptoWeave;
import nl.terr.weave.SyncWeave;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import biz.source_code.base64Coder.Base64Coder;

public class SyncWeaveImpl implements SyncWeave {

    private String sServerUrl;
    private String sUsername;
    private String sPassword;
    private String sPassphrase;

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

    public SyncWeaveImpl(String sServerUrl, String sUsername, String sPassword, String sPassphrase) {
        this.sServerUrl = sServerUrl;
        this.sUsername  = sUsername;
        this.sPassword  = sPassword;
        this.sPassphrase    = sPassphrase;
    }

    public String getSyncApiUrl() {
        return this.sServerUrl + VERSION + "/" + this.sUsername;
    }

    @Override
    public void setServerUrl(String sServerUrl) {
        this.sServerUrl = sServerUrl;
    }

    @Override
    public JSONArray getCollection(String sCollection)
        throws HttpResponseException, ClientProtocolException, IOException, JSONException
         {

        DefaultHttpClient client    = new DefaultHttpClient();
        String sUrl                 = getSyncApiUrl() + "/storage/" + sCollection;
        String response             = "";
        JSONArray jsonArray         = new JSONArray();

        response   = HttpRequest.get(client, sUrl, this.sUsername, this.sPassword);

        jsonArray   = new JSONArray(response);

        Log.d("getCollection", response);

        client.getConnectionManager().shutdown();

        return jsonArray;
    }

    public JSONObject getItem(String sCollection, String sId)
        throws WeaveException, HttpResponseException, ClientProtocolException, IOException, JSONException {

        DefaultHttpClient client    = new DefaultHttpClient();
        String sUrl                 = getSyncApiUrl() + "/storage/" + sCollection + "/" + sId;
        String response             = "";
        JSONObject jsonObject       = new JSONObject();

        response   = HttpRequest.get(client, sUrl, this.sUsername, this.sPassword);

        jsonObject   = new JSONObject(response);

        Log.d("getItem", response);


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
    
    @Override
    public void setPassphrase(String sPassphrase) {
        this.sPassphrase    = sPassphrase;
    }

    @Override
    public byte[] getPrivateKey()
        throws WeaveException, JSONException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException, IOException {
        
        JSONObject oPrivKey     = this.getItem("keys", "privkey");
        JSONObject oPrivPayload = new JSONObject(oPrivKey.getString("payload"));

        byte[] bytePrivateSalt  = Base64Coder.decode(oPrivPayload.getString("salt"));
        byte[] bytePrivateIV    = Base64Coder.decode(oPrivPayload.getString("iv"));
        byte[] bytePrivateKey   = Base64Coder.decode(oPrivPayload.getString("keyData"));

        CryptoWeave mCryptoWeave    = new CryptoWeaveImpl();
        
        // This is a very long process on slow phones (and the emulator)
        byte[] bytePrivateKeyDecrypted = mCryptoWeave.decryptPrivateKey(sPassphrase, bytePrivateSalt, bytePrivateIV, bytePrivateKey);
        
        return bytePrivateKeyDecrypted;
    }

    @Override
    public byte[] getSymmetricKey(byte[] bytePrivateKeyDecrypted)
        throws WeaveException, JSONException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException, IOException, InvalidKeySpecException { 
        
        JSONObject oCryptoTabs          = this.getItem("crypto", "tabs");
        JSONObject oCryptoTabsPayload   = new JSONObject(oCryptoTabs.getString("payload"));
        JSONArray oCryptoTabsKeyring    = new JSONObject(oCryptoTabsPayload.getString("keyring")).toJSONArray(oCryptoTabsPayload.getJSONObject("keyring").names());

        byte[] byteSymmetricKey         = Base64Coder.decode(new JSONObject(oCryptoTabsKeyring.getString(0)).getString("wrapped"));
        
        CryptoWeave mCryptoWeave        = new CryptoWeaveImpl();
        
        byte[] byteSymmetricKeyDecrypted = mCryptoWeave.decryptSymmetricKey(bytePrivateKeyDecrypted, byteSymmetricKey);
        
        return byteSymmetricKeyDecrypted;
    }
}