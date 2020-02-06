package de.bikebean.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.StateViewModel;
import de.bikebean.app.ui.status.sms.SmsViewModel;

public class InitialConfigurationActivity extends AppCompatActivity {

    public static final double INITIAL_WIFI = 0.0;
    public static final double INITIAL_INTERVAL = 1.0;

    private StateViewModel stateViewModel;
    private SmsViewModel smsViewModel;
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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // setup UI elements
        Button continueButton = findViewById(R.id.button);
        input = findViewById(R.id.textInput);
        editText = input.getEditText();
        progressBar = findViewById(R.id.progressBar2);

        continueButton.setOnClickListener(v -> onButtonClicked());

        resetAll();
    }

    @Override
    public void onBackPressed() {
        assert true;
    }

    private void resetAll() {
        // Preferences
        sharedPreferences.edit()
                .putBoolean("initialLoading", true)
                .apply();
    }

    private void onButtonClicked() {
        if (editText != null) {
            String number = editText.getText().toString();

            if (number.equals(""))
                input.setError(getString(R.string.number_error));
            else if (!number.substring(0, 1).equals("+"))
                input.setError(getString(R.string.number_subtitle));
            else {
                // TODO: display friendly message explaining the need for SMS
                // reading etc.
                // Also warn the user for the warningNumber SMS which is to be sent.
                sharedPreferences.edit()
                        .putString("number", number)
                        .apply();

                stateViewModel.insert(new State(
                        1, State.KEY_WARNING_NUMBER,
                        0.0, "",
                        State.STATUS_CONFIRMED, 0)
                );

                stateViewModel.insert(new State(
                        1, State.KEY_INTERVAL,
                        INITIAL_INTERVAL, "",
                        State.STATUS_CONFIRMED, 0)
                );

                stateViewModel.insert(new State(
                        1, State.KEY_WIFI,
                        INITIAL_WIFI, "",
                        State.STATUS_CONFIRMED, 0)
                );

                progressBar.setVisibility(View.VISIBLE);
                fetchSms();
            }
        }
    }

    private static final int REQUEST_PERMISSION_KEY = 1;

    private void fetchSms() {
        /*
        Load the messages from the phone's message storage into the App-internal DB.

        Before that, make sure the user has granted the necessary permissions.
         */
        String[] permissions = {
                android.Manifest.permission.READ_SMS,
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.RECEIVE_SMS
        };

        String address = sharedPreferences.getString("number", "");

        if (Utils.hasNoPermissions(this, permissions))
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_KEY);
        else {
            smsViewModel.fetchSms(
                    this, stateViewModel,
                    address, "", "true"
            );
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        /*
        This is executed after the user has decided if he wants to grant permissions to the App.

        If successful, start with fetching the messages.
        If not, display a toast noting the user needs to accept. Then prompt the user again!
        TODO: make the prompt translatable!
         */
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // final String prompt = "You must accept permissions!";
        final String prompt = "Die App wird ohne Berechtigung nicht funktionieren!";

        if (requestCode == REQUEST_PERMISSION_KEY) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, prompt, Toast.LENGTH_LONG).show();
            fetchSms();
        }
    }
}
