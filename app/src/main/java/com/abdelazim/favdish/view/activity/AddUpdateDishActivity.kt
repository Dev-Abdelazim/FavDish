package com.abdelazim.favdish.view.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdelazim.favdish.R
import com.abdelazim.favdish.application.FavDishApplication
import com.abdelazim.favdish.databinding.ActivityAddUpdateDishBinding
import com.abdelazim.favdish.databinding.DialogCustomImageSelectionBinding
import com.abdelazim.favdish.databinding.DialogCustomListBinding
import com.abdelazim.favdish.model.entities.FavDish
import com.abdelazim.favdish.utils.Constants
import com.abdelazim.favdish.view.adapters.CustomListItemAdapter
import com.abdelazim.favdish.viewmodel.FavDishViewModel
import com.abdelazim.favdish.viewmodel.FavDishViewModelFactory
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

@Suppress("DEPRECATION")
class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAddUpdateDishBinding
    private var imagePath: String = ""
    private lateinit var customListDialog: Dialog
    private var dish: FavDish? = null

    private val galleryImageContract = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ){
        it?.let { uri ->
            // this code convert image to Bitmap from uri
            val image: Bitmap = MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, uri)
            Glide.with(this)
                .load(image)
                .centerCrop()
                .into(binding.ivDishImage)
            imagePath = saveImageToInternalStorage(image)
        }
    }

    private val mFavDishViewModel: FavDishViewModel by viewModels{
        FavDishViewModelFactory((application as FavDishApplication).repository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.EDIT_DISH_KEY)){
            dish = intent.getParcelableExtra(Constants.EDIT_DISH_KEY)
        }

        setUpActionBar()

        dish?.let {
            setDishDataInUpdateView(it)
        }

        binding.ivAddDishImage.setOnClickListener(this)
        binding.etType.setOnClickListener(this)
        binding.etCategory.setOnClickListener(this)
        binding.btnAddDish.setOnClickListener(this)
    }

    private fun setUpActionBar(){
        setSupportActionBar(binding.addDishToolbar)
        supportActionBar?.let {
            if (dish != null && dish!!.id != 0){
                it.title = resources.getString(R.string.edit_dish_toolbar_title)
            }else{
                it.title = resources.getString(R.string.add_dish)
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.addDishToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        if (v != null){
            when(v.id){
                R.id.iv_add_dish_image -> {
                    customImageSelectionDialog()
                    return
                }

                R.id.et_type -> {
                    customItemsListDialog(
                        title = resources.getString(R.string.title_select_dish_type),
                        items = Constants.dishTypesList,
                        selection = Constants.DISH_TYPE
                    )
                    return
                }

                R.id.et_category -> {
                    customItemsListDialog(
                        title = resources.getString(R.string.title_select_dish_category),
                        items = Constants.dishCategoryList,
                        selection = Constants.DISH_CATEGORY
                    )
                    return
                }

                R.id.btn_add_dish -> {
                    val title = binding.etTitle.text.toString().trim{ it <= ' ' }
                    val type = binding.etType.text.toString().trim{ it <= ' ' }
                    val category = binding.etCategory.text.toString().trim{ it <= ' ' }
                    val ingredients = binding.etIngredients.text.toString().trim{ it <= ' ' }
                    val cookTime = binding.etCookTime.text.toString().trim{ it <= ' ' }
                    val directionToCook = binding.etDirection.text.toString().trim{ it <= ' ' }

                    var dishId = 0
                    var isLoadingFromInternet = false
                    var isFavorite = false

                    dish?.let {
                        dishId = it.id
                        isLoadingFromInternet = it.isLoadingFromInternet
                        isFavorite = it.isFavoriteDish
                    }

                    isValidInputsAndSaveDish(
                        imageUri = imagePath,
                        title = title,
                        type = type,
                        category = category,
                        ingredients = ingredients,
                        cookTime = cookTime,
                        directionToCook = directionToCook
                    ){
                        // add dish button action
                        val dish = FavDish(
                            id = dishId,
                            imageUri = imagePath,
                            isLoadingFromInternet = isLoadingFromInternet,
                            title = title,
                            type = type,
                            category = category,
                            ingredients = ingredients,
                            cookingTime = cookTime,
                            directionToCook = directionToCook,
                            isFavoriteDish = isFavorite
                        )

                        if (dishId == 0){
                            mFavDishViewModel.insert(dish)
                            Toast.makeText(
                                this,
                                resources.getString(R.string.save_dish_msg),
                                Toast.LENGTH_LONG
                            ).show()
                        }else{
                            mFavDishViewModel.updateDishDetails(dish)
                            Toast.makeText(
                                this,
                                resources.getString(R.string.update_dish_msg),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        finish()
                    }

                }
            }
        }
    }

    private fun customImageSelectionDialog(){
        val dialog = Dialog(this)
        val dBinding = DialogCustomImageSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(dBinding.root)

        dBinding.tvCamera.setOnClickListener{
            requestPermissionsFromUser {
                // open camera and take an image
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA)
            }
            dialog.dismiss()
        }

        dBinding.tvGallery.setOnClickListener{
            requestPermissionsFromUser {
                galleryImageContract.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            dialog.dismiss()
        }

        dialog.show()
    }


    // this function handel permissions and action after grant permissions
    private fun requestPermissionsFromUser(buttonAction: () -> Unit){
        val readWriteStoragePermission = arrayListOf<String>()

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2){
            readWriteStoragePermission.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            readWriteStoragePermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }else{
            readWriteStoragePermission.add(Manifest.permission.READ_MEDIA_IMAGES)
        }

        readWriteStoragePermission.add(Manifest.permission.CAMERA)


        Dexter.withContext(this)
            .withPermissions(
                readWriteStoragePermission
            )
            .withListener(
                object : MultiplePermissionsListener{
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let{
                            if (report.areAllPermissionsGranted()) {
                                // do button action
                                buttonAction()

                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }
                }
            )
            .onSameThread()
            .check()
    }

    // alert dialog show massage if user denied permissions
    // and help user to enabled permissions for settings
    private fun showRationalDialogForPermissions(){
        AlertDialog.Builder(this)
            .setTitle(R.string.permission_alert_dialog_title)
            .setMessage(R.string.permission_denied_massage)
            .setPositiveButton(R.string.permission_positive_button_title){_,_ ->
                try{
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                    Toast.makeText(this, R.string.application_package_error_massage, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.permission_negative_button_title){ dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // create image uri from file
    /*private fun createImageUri(): Uri?{
        val image = File(applicationContext.filesDir, "camera_photo.png")
        return FileProvider.getUriForFile(
            applicationContext,
            "com.abdelazim.favdish.fileProvider",
            image
        )
    }*/

    // create image uri from Bitmap
    private fun saveImageToInternalStorage(bitmap: Bitmap): String{
        val wrapper = ContextWrapper(applicationContext)

        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }

        return file.absolutePath
    }

    // custom dialog show list of items
    private fun customItemsListDialog(
        title: String,
        items: ArrayList<String>,
        selection: String){

        customListDialog = Dialog(this)
        val dBinding = DialogCustomListBinding.inflate(layoutInflater)

        customListDialog.setContentView(dBinding.root)
        dBinding.tvDialogTitle.text = title
        dBinding.rvList.layoutManager = LinearLayoutManager(this)

        val adapter = CustomListItemAdapter(this, items, selection)
        dBinding.rvList.adapter = adapter
        customListDialog.show()
    }

    fun selectedListItem(item: String, selection: String){
        when(selection){
            Constants.DISH_TYPE -> {
                customListDialog.dismiss()
                binding.etType.setText(item)
            }
            else -> {
                customListDialog.dismiss()
                binding.etCategory.setText(item)
            }
        }
    }

    // check if all inputs are valid
    // and handel save dish button click action
    private fun isValidInputsAndSaveDish(
        imageUri: String?,
        title: String,
        type: String,
        category: String,
        ingredients: String,
        cookTime: String,
        directionToCook: String,
        onClick: () -> Unit
    ){
        when{
            imageUri == null -> {
                Toast.makeText(
                    this,
                    resources.getString(R.string.err_msg_select_dish_image),
                    Toast.LENGTH_SHORT
                ).show()
            }
            title.isEmpty() -> {
                Toast.makeText(
                    this,
                    resources.getString(R.string.err_msg_enter_dish_titel),
                    Toast.LENGTH_SHORT
                ).show()
            }

            type.isEmpty() -> {
                Toast.makeText(
                    this,
                    resources.getString(R.string.err_msg_select_dish_type),
                    Toast.LENGTH_SHORT
                ).show()
            }

            category.isEmpty() -> {
                Toast.makeText(
                    this,
                    resources.getString(R.string.err_msg_select_dish_category),
                    Toast.LENGTH_SHORT
                ).show()
            }

            ingredients.isEmpty() -> {
                Toast.makeText(
                    this,
                    resources.getString(R.string.err_msg_enter_dish_ingredients),
                    Toast.LENGTH_SHORT
                ).show()
            }

            cookTime.isEmpty() -> {
                Toast.makeText(
                    this,
                    resources.getString(R.string.err_msg_enter_dish_cook_time),
                    Toast.LENGTH_SHORT
                ).show()
            }

            directionToCook.isEmpty() -> {
                Toast.makeText(
                    this,
                    resources.getString(R.string.err_msg_enter_dish_instructions),
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                onClick()
            }
        }
    }

    // setup all info in update dish layout
    private fun setDishDataInUpdateView(dish: FavDish){
        if (dish.id != 0){
            Glide.with(this)
                .load(dish.imageUri)
                .centerCrop()
                .into(binding.ivDishImage)

            binding.etTitle.setText(dish.title)
            binding.etType.setText(dish.type)
            binding.etCategory.setText(dish.category)
            binding.etIngredients.setText(dish.ingredients)
            binding.etCookTime.setText(dish.cookingTime)
            binding.etDirection.setText(dish.directionToCook)

            binding.btnAddDish.text = resources.getString(R.string.lbl_update_dish)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if(requestCode == CAMERA){
                data?.extras?.let {
                    val thumbnail: Bitmap = data.extras!!.get("data") as Bitmap
                    Glide.with(this)
                        .load(thumbnail)
                        .centerCrop()
                        .into(binding.ivDishImage)

                    imagePath = saveImageToInternalStorage(thumbnail)
                    Log.d("Path", "onActivityResult: $imagePath")
                }
            }
        }
    }

    companion object{
        const val IMAGE_DIRECTORY: String = "FavDishImages"
        const val CAMERA: Int = 1
    }

}


