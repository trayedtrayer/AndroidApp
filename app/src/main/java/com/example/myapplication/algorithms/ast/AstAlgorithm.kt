package com.example.myapplication.algorithms.ast

import android.util.Log
import com.example.myapplication.data.model.Grid
import com.example.myapplication.data.model.Point
import com.example.myapplication.data.model.Cell
import kotlin.math.abs

class AstAlgorithm {
    fun checkBreak(a : Int, b : Int, grid : Grid) : Boolean
    {
        if(a < grid.width && a >= 0 && b < grid.height && b >= 0){return true}
        else{return false}
    }
    fun findPath(grid: Grid,start : Cell,finish : Cell)
    : AstNode?
    {
        Log.d("ASTAR", "start: ${start.point}, finish: ${finish.point}, grid: ${grid.width}x${grid.height}")
        var openList : MutableList<AstNode> = mutableListOf<AstNode>()
        openList.add(AstNode(start,0,heurisitic(start.point,finish.point),null))
        var closedList : MutableList<AstNode> = mutableListOf<AstNode>()
        while (openList.count()>0){
            val cur = openList.minByOrNull { it.sumDist} ?: return null
            if(cur.cell == finish){return cur}
            openList.remove(cur)
            closedList.add(cur)
            var neigh = GetNeighbours(grid, cur, finish.point)
            neigh.forEach { neighbor ->
                if(closedList.none {it.cell == neighbor.cell} && openList.none {it.cell == neighbor.cell} && neighbor.cell.canBeReached) {
                    neighbor.parent = cur
                    openList.add(neighbor)
                }
            }
        }
        Log.d("ASTAR", "path not found")
        return null;
    }
    private fun heurisitic(a : Point, b : Point) : Int
    {
        return abs(a.x-b.x) + abs(a.y-b.y)
    }
    fun GetNeighbours(grid: Grid, node: AstNode, finish: Point) : List<AstNode>
    {
        val points : MutableList<AstNode> = mutableListOf<AstNode>()
        if(checkBreak(node.cell.point.x,node.cell.point.y+1,grid))
        {
            val pointTop = grid.cells[node.cell.point.x][node.cell.point.y+1]
            if(pointTop.canBeReached)
            {
                val left : Int = heurisitic(pointTop.point, finish)
                points.add(AstNode(pointTop, node.distWent+1,left))
            }
        }
        if(checkBreak(node.cell.point.x-1,node.cell.point.y,grid))
        {
            val pointLeft = grid.cells[node.cell.point.x-1][node.cell.point.y]
            if(pointLeft.canBeReached)
            {
                val left : Int = heurisitic(pointLeft.point, finish)
                points.add(AstNode(pointLeft, node.distWent+1,left))
            }
        }
        if(checkBreak(node.cell.point.x+1,node.cell.point.y,grid))
        {
            val pointRight = grid.cells[node.cell.point.x+1][node.cell.point.y]
            if(pointRight.canBeReached)
            {
                val left : Int = heurisitic(pointRight.point, finish)
                points.add(AstNode(pointRight, node.distWent+1,left))
            }
        }
        if(checkBreak(node.cell.point.x,node.cell.point.y-1,grid))
        {
            val pointBot = grid.cells[node.cell.point.x][node.cell.point.y-1]
            if(pointBot.canBeReached)
            {
                val left : Int = heurisitic(pointBot.point, finish)
                points.add(AstNode(pointBot, node.distWent+1,left))
            }
        }
        return points
    }
    fun reconstructPath(node: AstNode) : List<Point>
    {
        var listWent : MutableList<Point> = mutableListOf<Point>()
        var tempNode : AstNode? = node
        while(tempNode?.parent!=null)
        {
            listWent.add(tempNode.cell.point)
            tempNode = tempNode.parent
        }
        return listWent
    }
}