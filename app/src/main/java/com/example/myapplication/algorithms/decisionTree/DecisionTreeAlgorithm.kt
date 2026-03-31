package com.example.myapplication.algorithms.decisionTree

import com.example.myapplication.algorithms.decisionTree.decisionTreeNode
import kotlin.math.log2

class DecisionTreeAlgorithm {

    fun build(data: List<Map<String, String>>, targetAttribute: String): decisionTreeNode
    {
        val targetValues = data.map { it[targetAttribute]!! }
        if(targetValues.distinct().size == 1){
            return decisionTreeNode(attribute = null, result = targetValues[0])
        }
        val attributes = data[0].keys.filter { it != targetAttribute }
        if(attributes.isEmpty()){
            return decisionTreeNode(attribute = null, result = mostCommon(targetValues))
        }
        val bestAttribute = attributes.maxByOrNull { gain(data, it, targetAttribute) }!!
        val node = decisionTreeNode(attribute = bestAttribute, result = null)
        val values = data.map { it[bestAttribute]!! }.distinct()
        for(value in values){
            val subset = data
                .filter { it[bestAttribute] == value }
                .map { row ->
                    row.filter { it.key != bestAttribute }
                }
            if(subset.isEmpty()){
                node.children[value] = decisionTreeNode(
                    attribute = null,
                    result = mostCommon(targetValues)
                )
            }
            else {
                node.children[value] = build(subset, targetAttribute)
            }
        }
        return node
    }

    private fun entropy(values: List<String>): Double
    {
        val total = values.size.toDouble()
        if(total == 0.0) return 0.0
        val counts = values.groupBy { it }.mapValues { it.value.size }
        var result = 0.0
        for((_, count) in counts){
            val p = count / total
            if(p > 0){
                result -= p * log2(p)
            }
        }
        return result
    }

    private fun gain(
        data: List<Map<String, String>>,
        attribute: String,
        targetAttribute: String
    ): Double
    {
        val targetValues = data.map { it[targetAttribute]!! }
        val totalEntropy = entropy(targetValues)
        val groups = data.groupBy { it[attribute]!! }
        val total = data.size.toDouble()
        var weightedEntropy = 0.0
        for((_, group) in groups){
            val groupTargets = group.map { it[targetAttribute]!! }
            weightedEntropy += (group.size / total) * entropy(groupTargets)
        }
        return totalEntropy - weightedEntropy
    }

    private fun mostCommon(values: List<String>): String
    {
        return values.groupBy { it }
            .maxByOrNull { it.value.size }!!
            .key
    }

    fun parseCsv(csvText: String): List<Map<String, String>>
    {
        val lines = csvText.trim().lines().filter { it.isNotBlank() }
        if(lines.size < 2) return emptyList()
        val headers = lines[0].split(",").map { it.trim() }
        val data = mutableListOf<Map<String, String>>()
        for(i in 1 until lines.size){
            val values = lines[i].split(",").map { it.trim() }
            if(values.size == headers.size){
                val row = mutableMapOf<String, String>()
                for(j in headers.indices){
                    row[headers[j]] = values[j]
                }
                data.add(row)
            }
        }
        return data
    }

    fun classify(
        node: decisionTreeNode,
        input: Map<String, String>
    ): Pair<String, List<String>>
    {
        val path = mutableListOf<String>()
        var current = node
        while(current.result == null){
            val attr = current.attribute!!
            val value = input[attr] ?: "unknown"
            path.add("$attr = $value")
            val child = current.children[value]
            if(child == null){
                val fallback = current.children.values.first()
                current = fallback
            } else {
                current = child
            }
        }
        path.add("→ ${current.result}")
        return Pair(current.result!!, path)
    }
}