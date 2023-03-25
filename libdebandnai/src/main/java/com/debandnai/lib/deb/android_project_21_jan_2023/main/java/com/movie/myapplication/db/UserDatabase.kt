package com.movie.myapplication.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.movie.myapplication.db.entity.Auth
import com.movie.myapplication.db.entity.CurrentAuth


@Database(entities = [Auth::class, CurrentAuth::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {
    abstract fun getAuthDso(): UserDao
}