#include <jni.h>

extern "C" {

    JNIEXPORT jstring JNICALL
    Java_com_tripnetra_tripnetra_Splashscreen_getBaseurl(JNIEnv *env, jobject) {
        return env-> NewStringUTF("https://tripnetra.com/androidphpfiles/");
    }

    JNIEXPORT jstring JNICALL
    Java_com_tripnetra_tripnetra_Splashscreen_getImageurl(JNIEnv *env, jobject) {
        return env-> NewStringUTF("https://tripnetra.com/cpanel_admin/uploads/");
    }

    JNIEXPORT jstring JNICALL
    Java_com_tripnetra_tripnetra_Splashscreen_getPaytmMid(JNIEnv *env, jobject) {
        return env-> NewStringUTF("NaeTec18277227057898");
    }

    JNIEXPORT jstring JNICALL
    Java_com_tripnetra_tripnetra_Splashscreen_getPayuMid(JNIEnv *env, jobject) {
        return env-> NewStringUTF("vB6MnK");
    }

}