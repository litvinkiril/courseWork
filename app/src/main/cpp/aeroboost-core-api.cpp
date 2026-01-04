#include <jni.h>
#include <string>
#include <vector>

// Структура самолета
struct Plane {
    int id;
    const char* name;           // "Plane 1", "Plane 2" и т.д.
    const char* imageName;      // "plane1", "plane2" (без расширения)
    int basePrice;              // базовая цена
    int pricePerUnit;           // цена за каждый купленный
    int maxPurchases;           // максимум можно купить (слотов)
    int currentPurchased;       // сколько уже куплено
    int cpsPerUnit;             // C/S за один самолет
};

// База данных самолетов
std::vector<Plane> planes = {
        // id, name,      image,    base, price/ед., макс, куплено, C/S за ед.
        {0, "Plane 1",   "plane1",  50,   10,    5,   0,   1},
        {1, "Plane 2",   "plane2",  200,  50,    4,   0,   5},
        {2, "Plane 3",   "plane3",  500,  100,   3,   0,   15},
        {3, "Plane 4",   "plane4",  1000, 250,   3,   0,   40},
        {4, "Plane 5",   "plane5",  2500, 500,   2,   0,   100},
        {5, "Plane 6",   "plane6",  5000, 1000,  2,   0,   250},
        {6, "Plane 7",   "plane7",  10000,2000,  2,   0,   600},
        {7, "Plane 8",   "plane8",  25000,5000,  1,   0,   1500},
        {8, "Plane 9",   "plane9",  50000,10000, 1,   0,   4000},
        {9, "Plane 10",  "plane10", 100000,25000,1,   0,   10000}
};

// Рассчитать текущую цену для самолета
int calculateCurrentPrice(const Plane& plane) {
    return plane.basePrice + (plane.currentPurchased * plane.pricePerUnit);
}

// Рассчитать общий C/S для самолета
int calculateTotalCps(const Plane& plane) {
    return plane.currentPurchased * plane.cpsPerUnit;
}

// Получить цену самолета
extern "C" JNIEXPORT jint JNICALL
Java_com_example_aeroboost_SecondActivity_getPlanePriceNative(
        JNIEnv* env, jobject thiz, jint planeId) {

    // Проверяем что planeId в пределах массива
    if (planeId < 0 || planeId >= planes.size()) {
        return 0;
    }

    // Возвращаем текущую цену
    return calculateCurrentPrice(planes[planeId]);
}

// Получить количество купленных
extern "C" JNIEXPORT jint JNICALL
Java_com_example_aeroboost_SecondActivity_getPlanePurchasedNative(
        JNIEnv* env, jobject thiz, jint planeId) {

    if (planeId < 0 || planeId >= planes.size()) {
        return 0;
    }

    return planes[planeId].currentPurchased;
}

// Получить общий C/S
extern "C" JNIEXPORT jint JNICALL
Java_com_example_aeroboost_SecondActivity_getPlaneTotalCpsNative(
        JNIEnv* env, jobject thiz, jint planeId) {

    if (planeId < 0 || planeId >= planes.size()) {
        return 0;
    }

    return calculateTotalCps(planes[planeId]);
}

// Попытаться купить
extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_aeroboost_SecondActivity_tryBuyPlaneNative(
        JNIEnv* env, jobject thiz, jint planeId) {

    if (planeId < 0 || planeId >= planes.size()) {
        return false;
    }

    Plane& plane = planes[planeId];

    // Проверяем можно ли еще купить
    if (plane.currentPurchased >= plane.maxPurchases) {
        return false;
    }

    // Проверяем хватает ли денег
    int price = calculateCurrentPrice(plane);
    //if (coinCount >= price) {
    //    coinCount -= price;
    //    plane.currentPurchased++;
    //    return true;
    //}

    return false;
}