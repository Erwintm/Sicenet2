package com.example.marsphotos.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.marsphotos.model.CalifFinal
import com.example.marsphotos.model.CargaAcademica
import com.example.marsphotos.model.Kardex // Importante: Importar el modelo Kardex
import com.example.marsphotos.model.MateriaUnidades
import com.example.marsphotos.model.ProfileStudent

@Database(
    entities = [
        CargaAcademica::class,
        Kardex::class,
        MateriaUnidades::class, // Agregada
        CalifFinal::class,      // Agregada
        ProfileStudent::class// 1. Agregamos la entidad Kardex aquí
    ],
    version = 3, // 2. Subimos la versión a 2
    exportSchema = false
)
abstract class SNDatabase : RoomDatabase() {
    abstract fun cargaDao(): SNDao

    companion object {
        @Volatile
        private var Instance: SNDatabase? = null

        fun getDatabase(context: Context): SNDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, SNDatabase::class.java, "sicenet_db")
                    // 3. Esto borra la DB vieja y crea la nueva con la tabla kardex
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}