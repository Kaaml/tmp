import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by kaaml on 17.05.16.
 */
public class conn extends Thread{


    private PrintWriter socketOut = null;
    private Socket ircSocket;
    private BufferedReader socketIn = null;
    private app applitacionForm;
    public conn( app application )  {
        this.applitacionForm = application;
//        try {
//            ircSocket = new Socket("localhost", 1337);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            socketIn = new BufferedReader(new InputStreamReader(ircSocket.getInputStream()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            socketOut = new PrintWriter(ircSocket.getOutputStream(), true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        socketOut.println( "nick Ewangelista" );
        //socketOut.println( "join #default" );
    }
    public void connect( String host, int port ){
        try {
            ircSocket = new Socket( host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socketIn = new BufferedReader(new InputStreamReader(ircSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socketOut = new PrintWriter(ircSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void listen()  {

        String responseFromServer = null;
        try {
            while ((responseFromServer = socketIn.readLine()) != null) {
                System.out.println( responseFromServer );
                applitacionForm.handleMessage( responseFromServer );
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println( "something wrond with socket " );
        }finally {
            try {
                socketIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                ircSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socketOut.close();
        }
    }
    @Override
    public void run(){
        this.listen();
    }
    public void send( String msg ){
        socketOut.println( msg );
    }
}
