package com.example.myapplication.data.repository

import android.content.Context
import com.example.myapplication.data.map.MapLoader
import com.example.myapplication.data.map.TestGrid
import com.example.myapplication.data.model.Grid

class MapRepository(private val context: Context) {
    val grid: Grid by lazy {
        MapLoader.loadFromAssets(context, "map.png")
            ?: TestGrid.createGrid(150, 100)
    }
}