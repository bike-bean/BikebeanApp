package de.bikebean.app.ui.status.sms.send;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import de.bikebean.app.ui.status.sms.SmsViewModel;

public class SmsSendIdGetter extends AsyncTask<String, Void, Integer> {

    private final WeakReference<SmsViewModel> smsViewModelReference;
    private final WeakReference<AsyncResponse> asyncResponseReference;

    public interface AsyncResponse {
        void onIdRetrieved(int id);
    }

    public SmsSendIdGetter(SmsViewModel smsViewModel, AsyncResponse delegate) {
        smsViewModelReference = new WeakReference<>(smsViewModel);
        asyncResponseReference = new WeakReference<>(delegate);
    }

    @Override
    protected Integer doInBackground(String... args) {
        return smsViewModelReference.get().getLatestId();
    }

    @Override
    protected void onPostExecute(Integer id) {
        asyncResponseReference.get().onIdRetrieved(id);
    }
}

