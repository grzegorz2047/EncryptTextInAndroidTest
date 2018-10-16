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

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Timer;
import java.util.TimerTask;

import pl.bsm.securenotepad.exceptions.IncorrectPassword;

public class MainActivity extends AppCompatActivity {

    private final AuthenticationProvider authenticationProvider = new AuthenticationProvider();
    Button loginButton;
    EditText passwordField;
    private final DecryptEncryptNaive decryptEncryptNaive = new DecryptEncryptNaive();

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Security.addProvider(new BouncyCastleProvider());
        Security.addProvider(new BouncyCastlePQCProvider());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        loginButton = findViewById(R.id.loginButton);
        passwordField = findViewById(R.id.passwordField);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] passwordBytes = passwordField.getText().toString().getBytes();
                if (notValidated(passwordBytes)) {
                    showActionMsg("Haslo min 5 znakow i max 25", getSupportActionBar());
                } else {
                    Utils utils = new Utils();
                    String inputPasswordValue = passwordField.getText().toString();
                    byte[] filledPasswordToMatch = null;
                    try {
                        filledPasswordToMatch = MainActivity.this.authenticationProvider.stretchPasswordToMatchLengthUnsafe(inputPasswordValue.getBytes("UTF-8"));
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    Context applicationContext = MainActivity.this.getApplicationContext();
                    SharedPreferences data = utils.getAppSharedUserData(applicationContext, getString(R.string.encryptedData));
                    try {
                        String decryptedTextPlain = decryptEncryptNaive.decryptToPlainText(data, "TEXT", filledPasswordToMatch);
                        createAndSetNewActivity(decryptedTextPlain, filledPasswordToMatch);
                    } catch (Exception e) {
                        createAndSetNewActivity("", filledPasswordToMatch);
                    } catch (IncorrectPassword incorrectPassword) {
                        showActionMsg("Niepoprawne haslo!", getSupportActionBar());
                    }
                }
            }
        });
    }


    private boolean notValidated(byte[] passwordBytes) {
        return passwordBytes.length >= 25 || passwordBytes.length < 5;
    }

    private void createAndSetNewActivity(String decryptedTextPlain, byte[] toMatchBytes) {
        Intent notesIntent = new Intent(MainActivity.this.getApplicationContext(), EnterDataActivity.class);
        notesIntent.putExtra("password", toMatchBytes);
        notesIntent.putExtra("notes", decryptedTextPlain);
        startActivity(notesIntent);
    }
    public void showActionMsg(String text, final ActionBar actionBar) {
        actionBar.setTitle(text);
        actionBar.show();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        actionBar.hide();
                    }
                });
            }
        }, 5000L);
    }
}
