package com.caoc.marketplace;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.caoc.marketplace.database.Database;
import com.caoc.marketplace.database.model.User;

import java.lang.ref.WeakReference;
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

    private Database database;

    private User userDb = new User();
    private User userCreate;

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

        database = Database.getInstance(this);

        if(this.validate(names,surnames,email,password,phonenumber)) {
            userCreate = new User(names, surnames, email, password, phonenumber);

            new GetUserTask(this).execute();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.txt_title_register);

            builder.setMessage(R.string.txt_msg_fail);

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void registerOk(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.txt_title_register);

        database.getUserDao().insertUser(userCreate);

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

    public void registerError(){
        Toast.makeText(this, R.string.txt_msg_fail_exist, Toast.LENGTH_SHORT).show();
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

    private static class GetUserTask extends AsyncTask<Void, Void, User> {

        private WeakReference<MainActivityRegister> activityWeakReference;

        GetUserTask(MainActivityRegister context){
            activityWeakReference = new WeakReference<>(context);
        }


        @Override
        protected User doInBackground(Void... voids) {
            if(activityWeakReference != null){
                User user = activityWeakReference.get().database.getUserDao().getUser(activityWeakReference.get().userCreate.getEmail());
                return user;
            }else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(User user){
            if(user != null){
                activityWeakReference.get().registerError();
            }else{
                activityWeakReference.get().registerOk();
            }
            super.onPostExecute(user);
        }
    }

}