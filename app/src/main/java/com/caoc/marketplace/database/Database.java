package com.caoc.marketplace.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.caoc.marketplace.database.dao.ProductDao;
import com.caoc.marketplace.database.dao.UserDao;
import com.caoc.marketplace.database.model.Product;
import com.caoc.marketplace.database.model.User;
import com.caoc.marketplace.util.Constant;

@androidx.room.Database(entities = {User.class, Product.class}, version=1)
public abstract class Database extends RoomDatabase {

    public abstract UserDao getUserDao();
    private static Database userDB;

    public abstract ProductDao getProductDao();

    public static Database getInstance(Context context){
        if(userDB == null){
            userDB = buildDatabaseInstance(context);
        }
        return userDB;
    }

    private static Database buildDatabaseInstance(Context context){
        return Room.databaseBuilder(context,
                Database.class,
                Constant.DB_MASTER).allowMainThreadQueries().build();
    }

    public void cleanUp(){

    }

}
