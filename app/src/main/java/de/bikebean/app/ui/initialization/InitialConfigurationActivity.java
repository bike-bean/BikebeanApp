package de.bikebean.app.ui.initialization;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import de.bikebean.app.R;
import de.bikebean.app.ui.utils.Utils;
import de.bikebean.app.ui.utils.PermissionsRationaleDialog;
import de.bikebean.app.ui.main.status.StateViewModel;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;
import de.bikebean.app.ui.main.status.menu.sms_history.SmsViewModel;

public class InitialConfigurationActivity extends AppCompatActivity {

    private StateViewModel stateViewModel;
    private SmsViewModel smsViewModel;
    private LogViewModel logViewModel;
    private SharedPreferences sharedPreferences;

    // UI elements
    private TextInputLayout input;
    private EditText editText;
    private ProgressBar progressBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_configuration);

        stateViewModel = new ViewModelProvider(this).get(StateViewModel.class);
        smsViewModel = new ViewModelProvider(this).get(SmsViewModel.class);
        logViewModel = new ViewModelProvider(this).get(LogViewModel.class);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // setup UI elements
        Button continueButton = findViewById(R.id.button);
        input = findViewById(R.id.textInput);
        editText = input.getEditText();
        progressBar = findViewById(R.id.progressBar2);

        continueButton.setOnClickListener(v -> onButtonClicked());

        stateViewModel.insertInitialStates(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        assert true;
    }

    private void onButtonClicked() {
        if (editText != null) {
            String number = editText.getText().toString();

            if (number.isEmpty())
                input.setError(getString(R.string.number_error));
            else if (!number.substring(0, 1).equals("+"))
                input.setError(getString(R.string.number_subtitle));
            else {
                // TODO: warn the user for the warningNumber SMS which is to be sent.
                sharedPreferences.edit()
                        .putString("number", number)
                        .apply();

                progressBar.setVisibility(View.VISIBLE);
                getPermissions();
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    private void getPermissions() {
        if (Utils.getPermissions(this, Utils.PERMISSION_KEY.SMS, () ->
                new PermissionsRationaleDialog(this, Utils.PERMISSION_KEY.SMS).show(
                        getSupportFragmentManager(),
                        "smsRationaleDialog"
                )
        ))
            fetchSms();
    }

    private void fetchSms() {
        smsViewModel.fetchSmsSync(this, stateViewModel, logViewModel,
                sharedPreferences.getString("number", "")
        );
        finish();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == Utils.PERMISSION_KEY.SMS.ordinal()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                fetchSms();
            else
                Toast.makeText(this,
                        getString(R.string.warning_sms_permission),
                        Toast.LENGTH_LONG
                ).show();
        }
    }
}
