package com.caoc.marketplace.ui.product;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.List;

public class ProductFragment extends Fragment {

    private ProductViewModel productViewModel;
    private FragmentProductBinding binding;

    private RecyclerView rv_products;
    private RecyclerView.Adapter mAdapter;

    private Activity myself;

    private Database database;

    private List<Product> products;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        binding = FragmentProductBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Root se toma como la activdad principal para el findViewById
        //getActivity() es el this

        myself = getActivity();

        database = Database.getInstance(getActivity());

        /*
        Product prod1 = new Product("Tarjeta Grafica","Tarjeta encargada de generar las imagenes","https://images-na.ssl-images-amazon.com/images/I/81IGSLMN16L._AC_SL1500_.jpg","1370000");
        Product prod2 = new Product("Procesador","El nucleo de una computadora","https://images-na.ssl-images-amazon.com/images/I/614Oc4WrCeL._AC_SL1500_.jpg", "900000");
        Product prod3 = new Product("Ram","Memorias de acceso rapido","https://images-na.ssl-images-amazon.com/images/I/71-cxf0ku8L._AC_SL1200_.jpg","500000");
        Product prod4 = new Product("Disipador","Viene con un ventilador para enfriar el procesador", "https://images-na.ssl-images-amazon.com/images/I/71ew8nPq9kL._AC_SL1200_.jpg","500000");
        Product prod5 = new Product("Placa madre","Tarjeta donde se conectan todos los componentes de un computador","https://images-na.ssl-images-amazon.com/images/I/81TbXzYLzUL._AC_SL1500_.jpg","500000");
        database.getProductDao().insertProduct(prod1);
        database.getProductDao().insertProduct(prod2);
        database.getProductDao().insertProduct(prod3);
        database.getProductDao().insertProduct(prod4);
        database.getProductDao().insertProduct(prod5);
        */

        new GetProductTask(ProductFragment.this).execute();


        rv_products = root.findViewById(R.id.reciclerViewProducts);
        rv_products.setLayoutManager(new LinearLayoutManager(getActivity()));
        /*
        try {
            JSONArray json = new JSONArray(text);
            mAdapter = new ProductAdapter(json, myself);
            rv_products.setAdapter(mAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        */
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

    private static class GetProductTask extends AsyncTask<Void, Void, List<Product>>{

        private WeakReference<ProductFragment> activityWeakReference;

        GetProductTask(ProductFragment context){
            activityWeakReference = new WeakReference<>(context);
        }


        @Override
        protected List<Product> doInBackground(Void... voids) {

            if(activityWeakReference != null){
                List<Product> products = activityWeakReference.get().database.getProductDao().getProduct();
                return products;
            }else {
                return null;
            }

        }

        @Override
        protected void onPostExecute(List<Product> products){
            if(products != null && products.size() > 0){
                activityWeakReference.get().products = products;
                activityWeakReference.get().loadProducts();
            }
            super.onPostExecute(products);
        }
    }
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
        holder.name.setText(name);
        holder.description.setText(description);

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
        public ViewHolder(View v){
            super(v);
            name = (TextView) v.findViewById(R.id.row_titulo);
            description = v.findViewById(R.id.row_description);
            image = v.findViewById(R.id.row_image);

        }
    }
}