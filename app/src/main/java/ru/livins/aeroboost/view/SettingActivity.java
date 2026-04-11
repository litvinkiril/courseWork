package ru.livins.aeroboost.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import ru.livins.aeroboost.model.GameModel;
import ru.livins.aeroboost.R;

public class SettingActivity extends AppCompatActivity {

    private TextView tvCurrentName;
    private Button btnChangeName;
    private Button btnClearGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tvCurrentName = findViewById(R.id.tvCurrentName);
        btnChangeName = findViewById(R.id.btnChangeName);
        btnClearGame = findViewById(R.id.btnClearGame);
        updateCurrentNameDisplay();

        btnChangeName.setOnClickListener(v -> {
            showChangeNameDialog();
        });
        btnClearGame.setOnClickListener(v -> {
            showClearGameDialog();
        });
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
        });


    }

    private void updateCurrentNameDisplay() {
        String currentName = GameModel.getInstance().getUserName();
        tvCurrentName.setText(currentName);
    }

    private void showChangeNameDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_name, null);

        EditText etNewName = dialogView.findViewById(R.id.etNewName);

        String currentName = GameModel.getInstance().getUserName();
        etNewName.setText(currentName);
        etNewName.setSelection(currentName.length());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String newName = etNewName.getText().toString().trim();

            if (TextUtils.isEmpty(newName)) {
                Toast.makeText(this, "Имя не может быть пустым", Toast.LENGTH_SHORT).show();
                return;
            }
            if (etNewName.length() > 20) {
                Toast.makeText(this, "Имя не может быть длиннее 20 символов", Toast.LENGTH_SHORT).show();
            }
            GameModel.getInstance().setUserName(newName);

            updateCurrentNameDisplay();
            Toast.makeText(this, "Имя изменено на: " + newName, Toast.LENGTH_SHORT).show();

            dialog.dismiss();
        });
    }

    private void showClearGameDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_clear_game, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSave = dialogView.findViewById(R.id.btnDelete);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            clearAllGame();
        });
    }

    private void clearAllGame() {
        MainBoardActivity.clearGameBoard();
    }
}