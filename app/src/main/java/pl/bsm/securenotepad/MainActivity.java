package pl.bsm.securenotepad;

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
                    showActionMsg("Za długie hasło");
                } else {
                    Utils utils = new Utils();
                    String inputPasswordValue = passwordField.getText().toString();
                    String filledPasswordToMatch = MainActivity.this.authenticationProvider.stretchPasswordToMatchLengthUnsafe(inputPasswordValue);


                    SharedPreferences data = utils.getAppSharedUserData(MainActivity.this.getApplicationContext(), getString(R.string.encryptedData));
                    try {
                        String decryptedTextPlain = decryptEncryptNaive.decryptToPlainText(filledPasswordToMatch, data, "TEXT");
                        createAndSetNewActivity(filledPasswordToMatch, decryptedTextPlain);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } catch (IncorrectPassword incorrectPassword) {
                        showActionMsg("Niepoprawne haslo!");
                    }
                }
            }
        });
    }

    private void showActionMsg(String text) {
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setTitle(text);
        supportActionBar.show();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getSupportActionBar().hide();
                    }
                });
            }
        }, 5000L);
    }

    private boolean notValidated(byte[] passwordBytes) {
        return passwordBytes.length >= 32;
    }

    private void createAndSetNewActivity(String filledPasswordToMatch, String decryptedTextPlain) {
        Intent notesIntent = new Intent(MainActivity.this.getApplicationContext(), EnterDataActivity.class);
        notesIntent.putExtra("password", filledPasswordToMatch);
        notesIntent.putExtra("notes", decryptedTextPlain);
        startActivity(notesIntent);
    }
}
