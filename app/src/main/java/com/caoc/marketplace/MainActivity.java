package com.caoc.marketplace;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.caoc.marketplace.database.Database;
import com.caoc.marketplace.database.model.User;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private EditText et_email;
    private EditText et_password;

    private Button btn_login;
    private Button btn_register;

    private User users = new User();

    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_login);

        database = Database.getInstance(this);
        /*
        User admin = new User("admin", "admin", "admin@gmail.com","admin","3204694115");
        database.getUserDao().insertUser(admin);
        */
        new GetUserTask(this).execute();
    }

    public void register(View v){
        et_email.setText(null);
        et_password.setText(null);

        Intent register = new Intent(this, MainActivityRegister.class);

        startActivity(register);
    }

    public void login(View v) {
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.txt_title_login);

        if (validateLogin(email,password)) {

            builder.setMessage(R.string.txt_success_login);
            builder.setPositiveButton(R.string.btn_accept, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    loginAccept(email, password);
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

    public boolean validateLogin(String email, String pass){
        boolean test = false;
        User user = database.getUserDao().getUser(email);
        if(user != null && user.getPassword().equals(pass)){
            test = true;
        }
        return test;
    }

    public void loginAccept(String username,String password){

        Intent activity2 = new Intent(this, PantallaPrincipal.class);
        activity2.putExtra("username", username);
        activity2.putExtra("password", password);
        startActivity(activity2);

        Log.e("LOGIN", "Aceptado");
    }

    private static class GetUserTask extends AsyncTask<Void, Void, User> {

        private WeakReference<MainActivity> activityWeakReference;

        GetUserTask(MainActivity context){
            activityWeakReference = new WeakReference<>(context);
        }


        @Override
        protected User doInBackground(Void... voids) {

            if(activityWeakReference != null){
                User user = activityWeakReference.get().database.getUserDao().getUser("admin@gmail.com");

                return user;
            }else {
                return null;
            }

        }

        @Override
        protected void onPostExecute(User user){
            if(user != null){
                activityWeakReference.get().users = user;
            }
            super.onPostExecute(user);
        }
    }
}