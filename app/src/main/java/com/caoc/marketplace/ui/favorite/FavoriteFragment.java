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

    private FavoriteViewModel slideshowViewModel;
    private FragmentFavoriteBinding binding;

    private RecyclerView rv_products;
    private RecyclerView.Adapter mAdapter;

    private Activity myself;

    private ArrayList<Product> products;

    private FirebaseFirestore db;

    private SharedPreferences preferences;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        slideshowViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);

        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        myself = getActivity();

        db = FirebaseFirestore.getInstance();

        preferences = myself.getSharedPreferences(Constant.PREFERENCES, myself.MODE_PRIVATE);

        String fav = preferences.getString(Constant.ADD_FAV, "[]");
        JSONArray favJson;
        try {
            favJson = new JSONArray(fav);
            for (int i = 0; i < favJson.length(); i++) {
                JSONObject object = favJson.getJSONObject(i);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }



        //Crear array de keys productos

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
    public ProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product , parent, false);
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

            shared = context.getSharedPreferences(Constant.PREFERENCES, context.MODE_PRIVATE);

        }

        public void setOnClickListeners(){
            btn_del.setOnClickListener(this);
            btn_show_prod.setOnClickListener(this);
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

        private void delFav(){
            try {
                //Obtener datos guardados
                String market = shared.getString(Constant.ADD_CART, "[]");
                JSONArray jMarket = new JSONArray(market);

                if(existObject(jMarket)){
                    Toast.makeText(context, R.string.txt_msg_prod_add_exist, Toast.LENGTH_SHORT).show();
                    return;
                }

                //Producto a eliminar en el json
                /*
                JSONObject product = new JSONObject();
                product.put("user", shared.getString("email", null));
                product.put("key", key.getText().toString());
                product.put("count", 1);

                jMarket.put(product);

                SharedPreferences.Editor editor = shared.edit();
                editor.putString(Constant.ADD_CART, jMarket.toString());
                editor.commit();
                */
                Toast.makeText(context, R.string.txt_msg_prod_add, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}