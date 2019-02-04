package org.michaelbel.tjgram.ui.newphoto

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.*
import android.provider.MediaStore
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.fragment_gallery.*
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.ui.NewPhotoActivity
import org.michaelbel.tjgram.ui.newphoto.adapter.PhotoClickListener
import org.michaelbel.tjgram.ui.newphoto.adapter.PhotosAdapter
import org.michaelbel.tjgram.ui.newphoto.decoration.PhotoSpacingDecoration
import org.michaelbel.tjgram.utils.DeviceUtil
import org.michaelbel.tjgram.utils.FileUtil
import org.michaelbel.tjgram.utils.ViewUtil
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class GalleryFragment : Fragment(), PhotoClickListener {

    companion object {
        const val FRAGMENT_TAG = "newEntryFragment"

        const val EXTENSION_JPG = ".jpg"
        const val EXTENSION_JPEG = ".jpeg"
        const val EXTENSION_PNG = ".png"
        const val EXTENSION_BMP = ".bmp"

        const val REQUEST_PERMISSION = 101
        const val REQUEST_IMAGE_CAPTURE = 201
        const val REQUEST_SELECT_IMAGE = 301

        fun newInstance(): GalleryFragment {
            return GalleryFragment()
        }
    }

    private var activity: NewPhotoActivity? = null
    private var adapter: PhotosAdapter? = null

    private val photoFiles = ArrayList<File>()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                val args = data!!.extras
                if (args != null) {
                    val photo = args.get("data") as Bitmap
                    val imageUri = FileUtil.getImageUri(requireContext(), photo)
                    val file = File(FileUtil.getRealPathFromURI(requireContext(), imageUri))
                    activity!!.startFragment(NewEntryFragment.newInstance(file), FRAGMENT_TAG)
                }
            } else if (requestCode == REQUEST_SELECT_IMAGE) {
                try {
                    val uri = data!!.data
                    if (uri != null) {
                        val stream = activity!!.contentResolver.openInputStream(uri)
                        val bitmap = BitmapFactory.decodeStream(stream)
                        val imageUri = FileUtil.getImageUri(requireContext(), bitmap)
                        val file = File(FileUtil.getRealPathFromURI(requireContext(), imageUri))
                        activity!!.startFragment(NewEntryFragment.newInstance(file), FRAGMENT_TAG)
                    }
                } catch (e: FileNotFoundException) {
                    Timber.e(e)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION && grantResults.isNotEmpty()) {
            if (permissions[0] == Manifest.permission.READ_EXTERNAL_STORAGE) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadPhotos()
                }
            } else if (permissions[0] == Manifest.permission.CAMERA && permissions[1] == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                val cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val writeExternalPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED

                if (cameraPermission && writeExternalPermission) {
                    takePhoto()
                }
            } else if (permissions[0] == Manifest.permission.CAMERA) {
                val cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (cameraPermission) {
                    takePhoto()
                }
            } else if (permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                val writeExternalPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (writeExternalPermission) {
                    takePhoto()
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activity = getActivity() as NewPhotoActivity?
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_gallery, menu)
        menu.findItem(R.id.item_camera).icon = ViewUtil.getIcon(requireContext(), R.drawable.ic_camera, R.color.icon_active)
        menu.findItem(R.id.item_gallery).icon = ViewUtil.getIcon(requireContext(), R.drawable.ic_gallery, R.color.icon_active)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_gallery) {
            chooseFromGallery()
            return true
        } else if (item.itemId == R.id.item_camera) {
            takePhoto()
            return true
        }

        return false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.toolbar.navigationIcon = ViewUtil.getIcon(requireContext(), R.drawable.ic_clear, R.color.icon_active)
        activity!!.toolbar.setNavigationOnClickListener { activity!!.finish() }
        activity!!.toolbar_title.setText(R.string.choose_photo)

        adapter = PhotosAdapter(this)

        recycler_view.adapter = adapter
        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = GridLayoutManager(requireContext(), PhotosAdapter.SPAN_COUNT)
        recycler_view.addItemDecoration(PhotoSpacingDecoration(PhotosAdapter.SPAN_COUNT, DeviceUtil.dp(requireContext(), 0.25F)))

        turn_btn.setOnClickListener { loadPhotos() }

        if (savedInstanceState == null) {
            loadPhotos()
        }
    }

    override fun onPhotoClick(photo: File) {
        activity!!.startFragment(NewEntryFragment.newInstance(photo), FRAGMENT_TAG)
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

        stub_view.visibility = GONE
        recycler_view.visibility = VISIBLE

        photoFiles.clear()
        parseDir(Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM))
        parseDir(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES))
        parseDir(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS))
        parseDir(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS))
        adapter!!.addPhotos(photoFiles)
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
                if (file.name.toLowerCase().endsWith(EXTENSION_JPG)
                        || file.name.toLowerCase().endsWith(EXTENSION_JPEG)
                        || file.name.toLowerCase().endsWith(EXTENSION_PNG)
                        || file.name.toLowerCase().endsWith(EXTENSION_BMP)) {
                    photoFiles.add(file)
                }
            }
        }
    }

    private fun takePhoto() {
        if (Build.VERSION.SDK_INT >= 23) {
            val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val permissionCamera = arrayOf(Manifest.permission.CAMERA)
            val permissionStorage = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

            val cameraGranted = requireContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            val writeStorageGranted = requireContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

            //val cameraDenied = !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
            //val writeStorageDenied = !shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (!cameraGranted && !writeStorageGranted) {
                requestPermissions(permissions, REQUEST_PERMISSION)
            } else if (!cameraGranted) {
                requestPermissions(permissionCamera, REQUEST_PERMISSION)
            } else if (!writeStorageGranted) {
                requestPermissions(permissionStorage, REQUEST_PERMISSION)
            }

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
}