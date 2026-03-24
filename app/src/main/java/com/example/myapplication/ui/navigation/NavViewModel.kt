package com.example.myapplication.ui.navigation
import android.util.Log
import com.example.myapplication.algorithms.ast.AstAlgorithm
import com.example.myapplication.data.model.Point
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.myapplication.data.model.Cell
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.model.Grid

class NavViewModel : ViewModel() {
    private val _path = MutableStateFlow<List<Point>?>(null)
    val path: StateFlow<List<Point>?> = _path

    private val _start = MutableStateFlow<Cell?>(null)
    val start: StateFlow<Cell?> = _start

    private val _finish = MutableStateFlow<Cell?>(null)
    val finish: StateFlow<Cell?> = _finish

    val alg = AstAlgorithm()

    fun selectPoint(grid: Grid, x: Int, y: Int) {
        Log.d("SELECT", "FUNCTION CALLED")
        Log.d("SELECT", "x: $x, y: $y, width: ${grid.width}, height: ${grid.height}")
        if (x < 0 || x >= grid.width || y < 0 || y >= grid.height) return
        val cell = grid.cells[x][y]
        Log.d("SELECT", "canBeReached: ${cell.canBeReached}")
        if (!cell.canBeReached) return

        if (_start.value == null) {
            _start.value = cell
        } else if (_finish.value == null) {
            _finish.value = cell
            findPath(grid, _start.value!!, cell)
        } else {
            _start.value = cell
            _finish.value = null
            _path.value = null
        }
    }

    private fun findPath(grid: Grid, start: Cell, finish: Cell) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.Default) {
                alg.findPath(grid, start, finish)
            }
            _path.value = result?.let { alg.reconstructPath(it) }
        }
    }

    fun resetPath() {
        _path.value = null
        _start.value = null
        _finish.value = null
    }
}