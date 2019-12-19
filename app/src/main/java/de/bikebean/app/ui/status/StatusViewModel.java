package de.bikebean.app.ui.status;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StatusViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public StatusViewModel() {
        mText = new MutableLiveData<>();

        //VERLINKEN AUF DIE JSON mit den Koordinaten, AKKUSTAND,
        mText.setValue("STATUSINFORMATIONEN");
    }

    public LiveData<String> getText() {
        return mText;
    }
}