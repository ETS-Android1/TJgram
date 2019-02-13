package org.michaelbel.tjgram.ui.main

import org.michaelbel.tjgram.data.consts.Category
import org.michaelbel.tjgram.data.consts.Sorting
import org.michaelbel.tjgram.data.entity.BaseResult
import org.michaelbel.tjgram.data.entity.Entry
import org.michaelbel.tjgram.data.remote.TjService

import java.util.ArrayList

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class MainPresenter(private val service: TjService) : MainContract.Presenter {

    companion object {
        private const val ENTRIES_COUNT = 20
    }

    private val disposables = CompositeDisposable()

    private lateinit var view: MainContract.View

    override fun create(view: MainContract.View) {
        this.view = view
        //wwsConnect
    }

    override fun complaintEntry(contentId: Int) {
        disposables.add(service.entryComplaint(contentId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ baseResult ->
                val status = baseResult.result
                if (status != null) {
                    view.complaintSent(status)
                }
        }, { view.complaintSent(false) }))
    }

    override fun entries(subsiteId: Long, sorting: String, offset: Int, upd: Boolean) {
        //service.userMeFavoritesEntries(count, offset)
        //service.subsiteTimeline(subsiteId, sorting, count, offset)
        disposables.add(service.timeline(Category.MAINPAGE, Sorting.RECENT, ENTRIES_COUNT, offset)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retry(5L)
                .subscribe({ (results) ->
                    val result = ArrayList(results)
                    val entriesCount = results.size
                    if (upd) {
                        view.updateEntries(result)
                    } else {
                        view.addEntries(result, entriesCount)
                    }
                }, { throwable -> view.errorEntries(throwable, upd) })
        )
    }

    override fun likeEntry(entry: Entry, sign: Int) {
        disposables.add(service.likeEntry(entry.id, sign).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
            { likesResult -> view.updateLikes(entry, likesResult) },
            { throwable -> view.updateLikesError(entry, throwable) }
        ))
    }

    /*@Override
    public void wwsConnect() {
        disposables.add(repository.wwsConnect().subscribe(
            event -> Timber.e("WWS connected successful: " + event.toString()),
            throwable -> Timber.e("WWS connected failure: " + throwable.getMessage())
        ));
    }

    @Override
    public void wwsDisconnect() {
        disposables.add(repository.wwsDisconnect().subscribe(
            event -> Timber.e("WWS disconnected successful: " + event.toString()),
            throwable -> Timber.e("WWS disconnected failure: " + throwable.getMessage()))
        );
    }

    @Override
    public void wwsEventStream() {
        disposables.add(repository.wwsEventStream().doOnNext(event -> {
            if (event instanceof TjWebSocket.Open) {
                Timber.e("WWS Open");
            } else if (event instanceof TjWebSocket.Closed) {
                Timber.e("WWS Closed");
            } else if (event instanceof TjWebSocket.QueuedMessage) {
                Timber.e("WWS Queued Message: " + ((TjWebSocket.QueuedMessage) event).message());
            } else if (event instanceof TjWebSocket.Message) {
                if (((TjWebSocket.Message) event).data() != null) {
                    String message = ((TjWebSocket.Message) event).data();
                    if (message != null) {
                        SocketResponse socketResponse = new SimpleJsonParser().getSocketData(message);
                        if (socketResponse.type.equals(SimpleJsonParser.TYPE_CONTENT_VOTED)) {
                            getView().sentWssResponse(socketResponse);
                        }
                    }
                }
            }
        }).subscribe(
                event -> Timber.e("WWS event stream subscribed successful: " + event.toString()),
                throwable -> Timber.e("WWS event stream subscribed failure: " + throwable.getMessage())
        ));
    }*/

    /*override fun wwsConnect(): Single<TjWebSocket.Open> {
        return webSocket.connect().observeOn(AndroidSchedulers.mainThread())
    }

    override fun wwsDisconnect(): Single<TjWebSocket.Closed> {
        return webSocket.disconnect(1000, "Disconnect").observeOn(AndroidSchedulers.mainThread())
    }

    override fun wwsEventStream(): Flowable<TjWebSocket.Event> {
        return webSocket.eventStream().observeOn(AndroidSchedulers.mainThread())
    }*/

    override fun destroy() {
        //this.wwsDisconnect();
        disposables.dispose()
    }
}