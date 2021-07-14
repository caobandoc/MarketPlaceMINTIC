package com.caoc.marketplace;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.caoc.marketplace.ui.User;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivityRegister extends AppCompatActivity {

    private EditText et_names;
    private EditText et_surnames;
    private EditText et_email;
    private EditText et_password;
    private EditText et_phonenumber;

    private Button btn_register;

    private ArrayList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_register);

        et_names = findViewById(R.id.et_names);
        et_surnames = findViewById(R.id.et_surnames);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_phonenumber = findViewById(R.id.et_phonenumber);

        btn_register = findViewById(R.id.btn_login);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        users = (ArrayList<User>) args.getSerializable("ARRAYLIST");
    }

    public void register(View v){
        String names = et_names.getText().toString();
        String surnames = et_surnames.getText().toString();
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();
        String phonenumber = et_phonenumber.getText().toString();

        User user = new User(names,surnames,email,password,phonenumber);

        users.add(user);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.txt_tittle_register);
        builder.setMessage(R.string.txt_msg_register);

        AlertDialog dialog = builder.create();
        dialog.show();

    }


}