package de.bikebean.app.ui.drawer.map;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.drawer.status.StateViewModel;
import de.bikebean.app.ui.drawer.status.location.LocationUri;
import de.bikebean.app.ui.drawer.status.location.LocationUrl;
import de.bikebean.app.ui.utils.Utils;

import static de.bikebean.app.ui.drawer.preferences.SettingsFragment.NAME_PREFERENCE;

public class MapFragmentViewModel extends StateViewModel {

    private final @NonNull MapStateRepository mRepository;

    private final LiveData<List<State>> mConfirmedLocationLat;
    private final LiveData<List<State>> mConfirmedLocationLng;
    private final LiveData<List<State>> mConfirmedLocationAcc;

    public MapFragmentViewModel(final @NonNull Application application) {
        super(application);

        mRepository = new MapStateRepository(application);

        mConfirmedLocationLat = mRepository.getConfirmedLocationLat();
        mConfirmedLocationLng = mRepository.getConfirmedLocationLng();
        mConfirmedLocationAcc = mRepository.getConfirmedLocationAcc();
    }

    LiveData<List<State>> getConfirmedLocationLat() {
        return mConfirmedLocationLat;
    }

    LiveData<List<State>> getConfirmedLocationLng() {
        return mConfirmedLocationLng;
    }

    LiveData<List<State>> getConfirmedLocationAcc() {
        return mConfirmedLocationAcc;
    }

    public void startShareIntent(final @NonNull Fragment fragment) {
        final @NonNull LocationUrl locationUrl = new LocationUrl();

        getConfirmedLocationLat().observe(fragment.getViewLifecycleOwner(), locationUrl::setLat);
        getConfirmedLocationLng().observe(fragment.getViewLifecycleOwner(), locationUrl::setLng);
        locationUrl.getUrl().observe(fragment.getViewLifecycleOwner(), string -> {
            final @Nullable Intent shareIntent = Utils.getShareIntent(string);

            if (shareIntent != null)
                fragment.startActivity(shareIntent);
            else
                Snackbar.make(
                        fragment.requireView(),
                        "Keine Position vorhanden!",
                        Snackbar.LENGTH_LONG
                ).show();
        });
    }

    public void startRouteIntent(final @NonNull Fragment fragment) {
        final @NonNull String bikeName = PreferenceManager
                .getDefaultSharedPreferences(fragment.requireContext())
                .getString(NAME_PREFERENCE, "bike");
        final @NonNull LocationUri locationUri = new LocationUri(bikeName);

        getConfirmedLocationLat().observe(fragment.getViewLifecycleOwner(), locationUri::setLat);
        getConfirmedLocationLng().observe(fragment.getViewLifecycleOwner(), locationUri::setLng);
        locationUri.getUri().observe(fragment.getViewLifecycleOwner(), string -> {
            final @NonNull Intent mapIntent = Utils.getRouteIntent(Uri.parse(string));

            if (mapIntent.resolveActivity(fragment.requireActivity().getPackageManager()) != null)
                fragment.startActivity(mapIntent);
            else
                Snackbar.make(
                        fragment.requireView(),
                        "Keine Position vorhanden!",
                        Snackbar.LENGTH_LONG
                ).show();
        });
    }

    public @Nullable Bitmap getMapMarkerBitmap(final MapMarkerCache.DRAWABLES drawable,
                                               final @ColorInt int color) {
        return mRepository.getMapMarkerBitmap(drawable, color);
    }

    public @NonNull Bitmap getMapMarkerBitmap(final Drawable drawable,
                                               final @ColorInt int color) {
        return mRepository.getMapMarkerBitmap(drawable, color);
    }

    public interface PositionHandler {
        void handlePosition(final boolean hasPosition);
    }

    public void hasPosition(LifecycleOwner l, PositionHandler positionHandler) {
        mConfirmedLocationLat.observe(l, states ->
                positionHandler.handlePosition(states != null && states.size() > 0)
        );
    }
}