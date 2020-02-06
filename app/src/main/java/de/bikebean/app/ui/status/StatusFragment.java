package de.bikebean.app.ui.status;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import java.util.Objects;

import de.bikebean.app.R;
import de.bikebean.app.ui.status.battery.BatteryStatusFragment;
import de.bikebean.app.ui.status.location.LocationStatusFragment;
import de.bikebean.app.ui.status.status.StatusStatusFragment;

public class StatusFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_status, container, false);

        // init sub-fragments
        getChildFragmentManager().beginTransaction()
                .replace(R.id.include0, new LocationStatusFragment())
                .replace(R.id.include1, new StatusStatusFragment())
                .replace(R.id.include2, new BatteryStatusFragment())
                .disallowAddToBackStack()
                .commit();

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get Activity
        FragmentActivity act = Objects.requireNonNull(getActivity());

        // Show the Action Bar
        ActionBar actionbar = ((AppCompatActivity) act).getSupportActionBar();
        Objects.requireNonNull(actionbar).show();
        actionbar.setTitle(R.string.status_text);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.status_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_sms_history:
                Navigation.findNavController(Objects.requireNonNull(getView())).navigate(R.id.sms_history_action);
                return true;
            case R.id.menu_item_settings:
                Navigation.findNavController(Objects.requireNonNull(getView())).navigate(R.id.settings_action);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
