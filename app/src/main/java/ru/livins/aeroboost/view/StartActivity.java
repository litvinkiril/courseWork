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
        var startGameDataIntent = new Intent(this, MainBoardActivity.class);
        startActivity(startGameDataIntent);
    }
}
