package pl.bsm.securenotepad;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.security.GeneralSecurityException;


public class EnterDataActivity extends AppCompatActivity {

    private final DecryptEncryptNaive decryptEncryptNaive = new DecryptEncryptNaive();
    private final Utils utils = new Utils();
    private Button saveButton;
    private Button changePasswordButton;
    private EditText notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_data);

        final String password = getIntent().getExtras().getString("password");
        final String notesText = getIntent().getExtras().getString("notes");

        saveButton = findViewById(R.id.saveButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        notes = findViewById(R.id.notes);

        notes.setText(notesText);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences data = utils.getAppSharedUserData(EnterDataActivity.this.getApplicationContext(), EnterDataActivity.this.getString(R.string.encryptedData));
                try {
                    String notesToEncrypt = notes.getText().toString();
                    decryptEncryptNaive.encryptAndSaveNotes(notesToEncrypt, data.edit(), password, "TEXT");
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

            }


        });
    }


}
