package com.caoc.marketplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.caoc.marketplace.database.model.User;
import com.caoc.marketplace.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.ref.WeakReference;

public class MainActivityRegister extends AppCompatActivity {

    private EditText et_names;
    private EditText et_surnames;
    private EditText et_email;
    private EditText et_password;
    private EditText et_phonenumber;

    private Button btn_register;
    private Button btn_gps;

    private CheckBox cb_term;

    private User userDb = new User();
    private User userCreate;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private SharedPreferences shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_register);

        et_names = findViewById(R.id.et_names);
        et_surnames = findViewById(R.id.et_surnames);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_phonenumber = findViewById(R.id.et_phonenumber);

        btn_register = findViewById(R.id.btn_register);
        btn_gps = findViewById(R.id.btn_add_gps);

        cb_term = findViewById(R.id.cb_term);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        shared = getSharedPreferences(Constant.PREFERENCES, MODE_PRIVATE);

    }

    public void createUserPassword(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            new GetUserTask(MainActivityRegister.this).execute();
                        } else {
                            Toast.makeText(MainActivityRegister.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void register(View v) {
        String names = et_names.getText().toString();
        String surnames = et_surnames.getText().toString();
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();
        String phonenumber = et_phonenumber.getText().toString();
        String latitude = String.valueOf(shared.getFloat("LAT",0));
        String longitude = String.valueOf(shared.getFloat("LAM",0));

        if (validate(names, surnames, email, password, phonenumber)) {
            userCreate = new User(names, surnames, email, phonenumber, latitude, longitude);

            createUserPassword(email, password);
        }
    }

    public void registerOk() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.txt_title_register);

        builder.setPositiveButton(R.string.btn_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setMessage(R.string.txt_msg_register);

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void registerError() {
        Toast.makeText(this, R.string.txt_msg_fail_exist, Toast.LENGTH_SHORT).show();
    }

    public void accept(View v) {
        if (cb_term.isChecked() == true) {
            this.btn_register.setEnabled(true);
        } else {
            this.btn_register.setEnabled(false);
        }
    }

    public boolean validate(String names, String surnames, String email, String password, String phonenumber) {
        //names
        if (names.equals("")) {
            et_names.setError(getString(R.string.txt_msg_empty));
            return false;
        } else if (!soloLetras(names)) {
            et_names.setError(getString(R.string.txt_msg_fail));
            return false;
        } else {
            et_names.setError(null);
        }

        //surnames
        if (surnames.equals("")) {
            et_surnames.setError(getString(R.string.txt_msg_empty));
            return false;
        } else if (!soloLetras(surnames)) {
            et_surnames.setError(getString(R.string.txt_msg_fail));
            return false;
        } else {
            et_surnames.setError(null);
        }

        //Correo
        if (email.equals("")) {
            et_email.setError(getString(R.string.txt_msg_empty));
            return false;
        } else if (Patterns.EMAIL_ADDRESS.matcher(email).matches() == false) {
            et_email.setError(getString(R.string.txt_msg_email_fail));
            return false;
        } else {
            et_email.setError(null);
        }

        //password
        if (password.equals("")) {
            et_password.setError(getString(R.string.txt_msg_empty));
            return false;
        } else if (password.length() < 6) {
            et_password.setError(getString(R.string.txt_msg_pass_short));
            return false;
        } else {
            et_password.setError(null);
        }

        //telefono
        if (!phonenumber.equals("") && Patterns.PHONE.matcher(phonenumber).matches() == false) {
            et_phonenumber.setError(getString(R.string.txt_msg_phone_error));
        } else {
            et_phonenumber.setError(null);
        }

        //gps
        if(shared.getFloat("LAT",0) == 0 && shared.getFloat("LON",0) == 0){
            btn_gps.setError(getString(R.string.txt_msg_empty_gps));
            return false;
        }else{
            btn_gps.setError(null);
        }

        return true;
    }

    public boolean soloLetras(String s) {
        for (int x = 0; x < s.length(); x++) {
            char c = s.charAt(x);
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == ' ')) {
                return false;
            }
        }
        return true;
    }

    public void gps(View v){
        Intent gps = new Intent(this, MapsActivity.class);
        startActivity(gps);
    }

    private static class GetUserTask extends AsyncTask<Void, Void, User> {

        private WeakReference<MainActivityRegister> activityWeakReference;

        GetUserTask(MainActivityRegister context) {
            activityWeakReference = new WeakReference<>(context);
        }


        @Override
        protected User doInBackground(Void... voids) {
            if (activityWeakReference != null) {
                activityWeakReference.get().db.collection(Constant.TABLE_USER).document(activityWeakReference.get().userCreate.getEmail()).set(activityWeakReference.get().userCreate);
                return activityWeakReference.get().userCreate;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(User user) {
            User u = activityWeakReference.get().userCreate;
            if (activityWeakReference.get().userCreate != null) {
                activityWeakReference.get().registerOk();
            } else {
                activityWeakReference.get().registerError();
            }
            super.onPostExecute(user);
        }
    }

}