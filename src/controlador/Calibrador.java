package controlador;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class Calibrador {


    private final NativeWiringPI pi;
    private boolean running;
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

    public Calibrador() {
        this.pi = new NativeWiringPI();
        running = true;

    }

    public static void main(String[] args) {
        var calibrador = new Calibrador();
        calibrador.run();
    }

    public void run() {
        setup();
        SignalHandler handler = sig -> {
            System.out.println("Ctrl+C detected. Exiting...");
            cleanup();
            running = false;
            System.exit(0); // Exit gracefully
        };

        Signal.handle(new Signal("INT"), handler);

        while (running) {
            var vermelho = lerVermelho();
            var azul = lerAzul();
            var verde = lerVerde();
            System.out.println("Valor de Vermelho: " + vermelho);
            System.out.println("Valor de Verde: " + verde);
            System.out.println("Valor de Azul: " + azul);
            if (vermelho >= 10 && verde < 5 && azul < 5) {
                System.out.println("Vermelho Detectado!");
            }
            if (verde >= 10 && vermelho < 6 && azul < 6) {
                System.out.println("Verde Detectado!");
            }
            if (azul >= 10 && vermelho < 3 && verde < 6) {
                System.out.println("Azul Detectado!");
            }
            if (vermelho > 15 && verde >= 5 && verde < 10 && azul < 6) {
                System.out.println("Laranja Detectado!");
            }
            if (vermelho > 15 && verde > 10 && azul < 10) {
                System.out.println("Amarelo Detectado!");
            }
            if (vermelho > 15 && verde > 15 && azul > 15) {
                System.out.println("Branco Detectado!");
            }
            if (vermelho<5 && verde <5 && azul< 5){
                System.out.println("Preto Detectado!");
            }
        }
    }

    public void setup() {
        pi.createOutput(PIN_LED_RED, NativeWiringPI.STATE_LOW);
        pi.createOutput(PIN_LED_BLUE, NativeWiringPI.STATE_LOW);
        pi.createOutput(PIN_LED_GREEN, NativeWiringPI.STATE_LOW);
        pi.createOutput(PIN_S2, NativeWiringPI.STATE_LOW);
        pi.createOutput(PIN_S3, NativeWiringPI.STATE_LOW);
        pi.createInput(PIN_OUT, NativeWiringPI.PULL_UP);
        pi.createOutput(VCC, NativeWiringPI.STATE_HIGH);
        pi.createOutput(PIN_S0, NativeWiringPI.STATE_HIGH);
        pi.createOutput(PIN_S1, NativeWiringPI.STATE_HIGH);
        pi.createOutput(BUZZER_VCC, NativeWiringPI.STATE_HIGH);
        pi.createOutput(BUZZER_IO, NativeWiringPI.STATE_HIGH);
    }

    public void cleanup() {
        pi.digitalWrite(PIN_LED_RED, NativeWiringPI.STATE_LOW);
        pi.digitalWrite(PIN_LED_BLUE, NativeWiringPI.STATE_LOW);
        pi.digitalWrite(PIN_LED_GREEN, NativeWiringPI.STATE_LOW);
        pi.digitalWrite(PIN_S2, NativeWiringPI.STATE_LOW);
        pi.digitalWrite(PIN_S3, NativeWiringPI.STATE_LOW);
        pi.digitalWrite(VCC, NativeWiringPI.STATE_LOW);
        pi.digitalWrite(PIN_S0, NativeWiringPI.STATE_LOW);
        pi.digitalWrite(PIN_S1, NativeWiringPI.STATE_LOW);
        pi.digitalWrite(BUZZER_VCC, NativeWiringPI.STATE_LOW);
        pi.digitalWrite(BUZZER_IO, NativeWiringPI.STATE_LOW);
        pi.cleanup(PIN_LED_RED);
        pi.cleanup(PIN_LED_BLUE);
        pi.cleanup(PIN_LED_GREEN);
        pi.cleanup(PIN_S2);
        pi.cleanup(PIN_S3);
        pi.cleanup(VCC);
        pi.cleanup(PIN_S0);
        pi.cleanup(PIN_S1);
        pi.cleanup(BUZZER_VCC);
        pi.cleanup(BUZZER_IO);
    }

    public long lerVermelho() {
        pi.digitalWrite(PIN_S2, NativeWiringPI.STATE_LOW);
        pi.digitalWrite(PIN_S3, NativeWiringPI.STATE_LOW);
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

        try {
            return (20 / duracao);
        } catch (ArithmeticException e) {
            return 0;
        }
    }

    public long lerAzul() {
        pi.digitalWrite(PIN_S2, NativeWiringPI.STATE_LOW);
        pi.digitalWrite(PIN_S3, NativeWiringPI.STATE_HIGH);
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
        try {
            return (20 / duracao);
        } catch (ArithmeticException e) {
            return 0;
        }
    }

    public long lerVerde() {
        pi.digitalWrite(PIN_S2, NativeWiringPI.STATE_HIGH);
        pi.digitalWrite(PIN_S3, NativeWiringPI.STATE_HIGH);
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
        try {
            return (20 / duracao);
        } catch (ArithmeticException e) {
            return 0;
        }
    }

}
