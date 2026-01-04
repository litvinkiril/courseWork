package ru.livins.aeroboost.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import ru.livins.aeroboost.R;
import ru.livins.aeroboost.viewmodel.StartGameData;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        if (savedInstanceState == null) {
        }
    }

    public void onStartGame(View view) {
        // Открыть окно игры.
        var startGameDataIntent = new Intent(this, MainBoardActivity.class);
        var startGameData = new StartGameData("Andrej");
        startGameDataIntent.putExtra(StartGameData.class.getSimpleName(), startGameData);
        startActivity(startGameDataIntent);
    }
}
