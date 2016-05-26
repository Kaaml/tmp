import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kaaml on 16.05.16.
 */
public class Server {

    private Set< ClientThread> userts = new HashSet<>();
    private HashMap<String, Channel> serverChannels = new HashMap<>();

    class Channel{
        protected String channelTopic = "";
        protected String chanelName;

        private Set<ClientThread> channelMembers = new HashSet<>();


        //Constructor
        public Channel(String name ){
            this.chanelName = name;
        }

        public void send( String msg ){
            for( ClientThread member : channelMembers ){
                member.send( "MSG " + this.chanelName + " " + msg );
            }
        }

    }












    public void run() throws Exception {
        ServerSocket server = null;
        try {
            server = new ServerSocket(1337);
        }catch (Exception e ){
            System.out.println(e.getMessage() );
            //System.out.println( e.getStackTrace() );
        }
        //ClientServiceThread.setServer( this );

        while( true ){
            Socket client = null;
            try {
                client = server.accept();
                ClientThread c = new ClientThread(client, this );
                userts.add(c);
                c.start();
            }catch( Exception e ){
                System.out.println( e.getStackTrace() );
            }
        }
    }
    public void sendToAll( String msg){
        for( ClientThread cl : userts ){
              cl.send(msg);
        }
    }
}
