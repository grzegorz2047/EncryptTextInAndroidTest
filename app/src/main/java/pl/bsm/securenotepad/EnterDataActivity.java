package pl.bsm.securenotepad;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.spongycastle.util.encoders.Hex;

import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static java.nio.charset.StandardCharsets.UTF_8;


public class EnterDataActivity extends AppCompatActivity {

    private Button saveButton;
    private EditText notes;
    private SecretKey secretKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_data);

        String password = getIntent().getExtras().getString("password");
        if (password == null) {
            finishAndRemoveTask();
            return;
        }
        secretKey = defineKey(password.getBytes());


        saveButton = findViewById(R.id.saveButton);
        notes = findViewById(R.id.notes);
        Context context = this.getApplicationContext();
        System.out.println("password: " + password);
        SharedPreferences data = getSharedData();
        if (data.contains("TEXT")) {
            try {
                String encryptedData = getString(R.string.encryptedData);

                String decryptedPlain = decryptData(context, encryptedData);
                notes.setText(decryptedPlain);
                System.out.println("out: " + decryptedPlain);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences data = getSharedData();
                try {
                    String notesToEncrypt = notes.getText().toString();
                    byte[][] bytes = ctrEncrypt(secretKey, notesToEncrypt.getBytes());
                    SharedPreferences.Editor editor = data.edit();
                    //encrypt
                    String iv = Arrays.toString(bytes[0])
                            .replace("[", "")
                            .replace("]", "").trim();
                    String cipherText = Arrays.toString(bytes[1])
                            .replace("[", "")
                            .replace("]", "").trim();

                    editor.putString("IV", iv);
                    editor.putString("TEXT", cipherText);
                    editor.apply();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

            }


        });
    }

    private SharedPreferences getSharedData() {
        Context context = EnterDataActivity.this.getApplicationContext();
        String encryptedData = getString(R.string.encryptedData);
        return context.getSharedPreferences(
                encryptedData, Context.MODE_PRIVATE);
    }

    @NonNull
    private String decryptData(Context context, String encryptedData) throws GeneralSecurityException {
        SharedPreferences sharedPreferences = context.getSharedPreferences(encryptedData, Context.MODE_PRIVATE);
        String[] output = sharedPreferences.getString("TEXT", "noData").split(",");
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
        return new String(decryptedToStringifiy, UTF_8);
    }

    public static SecretKey defineKey(byte[] keyBytes) {
        if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
            throw new IllegalArgumentException("keyBytes wrong length for AES key");
        }
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static byte[][] ctrEncrypt(SecretKey key, byte[] data)
            throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(Hex.decode("000102030405060708090a0b")));//Jakis tam nielosowy
        return new byte[][]{cipher.getIV(), cipher.doFinal(data)};
    }

    public static byte[] ctrDecrypt(SecretKey key, byte[] iv, byte[] cipherText)
            throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher.doFinal(cipherText);
    }

    private boolean isinitialised() {
        SharedPreferences sharedPreferences = getEncryptedData();
        String exitingNotes = sharedPreferences.getString("TEXT", "NOT_FOUND");
        return !exitingNotes.equalsIgnoreCase("NOT_FOUND");
    }

    private SharedPreferences getEncryptedData() {
        Context context = EnterDataActivity.this.getApplicationContext();
        String encryptedData = getString(R.string.encryptedData);
        return context.getSharedPreferences(encryptedData, Context.MODE_PRIVATE);
    }

}
