package com.example.agentlifechanger.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.agentlifechanger.Adapters.AdapterClient
import com.example.agentlifechanger.Constants
import com.example.agentlifechanger.Data.Repo
import com.example.agentlifechanger.Models.FAViewModel
import com.example.agentlifechanger.Models.AgentTransactionModel
import com.example.agentlifechanger.Models.InvesterViewModel
import com.example.agentlifechanger.Models.InvestmentModel
import com.example.agentlifechanger.Models.ModelFA
import com.example.agentlifechanger.Models.User
import com.example.agentlifechanger.R
import com.example.agentlifechanger.SharedPrefManagar
import com.example.agentlifechanger.UI.ActivityInvestorDetail
import com.example.agentlifechanger.UI.ActivityNewWithdrawReq
import com.example.agentlifechanger.Utils
import com.example.agentlifechanger.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.util.Locale

class FragmentHome : Fragment() ,AdapterClient.OnItemClickListener{

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    private lateinit var repo: Repo

    private lateinit var mContext: Context
    private lateinit var sharedPrefManager: SharedPrefManagar
    private lateinit var utils: Utils
    private var constants = Constants()
    private val db = Firebase.firestore
    private lateinit var modelFA: ModelFA

    private lateinit var userArraylist: ArrayList<User>
    private lateinit var investorIDArraylist: ArrayList<InvestmentModel>
    private lateinit var balanceArraylist: ArrayList<Int>
    private lateinit var faProfitModel: AgentTransactionModel

    private val investerViewModel: InvesterViewModel by viewModels()
    private val faViewModel: FAViewModel by viewModels()



    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: AdapterClient

    private var id = ""
    private var photo = ""




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mContext = requireContext()
        utils = Utils(mContext)
        constants = Constants()
        sharedPrefManager = SharedPrefManagar(mContext)

        mContext = requireContext()
        utils = Utils(mContext)
        sharedPrefManager = SharedPrefManagar(mContext)

        modelFA = ModelFA()
        repo = Repo(mContext)


        faProfitModel = AgentTransactionModel()
        recyclerView = binding.rvClients
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        investorIDArraylist = arrayListOf()
        userArraylist = arrayListOf()
        balanceArraylist = arrayListOf()
        getFAID()

        getData()
        getBalance()
        myAdapter = AdapterClient(
            id,
            sharedPrefManager.getAssignedInvestor(),
            this,
            viewLifecycleOwner.lifecycleScope,
            investerViewModel
        )

        recyclerView.adapter = myAdapter

        binding.svClients.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterclients(newText)
                return false
            }
        })

        binding.withdraw.setOnClickListener {
            startActivity(Intent(mContext, ActivityNewWithdrawReq::class.java))
        }

        return root
    }


    private fun filterclients(text: String) {
        // creating a new array list to filter our data.
        val filteredlist = ArrayList<User>()
        if (text.isEmpty() || text.equals("")) {
            binding.rvClients.adapter =
                AdapterClient(constants.FROM_ASSIGNED_FA, sharedPrefManager.getAssignedInvestor(), this,
                    viewLifecycleOwner.lifecycleScope,
                    investerViewModel)

        } else {
            for (user in sharedPrefManager.getAssignedInvestor()) {
                if (user.firstName.toLowerCase(Locale.getDefault())
                        .contains(text.toLowerCase(Locale.getDefault()))
                ) {
                    filteredlist.add(user)
                }
            }
            if (filteredlist.isEmpty()) {
                Toast.makeText(mContext, "No Data Found..", Toast.LENGTH_SHORT).show()
            } else {

                binding.rvClients.adapter = AdapterClient(
                    constants.FROM_ASSIGNED_FA,
                    filteredlist,
                    this,
                    viewLifecycleOwner.lifecycleScope,
                    investerViewModel
                )

            }
        }

    }
    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.top_right_investor_menue, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId)
        {
            R.id.top_inestment -> {

                true

            }
            R.id.top_withdraw -> {

                true

            }
            else -> return super.onOptionsItemSelected(item)
        }


    }
    fun getData() {
        utils.startLoadingAnimation()
        lifecycleScope.launch {
            investerViewModel.getInvestors()
                .addOnCompleteListener { task ->
                    utils.endLoadingAnimation()
                    binding.tvProfit.text=sharedPrefManager.getProfit().newBalance
                    binding.ramarks .text=sharedPrefManager.getProfit().remarks
                    if (task.isSuccessful) {
                        val AssignedInvestorList = ArrayList<User>()
                        if (task.result.size() > 0) {
                            for (document in task.result) {
                                if (document.toObject(User::class.java).fa_id == sharedPrefManager.getToken())
                                {
                                    AssignedInvestorList.add(
                                        document.toObject(User::class.java)
                                    )
                                }

                            }
                            sharedPrefManager.putAssignedInvestor(AssignedInvestorList)


                        } else Toast.makeText(
                            mContext,
                            "SOMETHING_WENT_WRONG_MESSAGE",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
                .addOnFailureListener {
                    utils.endLoadingAnimation()


                }

        }
    }

    fun getBalance() {
        var balance:Int=0
        lifecycleScope.launch {
            investerViewModel.getInvestement()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (task.result.size() > 0) {
                            for (document in task.result) {
                                for (invest in sharedPrefManager.getAssignedInvestor()) {
                                    if (document.toObject(InvestmentModel::class.java).investorID == invest.id) {

                                        balance += document.toObject(InvestmentModel::class.java).investmentBalance.toInt()
                                    }

                                }
                            }
                            binding.tvInvestment.text=balance.toString()


                        } else Toast.makeText(
                            mContext,
                            "SOMETHING_WENT_WRONG_MESSAGE",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
                .addOnFailureListener {


                }

        }





    }
    override fun onItemClick(user: User) {
        startActivity(
            Intent(mContext,
                ActivityInvestorDetail::class.java).putExtra("Investor", Gson().toJson(user)))
    }



    private fun getFAID() {
        db.collection(constants.FA_COLLECTION).whereEqualTo(FieldPath.documentId(),sharedPrefManager.getToken())
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    snapshot.documents?.forEach { document ->
                        id =document.getString("id").toString()
                        photo = document.getString("photo").toString()
                        sharedPrefManager.putId(document.id)
                        binding.tvInvestorName.setText(document.getString("firstName")+" "+document.getString("lastName"))
                        binding.tvInvestordesignation.setText(document.getString("designantion"))
                        binding.tvInvestorCnic.setText(document.getString("cnic"))
                        binding.tvInvestorPhoneNumber.setText(document.getString("phone"))
                        Glide.with(mContext)
                            .load(photo)
                            .centerCrop()
                            .placeholder(R.drawable.ic_launcher_background) // Placeholder image while loading
                            .into(binding.imageView)


                    }
                }
            }

        var faProfitModel=AgentTransactionModel()
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
        }


    }







}