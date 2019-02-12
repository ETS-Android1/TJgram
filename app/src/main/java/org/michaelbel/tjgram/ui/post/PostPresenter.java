package org.michaelbel.tjgram.ui.post;

import org.jetbrains.annotations.NotNull;
import org.michaelbel.tjgram.data.entity.AttachResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PostPresenter implements PostContract.Presenter {

    private PostContract.View view;
    private PostContract.Repository repository;
    private CompositeDisposable disposables = new CompositeDisposable();

    public PostPresenter(PostRepository repository) {
        this.repository = repository;
    }

    @NotNull
    @Override
    public PostContract.View getView() {
        return view;
    }

    @Override
    public void setView(@NotNull PostContract.View view) {
        this.view = view;
    }

    @Override
    public void uploadFile(File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

        disposables.add(repository.uploadFile(body).subscribe(baseResult -> {
            ArrayList<AttachResponse> attaches = baseResult.getResult();
            AttachResponse attach = attaches.get(0);
            getView().photoUploaded(attach);
        }, throwable -> getView().uploadError(throwable)));
    }

    @Override
    public void createEntry(@NotNull String title, @NotNull String text, long subsiteId, @NotNull Map<String, String> attaches) {
        disposables.add(repository.createEntry(title, text, subsiteId, attaches).subscribe(
            entryResult -> getView().setEntryCreated(entryResult.getResult()),
            throwable -> getView().setError(throwable))
        );
    }

    @Override
    public void onDestroy() {
        disposables.dispose();
    }
}