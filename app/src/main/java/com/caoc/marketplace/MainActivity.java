package com.caoc.marketplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.caoc.marketplace.database.Database;
import com.caoc.marketplace.database.model.User;
import com.caoc.marketplace.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private EditText et_email;
    private EditText et_password;

    private Button btn_login;
    private Button btn_register;

    private User users = new User();

    private Database database;

    private SharedPreferences preferences;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_login);

        database = Database.getInstance(this);

        db = FirebaseFirestore.getInstance();

        preferences = getSharedPreferences(Constant.PREFERENCES, MODE_PRIVATE);

        String email = preferences.getString("email", null);
        String pass = preferences.getString("password", null);

        if (email != null && pass != null) {
            DocumentReference docRef = db.collection(Constant.TABLE_USER).document(email);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.txt_title_login);
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            if (validateLogin(document, pass)) {
                                loginAccept(document);
                            }
                        }
                    } else {
                        Log.d("CLOUD_FIRE_LOGIN", "get failed with ", task.getException());
                        builder.setMessage(R.string.txt_msg_email_fail);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });
            return;
        }
    }

    public void register(View v) {
        et_email.setText(null);
        et_password.setText(null);

        Intent register = new Intent(this, MainActivityRegister.class);

        startActivity(register);
    }

    public void login(View v) {
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();

        if (!email.equals("") && !password.equals("")) {
            DocumentReference docRef = db.collection(Constant.TABLE_USER).document(email);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.txt_title_login);

                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            if (validateLogin(document, password)) {
                                builder.setMessage(R.string.txt_success_login);
                                builder.setPositiveButton(R.string.btn_accept, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        loginAccept(document);
                                    }
                                });
                                builder.setNegativeButton(R.string.btn_cancel, null);

                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }else{
                                builder.setMessage(R.string.txt_msg_pass_fail);
                            };
                        } else {
                            Log.d("CLOUD_FIRE_LOGIN", "No such document");
                            builder.setMessage(R.string.txt_msg_email_fail);
                        }

                    } else {
                        Log.d("CLOUD_FIRE_LOGIN", "get failed with ", task.getException());
                        builder.setMessage(R.string.txt_msg_email_fail);
                    }

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        } else {
            Toast.makeText(this, R.string.txt_msg_empty, Toast.LENGTH_SHORT).show();
        }

    }

    public boolean validateLogin(DocumentSnapshot document, String pass) {
        boolean test = false;
        if (pass.equals(document.get("password"))) {
            test = true;
        }
        return test;
    }

    public void loginAccept(DocumentSnapshot document) {

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", (document.get("name").toString() + " " + document.get("surname").toString()));
        editor.putString("email", document.getId());
        editor.putString("password", document.get("password").toString());
        editor.commit();


        finish();

        Intent activity2 = new Intent(this, PantallaPrincipal.class);
        startActivity(activity2);

        Log.e("LOGIN", "Aceptado");
    }


}