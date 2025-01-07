package com.example.stormmasterclient.helpers.dialogs;

import android.content.Context;

import com.example.stormmasterclient.R;
import com.example.stormmasterclient.helpers.API.ApiAuthorizedClient;
import com.example.stormmasterclient.helpers.others.LoggerOut;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ExitDialog {

    private final Context context;

    public ExitDialog(Context context) {
        this.context = context;
    }

    public void show(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                .setTitle("Выйти из аккаунта")
                .setMessage("Учтите, что при выходе удалятся все сохраненные данные, включая информацию" +
                        "о ваших мозгововых штурмах. Вы также можете выйти со всех устройств, если нажмете " +
                        "на кнопку \"Выйти со всех устройств\".")
                .setNegativeButton("Отмена", (dialogInterface, i) -> dialogInterface.dismiss())
                .setNeutralButton("Выйти со всех устройств", (dialogInterface, i) -> {
                    String token = context.getSharedPreferences("USER_DATA", 0).getString("token", "");
                    ApiAuthorizedClient apiClient = new ApiAuthorizedClient(context, token);
                    apiClient.userLogout();
                })
                .setPositiveButton("Выйти", (dialogInterface, i) -> {
                    LoggerOut loggerOut = new LoggerOut(context);
                    loggerOut.logOut();
                })
                .setIcon(R.drawable.exit_icon)
                .setCancelable(true);
        builder.show();
    }
}
