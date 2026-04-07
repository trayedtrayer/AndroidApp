package com.example.myapplication.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.ant.antScreen
import com.example.myapplication.ui.navigation.ClusteringScreen
import com.example.myapplication.ui.navigation.NavScreen
import com.example.myapplication.ui.navigation.decisionTreeScreen

data class Feature(
    val route: String,
    val title: String,
    val description: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf("home") }

    when (currentScreen) {
        "home" -> {
            Scaffold(
                topBar = {
                    TopAppBar(title = { Text("ТГУ Помощник") })
                }
            ) { padding ->
                HomeContent(
                    modifier = Modifier.padding(padding),
                    onNavigate = { currentScreen = it }
                )
            }
        }
        "navigation" -> ScreenWrapper("Навигация (A*)", { currentScreen = "home" }) {
            NavScreen()
        }
        //"neural" -> NeuralScreen(onBack = { currentScreen = "home" })
        "clustering" -> ClusteringScreen(onBack = { currentScreen = "home" })
        "genetic" -> PlaceholderScreen("Генетический алгоритм", { currentScreen = "home" })
        "ant" -> antScreen(onBack = { currentScreen = "home" })
        "decision_tree" -> decisionTreeScreen(onBack = { currentScreen = "home" })
    }
}

@Composable
fun HomeContent(modifier: Modifier = Modifier, onNavigate: (String) -> Unit) {
    val features = listOf(
        Feature("navigation", "Навигация", "Маршрут по кампусу (A*)", Icons.Default.Place),
        Feature("clustering", "Зоны еды", "Кластеризация заведений", Icons.Default.LocationOn),
        Feature("genetic", "Маршрут еды", "Сбор обеда", Icons.Default.ShoppingCart),
        Feature("ant", "Экскурсия", "Обход достопримечательностей", Icons.Default.Star),
        Feature("decision_tree", "Где поесть", "Рекомендация заведения", Icons.Default.List),
        Feature("neural", "Оценка", "Поставить оценку (0–9)", Icons.Default.Create),
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(features) { feature ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clickable { onNavigate(feature.route) },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = feature.icon,
                        contentDescription = feature.title,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(feature.title, style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center)
                    Spacer(Modifier.height(4.dp))
                    Text(feature.description, style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenWrapper(title: String, onBack: () -> Unit, content: @Composable () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding)) { content() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(title: String, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("В разработке…", style = MaterialTheme.typography.headlineMedium)
        }
    }
}