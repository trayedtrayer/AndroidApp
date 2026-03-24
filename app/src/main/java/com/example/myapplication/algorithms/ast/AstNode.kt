package com.example.myapplication.algorithms.ast
import com.example.myapplication.data.model.Cell

class AstNode (
    val cell: Cell,
    var distWent : Int,
    var distLeft : Int,
    var parent: AstNode? = null
){
    val sumDist get() = distLeft + distWent
}