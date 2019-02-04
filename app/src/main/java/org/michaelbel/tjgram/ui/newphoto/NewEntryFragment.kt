package org.michaelbel.tjgram.ui.newphoto

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity.RESULT_OK
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.view.View.GONE
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.fragment_new_entry.*
import org.koin.android.ext.android.inject
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.data.entity.AttachResponse
import org.michaelbel.tjgram.data.entity.Entry
import org.michaelbel.tjgram.data.enums.TJGRAM
import org.michaelbel.tjgram.ui.MainActivity.Companion.NEW_ENTRY_RESULT
import org.michaelbel.tjgram.ui.NewPhotoActivity
import org.michaelbel.tjgram.utils.ViewUtil
import java.io.File
import java.util.*

class NewEntryFragment : Fragment(), NewEntryContract.View {

    companion object {
        const val ARG_FILE = "file"

        fun newInstance(imageFile: File): NewEntryFragment {
            val args = Bundle()
            args.putSerializable(ARG_FILE, imageFile)
            val fragment = NewEntryFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var activity: NewPhotoActivity? = null

    private var titleText: String? = null
    private var introText: String? = null
    private val subsiteId = TJGRAM.toLong()
    private val attachesMap = HashMap<String, String>()

    private var imageFile: File? = null

    private val attachedMedia = ArrayList<AttachResponse>()

    val presenter: NewEntryContract.Presenter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activity = getActivity() as NewPhotoActivity?
        presenter.setView(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_new_entry, menu)
        menu.findItem(R.id.item_send).icon = ViewUtil.getIcon(requireContext(), R.drawable.ic_send, R.color.accent)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_send) {
            if (attachedMedia.size == 0) {
                Toast.makeText(requireContext(), R.string.msg_wait_image_load, Toast.LENGTH_SHORT).show()
            } else {
                progress_bar.visibility = View.VISIBLE
                postEntry()
            }
            return true
        }
        return false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.toolbar.navigationIcon = ViewUtil.getIcon(requireContext(), R.drawable.ic_arrow_back, R.color.icon_active)
        activity!!.toolbar.setNavigationOnClickListener {
            hideKeyboard(intro_edit_text)
            activity!!.finishFragment()
        }
        activity!!.toolbar_title.setText(R.string.post_entry)

        image_progress_bar.indeterminateDrawable.setColorFilter(ContextCompat.getColor(requireContext(), R.color.foreground), PorterDuff.Mode.MULTIPLY)

        title_edit_text.background = null
        ViewUtil.clearCursorDrawable(title_edit_text)

        intro_edit_text.background = null
        ViewUtil.clearCursorDrawable(intro_edit_text)

        imageFile = (arguments!!.getSerializable(ARG_FILE)) as File
        Picasso.get().load(Uri.fromFile(imageFile)).placeholder(R.drawable.placeholder_rectangle).error(R.drawable.error_rectangle).into(image)
        presenter.uploadFile(imageFile!!)
    }

    override fun photoUploaded(attach: AttachResponse) {
        attachedMedia.add(attach)
        hideImageLoading()
    }

    override fun uploadError(throwable: Throwable) {
        image_progress_bar.visibility = GONE
        Toast.makeText(requireContext(), R.string.err_loading_image, Toast.LENGTH_SHORT).show()
    }

    override fun setEntryCreated(entry: Entry) {
        progress_bar.visibility = GONE

        val intent = Intent()
        intent.putExtra(NEW_ENTRY_RESULT, true)
        activity!!.setResult(RESULT_OK, intent)
        activity!!.finish()
    }

    override fun setError(throwable: Throwable) {
        Toast.makeText(requireContext(), R.string.err_while_posting, Toast.LENGTH_SHORT).show()
    }

    private fun postEntry() {
        if (title_edit_text.text!!.isNotEmpty()) {
            titleText = title_edit_text.text!!.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(titleText)) {
                Toast.makeText(requireContext(), R.string.msg_enter_header, Toast.LENGTH_SHORT).show()
                return
            }
        }

        if (intro_edit_text.text!!.isNotEmpty()) {
            introText = intro_edit_text.text!!.toString().trim { it <= ' ' }
        }

        for (i in attachedMedia.indices) {
            attachesMap["attaches[$i][type]"] = ""
            attachesMap["attaches[$i][data][type]"] = attachedMedia[i].type
            attachesMap["attaches[$i][data][data][id]"] = ""
            attachesMap["attaches[$i][data][data][uuid]"] = attachedMedia[i].data.uuid
            attachesMap["attaches[$i][data][data][additionalData]"] = ""
            attachesMap["attaches[$i][data][data][type]"] = attachedMedia[i].data.type
            attachesMap["attaches[$i][data][data][color]"] = attachedMedia[i].data.color
            attachesMap["attaches[$i][data][data][width]"] = attachedMedia[i].data.width.toString()
            attachesMap["attaches[$i][data][data][height]"] = attachedMedia[i].data.height.toString()
            attachesMap["attaches[$i][data][data][size]"] = attachedMedia[i].data.size.toString()
            attachesMap["attaches[$i][data][data][name]"] = ""
            attachesMap["attaches[$i][data][data][origin]"] = ""
            attachesMap["attaches[$i][data][data][title]"] = ""
            attachesMap["attaches[$i][data][data][description]"] = ""
            attachesMap["attaches[$i][data][data][url]"] = ""
        }

        presenter.createEntry(titleText!!, introText!!, subsiteId, attachesMap)
    }

    private fun hideImageLoading() {
        val set = AnimatorSet()
        set.playTogether(
            ObjectAnimator.ofFloat(shadow_view, "alpha", 1f, 0f),
            ObjectAnimator.ofFloat(image_progress_bar, "scaleX", 1f, 0f),
            ObjectAnimator.ofFloat(image_progress_bar, "scaleY", 1f, 0f)
        )
        set.duration = 300
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                shadow_view?.visibility = GONE
                image_progress_bar?.visibility = GONE
            }
        })
        set.start()
    }

    private fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (!imm.isActive) {
            return
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}