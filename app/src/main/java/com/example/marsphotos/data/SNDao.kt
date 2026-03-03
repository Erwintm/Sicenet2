package com.example.marsphotos.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.marsphotos.model.CalifFinal
import com.example.marsphotos.model.CargaAcademica
import com.example.marsphotos.model.Kardex
import com.example.marsphotos.model.MateriaUnidades
import com.example.marsphotos.model.ProfileStudent
import kotlinx.coroutines.flow.Flow

@Dao
interface SNDao {
    // CARGA
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCarga(materias: List<CargaAcademica>)
    @Query("SELECT * FROM carga_academica")
    fun obtenerCarga(): Flow<List<CargaAcademica>>
    @Query("DELETE FROM carga_academica")
    suspend fun borrarCarga()

    // KARDEX
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarKardex(materias: List<Kardex>)
    @Query("SELECT * FROM kardex")
    fun obtenerKardex(): Flow<List<Kardex>>
    @Query("DELETE FROM kardex")
    suspend fun borrarKardex()

    // NOTAS POR UNIDAD (NUEVO)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarNotas(notas: List<MateriaUnidades>)
    @Query("SELECT * FROM notas_unidades")
    fun obtenerNotas(): Flow<List<MateriaUnidades>>

    // CALIF FINALES (NUEVO)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarFinales(finales: List<CalifFinal>)
    @Query("SELECT * FROM calif_finales")
    fun obtenerFinales(): Flow<List<CalifFinal>>

    // PERFIL (NUEVO)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPerfil(perfil: ProfileStudent)
    @Query("SELECT * FROM perfil_estudiante WHERE matricula = :mat")
    fun obtenerPerfil(mat: String): Flow<ProfileStudent?>
}