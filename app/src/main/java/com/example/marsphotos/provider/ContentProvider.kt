package com.example.marsphotos.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.marsphotos.data.SNDatabase
import com.example.marsphotos.model.CargaAcademica
import com.example.marsphotos.model.Kardex

class SNContentProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.example.marsphotos.provider"
        val CARGA_URI: Uri = Uri.parse("content://$AUTHORITY/carga")
        val KARDEX_URI: Uri = Uri.parse("content://$AUTHORITY/kardex")

        private const val CARGA = 1
        private const val KARDEX = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "carga", CARGA)
            addURI(AUTHORITY, "kardex", KARDEX)
        }
    }

    private lateinit var database: SNDatabase

    override fun onCreate(): Boolean {
        database = SNDatabase.getDatabase(context!!)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        // En tu SNDatabase la función se llama cargaDao()
        val dao = database.cargaDao()
        val cursor: Cursor = when (uriMatcher.match(uri)) {
            CARGA -> dao.obtenerCargaCursor()
            KARDEX -> dao.obtenerKardexCursor()
            else -> throw IllegalArgumentException("URI no soportada: $uri")
        }

        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (values == null) return null
        val dao = database.cargaDao()

        val id: Long = when (uriMatcher.match(uri)) {
            CARGA -> {
                // Mapeo con los nombres exactos de CargaAcademica.kt
                val materia = CargaAcademica(
                    Materia = values.getAsString("Materia") ?: "",
                    Docente = values.getAsString("Docente") ?: "",
                    Grupo = values.getAsString("Grupo") ?: "",
                    EstadoMateria = values.getAsString("EstadoMateria") ?: "",
                    clvOficial = values.getAsString("clvOficial") ?: "",
                    fechaSincronizacion = values.getAsString("fechaSincronizacion") ?: ""
                )
                dao.insertarCargaDesdeProvider(materia)
            }
            KARDEX -> {
                // Mapeo con los nombres exactos de Kardex.kt
                val materiaKardex = Kardex(
                    materia = values.getAsString("materia") ?: "",
                    // Cambiamos getAsInt por getAsInteger
                    calificacion = values.getAsInteger("calificacion") ?: 0,
                    acreditacion = values.getAsString("acreditacion") ?: "",
                    periodo = values.getAsString("periodo") ?: "",
                    clvMateria = values.getAsString("clvMateria") ?: "",
                    fechaSincronizacion = values.getAsString("fechaSincronizacion") ?: ""
                )
                dao.insertarKardexDesdeProvider(materiaKardex)
            }
            else -> throw IllegalArgumentException("URI desconocida: $uri")
        }

        context?.contentResolver?.notifyChange(uri, null)
        return Uri.withAppendedPath(uri, id.toString())
    }

    override fun getType(uri: Uri): String? = "vnd.android.cursor.dir/$AUTHORITY"

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, v: ContentValues?, s: String?, sa: Array<out String>?): Int = 0
}