package com.example.myapplication.ui.navigation
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.common.MapScreen
import com.example.myapplication.ui.common.MapViewModel

@Composable
fun NavScreen(
    navViewModel: NavViewModel = viewModel(),
    mapViewModel: MapViewModel = viewModel()
) {
    val path by navViewModel.path.collectAsState()
    val start by navViewModel.start.collectAsState()
    val finish by navViewModel.finish.collectAsState()
    val grid = mapViewModel.grid

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            MapScreen(
                mapViewModel = mapViewModel,
                onTap = { x, y ->
                    Log.d("TAP_NAV", "received: $x, $y")
                    navViewModel.selectPoint(grid, x, y)
                },
                overlayContent = {
                    val cellSize = size.width / grid.width
                    path?.forEach { point ->
                        drawRect(
                            color = Color.Blue,
                            topLeft = Offset(point.x * cellSize, point.y * cellSize),
                            size = Size(cellSize + 1f, cellSize + 1f)
                        )
                    }
                    start?.let { cell ->
                        drawRect(
                            color = Color.Green,
                            topLeft = Offset(cell.point.x * cellSize, cell.point.y * cellSize),
                            size = Size(cellSize + 1f, cellSize + 1f)
                        )
                    }
                    finish?.let { cell ->
                        drawRect(
                            color = Color.Red,
                            topLeft = Offset(cell.point.x * cellSize, cell.point.y * cellSize),
                            size = Size(cellSize + 1f, cellSize + 1f)
                        )
                    }
                }
            )
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { navViewModel.resetPath() }
        ) {
            Text("Сбросить")
        }
    }
}