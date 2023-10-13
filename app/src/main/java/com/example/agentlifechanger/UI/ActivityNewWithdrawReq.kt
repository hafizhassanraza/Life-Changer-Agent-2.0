package com.example.agentlifechanger.UI

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.enfotrix.lifechanger.Models.FaProfitViewModel
import com.example.agentlifechanger.Adapters.FaAccountsAdapter
import com.example.agentlifechanger.Constants
import com.example.agentlifechanger.Data.Repo
import com.example.agentlifechanger.Models.AgentTransactionModel
import com.example.agentlifechanger.Models.AgentWithdrawModel
import com.example.agentlifechanger.Models.FAViewModel
import com.example.agentlifechanger.Models.ModelBankAccount
import com.example.agentlifechanger.R
import com.example.agentlifechanger.SharedPrefManagar
import com.example.agentlifechanger.Utils
import com.example.agentlifechanger.databinding.ActivityNewWithdrawReqBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import java.sql.Timestamp

class ActivityNewWithdrawReq : AppCompatActivity(), FaAccountsAdapter.OnItemClickListener {

    private val faViewModel: FAViewModel by viewModels()
    private lateinit var binding: ActivityNewWithdrawReqBinding
    private val faProfitViewModel: FaProfitViewModel by viewModels()

    private lateinit var utils: Utils
    private lateinit var mContext: Context
    private lateinit var constants: Constants
    private lateinit var sharedPrefManager: SharedPrefManagar
    private lateinit var dialog: BottomSheetDialog
    private lateinit var confirmationDialog: Dialog
    private lateinit var repo: Repo
    private lateinit var adapter: FaAccountsAdapter
    private lateinit var agentWithdrawModel: AgentWithdrawModel

    private var accountID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewWithdrawReqBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this@ActivityNewWithdrawReq
        utils = Utils(mContext)
        repo = Repo(mContext)
        agentWithdrawModel= AgentWithdrawModel()
        constants = Constants()
        sharedPrefManager = SharedPrefManagar(mContext)
        setTitle("Add Withdraw Request")

        // Initialize the confirmationDialog
        confirmationDialog = Dialog(mContext)
        confirmationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        confirmationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        confirmationDialog.setContentView(R.layout.dialog_check_withdraw_info)
        binding.layInvestorAccountSelect.setOnClickListener { getInvestorAccounts() }
        binding.layBalance.setOnClickListener { showAddBalanceDialog() }
        binding.btnWithdraw.setOnClickListener {
            val balance = binding.tvBalance.text.toString()
            var bankTitle=binding.tvAccountTittle.text.toString()
            var accountNumber=binding.tvAccountNumber.text.toString()
            if(sharedPrefManager.getProfit().newBalance.equals(""))
            {
                Toast.makeText(mContext, "Something went Wrong", Toast.LENGTH_SHORT).show()
            }
            else
            {
                if(sharedPrefManager.getProfit().newBalance.toInt()< (binding.tvBalance.text.toString()).toInt())
                    Toast.makeText(this@ActivityNewWithdrawReq, "Insufficient Balance", Toast.LENGTH_SHORT).show()
                else  if (balance.isEmpty() || balance.toDoubleOrNull() == 0.0) {
                    Toast.makeText(mContext, "Please enter a valid balance", Toast.LENGTH_SHORT).show()
                }
                else if(bankTitle.isEmpty()||bankTitle.equals("Account Tittle")){

                    Toast.makeText(mContext, "Please Enter Account Details", Toast.LENGTH_SHORT).show()
                }



                else {
                    showConfirmationDialog()
                }
            }
        }
    }

    fun addWithdrawReq(transactionModel: AgentWithdrawModel) {
        utils.startLoadingAnimation()
            lifecycleScope.launch {
                faProfitViewModel.addTransactionReq(transactionModel).observe(this@ActivityNewWithdrawReq) {
                    utils.endLoadingAnimation()
                    if (it == true) {
                        Toast.makeText(mContext, "Request Submitted Successfully!1111111", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(mContext, constants.SOMETHING_WENT_WRONG_MESSAGE, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


    fun showAddBalanceDialog() {
        val dialog = Dialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_add_balance)

        val etBalance = dialog.findViewById<EditText>(R.id.etBalance)
        val btnAddBalance = dialog.findViewById<Button>(R.id.btnAddBalance)

        btnAddBalance.setOnClickListener {
            dialog.dismiss()
            binding.tvBalance.text = etBalance.text
        }

        dialog.show()
    }

    fun getInvestorAccounts() {
        dialog = BottomSheetDialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_bottom_sheet_accounts)

        val rvInvestorAccounts: RecyclerView = dialog.findViewById<RecyclerView>(R.id.rvInvestorAccounts) as RecyclerView
        rvInvestorAccounts.layoutManager = LinearLayoutManager(mContext)
        rvInvestorAccounts.adapter = faViewModel.getFaAccountsAdapter(constants.FROM_NEW_WITHDRAW_REQ, this@ActivityNewWithdrawReq)

        dialog.show()
    }

    override fun onItemClick(modelBankAccount: ModelBankAccount) {
        dialog.dismiss()

        binding.tvAccountNumber.text = modelBankAccount.account_number
        binding.tvBankName.text = modelBankAccount.bank_name
        binding.tvAccountTittle.text = modelBankAccount.account_tittle
        accountID = modelBankAccount.docID
    }

    override fun onDeleteClick(modelBankAccount: ModelBankAccount) {
        // Handle the delete action here if needed
    }

    private fun showConfirmationDialog() {
        // Populate the confirmationDialog with required information
        val tvSenderAccountName = confirmationDialog.findViewById<TextView>(R.id.tvsender_account_name)
        val tvSenderAccountNumber = confirmationDialog.findViewById<TextView>(R.id.tvsender_account_number)
        val tvPhone = confirmationDialog.findViewById<TextView>(R.id.tvPhone)
        val tvAmount = confirmationDialog.findViewById<TextView>(R.id.tvamount)
        val btnCancel = confirmationDialog.findViewById<Button>(R.id.btnCancel)
        val btnSave = confirmationDialog.findViewById<Button>(R.id.btnsave)
        tvSenderAccountName?.text = binding.tvBankName.text.toString()
        tvSenderAccountNumber?.text = binding.tvAccountNumber.text.toString()
        tvPhone?.text = binding.tvPhone.text.toString()
        tvAmount?.text = binding.tvBalance.text.toString()

        btnCancel.setOnClickListener {
            confirmationDialog.dismiss()
        }












        btnSave.setOnClickListener {
            addWithdrawReq(
                AgentWithdrawModel(
                    sharedPrefManager.getToken(),
                    binding.tvBalance.text.toString(),
                 sharedPrefManager.getProfit().newBalance,
                    constants.TRANSACTION_STATUS_PENDING
                )
            )
            confirmationDialog.dismiss()
        }
        confirmationDialog.show()
    }
}