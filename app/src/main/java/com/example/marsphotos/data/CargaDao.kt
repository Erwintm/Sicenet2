package com.example.marsphotos.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.marsphotos.model.CargaAcademica

@Dao
interface CargaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCarga(materias: List<CargaAcademica>)

    @Query("SELECT * FROM carga_academica")
    fun obtenerCarga(): Flow<List<CargaAcademica>>

    @Query("DELETE FROM carga_academica")
    suspend fun limpiarTabla()
}