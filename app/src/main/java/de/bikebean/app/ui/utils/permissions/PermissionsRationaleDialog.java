package de.bikebean.app.ui.utils.permissions;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.HashMap;
import java.util.Map;

import de.bikebean.app.R;

public class PermissionsRationaleDialog extends DialogFragment {

    private final @NonNull Activity act;
    private final @NonNull PermissionUtils.KEYS permissionKey;

    private static final Map<PermissionUtils.KEYS, Integer[]> permissionMap =
            new HashMap<PermissionUtils.KEYS, Integer[]>() {{
                put(PermissionUtils.KEYS.SMS, new Integer[]{
                        R.string.no_sms_permission,
                        R.string.no_sms_permission_warning
                });
                put(PermissionUtils.KEYS.MAPS, new Integer[]{
                        R.string.no_map_permission,
                        R.string.no_map_permission_warning
                });
    }};

    public PermissionsRationaleDialog(final @NonNull Activity act,
                                      final @NonNull PermissionUtils.KEYS permissionKey) {
        this.act = act;
        this.permissionKey = permissionKey;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final @NonNull AlertDialog.Builder builder = new AlertDialog.Builder(act);

        final @Nullable Integer[] integers = permissionMap.get(permissionKey);
        final @Nullable Integer title;
        final @Nullable Integer message;
        if (integers != null) {
            title = integers[0];
            message = integers[1];
        } else {
            title = null;
            message = null;
        }

        if (title != null && message != null)
            builder.setTitle(title)
                    .setMessage(message);
        else
            builder.setTitle("Berechtigungen")
                    .setMessage("Die App benötigt weitere Berechtigungen!");

        builder.setPositiveButton(R.string.continue_ok, (dialog, id) ->
                PermissionUtils.askForPermissions(act, permissionKey)
        );

        return builder.create();
    }
}
