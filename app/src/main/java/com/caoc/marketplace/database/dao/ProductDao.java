package com.caoc.marketplace.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.caoc.marketplace.database.model.Product;
import com.caoc.marketplace.util.Constant;

import java.util.List;

@Dao
public interface ProductDao {
    @Query("SELECT * FROM " + Constant.TABLE_PRODUCT)
    List<Product> getProduct();

    @Insert
    long insertProduct(Product product);

    @Update
    void updateProduct(Product product);

    @Delete
    void deleteProduct(Product product);

    @Delete
    void deleteProduct(Product... product);
}
