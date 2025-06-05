package com.example.herometrics.ui.screens.busqueda

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.herometrics.data.DataFirebase
import com.example.herometrics.data.DataPlayer
import com.google.firebase.firestore.Query

class BusquedaViewModel(
    private val dataFirebase: DataFirebase
) : ViewModel() {

    private val _busqueda = MutableLiveData<String>()
    val busqueda: LiveData<String> = _busqueda

    private val _recientes = MutableLiveData<List<DataPlayer>>()
    val recientes: LiveData<List<DataPlayer>> = _recientes

    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog: LiveData<Boolean> = _showDialog

    private val _errorDialog = MutableLiveData<String>()
    val errorDialog: LiveData<String> = _errorDialog

    fun setBusqueda(valor: String) {
        _busqueda.value = valor
    }

    fun cargarBusquedasRecientes() {
        var query: Query = dataFirebase.db.collection("busquedas")
        query.get()
            .addOnSuccessListener { snapshot ->
                val listaPlayers = snapshot.documents.map { doc ->
                    DataPlayer(
                        nombre = doc.getString("nombre") ?: "",
                        servidor = doc.getString("servidor") ?: "",
                        region = doc.getString("region") ?: ""
                    )
                }
                _recientes.value = listaPlayers
            }
            .addOnFailureListener { error ->
               Log.d("busqueda","Error al cargar ${error.message}")
            }
    }
}