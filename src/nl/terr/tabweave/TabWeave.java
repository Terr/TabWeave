package nl.terr.tabweave;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.NoSuchPaddingException;

import nl.terr.weave.CryptoWeave;
import nl.terr.weave.exception.WeaveException;
import nl.terr.weave.impl.CryptoWeaveImpl;
import nl.terr.weave.impl.SyncWeaveImpl;
import nl.terr.weave.impl.UserWeaveImpl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import biz.source_code.base64Coder.Base64Coder;

public class TabWeave extends ListActivity {
    
    private TabWeaveDbAdapter mWeaveTabsDbAdapter;
    private UserWeaveImpl mUserWeave;
    private SyncWeaveImpl mSyncWeave;
    private CryptoWeave mCryptoWeave;
    
    public static final String PREFS_NAME = "TabWeavePrefs";
    public static final String PREFS_PRIVATE_KEY    = "storageKeysPrivkey";
    public static final String PREFS_CRYPTO_TABS_SYMMETRIC_KEY    = "storageCryptoTabsSymmetricKey";
    
    public static final int MENU_SETTINGS = 1; 
    public static final int ACTIVITY_EDIT_SETTINGS = 1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // These 4 should become the user settings
        String sSyncServerUrl   = "";
        String sUsername    = "Terr";
        String sPassword    = "zOTr1jjV";
        String sPassphrase  = "JI2mOSobtztY01dR";
        
        byte[] bytePrivateKeyDecrypted;
        byte[] byteSymmetricKeyDecrypted;
        
        mWeaveTabsDbAdapter = new TabWeaveDbAdapter(this);
        mWeaveTabsDbAdapter.open();
        
        TabViewConfig mTabWeaveConfig = new TabViewConfig(this, PREFS_NAME);
        
        mUserWeave      = new UserWeaveImpl();
        mCryptoWeave    = new CryptoWeaveImpl();
        
        try {
            
            // Check the settings if the weaveNode is already known. If not, request it
            if(!mTabWeaveConfig.preferences.contains("weaveNode"))
            {
                mTabWeaveConfig.preferencesEdit.putString("weaveNode", mUserWeave.getUserStorageNode(sUsername, null));
                mTabWeaveConfig.preferencesEdit.commit();
            }

            sSyncServerUrl = mTabWeaveConfig.preferences.getString("weaveNode", null);
            mSyncWeave      = new SyncWeaveImpl(sSyncServerUrl, sUsername, sPassword);
            
            // Check settings storage for the user's private key
            if(!mTabWeaveConfig.preferences.contains(PREFS_PRIVATE_KEY) || mTabWeaveConfig.preferences.getString(PREFS_PRIVATE_KEY, "") == "")
            {
                JSONObject oPrivKey     = mSyncWeave.getItem("keys", "privkey");
                JSONObject oPrivPayload = new JSONObject(oPrivKey.getString("payload"));
                //JSONObject oPubKey      = mSyncWeave.getItem("keys", "pubkey");
                
                byte[] bytePrivateSalt     = Base64Coder.decode(oPrivPayload.getString("salt"));
                byte[] bytePrivateIV       = Base64Coder.decode(oPrivPayload.getString("iv"));
                byte[] bytePrivateKey   = Base64Coder.decode( oPrivPayload.getString("keyData"));
                
                // This is a very long process on slow phones (and the emulator)
                bytePrivateKeyDecrypted  = mCryptoWeave.decryptPrivateKey(sPassphrase, bytePrivateSalt, bytePrivateIV, bytePrivateKey);
                
                // Save the generated private key
                mTabWeaveConfig.preferencesEdit.putString(PREFS_PRIVATE_KEY, new String(Base64Coder.encode(bytePrivateKeyDecrypted)));
                
                mTabWeaveConfig.preferencesEdit.commit();
            }
            
            bytePrivateKeyDecrypted     = Base64Coder.decode(mTabWeaveConfig.preferences.getString(PREFS_PRIVATE_KEY, ""));
            
            if(!mTabWeaveConfig.preferences.contains(PREFS_CRYPTO_TABS_SYMMETRIC_KEY) || mTabWeaveConfig.preferences.getString(PREFS_CRYPTO_TABS_SYMMETRIC_KEY, "") == "")
            {
                JSONObject oCryptoTabs          = mSyncWeave.getItem("crypto", "tabs");
                JSONObject oCryptoTabsPayload   = new JSONObject(oCryptoTabs.getString("payload"));
                JSONArray oCryptoTabsKeyring    = new JSONObject(oCryptoTabsPayload.getString("keyring")).toJSONArray(oCryptoTabsPayload.getJSONObject("keyring").names());
                
                byte[] byteSymmetricKey         = Base64Coder.decode(new JSONObject(oCryptoTabsKeyring.getString(0)).getString("wrapped"));
                byteSymmetricKeyDecrypted    = mCryptoWeave.decryptSymmetricKey(bytePrivateKeyDecrypted, byteSymmetricKey);
                
                mTabWeaveConfig.preferencesEdit.putString(PREFS_CRYPTO_TABS_SYMMETRIC_KEY, new String(Base64Coder.encode(byteSymmetricKeyDecrypted)));
            
                mTabWeaveConfig.preferencesEdit.commit();
            }

            byteSymmetricKeyDecrypted   = Base64Coder.decode(mTabWeaveConfig.preferences.getString(PREFS_CRYPTO_TABS_SYMMETRIC_KEY, ""));

            // Request all tabs objects
            JSONArray aCollection   = mSyncWeave.getCollection("tabs?full=1");
            JSONObject[] oTabs      = new JSONObject[32];
            List<JSONObject> lTabs  = new ArrayList<JSONObject>();
            
            JSONObject oTabPayload;
            byte[] byteTabCipherText;
            String sTabCipherText;
            byte[] byteTabCipherIV;
            
            int iTabCount            = aCollection.length();
            if(iTabCount > oTabs.length)
            {
                iTabCount   = oTabs.length;
            }
            
            for(int x = 0; x < iTabCount; x++)
            {
                //oTabs[x]    = mSyncWeave.getItem("tabs", aCollection.getString(x)); // Only needed when not requesting ?full=1
                oTabs[x]         = aCollection.getJSONObject(x);
                
                // Decrypt the ciphertext of this tab
                oTabPayload     = new JSONObject(oTabs[x].getString("payload"));
                byteTabCipherText  = Base64Coder.decode(oTabPayload.getString("ciphertext"));
                byteTabCipherIV    = Base64Coder.decode(oTabPayload.getString("IV"));
                sTabCipherText  = new String(mCryptoWeave.decryptCipherText(byteSymmetricKeyDecrypted, byteTabCipherIV, byteTabCipherText));
                
                lTabs.add(x, new JSONObject(sTabCipherText));
            }
            
            fillData(lTabs);
        
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (WeaveException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void fillData(List<JSONObject> lTabs)
    {
        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{SyncWeaveImpl.KEY_TITLE, SyncWeaveImpl.KEY_URL, SyncWeaveImpl.KEY_ROWID};
        
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{ R.id.title, R.id.url};

        int iBrowserInstances   = lTabs.size();
        MatrixCursor matrixCursor   = new MatrixCursor(from);
        
        int iTabId  = 0;
        
        try {
            for(int iWeaveBrowserInstance = 0; iWeaveBrowserInstance < iBrowserInstances; iWeaveBrowserInstance++)
            {
                int iNumberTabs = lTabs.get(iWeaveBrowserInstance).getJSONArray("tabs").length();
                
                for(int iWeaveTab = 0; iWeaveTab < iNumberTabs; iWeaveTab++)
                {                   
                    matrixCursor.newRow()
                        .add(lTabs.get(iWeaveBrowserInstance).getJSONArray("tabs").getJSONObject(iWeaveTab).getString("title"))
                        .add(lTabs.get(iWeaveBrowserInstance).getJSONArray("tabs").getJSONObject(iWeaveTab).getJSONArray("urlHistory").getString(0))
                        .add(iTabId);
                
                    iTabId++;
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        ListAdapter listAdapter    = new SimpleCursorAdapter(
          this,                 // Context
          R.layout.tab_row,     // Specify the row template to use (here, two columns bound to the two retrieved cursor rows).
          matrixCursor,
          from,
          to
        );
        
        setListAdapter(listAdapter);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        TextView viewText   = (TextView)v.findViewById(R.id.url);
        
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(viewText.getText().toString()));  
        startActivity(viewIntent); 
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_SETTINGS,0, R.string.settings);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case MENU_SETTINGS:
            Intent i    = new Intent(this, TabWeaveSettingsActivity.class);
            startActivityForResult(i, ACTIVITY_EDIT_SETTINGS);
            return true;
        }
        
        return super.onMenuItemSelected(featureId, item);
    }
}