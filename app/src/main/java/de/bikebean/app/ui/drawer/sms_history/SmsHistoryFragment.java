package de.bikebean.app.ui.drawer.sms_history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.db.sms.Sms;

public class SmsHistoryFragment extends Fragment {

    private SmsViewModel smsViewModel;

    private ChatAdapter adapter;

    private TextView noDataText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final @NonNull View v = inflater.inflate(R.layout.fragment_history_sms, container, false);

        noDataText = v.findViewById(R.id.noDataText2);
        smsViewModel = new ViewModelProvider(this).get(SmsViewModel.class);

        initRecyclerView(v);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        smsViewModel.getChat().observe(getViewLifecycleOwner(), this::updateAdapterSms);
    }

    @Override
    public void onResume() {
        super.onResume();

        final @NonNull MainActivity activity = (MainActivity) requireActivity();
        activity.setToolbarScrollEnabled(false);
        activity.resumeToolbarAndBottomSheet();
    }

    private void initRecyclerView(final @NonNull View v) {
        final @Nullable RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        if (recyclerView == null)
            return;

        adapter = new ChatAdapter(requireContext(), smsViewModel.getChat().getValue());
        recyclerView.setAdapter(adapter);

        final @NonNull LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void updateAdapterSms(final @NonNull List<Sms> sms) {
        if (sms.size() != 0) {
            adapter.setSms(sms);
            noDataText.setVisibility(View.GONE);
        } else
            noDataText.setVisibility(View.VISIBLE);
    }
}
