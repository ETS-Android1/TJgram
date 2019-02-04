package org.michaelbel.tjgram.ui.main;

import org.jetbrains.annotations.NotNull;
import org.michaelbel.tjgram.data.entity.BaseResult;
import org.michaelbel.tjgram.data.entity.Entry;
import org.michaelbel.tjgram.data.wss.TjWebSocket;
import org.michaelbel.tjgram.data.wss.model.SocketResponse;
import org.michaelbel.tjgram.data.wss.parser.SimpleJsonParser;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

public class MainPresenter implements MainContract.Presenter {

    private static final int ENTRIES_COUNT = 20;

    private MainContract.View view;
    private MainContract.Repository repository;
    private CompositeDisposable disposables = new CompositeDisposable();

    public MainPresenter(MainRepository repository) {
        this.repository = repository;
    }

    @NotNull
    @Override
    public MainContract.View getView() {
        return view;
    }

    @Override
    public void setView(@NotNull MainContract.View view) {
        this.view = view;
    }

    @Override
    public void complaintEntry(int contentId) {
        disposables.add(repository.complaintEntry(contentId).subscribe((Consumer<BaseResult<Boolean>>) baseResult -> {
            Boolean status = baseResult.result;
            getView().complaintSent(status);
        }, throwable -> getView().complaintSent(false)));
    }

    @Override
    public void entries(long subsiteId, @NotNull String sorting, int offset, boolean upd) {
        disposables.add(repository.entries(subsiteId, sorting, ENTRIES_COUNT, offset)
            .retry(5L)
            .subscribe(entryResult -> {
                ArrayList<Entry> results = new ArrayList<>(entryResult.getResults());
                int entriesCount = results.size();
                if (upd) {
                    getView().updateEntries(results);
                } else {
                    getView().addEntries(results, entriesCount);
                }
            }, throwable -> getView().errorEntries(throwable, upd))
        );
    }

    @Override
    public void likeEntry(@NotNull Entry entry, int sign) {
        disposables.add(repository.likeEntry(entry.id, sign).subscribe(
            likesResult -> getView().updateLikes(entry, likesResult),
            throwable -> getView().updateLikesError(entry, throwable)
        ));
    }

    @Override
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
    }

    @Override
    public void onDestroy() {
        this.wwsDisconnect();
        disposables.dispose();
    }
}