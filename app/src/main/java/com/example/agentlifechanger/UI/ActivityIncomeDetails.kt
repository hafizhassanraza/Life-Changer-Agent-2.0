package com.example.agentlifechanger.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agentlifechanger.Adapters.AdapterIncome
import com.example.agentlifechanger.Constants
import com.example.agentlifechanger.Models.AgentTransactionModel
import com.example.agentlifechanger.Models.FAViewModel
import com.example.agentlifechanger.Models.User
import com.example.agentlifechanger.R
import com.example.agentlifechanger.SharedPrefManagar
import com.example.agentlifechanger.Utils
import com.example.agentlifechanger.databinding.ActivityIncomeDetailsBinding
import kotlinx.coroutines.launch

class ActivityIncomeDetails : AppCompatActivity() ,AdapterIncome.OnItemClickListener{
private  val faViewModel: FAViewModel by viewModels()

    private lateinit var sharedPrefManager: SharedPrefManagar
    private lateinit var binding:ActivityIncomeDetailsBinding
    private lateinit var utils: Utils
    private lateinit var constants: Constants
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityIncomeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle("Income Details")
        utils= Utils(this@ActivityIncomeDetails)
        constants=Constants()
sharedPrefManager=SharedPrefManagar(this@ActivityIncomeDetails)
        binding.rvIncomeDetail.layoutManager = LinearLayoutManager(this@ActivityIncomeDetails)
        getData()

    }
    fun getData() {
        utils.startLoadingAnimation()
        lifecycleScope.launch {
            faViewModel.getFaIncome()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        utils.endLoadingAnimation()
                        val agentTransactionModel = ArrayList<AgentTransactionModel>()
                        if (task.result.size() > 0) {
                            for (document in task.result) {
                                if (document.toObject(AgentTransactionModel::class.java).fa_id == sharedPrefManager.getToken()&&document.toObject(AgentTransactionModel::class.java).type =="Profit"&&document.toObject(AgentTransactionModel::class.java).status ==constants.TRANSACTION_STATUS_APPROVED)
                                {
                                    agentTransactionModel.add(
                                        document.toObject(AgentTransactionModel::class.java)
                                    )
                                }

                            }

binding.rvIncomeDetail.adapter=AdapterIncome(agentTransactionModel.sortedByDescending { it.transactionAt })


                        } else Toast.makeText(
                            this@ActivityIncomeDetails,
                            "SOMETHING_WENT_WRONG_MESSAGE",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
                .addOnFailureListener {



                }

        }
    }
    override fun onItemClick(transactionModel: AgentTransactionModel) {

    }

    override fun onDeleteClick(transactionModel: AgentTransactionModel) {

    }
}