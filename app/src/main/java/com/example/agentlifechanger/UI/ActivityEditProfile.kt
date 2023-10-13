package com.example.agentlifechanger.UI

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.agentlifechanger.Constants
import com.example.agentlifechanger.Data.Repo
import com.example.agentlifechanger.Models.FAViewModel
import com.example.agentlifechanger.Models.ModelFA
import com.example.agentlifechanger.Models.User
import com.example.agentlifechanger.R
import com.example.agentlifechanger.SharedPrefManagar
import com.example.agentlifechanger.Utils
import com.example.agentlifechanger.databinding.ActivityEditProfileBinding
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class ActivityEditProfile : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var mContext: Context
    private lateinit var sharedPrefManager: SharedPrefManagar
    private lateinit var utils: Utils
    private val faViewModel: FAViewModel by viewModels()


    private var constants = Constants()
    private var photo = ""
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var dialog: Dialog
    private lateinit var imgProfilePhoto: ImageView

    private val IMAGE_PICKER_REQUEST_CODE = 200
    private var imageURI: Uri? = null

    private lateinit var repo: Repo

    private lateinit var modelFA: ModelFA


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this@ActivityEditProfile
        utils = Utils(mContext)
        sharedPrefManager = SharedPrefManagar(mContext)

        repo = Repo(mContext)
        modelFA = ModelFA()
        getFAID()

        edit()

        binding.btnProfileedit.setOnClickListener {
            update()
            val intent = Intent(this@ActivityEditProfile, MainActivity::class.java)
            startActivity(intent)
            finish()

        }
        binding.editPIC.setOnClickListener {
            showPhotoDialog()
        }
    }

    fun update() {
        var fa = sharedPrefManager.getUser()

        fa.firstName = binding.etFirstName.editText?.text.toString()
        fa.lastName = binding.etLastName.editText?.text.toString()
        fa.designantion = binding.etDesignation.editText?.text.toString()
        fa.cnic = binding.etCNIC.editText?.text.toString()
        fa.address = binding.etAddress.editText?.text.toString()
        fa.phone = binding.etPhone.editText?.text.toString()
        lifecycleScope.launch {
            faViewModel.updateFA(fa)
        }
        sharedPrefManager.saveUser(fa)
    }


    fun edit() {
        val model = sharedPrefManager.getUser()
        if (model != null) {
            Glide.with(mContext)
                .load(model.photo)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.imgView)
            binding.etFirstName.editText?.setText(model.firstName)
            binding.etLastName.editText?.setText(model.lastName)
            binding.etDesignation.editText?.setText(model.designantion)
            binding.etCNIC.editText?.setText(model.cnic)
            binding.etAddress.editText?.setText(model.address)
            binding.etPhone.editText?.setText(model.phone)
        }
    }


    private fun getFAID() {
        db.collection(constants.FA_COLLECTION)
            .whereEqualTo(FieldPath.documentId(), sharedPrefManager.getToken())
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    utils.startLoadingAnimation()
                    snapshot.documents?.forEach { document ->
                        photo = document.getString("photo").toString()
                        Glide.with(mContext)
                            .load(photo)
                            .centerCrop()
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(binding.imgView)
                        utils.endLoadingAnimation()
                    }
                }
            }

    }

    fun showPhotoDialog() {

        dialog = Dialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_profile_photo_upload)

        imgProfilePhoto = dialog.findViewById<ImageView>(R.id.imgProfilePhoto)
        val tvSelect = dialog.findViewById<TextView>(R.id.tvSelect)
        val btnUplodProfile = dialog.findViewById<Button>(R.id.btnUplodProfile)

        tvSelect.setOnClickListener {
            resultLauncher.launch("image/*")
        }

        btnUplodProfile.setOnClickListener {

            lifecycleScope.launch {
                if (imageURI != null) addFaPhoto(imageURI!!, "AdvisorProfilePhoto")
                else Toast.makeText(mContext, "Please Select Image", Toast.LENGTH_SHORT).show()
            }


        }

        dialog.show()
    }


    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        imageURI = it
        imgProfilePhoto.setImageURI(imageURI)

    }


    suspend fun addFaPhoto(imageUri: Uri, type: String) {
        utils.startLoadingAnimation()
        faViewModel.uploadPhoto(imageUri, type)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    var modelFa: ModelFA = sharedPrefManager.getUser()
                    modelFa.photo = uri.toString()

                    lifecycleScope.launch {

                        faViewModel.updateFA(modelFa).observe(this@ActivityEditProfile) {
                            utils.endLoadingAnimation()
                            if (it == true) {
                                sharedPrefManager.saveUser(modelFa)
                                Toast.makeText(
                                    mContext,
                                    "Profile Photo Updated",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else Toast.makeText(
                                mContext,
                                constants.SOMETHING_WENT_WRONG_MESSAGE,
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }

                }
                    .addOnFailureListener { exception ->
                        Toast.makeText(mContext, exception.message + "", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(mContext, "Failed to upload profile pic", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(
            Intent(
                mContext,
                MainActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )

    }


}