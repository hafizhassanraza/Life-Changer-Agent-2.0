package com.example.agentlifechanger.Models

import com.google.firebase.Timestamp


data class AgentTransactionModel @JvmOverloads constructor(
    var fa_id: String = "",
    var type: String = "", //invest, withdraw
    var salary: String = "", //invest, withdraw
    var status: String = "", // pending ,approved , reject
    var amount: String = "",
    var receiverAccountID: String = "", // account id
    var previousBalance: String = "", //
    var senderAccountID: String = "",// account id
    var id: String = "",
    var newBalance: String = "", //
    var remarks: String = "", //
    var transactionAt: Timestamp? = Timestamp.now(), // Creation timestamp
    val createdAt: Timestamp ?=null // Creation timestamp
)