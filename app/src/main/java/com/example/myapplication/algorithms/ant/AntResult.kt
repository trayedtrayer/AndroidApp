package com.example.myapplication.algorithms.ant

data class AntResult(
    val assignments: Map<Int, Int>,
    val pathLengths: Map<Int, Double>,
    val pheromoneMap: Array<DoubleArray>
)