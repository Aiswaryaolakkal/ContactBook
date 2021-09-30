package com.example.ContactBook.Data.Local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ContactBook.Model.Contactitem;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface TableDao {

    @Query("SELECT * FROM local_dbs")
    Single<List<Contactitem>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(LocalDb data);

    @Delete
    void delete(LocalDb data);

    @Query("DELETE FROM local_dbs WHERE id = :id")
    int deleteById(int id);

    @Update
    void update(LocalDb data);

}