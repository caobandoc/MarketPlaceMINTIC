package com.caoc.marketplace.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.caoc.marketplace.database.model.User;
import com.caoc.marketplace.util.Constant;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM " + Constant.TABLE_USER+" WHERE email = :email")
    User getUser(String email);

    @Insert
    long insertUser(User user);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);
}
