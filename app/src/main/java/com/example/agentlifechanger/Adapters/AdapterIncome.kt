package com.example.agentlifechanger.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agentlifechanger.Constants
import com.example.agentlifechanger.Models.AgentTransactionModel
import com.example.agentlifechanger.Models.TransactionModel
import com.example.agentlifechanger.databinding.ItemIncomeBinding
import com.example.agentlifechanger.databinding.ItemTransactionBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AdapterIncome ( val data: List<AgentTransactionModel>) : RecyclerView.Adapter<AdapterIncome.ViewHolder>(){


    var constant= Constants()

    interface OnItemClickListener {
        fun onItemClick(transactionModel: AgentTransactionModel)
        fun onDeleteClick(transactionModel: AgentTransactionModel)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemIncomeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder.bind(data[position]) }
    override fun getItemCount(): Int { return data.size }
    inner class ViewHolder(val itemBinding: ItemIncomeBinding) : RecyclerView.ViewHolder(itemBinding.root){

        fun bind(transactionModel: AgentTransactionModel) {


            val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
            val formattedDate = transactionModel.transactionAt?.toDate()?.let { dateFormat.format(it) }
           itemBinding.tvtransactionAt.text =
               transactionModel.transactionAt?.toDate()
                   ?.let { SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(it) }
            itemBinding.tvIncome.text = transactionModel.salary
            itemBinding.tvRemarks.text = transactionModel.remarks


        }

    }

}