package com.caoc.marketplace;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.caoc.marketplace.database.UserDatabase;
import com.caoc.marketplace.database.model.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivityRegister extends AppCompatActivity {

    private EditText et_names;
    private EditText et_surnames;
    private EditText et_email;
    private EditText et_password;
    private EditText et_phonenumber;

    private Button btn_register;

    private CheckBox cb_term;

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

        cb_term = findViewById(R.id.cb_term);

    }

    public void register(View v){
        String names = et_names.getText().toString();
        String surnames = et_surnames.getText().toString();
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();
        String phonenumber = et_phonenumber.getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.txt_tittle_register);
        if(this.validate(names,surnames,email,password,phonenumber)){
            User user = new User(names,surnames,email,password,phonenumber);

            UserDatabase userDatabase = UserDatabase.getInstance(this);

            long response = userDatabase.getUserDao().insertUser(user);

            if(response > 0){

                builder.setPositiveButton(R.string.btn_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                builder.setMessage(R.string.txt_msg_register);



            }else{
                builder.setMessage(R.string.txt_msg_error_register);

            }

            AlertDialog dialog = builder.create();

            dialog.show();


        }else{
            builder.setMessage(R.string.txt_msg_fail);

            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    public void accept(View v){
        if(cb_term.isChecked() == true){
            this.btn_register.setEnabled(true);
        }else{
            this.btn_register.setEnabled(false);
        }
    }

    public boolean validate(String names, String surnames, String email, String password, String phonenumber){
        boolean test = false;
        if(names != null && surnames != null && email != null && password != null && phonenumber != null
                && soloLetras(names) && soloLetras(surnames) && validarCorreo(email) && soloNumeros(phonenumber)){
            test = true;
        }
        return test;
    }

    public boolean soloLetras(String s){
        for (int x = 0; x < s.length(); x++) {
            char c = s.charAt(x);
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == ' ')) {
                return false;
            }
        }
        return true;
    }

    public boolean validarCorreo(String s){
        Pattern pattern = Pattern
                .compile("([a-z0-9]+(\\.?[a-z0-9])*)+@(([a-z]+)\\.([a-z]+))+");

        Matcher mather = pattern.matcher(s);

        return mather.find();
    }

    public boolean soloNumeros(String s){
        for (int x = 0; x < s.length(); x++) {
            char c = s.charAt(x);
            if (!(c >= '0' && c <= '9')) {
                return false;
            }
        }
        return true;
    }


}