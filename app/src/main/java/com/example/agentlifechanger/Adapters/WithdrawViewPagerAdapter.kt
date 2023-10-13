package com.example.agentlifechanger.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.agentlifechanger.Fragments.FragmentApprvedReq
import com.example.agentlifechanger.Fragments.FragmentPendingWithdraw

class WithdrawViewPagerAdapter (fragmentActivity: FragmentActivity, private var totalCount: Int) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return totalCount
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentApprvedReq()
            1 -> FragmentPendingWithdraw()
            else -> FragmentApprvedReq()
        }
    }
}
