#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_test001_FirstFragment_stringFromJNI(
        JNIEnv* env,
        jobject /* this */,
        jint number) {
    const std::string prefix = "Number of clicks is: ";
    std::string result = prefix + std::to_string(number);
    return env->NewStringUTF(result.c_str());
}

extern "C" JNIEXPORT jint JNICALL
Java_com_example_test001_FirstFragment_addTwoNumbers(
        JNIEnv* env,
        jobject /* this */,
        jint a,
        jint b) {
    return a + b;
}