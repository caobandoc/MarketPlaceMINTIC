package com.caoc.marketplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.caoc.marketplace.database.model.Product;
import com.caoc.marketplace.database.model.User;
import com.caoc.marketplace.util.Constant;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ActivityProduct extends AppCompatActivity implements OnMapReadyCallback {

    private FirebaseFirestore db;

    private TextView title;
    private TextView description;
    private TextView prices;
    private ImageView image;

    private Product prod;
    private User user;

    private GoogleMap mMap;

    private ActivityProduct myself;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        myself = this;

        db = FirebaseFirestore.getInstance();
        title = findViewById(R.id.tv_titulo);
        description = findViewById(R.id.tv_description);
        prices = findViewById(R.id.tv_price);
        image = findViewById(R.id.iv_image);

        String idProd = getIntent().getStringExtra("key");

        DocumentReference docRef = db.collection(Constant.TABLE_PRODUCT).document(idProd);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        prod = document.toObject(Product.class);
                        title.setText(prod.getName());
                        description.setText(prod.getDescription());
                        String priceR = prod.getPrice();
                        String price = "$";
                        for (int i = 0; i < priceR.length(); i++) {
                            if (price.length() > 1 && (priceR.length() - i) % 3 == 0) {
                                price = price + ".";
                            }
                            price = price + priceR.charAt(i);
                        }

                        prices.setText(price);

                        getUser(prod.getUser());
                        Glide.with(ActivityProduct.this).load(prod.getImage()).into(image);
                    } else {
                        Log.d("Firebase", "No such document");
                    }
                } else {
                    Log.d("Firebase", "get failed with ", task.getException());
                }
            }
        });
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        float latitude = Float.parseFloat(user.getLatitude());
        float longitude = Float.parseFloat(user.getLongitude());

        // Add a marker in Sydney and move the camera
        LatLng home = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(home).title("Home"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(home));
        mMap.setMinZoomPreference(15.0f);
        mMap.setMaxZoomPreference(20.0f);

    }

    public void getUser(String email) {
        DocumentReference docRef = db.collection(Constant.TABLE_USER).document(email);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user = document.toObject(User.class);
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map);
                        mapFragment.getMapAsync(myself);
                    } else {
                        Log.d("document", "No existe el usuario");
                    }
                } else {
                    Log.d("conection", "get failed with ", task.getException());
                }
            }
        });
    }
}