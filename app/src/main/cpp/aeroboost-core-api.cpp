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
        {0, "1. Stipa",   "plane1",  100,   10,   0,   5, "airplane001"},
        {1, "2. SuperMarin",   "plane2",  300,  20,  0,   10, "airplane001"},
        {2, "3. Mikoy",   "plane3",  500,  100,   0,   20, "airplane001"},
        {3, "4. Yako",   "plane4",  1000, 250, 0,   40, "blockplane4"},
        {4, "5. Voughtent",   "plane5",  2500, 500, 0,   80, "blockplane5"},
        {5, "6. Bufaloo",   "plane6",  5000, 1000, 0,   160, "blockplane6"},
        {6, "7. Brew",   "plane7",  10000,2000, 0,   320, "blockplane7"},
        {7, "8. Gruman",   "plane8",  25000,5000, 0,   640, "blockplane8"},
        {8, "9. Flyer-1",   "plane9",  50000,10000, 0,   1280, "blockplane9"},
        {9, "10. Flyer-2",  "plane10", 100000,25000, 0,   2560, "blockplane10"}
};

// Рассчитать текущую цену для самолета
int calculateCurrentPrice(const Plane& plane) {
    if (plane.currentPurchased == 0) {
        return plane.basePrice;
    }
    int plus = 0;
    for (int i = 1; i <= plane.currentPurchased; ++i) {
        plus += i;
    }
    int prevPrice = plane.basePrice + (plane.currentPurchased * plane.pricePerUnit);
    return prevPrice + plus;
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

    jfieldID userNameField = env->GetFieldID(gameStateClass, "userName", "Ljava/lang/String;");
    jfieldID gameSpeedField = env->GetFieldID(gameStateClass, "gameSpeed", "D");
    jfieldID totalCoinsField = env->GetFieldID(gameStateClass, "totalCoins", "D");
    jfieldID runningPlanesField = env->GetFieldID(gameStateClass, "runningPlanes", "Ljava/util/List;");

    // Копируем имя пользователя и скорость
    env->SetObjectField(new_state, userNameField, env->GetObjectField(prev_state, userNameField));
    jdouble gameSpeed = env->GetDoubleField(prev_state, gameSpeedField);
    env->SetDoubleField(new_state, gameSpeedField, gameSpeed);

    // Получаем текущее количество монет
    jdouble totalCoins = env->GetDoubleField(prev_state, totalCoinsField);

    // Получаем список самолетов
    jobject runningPlanesList = env->GetObjectField(prev_state, runningPlanesField);
    jclass listOfRunningPlaneClass = env->GetObjectClass(runningPlanesList);
    jmethodID sizeMethodId = env->GetMethodID(listOfRunningPlaneClass, "size", "()I");
    jint runningPlanesCount = env->CallIntMethod(runningPlanesList, sizeMethodId);

    // Обновить пробег самолетиков и считать монеты за круги
    jdouble additionalCoins = 0.0;

    for (int pi = 0; pi < runningPlanesCount; ++pi) {
        jmethodID getMethodId = env->GetMethodID(listOfRunningPlaneClass, "get", "(I)Ljava/lang/Object;");
        jobject runningPlane = env->CallObjectMethod(runningPlanesList, getMethodId, pi);

        jclass runningPlaneClass = env->GetObjectClass(runningPlane);
        jfieldID speedField = env->GetFieldID(runningPlaneClass, "speed", "D");
        jfieldID odometerField = env->GetFieldID(runningPlaneClass, "odometer", "D");
        jfieldID planeIdField = env->GetFieldID(runningPlaneClass, "planeId", "I"); // ВАЖНО: "I" для int!

        jdouble planeSpeed = env->GetDoubleField(runningPlane, speedField);
        jdouble planeOdometer = env->GetDoubleField(runningPlane, odometerField);
        jint planeId = env->GetIntField(runningPlane, planeIdField);

        // Сохраняем старое значение для проверки пересечения круга
        double oldOdometer = planeOdometer;

        // Вычисляем новый odometer
        double newOdometer = planeOdometer + (planeSpeed / 25);

        // Проверяем, пересек ли самолет границу целого числа (сделал круг)
        // Используем floor для определения целых частей
        int oldCircle = static_cast<int>(floor(oldOdometer));
        int newCircle = static_cast<int>(floor(newOdometer));

        // Если целая часть увеличилась, значит самолет сделал круг(и)
        if (newCircle > oldCircle) {
            // Сколько кругов сделано
            int circlesCompleted = newCircle - oldCircle;

            // Получаем cpsPerUnit из глобального массива planes
            // Проверяем, что planeId в допустимых пределах
            if (planeId >= 0 && planeId < planes.size()) {
                // Добавляем монеты: cpsPerUnit * количество кругов
                additionalCoins += planes[planeId].cpsPerUnit;
            }
        }

        // Устанавливаем новое значение odometer
        env->SetDoubleField(runningPlane, odometerField, newOdometer);
    }

    // Устанавливаем обновленный список обратно
    env->SetObjectField(new_state, runningPlanesField, runningPlanesList);

    // Обновляем счет с добавленными монетами
    jdouble newCoins = totalCoins + additionalCoins;
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

extern "C"
JNIEXPORT jdouble JNICALL
Java_ru_livins_aeroboost_view_MainBoardActivity_countCpsPerSecond(JNIEnv *env, jclass clazz,
                                                                  jint plane_id) {
    double planeSpeed = 0.2 * pow(1.1, plane_id - 1);
    int coin = planes[plane_id].cpsPerUnit;
    return coin * planeSpeed;
}
extern "C"
JNIEXPORT jdouble JNICALL
Java_ru_livins_aeroboost_model_GameModel_tryBuyPlane(JNIEnv *env, jclass clazz, jint plane_id,
                                                     jdouble current_balance) {
    if (plane_id < 0 || plane_id >= planes.size()) {
        return -1.0;
    }

    Plane& plane = planes[plane_id];
    int price = calculateCurrentPrice(plane);


    if (current_balance >= price) {
        plane.currentPurchased++;
        jdouble newBalance = current_balance - price;
        return newBalance;
    }

    return -1.0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_ru_livins_aeroboost_view_MainBoardActivity_getPlaneCost(JNIEnv *env, jclass clazz,
                                                             jint plane_id) {
    Plane& plane = planes[plane_id];
    double cost = calculateCurrentPrice(plane);
    jint inCost = static_cast<jint>(cost);
    return inCost;
}
extern "C"
JNIEXPORT jint JNICALL
Java_ru_livins_aeroboost_view_MainBoardActivity_getPlaneLevel(JNIEnv *env, jclass clazz) {
    int level = 1;
    for (int i = planes.size() - 1; i > 0; --i) {
        if (planes[i].currentPurchased != 0) {
            if (i - 1 > 0) {
                level = i - 1;
            }
            break;
        }
    }
    return level;
}