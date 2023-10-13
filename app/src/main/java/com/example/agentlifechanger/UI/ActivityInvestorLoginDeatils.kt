package com.example.agentlifechanger.UI

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.agentlifechanger.Constants
import com.example.agentlifechanger.Models.FAViewModel
import com.example.agentlifechanger.Models.InvesterViewModel
import com.example.agentlifechanger.Models.ModelBankAccount
import com.example.agentlifechanger.Models.ModelFA
import com.example.agentlifechanger.Models.User
import com.example.agentlifechanger.R
import com.example.agentlifechanger.SharedPrefManagar
import com.example.agentlifechanger.Utils
import com.example.agentlifechanger.databinding.ActivityInvestorLoginDeatilsBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ActivityInvestorLoginDeatils : AppCompatActivity() {


    private val IMAGE_PICKER_REQUEST_CODE = 200

    private lateinit var uploadedImageURI: Uri

    private var imageURI: Uri? = null
    private var FACnicFrontURI: Uri? = null
    private var FACnicBackURI: Uri? = null

    private lateinit var imgSelectCnicBack: ImageView
    private lateinit var imgSelectCnicFront: ImageView

    private var FACnicFront: Boolean = false
    private var FACnicBack: Boolean = false
    private var UserCnicFront: Boolean = false
    private var UserCnicBack: Boolean = false
    private var UserProfilePhoto: Boolean = false

    private val faViewModel: FAViewModel by viewModels()

    private lateinit var binding: ActivityInvestorLoginDeatilsBinding

    private lateinit var imgProfilePhoto: ImageView


    private lateinit var utils: Utils
    private lateinit var mContext: Context
    private lateinit var constants: Constants
    private lateinit var user: User
    private lateinit var sharedPrefManager: SharedPrefManagar
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvestorLoginDeatilsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this@ActivityInvestorLoginDeatils
        utils = Utils(mContext)
        constants = Constants()
        sharedPrefManager = SharedPrefManagar(mContext)

        binding.btnStart.visibility = View.GONE
        //imageURI = Uri.parse("https://www.google.com/search?q=place+holder+image&tbm=isch&ved=2ahUKEwjMh5DChNr_AhVMricCHXqrDJsQ2-cCegQIABAA&oq=place+holder+&gs_lcp=CgNpbWcQARgAMgUIABCABDIFCAAQgAQyBQgAEIAEMgUIABCABDIFCAAQgAQyBQgAEIAEMgUIABCABDIFCAAQgAQyBQgAEIAEMgUIABCABDoECCMQJzoKCAAQigUQsQMQQzoHCAAQigUQQzoICAAQgAQQsQM6CAgAELEDEIMBUIoOWJ4wYOY9aAFwAHgAgAH0AYgByRmSAQQyLTE1mAEAoAEBqgELZ3dzLXdpei1pbWfAAQE&sclient=img&ei=OuaVZMyCJszcnsEP-tay2Ak&bih=1081&biw=1920#imgrc=wqFTpxdyrrUpwM");

        checkData()



        binding.layInvestorPhone.setOnClickListener {

            //Toast.makeText(mContext, sharedPrefManager.getNominee().acc_number+"", Toast.LENGTH_SHORT).show()

            if (sharedPrefManager.isPhoneNumberAdded()) Toast.makeText(
                mContext,
                "Phone already added",
                Toast.LENGTH_SHORT
            ).show()
            else startActivity(
                Intent(
                    mContext,
                    ActivityPhoneNumber::class.java
                ).putExtra(constants.KEY_ACTIVITY_FLOW, constants.VALUE_ACTIVITY_FLOW_USER_DETAILS)
            )
        }






        binding.layInvestorBank.setOnClickListener {

            if (sharedPrefManager.isFABankAdded()) Toast.makeText(
                mContext,
                "Agent Bank already added!",
                Toast.LENGTH_SHORT
            ).show()
            else showAddAccountDialog(constants.VALUE_DIALOG_FLOW_INVESTOR_BANK)


        }
        binding.layInvestorProfilePhoto.setOnClickListener {
            //showPhotoDialog()
            if (sharedPrefManager.isUserPhotoAdded()) Toast.makeText(
                mContext,
                "Agent photo already added!",
                Toast.LENGTH_SHORT
            ).show()
            else showPhotoDialog()
        }
        binding.layInvestorCNIC.setOnClickListener {
            if (sharedPrefManager.isFACnicAdded()) Toast.makeText(
                mContext,
                "Agent CNIC already added!",
                Toast.LENGTH_SHORT
            ).show()
            else showAddCnicDialog(constants.VALUE_DIALOG_FLOW_INVESTOR_CNIC)
        }
        binding.btnStart.setOnClickListener {

            startApp()
        }

    }

    fun startApp() {
        var modelFA: ModelFA = sharedPrefManager.getUser()
            utils.startLoadingAnimation()
            lifecycleScope.launch {
                faViewModel.updateFA(modelFA).observe(this@ActivityInvestorLoginDeatils) {
                        if (it == true) {
                            Toast.makeText(
                                mContext,
                                "Profile Completed Successfully!",
                                Toast.LENGTH_SHORT
                            ).show()

                            sharedPrefManager.setLogin(it)
                            lifecycleScope.launch {

                                // Use async to execute Firebase calls asynchronously
                             /*   val saveAdminDeferred = async { saveAdminAccounts() }
                                val saveUserDeferred = async { saveUserAccounts() }*/

                                // Wait for both async tasks to complete
                               /* saveAdminDeferred.await()
                                saveUserDeferred.await()*/



                                startActivity(
                                    Intent(
                                        mContext,
                                        MainActivity::class.java
                                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                                finish()
                            }
                        } else {
                            Toast.makeText(
                                mContext,
                                constants.SOMETHING_WENT_WRONG_MESSAGE,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                }
            }




    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            when {
                FACnicFront -> {
                    FACnicFrontURI = data?.data
                    imgSelectCnicFront.setImageResource(R.drawable.check_small)
                    Log.d("ImageSelection", "Front Image URI: $FACnicFrontURI")
                }
                FACnicBack -> {
                    FACnicBackURI = data?.data
                    imgSelectCnicBack.setImageResource(R.drawable.check_small)
                    Log.d("ImageSelection", "Back Image URI: $FACnicBackURI")
                }
                UserProfilePhoto -> {
                    Glide.with(mContext).load(data?.data).into(imgProfilePhoto)
                    imageURI = data?.data
                    Log.d("ImageSelection", "Profile Photo URI: $imageURI")
                }
            }
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_right_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.top_logout -> {
                showDialog()
                true
            }
            R.id.top_contactUs -> {
                Toast.makeText(applicationContext, "Available soon", Toast.LENGTH_LONG).show()
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun checkData() {

        var checkCounter: Int = 0

        if (sharedPrefManager.isFABankAdded()) {
            checkCounter++
            binding.tvHeaderUserBank.text = "Completed"
            binding.tvHeaderUserBank.setTextColor(Color.parseColor("#2F9B47"))
            binding.imgCheckUserBank.setImageResource(R.drawable.check_small)
        }


        if (sharedPrefManager.isUserPhotoAdded()) {
            checkCounter++
            binding.tvHeaderUserPhoto.text = "Completed"
            binding.tvHeaderUserPhoto.setTextColor(Color.parseColor("#2F9B47"))
            binding.imgCheckUserPhoto.setImageResource(R.drawable.check_small)
        }
        if (sharedPrefManager.isFACnicAdded()) {
            checkCounter++
            binding.tvHeaderUserCnic.text = "Completed"
            binding.tvHeaderUserCnic.setTextColor(Color.parseColor("#2F9B47"))
            binding.imgCheckUserCnic.setImageResource(R.drawable.check_small)
        }
        if (sharedPrefManager.isPhoneNumberAdded()) {
            checkCounter++
            binding.tvHeaderUserPhoneNumber.text = "Completed"
            binding.tvHeaderUserPhoneNumber.setTextColor(Color.parseColor("#2F9B47"))
            binding.imgCheckUserPhoneNumber.setImageResource(R.drawable.check_small)
        }


        if (checkCounter == 4) {
            binding.btnStart.visibility = View.VISIBLE

        }
    }

    fun showAddCnicDialog(type: String) {

        FACnicFront = false
        FACnicBack = false


        dialog = Dialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_upload_cnic)

        imgSelectCnicFront = dialog.findViewById<ImageView>(R.id.imgSelectCnicFront)
        imgSelectCnicBack = dialog.findViewById<ImageView>(R.id.imgSelectCnicBack)
        val tvSelectCnicFront = dialog.findViewById<TextView>(R.id.tvSelectCnicFront)
        val tvSelectCnicBack = dialog.findViewById<TextView>(R.id.tvSelectCnicBack)
        val tvHeaderDesc = dialog.findViewById<TextView>(R.id.tvHeaderDesc)
        val tvHeader = dialog.findViewById<TextView>(R.id.tvHeader)
        val btnUploadCNIC = dialog.findViewById<Button>(R.id.btnUploadCNIC)


        tvSelectCnicFront.setOnClickListener {
            FACnicBack=false
            FACnicFront=true
            val pickImage = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickImage, IMAGE_PICKER_REQUEST_CODE)

        }
        tvSelectCnicBack.setOnClickListener {

            FACnicFront=false
            FACnicBack=true
            val pickImage = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickImage, IMAGE_PICKER_REQUEST_CODE)


        }
        btnUploadCNIC.setOnClickListener {



                if (FACnicFrontURI != null && FACnicBackURI != null) {
                    Toast.makeText(mContext, "clicked", Toast.LENGTH_SHORT).show()
                    Toast.makeText(
                        mContext,
                        FACnicFront.toString() + "",
                        Toast.LENGTH_SHORT
                    ).show()
                    Toast.makeText(mContext, FACnicBack.toString() + "", Toast.LENGTH_SHORT)
                        .show()
                    lifecycleScope.launch {
                        addUserCNIC(FACnicFrontURI!!, FACnicBackURI!!, type)
                    }
                }
                    else Toast.makeText(mContext, "Please Select both photos", Toast.LENGTH_SHORT)
                        .show()

            }



        dialog.show()
    }

    fun showAddAccountDialog(type: String) {

        dialog = Dialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_add_account)

        val spBank = dialog.findViewById<Spinner>(R.id.spBank)
        val etAccountTittle = dialog.findViewById<EditText>(R.id.etAccountTittle)
        val etAccountNumber = dialog.findViewById<EditText>(R.id.etAccountNumber)
        val btnAddAccount = dialog.findViewById<Button>(R.id.btnAddAccount)

        btnAddAccount.setOnClickListener {

            addUserBankAccount(
                type,
                spBank.selectedItem.toString(),
                etAccountTittle.text.toString(),
                etAccountNumber.text.toString()
            )
        }

        dialog.show()
    }

    fun showPhotoDialog() {
        UserProfilePhoto = false
        dialog = Dialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_profile_photo_upload)

        imgProfilePhoto = dialog.findViewById<ImageView>(R.id.imgProfilePhoto)
        val tvSelect = dialog.findViewById<TextView>(R.id.tvSelect)
        val btnUplodProfile = dialog.findViewById<Button>(R.id.btnUplodProfile)

        tvSelect.setOnClickListener {
            UserProfilePhoto = true
            val pickImage = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickImage, IMAGE_PICKER_REQUEST_CODE)
        }

        btnUplodProfile.setOnClickListener {

            lifecycleScope.launch {
                if (imageURI != null) addUserPhoto(imageURI!!, "FAProfilePhoto")
                else Toast.makeText(mContext, "Please Select Image", Toast.LENGTH_SHORT).show()
            }


        }

        dialog.show()
    }

    fun addUserBankAccount(
        type: String,
        bankName: String,
        accountTittle: String,
        accountNumber: String
    ) {
            utils.startLoadingAnimation()
            lifecycleScope.launch {
                faViewModel.addFAAccount(
                    ModelBankAccount(
                        "",
                        bankName,
                        accountTittle,
                        accountNumber,
                        ""
                    )
                )
                    .observe(this@ActivityInvestorLoginDeatils) {
                        utils.endLoadingAnimation()
                        if (it == true) {
                            sharedPrefManager.putFABank(true)
                            Toast.makeText(
                                mContext,
                                constants.ACCOPUNT_ADDED_MESSAGE,
                                Toast.LENGTH_SHORT
                            ).show()
                            dialog.dismiss()
                            checkData()
                        } else {
                            Toast.makeText(
                                mContext,
                                constants.SOMETHING_WENT_WRONG_MESSAGE,
                                Toast.LENGTH_SHORT
                            ).show()
                            dialog.dismiss()
                        }

                    }
            }

        }





    suspend fun addUserPhoto(imageUri: Uri, type: String) {
       utils .startLoadingAnimation()
        faViewModel.uploadPhoto(imageUri, type)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    var modelFA: ModelFA = sharedPrefManager.getUser()
                    modelFA.photo = uri.toString()

                    lifecycleScope.launch {

                        faViewModel.updateFA(modelFA).observe(this@ActivityInvestorLoginDeatils) {
                            utils.endLoadingAnimation()
                            if (it == true) {
                                sharedPrefManager.saveUser(modelFA)
                                sharedPrefManager.putFaPhoto(true)
                                Toast.makeText(
                                    mContext,
                                    "Profile Photo Updated",
                                    Toast.LENGTH_SHORT
                                ).show()
                                dialog.dismiss()
                                checkData()
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

    suspend fun addUserCNIC(imageUriFront: Uri, imageUriBack: Uri, type: String) {
        utils.startLoadingAnimation()
        val frontUploadTask =
            faViewModel.uploadPhoto(imageUriFront, type + "Front") // from_investorFront
        val backUploadTask =
            faViewModel.uploadPhoto(imageUriBack, type + "Back")  // from_investorBack

        val downloadUrlTasks = mutableListOf<Task<Uri>>()

        downloadUrlTasks.add(frontUploadTask.continueWithTask { task ->
            if (task.isSuccessful) {
                return@continueWithTask frontUploadTask.result?.storage?.downloadUrl
            } else {
                task.exception?.let {
                    throw it
                }
            }
        })
        downloadUrlTasks.add(backUploadTask.continueWithTask { task ->
            if (task.isSuccessful) {
                return@continueWithTask backUploadTask.result?.storage?.downloadUrl
            } else {
                task.exception?.let {
                    throw it
                }
            }
        })


        Tasks.whenAllComplete(downloadUrlTasks).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUrls = mutableListOf<String>()
                for (downloadUrlTask in downloadUrlTasks) {
                    if (downloadUrlTask.isSuccessful) {
                        val downloadUrl = downloadUrlTask.result?.toString()
                        downloadUrl?.let { downloadUrls.add(it) }
                    }
                }

                    val user: ModelFA = sharedPrefManager.getUser()
                    user.cnic_front = downloadUrls[0] // Assuming the front image URL is at index 0
                    user.cnic_back = downloadUrls[1] // Assuming the front image URL is at index 0


                    // Update user in the database
                    lifecycleScope.launch {
                        faViewModel.updateFA(user)
                            .observe(this@ActivityInvestorLoginDeatils) { updateResult ->
                                utils.endLoadingAnimation()
                                if (updateResult == true) {

                                    sharedPrefManager.saveUser(user)
                                    sharedPrefManager.putFACnic(true)
                                    Toast.makeText(mContext, "CNIC Updated!", Toast.LENGTH_SHORT)
                                        .show()
                                    dialog.dismiss()
                                    checkData()
                                } else {
                                    Toast.makeText(
                                        mContext,
                                        constants.SOMETHING_WENT_WRONG_MESSAGE,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }


            } else {
                // Handle the failure
                task.exception?.let {
                    // Handle the exception
                }
            }
        }
    }

  /*  private fun saveAdminAccounts() {
        lifecycleScope.launch {
            userViewModel.getUserAccounts(constants.ADMIN)
                .addOnCompleteListener { task ->
                    utils.endLoadingAnimation()
                    if (task.isSuccessful) {
                        val list = ArrayList<ModelBankAccount>()
                        if (task.result.size() > 0) {
                            for (document in task.result) list.add(
                                document.toObject(
                                    ModelBankAccount::class.java
                                )
                            )
                            sharedPrefManager.putAdminBankList(list)
                            Toast.makeText(
                                mContext,
                                constants.ACCOPUNT_ADDED_MESSAGE,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else Toast.makeText(
                        mContext,
                        constants.SOMETHING_WENT_WRONG_MESSAGE,
                        Toast.LENGTH_SHORT
                    ).show()

                }
                .addOnFailureListener {
                    utils.endLoadingAnimation()
                    Toast.makeText(mContext, it.message + "", Toast.LENGTH_SHORT).show()

                }
        }

    }

    private fun saveUserAccounts() {
        lifecycleScope.launch {
            userViewModel.getUserAccounts(sharedPrefManager.getToken())
                .addOnCompleteListener { task ->
                    utils.endLoadingAnimation()
                    if (task.isSuccessful) {
                        val list = ArrayList<ModelBankAccount>()
                        if (task.result.size() > 0) {
                            for (document in task.result) list.add(
                                document.toObject(
                                    ModelBankAccount::class.java
                                )
                            )
                            sharedPrefManager.putInvestorBankList(list)
                            sharedPrefManager.getInvestorAccount()
                            Toast.makeText(
                                mContext,
                                constants.ACCOPUNT_ADDED_MESSAGE,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else Toast.makeText(
                        mContext,
                        constants.SOMETHING_WENT_WRONG_MESSAGE,
                        Toast.LENGTH_SHORT
                    ).show()

                }
                .addOnFailureListener {
                    utils.endLoadingAnimation()
                    Toast.makeText(mContext, it.message + "", Toast.LENGTH_SHORT).show()

                }
        }


    }
*/

    fun showDialog(): Boolean {
        val dialogView = LayoutInflater.from(this@ActivityInvestorLoginDeatils).inflate(R.layout.logout_dialog, null)
        val buttonYes: Button = dialogView.findViewById(R.id.btn_yes)
        val buttonNo: Button = dialogView.findViewById(R.id.btn_no)

        val builder = AlertDialog.Builder(this@ActivityInvestorLoginDeatils)
        builder.setView(dialogView)
        builder.setCancelable(true)
        var flag = false

        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        buttonYes.setOnClickListener {
            sharedPrefManager.clearWholeSharedPref()
            sharedPrefManager.logOut()
            startActivity(Intent(this@ActivityInvestorLoginDeatils, ActivityLogin::class.java))
            ActivityInvestorLoginDeatils().finish()
            alertDialog.dismiss()
        }

        buttonNo.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
        return false
    }
}