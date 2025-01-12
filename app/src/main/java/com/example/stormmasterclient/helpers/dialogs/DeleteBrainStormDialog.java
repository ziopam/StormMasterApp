package com.example.stormmasterclient.helpers.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.text.LineBreaker;
import android.text.Layout;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.stormmasterclient.R;
import com.example.stormmasterclient.helpers.API.ApiAuthorizedClient;
import com.example.stormmasterclient.helpers.RoomDatabase.BrainstormRepository;
import com.example.stormmasterclient.helpers.others.LoggerOut;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

public class DeleteBrainStormDialog {

    private final Activity activity;
    private final int idToDelete;

    public DeleteBrainStormDialog(int idToDelete, Activity activity)
    {
        this.activity = activity;
        this.idToDelete = idToDelete;
    }

    @SuppressLint("WrongConstant")
    public void show(){
        String LineSeparator = System.lineSeparator();
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity)
                .setTitle("Удалить мозговой штурм")
                .setMessage("Вы уверены, что хотите удалить этот мозговой штурм? Это действие нельзя отменить." + LineSeparator +
                        "При удалении мозгового штурма произойдет следующее:" + LineSeparator + LineSeparator +
                        "1. Вы больше не сможете получить доступ к данным, если не сохранили их." + LineSeparator +
                        "2. Все участники мозгового штурма также потеряют доступ к данным." + LineSeparator +
                        "3. У других участников мозгового штурма все равно могла остаться копия информации, если " +
                        "они, например, сохранили информацию локально на устройстве.")
                .setNegativeButton("Отмена", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("Удалить", (dialogInterface, i) -> {
                    SharedPreferences preferences = activity.getSharedPreferences("USER_DATA", 0);
                    BrainstormRepository repository = new BrainstormRepository(activity.getApplication(),
                            preferences.getString("token", "null"));
                    repository.deleteById(idToDelete, activity);
                })
                .setIcon(R.drawable.exit_icon)
                .setCancelable(true);
        AlertDialog dialog = builder.show();

        TextView messageView = dialog.findViewById(android.R.id.message);
        assert messageView != null;
        messageView.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(activity.getResources().getColor(R.color.beautiful_red, null));
    }
}
