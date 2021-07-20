package com.caoc.marketplace;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.caoc.marketplace.database.Database;
import com.caoc.marketplace.database.model.Product;

import java.lang.ref.WeakReference;

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

        et_name = findViewById(R.id.et_name);
        et_description = findViewById(R.id.et_description);
        et_imageURL = findViewById(R.id.et_image_url);
        et_price = findViewById(R.id.et_price);
        btn_save = findViewById(R.id.btn_save_p);

        database = Database.getInstance(this);
    }

    public void save(View v){
        String name = et_name.getText().toString();
        String description = et_description.getText().toString();
        String image = et_imageURL.getText().toString();
        String price = et_price.getText().toString();

        product = new Product(name,description,image,price);

        new GetUserTask(this).execute();

    }

    private static class GetUserTask extends AsyncTask<Void, Void, Product> {

        private WeakReference<MainActivityProduct> activityWeakReference;

        GetUserTask(MainActivityProduct context){
            activityWeakReference = new WeakReference<>(context);
        }

        @Override
        protected Product doInBackground(Void... voids) {
            if(activityWeakReference != null){
                long res = activityWeakReference.get().database.getProductDao().insertProduct(activityWeakReference.get().product);
                activityWeakReference.get().product.setId(res);
                return activityWeakReference.get().product;
            }else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Product product){
            if(product.getId() == 0){
                activityWeakReference.get().saveError();
            }else{
                activityWeakReference.get().saveOk();
            }
            super.onPostExecute(product);
        }
    }

    private void saveOk() {
        Toast.makeText(this, R.string.txt_msg_add_product, Toast.LENGTH_SHORT).show();

        finish();
    }

    private void saveError() {
        Toast.makeText(this, R.string.txt_msg_add_product_fail, Toast.LENGTH_SHORT).show();
    }
}