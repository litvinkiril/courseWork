package ru.livins.aeroboost.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import ru.livins.aeroboost.R;
import ru.livins.aeroboost.model.PlaneItem;
import java.util.List;

public class PlanesAdapter extends ArrayAdapter<PlaneItem> {

    // Интерфейс для обработки кликов
    public interface OnPlaneClickListener {
        void onPlaneClick(int planeId);
    }

    private OnPlaneClickListener clickListener;

    public PlanesAdapter(Context context, List<PlaneItem> items, OnPlaneClickListener listener) {
        super(context, 0, items);
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        // Получаем данные самолета
        PlaneItem plane = getItem(position);

        // Создаем View если его нет
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_plane_row, parent, false);
        }

        // Находим View
        ImageView planeImage = convertView.findViewById(R.id.planeImage);
        TextView planeName = convertView.findViewById(R.id.planeName);
        TextView planePrice = convertView.findViewById(R.id.planePrice);
        TextView planeTotalCps = convertView.findViewById(R.id.planeTotalCps);

        // Заполняем данными
        if (plane != null) {
            // ЛОГИРОВАНИЕ для отладки
            Log.d("PlanesAdapter",
                    "Position: " + position +
                            ", Name: " + plane.getName() +
                            ", ImageName: " + plane.getImageName());

            // Устанавливаем картинку
            int imageResId = getImageResId(plane.getImageName());
            Log.d("PlanesAdapter", "ImageResId: " + imageResId);

            if (imageResId != 0) {
                planeImage.setImageResource(imageResId);
            } else {
                // Если картинка не найдена - ставим цвет
                planeImage.setBackgroundColor(0xFF666666);
                planeImage.setImageResource(android.R.drawable.ic_menu_report_image);
                Log.e("PlanesAdapter", "Image NOT FOUND: " + plane.getImageName());
            }

            // Основные данные
            planeName.setText(plane.getName());
            planePrice.setText(String.valueOf(plane.getCurrentPrice()));
            planeTotalCps.setText(plane.getCpsPerUnit() + " C/S");
        }

        // Обработка клика
        final int planeId = plane != null ? plane.getId() : -1;
        convertView.setOnClickListener(v -> {
            if (clickListener != null && planeId != -1) {
                clickListener.onPlaneClick(planeId);
            }
        });

        return convertView;
    }

    // Вспомогательный метод для получения ID картинки
    private int getImageResId(String imageName) {
        try {
            // Если передали null или пустую строку
            if (imageName == null || imageName.isEmpty()) {
                Log.e("PlanesAdapter", "Image name is null or empty");
                return 0;
            }

            // Убираем расширение .png если есть
            String cleanName = imageName;
            if (cleanName.endsWith(".png") || cleanName.endsWith(".jpg") || cleanName.endsWith(".webp")) {
                cleanName = cleanName.substring(0, cleanName.lastIndexOf('.'));
            }

            // Приводим к нижнему регистру (картинки должны быть lowercase)
            cleanName = cleanName.toLowerCase();

            Log.d("PlanesAdapter", "Looking for image: " + cleanName);

            int resId = getContext().getResources().getIdentifier(
                    cleanName,         // имя файла БЕЗ расширения
                    "drawable",        // тип ресурса
                    getContext().getPackageName()
            );

            return resId;
        } catch (Exception e) {
            Log.e("PlanesAdapter", "Error loading image: " + imageName, e);
            return 0;
        }
    }
}