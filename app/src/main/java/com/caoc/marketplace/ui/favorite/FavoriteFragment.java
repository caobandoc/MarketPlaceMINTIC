package com.caoc.marketplace.ui.favorite;

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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.caoc.marketplace.ActivityProduct;
import com.caoc.marketplace.R;
import com.caoc.marketplace.database.model.Product;
import com.caoc.marketplace.databinding.FragmentFavoriteBinding;
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

public class FavoriteFragment extends Fragment {

    private FragmentFavoriteBinding binding;

    private RecyclerView rv_products;
    private RecyclerView.Adapter mAdapter;

    private Activity myself;

    private ArrayList<Product> products;
    private ArrayList<String> keys;

    private FirebaseFirestore db;

    private SharedPreferences preferences;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        myself = getActivity();

        db = FirebaseFirestore.getInstance();

        preferences = myself.getSharedPreferences(Constant.PREFERENCES, myself.MODE_PRIVATE);
        String email = preferences.getString("email", null);

        if(email == null){
            Toast.makeText(myself, "ERROR AL INGRESAR EL LOGIN", Toast.LENGTH_SHORT).show();
            return root;
        }

        String fav = preferences.getString(Constant.LIST_FAV, "[]");

        if(fav.equals("[]")){
            Toast.makeText(myself, R.string.msg_empty_favorites, Toast.LENGTH_SHORT).show();
            return root;
        }

        try {
            JSONArray favJson = new JSONArray(fav);
            keys = new ArrayList<String>();
            for (int i = 0; i < favJson.length(); i++) {
                JSONObject object = favJson.getJSONObject(i);
                if(object.getString("user").equals(email)){
                    keys.add(object.getString("key"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(myself, "ERROR EN EL JSON", Toast.LENGTH_SHORT).show();
        }

        db.collection(Constant.TABLE_PRODUCT)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            products = new ArrayList<Product>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(keys.contains(document.getId())){
                                    Product prod = document.toObject(Product.class);
                                    prod.setKey(document.getId());
                                    products.add(prod);
                                }

                            }
                            loadProducts();
                        } else {
                            Log.d("ERROR FIREBASE:", "Error getting documents: ", task.getException());
                        }
                    }
                });


        rv_products = root.findViewById(R.id.rv_favorite);
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
    public ProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_fav , parent, false);
        ProductAdapter.ViewHolder viewHolder = new ProductAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

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

        private Button btn_show_prod;
        private Button btn_del;
        private Button btn_addCart;

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

            btn_del = v.findViewById(R.id.btn_del);
            btn_show_prod = v.findViewById(R.id.btn_show_prod);
            btn_addCart = v.findViewById(R.id.btn_add_cart);

            shared = context.getSharedPreferences(Constant.PREFERENCES, context.MODE_PRIVATE);

        }

        public void setOnClickListeners(){
            btn_del.setOnClickListener(this);
            btn_show_prod.setOnClickListener(this);
            btn_addCart.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_del:
                    delFav();
                    break;

                case R.id.btn_show_prod:
                    Intent showProd = new Intent(context, ActivityProduct.class);
                    showProd.putExtra("key", key.getText().toString());
                    context.startActivity(showProd);
                    break;
                case R.id.btn_add_cart:
                    addCar();
                    break;
            }
        }

        private int indexObject(JSONArray json) throws JSONException {
            for (int i = 0; i < json.length(); i++) {
                JSONObject object = json.getJSONObject(i);
                if(object.getString("key").equals(key.getText().toString())){
                    return i;
                }
            }
            return -1;
        }

        private void delFav(){
            try {
                //Obtener datos guardados
                String market = shared.getString(Constant.LIST_FAV, "[]");
                JSONArray jMarket = new JSONArray(market);

                int index = indexObject(jMarket);

                if(index == -1){
                    Toast.makeText(context, R.string.txt_msg_prod_del_exist, Toast.LENGTH_SHORT).show();
                    return;
                }

                jMarket.remove(index);

                SharedPreferences.Editor editor = shared.edit();
                editor.putString(Constant.LIST_FAV, jMarket.toString());
                editor.commit();

                Toast.makeText(context, R.string.txt_msg_prod_del, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
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