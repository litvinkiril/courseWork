package ru.livins.aeroboost.view;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import ru.livins.aeroboost.adapter.PlanesAdapter;
import ru.livins.aeroboost.model.PlaneItem;
import java.util.ArrayList;
import java.util.List;

import ru.livins.aeroboost.R;

public class ShopActivity extends AppCompatActivity
        implements PlanesAdapter.OnPlaneClickListener {

    // Нативные методы
    private static native int getPlanePrice(int planeId);
    private static native int getPlanePurchased(int planeId);
    private static native int getPlaneTotalCps(int planeId);
    private static native boolean tryBuyPlane(int planeId);
    private static native String getPlaneName(int planeId);
    private static native String getPlaneImageName(int planeId);
    private static native int getPlaneCpsPerUnit(int planeId);
    private static native int getTotalPlanesCount();

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

        listView = findViewById(R.id.planesListView);
        returnBoardButton = findViewById(R.id.returnBoardButton);

        returnBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Return button clicked - closing activity");
                finish();
            }
        });

        // Загружаем данные из C++
        loadPlanesFromNative();

        adapter = new PlanesAdapter(this, planes, this);
        listView.setAdapter(adapter);

        Log.d(TAG, "Adapter created with " + planes.size() + " items");
    }

    // Загрузка данных из нативного кода
    private void loadPlanesFromNative() {
        planes.clear();

        // Получаем количество самолетов из C++
        int totalPlanes = getTotalPlanesCount();
        Log.d(TAG, "Total planes in C++: " + totalPlanes);

        for (int i = 0; i < totalPlanes; i++) {
            try {
                // Получаем все данные из C++
                String name = getPlaneName(i);
                String imageName = getPlaneImageName(i);
                int currentPrice = getPlanePrice(i);
                int currentPurchased = getPlanePurchased(i);
                int cpsPerUnit = getPlaneCpsPerUnit(i);
                int totalCps = getPlaneTotalCps(i);

                Log.d(TAG, String.format(
                        "Plane %d: name=%s, image=%s, price=%d, purchased=%d, cpsPerUnit=%d, totalCps=%d",
                        i, name, imageName, currentPrice, currentPurchased, cpsPerUnit, totalCps
                ));

                // Создаем PlaneItem с данными из C++
                PlaneItem plane = new PlaneItem(
                        i,
                        name,
                        imageName,
                        currentPrice,
                        currentPurchased,
                        cpsPerUnit,
                        totalCps
                );

                planes.add(plane);
            } catch (Exception e) {
                Log.e(TAG, "Error loading plane " + i + ": " + e.getMessage());
            }
        }
    }

    // Обработка клика по самолету - покупка
    @Override
    public void onPlaneClick(int planeId) {
        Log.d(TAG, "Attempting to buy plane: " + planeId);

        boolean success = tryBuyPlane(planeId);

        if (success) {
            Toast.makeText(this, "Самолет куплен!", Toast.LENGTH_SHORT).show();
            // Обновляем данные этого самолета
            updatePlaneData(planeId);
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Недостаточно средств!", Toast.LENGTH_SHORT).show();
        }
    }

    // Обновление данных одного самолета после покупки
    private void updatePlaneData(int planeId) {
        if (planeId >= 0 && planeId < planes.size()) {
            PlaneItem plane = planes.get(planeId);

            // Получаем обновленные данные из C++
            int newPrice = getPlanePrice(planeId);
            int newPurchased = getPlanePurchased(planeId);
            int newTotalCps = getPlaneTotalCps(planeId);

            // Обновляем объект
            plane.setCurrentPrice(newPrice);
            plane.setCurrentPurchased(newPurchased);
            plane.setTotalCps(newTotalCps);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}