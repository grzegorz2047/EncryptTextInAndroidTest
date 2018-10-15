package pl.bsm.securenotepad;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthenticationProvider {

    private int AES_LENGTH = 32;

    public AuthenticationProvider() {
    }

    public SharedPreferences getEncryptedData(Context context, String path) {
        return context.getSharedPreferences(path, Context.MODE_PRIVATE);
    }


    String stretchPasswordToMatchLengthUnsafe(String passwordValue) {

        return fillMissingLength(AES_LENGTH - passwordValue.length()) + passwordValue;
    }

    String fillMissingLength(int remaining) {
        return "1qaz3edc5tgb7ujm6yhn4rfv2wsx0opl".substring(0, remaining);//Nie wiem czy trzeba, ale działa xd
    }
}