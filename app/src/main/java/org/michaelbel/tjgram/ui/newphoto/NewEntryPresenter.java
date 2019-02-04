package org.michaelbel.tjgram.ui.newphoto;

import org.jetbrains.annotations.NotNull;
import org.michaelbel.tjgram.data.entity.AttachResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class NewEntryPresenter implements NewEntryContract.Presenter {

    private NewEntryContract.View view;
    private NewEntryContract.Repository repository;
    private CompositeDisposable disposables = new CompositeDisposable();

    public NewEntryPresenter(NewEntryRepository repository) {
        this.repository = repository;
    }

    @NotNull
    @Override
    public NewEntryContract.View getView() {
        return view;
    }

    @Override
    public void setView(@NotNull NewEntryContract.View view) {
        this.view = view;
    }

    @Override
    public void uploadFile(@NotNull File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

        disposables.add(repository.uploadFile(body).subscribe(arrayListOsnovaResult -> {
            ArrayList<AttachResponse> attaches = arrayListOsnovaResult.result;
            AttachResponse attach = attaches.get(0);
            getView().photoUploaded(attach);
        }, throwable -> getView().uploadError(throwable)));
    }

    @Override
    public void createEntry(@NotNull String title, @NotNull String text, long subsiteId, @NotNull Map<String, String> attaches) {
        disposables.add(repository.createEntry(title, text, subsiteId, attaches).subscribe(
            entryResult -> getView().setEntryCreated(entryResult.result),
            throwable -> getView().setError(throwable))
        );
    }

    @Override
    public void onDestroy() {
        disposables.dispose();
    }
}