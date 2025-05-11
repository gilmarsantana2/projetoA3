package controlador;

import servidor.Server;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ControladorRaspberry extends Thread {
    private Server server;
    private final NativeWiringPI pi;
    public AtomicBoolean alarmeStarted;
    private AtomicBoolean running;
    public AtomicBoolean alarmRunning;

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

    public ControladorRaspberry(Server server) {
        this.pi = new NativeWiringPI();
        this.server = server;
        alarmeStarted = new AtomicBoolean(false);
        running = new AtomicBoolean(true);
        alarmRunning = new AtomicBoolean(true);
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

    @Override
    public void run() {
        setup();
        initAlarme();

        while (running.get()) {
            var vermelho = lerVermelho();
            var azul = lerAzul();
            var verde = lerVerde();
            if (vermelho > azul && vermelho > verde) {
                System.out.println("Vermelho Detectado");
                pi.digitalWrite(PIN_LED_RED, NativeWiringPI.STATE_HIGH);
                pi.digitalWrite(PIN_LED_BLUE, NativeWiringPI.STATE_LOW);
                pi.digitalWrite(PIN_LED_GREEN, NativeWiringPI.STATE_LOW);
                pararMotor(true);
                alarmeStarted.set(true);
            } else if (azul > vermelho && azul > verde) {
                System.out.println("Azul Detectado");
                countBlue++;
                resultado("#r");
                pi.digitalWrite(PIN_LED_BLUE, NativeWiringPI.STATE_HIGH);
                pi.digitalWrite(PIN_LED_RED, NativeWiringPI.STATE_LOW);
                pi.digitalWrite(PIN_LED_GREEN, NativeWiringPI.STATE_LOW);
            } else if (verde > vermelho && verde > azul) {
                System.out.println("Verde Detectado");
                countGreen++;
                resultado("#r");
                pi.digitalWrite(PIN_LED_GREEN, NativeWiringPI.STATE_HIGH);
                pi.digitalWrite(PIN_LED_BLUE, NativeWiringPI.STATE_LOW);
                pi.digitalWrite(PIN_LED_RED, NativeWiringPI.STATE_LOW);
            }
        }

        System.out.println("Scaneamento Finalizado");
    }

    public void initAlarme() {
        new Thread(() -> {
            while (alarmRunning.get()) {
                if (alarmeStarted.get()) {
                    pi.digitalWrite(BUZZER_IO, NativeWiringPI.STATE_LOW);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    pi.digitalWrite(BUZZER_IO, NativeWiringPI.STATE_HIGH);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            System.out.println("Thread de alarme desligada");
            running.set(false);
        }).start();

    }

    public void pararMotor(boolean falha) {
        if (falha){
            //todo parar motor
            countRed++;
            resultado("#f");
            running.set(false);
        }else {
            //todo
            running.set(false);
            System.out.println("Falta implementar!");
        }
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
        return (20 / duracao);
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
        return (20 / duracao);
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
        return (20 / duracao);
    }

    private int countRed = 0;
    private int countBlue = 0;
    private int countGreen = 0;

    public void resultado(String resumo) {
        new Thread(() -> {
            StringBuilder sb = new StringBuilder(resumo);
            sb.append("Resumo de Scaneamento!");
            sb.append("\nPeças Azuis detectadas: ").append(countBlue);
            sb.append("\nPeças Verdes detectadas: ").append(countGreen);
            sb.append("\nPeças Vermelhas detectadas: ").append(countRed);

            server.send(sb.toString());
        }).start();
    }
}
