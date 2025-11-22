package com.example.proyecto.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.Transaction
import com.example.proyecto.data.TransactionFileManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FinanceViewModel(context: Context) : ViewModel() {
    
    private val manager = TransactionFileManager(context)
    
    val allTransactions: Flow<List<Transaction>> = manager.getAllTransactions()
    
    val totalExpenses: Flow<Double> = allTransactions.map { transactions ->
        transactions.filter { it.type == "expense" }
            .sumOf { it.amount }
    }
    
    val totalIncome: Flow<Double> = allTransactions.map { transactions ->
        transactions.filter { it.type == "income" }
            .sumOf { it.amount }
    }
    
    val balance: Flow<Double> = allTransactions.map { transactions ->
        val income = transactions.filter { it.type == "income" }.sumOf { it.amount }
        val expenses = transactions.filter { it.type == "expense" }.sumOf { it.amount }
        income - expenses
    }
    
    fun addTransaction(
        description: String,
        amount: Double,
        category: String,
        type: String
    ) {
        viewModelScope.launch {
            val transaction = Transaction(
                description = description,
                amount = amount,
                category = category,
                type = type,
                date = System.currentTimeMillis()
            )
            manager.addTransaction(transaction)
        }
    }
    
    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            manager.deleteTransaction(id)
        }
    }
    
    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            manager.updateTransaction(transaction)
        }
    }
}
