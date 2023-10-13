package com.example.agentlifechanger.Models
import com.google.firebase.Timestamp
import com.google.gson.Gson

data class ProfitFA @JvmOverloads constructor(
    var id: String = "",
    var fa_id: String = "",
    var amount: String = "",
    var transactionAt: String = ""
) {

    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    companion object {
        fun fromString(profitFA: String): ProfitFA? {
            val gson = Gson()
            return try {
                gson.fromJson(profitFA, ProfitFA::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}