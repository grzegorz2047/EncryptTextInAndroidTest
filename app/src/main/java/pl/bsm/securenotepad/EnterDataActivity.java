package pl.bsm.securenotepad;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;


public class EnterDataActivity extends AppCompatActivity {

    private final AuthenticationProvider authenticationProvider = new AuthenticationProvider();
    private final DecryptEncryptNaive decryptEncryptNaive = new DecryptEncryptNaive();
    private final Utils utils = new Utils();
    private Button saveButton;
    private Button changePasswordButton;
    private EditText notes;
    private EditText changePasswordField;
    private byte[] passwordStretched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_data);

        final String notesText = getIntent().getExtras().getString("notes");
        passwordStretched = getIntent().getExtras().getByteArray("password");

        saveButton = findViewById(R.id.saveButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        changePasswordField = findViewById(R.id.changePasswordField);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputPasswordValue = changePasswordField.getText().toString();
                if (inputPasswordValue.length() >= 5 && inputPasswordValue.length() < 25) {
                    try {
                        passwordStretched = EnterDataActivity.this.authenticationProvider.stretchPasswordToMatchLengthUnsafe(inputPasswordValue.getBytes("UTF-8"));
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String notesToEncrypt = notes.getText().toString();
                    SharedPreferences data = utils.getAppSharedUserData(EnterDataActivity.this.getApplicationContext(), EnterDataActivity.this.getString(R.string.encryptedData));
                    try {
                        decryptEncryptNaive.encryptAndSaveNotes(notesToEncrypt, data.edit(), "TEXT", passwordStretched);
                    } catch (GeneralSecurityException e) {
                        showActionMsg("Nie udalo sie zmienic hasla i zaszyfrowac!", getSupportActionBar());
                    }
                } else {
                    showActionMsg("Haslo min 5 znakow i max 25!", getSupportActionBar());
                }
            }
        });
        notes = findViewById(R.id.notes);

        notes.setText(notesText);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences data = utils.getAppSharedUserData(EnterDataActivity.this.getApplicationContext(), EnterDataActivity.this.getString(R.string.encryptedData));
                try {
                    String notesToEncrypt = notes.getText().toString();
                    decryptEncryptNaive.encryptAndSaveNotes(notesToEncrypt, data.edit(), "TEXT", passwordStretched);
                } catch (GeneralSecurityException e) {
                    showActionMsg("Nie udalo sie zapisac!", getSupportActionBar());
                }

            }


        });
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
