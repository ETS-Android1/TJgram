package org.michaelbel.tjgram.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_main.*
import org.koin.android.ext.android.inject
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.data.TjConfig
import org.michaelbel.tjgram.data.constants.Subsites
import org.michaelbel.tjgram.data.entity.Entry
import org.michaelbel.tjgram.data.entity.Likes
import org.michaelbel.tjgram.data.entity.LikesResult
import org.michaelbel.tjgram.data.wss.model.SocketResponse
import org.michaelbel.tjgram.ui.main.adapter.EntriesAdapter
import org.michaelbel.tjgram.ui.main.adapter.EntriesListener
import org.michaelbel.tjgram.ui.main.decoration.EntrySpacingDecoration
import org.michaelbel.tjgram.utils.DeviceUtil
import org.michaelbel.tjgram.utils.FragmentUtils.getListenerOrThrowException
import org.michaelbel.tjgram.utils.NetworkUtil
import org.michaelbel.tjgram.utils.customtabs.Browser
import org.michaelbel.tjgram.utils.recycler.LinearSmoothScrollerMiddle
import java.util.*

class MainFragment : Fragment(), MainContract.View, EntriesListener, SwipeRefreshLayout.OnRefreshListener {

    interface Listener {
        fun showLoginSnack()
    }

    companion object {
        const val ARG_SORTING = "sorting"

        fun newInstance(sorting: String): MainFragment {
            val args = Bundle()
            args.putString(ARG_SORTING, sorting)
            val fragment = MainFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var adapter = EntriesAdapter(this)

    private var offset: Int = 0
    private var sorting: String? = null
    private var loading = true

    private lateinit var listener: Listener
    //var swapListener: SwapListener

    val presenter: MainContract.Presenter by inject()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = getListenerOrThrowException(Listener::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.view = this
        //presenter.wwsConnect()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.accent))
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(requireContext(), R.color.primary))
        swipeRefreshLayout.setOnRefreshListener(this)
        swipeRefreshLayout.isRefreshing = true

        val linearLayoutManager = object : LinearLayoutManager(requireContext()) {
            override fun supportsPredictiveItemAnimations(): Boolean {
                return false
            }

            override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State?, position: Int) {
                val linearSmoothScroller = LinearSmoothScrollerMiddle(requireContext())
                linearSmoothScroller.targetPosition = position
                startSmoothScroll(linearSmoothScroller)
            }
        }
        linearLayoutManager.orientation = RecyclerView.VERTICAL

        /*val animator = object : DefaultItemAnimator() {
            override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
                return true
            }
        }*/

        //recycler_view.itemAnimator = animator
        //recycler_view.setHasFixedSize(true)
        //recycler_view.setItemViewCacheSize(20)
        //recycler_view.adapter = EntriesAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.addItemDecoration(EntrySpacingDecoration(1, DeviceUtil.dp(requireContext(), 6F)))
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && adapter.itemCount != 0 && loading) {
                    loading = false
                    presenter.entries(Subsites.TJGRAM.toLong(), sorting!!, offset, false)
                }
            }
        })

        retryButton.setOnClickListener {
            swipeRefreshLayout.isRefreshing = true
            presenter.entries(Subsites.TJGRAM.toLong(), sorting!!, offset, false)
        }

        offset = 0
        sorting = if (arguments != null) arguments!!.getString(ARG_SORTING) else ""
        if (sorting != null) {
            presenter.entries(Subsites.TJGRAM.toLong(), sorting!!, offset, false)
        }

        presenter.wwsEventStream()
    }

    override fun onRefresh() {
        if (NetworkUtil.isNetworkConnected(requireContext())) {
            offset = 0
            presenter.entries(Subsites.TJGRAM.toLong(), sorting!!, offset, adapter.itemCount != 0)
        } else {
            swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun doLoginFirst() {
        listener.showLoginSnack()
    }

    override fun onAuthorClick(authorId: Int) {
        val userLink = String.format(Locale.getDefault(), TjConfig.TJ_USER, authorId)
        if (DeviceUtil.isAppInstalled(requireContext(), TjConfig.TJ_PACKAGE_NAME)) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(userLink)))
        } else {
            Browser.openUrl(requireContext(), userLink)
        }
    }

    override fun onAuthorLongClick(authorId: Int): Boolean {
        DeviceUtil.copyToClipboard(requireContext(), String.format(Locale.getDefault(), TjConfig.TJ_USER, authorId))
        Toast.makeText(requireContext(), R.string.msg_author_url_copied, Toast.LENGTH_SHORT).show()
        return true
    }

    override fun popupItemClick(itemId: Int, entryId: Int): Boolean {
        if (itemId == R.id.item_report) {
            presenter.complaintEntry(entryId)
        } else if (itemId == R.id.item_open_entry) {
            val entryLink = String.format(Locale.getDefault(), TjConfig.TJ_ENTRY, entryId)

            if (DeviceUtil.isAppInstalled(requireContext(), TjConfig.TJ_PACKAGE_NAME)) {
                val entryIntent = Intent(Intent.ACTION_VIEW, Uri.parse(entryLink))
                startActivity(entryIntent)
            } else {
                Browser.openUrl(requireContext(), entryLink)
            }
        } else if (itemId == R.id.item_copy_link) {
            DeviceUtil.copyToClipboard(requireContext(), String.format(Locale.getDefault(), TjConfig.TJ_ENTRY, entryId))
            Toast.makeText(requireContext(), R.string.msg_link_copied, Toast.LENGTH_SHORT).show()
        } else if (itemId == R.id.item_share_link) {
            val intent = Intent(Intent.ACTION_SEND).setType("text/plain")
            intent.putExtra(Intent.EXTRA_TEXT, String.format(Locale.getDefault(), TjConfig.TJ_ENTRY, entryId))
            startActivity(Intent.createChooser(intent, getString(R.string.menu_share_link)))
        } else {
            return false
        }

        return true
    }

    override fun complaintSent(status: Boolean) {
        Toast.makeText(requireContext(), if (status) R.string.msg_complaint_sent else R.string.err_complaint_sent, Toast.LENGTH_SHORT).show()
    }

    override fun addEntries(entries: ArrayList<Entry>, entriesCount: Int) {
        offset += entriesCount
        loading = true

        adapter.setEntries(entries)

        errorView.visibility = GONE
        swipeRefreshLayout.isRefreshing = false
    }

    override fun updateEntries(entries: ArrayList<Entry>) {
        offset += entries.size

        adapter.swapEntries(entries)
        errorView.visibility = GONE
        swipeRefreshLayout.isRefreshing = false
    }

    override fun errorEntries(throwable: Throwable, upd: Boolean) {
        if (upd) {
            Toast.makeText(requireContext(), R.string.err_load_data, Toast.LENGTH_SHORT).show()
            return
        }

        swipeRefreshLayout.isRefreshing = false

        if (adapter.itemCount == 0) {
            errorView.visibility = VISIBLE
        }
    }

    override fun likeEntry(entry: Entry, sign: Int) {
        presenter.likeEntry(entry, sign)
    }

    override fun updateLikes(entry: Entry, likesResult: LikesResult) {
        val likes = Likes(likesResult.result.count, likesResult.result.isHidden, likesResult.result.isLiked, likesResult.result.summ)
        entry.likes = likes
        adapter.changeLikes(entry)
    }

    override fun updateLikesError(entry: Entry, throwable: Throwable) {
        if (NetworkUtil.isHttpStatusCode(throwable, 400)) {
            Toast.makeText(requireContext(), R.string.msg_like_old_entry, Toast.LENGTH_SHORT).show()
        }

        adapter.changeLikes(entry)
    }

    override fun sentWssResponse(socket: SocketResponse) {
        val likes = Likes(socket.count.toInt())
        val entry = Entry(socket.id.toInt(), likes)

        /*val list = ArrayList<Entry>()
        list.add(entry)
        adapter.swapEntry(list)*/
    }

    /*override fun onStop() {
        super.onStop()
        presenter.wwsDisconnect()
    }*/

    /*override fun onResume() {
        super.onResume()
        presenter.wwsConnect()
    }*/

    /*fun updateAdapter() {
        offset = 0
        presenter.entries(TJGRAM.toLong(), sorting!!, offset, adapter!!.itemCount != 0)
        Toast.makeText(requireContext(), "Запись успешно опубликована!", Toast.LENGTH_SHORT).show()
    }*/

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}