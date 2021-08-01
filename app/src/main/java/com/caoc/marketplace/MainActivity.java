package com.caoc.marketplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.caoc.marketplace.database.model.User;
import com.caoc.marketplace.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private EditText et_email;
    private EditText et_password;

    private Button btn_login;
    private Button btn_register;

    private User users = new User();

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_login);

        preferences = getSharedPreferences(Constant.PREFERENCES, MODE_PRIVATE);

        String email = preferences.getString("email", null);
        String pass = preferences.getString("password", null);

        //Autenticacion
        mAuth = FirebaseAuth.getInstance();
        //Token cliente
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.e("USER_ID", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();

                        Log.e("USER_ID", token);
                    }
                });
        //Grupos
        FirebaseMessaging.getInstance().subscribeToTopic("grupo5");

        if (email != null && pass != null) {
            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                loginAccept(email, pass);
                            } else {
                                Log.w("ERROR", "signInWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
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

        if (validEt(email, password)) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    loginAccept(email, password);
                                    Toast.makeText(MainActivity.this, R.string.txt_msg_login_success,
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, R.string.txt_msg_fail,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
        }
    }

    public boolean validEt(String email, String password){
        //Email
        if(email.equals("")){
            et_email.setError(getString(R.string.txt_msg_empty));
            return false;
        }else if(Patterns.EMAIL_ADDRESS.matcher(email).matches()==false){
            et_email.setError(getString(R.string.txt_msg_email_fail));
            return false;
        }else{
            et_email.setError(null);
        }

        //Password
        if(password.equals("")){
            et_password.setError(getString(R.string.txt_msg_empty));
            return false;
        }else if(password.length()<6){
            et_password.setError(getString(R.string.txt_msg_pass_short));
            return false;
        }else{
            et_password.setError(null);
        }
        return true;
    }

    public void loginAccept(String email, String password) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.commit();

        finish();

        Intent activity2 = new Intent(this, PantallaPrincipal.class);
        startActivity(activity2);
    }

}