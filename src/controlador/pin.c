#include <stdio.h>
#include <wiringPi.h>
#include "controlador_NativeWiringPI.h"


/*
 * Class:     controlador_NativeWiringPI
 * Method:    createOutput
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_controlador_NativeWiringPI_createOutput
(JNIEnv *env, jobject object, jint adress, jint state){
  wiringPiSetup();
  pinMode (adress, OUTPUT);
  digitalWrite (adress, state);

}

/*
 * Class:     controlador_NativeWiringPI
 * Method:    createInput
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_controlador_NativeWiringPI_createInput
  (JNIEnv *env, jobject object, jint adress, jint pull){

    wiringPiSetup();
    pinMode(adress, INPUT);
    if (pull == 2)    {
      pullUpDnControl (adress, PUD_UP);
    }else if (pull == 3){
      pullUpDnControl (adress, PUD_DOWN);
    }
}

/*
 * Class:     controlador_NativeWiringPI
 * Method:    digitalWrite
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_controlador_NativeWiringPI_digitalWrite
  (JNIEnv *env, jobject object, jint adress, jint state){

    digitalWrite (adress, state);

}

/*
 * Class:     controlador_NativeWiringPI
 * Method:    digitalRead
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_controlador_NativeWiringPI_digitalRead
  (JNIEnv *env, jobject object, jint adress){

    return digitalRead(adress);

}

/*
 * Class:     controlador_NativeWiringPI
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_controlador_NativeWiringPI_init
  (JNIEnv *env, jobject object){
    wiringPiSetup();
}

/*
 * Class:     controlador_NativeWiringPI
 * Method:    cleanup
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_controlador_NativeWiringPI_cleanup
  (JNIEnv *env, jobject object, jint pin){

    pinMode(pin, INPUT);
}
