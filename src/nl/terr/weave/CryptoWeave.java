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
