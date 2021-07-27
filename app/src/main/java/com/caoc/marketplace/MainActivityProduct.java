package com.caoc.marketplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.caoc.marketplace.database.model.Product;
import com.caoc.marketplace.util.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivityProduct extends AppCompatActivity {

    private EditText et_name;
    private EditText et_description;
    private EditText et_imageURL;
    private EditText et_price;
    private Button btn_save;

    private Product product;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_product);

        et_name = findViewById(R.id.et_name);
        et_description = findViewById(R.id.et_description);
        et_imageURL = findViewById(R.id.et_image_url);
        et_price = findViewById(R.id.et_price);
        btn_save = findViewById(R.id.btn_save_p);

        db = FirebaseFirestore.getInstance();
    }

    public void save(View v){
        String name = et_name.getText().toString();
        String description = et_description.getText().toString();
        String image = et_imageURL.getText().toString();
        String price = et_price.getText().toString();

        if(validateInput(name,description,image,price)){
            product = new Product(name,description,image,price);

            db.collection(Constant.TABLE_PRODUCT)
                    .add(product)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            saveOk();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            saveError();
                        }
                    });

        }

    }

    public boolean validateInput(String name, String description, String image, String price){
        if(name.equals("")){
            et_name.setError(getString(R.string.txt_msg_empty));
            return false;
        }else{
            et_name.setError(null);
        }

        if(description.equals("")){
            et_description.setError(getString(R.string.txt_msg_empty));
            return false;
        }else{
            et_description.setError(null);
        }

        if(image.equals("")){
            et_imageURL.setError(getString(R.string.txt_msg_empty));
            return false;
        }else if(Patterns.WEB_URL.matcher(image).matches()==false){
            et_imageURL.setError(getString(R.string.txt_msg_url_fail));
            return false;
        }else{
            et_imageURL.setError(null);
        }

        if(image.equals("")){
            et_price.setError(getString(R.string.txt_msg_empty));
            return false;
        }else{
            et_price.setError(null);
        }

        return true;
    }

    private void saveOk() {
        Fragment frg = null;
        frg = getSupportFragmentManager().findFragmentByTag("Your_Fragment_TAG");
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();

        Toast.makeText(this, R.string.txt_msg_add_product, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void saveError() {
        Toast.makeText(this, R.string.txt_msg_add_product_fail, Toast.LENGTH_SHORT).show();
    }
}