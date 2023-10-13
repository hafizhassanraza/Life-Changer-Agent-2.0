package com.example.agentlifechanger.UI

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.agentlifechanger.Data.Repo
import com.example.agentlifechanger.Models.FAViewModel
import com.example.agentlifechanger.R
import com.example.agentlifechanger.SharedPrefManagar
import com.example.agentlifechanger.Utils
import com.example.agentlifechanger.databinding.ActivityUpdatePasswordBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class ActivityUpdatePassword : AppCompatActivity() {

    private lateinit var binding: ActivityUpdatePasswordBinding
    private lateinit var utils: Utils
    private lateinit var mContext: Context
    private lateinit var faViewModel: FAViewModel
    private lateinit var sharedPrefManager: SharedPrefManagar
    private lateinit var dialogUpdateTaken: Dialog
    private lateinit var db: FirebaseFirestore
    private  lateinit var repo:Repo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this@ActivityUpdatePassword
        utils = Utils(mContext)
        sharedPrefManager = SharedPrefManagar(mContext)
        showNewPasswordDialog()
repo= Repo(mContext)

    }

    private fun showNewPasswordDialog() {
        dialogUpdateTaken = Dialog(this)
        dialogUpdateTaken.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogUpdateTaken.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogUpdateTaken.setContentView(R.layout.dialog_update_taken_pin)
        dialogUpdateTaken.setCancelable(true)
        val pin1 = dialogUpdateTaken.findViewById<EditText>(R.id.etPin1)
        val pin2 = dialogUpdateTaken.findViewById<EditText>(R.id.etPin2)
        val pin3 = dialogUpdateTaken.findViewById<EditText>(R.id.etPin3)
        val pin4 = dialogUpdateTaken.findViewById<EditText>(R.id.etPin4)
        val pin5 = dialogUpdateTaken.findViewById<EditText>(R.id.etPin5)
        val pin6 = dialogUpdateTaken.findViewById<EditText>(R.id.etPin6)
        val btnSetPin = dialogUpdateTaken.findViewById<Button>(R.id.btnSetpin)

        pin1.requestFocus()
        utils.moveFocus(listOf(pin1, pin2, pin3, pin4, pin5, pin6))

        val tvClearAll = dialogUpdateTaken.findViewById<TextView>(R.id.tvClearAll)
        tvClearAll.setOnClickListener {
            utils.clearAll(listOf(pin1, pin2, pin3, pin4, pin5, pin6))
            pin1.requestFocus()
        }
        btnSetPin.setOnClickListener {

            val completePin =
                "${pin1.text}${pin2.text}${pin3.text}${pin4.text}${pin5.text}${pin6.text}"
            if (completePin.contains("-")) {
                Toast.makeText(this, "Please Enter Valid Password", Toast.LENGTH_SHORT).show()
            } else {

                storeInFireStore(completePin)


            }
        }
        dialogUpdateTaken.show()
    }


    private fun storeInFireStore(completePin: String) {
var user=sharedPrefManager.getUser()
        user.pin=completePin
        lifecycleScope.launch {
            val isSuccesLiveData=repo.updataFaPassword(sharedPrefManager.getToken(),completePin)
                isSuccesLiveData.observe(this@ActivityUpdatePassword)
                {  isSuccess->
                 if(isSuccess)
                 {
                     sharedPrefManager.saveUser(user)
                     dialogUpdateTaken.dismiss()
                     Toast.makeText(mContext, "Pin successfuly updated", Toast.LENGTH_SHORT).show()
                     startActivity(Intent(mContext,ActivityProfile::class.java))
                 }
                    else
                 {


                     Toast.makeText(mContext, "oh sad ,something went wrong", Toast.LENGTH_SHORT).show()
                     dialogUpdateTaken.dismiss()
                 }
                }
        }


       /* utils.startLoadingAnimation()
        val user1 = sharedPrefManager.getUser()
        user1.pin = "783765"*/
      /*  Toast.makeText(mContext, ""+user1, Toast.LENGTH_SHORT).show()
        lifecycleScope.launch {
            faViewModel.updateFA(user1)
                .observe(this@ActivityUpdatePassword) { updateResult ->
                    utils.endLoadingAnimation()
                    if (updateResult == true) {

                        sharedPrefManager.saveUser(user1)
                        Toast.makeText(mContext, "pin Updated!", Toast.LENGTH_SHORT)
                            .show()
                        dialogUpdateTaken.dismiss()

                    } else {
                        Toast.makeText(
                            mContext,
                            " constants.SOMETHING_WENT_WRONG_MESSAGE",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }*/


    }
}
