package com.example.agentlifechanger

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.enfotrix.lifechanger.Models.FaProfitViewModel
import com.example.agentlifechanger.Adapters.WithdrawViewPagerAdapter
import com.example.agentlifechanger.Models.AgentWithdrawModel
import com.example.agentlifechanger.databinding.ActivityWithdrawBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class ActivityWithdraw : AppCompatActivity() {
    private lateinit var binding: ActivityWithdrawBinding
    private val faProfitViewModel: FaProfitViewModel by viewModels()
    private lateinit var utils: Utils
    private lateinit var mContext: Context
    private lateinit var constants: Constants
    private lateinit var sharedPrefManager: SharedPrefManagar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdraw)



        binding = ActivityWithdrawBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this@ActivityWithdraw
        utils = Utils(mContext)
        constants = Constants()
        sharedPrefManager = SharedPrefManagar(mContext)
        setTitle("My Withdraws")

        binding.imgBack.setOnClickListener {
            finish()
        }

        setupViewPager()
        setupTabLayout()
getData()
    }
    private fun setupTabLayout() {
        TabLayoutMediator(
            binding.tabLayout, binding.viewPager
        ) { tab,
            position ->
            if (position == 0) tab.text = "Approved"
            else if (position == 1) tab.text = "Pending"
        }.attach()
    }

    private fun setupViewPager() {
        val adapter = WithdrawViewPagerAdapter(this, 2)
        binding.viewPager.adapter = adapter
    }
    private fun getData() {
        utils.startLoadingAnimation()
        lifecycleScope.launch {
            faProfitViewModel.getAgentTransactionReq(sharedPrefManager.getToken())
                .addOnCompleteListener { task ->
                    utils.endLoadingAnimation()
                    if (task.isSuccessful) {

                        val list = ArrayList<AgentWithdrawModel>()
                        if (task.result.size() > 0) {

                            for (document in task.result) list.add(
                                document.toObject(
                                    AgentWithdrawModel::class.java
                                )
                            )
                            sharedPrefManager.putWithdrawReqList(list)
                            setupViewPager()
                            setupTabLayout()


                        }
                    } else{
                        sharedPrefManager.putWithdrawReqList(emptyList())
                        Toast.makeText(
                            mContext,
                            constants.SOMETHING_WENT_WRONG_MESSAGE,
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                }
                .addOnFailureListener {
                    utils.endLoadingAnimation()
                    Toast.makeText(mContext, it.message + "", Toast.LENGTH_SHORT).show()

                }

        }
    }


    override fun onBackPressed() {
        val viewPager = binding.viewPager
        if (viewPager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }

}