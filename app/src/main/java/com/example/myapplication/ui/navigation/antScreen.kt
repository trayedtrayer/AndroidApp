package com.example.myapplication.ui.ant

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.common.MapScreen
import com.example.myapplication.ui.common.MapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun antScreen(
    onBack: () -> Unit,
    viewModel: AntViewModel = viewModel(),
    mapViewModel: MapViewModel = viewModel()
) {
    val coworkingColors = listOf(
        Color(0xFF2196F3),
        Color(0xFF4CAF50),
        Color(0xFFF44336),
        Color(0xFFFF9800),
        Color(0xFF9C27B0)
    )

    val grid = mapViewModel.grid

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Поиск коворкинга") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Количество студентов", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.updateStudentCount(viewModel.studentCount - 1) }) {
                    Text("−", fontSize = 20.sp)
                }
                Text(
                    "${viewModel.studentCount}",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.width(48.dp),
                    textAlign = TextAlign.Center
                )
                IconButton(onClick = { viewModel.updateStudentCount(viewModel.studentCount + 1) }) {
                    Text("+", fontSize = 20.sp)
                }
                Text(
                    "студентов",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Стартовая точка", style = MaterialTheme.typography.titleMedium)
                Button(
                    onClick = { viewModel.toggleSelectingStart() },
                    colors = if(viewModel.isSelectingStart)
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    else
                        ButtonDefaults.buttonColors()
                ) {
                    Text(if(viewModel.isSelectingStart) "Отмена" else "Выбрать на карте")
                }
            }

            if(viewModel.isSelectingStart){
                Spacer(Modifier.height(4.dp))
                Text(
                    "Нажмите на карту чтобы выбрать стартовую точку",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxWidth().height(350.dp)) {
                MapScreen(
                    mapViewModel = mapViewModel,
                    onTap = { x, y ->
                        if(viewModel.isSelectingStart){
                            viewModel.setStartPoint(x.toDouble(), y.toDouble())
                        }
                    },
                    overlayContent = {
                        val cellSize = size.width / mapViewModel.grid.width

                        val start = viewModel.startPoint
                        val startPos = Offset(
                            start.x.toFloat() * cellSize,
                            start.y.toFloat() * cellSize
                        )

                        viewModel.paths.forEach { (idx, path) ->
                            val color = coworkingColors[idx % coworkingColors.size]
                            val assigned = viewModel.getAssignedCount(idx)
                            val thickness = if(viewModel.result != null)
                                (assigned / viewModel.studentCount.toFloat() * 8f + 2f)
                            else 2f

                            for(i in 0 until path.size - 1){
                                val from = Offset(path[i].x * cellSize, path[i].y * cellSize)
                                val to = Offset(path[i + 1].x * cellSize, path[i + 1].y * cellSize)
                                drawLine(
                                    color = color.copy(alpha = 0.7f),
                                    start = from,
                                    end = to,
                                    strokeWidth = thickness
                                )
                            }
                        }

                        viewModel.coworkings.forEachIndexed { idx, coworking ->
                            val color = coworkingColors[idx % coworkingColors.size]
                            val pos = Offset(
                                coworking.position.x.toFloat() * cellSize,
                                coworking.position.y.toFloat() * cellSize
                            )
                            val assigned = viewModel.getAssignedCount(idx)
                            val maxAssigned = viewModel.coworkings.indices
                                .map { viewModel.getAssignedCount(it) }
                                .maxOrNull()?.toFloat() ?: 1f
                            val radius = if(viewModel.result != null && maxAssigned > 0)
                                cellSize * 3f + (assigned / maxAssigned * cellSize * 2f)
                            else
                                cellSize * 3f

                            drawCircle(color = color.copy(alpha = 0.3f), radius = radius + cellSize, center = pos)
                            drawCircle(color = color, radius = radius, center = pos)
                            drawCircle(color = Color.White, radius = radius * 0.5f, center = pos)
                        }

                        drawCircle(
                            color = Color(0xFF795548),
                            radius = cellSize * 3f,
                            center = startPos
                        )
                        drawCircle(
                            color = Color.White,
                            radius = cellSize * 1.5f,
                            center = startPos
                        )
                        drawCircle(
                            color = Color(0xFF795548),
                            radius = cellSize * 4f,
                            center = startPos,
                            style = Stroke(width = 2f)
                        )
                    }
                )
            }

            Text(
                "Старт: (${viewModel.startPoint.x.toInt()}, ${viewModel.startPoint.y.toInt()})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            Text("Доступные локации", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            viewModel.coworkings.forEachIndexed { idx, coworking ->
                val assigned = viewModel.getAssignedCount(idx)
                val color = coworkingColors[idx % coworkingColors.size]
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Canvas(modifier = Modifier.size(16.dp)) {
                            drawCircle(color = color)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(coworking.name, fontWeight = FontWeight.Bold)
                            Text(
                                "Вместимость: ${coworking.capacity} | Комфорт: ${"%.0f".format(coworking.comfort * 100)}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if(viewModel.result != null){
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "$assigned чел.",
                                    fontWeight = FontWeight.Bold,
                                    color = if(assigned > coworking.capacity)
                                        MaterialTheme.colorScheme.error
                                    else color
                                )
                                if(assigned > coworking.capacity){
                                    Text(
                                        "перегруж.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { viewModel.runAlgorithm(grid) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewModel.isRunning
            ) {
                if(viewModel.isRunning){
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Муравьи ищут место...")
                } else {
                    Text("Распределить студентов")
                }
            }

            if(viewModel.result != null){
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Распределение студентов",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        viewModel.coworkings.forEachIndexed { idx, coworking ->
                            val assigned = viewModel.getAssignedCount(idx)
                            if(assigned > 0){
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(coworking.name, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        "$assigned / ${coworking.capacity}",
                                        fontWeight = FontWeight.Bold,
                                        color = if(assigned > coworking.capacity)
                                            MaterialTheme.colorScheme.error
                                        else MaterialTheme.colorScheme.primary
                                    )
                                }
                                LinearProgressIndicator(
                                    progress = (assigned / coworking.capacity.toFloat()).coerceIn(0f, 1f),
                                    modifier = Modifier.fillMaxWidth().height(6.dp).padding(bottom = 4.dp),
                                    color = if(assigned > coworking.capacity)
                                        MaterialTheme.colorScheme.error
                                    else coworkingColors[idx % coworkingColors.size]
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}