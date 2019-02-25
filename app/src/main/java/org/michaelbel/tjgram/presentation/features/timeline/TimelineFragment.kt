package org.michaelbel.tjgram.presentation.features.timeline

import android.content.Intent
import android.graphics.Rect
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
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_timeline.*
import org.michaelbel.tjgram.R
import org.michaelbel.tjgram.core.customtabs.Browser
import org.michaelbel.tjgram.core.imageloader.ImageLoader
import org.michaelbel.tjgram.core.views.DeviceUtil
import org.michaelbel.tjgram.core.views.LinearSmoothScrollerMiddle
import org.michaelbel.tjgram.data.api.consts.Subsites
import org.michaelbel.tjgram.data.api.results.EntriesResult
import org.michaelbel.tjgram.data.entities.Entry
import org.michaelbel.tjgram.data.entities.Likes
import org.michaelbel.tjgram.data.net.NetworkUtil
import org.michaelbel.tjgram.data.net.TjConfig
import org.michaelbel.tjgram.presentation.App
import org.michaelbel.tjgram.presentation.features.main.MainVM
import java.lang.String.format
import java.util.*
import javax.inject.Inject

class TimelineFragment: Fragment(), EntriesAdapter.Listener {

    companion object {
        private const val EXTRA_SORTING = "sorting"
        private const val LIST_SPAN_COUNT = 1

        fun newInstance(sorting: String): TimelineFragment {
            val args = Bundle()
            args.putString(EXTRA_SORTING, sorting)
            val fragment = TimelineFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var offset: Int = 0
    private var loading = true

    private var likedEntry: Entry? = null

    private lateinit var adapter: EntriesAdapter

    private lateinit var mainVM: MainVM
    private lateinit var timelineVM: TimelineVM

    @Inject
    lateinit var factory: TimelineVMFactory

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App[requireActivity().application].createTimelineComponent().inject(this)

        adapter = EntriesAdapter(this, imageLoader)

        mainVM = ViewModelProviders.of(requireActivity())[MainVM::class.java]

        timelineVM = ViewModelProviders.of(requireActivity(), factory)[TimelineVM::class.java]
        timelineVM.dataLoading.observe(this, androidx.lifecycle.Observer {
            swipeRefreshLayout.isRefreshing = it
        })
        timelineVM.reportSent.observe(this, androidx.lifecycle.Observer {
            Toast.makeText(requireContext(), if (it) R.string.msg_complaint_sent else R.string.err_complaint_sent, Toast.LENGTH_SHORT).show()
        })
        timelineVM.items.observe(this, androidx.lifecycle.Observer {
            offset += it.size
            loading = true
            adapter.setEntries(it)
        })
        timelineVM.dataLoadingError.observe(this, androidx.lifecycle.Observer {
            errorView.visibility = VISIBLE
        })
        timelineVM.likedEntry.observe(this, androidx.lifecycle.Observer {
            val likes = Likes(count = it.count, isHidden = it.isHidden, isLiked = it.isLiked, summ = it.summ)
            likedEntry?.likes = likes
            adapter.changeLikes(likedEntry)
        })
        timelineVM.likedEntryError.observe(this, androidx.lifecycle.Observer {
            if (NetworkUtil.isHttpStatusCode(it, 400)) {
                Toast.makeText(requireContext(), R.string.msg_like_old_entry, Toast.LENGTH_SHORT).show()
            }

            adapter.changeLikes(likedEntry)
        })
        timelineVM.refreshedItems.observe(this, androidx.lifecycle.Observer {
            offset += it.size

            adapter.swapEntries(it)
            errorView.visibility = GONE
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_timeline, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainVM.changeToolbarTitle(R.string.app_name)

        initSwipeRefresh()
        initRecyclerView()
        initEmptyView()

        offset = 0
        timelineVM.entries(Subsites.TJGRAM, EntriesResult.Sorting.NEW, offset)
    }

    private fun initSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.accent))
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(requireContext(), R.color.primary))
        swipeRefreshLayout.setOnRefreshListener {
            if (NetworkUtil.isNetworkConnected(requireContext()).not()) {
                swipeRefreshLayout.isRefreshing = false
            } else {
                offset = 0
                timelineVM.entriesRefresh(Subsites.TJGRAM, EntriesResult.Sorting.NEW, offset)
            }
        }
        swipeRefreshLayout.isRefreshing = true
    }

    private fun initRecyclerView() {
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

        recyclerView.adapter = adapter
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(false)
        recyclerView.addItemDecoration(EntriesSpacingDecoration(LIST_SPAN_COUNT, resources.getDimension(R.dimen.timeline_entries_space_margin).toInt()))
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && adapter.itemCount != 0 && loading) {
                    loading = false
                    timelineVM.entriesNext(Subsites.TJGRAM, EntriesResult.Sorting.NEW, offset)
                }
            }
        })
    }

    private fun initEmptyView() {
        retryButton.setOnClickListener {
            swipeRefreshLayout.isRefreshing = true
            timelineVM.entriesNext(Subsites.TJGRAM, EntriesResult.Sorting.NEW, offset)
        }
    }

    override fun doLoginFirst() {
        mainVM.showSnackBarMessage(R.string.msg_login_first)
    }

    override fun onAuthorClick(authorId: Int) {
        val userLink = format(Locale.getDefault(), TjConfig.TJ_USER, authorId)
        if (DeviceUtil.isAppInstalled(requireContext(), TjConfig.TJ_PACKAGE_NAME)) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(userLink)))
        } else {
            Browser.openUrl(requireContext(), userLink)
        }
    }

    override fun onAuthorLongClick(authorId: Int): Boolean {
        DeviceUtil.copyToClipboard(requireContext(), format(Locale.getDefault(), TjConfig.TJ_USER, authorId))
        Toast.makeText(requireContext(), R.string.msg_author_url_copied, Toast.LENGTH_SHORT).show()
        return true
    }

    override fun popupItemClick(itemId: Int, entryId: Int): Boolean {
        if (itemId == R.id.item_report) {
            timelineVM.complaintEntry(entryId)
        } else if (itemId == R.id.item_open_entry) {
            val entryLink = format(Locale.getDefault(), TjConfig.TJ_ENTRY, entryId)
            if (DeviceUtil.isAppInstalled(requireContext(), TjConfig.TJ_PACKAGE_NAME)) {
                val entryIntent = Intent(Intent.ACTION_VIEW, Uri.parse(entryLink))
                startActivity(entryIntent)
            } else {
                Browser.openUrl(requireContext(), entryLink)
            }
        } else if (itemId == R.id.item_copy_link) {
            DeviceUtil.copyToClipboard(requireContext(), format(Locale.getDefault(), TjConfig.TJ_ENTRY, entryId))
            Toast.makeText(requireContext(), R.string.msg_link_copied, Toast.LENGTH_SHORT).show()
        } else if (itemId == R.id.item_share_link) {
            val intent = Intent(Intent.ACTION_SEND).setType("text/plain")
            intent.putExtra(Intent.EXTRA_TEXT, format(Locale.getDefault(), TjConfig.TJ_ENTRY, entryId))
            startActivity(Intent.createChooser(intent, getString(R.string.menu_share_link)))
        } else {
            return false
        }

        return true
    }

    /*override fun errorEntries(throwable: Throwable, upd: Boolean) {
        if (upd) {
            Toast.makeText(requireContext(), R.string.err_load_data, Toast.LENGTH_SHORT).show()
            return
        }

        swipeRefreshLayout.isRefreshing = false

        if (adapter.itemCount == 0) {
            errorView.visibility = VISIBLE
        }
    }*/

    override fun likeEntry(entry: Entry, sign: Int) {
        likedEntry = entry
        timelineVM.likeEntry(entry, sign)
    }

    private inner class EntriesSpacingDecoration(
            private val spanCount: Int, private val spacing: Int
    ): RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view)
            if (position < spanCount) {
                outRect.top = spacing
            }
            outRect.bottom = spacing
        }
    }
}