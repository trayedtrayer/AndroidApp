package com.example.myapplication.ui.common

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.data.repository.MapRepository

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MapRepository(application)
    val grid = repository.grid

    var scale by mutableStateOf(1f)
        private set
    var offset by mutableStateOf(Offset.Zero)
        private set

    fun onZoom(zoomChange: Float) {
        scale = (scale * zoomChange).coerceIn(0.5f, 5f)
    }

    fun onDrag(dragChange: Offset) {
        offset += dragChange
    }

    fun resetView() {
        scale = 1f
        offset = Offset.Zero
    }
}