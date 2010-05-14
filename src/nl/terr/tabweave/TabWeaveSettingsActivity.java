/**
 * Tab Weave, Mozilla Weave client for Android
 * Copyright (C) 2010 Arjen Verstoep <terr@xs4all.nl>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package nl.terr.tabweave;

import nl.terr.weave.Config;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

public class TabWeaveSettingsActivity extends Activity {

    EditText inputUsername;
    EditText inputPassword;
    EditText inputPassphrase;

    Config mConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);

        mConfig = Config.getConfig(this);

        inputUsername  = (EditText)findViewById(R.id.username);
        inputPassword  = (EditText)findViewById(R.id.password);
        inputPassphrase = (EditText)findViewById(R.id.passphrase);
        Button buttonSave       = (Button)findViewById(R.id.settingsSave);
        CheckBox cbShowPasswords    = (CheckBox)findViewById(R.id.showPasswords);

        inputUsername.setText(mConfig.getUsername());
        inputPassword.setText(mConfig.getPassword());
        inputPassphrase.setText(mConfig.getPassphrase());

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();

                // Check if any of the old settings are changed. If so, write all settings and delete crypto
                // keys so they will be regenerated
                String sOldUsername = mConfig.getUsername();
                String sOldPassword = mConfig.getPassword();
                String sOldPassphrase = mConfig.getPassphrase();

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

                    mConfig.setUsername(sNewUsername);
                    mConfig.setPassword(sNewPassword);
                    mConfig.setPassphrase(sNewPassphrase);

                    // Remove values so they can be regenerated
                    mConfig.setWeaveNode("");
                    mConfig.setPrivateKey("");
                    mConfig.setSymmetricKey("");
                    mConfig.setWeaveNode("");

                    mConfig.commit();
                }

                mIntent.putExtras(bundle);
                setResult(RESULT_OK, mIntent);
                finish();
            }
        });

        cbShowPasswords.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    inputPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    inputPassphrase.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                else
                {
                    inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    inputPassphrase.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
    }
}

