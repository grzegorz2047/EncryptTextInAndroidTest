package pl.bsm.securenotepad;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import org.spongycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import pl.bsm.securenotepad.exceptions.IncorrectPassword;

public class DecryptEncryptNaive {

    private final String verifier = "<<!!>>VERIFIED<<!!>>";

    DecryptEncryptNaive() {
    }

    String decryptToPlainText(SharedPreferences data, String encryptedTextPath, byte[] passwordBytes) throws Exception, IncorrectPassword {
        SecretKey secretKey = defineKey(passwordBytes);
        String decryptedPlain;
        if (data.contains(encryptedTextPath)) {
            try {

                decryptedPlain = decryptData(data, secretKey, "TEXT");
                ;
                int i = decryptedPlain.indexOf(this.verifier);
                if (i == -1) {
                    throw new IncorrectPassword("Niepoprawne haslo!");
                }
                decryptedPlain = decryptedPlain.replaceAll(this.verifier, "");
                return decryptedPlain;
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        }
        throw new Exception("Nothing to encrypt");
    }

    void encryptAndSaveNotes(String notesToEncryptBytes, SharedPreferences.Editor editor, String encryptedTextPath, byte[] passwordBytes) throws GeneralSecurityException {
        SecretKey secretKey = defineKey(passwordBytes);
        notesToEncryptBytes += this.verifier;
        byte[][] bytes = ctrEncrypt(secretKey, notesToEncryptBytes.getBytes());
        //encrypt
        String iv = Arrays.toString(bytes[0])
                .replace("[", "")
                .replace("]", "").trim();
        String cipherText = Arrays.toString(bytes[1])
                .replace("[", "")
                .replace("]", "").trim();

        editor.putString("IV", iv);
        editor.putString(encryptedTextPath, cipherText);
        editor.apply();
    }

    @NonNull
    private String decryptData(SharedPreferences sharedPreferences, SecretKey secretKey, String encryptedTextPath) throws GeneralSecurityException {
        String[] output = sharedPreferences.getString(encryptedTextPath, "noData").split(",");
        String[] iv = sharedPreferences.getString("IV", "noData").split(",");
        byte[] cipherTextArray = new byte[output.length];

        for (int i = 0; i < output.length; i++) {
            cipherTextArray[i] = (byte) Integer.parseInt(output[i].trim());
        }
        byte[] ivArray = new byte[iv.length];
        for (int i = 0; i < iv.length; i++) {
            ivArray[i] = (byte) Integer.parseInt(iv[i].trim());
        }
        byte[] decryptedToStringifiy = ctrDecrypt(secretKey, ivArray, cipherTextArray);
        return new String(decryptedToStringifiy, StandardCharsets.UTF_8);
    }

    private static SecretKey defineKey(byte[] keyBytes) {
        if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
            throw new IllegalArgumentException("keyBytes wrong length for AES key");
        }
        return new SecretKeySpec(keyBytes, "AES");
    }

    private static byte[][] ctrEncrypt(SecretKey key, byte[] data)
            throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(Hex.decode("000102030405060708090a0b")));//Jakis tam nielosowy initializatino vector, chociaż mógłby być
        return new byte[][]{cipher.getIV(), cipher.doFinal(data)};
    }

    private static byte[] ctrDecrypt(SecretKey key, byte[] iv, byte[] cipherText)
            throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher.doFinal(cipherText);
    }
}