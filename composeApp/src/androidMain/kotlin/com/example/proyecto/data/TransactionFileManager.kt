package com.example.proyecto.data

import android.content.Context
import kotlinx.serialization.json.Json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class TransactionFileManager(private val context: Context) {
    
    private val fileName = "transactions.json"
    
    private fun getFile(): File {
        return File(context.filesDir, fileName)
    }
    
    fun getAllTransactions(): Flow<List<Transaction>> = flow {
        val file = getFile()
        val transactions = if (file.exists()) {
            try {
                val json = file.readText()
                Json.decodeFromString<List<Transaction>>(json)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
        emit(transactions)
    }
    
    suspend fun addTransaction(transaction: Transaction) {
        val file = getFile()
        val transactions = if (file.exists()) {
            try {
                val json = file.readText()
                Json.decodeFromString<List<Transaction>>(json).toMutableList()
            } catch (e: Exception) {
                mutableListOf()
            }
        } else {
            mutableListOf()
        }
        
        transactions.add(transaction)
        val json = Json.encodeToString(transactions)
        file.writeText(json)
    }
    
    suspend fun deleteTransaction(id: String) {
        val file = getFile()
        val transactions = if (file.exists()) {
            try {
                val json = file.readText()
                Json.decodeFromString<List<Transaction>>(json).toMutableList()
            } catch (e: Exception) {
                mutableListOf()
            }
        } else {
            mutableListOf()
        }
        
        transactions.removeAll { it.id == id }
        val json = Json.encodeToString(transactions)
        file.writeText(json)
    }
    
    suspend fun updateTransaction(updatedTransaction: Transaction) {
        val file = getFile()
        val transactions = if (file.exists()) {
            try {
                val json = file.readText()
                Json.decodeFromString<List<Transaction>>(json).toMutableList()
            } catch (e: Exception) {
                mutableListOf()
            }
        } else {
            mutableListOf()
        }
        
        val index = transactions.indexOfFirst { it.id == updatedTransaction.id }
        if (index != -1) {
            transactions[index] = updatedTransaction
        }
        
        val json = Json.encodeToString(transactions)
        file.writeText(json)
    }
}
