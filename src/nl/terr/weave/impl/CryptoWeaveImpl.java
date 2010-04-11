package nl.terr.weave.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import nl.terr.weave.CryptoWeave;
import cheeso.examples.PBKDF2;

public class CryptoWeaveImpl implements CryptoWeave {
    
    /**
     * Decrypts private key
     * 
     * @return Decrypted private key
     * @throws NoSuchPaddingException 
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidAlgorithmParameterException 
     * @throws InvalidKeyException 
     * @throws IOException 
     */
    public byte[] decryptPrivateKey(String sPassphrase, byte[] byteSalt, byte[] byteIv, byte[] bytePrivateKey)
        throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IOException {

//        PKCS5S2ParametersGenerator pkcsParam = new PKCS5S2ParametersGenerator();
//        pkcsParam.init(sPassphrase.getBytes(), byteSalt, 4096);
//        KeyParameter cipherParam = (KeyParameter)pkcsParam.generateDerivedParameters(256);
        
        IvParameterSpec specIv  = new IvParameterSpec(byteIv);
        SecretKeySpec specKey   = new SecretKeySpec(PBKDF2.deriveKey(sPassphrase.getBytes(), byteSalt, 4096, 32), "PKCS5S2");

        Cipher cipherEncrypt    = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
        cipherEncrypt.init(Cipher.DECRYPT_MODE, specKey, specIv);
        
        ByteArrayOutputStream bOut  = new ByteArrayOutputStream();
        CipherOutputStream cOut     = new CipherOutputStream(bOut, cipherEncrypt);

        cOut.write(bytePrivateKey);
        cOut.close();
        
        return bOut.toByteArray();
    }
    
    /**
     * Decrypts the symmetric key from /storage/crypto/[collection] using the user's (decrypted) private key
     * 
     * @param byteDecryptedPrivateKey
     * @param byteSymmetricKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws NoSuchProviderException 
     * @throws NoSuchProviderException 
     */
    public byte[] decryptSymmetricKey(byte[] byteDecryptedPrivateKey, byte[] byteSymmetricKey) 
        throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, IOException, NoSuchProviderException {
        
        Cipher cipherEncrypt    = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
        KeyFactory keyFactory   = KeyFactory.getInstance("RSA", "BC");
        EncodedKeySpec privateKeySpec   = new PKCS8EncodedKeySpec(byteDecryptedPrivateKey);
        PrivateKey decryptedPrivateKey  = keyFactory.generatePrivate(privateKeySpec);
        
        cipherEncrypt.init(Cipher.DECRYPT_MODE, decryptedPrivateKey);
        
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        CipherOutputStream cOut = new CipherOutputStream(bOut, cipherEncrypt);
        
        cOut.write(byteSymmetricKey);
        cOut.close();
        
        return bOut.toByteArray();
    }

    /**
     * Decrypts the ciphertext from a WBO
     * 
     * @param byteDecryptedSymmetricKey
     * @param byteWboIV
     * @param byteCipherText
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IOException
     */
    public byte[] decryptCipherText(byte[] byteDecryptedSymmetricKey, byte[] byteWboIV, byte[] byteCipherText)
        throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IOException {
        
        SecretKeySpec specSymmetricKey   = new SecretKeySpec(byteDecryptedSymmetricKey, "AES");
        IvParameterSpec specTabIv  = new IvParameterSpec(byteWboIV);
        
        Cipher cipherEncrypt    = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
        cipherEncrypt.init(Cipher.DECRYPT_MODE, specSymmetricKey, specTabIv);
        
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        CipherOutputStream cOut = new CipherOutputStream(bOut, cipherEncrypt);

        cOut.write(byteCipherText);
        cOut.close();
        
        return bOut.toByteArray();
    }
}