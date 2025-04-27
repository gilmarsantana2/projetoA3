#include <stdio.h>
#include <wiringPi.h>
#include "controlador_NativeWringPI.h"


/*
 * Class:     controlador_NativeWringPI
 * Method:    createOutput
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_controlador_NativeWringPI_createOutput
(JNIEnv *env, jobject object, jint adress, jint state){
  
  wiringPiSetup();
  pinMode (adress, OUTPUT);
  digitalWrite (adress, state);

}

/*
 * Class:     controlador_NativeWringPI
 * Method:    createInput
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_controlador_NativeWringPI_createInput
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
 * Class:     controlador_NativeWringPI
 * Method:    digitalWrite
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_controlador_NativeWringPI_digitalWrite
  (JNIEnv *env, jobject object, jint adress, jint state){

    digitalWrite (adress, state);

}

/*
 * Class:     controlador_NativeWringPI
 * Method:    digitalRead
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_controlador_NativeWringPI_digitalRead
  (JNIEnv *env, jobject object, jint adress){

    return digitalRead(adress);

}