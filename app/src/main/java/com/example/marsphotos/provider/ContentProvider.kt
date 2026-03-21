package com.example.marsphotos.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.marsphotos.data.SNDatabase

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
        val db = database.cargaDao()
        val cursor: Cursor = when (uriMatcher.match(uri)) {
            CARGA -> db.obtenerCargaCursor()
            KARDEX -> db.obtenerKardexCursor()
            else -> throw IllegalArgumentException("URI no soportada: $uri")
        }


        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }


    override fun getType(uri: Uri): String? = "vnd.android.cursor.dir/$AUTHORITY"
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, v: ContentValues?, s: String?, sa: Array<out String>?): Int = 0
}


