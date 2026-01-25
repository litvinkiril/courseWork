package ru.livins.aeroboost.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ru.livins.aeroboost.R;
import ru.livins.aeroboost.model.GameModel;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
    }

    public void onStartGame(View view) {
        // Открыть окно игры.
        var startGameDataIntent = new Intent(this, MainBoardActivity.class);
        /*var startGameData = new GameModel("Andrej");
        startGameDataIntent.putExtra(GameModel.class.getSimpleName(), startGameData);*/
        startActivity(startGameDataIntent);
    }

    public void onSettings(View view) {
        Toast.makeText(StartActivity.this,
                "Settings",
                Toast.LENGTH_SHORT).show();
    }
}
