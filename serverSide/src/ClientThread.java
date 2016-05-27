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
    String getUserName(){
        return this.userName;
    }
    @Override
    public void run(){
        String msg = null;
        try {
            while ((msg = in.readLine()) != null) {
                System.out.println(socket.getInetAddress() + " MSG{'" + msg + "'}");
                //server.sendToAll(msg );
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
    public void send( String msg, String from ){
        outWritter.println( "MSG " + from + " " + msg );
    }

    public void handleMessage( String msg ){
        String command = msg.split(" ", 2 )[0];
        switch ( command.toUpperCase() ){
            case "JOIN":
                server.handleJoin( this, msg );
                break;
            case "NICK" :
                this.handleNick( msg );
                break;
            case "MSG":
                server.handleMsg(this, msg );
                break;
            case "USERS":
                server.handleUsers(this, msg );
                break;
            default:
                System.out.println("Command not found" );
                System.out.println( "Command[ " + command + " ] " );
                //send error or sth
                break;
        }

    }
    private void handleNick( String msg ){
        String nick = msg.split( " ", 2 )[1];
        this.userName = nick;
        server.handleNick( this, msg );
    }
}
