/**
 * Mozilla Weave User API interface class
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

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import info.elebescond.weave.exception.WeaveException;

public interface UserWeave {

	public static String PREFIX = "user";
	public static String VERSION = "1.0";
	public static String USER_API_URL = "/" + PREFIX + "/" + VERSION;

	/**
	 * Change the email address of the given user.
	 *
	 * @param userId
	 * @param password
	 * @param newEmail
	 * @return
	 * @throws WeaveException
	 */
	public boolean changeEmail(String userId, String password, String newEmail)
			throws WeaveException;

	/**
	 * Change the password of the given user.
	 *
	 * @param userId
	 * @param password
	 * @param newPassword
	 * @return
	 * @throws WeaveException
	 */
	public boolean changePassword(String userId, String password,
			String newPassword) throws WeaveException;

	/**
	 * Returns a boolean for whether the given userID is available at the given
	 * server.
	 *
	 * @param userId
	 * @return
	 * @throws WeaveException
	 */
	public boolean checkUserIdAvailable(String userId) throws WeaveException;

	/**
	 * Create a new user at the given server, with the given userID, password,
	 * and email. If a secret is provided those will be provided as well. Note
	 * that the exact new-user-authorization logic is determined by the server.
	 *
	 * @param userId
	 * @param password
	 * @param email
	 * @return
	 * @throws WeaveException
	 */
	public boolean createUser(String userId, String password, String email)
			throws WeaveException;

	/**
	 * Create a new user at the given server, with the given userID, password,
	 * and email. If a secret is provided, or a captchaChallenge/captchaResponse
	 * pair, those will be provided as well. Note that the exact
	 * new-user-authorization logic is determined by the server.
	 *
	 * @param userId
	 * @param password
	 * @param email
	 * @param captchaChallenge
	 * @param captchaResponse
	 * @return
	 * @throws WeaveException
	 */
	public boolean createUser(String userId, String password, String email,
			String captchaChallenge, String captchaResponse)
			throws WeaveException;

	/**
	 * Delete the given userId
	 *
	 * @param userId
	 * @param password
	 * @return
	 * @throws WeaveException
	 */
	public boolean deleteUser(String userId, String password)
			throws WeaveException;

	public String getSecret();

	public String getServerUrl();

	/**
	 * Returns the URL representing the storage node for the given user. Note
	 * that in the 1.0 server implementation hosted by Mozilla, the password is
	 * not actually required for this call.
	 *
	 * @param userId
	 * @param password
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public String getUserStorageNode(String userId, String password)
			throws WeaveException, ClientProtocolException, IOException;

	/**
	 * Requests a password reset email be mailed to the email address on file.
	 *
	 * @param userId
	 * @return
	 * @throws WeaveException
	 */
	public boolean resetPassword(String userId) throws WeaveException;

	/**
	 * Requests a password reset email be mailed to the email address on file.
	 * If a secret is provided, or a captchaChallenge/captchaResponse pair,
	 * those will be provided as well.
	 *
	 * @param userId
	 * @param captchaChallenge
	 * @param captchaResponse
	 * @return
	 * @throws WeaveException
	 */
	public boolean resetPassword(String userId, String captchaChallenge,
			String captchaResponse) throws WeaveException;

	public void setSecret(String secret);

	public void setServerUrl(String serverUrl);
}