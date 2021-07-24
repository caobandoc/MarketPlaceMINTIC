package com.caoc.marketplace.ui.product;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.caoc.marketplace.R;
import com.caoc.marketplace.database.Database;
import com.caoc.marketplace.database.model.Product;
import com.caoc.marketplace.databinding.FragmentProductBinding;
import com.caoc.marketplace.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ProductFragment extends Fragment {

    private ProductViewModel productViewModel;
    private FragmentProductBinding binding;

    private RecyclerView rv_products;
    private RecyclerView.Adapter mAdapter;

    private Activity myself;

    private Database database;

    private ArrayList<Product> products;

    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        binding = FragmentProductBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Root se toma como la actividad principal para el findViewById
        //getActivity() es el this

        myself = getActivity();

        database = Database.getInstance(getActivity());
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
                                Log.d("FIREBASE GET:", document.getId() + " => " + document.getData());
                                Product prod = document.toObject(Product.class);
                                products.add(prod);
                            }
                            loadProducts();
                        } else {
                            Log.d("ERROR FIREBASE:", "Error getting documents: ", task.getException());
                        }
                    }
                });


        rv_products = root.findViewById(R.id.reciclerViewProducts);
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

    /*private static class GetProductTask extends AsyncTask<Void, Void, DocumentSnapshot>{

        private WeakReference<ProductFragment> activityWeakReference;

        GetProductTask(ProductFragment context){
            activityWeakReference = new WeakReference<>(context);
        }

        @Override
        protected DocumentSnapshot doInBackground(Void... voids) {

            if(activityWeakReference != null){
                activityWeakReference.get().db.collection(Constant.TABLE_PRODUCT)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("FIREBASE GET:", document.getId() + " => " + document.getData());
                                    }
                                } else {
                                    Log.d("ERROR FIREBASE:", "Error getting documents: ", task.getException());
                                }
                            }
                        });
                return products;
            }else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Product> products){
            if(products != null && products.size() > 0){

            }
            super.onPostExecute(products);
        }
    }*/
}

class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder>{

    private List<Product> productModelList;
    private Activity myself;

    public ProductAdapter(List<Product> productModelList){
        this.productModelList = productModelList;
    }

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
        String name = this.productModelList.get(position).getName();
        String description = this.productModelList.get(position).getDescription();
        String urlImage = this.productModelList.get(position).getImage();
        String priceR = this.productModelList.get(position).getPrice();
        String price = "$";
        int cont = 0;
        for(int i = 0; i<priceR.length();i++){
            if(price.length()>1 && (priceR.length()-i)%3 == 0){
                price = price + ".";
            }
            price = price + priceR.charAt(i);
        }

        holder.name.setText(name);
        holder.description.setText(description);
        holder.price.setText(price);
        Glide.with(myself).load(urlImage).into(holder.image);

    }

    @Override
    public int getItemCount(){
        return productModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView description;
        private ImageView image;
        private TextView price;
        public ViewHolder(View v){
            super(v);
            name = (TextView) v.findViewById(R.id.row_titulo);
            description = v.findViewById(R.id.row_description);
            image = v.findViewById(R.id.row_image);
            price = v.findViewById(R.id.row_price);

        }
    }
}