package de.bikebean.app.ui.drawer.log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.db.log.Log;

import static de.bikebean.app.ui.drawer.log.LogFragmentExtKt.onSendButtonClick;

public class LogFragment extends Fragment {

    LogViewModel logViewModel;

    private LogAdapter adapter;

    Button sendButton;
    private TextView noDataText;
    private Spinner spinner;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final @Nullable ViewGroup container,
                             final @Nullable Bundle savedInstanceState) {
        final @NonNull View v = inflater.inflate(R.layout.fragment_log, container, false);

        logViewModel = new ViewModelProvider(this).get(LogViewModel.class);

        recyclerView = v.findViewById(R.id.recyclerView);
        sendButton = v.findViewById(R.id.sendButton);
        noDataText = v.findViewById(R.id.noDataText4);
        spinner = v.findViewById(R.id.spinner2);
        initSpinner();
        initUserElements();

        initRecyclerView();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        logViewModel.getLastLevel().observe(getViewLifecycleOwner(), this::changeLevel);
    }

    @Override
    public void onResume() {
        super.onResume();

        final @NonNull MainActivity activity = (MainActivity) requireActivity();
        activity.setToolbarScrollEnabled(true);
        activity.resumeToolbarAndBottomSheet();
    }

    private void initSpinner() {
        final @NonNull ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(requireContext(),
                R.array.log_entries, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
    }

    private void initUserElements() {
        spinner.setSelection(getLevelPosition(logViewModel.getLastLevelSync()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final @NonNull AdapterView<?> parent,
                                       final @NonNull View view, int position, long id) {
                final @Nullable String newValue = getLevelString(position);

                /*
                 See if the "new" value is actually just the placeholder.
                 In that case, set the text underneath to reflect the last known status
                 */
                if (newValue.equals("0"))
                    return;

                /*
                 Get the last confirmed level status and
                 see if the value has changed from then.
                 If it has not changed, return
                 */
                if (position == getLevelPosition(logViewModel.getLastLevelSync()))
                    return;

                /* if it has changed, create a new pending state and fire it into the db */
                logViewModel.setInternal(getResources().getStringArray(R.array.log_entries_internal)[position]);
            }

            @Override
            public void onNothingSelected(final @NonNull AdapterView<?> parent) {
                // do nothing
            }
        });
        sendButton.setOnClickListener(v -> onSendButtonClick(this));
    }

    private void initRecyclerView() {
        adapter = new LogAdapter(requireContext(), logViewModel.getHigherThanLevel(Log.LEVEL.ERROR).getValue());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private @NonNull String getLevelString(int position) {
        final @NonNull String[] items = getResources().getStringArray(R.array.log_values);

        if (items.length > position)
            return items[position];
        else
            return "0";
    }

    private int getLevelPosition(final @NonNull Log.LEVEL level) {
        final @NonNull String[] items = getResources().getStringArray(R.array.log_values);

        for (int i=0; i<items.length; i++)
            if (items[i].equals(String.valueOf(level.ordinal())))
                return i;

        return 0;
    }

    private void changeLevel(final @NonNull List<Log> logs) {
        final @NonNull Log.LEVEL level;

        if (logs.size() > 0)
            level = Log.LEVEL.valueOf(logs.get(0).getMessage());
        else
            level = Log.LEVEL.ERROR;

        logViewModel.getHigherThanLevel(level).observe(this, this::updateAdapter);
    }

    private void updateAdapter(final @NonNull List<Log> log) {
        if (log.size() != 0) {
            adapter.setLog(log);
            noDataText.setVisibility(View.GONE);
        } else {
            adapter.setLog(null);
            noDataText.setVisibility(View.VISIBLE);
        }
    }
}
