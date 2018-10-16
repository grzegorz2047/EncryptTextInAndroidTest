package pl.bsm.securenotepad;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class AuthenticationProvider {

    private int AES_LENGTH = 32;

    public AuthenticationProvider() {
    }

    public SharedPreferences getEncryptedData(Context context, String path) {
        return context.getSharedPreferences(path, Context.MODE_PRIVATE);
    }


    byte[] stretchPasswordToMatchLengthUnsafe(byte[] bytes) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        byte[] key = sha.digest(bytes);
        key = Arrays.copyOf(key, 32);
        return key;
    }

}