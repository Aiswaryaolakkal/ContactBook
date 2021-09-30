package com.example.ContactBook.Data.Local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {LocalDb.class}, version = 1)
public abstract class AppDatabase  extends RoomDatabase {
    public abstract TableDao taskDao();
}