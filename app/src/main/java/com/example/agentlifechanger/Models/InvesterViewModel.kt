package com.example.agentlifechanger.Models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.agentlifechanger.Adapters.TransactionsAdapter
import com.example.agentlifechanger.Constants
import com.example.agentlifechanger.Data.Repo
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.example.agentlifechanger.SharedPrefManagar


class InvesterViewModel(context: Application) : AndroidViewModel(context) {

    private val userRepo = Repo(context)
    private val sharedPrefManager= SharedPrefManagar(context)

    private var constants= Constants()


    suspend fun getInvestors():Task<QuerySnapshot>
    {
        return  userRepo.getInvestors()
    }

    suspend fun getInvestement():Task<QuerySnapshot>
    {
        return  userRepo.getInvestement()
    }


    fun getApprovedInvestmentReqAdapter(): TransactionsAdapter {
        return TransactionsAdapter(sharedPrefManager.getInvestmentReqList().filter{ it.status.equals(constants.TRANSACTION_STATUS_APPROVED) }.sortedByDescending { it.createdAt })

    }
    suspend fun getInvestmentReq(token: String): Task<QuerySnapshot> {
        return userRepo.getTransactionReq(token)
    }  suspend fun getInvestmentAll(): Task<QuerySnapshot> {
        return userRepo.getInvestmentAll()
    }
    suspend fun getProfitReq(token: String): Task<QuerySnapshot> {
        return userRepo.getProfitReq(token)
    } suspend fun getWithdrawReq(token: String): Task<QuerySnapshot> {
        return userRepo.getWithdrawReq(token)
    } suspend fun getTaxReq(token: String): Task<QuerySnapshot> {
        return userRepo.getTaxReq(token)
    }


}