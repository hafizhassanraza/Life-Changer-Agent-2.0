package com.example.agentlifechanger.Adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agentlifechanger.Constants
import com.example.agentlifechanger.Models.AgentWithdrawModel
import com.example.agentlifechanger.databinding.ItemTransactionBinding
import com.google.firebase.database.collection.LLRBNode
import java.text.SimpleDateFormat
import java.util.Locale

class FATransactionsAdapter (var activity:String, val data: List<AgentWithdrawModel>) : RecyclerView.Adapter<FATransactionsAdapter.ViewHolder>(){


    var constant= Constants()

    interface OnItemClickListener {
        fun onItemClick(transactionModel: AgentWithdrawModel)
        fun onDeleteClick(transactionModel: AgentWithdrawModel)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder.bind(data[position]) }
    override fun getItemCount(): Int { return data.size }
    inner class ViewHolder(val itemBinding: ItemTransactionBinding) : RecyclerView.ViewHolder(itemBinding.root){

        fun bind(transactionModel: AgentWithdrawModel) {


            val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
            val formattedDate = transactionModel.withdrawApprovedDate?.toDate()?.let { dateFormat.format(it) }

            itemBinding.tvReqDate.text = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(transactionModel.lastWithdrawReqDate.toDate())
            itemBinding.tvPreviousBalance.text = transactionModel.lastWithdrawBalance

            when (activity) {
                constant.FROM_PENDING_WITHDRAW_REQ -> {
                    itemBinding.tvApprovedDate.text = constant.TRANSACTION_STATUS_PENDING
                    itemBinding.tvNewBalance.text = constant.TRANSACTION_STATUS_PENDING
                    itemBinding.tvReqAmount.text = "-${transactionModel.withdrawBalance}"
                    itemBinding.tvApprovedDate.setTextColor(Color.RED)
                    itemBinding.tvNewBalance.setTextColor(Color.RED)
                    itemBinding.tvReqAmount.setTextColor(Color.RED)
                }
                constant.FROM_APPROVED_WITHDRAW_REQ -> {
                    itemBinding.tvApprovedDate.text = formattedDate
                    itemBinding.tvPreviousBalance.text = transactionModel.lastWithdrawBalance
                    itemBinding.tvNewBalance.text = transactionModel.totalWithdrawBalance
                    itemBinding.tvReqAmount.text = if (activity == constant.FROM_TAX) "-${transactionModel.withdrawBalance}" else transactionModel.withdrawBalance
                    itemBinding.tvNewBalance.setTextColor(0xFF2F9B47.toInt())
                    itemBinding.tvReqAmount.setTextColor(if (activity == constant.FROM_TAX) Color.RED else 0xFF2F9B47.toInt())
                }

            }





        }

    }

}