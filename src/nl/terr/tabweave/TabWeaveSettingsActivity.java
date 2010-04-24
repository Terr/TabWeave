package nl.terr.tabweave;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TabWeaveSettingsActivity extends Activity {

    SharedPreferences mTabWeavePrefs;
    SharedPreferences.Editor mTabWeavePrefsEdit;
    
    EditText inputUsername;
    EditText inputPassword;
    EditText inputPassphrase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);

        mTabWeavePrefs = this.getSharedPreferences(TabWeave.PREFS_NAME, 0);

        inputUsername  = (EditText)findViewById(R.id.username);
        inputPassword  = (EditText)findViewById(R.id.password);
        inputPassphrase = (EditText)findViewById(R.id.passphrase);
        Button buttonSave       = (Button)findViewById(R.id.settingsSave);

        inputUsername.setText(mTabWeavePrefs.getString(TabWeave.PREFS_USERNAME, ""));
        inputPassword.setText(mTabWeavePrefs.getString(TabWeave.PREFS_PASSWORD, ""));
        inputPassphrase.setText(mTabWeavePrefs.getString(TabWeave.PREFS_PASSPHRASE, ""));

        buttonSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();

                mTabWeavePrefsEdit  = mTabWeavePrefs.edit();
                
                // Check if any of the old settings are changed. If so, write all settings and delete crypto
                // keys so they will be regenerated
                String sOldUsername = mTabWeavePrefs.getString(TabWeave.PREFS_USERNAME, "");
                String sOldPassword = mTabWeavePrefs.getString(TabWeave.PREFS_PASSWORD, "");
                String sOldPassphrase = mTabWeavePrefs.getString(TabWeave.PREFS_PASSPHRASE, "");
                
                String sNewUsername = inputUsername.getText().toString();
                String sNewPassword = inputPassword.getText().toString();
                String sNewPassphrase   = inputPassphrase.getText().toString();

                Intent mIntent = new Intent();

                // Check if any of the credentials have changed. If so, delete the cryptographic 
                // keys so they will be regenerated
                if(
                        !sNewUsername.equals(sOldUsername) ||
                        !sNewPassword.equals(sOldPassword) ||
                        !sNewPassphrase.equals(sOldPassphrase)
                )
                {
                    Log.d("TabWeaveSettings", "Credentials have changed");

                    mTabWeavePrefsEdit.putString(TabWeave.PREFS_USERNAME, sNewUsername);
                    mTabWeavePrefsEdit.putString(TabWeave.PREFS_PASSWORD, sNewPassword);
                    mTabWeavePrefsEdit.putString(TabWeave.PREFS_PASSPHRASE, sNewPassphrase);

                    mTabWeavePrefsEdit.remove(TabWeave.PREFS_WEAVENODE);
                    mTabWeavePrefsEdit.remove(TabWeave.PREFS_PRIVATE_KEY);
                    mTabWeavePrefsEdit.remove(TabWeave.PREFS_CRYPTO_TABS_SYMMETRIC_KEY);

                    mTabWeavePrefsEdit.commit();
                }

                mIntent.putExtras(bundle);
                setResult(RESULT_OK, mIntent);
                finish();
            }
        });
    }
}
