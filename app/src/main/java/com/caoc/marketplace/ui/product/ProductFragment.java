package com.caoc.marketplace.ui.product;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.caoc.marketplace.ActivityProduct;
import com.caoc.marketplace.R;
import com.caoc.marketplace.database.model.Product;
import com.caoc.marketplace.databinding.FragmentProductBinding;
import com.caoc.marketplace.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductFragment extends Fragment {

    private ProductViewModel productViewModel;
    private FragmentProductBinding binding;

    private RecyclerView rv_products;
    private RecyclerView.Adapter mAdapter;

    private Activity myself;

    private ArrayList<Product> products;

    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        binding = FragmentProductBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        myself = getActivity();

        //Root se toma como la actividad principal para el findViewById
        //getActivity() es el this

        db = FirebaseFirestore.getInstance();

        //new GetProductTask(ProductFragment.this).execute();
        db.collection(Constant.TABLE_PRODUCT)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            products = new ArrayList<Product>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product prod = document.toObject(Product.class);
                                prod.setKey(document.getId());
                                products.add(prod);
                            }
                            loadProducts();
                        } else {
                            Log.d("ERROR FIREBASE:", "Error getting documents: ", task.getException());
                        }
                    }
                });

        rv_products = root.findViewById(R.id.rv_products);
        rv_products.setLayoutManager(new LinearLayoutManager(getActivity()));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadProducts(){
        mAdapter = new ProductAdapter(products, myself);

        rv_products.setAdapter(mAdapter);

    }

}

class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder>{

    private List<Product> productModelList;
    private Activity myself;

    public ProductAdapter(List<Product> productModelList, Activity myself){
        this.productModelList = productModelList;
        this.myself = myself;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product , parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ProductAdapter.ViewHolder holder, int position) {
        String key = this.productModelList.get(position).getKey();
        String name = this.productModelList.get(position).getName();
        String description = this.productModelList.get(position).getDescription();
        String urlImage = this.productModelList.get(position).getImage();
        String priceR = this.productModelList.get(position).getPrice();
        String price = "$";
        for(int i = 0; i<priceR.length();i++){
            if(price.length()>1 && (priceR.length()-i)%3 == 0){
                price = price + ".";
            }
            price = price + priceR.charAt(i);
        }

        holder.key.setText(key);
        holder.name.setText(name);
        holder.description.setText(description);
        holder.price.setText(price);
        Glide.with(myself).load(urlImage).into(holder.image);

        holder.setOnClickListeners();

    }

    @Override
    public int getItemCount(){
        return productModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView name;
        private TextView description;
        private ImageView image;
        private TextView price;
        private TextView key;

        private Button btn_addProd;
        private Button btn_addFav;
        private Button btn_showProd;

        private Context context;

        private SharedPreferences shared;

        public ViewHolder(View v){
            super(v);
            context = v.getContext();

            key = v.findViewById(R.id.row_key);
            name = v.findViewById(R.id.row_titulo);
            description = v.findViewById(R.id.row_description);
            image = v.findViewById(R.id.row_image);
            price = v.findViewById(R.id.row_price);

            btn_addProd = v.findViewById(R.id.btn_add_cart);
            btn_addFav = v.findViewById(R.id.btn_fav_prod);
            btn_showProd = v.findViewById(R.id.btn_show_prod);

            shared = context.getSharedPreferences(Constant.PREFERENCES, context.MODE_PRIVATE);

        }

        public void setOnClickListeners(){
            btn_addProd.setOnClickListener(this);
            btn_addFav.setOnClickListener(this);
            btn_showProd.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_add_cart:
                    addCar();
                    break;

                case R.id.btn_show_prod:
                    Intent showProd = new Intent(context, ActivityProduct.class);
                    showProd.putExtra("key", key.getText().toString());
                    context.startActivity(showProd);
                    break;

                case R.id.btn_fav_prod:
                    addFav();
                    break;
            }
        }

        private boolean existObject(JSONArray json) throws JSONException {
            for (int i = 0; i < json.length(); i++) {
                JSONObject object = json.getJSONObject(i);
                if(object.getString("key").equals(key.getText().toString())){
                    return true;
                }
            }
            return false;
        }

        private void addFav(){
            try {
                //Obtener datos guardados
                String market = shared.getString(Constant.LIST_FAV, "[]");
                JSONArray jMarket = new JSONArray(market);

                if(existObject(jMarket)){
                    Toast.makeText(context, R.string.txt_msg_prod_add_exist, Toast.LENGTH_SHORT).show();
                    return;
                }

                //Producto a guardar en el json
                JSONObject product = new JSONObject();
                product.put("user", shared.getString("email", null));
                product.put("key", key.getText().toString());

                jMarket.put(product);

                SharedPreferences.Editor editor = shared.edit();
                editor.putString(Constant.LIST_FAV, jMarket.toString());
                editor.commit();

                Toast.makeText(context, R.string.txt_msg_prod_add, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void addCar(){
            try {
                //Obtener datos guardados
                String market = shared.getString(Constant.LIST_CART, "[]");
                JSONArray jMarket = new JSONArray(market);

                if(existObject(jMarket)){
                    Toast.makeText(context, R.string.txt_msg_prod_add_exist, Toast.LENGTH_SHORT).show();
                    return;
                }

                //Producto a guardar en el json
                JSONObject product = new JSONObject();
                product.put("user", shared.getString("email", null));
                product.put("key", key.getText().toString());
                product.put("count", 1);

                jMarket.put(product);

                SharedPreferences.Editor editor = shared.edit();
                editor.putString(Constant.LIST_CART, jMarket.toString());
                editor.commit();

                Toast.makeText(context, R.string.txt_msg_prod_add, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}