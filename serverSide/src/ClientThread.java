import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by kaaml on 16.05.16.
 */
public class ClientThread extends Thread {
    private Socket socket;
    private PrintWriter outWritter;
    private BufferedReader in;
    private Server server;

    private String userName = "unknown";

    public ClientThread(Socket socket, Server server) {
        System.out.println( "New thread [" + this.getId() + " ] " );
        this.server = server;
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outWritter = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run(){
        String msg = null;
        try {
            while ((msg = in.readLine()) != null) {
                System.out.println(socket.getInetAddress() + " MSG{'" + msg + "'}");
                server.sendToAll(msg );
                handleMessage(msg);
            }
        }catch (SocketException e ){
            System.out.println( "socket exception zerwane polaczenia" );
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            System.out.println( "Thread [ " + this.getId() + " ] are closed" );
        }
    }
    public void send( String msg){
        outWritter.println( msg );
    }

    public void handleMessage( String msg ){
        String command = msg.split(" ", 2 )[0];
        switch ( command){
            case "JOIN":
                server.handleJoin( this, msg );
                break;
            default:
                System.out.println("Command not found" );
                //send error or sth
                break;
        }

    }
}
