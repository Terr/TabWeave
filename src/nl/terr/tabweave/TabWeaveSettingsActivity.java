package nl.terr.tabweave;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TabWeaveSettingsActivity extends Activity {

    TabWeaveConfig mWeaveConfig;
    EditText inputUsername;
    EditText inputPassword;
    EditText inputPassphrase;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.settings);
        
        mWeaveConfig    = new TabWeaveConfig(this, TabWeave.PREFS_NAME);

        inputUsername  = (EditText)findViewById(R.id.username);
        inputPassword  = (EditText)findViewById(R.id.password);
        inputPassphrase = (EditText)findViewById(R.id.passphrase);
        Button buttonSave       = (Button)findViewById(R.id.settingsSave);
        
        inputUsername.setText(mWeaveConfig.preferences.getString(TabWeave.PREFS_USERNAME, ""));
        inputPassword.setText(mWeaveConfig.preferences.getString(TabWeave.PREFS_PASSWORD, ""));
        inputPassphrase.setText(mWeaveConfig.preferences.getString(TabWeave.PREFS_PASSPHRASE, ""));
        
        buttonSave.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                
                // Check if any of the old settings are changed. If so, write all settings and delete crypto
                // keys so they will be regenerated
                String sOldUsername = new String(mWeaveConfig.preferences.getString(TabWeave.PREFS_USERNAME, "").toCharArray());
                String sOldPassword = new String(mWeaveConfig.preferences.getString(TabWeave.PREFS_PASSWORD, "").toCharArray());
                String sOldPassphrase = new String(mWeaveConfig.preferences.getString(TabWeave.PREFS_PASSPHRASE, "").toCharArray());
                
                String sNewUsername = inputUsername.getText().toString();
                String sNewPassword = inputPassword.getText().toString();
                String sNewPassphrase   = inputPassphrase.getText().toString();
                
                Intent mIntent = new Intent();
                
                if(
                        sOldUsername != sNewUsername ||
                        sOldPassword != sNewPassword ||
                        sOldPassphrase != sNewPassphrase
                )
                {
                    Log.d("TabWeaveSettings", "Credentials have changed");
                    
                    mWeaveConfig.preferencesEdit.putString(TabWeave.PREFS_USERNAME, sNewUsername);
                    mWeaveConfig.preferencesEdit.putString(TabWeave.PREFS_PASSWORD, sNewPassword);
                    mWeaveConfig.preferencesEdit.putString(TabWeave.PREFS_PASSPHRASE, sNewPassphrase);
                    
                    mWeaveConfig.preferencesEdit.remove(TabWeave.PREFS_WEAVENODE);
                    mWeaveConfig.preferencesEdit.remove(TabWeave.PREFS_PRIVATE_KEY);
                    mWeaveConfig.preferencesEdit.remove(TabWeave.PREFS_CRYPTO_TABS_SYMMETRIC_KEY);
                    
                    mWeaveConfig.preferencesEdit.commit();
                }
                
                mIntent.putExtras(bundle);
                setResult(RESULT_OK, mIntent);
                finish();
            }
        });
    }
}
