package com.example.agentlifechanger.Adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agentlifechanger.Constants
import com.example.agentlifechanger.Models.TransactionModel
import com.example.agentlifechanger.databinding.ItemTransactionBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionsAdapter ( val data: List<TransactionModel>) : RecyclerView.Adapter<TransactionsAdapter.ViewHolder>(){







    var constant= Constants()

    interface OnItemClickListener {
        fun onItemClick(transactionModel: TransactionModel)
        fun onDeleteClick(transactionModel: TransactionModel)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder.bind(data[position]) }
    override fun getItemCount(): Int { return data.size }
    inner class ViewHolder(val itemBinding: ItemTransactionBinding) : RecyclerView.ViewHolder(itemBinding.root){

        fun bind(transactionModel: TransactionModel) {




            val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
            val formattedDate = transactionModel.transactionAt?.toDate()?.let { dateFormat.format(it) }

            itemBinding.tvReqDate.text = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(transactionModel.createdAt.toDate())
            itemBinding.tvPreviousBalance.text = transactionModel.previousBalance
            itemBinding.tvApprovedDate.text = formattedDate
            itemBinding.tvPreviousBalance.text = transactionModel.previousBalance
            itemBinding.tvNewBalance.text = transactionModel.newBalance
            itemBinding.tvReqAmount.text =  transactionModel.amount
            itemBinding.tvNewBalance.setTextColor(0xFF2F9B47.toInt())
            itemBinding.tvReqAmount.setTextColor( 0xFF2F9B47.toInt())

        }

    }

}