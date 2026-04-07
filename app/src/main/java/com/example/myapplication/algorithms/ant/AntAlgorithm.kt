package com.example.myapplication.algorithms.ant

import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class AntAlgorithm {

    fun run(
        start: PointA,
        coworkings: List<Coworking>,
        studentCount: Int,
        realDistances: DoubleArray? = null,
        iterations: Int = 100,
        alpha: Double = 1.0,
        beta: Double = 2.0,
        gamma: Double = 1.5,
        evaporation: Double = 0.3,
        q: Double = 100.0
    ): AntResult
    {
        val n = coworkings.size
        if(n == 0) return AntResult(emptyMap(), emptyMap(), Array(0) { DoubleArray(0) })

        val distances = realDistances ?: DoubleArray(n) { i -> distance(start, coworkings[i].position) }
        val pheromone = DoubleArray(n) { 1.0 }
        val rng = Random(System.currentTimeMillis())

        var bestAssignments = mutableMapOf<Int, Int>()
        var bestScore = Double.MAX_VALUE

        for(iter in 0 until iterations){
            val assignedCount = IntArray(n) { 0 }
            val iterAssignments = mutableMapOf<Int, Int>()

            for(ant in 0 until studentCount){
                val chosen = chooseCoworking(
                    n, distances, pheromone, coworkings,
                    assignedCount, alpha, beta, gamma, rng
                )
                iterAssignments[ant] = chosen
                assignedCount[chosen]++
            }

            val score = calcScore(iterAssignments, distances, coworkings, studentCount)
            if(score < bestScore){
                bestScore = score
                bestAssignments = iterAssignments.toMutableMap()
            }

            for(i in 0 until n){
                pheromone[i] *= (1.0 - evaporation)
            }

            for((_, coworkingIdx) in iterAssignments){
                val deposit = q / (distances[coworkingIdx] + 1.0)
                pheromone[coworkingIdx] += deposit
            }
        }

        val pheromoneMap = Array(1) { pheromone.clone() }
        val pathLengths = bestAssignments.values.distinct().associateWith { distances[it] }

        return AntResult(bestAssignments, pathLengths, pheromoneMap)
    }

    private fun chooseCoworking(
        n: Int,
        distances: DoubleArray,
        pheromone: DoubleArray,
        coworkings: List<Coworking>,
        assignedCount: IntArray,
        alpha: Double,
        beta: Double,
        gamma: Double,
        rng: Random
    ): Int
    {
        val probabilities = DoubleArray(n)
        var total = 0.0

        for(i in 0 until n){
            val tau = pheromone[i].pow(alpha)
            val eta = (1.0 / (distances[i] + 0.001)).pow(beta)
            val capacityFactor = coworkings[i].comfort.pow(gamma)
            val overloadPenalty = if(assignedCount[i] >= coworkings[i].capacity) 0.01 else 1.0
            probabilities[i] = tau * eta * capacityFactor * overloadPenalty
            total += probabilities[i]
        }

        if(total == 0.0) return rng.nextInt(n)

        val rand = rng.nextDouble() * total
        var cumulative = 0.0
        for(i in 0 until n){
            cumulative += probabilities[i]
            if(cumulative >= rand) return i
        }
        return n - 1
    }

    private fun calcScore(
        assignments: Map<Int, Int>,
        distances: DoubleArray,
        coworkings: List<Coworking>,
        studentCount: Int
    ): Double
    {
        val assignedCount = IntArray(coworkings.size) { 0 }
        for((_, c) in assignments) assignedCount[c]++

        var score = 0.0
        for((_, coworkingIdx) in assignments){
            score += distances[coworkingIdx]
        }
        for(i in coworkings.indices){
            if(assignedCount[i] > coworkings[i].capacity){
                score += (assignedCount[i] - coworkings[i].capacity) * 1000.0
            }
        }
        return score
    }

    fun distance(a: PointA, b: PointA): Double
    {
        val dx = a.x - b.x
        val dy = a.y - b.y
        return sqrt(dx * dx + dy * dy)
    }
}