/**
 * Implementation of nl.terr.weave.UserWeave interface
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

package nl.terr.weave.impl;

import info.elebescond.weave.exception.WeaveException;

import java.io.IOException;

import nl.terr.http.HttpRequest;
import nl.terr.weave.UserWeave;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class UserWeaveImpl implements UserWeave {

	private String sSecret;
	private String sServerUrl;

	public UserWeaveImpl() {
	    this.sServerUrl    = "https://auth.services.mozilla.com";
	}

	public UserWeaveImpl(String sServerUrl) {
        this.sServerUrl = sServerUrl;
    }

	@Override
    public boolean changeEmail(String userId, String password, String newEmail)
            throws WeaveException {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean changePassword(String userId, String password,
            String newPassword) throws WeaveException {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean checkUserIdAvailable(String userId) throws WeaveException {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean createUser(String userId, String password, String email)
            throws WeaveException {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean createUser(String userId, String password, String email,
            String captchaChallenge, String captchaResponse)
            throws WeaveException {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean deleteUser(String userId, String password)
            throws WeaveException {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public String getSecret() {
        return this.sSecret;
    }
    @Override
    public String getServerUrl() {
        return this.sServerUrl;
    }

    public String getUserApiUrl() {
        return this.sServerUrl + USER_API_URL + "/";
    }

    @Override
    public String getUserStorageNode(String sUserId, String sPassword)
            throws ClientProtocolException, IOException {

        DefaultHttpClient client    = new DefaultHttpClient();
        String response             = "";

        String sUrl = getUserApiUrl() + sUserId + "/node/weave";

        response   = HttpRequest.get(client, sUrl);

        Log.d("getUserStorageNode", response);

        client.getConnectionManager().shutdown();

        return response;
    }
    @Override
    public boolean resetPassword(String userId) throws WeaveException {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean resetPassword(String userId, String captchaChallenge,
            String captchaResponse) throws WeaveException {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public void setSecret(String sSecret) {
        this.sSecret    = sSecret;
    }
    @Override
    public void setServerUrl(String sServerUrl) {
        this.sServerUrl = sServerUrl;
    }
}