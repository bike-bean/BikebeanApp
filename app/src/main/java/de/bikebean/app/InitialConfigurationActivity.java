package de.bikebean.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Objects;

public class InitialConfigurationActivity extends AppCompatActivity {

    NumberConfigurationFragment numberConfigurationFragment;
    FragmentManager fragmentManager;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intial_configuration);

        fragmentManager = getSupportFragmentManager();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        numberConfigurationFragment = new InitialConfigurationActivity.NumberConfigurationFragment();

        showDialog();
    }

    private void showDialog() {
        numberConfigurationFragment.show(fragmentManager, "initialConfig");
    }

    public static class NumberConfigurationFragment extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

            Context context = getActivity().getApplicationContext();

            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            EditText input = new EditText(context);
            input.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT)
            );

            input.setInputType(InputType.TYPE_CLASS_PHONE);

//            numberPreference.setOnPreferenceChangeListener((preference, newValue) -> {
//                if (!newValue.toString().substring(0, 1).equals("+"))
//                    return false;
//                else
//                    return true;
//            });

            builder.setView(input)
                    .setTitle(R.string.number_title)
                    .setMessage(R.string.number_subtitle)
                    .setPositiveButton(R.string.continue_general, (dialog, id) -> {
                        String number = input.getText().toString();

                        if (number.equals("")) {
                            Toast.makeText(context,
                                    "Bitte Nummer eingeben!",
                                    Toast.LENGTH_LONG)
                                    .show();
                        } else if (!number.substring(0, 1).equals("+")) {
                            Toast.makeText(context,
                                    R.string.number_subtitle,
                                    Toast.LENGTH_LONG)
                                    .show();
                        } else {
                            editor.putString("number", number);
                            editor.apply();
                        }

                        if (sharedPreferences.getString("number", "").equals(""))
                            getActivity().setResult(RESULT_CANCELED);
                        else
                            getActivity().setResult(RESULT_OK);
                        getActivity().finish();
                    });

            return builder.create();
        }
    }
}
