package com.movie.myapplication.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currentAuth")
data class CurrentAuth(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int?,

    @ColumnInfo(name = "auth_id")
    val auth_id: Int?


)
