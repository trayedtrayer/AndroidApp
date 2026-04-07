package com.example.myapplication.algorithms.clustering

import kotlin.math.sqrt
import kotlin.random.Random
import com.example.myapplication.algorithms.clustering.PointD

class KMeansAlgorithm {

    fun run(
        points: List<PointD>,
        k: Int,
        maxIterations: Int = 100
    ): Pair<List<Int>, List<PointD>>
    {
        if(points.isEmpty() || k <= 0) return Pair(emptyList(), emptyList())

        val rng = Random(System.currentTimeMillis())
        var centers = points.shuffled(rng).take(k).toMutableList()
        var assignments = List(points.size) { 0 }

        for(iter in 0 until maxIterations){
            val newAssignments = points.map { point ->
                centers.indices.minByOrNull { i -> distance(point, centers[i]) } ?: 0
            }

            if(newAssignments == assignments) break
            assignments = newAssignments

            val newCenters = mutableListOf<PointD>()
            for(i in 0 until k){
                val clusterPoints = points.filterIndexed { idx, _ -> assignments[idx] == i }
                if(clusterPoints.isEmpty()){
                    newCenters.add(centers[i])
                } else {
                    newCenters.add(PointD(
                        clusterPoints.map { it.x }.average(),
                        clusterPoints.map { it.y }.average()
                    ))
                }
            }
            centers = newCenters.toMutableList()
        }

        return Pair(assignments, centers)
    }

    private fun distance(a: PointD, b: PointD): Double
    {
        val dx = a.x - b.x
        val dy = a.y - b.y
        return sqrt(dx * dx + dy * dy)
    }
}