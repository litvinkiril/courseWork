package ru.livins.aeroboost.view;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import ru.livins.aeroboost.adapter.PlanesAdapter;
import ru.livins.aeroboost.model.PlaneItem;
import java.util.ArrayList;
import java.util.List;

import ru.livins.aeroboost.R;

public class ShopActivity extends AppCompatActivity
        implements PlanesAdapter.OnPlaneClickListener {

    private static final String TAG = "SecondActivity";
    private ListView listView;
    private PlanesAdapter adapter;
    private List<PlaneItem> planes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_activity);

        Log.d(TAG, "SecondActivity started");

        // Находим ListView
        listView = findViewById(R.id.planesListView);

        // Загружаем данные (сначала тестовые)
        loadTestPlanes();

        // Создаем адаптер
        adapter = new PlanesAdapter(this, planes, this);
        listView.setAdapter(adapter);

        Log.d(TAG, "Adapter created with " + planes.size() + " items");
    }

    // Тестовые данные
    private void loadTestPlanes() {
        planes.clear();

        // Сначала протестируем с СУЩЕСТВУЮЩИМИ картинками
        String[] testImages = {
                "coin",              // точно есть
                "main_board",        // точно есть
                "empty_place",       // точно есть
                "coin",              // повтор
                "main_board",
                "empty_place",
                "coin",
                "main_board",
                "empty_place",
                "coin"
        };

        for (int i = 0; i < 10; i++) {
            int price = 100 * (i + 1);
            int max = 5 - (i / 3);
            int bought = i < 3 ? i : 0;

            PlaneItem plane = new PlaneItem(
                    i,
                    "Plane " + (i + 1),
                    testImages[i],  // используем СУЩЕСТВУЮЩИЕ картинки
                    price,
                    max,
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
}