package com.example.stormmasterclient.helpers.dialogs;

import android.content.Context;

import com.example.stormmasterclient.R;
import com.example.stormmasterclient.helpers.API.ApiAuthorizedClient;
import com.example.stormmasterclient.helpers.others.LoggerOut;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Dialog for logging out of the account.
 */
public class ExitDialog {

    private final Context context;

    /**
     * Constructor for ExitDialog.
     *
     * @param context The context in which the dialog should be displayed.
     */
    public ExitDialog(Context context) {
        this.context = context;
    }

    /**
     * Shows the exit dialog.
     */
    public void show(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                .setTitle("Выйти из аккаунта")
                .setMessage("Учтите, что при выходе удалятся все сохраненные данные, включая информацию" +
                        "о ваших мозгововых штурмах. Вы также можете выйти со всех устройств, если нажмете " +
                        "на кнопку \"Выйти со всех устройств\".")
                .setNegativeButton("Отмена", (dialogInterface, i) -> dialogInterface.dismiss())
                .setNeutralButton("Выйти со всех устройств", (dialogInterface, i) -> {
                    // Log out of the account locally and on the server
                    String token = context.getSharedPreferences("USER_DATA", 0).getString("token", "");
                    ApiAuthorizedClient apiClient = new ApiAuthorizedClient(context, token);
                    apiClient.userLogout();
                })
                .setPositiveButton("Выйти", (dialogInterface, i) -> {
                    // Log out of the account only locally
                    LoggerOut loggerOut = new LoggerOut(context);
                    loggerOut.logOut();
                })
                .setIcon(R.drawable.exit_icon)
                .setCancelable(true);
        builder.show();
    }
}
