package pl.bsm.securenotepad;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {
    public Utils() {
    }

    SharedPreferences getAppSharedUserData(Context context, String encryptedDataPath) {
        return context.getSharedPreferences(
                encryptedDataPath, Context.MODE_PRIVATE);
    }
}