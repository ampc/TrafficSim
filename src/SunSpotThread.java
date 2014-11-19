import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.peripheral.ota.OTACommandServer;
import java.util.Scanner;
import javax.microedition.io.*;

public class SunSpotThread extends Thread {

    private static final int HOST_PORT = 67;
    private static final int HOST_PORT_DESTINATION = 68;

    public SunSpotThread() {
        OTACommandServer.start("SendDataDemo");
        startReceiverThread();
        run();
    }

    public void run() {
        Scanner stdin;
        stdin = new Scanner(System.in);
        while (true) {
            System.out.println("Escrever Input");
            String me = stdin.nextLine();
            me.trim();
            System.out.println("Pedir troca de sinal" + me);
            if (me.equals("1")) {
                cor = "vermelho1";
            } else if (me.equals("2")) {
                cor = "verde1";
            }
            else if (me.equals("3")) {
                cor = "vermelho2";
            }
            else if (me.equals("4")) {
                cor = "verde2";
            }
            startSenderThread();
        }        
    }

    public void startReceiverThread() {
        new Thread() {
            public void run() {
                RadiogramConnection rCon = null;
                Datagram dg = null;

                try {
                    // Open up a server-side broadcast radiogram connection
                    // to listen for sensor readings being sent by different SPOTs
                    rCon = (RadiogramConnection) Connector.open("radiogram://:" + HOST_PORT);
                    dg = rCon.newDatagram(rCon.getMaximumLength());
                } catch (Exception e) {
                    System.err.println("setUp caught " + e.getMessage());

                }

                // Main data collection loop
                while (true) {
                    try {

                        rCon.receive(dg);
                        int semaforo = dg.readInt();

                        System.out.println("Quero passar: " + semaforo);

                    } catch (Exception e) {
                        System.out.println("erro");
                    }
                }
            }
        }.start();
    }
    static public String cor;

    public void startSenderThread() {
        new Thread() {
            public void run() {
                RadiogramConnection rCon1 = null;
                Datagram dp = null;

                try {
                    rCon1 = (RadiogramConnection) Connector.open("radiogram://broadcast:" + HOST_PORT_DESTINATION);
                    dp = rCon1.newDatagram(rCon1.getMaximumLength());
                } catch (Exception e) {
                    System.err.println("setUp caught " + e.getMessage());
                }

                try {

                    dp.reset();
                    dp.writeUTF(cor);
                    rCon1.send(dp);
                    System.out.println("enviar?");
                } catch (Exception e) {
                    System.err.println(e);
                }

            }

        }
                .start();
    }
}
