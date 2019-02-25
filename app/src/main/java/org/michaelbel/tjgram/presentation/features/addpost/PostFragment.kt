package org.michaelbel.tjgram.presentation.features.addpost

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.net.Uri
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
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import kotlinx.android.synthetic.main.fragment_post.*
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.core.imageloader.ImageLoader
import org.michaelbel.tjgram.core.views.FileUtil
import org.michaelbel.tjgram.core.views.ViewUtil
import org.michaelbel.tjgram.data.api.consts.Subsites
import org.michaelbel.tjgram.data.entities.AttachResponse
import org.michaelbel.tjgram.presentation.App
import org.michaelbel.tjgram.presentation.features.main.MainActivity.Companion.EXTRA_NEW_ENTRY
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class PostFragment: Fragment(), GalleryAdapter.Listener, EasyPermissions.PermissionCallbacks {

    companion object {
        private const val MENU_ITEM_SENT = 10
        private const val MENU_ITEM_SENT_ACTIVE = 11
        private const val MENU_ITEM_PROGRESS = 12

        private const val EXTENSION_JPG = ".jpg"
        //private const val EXTENSION_JPEG = ".jpeg"
        private const val EXTENSION_PNG = ".png"
        private const val EXTENSION_BMP = ".bmp"

        /**
         * Значение не должно быть больше 128.
         */
        private const val REQUEST_PERMISSION_STORAGE = 2
        private const val REQUEST_PERMISSION_CAMERA = 3

        private const val REQUEST_IMAGE_CAPTURE = 201
        private const val REQUEST_SELECT_IMAGE = 301

        private const val LIST_SPAN_COUNT = 1

        private const val ANIM_DURATION = 300L

        private var INTERPOLATOR = LinearInterpolator()

        fun newInstance() = PostFragment()
    }

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var factory: PostVMFactory

    private lateinit var postVM: PostVM

    private var menuItem: MenuItem? = null
    private var menuIconMode: Int = MENU_ITEM_SENT

    private val attachedMedia = ArrayList<AttachResponse>()
    private val map = HashMap<String, String>()

    private val photoFiles = ArrayList<File>()
    private lateinit var adapter: GalleryAdapter

    private var photoFile: File? = null
    private var titleText: String? = null
    private var introText: String? = null

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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Timber.d("onPermissionsGranted")
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this)
                    .setRationale(R.string.rationale_storage)
                    .setPositiveButton(R.string.dialog_action_ok)
                    .setNegativeButton(R.string.dialog_action_cancel)
                    .build()
                    .show()
        } else {
            loadPhotos()

            /*if (requestCode == REQUEST_PERMISSION_STORAGE) {
                loadPhotos()
            } else if (requestCode == REQUEST_PERMISSION_CAMERA) {
                takePhoto()
            }*/
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
                        postVM.uploadFile(photoFile)
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
        App[requireActivity().application].createPostComponent().inject(this)

        adapter = GalleryAdapter(this, imageLoader)

        postVM = ViewModelProviders.of(requireActivity(), factory)[PostVM::class.java]
        postVM.attaches.observe(this, androidx.lifecycle.Observer {
            attachedMedia.add(it)
            postEntry()
        })
        postVM.attachError.observe(this, androidx.lifecycle.Observer {
            Toast.makeText(requireContext(), R.string.err_while_posting, Toast.LENGTH_SHORT).show()
        })
        postVM.entryCreate.observe(this, androidx.lifecycle.Observer {
            //progress_bar.visibility = GONE

            val intent = Intent()
            intent.putExtra(EXTRA_NEW_ENTRY, true)
            requireActivity().setResult(RESULT_OK, intent)
            requireActivity().finish()
        })
        postVM.entryError.observe(this, androidx.lifecycle.Observer {
            Toast.makeText(requireContext(), R.string.err_while_posting, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_post, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL

        ViewCompat.setElevation(imagesLayout, resources.getDimension(R.dimen.post_images_layout_elevation))
        imagesLayout.setOnClickListener {loadPhotos()}

        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.hasFixedSize()
        recyclerView.addItemDecoration(PhotosSpacingDecoration(LIST_SPAN_COUNT, resources.getDimension(R.dimen.post_item_decoration_margin).toInt()))

        placeholderText.visibility = VISIBLE

        titleEditText.background = null
        ViewUtil.clearCursorDrawable(titleEditText)
        titleEditText.addTextChangedListener(object: TextWatcher {
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

    override fun onPhotoClick(photo: File) {
        photoFile = photo
        imageLoader.load(Uri.fromFile(photo), imageView, R.drawable.placeholder_rectangle, R.drawable.error_rectangle) {
            showRemoveIcon()
        }

        cardImage.visibility = VISIBLE
        animateImagesLayout(true)
    }

    override fun onCameraClick() {
        takePhoto()
    }

    override fun onGalleryClick() {
        chooseFromGallery()
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_STORAGE)
    private fun loadPhotos() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (EasyPermissions.hasPermissions(requireContext(), permission)) {
            // fixme использовать MediaStorage
            parseDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM))
            parseDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))
            parseDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
            //parseDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS))
            adapter.swapData(photoFiles)

            recyclerView.visibility = VISIBLE
            placeholderText.visibility = GONE

            animateImagesLayout(false)
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_storage), REQUEST_PERMISSION_STORAGE, permission)
        }
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
        // todo Добавить camera permission
        // Пока что будем надеяться, что юзер не запретит доступ к камере

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    private fun chooseFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_SELECT_IMAGE)
    }

    private fun postEntry() {
        titleText = titleEditText.text.toString().trim { it <= ' ' }
        introText = introEditText.text.toString().trim { it <= ' ' }

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

        // todo make safety strings
        postVM.createEntry(titleText!!, introText!!, Subsites.TJGRAM, map)
    }

    private fun animateImagesLayout(hide: Boolean) {
        val anim = imagesLayout.animate().translationY(if (hide) imagesLayout.height.toFloat() else 0F)
        anim.duration = ANIM_DURATION
        anim.setListener(object: AnimatorListenerAdapter() {
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

    private inner class PhotosSpacingDecoration(
            private val spanCount: Int, private val spacing: Int
    ): RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount

            if (position < spanCount) {
                outRect.top = spacing
            }

            outRect.bottom = spacing
        }
    }
}