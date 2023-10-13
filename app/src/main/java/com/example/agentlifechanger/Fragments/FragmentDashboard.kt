package com.example.agentlifechanger.Fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.enfotrix.lifechanger.Models.ContactUsModel
import com.enfotrix.lifechanger.Models.DashboardViewModel
import com.example.agentlifechanger.ActivityWithdraw
import com.example.agentlifechanger.Constants
import com.example.agentlifechanger.Models.ModelAnnouncement
import com.example.agentlifechanger.Models.ModelFA
import com.example.agentlifechanger.R
import com.example.agentlifechanger.SharedPrefManagar
import com.example.agentlifechanger.UI.ActivityEditProfile
import com.example.agentlifechanger.UI.ActivityIncomeDetails
import com.example.agentlifechanger.UI.ActivityProfile
import com.example.agentlifechanger.Utils
import com.example.agentlifechanger.databinding.FragmentDashboardBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class FragmentDashboard : Fragment() {

    private var _binding: FragmentDashboardBinding? = null


    private val db = Firebase.firestore

    private lateinit var utils: Utils
    private lateinit var mContext: Context
    private lateinit var constants: Constants
    //private lateinit var addBalanceDialog: Dialog

    //private lateinit var user: User
    private lateinit var designatorWhatsapp:String
    private lateinit var designatorMail:String
    private lateinit var designatorPhone:String
    var list=ArrayList<ContactUsModel>()
    var listDesignation=ArrayList<String>()
    var listwhatsapp=ArrayList<String>()
    var listmail=ArrayList<String>()
    var listPhoneNumber=ArrayList<String>()
    //private lateinit var contactUsModel: ContactUsModel
    private lateinit var sharedPrefManager : SharedPrefManagar
    private lateinit var dialog : Dialog
    private lateinit var dialogFA : Dialog
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mContext=requireContext()
        utils = Utils(mContext)
        constants= Constants()
        //dialog intilization.
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_for_contact_us)


        sharedPrefManager = SharedPrefManagar(mContext)



        binding.editfa.setOnClickListener {
            startActivity(Intent(mContext,ActivityEditProfile::class.java))
        }



        binding.layAboutUs.setOnClickListener {

            Toast.makeText(mContext, "Available Soon", Toast.LENGTH_SHORT).show()

        }

        binding.layServices.setOnClickListener {
            Toast.makeText(mContext, "Available Soon", Toast.LENGTH_SHORT).show()
        }

        binding.layshare.setOnClickListener {
            Toast.makeText(mContext, "Available Soon", Toast.LENGTH_SHORT).show()
        }


        binding.layFeeback.setOnClickListener {
            Toast.makeText(mContext, "Available Soon", Toast.LENGTH_SHORT).show()
        }
         binding.layshare.setOnClickListener {
            Toast.makeText(mContext, "Available Soon", Toast.LENGTH_SHORT).show()
        }
        binding.laycompay.setOnClickListener {
            Toast.makeText(mContext, "Available Soon", Toast.LENGTH_SHORT).show()
        }
        binding.layInvest.setOnClickListener {
            Toast.makeText(mContext, "Available Soon", Toast.LENGTH_SHORT).show()
        }
      binding.Income .setOnClickListener {
    startActivity(Intent(requireContext(),ActivityIncomeDetails::class.java))
      }
        binding.withdraw.setOnClickListener {
            startActivity(Intent(requireContext(),ActivityWithdraw::class.java))

        }





binding.myFinancialAdvisor.setOnClickListener {
    startActivity(Intent(mContext,ActivityProfile::class.java))
}

        binding.contactLinear.setOnClickListener {

            showCustomDialog()
        }

        var list=ArrayList<ContactUsModel>()
        db.collection("ContactUs").get()
            .addOnSuccessListener { querySnapshot ->
                // Clear the existing list to avoid duplicates
                list.clear()
                for (document in querySnapshot.documents) {
                    val contactUsModel = document.toObject<ContactUsModel>()
                    if (contactUsModel != null) {
                        // Add the UserData object to the list
                        list.add(contactUsModel)
                    }
                }

                for(doc in list){
                    listDesignation.add(doc.designation)
                }

                for(doc in list){
                    listwhatsapp.add(doc.wa)
                }
                for(doc in list){
                    listmail.add(doc.email)
                }
                for(doc in list){
                    listPhoneNumber.add(doc.mobile)
                }






            }.addOnFailureListener{ exception->
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }

        val whatsapp = dialog.findViewById<ImageView>(R.id.whatsapp)
        val mail = dialog.findViewById<ImageView>(R.id.mail)
        val phone = dialog.findViewById<ImageView>(R.id.dailor)

        whatsapp?.setOnClickListener {
            openWhatsApp(designatorWhatsapp)

        }

        mail?.setOnClickListener {
            Toast.makeText(requireContext(), ""+designatorMail, Toast.LENGTH_SHORT).show()

            openEmail(designatorMail)
        }
        phone?.setOnClickListener{
            openDialer(designatorPhone)

        }





        checkData()
        setData()

        return root
    }

    private fun checkData() {

        db.collection(constants.ANNOUNCEMENT_COLLECTION).document("Rx3xDtgwOH7hMdWxkf94")
            .addSnapshotListener { snapshot, firebaseFirestoreException ->
                firebaseFirestoreException?.let {
                    Toast.makeText( mContext, it.message.toString(), Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                snapshot?.let { document ->
                    val announcement = document.toObject<ModelAnnouncement>()
                    if (announcement != null) {
                        sharedPrefManager.putAnnouncement(announcement)
                        setData()
                    }
                }
            }

    }

    private fun setData() {

        binding.tvAnnouncement.text=sharedPrefManager.getAnnouncement().announcement

       var fa= sharedPrefManager.getUser()
        binding.tvFAName.text=fa.firstName
        binding.tvFADesignation.text=fa.designantion
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun showCustomDialog() {
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        val spinnerWithdrawType = dialog.findViewById<Spinner>(R.id.spinnerWithdrawType)
        if (listDesignation.isNotEmpty()) {
            val adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner,listDesignation)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerWithdrawType.adapter = adapter


            spinnerWithdrawType.setSelection(0) // Set the first index as the selected value
            spinnerWithdrawType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedDesignator = listDesignation[position]
                    val designator=dialog.findViewById<TextView>(R.id.designator)
                    designator.setText(selectedDesignator)



                    designatorWhatsapp = listwhatsapp.get(position)
                    designatorMail=listmail.get(position)
                    designatorPhone=listPhoneNumber.get(position)



                }
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        } else {
            // Set a default empty value in the spinner when the list is empty
            val emptyAdapter = ArrayAdapter(requireContext(), R.layout.custom_spinner, listOf("Empty"))
            emptyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerWithdrawType.adapter = emptyAdapter

            // Set the spinner to display "Empty" as the selected value
            spinnerWithdrawType.setSelection(0)
            spinnerWithdrawType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    // Handle the selected value when the list is empty
                    Toast.makeText(requireContext(), "Empty", Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
        dialog.show()
    }












    private fun openWhatsApp(phone:String) {

        val message = "Hello, Let me know when you are free?"

        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$phone&text=$message")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }



    private   fun openEmail(email:String) {
        val subject = "Hello, this is the email subject" // Replace with the email subject
        val message = "This is the email message body" // Replace with the email message body

        val uri = Uri.parse("mailto:$email?subject=$subject&body=$message")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        startActivity(intent)
    }


    private fun openDialer(phoneNumber: String) {
        val uri = Uri.parse("tel:$phoneNumber")
        val intent = Intent(Intent.ACTION_DIAL, uri)
        startActivity(intent)
    }























}