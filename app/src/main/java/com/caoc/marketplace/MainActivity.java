package com.caoc.marketplace;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText et_username;
    private EditText et_password;

    private Button btn_login;
    private Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_login);
    }

    public void login(View v) {
        String username = et_username.getText().toString();
        String password = et_password.getText().toString();
        Log.e("USERNAME", username);
        Log.e("PASSWORD", password);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.txt_tittle_login);
        if (username.equals("admin") && password.equals("admin")) {
            Log.e("LOGIN", "Se ha iniciado sesion");

            builder.setMessage(R.string.txt_success_login);
            builder.setPositiveButton(R.string.btn_accept, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    loginAccept(username, password);
                }
            });
            builder.setNegativeButton(R.string.btn_cancel, null);

            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            Log.e("LOGIN", "Error iniciado sesion");
            builder.setMessage(R.string.txt_error_login);

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void loginAccept(String username,String password){
        Intent activity2 = new Intent(this, MainActivity2.class);
        activity2.putExtra("username", username);
        activity2.putExtra("password", password);
        startActivity(activity2);
    }
}