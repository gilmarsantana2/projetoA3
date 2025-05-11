package controlador;

import java.io.IOException;

public class NativeWiringPI {

    static{
        try {
            NativeUtils.loadLibraryFromJar("/controlador/libpin.so");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    * gcc -o libpin.so -shared -I/usr/lib/jvm/java-11-openjdk-armhf/include -I/usr/lib/jvm/java-11-openjdk-armhf/include/linux -lwiringPi /home/pi/Downloads/pin.c
    * */

    public static final int STATE_LOW = 0;
    public static final int STATE_HIGH = 1;
    public static final int PULL_UP = 2;
    public static final int PULL_DOWN = 3;

    public native void createOutput(int address, int state);

    public native void createInput(int address, int pull);

    public native void digitalWrite(int address, int state);

    public native int digitalRead(int address);

    public native void init();

    public native void cleanup(int pin);

}
