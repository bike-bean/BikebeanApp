package de.bikebean.app.ui.status;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.bikebean.app.R;
import de.bikebean.app.Utils;

public class PermissionsRationaleDialog extends DialogFragment {

    private final Activity act;

    private static final Map<Utils.PERMISSION_KEY, Integer[]> permissionMap =
            new HashMap<Utils.PERMISSION_KEY, Integer[]>() {{
                put(Utils.PERMISSION_KEY.SMS, new Integer[]{
                        R.string.no_sms_permission,
                        R.string.no_sms_permission_warning
                });
                put(Utils.PERMISSION_KEY.MAPS, new Integer[]{
                        R.string.no_map_permission,
                        R.string.no_map_permission_warning
                });
    }};

    private Utils.PERMISSION_KEY permissionKey;

    public PermissionsRationaleDialog(Activity act, Utils.PERMISSION_KEY permissionKey) {
        this.act = act;
        this.permissionKey = permissionKey;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(act);

        builder.setTitle(Objects.requireNonNull(permissionMap.get(permissionKey))[0])
                .setMessage(Objects.requireNonNull(permissionMap.get(permissionKey))[1])
                .setPositiveButton(R.string.continue_ok, (dialog, id) ->
                        Utils.askForPermissions(act, permissionKey)
                );

        return builder.create();
    }
}
