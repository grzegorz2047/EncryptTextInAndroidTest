package pl.bsm.securenotepad;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
import org.spongycastle.util.encoders.Hex;

import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.Timer;
import java.util.TimerTask;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.Security;


import static java.nio.charset.StandardCharsets.UTF_8;

public class MainActivity extends AppCompatActivity {

    Button loginButton;
    Button registerButton;
    EditText passwordField;

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Security.addProvider(new BouncyCastleProvider());
        Security.addProvider(new BouncyCastlePQCProvider());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.loginButton);
        passwordField = findViewById(R.id.passwordField);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] passwordBytes = passwordField.getText().toString().getBytes();
                if (passwordBytes.length >= 32) {
                    ActionBar supportActionBar = getSupportActionBar();
                    supportActionBar.setTitle("Za długie hasło");
                    supportActionBar.show();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            getSupportActionBar().hide();
                        }
                    }, 5000L);
                } else {

                    createAndSetNewActivity();
                }
            }
        });
    }

    private SharedPreferences getEncryptedData() {
        Context context = MainActivity.this.getApplicationContext();
        String encryptedData = getString(R.string.encryptedData);
        return context.getSharedPreferences(encryptedData, Context.MODE_PRIVATE);
    }

    private void createAndSetNewActivity() {
        Intent notesIntent = new Intent(MainActivity.this.getApplicationContext(), EnterDataActivity.class);
        String passwordValue = passwordField.getText().toString();
        int resultLength = 32;
        notesIntent.putExtra("password", supplyText(resultLength - passwordValue.length()) + passwordValue);
        startActivity(notesIntent);
    }

    private String supplyText(int remaining) {
        return "1qaz3edc5tgb7ujm6yhn4rfv2wsx0opl".substring(0, remaining);
    }
}
