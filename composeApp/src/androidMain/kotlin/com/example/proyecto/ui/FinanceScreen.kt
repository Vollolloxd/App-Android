package com.example.proyecto.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto.data.Transaction
import com.example.proyecto.viewmodel.FinanceViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FinanceApp(viewModel: FinanceViewModel) {
    var showAddForm by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar ={
            TopAppBar(
                title = { Text("💰 Gestor de Finanzas") },
                backgroundColor = Color(0xFF2196F3)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SummarySection(viewModel)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { showAddForm = !showAddForm },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            ) {
                Text(if (showAddForm) "Cancelar" else "➕ Agregar Transacción")
            }
            
            if (showAddForm) {
                AddTransactionForm(viewModel) {
                    showAddForm = false
                }
            }
            
            TransactionList(viewModel)
        }
    }
}

@Composable
fun SummarySection(viewModel: FinanceViewModel) {
    val totalIncome by viewModel.totalIncome.collectAsState(initial = 0.0)
    val totalExpenses by viewModel.totalExpenses.collectAsState(initial = 0.0)
    val balance by viewModel.balance.collectAsState(initial = 0.0)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("RESUMEN", fontSize = 18.sp, modifier = Modifier.padding(8.dp))
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SummaryItem("Ingresos", totalIncome, Color.Green)
                SummaryItem("Gastos", totalExpenses, Color.Red)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "Balance: \$${"%.2f".format(balance)}",
                fontSize = 20.sp,
                color = if (balance >= 0) Color.Green else Color.Red,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun SummaryItem(label: String, amount: Double, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 12.sp)
        Text(
            "\$${"%.2f".format(amount)}",
            fontSize = 16.sp,
            color = color
        )
    }
}

@Composable
fun AddTransactionForm(viewModel: FinanceViewModel, onComplete: () -> Unit) {
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("expense") }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Nueva Transacción", fontSize = 16.sp, modifier = Modifier.padding(8.dp))
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Monto") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Categoría (ej: Comida, Transporte)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { type = "income" },
                    modifier = Modifier.weight(1f).padding(4.dp),
                    backgroundColor = if (type == "income") Color.Green else Color.LightGray
                ) {
                    Text("Ingreso")
                }
                
                Button(
                    onClick = { type = "expense" },
                    modifier = Modifier.weight(1f).padding(4.dp),
                    backgroundColor = if (type == "expense") Color.Red else Color.LightGray
                ) {
                    Text("Gasto")
                }
            }
            
            Button(
                onClick = {
                    if (description.isNotEmpty() && amount.isNotEmpty() && category.isNotEmpty()) {
                        viewModel.addTransaction(
                            description = description,
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            category = category,
                            type = type
                        )
                        onComplete()
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            ) {
                Text("Guardar")
            }
        }
    }
}

@Composable
fun TransactionList(viewModel: FinanceViewModel) {
    val transactions by viewModel.allTransactions.collectAsState(initial = emptyList())
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        items(transactions) { transaction ->
            TransactionItem(transaction, viewModel)
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, viewModel: FinanceViewModel) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(transaction.date))
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 2.dp,
        backgroundColor = if (transaction.type == "income") Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    fontSize = 14.sp
                )
                Text(
                    text = "${transaction.category} - $formattedDate",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Text(
                text = "\$${transaction.amount}",
                fontSize = 14.sp,
                color = if (transaction.type == "income") Color.Green else Color.Red
            )
            
            IconButton(
                onClick = { viewModel.deleteTransaction(transaction.id) },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.Red
                )
            }
        }
    }
}
