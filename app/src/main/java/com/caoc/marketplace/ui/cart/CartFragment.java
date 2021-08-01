package com.caoc.marketplace.ui.cart;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.caoc.marketplace.ActivityProduct;
import com.caoc.marketplace.R;
import com.caoc.marketplace.database.model.Product;
import com.caoc.marketplace.databinding.FragmentCartBinding;
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

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;

    private RecyclerView rv_products;
    private RecyclerView.Adapter mAdapter;

    private Activity myself;

    private ArrayList<Product> products;
    private ArrayList<String> keys;

    private FirebaseFirestore db;

    private SharedPreferences preferences;
    private JSONArray carJson;

    private TextView tv_total;

    private int total;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        myself = getActivity();

        db = FirebaseFirestore.getInstance();

        tv_total = root.findViewById(R.id.tv_total);

        preferences = myself.getSharedPreferences(Constant.PREFERENCES, myself.MODE_PRIVATE);
        String email = preferences.getString("email", null);

        if(email == null){
            Toast.makeText(myself, "ERROR AL INGRESAR EL LOGIN", Toast.LENGTH_SHORT).show();
            return root;
        }

        String fav = preferences.getString(Constant.LIST_CART, "[]");

        if(fav.equals("[]")){
            Toast.makeText(myself, R.string.msg_empty_products, Toast.LENGTH_SHORT).show();
            setTotal(total);
            return root;
        }

        try {
            carJson = new JSONArray(fav);
            keys = new ArrayList<String>();
            for (int i = 0; i < carJson.length(); i++) {
                JSONObject object = carJson.getJSONObject(i);
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
                            products = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                if(keys.contains(document.getId())){
                                    Product prod = document.toObject(Product.class);
                                    prod.setKey(document.getId());

                                    try {
                                        prod.setCount( getCount(document.getId()) );
                                    } catch (JSONException e) {
                                        prod.setCount( 1 );
                                    }
                                    products.add(prod);
                                }

                            }
                            loadProducts();
                        } else {
                            Log.d("ERROR FIREBASE:", "Error getting documents: ", task.getException());
                        }
                    }
                });


        rv_products = root.findViewById(R.id.rv_cart);
        rv_products.setLayoutManager(new LinearLayoutManager(getActivity()));

        return root;
    }

    private int getCount(String key) throws JSONException{
        for (int i = 0; i < carJson.length(); i++) {
            JSONObject object = carJson.getJSONObject(i);
            if(object.getString("key").equals(key)){
                return object.getInt(Constant.COUNT);
            }
        }

        return 1;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadProducts(){
        mAdapter = new ProductAdapter(products, myself, this);
        rv_products.setAdapter(mAdapter);

    }

    public int getTotal(){
        return total;
    }

    public void setTotal(int t){
        total = t;
        String txt = String.valueOf(t);
        String fin = "$";
        for(int i = 0; i<txt.length();i++){
            if(fin.length()>1 && (txt.length()-i)%3 == 0){
                fin = fin + ".";
            }
            fin = fin + txt.charAt(i);
        }
        tv_total.setText(fin);
    }
}

class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder>{

    private List<Product> productModelList;
    private Activity myself;
    private SharedPreferences shared;
    private CartFragment cf;

    public ProductAdapter(List<Product> productModelList, Activity myself, CartFragment cf){
        this.productModelList = productModelList;
        this.myself = myself;
        this.cf = cf;
        shared = myself.getSharedPreferences(Constant.PREFERENCES, myself.MODE_PRIVATE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cart , parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

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
        String count = String.valueOf(this.productModelList.get(position).getCount());
        String totalR = String.valueOf(Integer.parseInt(priceR) * this.productModelList.get(position).getCount());
        String total = "$";
        for(int i = 0; i<totalR.length();i++){
            if(total.length()>1 && (totalR.length()-i)%3 == 0){
                total = total + ".";
            }
            total = total + totalR.charAt(i);
        }

        //SUMATORIATOTAL:
        int t = cf.getTotal();
        int tAcum = Integer.parseInt(totalR);
        cf.setTotal(t+tAcum);

        if(holder.getCf() == null){
            holder.setCf(cf);
        }

        holder.product = this.productModelList.get(position);
        holder.key.setText(key);
        holder.name.setText(name);
        holder.description.setText(description);
        holder.price.setText(price);
        holder.count.setText(count);
        holder.total.setText(total);
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
        private TextView count;
        private TextView total;

        private Button btn_show_prod;
        private Button btn_del;
        private Button btn_add;
        private Button btn_substract;

        private CartFragment cf;

        private Context context;

        private SharedPreferences shared;

        private Product product;

        public ViewHolder(View v){
            super(v);
            context = v.getContext();

            key = v.findViewById(R.id.row_key);
            name = v.findViewById(R.id.row_titulo);
            description = v.findViewById(R.id.row_description);
            image = v.findViewById(R.id.row_image);
            price = v.findViewById(R.id.row_price);
            count = v.findViewById(R.id.tv_count);
            total = v.findViewById(R.id.row_total);

            btn_show_prod = v.findViewById(R.id.btn_show_prod);
            btn_del = v.findViewById(R.id.btn_del);
            btn_add = v.findViewById(R.id.btn_add);
            btn_substract = v.findViewById(R.id.btn_substract);

            shared = context.getSharedPreferences(Constant.PREFERENCES, context.MODE_PRIVATE);

        }

        public void setCf(CartFragment cf){
            this.cf = cf;
        }

        public CartFragment getCf(){
            return cf;
        }

        public void setOnClickListeners(){
            btn_show_prod.setOnClickListener(this);
            btn_del.setOnClickListener(this);
            btn_add.setOnClickListener(this);
            btn_substract.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int cantidad = Integer.parseInt((String) count.getText());
            switch (v.getId()){
                case R.id.btn_del:
                    delCart();
                    break;

                case R.id.btn_show_prod:
                    Intent showProd = new Intent(context, ActivityProduct.class);
                    showProd.putExtra("key", key.getText().toString());
                    context.startActivity(showProd);
                    break;
                case R.id.btn_add:
                    if(cantidad == 99){
                        Toast.makeText(context, R.string.msg_add_max, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        //Obtener datos guardados
                        String market = shared.getString(Constant.LIST_CART, "[]");
                        JSONArray jMarket = new JSONArray(market);

                        Log.d("jMarket read", jMarket.toString());

                        int index = indexObject(jMarket);

                        if(index == -1){
                            Toast.makeText(context, R.string.txt_msg_prod_del_exist, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONObject jProduct = jMarket.getJSONObject(index);
                        jProduct.put("count", String.valueOf(++cantidad));

                        updateValor(cantidad);

                        cf.setTotal(cf.getTotal()+Integer.parseInt(product.getPrice()));

                        SharedPreferences.Editor editor = shared.edit();
                        editor.putString(Constant.LIST_CART, jMarket.toString());
                        editor.commit();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case R.id.btn_substract:
                    if(cantidad == 1){
                        Toast.makeText(context, R.string.msg_substract_min, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        //Obtener datos guardados
                        String market = shared.getString(Constant.LIST_CART, "[]");
                        JSONArray jMarket = new JSONArray(market);

                        Log.d("jMarket read", jMarket.toString());

                        int index = indexObject(jMarket);

                        if(index == -1){
                            Toast.makeText(context, R.string.txt_msg_prod_del_exist, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONObject jProduct = jMarket.getJSONObject(index);
                        jProduct.put("count", String.valueOf(--cantidad));

                        updateValor(cantidad);

                        cf.setTotal(cf.getTotal()-Integer.parseInt(product.getPrice()));

                        SharedPreferences.Editor editor = shared.edit();
                        editor.putString(Constant.LIST_CART, jMarket.toString());
                        editor.commit();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                default:
                    Toast.makeText(context, "No se pudo leer el boton", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        private void updateValor(int cantidad){
            count.setText(String.valueOf(cantidad));
            int valorActual = Integer.parseInt(product.getPrice());
            String priceR = String.valueOf(valorActual * cantidad);
            StringBuilder newTotal = new StringBuilder();
            newTotal.append("$");

            for(int i = 0; i<priceR.length();i++){
                if(newTotal.length()>1 && (priceR.length()-i)%3 == 0){
                    newTotal.append(".");
                }
                newTotal.append(priceR.charAt(i));
            }

            total.setText(newTotal.toString());
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

        private void delCart(){
            try {
                //Obtener datos guardados
                String market = shared.getString(Constant.LIST_CART, "[]");
                JSONArray jMarket = new JSONArray(market);

                int index = indexObject(jMarket);

                if(index == -1){
                    Toast.makeText(context, R.string.txt_msg_prod_del_exist, Toast.LENGTH_SHORT).show();
                    return;
                }

                jMarket.remove(index);

                SharedPreferences.Editor editor = shared.edit();
                editor.putString(Constant.LIST_CART, jMarket.toString());
                editor.commit();

                Toast.makeText(context, R.string.txt_msg_prod_del, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

