package controlador;

import com.pi4j.context.Context;
import servidor.Server;

public class ControladorRaspberry {
    private Server server;

    // Connect a button to PIN 18 = BCM 24
    private static final int PIN_BUTTON = 24;
    // Connect a LED to PIN 15 = BCM 22
    private static final int PIN_LED_RED = 0;//17,11
    private static final int PIN_LED_GREEN = 3;//22,13
    private static final int PIN_LED_BLUE = 2;//27,15
    private static final int PIN_OUT = 25;
    private static final int PIN_S2 = 29;
    private static final int PIN_S3 = 28;
    private static final int VCC = 27;
    private static final int PIN_S1 = 15;
    private static final int PIN_S0 = 16;
    private static final int BUZZER_VCC = 10;
    private static final int BUZZER_IO = 6;
    private NativeWringPI pi = new NativeWringPI();

    private static int pressCount = 0;

    public static void main(String[] args) {
        ControladorRaspberry cr = new ControladorRaspberry();
        // cr.initServer(args[0], cr);
        cr.runWithC();
    }

    private void initServer(String endereco, ControladorRaspberry cr) {
        server = new Server(endereco, 9678);//"192.168.1.40"
        server.ligarServidor(cr);
    }

    public void setup(){
        pi.createOutput(PIN_LED_RED, NativeWringPI.STATE_LOW);
        pi.createOutput(PIN_LED_BLUE, NativeWringPI.STATE_LOW);
        pi.createOutput(PIN_LED_GREEN, NativeWringPI.STATE_LOW);
        pi.createOutput(PIN_S2, NativeWringPI.STATE_LOW);
        pi.createOutput(PIN_S3, NativeWringPI.STATE_LOW);
        pi.createInput(PIN_OUT, NativeWringPI.PULL_UP);
        pi.createOutput(VCC, NativeWringPI.STATE_HIGH);
        pi.createOutput(PIN_S0, NativeWringPI.STATE_HIGH);
        pi.createOutput(PIN_S1, NativeWringPI.STATE_HIGH);
        pi.createOutput(BUZZER_VCC, NativeWringPI.STATE_HIGH);
        pi.createOutput(BUZZER_IO, NativeWringPI.STATE_HIGH);
    }

    public void runWithC() {
        setup();

        while (true) {
            var vermelho = lerVermelho();
            var azul = lerAzul();
            var verde = lerVerde();
            if (vermelho > azul && vermelho > verde) {
                System.out.println("Vermelho Detectado");
                pi.digitalWrite(PIN_LED_RED, NativeWringPI.STATE_HIGH);
                pi.digitalWrite(PIN_LED_BLUE, NativeWringPI.STATE_LOW);
                pi.digitalWrite(PIN_LED_GREEN, NativeWringPI.STATE_LOW);
                long end = System.currentTimeMillis() + 1000;
                do {
                    pi.digitalWrite(BUZZER_IO, NativeWringPI.STATE_LOW);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    pi.digitalWrite(BUZZER_IO, NativeWringPI.STATE_HIGH);
                } while (System.currentTimeMillis() < end);


            } else if (azul > vermelho && azul > verde) {
                System.out.println("Azul Detectado");
                pi.digitalWrite(PIN_LED_BLUE, NativeWringPI.STATE_HIGH);
                pi.digitalWrite(PIN_LED_RED, NativeWringPI.STATE_LOW);
                pi.digitalWrite(PIN_LED_GREEN, NativeWringPI.STATE_LOW);
            } else if (verde > vermelho && verde > azul) {
                System.out.println("Verde Detectado");
                pi.digitalWrite(PIN_LED_GREEN, NativeWringPI.STATE_HIGH);
                pi.digitalWrite(PIN_LED_BLUE, NativeWringPI.STATE_LOW);
                pi.digitalWrite(PIN_LED_RED, NativeWringPI.STATE_LOW);
            }
        }

    }

    public long lerVermelho() {
        pi.digitalWrite(PIN_S2, NativeWringPI.STATE_LOW);
        pi.digitalWrite(PIN_S3, NativeWringPI.STATE_LOW);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        var start = System.currentTimeMillis();

        for (int pulso = 0; pulso < 20; pulso++) {
            int nivelAnt = 0;
            int nivelNovo = pi.digitalRead(PIN_OUT);
            while (nivelNovo == 1 || nivelAnt == 0) {
                nivelAnt = nivelNovo;
                nivelNovo = pi.digitalRead(PIN_OUT);
            }
        }

        var duracao = System.currentTimeMillis() - start;
        return (20 / duracao);
    }

    public long lerAzul() {
        pi.digitalWrite(PIN_S2, NativeWringPI.STATE_LOW);
        pi.digitalWrite(PIN_S3, NativeWringPI.STATE_HIGH);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        var start = System.currentTimeMillis();

        for (int pulso = 0; pulso < 20; pulso++) {
            int nivelAnt = 0;
            int nivelNovo = pi.digitalRead(PIN_OUT);
            while (nivelNovo == 1 || nivelAnt == 0) {
                nivelAnt = nivelNovo;
                nivelNovo = pi.digitalRead(PIN_OUT);
            }
        }

        var duracao = System.currentTimeMillis() - start;
        return (20 / duracao);
    }

    public long lerVerde() {
        pi.digitalWrite(PIN_S2, NativeWringPI.STATE_HIGH);
        pi.digitalWrite(PIN_S3, NativeWringPI.STATE_HIGH);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        var start = System.currentTimeMillis();

        for (int pulso = 0; pulso < 20; pulso++) {
            int nivelAnt = 0;
            int nivelNovo = pi.digitalRead(PIN_OUT);
            while (nivelNovo == 1 || nivelAnt == 0) {
                nivelAnt = nivelNovo;
                nivelNovo = pi.digitalRead(PIN_OUT);
            }
        }

        var duracao = System.currentTimeMillis() - start;
        return (20 / duracao);
    }

}
