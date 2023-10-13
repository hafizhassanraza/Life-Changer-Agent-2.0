package com.example.agentlifechanger.UI

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.agentlifechanger.Constants
import com.example.agentlifechanger.Models.FAViewModel
import com.example.agentlifechanger.Models.AgentTransactionModel
import com.example.agentlifechanger.Models.ModelBankAccount
import com.example.agentlifechanger.Models.User
import com.example.agentlifechanger.R
import com.example.agentlifechanger.SharedPrefManagar
import com.example.agentlifechanger.Utils
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.schedule

class ActivitySplash : AppCompatActivity() {
    private val faViewModel: FAViewModel by viewModels()
    private lateinit var utils: Utils
    private lateinit var mContext: Context
    private lateinit var constants: Constants
    private lateinit var dialog : Dialog
    private lateinit var sharedPrefManager : SharedPrefManagar

    private val db = Firebase.firestore




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        mContext = this@ActivitySplash;
        sharedPrefManager = SharedPrefManagar(mContext)
        utils = Utils(mContext)
        constants = Constants()

        getUser()
getData()
        Timer().schedule(1500) {



            if(sharedPrefManager.isLoggedIn()==true)
            {

                startActivity(Intent(mContext,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                finish()

            }

            else if(sharedPrefManager.isStatus()=="pending")
            {


                startActivity(Intent(mContext,ActivityInvestorLoginDeatils::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                finish()
            }
            if(!(sharedPrefManager.isStatus()=="pending") ) {
                startActivity(Intent(mContext,ActivityLogin::class.java))
                finish()
            }
            finish()
        }


    }



        fun getUser() {
            lifecycleScope.launch {
                db.collection(constants.INVESTOR_COLLECTION)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val list = ArrayList<User>()
                            if (task.result.size() > 0) {
                                for (document in task.result) list.add(
                                    document.toObject(User::class.java).apply { id = document.id })
                                sharedPrefManager.putUserList(list)
                            }
                        } else Toast.makeText(
                            mContext,
                            constants.SOMETHING_WENT_WRONG_MESSAGE,
                            Toast.LENGTH_SHORT
                        ).show()
                    }.addOnFailureListener {
                        utils.endLoadingAnimation()
                        Toast.makeText(mContext, it.message + "", Toast.LENGTH_SHORT).show()
                    }
            }
        }



   /* suspend fun setUser(user: User): Task<Void> {
        return InvestorsCollection.document(user.id).set(user)

    }
    suspend fun getUsers(): Task<QuerySnapshot> {
        return InvestorsCollection.get()
    }*/




    fun getData(){
        lifecycleScope.launch{
            faViewModel.getAccounts()
                .addOnCompleteListener{task ->
                    utils.endLoadingAnimation()
                    if (task.isSuccessful) {
                        val listFaAccounts = ArrayList<ModelBankAccount>()
                        val listAdminAccounts = ArrayList<ModelBankAccount>()
                        if(task.result.size()>0){
                            for (document in task.result) {
                                if(document.toObject(ModelBankAccount::class.java).account_holder.equals(sharedPrefManager.getToken()))
                                    listFaAccounts.add( document.toObject(ModelBankAccount::class.java))
                            }
                            sharedPrefManager.putFaBankList(listFaAccounts)
                            sharedPrefManager.putAdminBankList(listAdminAccounts)

                        }
                    }
                    else Toast.makeText(mContext, constants.SOMETHING_WENT_WRONG_MESSAGE, Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener{
                    utils.endLoadingAnimation()
                    Toast.makeText(mContext, it.message+"", Toast.LENGTH_SHORT).show()

                }


        }
      /*  var faProfitModel=AgentTransactionModel()
        lifecycleScope.launch {
            faViewModel.getAgentProfit().addOnCompleteListener { task->
                if(task.isSuccessful)
                {
                    if (task.result.size()>0)
                    {
                        for (document in task.result) {
                            if(document.toObject(AgentTransactionModel::class.java).fa_id == sharedPrefManager.getToken()&&document.toObject(AgentTransactionModel::class.java).type == constants.PROFIT_TYPE)
                            {
                                faProfitModel=document.toObject(AgentTransactionModel::class.java)
                                break
                            }
                        }
                        sharedPrefManager.putFaProfit( faProfitModel)
                    }
                }
            }
        }*/
    }

    }