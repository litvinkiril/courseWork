package ru.livins.aeroboost.view;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import ru.livins.aeroboost.adapter.PlanesAdapter;
import ru.livins.aeroboost.model.PlaneItem;
import java.util.ArrayList;
import java.util.List;

import ru.livins.aeroboost.R;

public class ShopActivity extends AppCompatActivity
        implements PlanesAdapter.OnPlaneClickListener {

    private static final String TAG = "ShopActivity";
    private ListView listView;
    private PlanesAdapter adapter;
    private List<PlaneItem> planes = new ArrayList<>();
    private ImageButton returnBoardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_activity);

        Log.d(TAG, "ShopActivity started");

        // Находим ListView
        listView = findViewById(R.id.planesListView);

        // Находим кнопку возврата
        returnBoardButton = findViewById(R.id.returnBoardButton);

        // Настраиваем обработчик клика для кнопки возврата
        returnBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Return button clicked - closing activity");
                closeShopActivity();
            }
        });

        // Загружаем данные
        loadTestPlanes();

        // Создаем адаптер
        adapter = new PlanesAdapter(this, planes, this);
        listView.setAdapter(adapter);

        Log.d(TAG, "Adapter created with " + planes.size() + " items");
    }

    // Метод для закрытия ShopActivity
    private void closeShopActivity() {
        Log.d(TAG, "Closing ShopActivity");
        finish(); // Просто закрываем текущую активность
    }

    // Тестовые данные
    private void loadTestPlanes() {
        planes.clear();

        String[] testImages = {
                "plane1",
                "plane2",
                "plane3",
                "plane4",
                "plane5",
                "plane6",
                "plane7",
                "plane8",
                "plane9",
                "plane10"
        };

        for (int i = 0; i < 10; i++) {
            int price = 100 * (i + 1);
            int max = 5 - (i / 3);
            int bought = i < 3 ? i : 0;

            PlaneItem plane = new PlaneItem(
                    i,
                    "Plane " + (i + 1),
                    testImages[i],
                    price,
                    bought,
                    10 * (i + 1),
                    (10 * (i + 1)) * bought
            );

            planes.add(plane);
            Log.d(TAG, "Created plane: " + plane.getName() + " with image: " + testImages[i]);
        }
    }

    // Обработка клика по самолету
    @Override
    public void onPlaneClick(int planeId) {
        Log.d(TAG, "Plane clicked: " + planeId);
        // ... остальной код
    }

    // Обработка кнопки "Назад" на устройстве - тоже закрываем ShopActivity
    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back button pressed - closing activity");
        closeShopActivity();
    }
}