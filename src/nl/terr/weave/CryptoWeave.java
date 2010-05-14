/**
 * Mozilla Weave cryptography interface class
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
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.NoSuchPaddingException;

public interface CryptoWeave {

    public byte[] decryptPrivateKey(String sPassphrase, byte[] byteSalt, byte[] byteIv, byte[] bytePrivateKey)
        throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IOException;

    public byte[] decryptSymmetricKey(byte[] byteDecryptedPrivateKey, byte[] byteSymmetricKey)
        throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, IOException, NoSuchProviderException;

    public byte[] decryptCipherText(byte[] byteDecryptedSymmetricKey, byte[] byteWboIV, byte[] byteCipherText)
        throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IOException;

}
