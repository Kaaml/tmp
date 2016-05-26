import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by kaaml on 17.05.16.
 */
public class conn {


    private final PrintWriter socketOut;
    private final Socket ircSocket;
    private final BufferedReader socketIn;

    public conn() throws IOException {
        ircSocket = new Socket("localhost", 1337);
        socketIn = new BufferedReader(new InputStreamReader(ircSocket.getInputStream()));
        socketOut = new PrintWriter(ircSocket.getOutputStream(), true);
    }
    public void run(){

    }
    public void listen(  ) throws IOException {

        String responseFromServer = null;
        while ((responseFromServer = socketIn.readLine()) != null) {
            System.out.println( responseFromServer );
        }
    }
}
