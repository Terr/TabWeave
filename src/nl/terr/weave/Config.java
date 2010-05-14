/**
 * Config class for Android applications
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

package nl.terr.weave;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Config {

    public static final String PREFS_NAME = "TabWeavePrefs";

    private SharedPreferences settings;
    private Editor editor;

    public static Config getConfig(Context context) {
        return new Config(context);
    }

    private Config(Context context) {
        settings = context.getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();
    }

    public void commit() {
        editor.commit();
    }

    public String getUsername() {
        return settings.getString("username", "");
    }

    public void setUsername(String sValue) {
        editor.putString("username", sValue);
    }

    public String getPassword() {
        return settings.getString("password", "");
    }

    public void setPassword(String sValue) {
        editor.putString("password", sValue);
    }

    public String getPassphrase() {
        return settings.getString("passphrase", "");
    }

    public void setPassphrase(String sValue) {
        editor.putString("passphrase", sValue);
    }

    public String getWeaveNode() {
        return settings.getString("weaveNode", "");
    }

    public void setWeaveNode(String sValue) {
        editor.putString("weaveNode", sValue);
    }

    public String getPrivateKey() {
        return settings.getString("storageKeysPrivkey", "");
    }

    public void setPrivateKey(String sValue) {
        editor.putString("storageKeysPrivkey", sValue);
    }

    public String getSymmetricKey() {
        return settings.getString("storageCryptoTabsSymmetricKey", "");
    }

    public void setSymmetricKey(String sValue) {
        editor.putString("storageCryptoTabsSymmetricKey", sValue);
    }
}