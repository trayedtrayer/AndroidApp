package com.example.myapplication.algorithms.decisionTree

data class decisionTreeNode(val attribute: String?,
                            val result: String?,
                            val children: MutableMap<String, decisionTreeNode> = mutableMapOf())