package de.bikebean.app.ui.status.history;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import de.bikebean.app.R;

public class HistoryActivity extends AppCompatActivity {

    private Button positionHistoryButton, batteryHistoryButton;

    private LinearLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        positionHistoryButton = findViewById(R.id.positionButton);
        batteryHistoryButton = findViewById(R.id.batteryButton);

        tabs = findViewById(R.id.tabs);

        initUserElements();
    }

    public void showButtons(boolean show) {
        if (show)
            tabs.setVisibility(View.VISIBLE);
        else
            tabs.setVisibility(View.GONE);
    }

    private void initUserElements() {
        positionHistoryButton.setOnClickListener(this::navigateToTab);
        batteryHistoryButton.setOnClickListener(this::navigateToTab);
    }

    private void navigateToTab(View v) {
        if (v.getId() == R.id.positionButton)
            Navigation.findNavController(this, R.id.history)
                    .navigate(R.id.position_action);
        else
            Navigation.findNavController(this, R.id.history)
                    .navigate(R.id.battery_action);
    }
}
