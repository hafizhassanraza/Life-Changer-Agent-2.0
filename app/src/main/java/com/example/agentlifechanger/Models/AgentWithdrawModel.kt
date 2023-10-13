package com.example.agentlifechanger.Models

import com.google.firebase.Timestamp

data class AgentWithdrawModel @JvmOverloads constructor(
    var fa_ID: String = "",
    var withdrawBalance: String = "",
    var totalWithdrawBalance: String = "",
    var status: String = "",
    var lastWithdrawBalance: String = "",
    var withdrawApprovedDate: Timestamp? = null,
    var lastWithdrawReqDate: Timestamp = Timestamp.now(),
    var id:String=""

)