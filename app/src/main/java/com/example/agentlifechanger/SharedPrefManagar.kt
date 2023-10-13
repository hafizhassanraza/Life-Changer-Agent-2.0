package com.example.agentlifechanger

import android.content.Context
import android.content.SharedPreferences
import com.example.agentlifechanger.Models.AgentTransactionModel
import com.example.agentlifechanger.Models.AgentWithdrawModel
import com.example.agentlifechanger.Models.ModelAnnouncement
import com.example.agentlifechanger.Models.ModelBankAccount
import com.example.agentlifechanger.Models.ModelFA
import com.example.agentlifechanger.Models.TransactionModel
import com.example.agentlifechanger.Models.User
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class SharedPrefManagar (context: Context ){

    private val sharedPref: SharedPreferences = context.getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor = sharedPref.edit()

    fun saveUser(user: ModelFA) {
        editor.putString("User", Gson().toJson(user))
        editor.commit()
    }

    fun putToken(docID: String) {
        editor.putString("docID", docID)
        editor.commit()
    }
    fun setLogin(isLoggedIn: Boolean = false) {
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.commit()
    }

    fun isLoggedIn(): Boolean {
        var isLoggedIn:Boolean=false
        if(sharedPref.getBoolean("isLoggedIn", false).equals(null)) isLoggedIn= false
        else if(sharedPref.getBoolean("isLoggedIn", false)==true) isLoggedIn =true
        return isLoggedIn
    }
    fun isStatus(): String {
        return sharedPref.getString("status","")!!
    }
    fun setStatus(status: String) {
        editor.putString("status", status)
        editor.commit()
    }

    fun saveLoginAuth(user: ModelFA, token:String, loggedIn: Boolean){
        saveUser(user)
        putToken(token)
        setLogin(loggedIn)

    }

    fun getToken(): String {
        return sharedPref.getString("docID", "")!!
    }

    fun logOut(isLoggedOut: Boolean = false) {
        editor.putBoolean("isLoggedIn", isLoggedOut)
        editor.commit()
    }
    fun putUserList(list: List<User>) {
        editor.putString("ListUser", Gson().toJson(list))
        editor.commit()
    }

    fun getUser(): ModelFA {

        val json = sharedPref.getString("User", "") ?: ""
        return Gson().fromJson(json, ModelFA::class.java)

    }


    fun getUsersList(): List<User>{

        val json = sharedPref.getString("ListUser", "") ?: ""
        val type: Type = object : TypeToken<List<User?>?>() {}.getType()
        //return Gson().fromJson(json, type)
        return if (!json.isNullOrEmpty()) {
            Gson().fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }
fun putId(id:String)
{
    editor.putString("id",id)
    editor.commit()
}

    fun getId(): String?

    {
       return sharedPref.getString("id","")

    }



    fun putAssignedInvestor(list:List<User>)
    {
        editor.putString("AssignedInvestor",Gson().toJson(list))
        editor.commit()
    }

    fun getAssignedInvestor(): List<User> {
        val json = sharedPref.getString("AssignedInvestor", "") ?: ""
        val type: Type = object : TypeToken<List<User?>?>() {}.type
        return Gson().fromJson(json, type) ?: emptyList()
    }

    fun putInvestmentReqList(list: List<TransactionModel>) {
        editor.putString("ListInvestmentReq", Gson().toJson(list))
        editor.commit()
    }
    fun getInvestmentReqList(): List<TransactionModel>{

        val json = sharedPref.getString("ListInvestmentReq", "") ?: ""
        val type: Type = object : TypeToken<List<TransactionModel?>?>() {}.getType()
        //return Gson().fromJson(json, type)

        return if (!json.isNullOrEmpty()) {
            Gson().fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }



    fun isNomineeAdded(): Boolean {
        return sharedPref.getBoolean("IsNomineeAdded", false)!!
    }
    fun isNomineeBankAdded(): Boolean {
        return sharedPref.getBoolean("IsNomineeBankAdded", false)!!
    }
    fun isFABankAdded(): Boolean {
        return sharedPref.getBoolean("IsFABankAdded", false)!!
    }
    fun putFABank(IsFABankAdded:Boolean)
    {
        editor.putBoolean("IsFABankAdded",IsFABankAdded)
        editor.commit()
    }
    fun isUserPhotoAdded(): Boolean {
        return sharedPref.getBoolean("IsFAPhotoAdded", false)!!
    }
    fun isFACnicAdded(): Boolean {
        return sharedPref.getBoolean("IsFACnicAdded", false)!!
    }

    fun putFACnic(IsFACnicAdded:Boolean)
    {
        editor.putBoolean("IsFACnicAdded",IsFACnicAdded)
        editor.commit()
    }
    fun putFaPhoto(IsFAPhotoAdded:Boolean)
    {
        editor.putBoolean("IsFAPhotoAdded",IsFAPhotoAdded)
        editor.commit()
    }
    fun isPhoneNumberAdded(): Boolean {
        return sharedPref.getBoolean("isPhoneNumberAdded", false)!!
    }
    fun putFAPhoneNumber(isPhoneNumberAdded:Boolean)
    {
        editor.putBoolean("isPhoneNumberAdded",isPhoneNumberAdded)
        editor.commit()
    }
    fun clearWholeSharedPref()
    {
        sharedPref.edit().clear().apply()

    }


    fun getProfit(): AgentTransactionModel {
        val json = sharedPref.getString("Profit", "") ?: ""

        // If the JSON string is empty or null, return a default InvestmentModel object
        if (json.isEmpty()) {
            return AgentTransactionModel() // Replace this with your default InvestmentModel constructor
        }

        // Try to deserialize the JSON string into an InvestmentModel object
        return try {
            Gson().fromJson(json, AgentTransactionModel::class.java)
        } catch (e: JsonSyntaxException) {
            // If the deserialization fails, return a default InvestmentModel object
            AgentTransactionModel() // Replace this with your default InvestmentModel constructor
        }
    }

    fun putFaProfit(faProfitModel: AgentTransactionModel) {

        editor.putString("Profit", Gson().toJson(faProfitModel))
        editor.commit()

    }
    fun putFaBankList(list: List<ModelBankAccount>) {
        editor.putString("ListFaBanks", Gson().toJson(list))
        editor.commit()
    }
    fun getFaBankList(): List<ModelBankAccount>{

        val json = sharedPref.getString("ListFaBanks", "") ?: ""
        val type: Type = object : TypeToken<List<ModelBankAccount?>?>() {}.getType()
        return Gson().fromJson(json, type)
    }
    fun putAdminBankList(list: List<ModelBankAccount>) {
        editor.putString("ListAdminBanks", Gson().toJson(list))
        editor.commit()
    }
    fun putAnnouncement(announcement: ModelAnnouncement) {
        editor.putString("announcement", Gson().toJson(announcement))
        editor.commit()
    }
    fun getAnnouncement(): ModelAnnouncement {
        val json = sharedPref.getString("announcement", "") ?: ""

        // If the JSON string is empty or null, return a default InvestmentModel object
        if (json.isEmpty()) {
            return ModelAnnouncement() // Replace this with your default InvestmentModel constructor
        }

        // Try to deserialize the JSON string into an InvestmentModel object
        return try {
            Gson().fromJson(json, ModelAnnouncement::class.java)
        } catch (e: JsonSyntaxException) {
            // If the deserialization fails, return a default InvestmentModel object
            ModelAnnouncement() // Replace this with your default InvestmentModel constructor
        }
    }


    fun putWithdrawReqList(list: List<AgentWithdrawModel>) {
        editor.putString("ListWithdrawReq", Gson().toJson(list))
        editor.commit()
    }
    fun getWithdrawReqList(): List<AgentWithdrawModel>{

        val json = sharedPref.getString("ListWithdrawReq", "") ?: ""
        val type: Type = object : TypeToken<List<AgentWithdrawModel?>?>() {}.getType()
        //return Gson().fromJson(json, type)

        return if (!json.isNullOrEmpty()) {
            Gson().fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }
}