package com.caoc.marketplace.ui.product;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.caoc.marketplace.R;
import com.caoc.marketplace.databinding.FragmentProductBinding;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

public class ProductFragment extends Fragment {

    private ProductViewModel productViewModel;
    private FragmentProductBinding binding;

    private RecyclerView rv_products;
    private RecyclerView.Adapter mAdapter;

    private Activity myself;

    String text = "[{\"id\":\"1\",\"name\":\"Placa madre\",\"descripcion\":\"Tarjeta donde se conectan todos los componentes de un computador\",\"image\":\"https://images-na.ssl-images-amazon.com/images/I/81TbXzYLzUL._AC_SL1500_.jpg\",\"price\":\"500000\"},{\"id\":\"2\",\"name\":\"Procesador\",\"descripcion\":\"El nucleo de una computadora\",\"image\":\"https://images-na.ssl-images-amazon.com/images/I/614Oc4WrCeL._AC_SL1500_.jpg\",\"price\":\"900000\"},{\"id\":\"3\",\"name\":\"Ram\",\"descripcion\":\"Memorias de acceso rapido\",\"image\":\"https://images-na.ssl-images-amazon.com/images/I/71-cxf0ku8L._AC_SL1200_.jpg\",\"price\":\"500000\"},{\"id\":\"4\",\"name\":\"Disipador\",\"descripcion\":\"Viene con un ventilador para enfriar el procesador\",\"image\":\"https://images-na.ssl-images-amazon.com/images/I/71ew8nPq9kL._AC_SL1200_.jpg\",\"price\":\"500000\"}]";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        binding = FragmentProductBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Root se toma como la activdad principal para el findViewById
        //getActivity() es el this

        myself = getActivity();

        rv_products = root.findViewById(R.id.reciclerViewProducts);
        rv_products.setLayoutManager(new LinearLayoutManager(getActivity()));

        try {
            JSONArray json = new JSONArray(text);
            mAdapter = new ProductAdapter(json, myself);
            rv_products.setAdapter(mAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder>{

    private JSONArray productModelList;
    private Activity myself;

    public ProductAdapter(JSONArray productModelList){
        this.productModelList = productModelList;
    }

    public ProductAdapter(JSONArray productModelList, Activity myself){
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
        try{
            String name = this.productModelList.getJSONObject(position).getString("name");
            String description = this.productModelList.getJSONObject(position).getString("descripcion");
            String urlImage = this.productModelList.getJSONObject(position).getString("image");
            holder.name.setText(name);
            holder.description.setText(description);

            Glide.with(myself).load(urlImage).into(holder.image);

        }catch (JSONException e){
            holder.name.setText(R.string.txt_error);
        }
    }

    @Override
    public int getItemCount(){
        return productModelList.length();
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