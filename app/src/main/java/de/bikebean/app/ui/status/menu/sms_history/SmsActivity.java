package de.bikebean.app.ui.status.menu.sms_history;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.bikebean.app.R;
import de.bikebean.app.db.sms.Sms;

public class SmsActivity extends AppCompatActivity {

    private SmsViewModel smsViewModel;

    private ChatAdapter adapter;

    private TextView noDataText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        smsViewModel = new ViewModelProvider(this).get(SmsViewModel.class);
        smsViewModel.getChat().observe(this, this::updateAdapterSms);

        noDataText = findViewById(R.id.noDataText2);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new ChatAdapter(this, smsViewModel.getChat().getValue());
        recyclerView.setAdapter(adapter);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void updateAdapterSms(List<Sms> sms) {
        if (sms.size() != 0) {
            adapter.setSms(sms);
            noDataText.setVisibility(View.GONE);
        } else
            noDataText.setVisibility(View.VISIBLE);
    }
}
