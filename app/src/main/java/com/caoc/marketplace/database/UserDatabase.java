package com.caoc.marketplace.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.caoc.marketplace.database.dao.UserDao;
import com.caoc.marketplace.database.model.User;
import com.caoc.marketplace.util.Constant;

@Database(entities = {User.class}, version=1)
public abstract class UserDatabase extends RoomDatabase {

    public abstract UserDao getUserDao();
    private static UserDatabase userDB;

    public static UserDatabase getInstance(Context context){
        if(userDB == null){
            userDB = buildDatabaseInstance(context);
        }
        return userDB;
    }

    private static UserDatabase buildDatabaseInstance(Context context){
        return Room.databaseBuilder(context,
                UserDatabase.class,
                Constant.DB_MASTER).allowMainThreadQueries().build();
    }

    public void cleanUp(){

    }

}
