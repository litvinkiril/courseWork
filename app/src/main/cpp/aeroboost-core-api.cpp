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
    int currentPurchased;       // сколько уже куплено
    int cpsPerUnit;             // C/S за один самолет
};

// База данных самолетов
std::vector<Plane> planes = {
        // id, name,      image,    base, price/ед., макс, куплено, C/S за ед.
        {0, "Plane 1",   "plane1",  50,   10,   0,   1},
        {1, "Plane 2",   "plane2",  200,  50,  0,   5},
        {2, "Plane 3",   "plane3",  500,  100,   0,   15},
        {3, "Plane 4",   "plane4",  1000, 250, 0,   40},
        {4, "Plane 5",   "plane5",  2500, 500, 0,   100},
        {5, "Plane 6",   "plane6",  5000, 1000, 0,   250},
        {6, "Plane 7",   "plane7",  10000,2000, 0,   600},
        {7, "Plane 8",   "plane8",  25000,5000, 0,   1500},
        {8, "Plane 9",   "plane9",  50000,10000, 0,   4000},
        {9, "Plane 10",  "plane10", 100000,25000, 0,   10000}
};

// Рассчитать текущую цену для самолета
int calculateCurrentPrice(const Plane& plane) {
    return plane.basePrice + (plane.currentPurchased * plane.pricePerUnit);
}

// Рассчитать общий C/S для самолета
int calculateTotalCps(const Plane& plane) {
    return plane.cpsPerUnit;
}

// Получить цену самолета
extern "C"
JNIEXPORT jint JNICALL
Java_ru_livins_aeroboost_view_ShopActivity_getPlanePrice(JNIEnv *env, jclass clazz, jint plane_id) {

    // Проверяем что planeId в пределах массива
    if (plane_id < 0 || plane_id >= planes.size()) {
        return 0;
    }

    // Возвращаем текущую цену
    return calculateCurrentPrice(planes[plane_id]);
}

// Получить количество купленных
extern "C"
JNIEXPORT jint JNICALL
Java_ru_livins_aeroboost_view_ShopActivity_getPlanePurchased(JNIEnv *env, jclass clazz, jint plane_id) {

    if (plane_id < 0 || plane_id >= planes.size()) {
        return 0;
    }

    return planes[plane_id].currentPurchased;
}


// Получить общий C/S
extern "C"
JNIEXPORT jint JNICALL
Java_ru_livins_aeroboost_view_ShopActivity_getPlaneTotalCps(JNIEnv *env, jclass clazz, jint plane_id) {

    if (plane_id < 0 || plane_id >= planes.size()) {
        return 0;
    }

    return calculateTotalCps(planes[plane_id]);
}

// Попытаться купить
extern "C"
JNIEXPORT jboolean JNICALL
Java_ru_livins_aeroboost_view_ShopActivity_tryBuyPlane(JNIEnv *env, jclass clazz, jint plane_id) {

    if (plane_id < 0 || plane_id >= planes.size()) {
        return false;
    }

    Plane& plane = planes[plane_id];

    // Проверяем хватает ли денег
    int price = calculateCurrentPrice(plane);
    //if (coinCount >= price) {
    //    coinCount -= price;
    //    plane.currentPurchased++;
    //    return true;
    //}

    return false;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_ru_livins_aeroboost_model_GameModel_doGameStep(JNIEnv *env, jclass clazz, jobject prev_state) {

    jclass gameStateClass = env->GetObjectClass(prev_state);
    jobject new_state = env->AllocObject(gameStateClass);

    jfieldID userNameField = env->GetFieldID(gameStateClass , "userName", "Ljava/lang/String;");
    jfieldID totalProfitRateField = env->GetFieldID(gameStateClass , "totalProfitRate", "D");
    jfieldID totalCoinsField = env->GetFieldID(gameStateClass , "totalCoins", "D");

    (*env).SetObjectField(new_state, userNameField, env->GetObjectField(prev_state, userNameField));
    jdouble profitRate = env->GetDoubleField(prev_state, totalProfitRateField);
    jdouble totalCoins = env->GetDoubleField(prev_state, totalCoinsField);
    jdouble newCoins = totalCoins + profitRate;
    env->SetDoubleField(new_state, totalProfitRateField, profitRate);
    env->SetDoubleField(new_state, totalCoinsField, newCoins);

    return new_state;
}
