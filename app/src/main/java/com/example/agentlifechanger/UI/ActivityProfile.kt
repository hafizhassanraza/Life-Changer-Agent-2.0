package com.example.agentlifechanger.UI

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.agentlifechanger.Constants
import com.example.agentlifechanger.R
import com.example.agentlifechanger.SharedPrefManagar
import com.example.agentlifechanger.Utils
import com.example.agentlifechanger.databinding.ActivityProfileBinding

class ActivityProfile : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var sharedPrefManagar: SharedPrefManagar
    private lateinit var dialogPinUpdate: Dialog
    private lateinit var utils: Utils
    private lateinit var constants: Constants
    private lateinit var mcontext: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mcontext = this@ActivityProfile
        sharedPrefManagar = SharedPrefManagar(mcontext)
        utils = Utils(mcontext)
        constants = Constants()


        setData()
        binding.layInvestorAccount.setOnClickListener {
            Toast.makeText(mcontext, "Available Soon", Toast.LENGTH_SHORT).show()
        }
        binding.layLogut.setOnClickListener { task ->
            showDialog()
        }
        binding.updatepassword.setOnClickListener {
            showUpdatePinDialog()
        }

    }

    private fun setData() {
        var fa = sharedPrefManagar.getUser()
        binding.tvAddress.text = fa.address
        binding.tvCNIC.text = fa.cnic
        binding.tvPhoneNumber.text = fa.phone
        binding.tvUserName.text = fa.firstName
        Glide.with(mcontext)
            .load(fa.photo)
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_background)
            .into(binding.imgUser)

    }

    fun showDialog(): Boolean {
        val dialogView = LayoutInflater.from(mcontext).inflate(R.layout.logout_dialog, null)
        val buttonYes: Button = dialogView.findViewById(R.id.btn_yes)
        val buttonNo: Button = dialogView.findViewById(R.id.btn_no)

        val builder = AlertDialog.Builder(mcontext)
        builder.setView(dialogView)
        builder.setCancelable(true)
        var flag = false

        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        buttonYes.setOnClickListener {
            sharedPrefManagar.clearWholeSharedPref()
            sharedPrefManagar.logOut()
            startActivity(Intent(mcontext, ActivityLogin::class.java))
            finish()
            alertDialog.dismiss()
        }

        buttonNo.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
        return false
    }

    private fun showUpdatePinDialog() {
        dialogPinUpdate = Dialog(mcontext)
        dialogPinUpdate.setContentView(R.layout.dialog_for_update_pin)
        dialogPinUpdate.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogPinUpdate.setCancelable(true)

        val pin1 = dialogPinUpdate.findViewById<EditText>(R.id.etPin1)
        val pin2 = dialogPinUpdate.findViewById<EditText>(R.id.etPin2)
        val pin3 = dialogPinUpdate.findViewById<EditText>(R.id.etPin3)
        val pin4 = dialogPinUpdate.findViewById<EditText>(R.id.etPin4)
        val pin5 = dialogPinUpdate.findViewById<EditText>(R.id.etPin5)
        val pin6 = dialogPinUpdate.findViewById<EditText>(R.id.etPin6)
        val btnSetPin = dialogPinUpdate.findViewById<Button>(R.id.btnSetpin)

        pin1.requestFocus()
        utils.moveFocus(listOf(pin1, pin2, pin3, pin4, pin5, pin6))

        val tvClearAll = dialogPinUpdate.findViewById<TextView>(R.id.tvClearAll)
        tvClearAll.setOnClickListener {
            utils.clearAll(listOf(pin1, pin2, pin3, pin4, pin5, pin6))
            pin1.requestFocus()
        }
        btnSetPin.setOnClickListener {
            Toast.makeText(mcontext, "" + sharedPrefManagar.getUser().pin, Toast.LENGTH_SHORT)
                .show()
            val completePin =
                "${pin1.text}${pin2.text}${pin3.text}${pin4.text}${pin5.text}${pin6.text}"
            if (completePin == sharedPrefManagar.getUser().pin) {
                if (completePin.contains("-")) {
                    Toast.makeText(mcontext, "Enter valid pin", Toast.LENGTH_SHORT).show()
                } else {
                    startActivity(Intent(mcontext, ActivityUpdatePassword::class.java))
                    finish()

                }
            } else {
                Toast.makeText(mcontext, "Invalid password, try another", Toast.LENGTH_SHORT).show()
            }
        }

        dialogPinUpdate.show()
    }
}