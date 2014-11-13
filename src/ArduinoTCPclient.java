import java.io.*; 
import java.net.*;
class ArduinoTCPclient {  
    public static void main(String argv[]) throws Exception  {
        int port = Integer.parseInt(argv[0]);
        String output = "L";

        String sentence;
        String modifiedSentence;
        BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));  
        Socket clientSocket = new Socket("localhost", port); 
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        while(true) {
            while ((sentence = in.readLine()) != null) {
                System.out.println("I read: " + sentence);
                System.out.println("Sending: " + output);
                out.writeBytes(output);
                output = "H";
            }
        }
    }
}
