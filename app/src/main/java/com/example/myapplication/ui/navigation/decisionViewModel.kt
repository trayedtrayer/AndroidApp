package com.example.myapplication.ui.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.algorithms.decisionTree.decisionTreeNode
import com.example.myapplication.algorithms.decisionTree.DecisionTreeAlgorithm

class decisionViewModel : ViewModel() {

    private val builder = DecisionTreeAlgorithm()
    private val targetAttribute = "recommended_place"

    var csvText by mutableStateOf(defaultCsv)
        private set
    var tree by mutableStateOf<decisionTreeNode?>(null)
        private set
    var treeText by mutableStateOf("")
        private set
    var attributes by mutableStateOf<List<String>>(emptyList())
        private set
    var possibleValues by mutableStateOf<Map<String, List<String>>>(emptyMap())
        private set
    var selectedValues by mutableStateOf<Map<String, String>>(emptyMap())
        private set
    var classifyResult by mutableStateOf<String?>(null)
        private set
    var classifyPath by mutableStateOf<List<String>>(emptyList())
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun updateCsv(text: String)
    {
        csvText = text
    }

    fun buildTree()
    {
        errorMessage = null
        classifyResult = null
        classifyPath = emptyList()

        val data = builder.parseCsv(csvText)
        if(data.isEmpty()){
            errorMessage = "Ошибка: пустые данные"
            return
        }

        val attrs = data[0].keys.filter { it != targetAttribute }
        if(attrs.isEmpty()){
            errorMessage = "Ошибка: нет признаков"
            return
        }

        attributes = attrs
        possibleValues = attrs.associateWith { attr ->
            data.map { it[attr]!! }.distinct()
        }
        selectedValues = attrs.associateWith { attr ->
            possibleValues[attr]!!.first()
        }

        tree = builder.build(data, targetAttribute)
        treeText = treeToString(tree!!, 0)
    }

    fun selectValue(attribute: String, value: String)
    {
        selectedValues = selectedValues.toMutableMap().apply {
            this[attribute] = value
        }
    }

    fun classify()
    {
        val currentTree = tree ?: return
        val (result, path) = builder.classify(currentTree, selectedValues)
        classifyResult = result
        classifyPath = path
    }

    private fun treeToString(node: decisionTreeNode, depth: Int): String
    {
        val indent = "  ".repeat(depth)
        if(node.result != null){
            return "$indent→ ${node.result}\n"
        }
        val sb = StringBuilder()
        sb.append("$indent[${node.attribute}?]\n")
        for((value, child) in node.children){
            sb.append("$indent  $value:\n")
            sb.append(treeToString(child, depth + 2))
        }
        return sb.toString()
    }

    companion object {
        val defaultCsv = """
location,budget,time_available,food_type,queue_tolerance,weather,recommended_place
main_building,low,medium,full_meal,medium,good,Main_Cafeteria
main_building,low,short,snack,low,good,Yarche
main_building,medium,short,coffee,low,good,Bus_Stop_Coffee
main_building,high,medium,coffee,medium,good,Starbooks
second_building,low,very_short,snack,low,good,Vending_Machine
second_building,medium,short,coffee,medium,good,Second_Building_Cafe
second_building,medium,medium,full_meal,medium,good,Main_Cafeteria
second_building,low,short,snack,low,bad,Vending_Machine
campus_center,medium,short,pancakes,medium,good,Siberian_Pancakes
        """.trimIndent()
    }
}