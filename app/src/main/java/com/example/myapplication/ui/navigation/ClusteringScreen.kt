package com.example.myapplication.ui.navigation

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.algorithms.clustering.PointD

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClusteringScreen(
    onBack: () -> Unit,
    viewModel: ClusteringViewModel = viewModel()
) {
    val clusterColors = listOf(
        Color.Red, Color.Blue, Color.Green,
        Color.Magenta, Color.Cyan, Color(0xFFFF9800),
        Color(0xFF795548), Color(0xFF9C27B0), Color(0xFF009688)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Зоны еды (K-Means)") },
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Кластеров (K): ", style = MaterialTheme.typography.bodyLarge)
                IconButton(onClick = { viewModel.updateK(viewModel.k - 1) }) {
                    Text("−", fontSize = 20.sp)
                }
                Text(
                    "${viewModel.k}",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.width(32.dp),
                    textAlign = TextAlign.Center
                )
                IconButton(onClick = { viewModel.updateK(viewModel.k + 1) }) {
                    Text("+", fontSize = 20.sp)
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { viewModel.runClustering() }) {
                    Text("Кластеризовать")
                }
                OutlinedButton(onClick = { viewModel.reset() }) {
                    Text("Сбросить")
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    val places = viewModel.places
                    if(places.isEmpty()) return@Canvas

                    val xs = places.map { it.position.x }
                    val ys = places.map { it.position.y }
                    val minX = xs.min()
                    val maxX = xs.max()
                    val minY = ys.min()
                    val maxY = ys.max()
                    val rangeX = if(maxX - minX == 0.0) 1.0 else maxX - minX
                    val rangeY = if(maxY - minY == 0.0) 1.0 else maxY - minY
                    val padding = 30f

                    fun toScreen(p: PointD): Offset
                    {
                        val sx = padding + ((p.x - minX) / rangeX * (size.width - padding * 2)).toFloat()
                        val sy = padding + ((p.y - minY) / rangeY * (size.height - padding * 2)).toFloat()
                        return Offset(sx, sy)
                    }

                    if(viewModel.isClustered && viewModel.centers.isNotEmpty()){
                        viewModel.centers.forEachIndexed { i, center ->
                            val pos = toScreen(center)
                            val color = clusterColors[i % clusterColors.size]
                            drawCircle(color = color.copy(alpha = 0.2f), radius = 80f, center = pos)
                            drawCircle(color = color, radius = 8f, center = pos)
                        }
                    }

                    places.forEachIndexed { idx, place ->
                        val pos = toScreen(place.position)
                        val color = if(viewModel.isClustered && idx < viewModel.assignments.size){
                            clusterColors[viewModel.assignments[idx] % clusterColors.size]
                        } else {
                            Color.Gray
                        }
                        drawCircle(color = color, radius = 14f, center = pos)
                        drawCircle(color = Color.White, radius = 6f, center = pos)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Заведения", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            viewModel.places.forEachIndexed { idx, place ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if(viewModel.isClustered && idx < viewModel.assignments.size){
                        val color = clusterColors[viewModel.assignments[idx] % clusterColors.size]
                        Canvas(modifier = Modifier.size(12.dp)) {
                            drawCircle(color = color)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "${place.name} (кластер ${viewModel.assignments[idx] + 1})",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Canvas(modifier = Modifier.size(12.dp)) {
                            drawCircle(color = Color.Gray)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(place.name, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}