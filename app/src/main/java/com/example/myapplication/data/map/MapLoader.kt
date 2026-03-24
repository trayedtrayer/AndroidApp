package com.example.myapplication.data.map
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.core.graphics.toColor
import com.example.myapplication.data.model.Cell
import com.example.myapplication.data.model.Grid
import com.example.myapplication.data.model.Point



    object MapLoader {
        fun loadFromAssets(context: Context, fileName: String): Grid? {
            val bitmap = BitmapFactory.decodeStream(context.assets.open(fileName))
                ?: return null
            val cellsBits: MutableList<MutableList<Cell>> = mutableListOf<MutableList<Cell>>()
            for (x in 0 until bitmap.width) {
                var row: MutableList<Cell> = mutableListOf<Cell>()
                for (y in 0 until bitmap.height) {
                    row.add(Cell(Point(x, y), Color.red(bitmap.getPixel(x, y)) > 200))
                }
                cellsBits.add(row)
            }
            return Grid(cellsBits, bitmap.width, bitmap.height)
        }
    }