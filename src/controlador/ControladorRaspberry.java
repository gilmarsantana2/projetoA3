package controlador;

import servidor.Server;

import java.util.concurrent.atomic.AtomicBoolean;

public class ControladorRaspberry extends Thread {

    //variáveis de Controle
    private Server server;
    private final NativeWiringPI pi;
    public AtomicBoolean alarmeStarted;
    private AtomicBoolean running;
    public AtomicBoolean alarmRunning;
    private int cor; //Cor selecionada pelo usuário

    //Pinagem da placa Raspberry Usando codificação Wiring
    private static final int PIN_LED_RED = 0;
    private static final int PIN_LED_GREEN = 3;
    private static final int PIN_LED_BLUE = 2;
    private static final int PIN_OUT = 25;
    private static final int PIN_S2 = 29;
    private static final int PIN_S3 = 28;
    private static final int VCC = 27;
    private static final int PIN_S1 = 15;
    private static final int PIN_S0 = 16;
    private static final int BUZZER_VCC = 10;
    private static final int BUZZER_IO = 6;
    private static final int MOTOR_PIN = 21;

    public ControladorRaspberry(Server server, int cor) {
        this.pi = new NativeWiringPI();
        this.server = server;
        alarmeStarted = new AtomicBoolean(false);
        running = new AtomicBoolean(true);
        alarmRunning = new AtomicBoolean(true);
        this.cor = cor;
    }

    //Configuração e inicialização dos pinos
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
        pi.createOutput(MOTOR_PIN, NativeWiringPI.STATE_LOW);
    }

    //Mét0do para restauração do padrão da placa.
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
        pi.digitalWrite(MOTOR_PIN, NativeWiringPI.STATE_LOW);
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
        pi.cleanup(MOTOR_PIN);
    }

    //Script de leitura de cores
    @Override
    public void run() {
        setup();
        initAlarme();
        pi.digitalWrite(MOTOR_PIN, NativeWiringPI.STATE_HIGH);

        while (running.get()) {
            var vermelho = lerVermelho();
            var azul = lerAzul();
            var verde = lerVerde();
            if (vermelho > azul && vermelho > verde) {
                System.out.println("Vermelho Detectado");
                countRed++;
                resultado("#r", "Vermelho");
                pi.digitalWrite(PIN_LED_RED, NativeWiringPI.STATE_HIGH);
                pi.digitalWrite(PIN_LED_BLUE, NativeWiringPI.STATE_LOW);
                pi.digitalWrite(PIN_LED_GREEN, NativeWiringPI.STATE_LOW);
                aproveColor(1);
            } else if (azul > vermelho && azul > verde) {
                System.out.println("Azul Detectado");
                countBlue++;
                resultado("#r", "Azul");
                pi.digitalWrite(PIN_LED_BLUE, NativeWiringPI.STATE_HIGH);
                pi.digitalWrite(PIN_LED_RED, NativeWiringPI.STATE_LOW);
                pi.digitalWrite(PIN_LED_GREEN, NativeWiringPI.STATE_LOW);
                aproveColor(3);
            } else if (verde > vermelho && verde > azul) {
                System.out.println("Verde Detectado");
                countGreen++;
                resultado("#r", "Verde");
                pi.digitalWrite(PIN_LED_GREEN, NativeWiringPI.STATE_HIGH);
                pi.digitalWrite(PIN_LED_BLUE, NativeWiringPI.STATE_LOW);
                pi.digitalWrite(PIN_LED_RED, NativeWiringPI.STATE_LOW);
                aproveColor(2);
            }
        }

        System.out.println("Scaneamento Finalizado");
    }

    //Thread do alarme
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
            pi.digitalWrite(MOTOR_PIN, NativeWiringPI.STATE_LOW);
            resultado("#f", null);
            running.set(false);
        }else {
            //todo
            pi.digitalWrite(MOTOR_PIN, NativeWiringPI.STATE_LOW);
            running.set(false);
            System.out.println("Motor Pausado Pelo Operador!");
        }
    }

    //Script para leitura de cores
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

    //Mensagem enviada ao celular sobre a leitura dos sensores
    private int countRed = 0;
    private int countBlue = 0;
    private int countGreen = 0;

    public void resultado(String resumo, String cor) {
        new Thread(() -> {
            StringBuilder sb = new StringBuilder(resumo);
            sb.append("Resumo de Scaneamento!");
            sb.append("\nSelecionada a cor: ").append(cor);
            sb.append("\nPeças Azuis detectadas: ").append(countBlue);
            sb.append("\nPeças Verdes detectadas: ").append(countGreen);
            sb.append("\nPeças Vermelhas detectadas: ").append(countRed);

            server.send(sb.toString());
        }).start();
    }

    /**
     * 1 -> Vermelho
     * 2 -> Verde
     * 3 -> Azul
     */

    private void aproveColor(int colorToAprove){
        if (cor != colorToAprove){
            pararMotor(true);
            alarmeStarted.set(true);
        }
    }
}
