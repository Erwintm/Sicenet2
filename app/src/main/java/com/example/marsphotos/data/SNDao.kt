package com.example.marsphotos.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.marsphotos.model.CargaAcademica
import com.example.marsphotos.model.Kardex
import kotlinx.coroutines.flow.Flow

@Dao
interface SNDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCarga(materias: List<CargaAcademica>)


    @Query("SELECT * FROM carga_academica")
    fun obtenerCarga(): Flow<List<CargaAcademica>>


    @Query("DELETE FROM carga_academica")
    suspend fun borrarCarga()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarKardex(materias: List<Kardex>)

    @Query("SELECT * FROM kardex")
    fun obtenerKardex(): kotlinx.coroutines.flow.Flow<List<Kardex>>

    @Query("DELETE FROM kardex")
    suspend fun borrarKardex()
}