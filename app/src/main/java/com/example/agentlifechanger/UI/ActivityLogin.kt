package com.example.agentlifechanger.UI

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.ColorSpace.Model
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.agentlifechanger.Constants
import com.example.agentlifechanger.Models.ModelFA
import com.example.agentlifechanger.R
import com.example.agentlifechanger.SharedPrefManagar
import com.example.agentlifechanger.Utils
import com.example.agentlifechanger.databinding.ActivityLoginBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class ActivityLogin : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    private lateinit var utils: Utils
    private lateinit var mContext: Context
    private lateinit var constants: Constants
    private lateinit var sharedPrefManager : SharedPrefManagar
    private lateinit var dialog : Dialog
    private lateinit var modelFa: ModelFA
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext=this@ActivityLogin
        modelFa = ModelFA()
        utils = Utils(mContext)
        constants= Constants()
        sharedPrefManager = SharedPrefManagar(mContext)

        binding.btnSignIn.setOnClickListener(View.OnClickListener {
            if((!IsEmpty()) && IsValid()) checkCNIC(utils.cnicFormate(   binding.etCNIC.editText?.text.toString()))
        })

    }

    fun showDialogPin(user:ModelFA?,token:String) {

        dialog = Dialog (mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_set_pin)
        val etPin1 = dialog.findViewById<EditText>(R.id.etPin1)
        val etPin2 = dialog.findViewById<EditText>(R.id.etPin2)
        val etPin3 = dialog.findViewById<EditText>(R.id.etPin3)
        val etPin4 = dialog.findViewById<EditText>(R.id.etPin4)
        val etPin5 = dialog.findViewById<EditText>(R.id.etPin5)
        val etPin6 = dialog.findViewById<EditText>(R.id.etPin6)
        val tvClearAll = dialog.findViewById<TextView>(R.id.tvClearAll)
        val tvHeader = dialog.findViewById<TextView>(R.id.tvHeader)
        val btnSetPin = dialog.findViewById<Button>(R.id.btnSetPin)

        tvHeader.setText("Enter your Pin to Login !")
        btnSetPin.setText("Login")
        etPin1.requestFocus();
        utils.moveFocus( listOf(etPin1, etPin2, etPin3, etPin4, etPin5, etPin6))

        tvClearAll.setOnClickListener{
            utils.clearAll( listOf(etPin1, etPin2, etPin3, etPin4, etPin5, etPin6))
            etPin1.requestFocus();

        }
        btnSetPin.setOnClickListener {
            if(!utils.checkEmpty( listOf(etPin1, etPin2, etPin3, etPin4, etPin5, etPin6))){
                var pin : String =  utils.getPIN( listOf(etPin1, etPin2, etPin3, etPin4, etPin5, etPin6))
                loginUser(user,pin,token)
            }
            else Toast.makeText(mContext, "Please enter 6 Digits Pin!", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    private fun loginUser(user:ModelFA?,pin:String,token: String){

        //pending, active, incomplete
        if (user != null) {


            if(user.pin.equals(pin)){

                    utils.startLoadingAnimation()
                    lifecycleScope.launch {
                        db.collection(constants.FA_COLLECTION).document().get()
                            .addOnSuccessListener {
                                utils.endLoadingAnimation()
                                    sharedPrefManager.setStatus("pending")
                                    sharedPrefManager.saveLoginAuth(user, token, false)//usre +token+login_boolean
                                    sharedPrefManager.saveUser(user)
                                        startActivity(
                                            Intent(
                                                mContext,
                                                ActivityInvestorLoginDeatils::class.java
                                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                        )
                                        finish()


                            }
                            .addOnFailureListener{
                                utils.endLoadingAnimation()
                                Toast.makeText(mContext, constants.SOMETHING_WENT_WRONG_MESSAGE, Toast.LENGTH_SHORT).show()
                            }


                    }

                }

else Toast.makeText(mContext, "Incorrect Password", Toast.LENGTH_SHORT).show()

        }

    }


    private fun checkCNIC(cnic:String) {

        utils.startLoadingAnimation()
        lifecycleScope.launch{
            db.collection(constants.FA_COLLECTION).whereEqualTo(constants.FA_CNIC,cnic).get()
                .addOnCompleteListener{task ->
                    utils.endLoadingAnimation()
                    if (task.isSuccessful) {

                        if(task.result.size()>0){
                            var token: String = ""
                            val documents = task.result
                            var user: ModelFA? = null
                            for (document in documents) {
                                user = document.toObject(ModelFA::class.java)
                                token= document.id
                            }


                            if(user?.status.equals(constants.INVESTOR_STATUS_ACTIVE) || user?.status.equals(constants.INVESTOR_STATUS_PENDING) || user?.status.equals(constants.INVESTOR_STATUS_INCOMPLETE) )
                                showDialogPin(user,token)
                            else if(user?.status.equals(constants.INVESTOR_STATUS_BLOCKED))
                                binding.etCNIC.editText?.error =constants.INVESTOR_CNIC_BLOCKED
                            else if(documents.size()==0)
                                binding.etCNIC.editText?.error =constants.INVESTOR_CNIC_NOT_EXIST
                        }
                        else binding.etCNIC.editText?.error =constants.INVESTOR_CNIC_NOT_EXIST
                    }

                }
                .addOnFailureListener{
                    utils.endLoadingAnimation()
                    Toast.makeText(mContext, it.message+"", Toast.LENGTH_SHORT).show()
                }

        }
    }

    private fun IsEmpty(): Boolean {

        val result = MutableLiveData<Boolean>()
        result.value=true
        if (binding.etCNIC.editText?.text.toString().isEmpty()) binding.etCNIC.editText?.error = "Empty CNIC"
        else result.value = false

        return result.value!!
    }
    private fun IsValid(): Boolean {

        val result = MutableLiveData<Boolean>()
        result.value=false
        if (binding.etCNIC.editText?.text.toString().length<13) binding.etCNIC.editText?.error = "Invalid CNIC"
        else result.value = true

        return result.value!!
    }




}