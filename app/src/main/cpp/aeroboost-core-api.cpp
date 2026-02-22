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
    const char* blockImageName;
};

// База данных самолетов
std::vector<Plane> planes = {
        // id, name,      image,    base, price/ед., макс, куплено, C/S за ед.
        {0, "1. Stipa",   "plane1",  50,   10,   0,   1, "airplane001"},
        {1, "2. SuperMarin",   "plane2",  200,  50,  0,   5, "airplane001"},
        {2, "3. Mikoy",   "plane3",  500,  100,   0,   15, "airplane001"},
        {3, "4. Yako",   "plane4",  1000, 250, 0,   40, "blockplane4"},
        {4, "5. Voughtent",   "plane5",  2500, 500, 0,   100, "blockplane5"},
        {5, "6. Bufaloo",   "plane6",  5000, 1000, 0,   250, "blockplane6"},
        {6, "7. Brew",   "plane7",  10000,2000, 0,   600, "blockplane7"},
        {7, "8. Gruman",   "plane8",  25000,5000, 0,   1500, "blockplane8"},
        {8, "9. Flyer-1",   "plane9",  50000,10000, 0,   4000, "blockplane9"},
        {9, "10. Flyer-2",  "plane10", 100000,25000, 0,   10000, "blockplane10"}
};

// Рассчитать текущую цену для самолета
int calculateCurrentPrice(const Plane& plane) {
    return plane.basePrice + (plane.currentPurchased * plane.pricePerUnit);
}

// Рассчитать общий C/S для самолета
int calculateTotalCps(const Plane& plane) {
    return plane.cpsPerUnit;
}

// получить количество самолетов
extern "C"
JNIEXPORT jint JNICALL
Java_ru_livins_aeroboost_view_ShopActivity_getTotalPlanesCount(JNIEnv *env, jclass clazz) {
    return static_cast<jint>(planes.size());
}

// Получить имя самолета
extern "C"
JNIEXPORT jstring JNICALL
Java_ru_livins_aeroboost_view_ShopActivity_getPlaneName(JNIEnv *env, jclass clazz, jint plane_id) {
    if (plane_id < 0 || plane_id >= planes.size()) {
        return env->NewStringUTF("Unknown");
    }
    return env->NewStringUTF(planes[plane_id].name);
}

// Получить имя картинки
extern "C"
JNIEXPORT jstring JNICALL
Java_ru_livins_aeroboost_view_ShopActivity_getPlaneImageName(JNIEnv *env, jclass clazz, jint plane_id) {
    if (plane_id < 0 || plane_id >= planes.size()) {
        return env->NewStringUTF("plane1");
    }
    return env->NewStringUTF(planes[plane_id].imageName);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ru_livins_aeroboost_view_ShopActivity_getPlaneBlockImageName(JNIEnv *env, jclass clazz,
                                                                  jint plane_id) {
    if (plane_id < 0 || plane_id >= planes.size()) {
        return env->NewStringUTF("plane1");
    }
    return env->NewStringUTF(planes[plane_id].blockImageName);
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


// Получить C/S за единицу
extern "C"
JNIEXPORT jint JNICALL
Java_ru_livins_aeroboost_view_ShopActivity_getPlaneCpsPerUnit(JNIEnv *env, jclass clazz, jint plane_id) {
    if (plane_id < 0 || plane_id >= planes.size()) {
        return 0;
    }
    return static_cast<jint>(planes[plane_id].cpsPerUnit);
}

// Получить общий C/S
extern "C"
JNIEXPORT jint JNICALL
Java_ru_livins_aeroboost_view_ShopActivity_getPlaneTotalCps(JNIEnv *env, jclass clazz, jint plane_id) {
    if (plane_id < 0 || plane_id >= planes.size()) {
        return 0;
    }
    return static_cast<jint>(calculateTotalCps(planes[plane_id]));
}

extern "C"
JNIEXPORT jdouble JNICALL
Java_ru_livins_aeroboost_view_ShopActivity_tryBuyPlane(
        JNIEnv *env,
        jclass clazz,
        jint plane_id,
        jdouble currentBalance
) {
    if (plane_id < 0 || plane_id >= planes.size()) {
        return -1.0;
    }

    Plane& plane = planes[plane_id];
    int price = calculateCurrentPrice(plane);

    // Если денег хватает
    if (currentBalance >= price) {
        plane.currentPurchased++;
        jdouble newBalance = currentBalance - price;
        return newBalance;
    }

    return -1.0;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_ru_livins_aeroboost_model_GameModel_doGameStep(JNIEnv *env, jclass clazz, jobject prev_state) {

    jclass gameStateClass = env->GetObjectClass(prev_state);
    jobject new_state = env->AllocObject(gameStateClass);

    jfieldID userNameField = env->GetFieldID(gameStateClass , "userName", "Ljava/lang/String;");
    jfieldID gameSpeedField = env->GetFieldID(gameStateClass , "gameSpeed", "D");
    jfieldID totalCoinsField = env->GetFieldID(gameStateClass , "totalCoins", "D");
    jfieldID runningPlanesField = env->GetFieldID(gameStateClass , "runningPlanes","Ljava/util/List;");

    // Обновить имя пользователя и скорость.
    env->SetObjectField(new_state, userNameField, env->GetObjectField(prev_state, userNameField));
    jdouble gameSpeed = env->GetDoubleField(prev_state, gameSpeedField);
    env->SetDoubleField(new_state, gameSpeedField, gameSpeed);

    // Обновить пробег самолетиков.
    double profit = 0.02;
    jobject runningPlanesList = env->GetObjectField(prev_state, runningPlanesField);
    //int runningPlanesCount = env->GetArrayLength(runningPlanes);
    jclass listOfRunningPlaneClass = env->GetObjectClass(runningPlanesList);
    jmethodID sizeMethodId = env->GetMethodID(listOfRunningPlaneClass, "size", "()I");
    jint runningPlanesCount = env->CallIntMethod(runningPlanesList, sizeMethodId);
    for (int pi = 0; pi < runningPlanesCount; ++pi) {
        //jobject runningPlane = env->GetObjectArrayElement(runningPlanes, pi);
        jmethodID getMethodId = env->GetMethodID(listOfRunningPlaneClass, "get", "(I)Ljava/lang/Object;");
        jobject runningPlane = env->CallObjectMethod(runningPlanesList, getMethodId, pi);

        jclass runningPlaneClass = env->GetObjectClass(runningPlane);
        jfieldID speedField = env->GetFieldID(runningPlaneClass , "speed", "D");
        jfieldID odometerField = env->GetFieldID(runningPlaneClass , "odometer", "D");

        jdouble planeSpeed = env->GetDoubleField(runningPlane, speedField);
        jdouble planeOdometer = env->GetDoubleField(runningPlane, odometerField);
        double newOdometer = planeOdometer + (planeSpeed / gameSpeed);
        env->SetDoubleField(runningPlane, odometerField, newOdometer);
    }
    env->SetObjectField(new_state, runningPlanesField, runningPlanesList);

    // Обновить счет.
    jdouble totalCoins = env->GetDoubleField(prev_state, totalCoinsField);
    jdouble newCoins = totalCoins + profit;
    env->SetDoubleField(new_state, totalCoinsField, newCoins);

    return new_state;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_ru_livins_aeroboost_adapter_PlanesAdapter_opened(JNIEnv *env, jclass clazz, jint plane_id) {
    if (plane_id <  3) {
        return true;
    }
    if (planes[plane_id - 1].currentPurchased > 0) {
        return true;
    }
    else {
        return false;
    }
}