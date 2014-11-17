import java.io.*; 
import java.net.*;
class AndroidTCPclient {  
    public static void main(String argv[]) throws Exception  {
        int port = Integer.parseInt(argv[0]);

        String sentence;
        String modifiedSentence;
        BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));  
        Socket clientSocket = new Socket("localhost", port); 
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        while(true) {
            while ((sentence = inFromServer.readLine()) != null) {
                System.out.println("I read: " + sentence);
            }
        }
    }
}
