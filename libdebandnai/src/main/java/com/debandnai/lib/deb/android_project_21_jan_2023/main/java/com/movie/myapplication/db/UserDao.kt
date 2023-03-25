package com.movie.myapplication.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.movie.myapplication.db.entity.Auth
import com.movie.myapplication.db.entity.CurrentAuth

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserDetails(auth: Auth)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(currentAuth: CurrentAuth)

    @Query("Select * from currentAuth ")
    fun getSessionDetails(): LiveData<List<CurrentAuth>>

    @Query("Select * from authTable order by auth_id ASC")
    fun getUserDetails(): LiveData<List<Auth>>



    @Update
    suspend fun updateAuth(auth: Auth)

    @Query("DELETE FROM currentAuth")
    fun logoutAllUser()


}