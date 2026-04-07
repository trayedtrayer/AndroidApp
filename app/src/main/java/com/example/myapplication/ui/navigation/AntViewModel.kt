package com.example.myapplication.ui.ant

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.algorithms.ant.AntAlgorithm
import com.example.myapplication.algorithms.ant.AntResult
import com.example.myapplication.algorithms.ant.Coworking
import com.example.myapplication.algorithms.ant.PointA
import com.example.myapplication.algorithms.ast.AstAlgorithm
import com.example.myapplication.data.model.Grid
import com.example.myapplication.data.model.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AntViewModel : ViewModel() {

    private val algorithm = AntAlgorithm()
    private val astar = AstAlgorithm()

    var studentCount by mutableStateOf(10)
        private set
    var startPoint by mutableStateOf(defaultStart)
        private set
    var coworkings by mutableStateOf(defaultCoworkings)
        private set
    var result by mutableStateOf<AntResult?>(null)
        private set
    var isRunning by mutableStateOf(false)
        private set
    var isSelectingStart by mutableStateOf(false)
        private set
    var paths by mutableStateOf<Map<Int, List<Point>>>(emptyMap())
        private set

    fun updateStudentCount(count: Int)
    {
        if(count in 1..100){
            studentCount = count
            result = null
        }
    }

    fun toggleSelectingStart()
    {
        isSelectingStart = !isSelectingStart
    }

    fun setStartPoint(x: Double, y: Double)
    {
        startPoint = PointA(x, y)
        isSelectingStart = false
        result = null
        paths = emptyMap()
    }

    fun runAlgorithm(grid: Grid)
    {
        if(coworkings.isEmpty()) return
        isRunning = true
        result = null
        paths = emptyMap()

        viewModelScope.launch {
            val newPaths = mutableMapOf<Int, List<Point>>()
            val realDistances = DoubleArray(coworkings.size) { 0.0 }

            withContext(Dispatchers.Default) {
                val startX = startPoint.x.toInt().coerceIn(0, grid.width - 1)
                val startY = startPoint.y.toInt().coerceIn(0, grid.height - 1)
                val startCell = grid.cells[startX][startY]

                coworkings.forEachIndexed { idx, coworking ->
                    val tx = coworking.position.x.toInt().coerceIn(0, grid.width - 1)
                    val ty = coworking.position.y.toInt().coerceIn(0, grid.height - 1)
                    val targetCell = grid.cells[tx][ty]

                    val node = astar.findPath(grid, startCell, targetCell)
                    if(node != null){
                        val path = astar.reconstructPath(node)
                        newPaths[idx] = path
                        realDistances[idx] = path.size.toDouble()
                    } else {
                        val dx = startPoint.x - coworking.position.x
                        val dy = startPoint.y - coworking.position.y
                        realDistances[idx] = Math.sqrt(dx * dx + dy * dy)
                    }
                }
            }

            paths = newPaths

            val res = withContext(Dispatchers.Default) {
                algorithm.run(
                    start = startPoint,
                    coworkings = coworkings,
                    studentCount = studentCount,
                    realDistances = realDistances
                )
            }

            result = res
            isRunning = false
        }
    }

    fun getAssignedCount(coworkingIdx: Int): Int
    {
        return result?.assignments?.values?.count { it == coworkingIdx } ?: 0
    }

    companion object {
        val defaultStart = PointA(75.0, 50.0)

        val defaultCoworkings = listOf(
            Coworking("Научная библиотека", PointA(100.0, 24.0), capacity = 30, comfort = 0.9),
            Coworking("Читальный зал №1", PointA(100.0, 24.0), capacity = 20, comfort = 0.8),
            Coworking("Коворкинг ГК", PointA(100.0, 24.0), capacity = 15, comfort = 0.95),
            Coworking("Свободная аудитория 210", PointA(100.0, 24.0), capacity = 25, comfort = 0.6),
            Coworking("Коворкинг 2-й корп.", PointA(100.0, 24.0), capacity = 12, comfort = 0.85)
        )
    }
}