package nl.terr.tabweave;

import info.elebescond.weave.exception.WeaveException;

import java.util.ArrayList;
import java.util.List;

import nl.terr.weave.CryptoWeave;
import nl.terr.weave.SyncWeave;
import nl.terr.weave.impl.CryptoWeaveImpl;
import nl.terr.weave.impl.SyncWeaveImpl;
import nl.terr.weave.impl.UserWeaveImpl;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import biz.source_code.base64Coder.Base64Coder;

public class TabWeave extends ListActivity {

//    private TabWeaveDbAdapter mWeaveTabsDbAdapter;
    private UserWeaveImpl mUserWeave;
    private SyncWeaveImpl mSyncWeave;
    private CryptoWeave mCryptoWeave;

    public static final String PREFS_NAME = "TabWeavePrefs";
    public static final String PREFS_PRIVATE_KEY    = "storageKeysPrivkey";
    public static final String PREFS_CRYPTO_TABS_SYMMETRIC_KEY    = "storageCryptoTabsSymmetricKey";

    public static final int ACTIVITY_EDIT_SETTINGS = 1;

    public static final String PREFS_USERNAME = "username";
    public static final String PREFS_PASSWORD = "password";
    public static final String PREFS_PASSPHRASE = "passphrase";
    public static final String PREFS_WEAVENODE = "weaveNode";

    SharedPreferences mTabWeavePrefs;
    SharedPreferences.Editor mTabWeavePrefsEdit;
    
    String sUsername;
    String sPassword;
    String sPassphrase;
    String sSyncServerUrl;
    
    byte[] bytePrivateKeyDecrypted; 
    byte[] byteSymmetricKeyDecrypted;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

//        mWeaveTabsDbAdapter = new TabWeaveDbAdapter(this);
//        mWeaveTabsDbAdapter.open();

        mTabWeavePrefs    = this.getSharedPreferences(PREFS_NAME, 0);
        
        readPreferencesOrRedirectToSettings();

        // Check the settings if the weaveNode is already known. If not, request it
        if(sSyncServerUrl == "")
        {
            mUserWeave      = new UserWeaveImpl();
            
            mTabWeavePrefsEdit = mTabWeavePrefs.edit();
            
            try {
                mTabWeavePrefsEdit.putString("weaveNode", mUserWeave.getUserStorageNode(sUsername, null));
                mTabWeavePrefsEdit.commit();
            } catch (WeaveException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        refreshTabList();
    }

    public void fillData(List<JSONObject> lTabs)
    {
        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{SyncWeave.KEY_TITLE, SyncWeave.KEY_URL, SyncWeave.KEY_ROWID};

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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case R.id.menuSettings:
            showSettings();
            return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    private void showSettings() {
        Intent i    = new Intent(this, TabWeaveSettingsActivity.class);
        startActivityForResult(i, ACTIVITY_EDIT_SETTINGS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(resultCode == RESULT_OK)
        {
            Bundle extras   = intent.getExtras();
        }

        switch(requestCode)
        {
            case ACTIVITY_EDIT_SETTINGS:

                if(resultCode != RESULT_OK)
                {
                    Log.d("ACTIVITY_EDIT_SETTINGS", "Result from ACTIVITY_EDIT_SETTINGS was not 'OK'");
                }
    
                // Re-read preference file
                readPreferencesOrRedirectToSettings();
                
                // Recalculte crypto keys if necessary
                prepareCryptoKeys();
    
                break;
        }
    }
    
    private void readPreferencesOrRedirectToSettings() {
        sUsername    = mTabWeavePrefs.getString(PREFS_USERNAME, "");
        sPassword    = mTabWeavePrefs.getString(PREFS_PASSWORD, "");
        sPassphrase  = mTabWeavePrefs.getString(PREFS_PASSPHRASE, "");
        sSyncServerUrl = mTabWeavePrefs.getString(PREFS_WEAVENODE, "");
        
        // Redirect the user to the settings panel if any of the credentials are missing
        if(sUsername == "" || sPassword == "" || sPassphrase == "") {
            showSettings();

            return;
        }
    }
    
    private void prepareCryptoKeys() {
        try {
            mSyncWeave      = new SyncWeaveImpl(sSyncServerUrl, sUsername, sPassword, sPassphrase);
    
            // Check settings storage for the user's private key
            if(mTabWeavePrefs.getString(PREFS_PRIVATE_KEY, "") == "") {
    
                bytePrivateKeyDecrypted  = mSyncWeave.getPrivateKey();
                
                // Save the generated private key
                mTabWeavePrefsEdit = mTabWeavePrefs.edit();
                mTabWeavePrefsEdit.putString(PREFS_PRIVATE_KEY, new String(Base64Coder.encode(bytePrivateKeyDecrypted)));
                mTabWeavePrefsEdit.commit();
            }
    
            bytePrivateKeyDecrypted     = Base64Coder.decode(mTabWeavePrefs.getString(PREFS_PRIVATE_KEY, ""));
    
            if(mTabWeavePrefs.getString(PREFS_CRYPTO_TABS_SYMMETRIC_KEY, "") == "")
            {
                byteSymmetricKeyDecrypted   = mSyncWeave.getSymmetricKey(bytePrivateKeyDecrypted);
    
                mTabWeavePrefsEdit = mTabWeavePrefs.edit();
                mTabWeavePrefsEdit.putString(PREFS_CRYPTO_TABS_SYMMETRIC_KEY, new String(Base64Coder.encode(byteSymmetricKeyDecrypted)));
                mTabWeavePrefsEdit.commit();
            }
    
            byteSymmetricKeyDecrypted   = Base64Coder.decode(mTabWeavePrefs.getString(PREFS_CRYPTO_TABS_SYMMETRIC_KEY, ""));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void refreshTabList()
    {
        JSONObject oTabPayload;
        String sTabCipherText;
        byte[] byteTabCipherText;
        byte[] byteTabCipherIV;
        CryptoWeave mCryptoWeave    = new CryptoWeaveImpl();
        
        try
        {
            // Retrieve or calculate the crypto keys
            prepareCryptoKeys();
            
            // Request all tabs objects
            JSONArray aCollection   = mSyncWeave.getCollection("tabs?full=1");
            JSONObject[] oTabs      = new JSONObject[32];
            List<JSONObject> lTabs  = new ArrayList<JSONObject>();
    
            int iTabCount           = aCollection.length();
            if(iTabCount > oTabs.length)
            {
                iTabCount   = oTabs.length;
            }
    
            for(int x = 0; x < iTabCount; x++)
            {
                //oTabs[x]    = mSyncWeave.getItem("tabs", aCollection.getString(x)); // Only needed when not requesting ?full=1
                oTabs[x]         = aCollection.getJSONObject(x);
    
                // Decrypt the ciphertext of this tab
                oTabPayload         = new JSONObject(oTabs[x].getString("payload"));
                byteTabCipherText   = Base64Coder.decode(oTabPayload.getString("ciphertext"));
                byteTabCipherIV     = Base64Coder.decode(oTabPayload.getString("IV"));
                sTabCipherText      = new String(mCryptoWeave.decryptCipherText(byteSymmetricKeyDecrypted, byteTabCipherIV, byteTabCipherText));
    
                lTabs.add(x, new JSONObject(sTabCipherText));
            }
            
            fillData(lTabs);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}