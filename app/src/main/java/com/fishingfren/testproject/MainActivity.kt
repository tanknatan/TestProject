package com.fishingfren.testproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.random.Random

sealed class CellState(val label: String, val description: String, val icon: Int, val backgroundColor: Color) {
    class Alive : CellState("Живая", "и шевелится!", R.drawable.ic_alive, Color(0xFFFFF176))
    class Dead : CellState("Мёртвая", "или прикидывается",  R.drawable.ic_dead, Color(0xFFA5D6A7))
    class Life : CellState("Жизнь", "Ку-ку!", R.drawable.ic_life, Color(0xFFBA68C8))
}

@Composable
fun CellItem(cellState: CellState) {
    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = cellState.icon),
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .padding(8.dp)
            )
            Column {
                Text(text = cellState.label, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = cellState.description, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun CellList(cells: List<CellState>, listState: LazyListState) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxHeight()
            .padding(bottom = 72.dp) // Отступ для кнопки внизу
    ) {
        items(cells) { cell ->
            CellItem(cellState = cell)
        }
    }
}

@Composable
fun CellGenerator() {
    var cells by remember { mutableStateOf(emptyList<CellState>()) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val addCell: () -> Unit = {
        val newCellState = if (Random.nextBoolean()) CellState.Alive() else CellState.Dead()
        cells = cells + newCellState

        if (cells.size >= 3) {
            val lastThreeCells = cells.takeLast(3)
            if (lastThreeCells.all { it is CellState.Alive }) {
                cells = cells + CellState.Life()
            } else if (lastThreeCells.all { it is CellState.Dead }) {
                // Удаляем все живые клетки, предшествующие последним трём мёртвым клеткам
                cells = cells.dropLastWhile { it is CellState.Alive }
            }
        }

        coroutineScope.launch {
            listState.animateScrollToItem(cells.size - 1)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Клеточное наполнение",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(16.dp)
            )

            CellList(cells = cells, listState = listState)
        }

        Button(
            onClick = addCell,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "СОТВОРИТЬ", color = Color.White)
        }
    }
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                CellGenerator()
            }
        }
    }
}
