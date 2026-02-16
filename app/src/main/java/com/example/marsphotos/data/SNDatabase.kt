package com.example.marsphotos.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.marsphotos.model.CargaAcademica

@Database(entities = [CargaAcademica::class], version = 1, exportSchema = false)
abstract class SNDatabase : RoomDatabase() {
    abstract fun cargaDao(): SNDao

    companion object {
        @Volatile
        private var Instance: SNDatabase? = null

        fun getDatabase(context: Context): SNDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, SNDatabase::class.java, "sicenet_db")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}