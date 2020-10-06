package de.bikebean.app.ui.main.status.menu.log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import de.bikebean.app.R;
import de.bikebean.app.db.BikeBeanRoomDatabase;
import de.bikebean.app.db.log.Log;

public class LogActivity extends AppCompatActivity {

    private LogViewModel logViewModel;

    private LogAdapter adapter;

    private Button sendButton;
    private TextView noDataText;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        logViewModel = new ViewModelProvider(this).get(LogViewModel.class);
        setupObservers();

        sendButton = findViewById(R.id.sendButton);
        noDataText = findViewById(R.id.noDataText4);
        spinner = findViewById(R.id.spinner2);
        initSpinner();
        initUserElements();

        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initRecyclerView();
    }

    private void setupObservers() {
        logViewModel.getLastLevel().observe(this, this::changeLevel);
    }

    private void initSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.log_entries, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
    }

    private void initUserElements() {
        spinner.setSelection(getLevelPosition(logViewModel.getLastLevelSync()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newValue = getLevelString(position);

                // See if the "new" value is actually just the placeholder.
                // In that case, set the text underneath to reflect the last known status
                if (newValue.equals("0"))
                    return;

                // Get the last confirmed level status and
                // see if the value has changed from then.
                // If it has not changed, return
                if (position == getLevelPosition(logViewModel.getLastLevelSync()))
                    return;

                // if it has changed, create a new pending state and fire it into the db
                logViewModel.setInternal(getResources().getStringArray(R.array.log_entries_internal)[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
        sendButton.setOnClickListener(this::generateLogAndUpload);
    }

    private void generateLogAndUpload(View v) {
        logViewModel.d("Exporting database...");
        Snackbar.make(v,
                "Fehlerbericht senden...",
                Snackbar.LENGTH_LONG
        ).show();

        GithubGistUploader githubGistUploader = BikeBeanRoomDatabase.createReport(
                getApplicationContext(), logViewModel, this::notifyUploadSuccess
        );

        githubGistUploader.execute();
    }

    private void notifyUploadSuccess(boolean success) {
        View parentLayout = findViewById(R.id.sendButton);

        if (success)
            Snackbar.make(parentLayout,
                    "Fehlerbericht gesendet",
                    Snackbar.LENGTH_LONG
            ).show();
        else
            Snackbar.make(parentLayout,
                    "Fehlerbericht konnte nicht gesendet werden!",
                    Snackbar.LENGTH_LONG
            ).show();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new LogAdapter(this, logViewModel.getHigherThanLevel(Log.LEVEL.ERROR).getValue());
        recyclerView.setAdapter(adapter);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private String getLevelString(int position) {
        String[] items = getResources().getStringArray(R.array.log_values);
        return items[position];
    }

    private int getLevelPosition(Log.LEVEL level) {
        String[] items = getResources().getStringArray(R.array.log_values);

        for (int i=0; i<items.length; i++)
            if (items[i].equals(String.valueOf(level.ordinal())))
                return i;

        return 0;
    }

    private void changeLevel(@NonNull List<Log> logs) {
        Log.LEVEL level;

        if (logs.size() > 0)
            level = Log.LEVEL.valueOf(logs.get(0).getMessage());
        else
            level = Log.LEVEL.ERROR;

        logViewModel.getHigherThanLevel(level).observe(this, this::updateAdapter);
    }

    private void updateAdapter(@NonNull List<Log> log) {
        if (log.size() != 0) {
            adapter.setLog(log);
            noDataText.setVisibility(View.GONE);
        } else {
            adapter.setLog(null);
            noDataText.setVisibility(View.VISIBLE);
        }
    }
}
