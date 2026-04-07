package com.example.myapplication.ui.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.algorithms.clustering.KMeansAlgorithm
import com.example.myapplication.algorithms.clustering.PointD

class ClusteringViewModel : ViewModel() {

    private val algorithm = KMeansAlgorithm()

    var places by mutableStateOf(defaultPlaces)
        private set
    var k by mutableStateOf(3)
        private set
    var assignments by mutableStateOf<List<Int>>(emptyList())
        private set
    var centers by mutableStateOf<List<PointD>>(emptyList())
        private set
    var isClustered by mutableStateOf(false)
        private set

    fun updateK(newK: Int)
    {
        if(newK in 1..places.size){
            k = newK
        }
    }

    fun addPlace(name: String, x: Double, y: Double)
    {
        places = places + Place(name, PointD(x, y))
    }

    fun removePlace(index: Int)
    {
        places = places.toMutableList().apply { removeAt(index) }
        isClustered = false
    }

    fun runClustering()
    {
        if(places.isEmpty()) return
        val points = places.map { it.position }
        val (a, c) = algorithm.run(points, k)
        assignments = a
        centers = c
        isClustered = true
    }

    fun reset()
    {
        assignments = emptyList()
        centers = emptyList()
        isClustered = false
    }

    data class Place(val name: String, val position: PointD)

    companion object {
        val defaultPlaces = listOf(
            Place("Starbooks", PointD(56.4715, 84.9503)),
            Place("Siberian Pancakes", PointD(56.4725, 84.9480)),
            Place("Main Cafeteria", PointD(56.4708, 84.9510)),
            Place("Yarche", PointD(56.4698, 84.9525)),
            Place("Bus Stop Coffee", PointD(56.4690, 84.9540)),
            Place("Second Building Cafe", PointD(56.4730, 84.9460)),
            Place("Vending Machine", PointD(56.4712, 84.9495)),
            Place("Buffet", PointD(56.4705, 84.9515)),
            Place("Canteen #2", PointD(56.4720, 84.9475))
        )
    }
}