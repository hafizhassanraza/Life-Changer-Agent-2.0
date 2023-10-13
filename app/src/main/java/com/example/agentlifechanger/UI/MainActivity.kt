package com.example.agentlifechanger.UI

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agentlifechanger.Adapters.AdapterClient
import com.example.agentlifechanger.Constants
import com.example.agentlifechanger.Data.Repo
import com.example.agentlifechanger.Models.FAViewModel
import com.example.agentlifechanger.Models.InvesterViewModel
import com.example.agentlifechanger.Models.AgentTransactionModel
import com.example.agentlifechanger.Models.InvestmentModel
import com.example.agentlifechanger.Models.ModelFA
import com.example.agentlifechanger.Models.User
import com.example.agentlifechanger.R
import com.example.agentlifechanger.SharedPrefManagar
import com.example.agentlifechanger.Utils
import com.example.agentlifechanger.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var repo: Repo

    private lateinit var mContext: Context
    private lateinit var binding: ActivityMainBinding
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this@MainActivity
        utils = Utils(mContext)
        sharedPrefManager = SharedPrefManagar(mContext)

        modelFA = ModelFA()
        repo = Repo(mContext)


        faProfitModel = AgentTransactionModel()
        recyclerView = findViewById(R.id.rvClients)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        investorIDArraylist = arrayListOf()
        userArraylist = arrayListOf()
        balanceArraylist = arrayListOf()

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

}
