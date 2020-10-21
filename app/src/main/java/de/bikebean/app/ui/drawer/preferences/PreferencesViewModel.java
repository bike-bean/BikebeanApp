package de.bikebean.app.ui.drawer.preferences;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class PreferencesViewModel extends AndroidViewModel {

    public final MutableLiveData<Release> newVersion = new MutableLiveData<>();

    public PreferencesViewModel(final @NonNull Application application) {
        super(application);
    }

    public void setNewVersion(final @NonNull String newVersionString) {
        this.newVersion.setValue(new Release(newVersionString, ""));
    }

    public void setNewVersion(final @NonNull Release newVersionRelease) {
        this.newVersion.setValue(newVersionRelease);
    }

    public MutableLiveData<Release> getNewVersion() {
        return newVersion;
    }
}
