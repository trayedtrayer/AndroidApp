package com.example.myapplication.data.map
import com.example.myapplication.data.model.Grid
import com.example.myapplication.data.model.Point
import com.example.myapplication.data.model.Cell

object TestGrid {
    fun createGrid(width : Int, height : Int) : Grid {
        var cells : MutableList<MutableList<Cell>> = mutableListOf<MutableList<Cell>>()
        for(x in 0 until height)
        {
            if(x!=30 && x != 0) {
                val row = mutableListOf<Cell>()
                for (y in 0 until width) {
                    row.add(Cell(Point(x, y), true))
                }
                cells.add(row)
            }
            else{
                val row = mutableListOf<Cell>()
                for (y in 0 until 5) {
                    row.add(Cell(Point(x, y), true))
                }
                for (y in 5 until width-2) {
                    row.add(Cell(Point(x, y), false))
                }
                for (y in width-2 until width) {
                    row.add(Cell(Point(x, y), true))
                }
                cells.add(row)
            }
        }
        return Grid(cells,height,width)
    }

}