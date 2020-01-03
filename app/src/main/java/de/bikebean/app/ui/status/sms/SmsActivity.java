package de.bikebean.app.ui.status.sms;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.bikebean.app.R;

public class SmsActivity extends AppCompatActivity {

    private SmsViewModel smsViewModel;

    private ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        smsViewModel = new ViewModelProvider(this).get(SmsViewModel.class);
        smsViewModel.getChat().observe(this, sms -> adapter.setSms(sms));

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
}
