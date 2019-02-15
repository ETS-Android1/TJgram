package org.michaelbel.tjgram.modules.addpost

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_post.*
import org.koin.android.ext.android.inject
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.data.consts.Subsites
import org.michaelbel.tjgram.data.entities.AttachResponse
import org.michaelbel.tjgram.data.entities.Entry
import org.michaelbel.tjgram.modules.MainActivity.Companion.NEW_ENTRY_RESULT
import org.michaelbel.tjgram.modules.addpost.adapter.GalleryAdapter
import org.michaelbel.tjgram.modules.addpost.decoration.PhotoSpacingDecoration
import org.michaelbel.tjgram.utils.DeviceUtil
import org.michaelbel.tjgram.utils.FileUtil
import org.michaelbel.tjgram.utils.ViewUtil
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import kotlin.collections.ArrayList

class PostFragment : Fragment(), PostContract.View, GalleryAdapter.PhotoClickListener {

    companion object {
        private const val MENU_ITEM_SENT = 10
        private const val MENU_ITEM_SENT_ACTIVE = 11
        private const val MENU_ITEM_PROGRESS = 12

        private const val EXTENSION_JPG = ".jpg"
        //private const val EXTENSION_JPEG = ".jpeg"
        private const val EXTENSION_PNG = ".png"
        private const val EXTENSION_BMP = ".bmp"

        private const val REQUEST_PERMISSION = 101
        private const val REQUEST_IMAGE_CAPTURE = 201
        private const val REQUEST_SELECT_IMAGE = 301

        private const val ANIM_DURATION = 300L

        private var INTERPOLATOR = LinearInterpolator()

        fun newInstance(): PostFragment {
            return PostFragment()
        }
    }

    private var menuItem: MenuItem? = null
    private var menuIconMode: Int = MENU_ITEM_SENT

    private val attachedMedia = ArrayList<AttachResponse>()
    private val map = HashMap<String, String>()

    private val photoFiles = ArrayList<File>()
    private val adapter: GalleryAdapter = GalleryAdapter()

    private var photoFile: File? = null
    private var titleText: String? = null
    private var introText: String? = null

    private val presenter: PostContract.Presenter by inject()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                val args = data!!.extras
                if (args != null) {
                    val photo = args.get("data") as Bitmap
                    val imageUri = FileUtil.getImageUri(requireContext(), photo)
                    val file = File(FileUtil.getRealPathFromURI(requireContext(), imageUri))
                    onPhotoClick(file)
                }
            } else if (requestCode == REQUEST_SELECT_IMAGE) {
                try {
                    val uri = data!!.data
                    if (uri != null) {
                        val stream = activity!!.contentResolver.openInputStream(uri)
                        val bitmap = BitmapFactory.decodeStream(stream)
                        val imageUri = FileUtil.getImageUri(requireContext(), bitmap)
                        val file = File(FileUtil.getRealPathFromURI(requireContext(), imageUri))
                        onPhotoClick(file)
                    }
                } catch (e: FileNotFoundException) {
                    Timber.e(e)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.isNotEmpty()) {
                if (permissions[0] == Manifest.permission.READ_EXTERNAL_STORAGE) {
                    val readStorageGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (readStorageGranted) {
                        loadPhotos()
                    } else {
                        Timber.e("permission decline")
                        showPlaceholder()
                    }
                } /*else if (permissions[0] == Manifest.permission.CAMERA && permissions[1] == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    val cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val writeExternalPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED

                    if (cameraPermission && writeExternalPermission) {
                        takePhoto()
                    }
                }*/ else if (permissions[0] == Manifest.permission.CAMERA) {
                    val cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (cameraPermission) {
                        takePhoto()
                    }
                } /*else if (permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    val writeExternalPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (writeExternalPermission) {
                        takePhoto()
                    }
                }*/
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val drawable: Drawable?

        when (menuIconMode) {
            MENU_ITEM_SENT -> drawable = ViewUtil.getIcon(requireContext(), R.drawable.ic_send, R.color.icon_active_unfocused)
            MENU_ITEM_SENT_ACTIVE -> drawable = ViewUtil.getIcon(requireContext(), R.drawable.ic_send, R.color.accent)
            else -> drawable = AnimatedVectorDrawableCompat.create(requireContext(), R.drawable.ic_anim_downloading_begin)
        }

        menuItem = menu.add("").setIcon(drawable).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        if (drawable is Animatable) {
            (drawable as Animatable).start()
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item == menuItem) {
            when (menuIconMode) {
                MENU_ITEM_SENT -> Toast.makeText(requireContext(), R.string.msg_enter_header, Toast.LENGTH_SHORT).show()
                MENU_ITEM_SENT_ACTIVE -> {
                    if (attachedMedia.size == 0) {
                        postEntry()
                    } else {
                        presenter.uploadFile(photoFile)
                    }

                    menuIconMode = MENU_ITEM_PROGRESS
                    requireActivity().invalidateOptionsMenu()
                }
                else -> return false
            }
            return true
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        presenter.create(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_post, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.addListener(this)

        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL

        ViewCompat.setElevation(imagesLayout, DeviceUtil.dp(requireContext(), 1.5F).toFloat())

        imagesLayout.setOnClickListener {loadPhotos()}

        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.hasFixedSize()
        recyclerView.addItemDecoration(PhotoSpacingDecoration(1, DeviceUtil.dp(requireContext(), 3F)))

        placeholderText.visibility = VISIBLE

        titleEditText.background = null
        ViewUtil.clearCursorDrawable(titleEditText)
        titleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val textEmpty = s.toString().trim { it <= ' ' }.isNotEmpty()
                menuIconMode = if (textEmpty) MENU_ITEM_SENT_ACTIVE else MENU_ITEM_SENT
                requireActivity().invalidateOptionsMenu()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        introEditText.background = null
        ViewUtil.clearCursorDrawable(introEditText)

        removeIcon.setOnClickListener {
            imageView.setImageDrawable(null)
            removeIcon.visibility = GONE
            cardImage.visibility = GONE
            animateImagesLayout(false)
        }

        if (savedInstanceState == null) {
            loadPhotos()
        }
    }

    override fun onDestroy() {
        presenter.destroy()
        super.onDestroy()
    }

    override fun onPhotoClick(photo: File) {
        photoFile = photo

        Picasso.get().load(Uri.fromFile(photo)).placeholder(R.drawable.placeholder_rectangle).error(R.drawable.error_rectangle)
            .into(imageView, object : Callback {
                    override fun onSuccess() {
                        showRemoveIcon()
                    }
                    override fun onError(e: Exception) {
                        showRemoveIcon()
                    }
                })

        cardImage.visibility = VISIBLE
        animateImagesLayout(true)
    }

    override fun onCameraClick() {
        takePhoto()
    }

    override fun onGalleryClick() {
        chooseFromGallery()
    }

    override fun photoUploaded(attach: AttachResponse) {
        attachedMedia.add(attach)
        postEntry()
    }

    override fun uploadError(throwable: Throwable) {
        //Toast.makeText(requireContext(), R.string.err_loading_image, Toast.LENGTH_SHORT).show()
        Toast.makeText(requireContext(), R.string.err_while_posting, Toast.LENGTH_SHORT).show()
    }

    override fun setEntryCreated(entry: Entry) {
        //progress_bar.visibility = GONE

        val intent = Intent()
        intent.putExtra(NEW_ENTRY_RESULT, true)
        activity!!.setResult(RESULT_OK, intent)
        activity!!.finish()
    }

    override fun setError(throwable: Throwable) {
        Toast.makeText(requireContext(), R.string.err_while_posting, Toast.LENGTH_SHORT).show()
    }

    private fun loadPhotos() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION)
                return
            }
        }

        /*if (Build.VERSION.SDK_INT >= 23) {
            if (requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder(requireContext())
                        .setTitle(R.string.permission_denied)
                        .setMessage(R.string.msg_permission_storage)
                        .setPositiveButton(R.string.settings) { _, _ ->
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            intent.data = Uri.fromParts("package", requireContext().packageName, null)
                            startActivity(intent)
                        }
                        .setNegativeButton(R.string.action_cancel, null)
                        .show()
                    return
                }

                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION)
                return
            }
        }*/

        parseDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM))
        parseDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))
        parseDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
        //parseDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS))
        adapter.swapData(photoFiles)

        recyclerView.visibility = VISIBLE
        placeholderText.visibility = GONE

        animateImagesLayout(false)
    }

    private fun parseDir(dir: File) {
        val files = dir.listFiles()
        if (files != null) {
            parseFileList(files)
        }
    }

    private fun parseFileList(files: Array<File>) {
        for (file in files) {
            if (file.isDirectory) {
                if (!file.name.toLowerCase().startsWith(".")) {
                    parseDir(file)
                }
            } else {
                if (file.name.toLowerCase().endsWith(EXTENSION_JPG) /*|| file.name.toLowerCase().endsWith(EXTENSION_JPEG)*/
                        || file.name.toLowerCase().endsWith(EXTENSION_PNG) || file.name.toLowerCase().endsWith(EXTENSION_BMP)) {
                    photoFiles.add(file)
                }
            }
        }
    }

    private fun takePhoto() {
        if (Build.VERSION.SDK_INT >= 23) {
            //val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val permissionCamera = arrayOf(Manifest.permission.CAMERA)
            //val permissionStorage = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

            val cameraGranted = requireContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            //val writeStorageGranted = requireContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

            //val cameraDenied = !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
            //val writeStorageDenied = !shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (cameraGranted.not()) {
                requestPermissions(permissionCamera, REQUEST_PERMISSION)
            }

            /*if (!cameraGranted && !writeStorageGranted) {
                requestPermissions(permissions, REQUEST_PERMISSION)
            } else if (!cameraGranted) {
                requestPermissions(permissionCamera, REQUEST_PERMISSION)
            } else if (!writeStorageGranted) {
                requestPermissions(permissionStorage, REQUEST_PERMISSION)
            }*/

            /*if (!cameraGranted && !writeStorageGranted) {
                if (cameraDenied || writeStorageDenied) {
                    AlertDialog.Builder(requireContext())
                        .setTitle(R.string.permission_denied)
                        .setMessage(R.string.msg_permission_camera_storage)
                        .setPositiveButton(R.string.settings) { _, _ ->
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            intent.data = Uri.fromParts("package", requireContext().packageName, null)
                            startActivity(intent)
                        }
                        .setNegativeButton(R.string.action_cancel, null)
                        .show()
                    return
                }

                requestPermissions(permissions, REQUEST_PERMISSION)
                return
            }*/
        }

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    private fun chooseFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_SELECT_IMAGE)
    }

    private fun postEntry() {
        titleText = titleEditText.text!!.toString().trim { it <= ' ' }
        introText = introEditText.text!!.toString().trim { it <= ' ' }

        for (i in attachedMedia.indices) {
            map["attaches[$i][type]"] = ""
            map["attaches[$i][data][type]"] = attachedMedia[i].type
            map["attaches[$i][data][data][id]"] = ""
            map["attaches[$i][data][data][uuid]"] = attachedMedia[i].data.uuid
            map["attaches[$i][data][data][additionalData]"] = ""
            map["attaches[$i][data][data][type]"] = attachedMedia[i].data.type
            map["attaches[$i][data][data][color]"] = attachedMedia[i].data.color
            map["attaches[$i][data][data][width]"] = attachedMedia[i].data.width.toString()
            map["attaches[$i][data][data][height]"] = attachedMedia[i].data.height.toString()
            map["attaches[$i][data][data][size]"] = attachedMedia[i].data.size.toString()
            map["attaches[$i][data][data][name]"] = ""
            map["attaches[$i][data][data][origin]"] = ""
            map["attaches[$i][data][data][title]"] = ""
            map["attaches[$i][data][data][description]"] = ""
            map["attaches[$i][data][data][url]"] = ""
        }

        presenter.createEntry(titleText!!, introText!!, Subsites.TJGRAM.toLong(), map)
    }

    private fun animateImagesLayout(hide: Boolean) {
        val anim = imagesLayout.animate().translationY(if (hide) imagesLayout.height.toFloat() else 0F)
        anim.duration = ANIM_DURATION
        anim.setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (hide) {
                    imagesLayout.visibility = GONE
                }
            }

            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                if (!hide) {
                    imagesLayout.visibility = VISIBLE
                }
            }
        })
        anim.start()
    }

    private fun showPlaceholder() {
        recyclerView.visibility = GONE
        placeholderText.visibility = VISIBLE

        animateImagesLayout(false)
    }

    private fun showRemoveIcon() {
        val set = AnimatorSet()
        set.playTogether(
            ObjectAnimator.ofFloat(removeIcon, "scaleX", 0F, 1F),
            ObjectAnimator.ofFloat(removeIcon, "scaleY", 0F, 1F)
        )
        set.duration = ANIM_DURATION
        set.interpolator = INTERPOLATOR
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                super.onAnimationStart(animation, isReverse)
                removeIcon.visibility = VISIBLE
            }
        })

        if (!removeIcon.isVisible) {
            set.start()
        }
    }
}