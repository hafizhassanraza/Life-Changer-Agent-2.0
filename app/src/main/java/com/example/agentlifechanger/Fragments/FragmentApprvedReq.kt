package com.example.agentlifechanger.Fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.enfotrix.lifechanger.Models.FaProfitViewModel
import com.example.agentlifechanger.Constants
import com.example.agentlifechanger.R
import com.example.agentlifechanger.SharedPrefManagar
import com.example.agentlifechanger.Utils
import com.example.agentlifechanger.databinding.FragmentApprvedReqBinding


class FragmentApprvedReq : Fragment() {

    private val faProfitViewModel: FaProfitViewModel by viewModels()
    private var _binding: FragmentApprvedReqBinding? = null
    private val binding get() = _binding!!

    private val CREATE_PDF_REQUEST_CODE = 123

    private lateinit var utils: Utils
    private lateinit var mContext: Context
    private lateinit var constants: Constants
    private lateinit var sharedPrefManager : SharedPrefManagar
    private lateinit var dialog : Dialog



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentApprvedReqBinding.inflate(inflater, container, false)
        val root: View = binding.root


        mContext=requireContext()
        utils = Utils(mContext)
        constants= Constants()
        sharedPrefManager = SharedPrefManagar(mContext)


        binding.rvWithdrawPending.layoutManager = LinearLayoutManager(mContext)
        binding.rvWithdrawPending.adapter= faProfitViewModel.getApprovedWithdrawReqAdapter(constants.FROM_APPROVED_WITHDRAW_REQ)




        return root
    }

}