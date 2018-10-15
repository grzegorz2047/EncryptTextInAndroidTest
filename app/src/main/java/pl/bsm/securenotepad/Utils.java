package pl.bsm.securenotepad;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;

import java.util.Timer;
import java.util.TimerTask;

public class Utils {
    public Utils() {
    }

    SharedPreferences getAppSharedUserData(Context context, String encryptedDataPath) {
        return context.getSharedPreferences(
                encryptedDataPath, Context.MODE_PRIVATE);
    }


}