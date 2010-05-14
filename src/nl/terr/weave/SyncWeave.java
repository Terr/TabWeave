/**
 * Mozilla Weave Sync API interface class
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

import info.elebescond.weave.exception.WeaveException;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.NoSuchPaddingException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public interface SyncWeave {

    public static String VERSION    = "1.0";
    public static String KEY_TITLE  = "Title";
    public static String KEY_URL    = "URL";
    public static String KEY_ROWID  = "_id";

    public JSONArray getCollection(String sCollection)
        throws WeaveException, HttpResponseException, ClientProtocolException, IOException, JSONException;

    public JSONObject getItem(String sCollection, String sId)
        throws WeaveException, HttpResponseException, ClientProtocolException, IOException, JSONException;

    public void setUsername(String sUsername);

    public void setPassword(String sPassword);

    public void setServerUrl(String sServerUrl);
    
    public void setPassphrase(String sPassphrase);
    
    public byte[] getPrivateKey()
        throws WeaveException, JSONException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException, IOException;
    
    public byte[] getSymmetricKey(byte[] bytePrivateKeyDecrypted)
        throws WeaveException, JSONException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException, IOException, InvalidKeySpecException;
}