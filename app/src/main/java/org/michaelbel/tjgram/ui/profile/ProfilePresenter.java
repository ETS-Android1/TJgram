package org.michaelbel.tjgram.ui.profile;

import org.jetbrains.annotations.NotNull;
import org.michaelbel.tjgram.data.entity.User;
import org.michaelbel.tjgram.data.entity.UserResult;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.Headers;
import timber.log.Timber;

public class ProfilePresenter implements ProfileContract.Presenter {

    private ProfileContract.View view;
    private ProfileContract.Repository repository;
    private CompositeDisposable disposables = new CompositeDisposable();

    public ProfilePresenter(ProfileRepository repository) {
        this.repository = repository;
    }

    @NotNull
    @Override
    public ProfileContract.View getView() {
        return view;
    }

    @Override
    public void setView(@NotNull ProfileContract.View view) {
        this.view = view;
    }

    @Override
    public void authQr(@NotNull String token) {
        disposables.add(repository.authQr(token).subscribe(response -> {
            if (response.isSuccessful()) {
                Headers headers = response.headers();
                String xDeviceToken = headers.get("X-Device-Token");

                UserResult userResult = response.body();
                if (userResult != null) {
                    User user = userResult.getResult();
                    getView().setUser(user, xDeviceToken);
                }
            }
        }, throwable -> getView().setAuthError(throwable)));
    }

    @Override
    public void userMe() {
        disposables.add(repository.userMe().subscribe(userResult -> {
            User user = userResult.getResult();
            getView().setUser(user, "x");
        }, Timber::e));
    }

    @Override
    public void onDestroy() {
        disposables.dispose();
    }
}