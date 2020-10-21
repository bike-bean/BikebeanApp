package de.bikebean.app.ui.initialization;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import de.bikebean.app.R;
import de.bikebean.app.db.type.types.Initial;
import de.bikebean.app.ui.utils.Utils;
import de.bikebean.app.ui.drawer.status.StateViewModel;
import de.bikebean.app.ui.utils.preferences.PreferencesUtils;

public class InitialConfigurationActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    // UI elements
    private TextInputLayout input;
    private EditText editText;

    protected void onCreate(final @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_configuration);

        StateViewModel stateViewModel = new ViewModelProvider(this).get(StateViewModel.class);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        /* setup UI elements */
        final @Nullable Button continueButton = findViewById(R.id.button);
        input = findViewById(R.id.textInput);
        editText = input.getEditText();

        if (continueButton != null)
            continueButton.setOnClickListener(v -> onButtonClicked());

        if (editText != null)
            editText.setOnKeyListener(this::listenKeys);

        stateViewModel.insert(new Initial());
    }

    @Override
    public void onBackPressed() {
        assert true;
    }

    private boolean listenKeys(final @NonNull View v, int keyCode, final @NonNull KeyEvent event) {
        if (Utils.beginsWithPlus(editText.getText().toString()))
            input.setError(null);
        else
            input.setError(getString(R.string.number_subtitle));

        return false;
    }

    private void onButtonClicked() {
        if (editText != null) {
            final @NonNull String number = editText.getText().toString();
            final @Nullable @StringRes Integer errorString = Utils.getErrorString(number);

            if (errorString != null) {
                input.setError(getString(errorString));

                if (errorString == R.string.number_no_blanks)
                    editText.setText(Utils.eliminateSpaces(number));

                return;
            }

            /* TODO: warn the user for the warningNumber SMS which is to be sent. */
            PreferencesUtils.setInitStateAddress(sharedPreferences, number);

            finish();
            overridePendingTransition(0, R.anim.slide_out_down);
        }
    }
}
