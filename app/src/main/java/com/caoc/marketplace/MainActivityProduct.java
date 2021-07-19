package com.caoc.marketplace;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.caoc.marketplace.database.Database;
import com.caoc.marketplace.database.model.Product;

public class MainActivityProduct extends AppCompatActivity {

    private EditText et_name;
    private EditText et_description;
    private EditText et_imageURL;
    private EditText et_price;
    private Button btn_save;

    private Database database;

    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_product);
    }

    public void save(){
        String name = et_name.getText().toString();
        String description = et_description.getText().toString();
        String image = et_imageURL.getText().toString();
        String price = et_price.getText().toString();



        product = new Product(name,description,image,price);

        database = Database.getInstance(this);

        database.getProductDao().insertProduct(product);

    }
}