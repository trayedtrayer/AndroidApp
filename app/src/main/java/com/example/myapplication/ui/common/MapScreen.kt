package com.example.myapplication.ui.common

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun MapScreen(
    mapViewModel: MapViewModel = viewModel(),
    onTap: ((x: Int, y: Int) -> Unit)? = null,
    overlayContent: DrawScope.() -> Unit = {}
) {
    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
        mapViewModel.onZoom(zoomChange)
        mapViewModel.onDrag(offsetChange)
    }
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    val cellSize = size.width / mapViewModel.grid.width.toFloat()
                    val gridX = ((tapOffset.x - mapViewModel.offset.x) / (cellSize * mapViewModel.scale)).toInt()
                    val gridY = ((tapOffset.y - mapViewModel.offset.y) / (cellSize * mapViewModel.scale)).toInt()
                    onTap?.invoke(gridX, gridY)
                }
            }
            .transformable(state = transformState)
            .graphicsLayer(
                scaleX = mapViewModel.scale,
                scaleY = mapViewModel.scale,
                translationX = mapViewModel.offset.x,
                translationY = mapViewModel.offset.y
            )
    ) {
        val cellSize = size.width / mapViewModel.grid.width
        mapViewModel.grid.cells.forEach { row ->
            row.forEach { cell ->
                drawRect(
                    color = if (cell.canBeReached) Color.White else Color.Black,
                    topLeft = Offset(cell.point.x * cellSize, cell.point.y * cellSize),
                    size = Size(cellSize + 1f, cellSize + 1f)
                )
            }
        }
        overlayContent()
    }
}