package com.example.agentlifechanger.UI

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.gson.Gson
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.agentlifechanger.Adapters.TransactionsAdapter
import com.example.agentlifechanger.Constants
import com.example.agentlifechanger.Data.Repo
import com.example.agentlifechanger.Models.InvesterViewModel
import com.example.agentlifechanger.Models.ModelFA
import com.example.agentlifechanger.Models.ProfitFA
import com.example.agentlifechanger.Models.SavePdf
import com.example.agentlifechanger.Models.TransactionModel
import com.example.agentlifechanger.Models.User
import com.example.agentlifechanger.R
import com.example.agentlifechanger.SharedPrefManagar
import com.example.agentlifechanger.Utils
import com.example.agentlifechanger.databinding.ActivityInvestorDetailBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ActivityInvestorDetail : AppCompatActivity() {

    private lateinit var repo: Repo
    private val CREATE_PDF_REQUEST_CODE = 123
    private lateinit var mContext: Context
    private lateinit var binding: ActivityInvestorDetailBinding
    private lateinit var sharedPrefManager: SharedPrefManagar
    private lateinit var utils: Utils
    private lateinit var user: User
    private var constants = Constants()
    private val db = Firebase.firestore
    private lateinit var modelFA: ModelFA

    private lateinit var transaction: ArrayList<TransactionModel>


    private val investerViewModel: InvesterViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvestorDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this@ActivityInvestorDetail
        utils = Utils(mContext)
        sharedPrefManager = SharedPrefManagar(mContext)
        transaction = ArrayList<TransactionModel>()
        modelFA = ModelFA()

        repo = Repo(mContext)
        var json = intent.getStringExtra("Investor")
        var gson = Gson()
        user = gson.fromJson(json, User::class.java)
        setData()
        getInestment()

        binding.saveAsPdf.setOnClickListener {
            generatePDF()
        }



        binding.rvWithdrawApproved.layoutManager = LinearLayoutManager(mContext)

    }

    fun setData() {

        binding.tvInvestorName.text = user.firstName
        binding.tvInvestorPhoneNumber.text = user.phone
        binding.tvInvestorCnic.text = user.cnic
        binding.tvInvestordate.text =
            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(user.createdAt.toDate())
        Glide.with(mContext)
            .load(user.photo)
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_background)
            .into(binding.imageView)

    }

    private fun openDialer(phoneNumber: String) {
        val uri = Uri.parse("tel:$phoneNumber")
        val intent = Intent(Intent.ACTION_DIAL, uri)
        startActivity(intent)
    }


    /* private fun getInvestorAmount() {
        // TODO("Not yet implemented")
     }*/


    private fun getProfit() {
        binding.tvWithdraw.text="Profit"
        binding.tvInvestment.text = ""
        binding.tvallblance.text="Profit"
        var investement: Int = 0
        utils.startLoadingAnimation()
        lifecycleScope.launch {
            investerViewModel.getProfitReq(user.id)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        val list = ArrayList<TransactionModel>()
                        if (task.result.size() > 0) {
                            for (document in task.result) {
                             /*   transaction.add(
                                    document.toObject(
                                        TransactionModel::class.java
                                    )
                                )*/
                                list.add(
                                    document.toObject(
                                        TransactionModel::class.java
                                    )
                                )


                            }

                            binding.rvWithdrawApproved.adapter =
                                TransactionsAdapter(list.sortedByDescending { it.createdAt })
                            for (investment in list.sortedByDescending { it.createdAt }) {

                                if (investment.newBalance == "") {
                                    continue
                                } else {

                                    investement = investment.newBalance.toInt()
                                    break
                                }
                            }
                            utils.endLoadingAnimation()
                            binding.tvInvestment.text = investement.toString()
                            /* investerViewModel.getApprovedInvestmentReqAdapter()*/
                        } else
                        {
                            utils.endLoadingAnimation()
                            binding.rvWithdrawApproved.adapter =
                                TransactionsAdapter(emptyList())
                        }
                    } else {
                        Toast.makeText(
                            mContext,
                            constants.SOMETHING_WENT_WRONG_MESSAGE,
                            Toast.LENGTH_SHORT
                        ).show()
                        utils.endLoadingAnimation()
                    }


                }
                .addOnFailureListener {
                    Toast.makeText(mContext, it.message + "", Toast.LENGTH_SHORT).show()
                    utils.endLoadingAnimation()
                }


        }


    }
    private fun getTax() {
        binding.tvallblance.text="Tax"
        binding.tvWithdraw.text="Tax"
        binding.tvInvestment.text = ""
        var investement: Int = 0
        utils.startLoadingAnimation()
        lifecycleScope.launch {
            investerViewModel.getTaxReq(user.id)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        val list = ArrayList<TransactionModel>()
                        if (task.result.size() > 0) {
                            for (document in task.result) {
                             /*   transaction.add(
                                    document.toObject(
                                        TransactionModel::class.java
                                    )
                                )*/
                                list.add(
                                    document.toObject(
                                        TransactionModel::class.java
                                    )
                                )


                            }

                            binding.rvWithdrawApproved.adapter =
                                TransactionsAdapter(list.sortedByDescending { it.createdAt })
                            for (investment in list.sortedByDescending { it.createdAt }) {

                                if (investment.newBalance == "") {
                                    continue
                                } else {

                                    investement = investment.newBalance.toInt()
                                    break
                                }
                            }
                            utils.endLoadingAnimation()
                            binding.tvInvestment.text = investement.toString()
                            /* investerViewModel.getApprovedInvestmentReqAdapter()*/
                        } else
                        {
                            utils.endLoadingAnimation()
                            binding.rvWithdrawApproved.adapter =
                                TransactionsAdapter(emptyList())
                        }
                    } else {
                        Toast.makeText(
                            mContext,
                            constants.SOMETHING_WENT_WRONG_MESSAGE,
                            Toast.LENGTH_SHORT
                        ).show()
                        utils.endLoadingAnimation()
                    }


                }
                .addOnFailureListener {
                    Toast.makeText(mContext, it.message + "", Toast.LENGTH_SHORT).show()
                    utils.endLoadingAnimation()
                }


        }


    }private fun getWithdraw() {
        binding.tvallblance.text="Withdraw Balance"
        binding.tvWithdraw.text="Withdraw"
        binding.tvInvestment.text = ""
        var withdraw: Int = 0
        utils.startLoadingAnimation()
        lifecycleScope.launch {
            investerViewModel.getWithdrawReq(user.id)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        val list = ArrayList<TransactionModel>()
                        if (task.result.size() > 0) {
                            for (document in task.result) {
                       /*         transaction.add(
                                    document.toObject(
                                        TransactionModel::class.java
                                    )
                                )*/
                                list.add(
                                    document.toObject(
                                        TransactionModel::class.java
                                    )
                                )


                            }

                            binding.rvWithdrawApproved.adapter =
                                TransactionsAdapter(list.sortedByDescending { it.createdAt })
                            for (investment in list.sortedByDescending { it.createdAt }) {

                                if (investment.newBalance == "") {
                                    continue
                                } else {

                                    withdraw = investment.newBalance.toInt()
                                    break
                                }
                            }
                            utils.endLoadingAnimation()
                            binding.tvInvestment.text = withdraw.toString()
                            /* investerViewModel.getApprovedInvestmentReqAdapter()*/
                        } else
                        {
                            utils.endLoadingAnimation()
                            binding.rvWithdrawApproved.adapter =
                                TransactionsAdapter(emptyList())
                        }
                    } else {
                        Toast.makeText(
                            mContext,
                            constants.SOMETHING_WENT_WRONG_MESSAGE,
                            Toast.LENGTH_SHORT
                        ).show()
                        utils.endLoadingAnimation()
                    }


                }
                .addOnFailureListener {
                    Toast.makeText(mContext, it.message + "", Toast.LENGTH_SHORT).show()
                    utils.endLoadingAnimation()
                }


        }


    }
    private fun getInestment() {
        binding.tvallblance.text="Deposit Balance"
        binding.tvWithdraw.text="Investment"
        binding.tvInvestment.text = ""
        var investement: Int = 0
        setTitle("Investment Details")
        utils.startLoadingAnimation()
        lifecycleScope.launch {
            investerViewModel.getInvestmentReq(user.id)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        val list = ArrayList<TransactionModel>()
                        if (task.result.size() > 0) {
                            for (document in task.result) {
                                transaction.add(
                                    document.toObject(
                                        TransactionModel::class.java
                                    )
                                )
                                list.add(
                                    document.toObject(
                                        TransactionModel::class.java
                                    )
                                )


                            }

                            binding.rvWithdrawApproved.adapter =
                                TransactionsAdapter(list.sortedByDescending { it.createdAt })
                            for (investment in list.sortedByDescending { it.createdAt }) {

                                if (investment.newBalance == "") {
                                    continue
                                } else {

                                    investement = investment.newBalance.toInt()
                                    break
                                }
                            }
                            utils.endLoadingAnimation()
                            binding.tvInvestment.text = investement.toString()
                            /* investerViewModel.getApprovedInvestmentReqAdapter()*/
                        }  else
                        {
                            utils.endLoadingAnimation()
                            binding.rvWithdrawApproved.adapter =
                                TransactionsAdapter(emptyList())
                        }
                    } else {
                        Toast.makeText(
                            mContext,
                            constants.SOMETHING_WENT_WRONG_MESSAGE,
                            Toast.LENGTH_SHORT
                        ).show()
                        utils.endLoadingAnimation()
                    }


                }
                .addOnFailureListener {
                    Toast.makeText(mContext, it.message + "", Toast.LENGTH_SHORT).show()
                    utils.endLoadingAnimation()
                }


        }


    }

    private fun generatePDF() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, user.firstName)
        }
        startActivityForResult(intent, CREATE_PDF_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_PDF_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val outputStream = mContext.contentResolver.openOutputStream(uri)
                if (outputStream != null) {
                    val success =
                        SavePdf(transaction).generateInvestmentPDF(
                            outputStream
                        )
                    outputStream.close()

                    if (success) {
                        Toast.makeText(mContext, "Saved successfully", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(mContext, "Failed to save", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_right_investor_menue, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.top_inestment -> {
                getInestment()
                true
            }

            R.id.top_Profit -> {
              getProfit()
                setTitle("Profit Details Details")
                return true
            }

            R.id.top_withdraw -> {
              getWithdraw()
                setTitle("Withdraw Details")
                return true
            }

            R.id.top_Tax -> {
             getTax()
                setTitle("Tax Details")
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}

