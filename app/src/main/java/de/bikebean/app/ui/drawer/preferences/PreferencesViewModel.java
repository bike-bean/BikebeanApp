package de.bikebean.app.ui.drawer.preferences;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class PreferencesViewModel extends AndroidViewModel {

    public final MutableLiveData<VersionJsonParser.AppRelease> newVersion = new MutableLiveData<>();

    public PreferencesViewModel(final @NonNull Application application) {
        super(application);
    }

    public void setNewVersion(final @NonNull String newVersionString) {
        this.newVersion.setValue(new VersionJsonParser.AppRelease(newVersionString, ""));
    }

    public void setNewVersion(final @NonNull VersionJsonParser.AppRelease newVersionRelease) {
        this.newVersion.setValue(newVersionRelease);
    }

    public MutableLiveData<VersionJsonParser.AppRelease> getNewVersion() {
        return newVersion;
    }
}
