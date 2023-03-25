package com.movie.myapplication.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "authTable")
data class Auth(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "auth_id")
    val id: Int?,

    @ColumnInfo(name = "user_fname")
    val user_fname: String?,

    @ColumnInfo(name = "user_lname")
    val user_lname: String?,

    @ColumnInfo(name = "user_age")
    val user_age: String?,

    @ColumnInfo(name = "user_gender")
    val user_gender: String?,

    @ColumnInfo(name = "user_email")
    val user_email: String?,

    @ColumnInfo(name = "user_phone_no")
    val user_phone_no: String?,

    @ColumnInfo(name = "user_password")
    val user_password: String?,

    @ColumnInfo(name = "is_Active")
    var is_Active: Boolean=false


)
