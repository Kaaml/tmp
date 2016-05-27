import com.sun.deploy.util.SessionState;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kaaml on 16.05.16.
 */
public class Server {
    private Set<ClientThread> connections = new HashSet<>();
    private Set< ClientThread> registerUser = new HashSet<>();
    private HashMap<String, Channel> serverChannels = new HashMap<>();


    class Channel{

        protected String channelTopic = "";
        protected String chanelName;

        private Set<ClientThread> channelMembers = new HashSet<>();


        //Constructor
        public Channel(String name ){
            this.chanelName = name;
        }

        public void addMember( ClientThread newMember ){
            this.send( "Member " + newMember.getUserName() + " has joined to channel " );
            channelMembers.add( newMember );
        }
        public void send( String msg ){
            for( ClientThread member : channelMembers ){
                member.send( "MSG " + this.chanelName + " " + msg );
            }
        }
        public void rawSend(String msg ){
            for( ClientThread member : channelMembers ){
                member.send( msg );
            }
        }
        public void send( String msg, String from ){
            for( ClientThread member : channelMembers ){
                member.send( "MSG " + from + " " + msg );
            }
        }
        public void removeClient( ClientThread client ){
            channelMembers.remove( client );
            send( client.getUserName() + " has left the channel " );
        }

    }





    public Server(){
        serverChannels.put("#default", new Channel("#default") );
    }

    public void handleNick(ClientThread clientThread, String msg) {
        connections.remove( clientThread );
        registerUser.add( clientThread );
        Channel ch = serverChannels.get("#default");
        ch.addMember( clientThread );
        clientThread.addChannel(ch);
    }


    public void handlePart(ClientThread clientThread, String msg) {
        String channel = msg.split( " " )[1];
        Channel ch = serverChannels.get( channel );
        if( ch != null ){
            ch.removeClient( clientThread );
            clientThread.removeChannel(ch);
        }

    }

    public void handleUsers(ClientThread clientThread, String msg) {
        String result = "[ ";
        String chanels[] = msg.split(" " );
        if( chanels.length <2 )
            return;

        Channel ch = serverChannels.get( chanels[1] );
        if( ch == null )
            return;

        for( ClientThread user : ch.channelMembers ){
            result+=user.getUserName() + " " ;
        }
        result+="]";
        clientThread.send( result );
    }

    public void handleMsg(ClientThread clientThread, String msg) {
        String msgTokens[] = msg.split(" ", 3 );    // 0 - token MSG, 1 - target, 2 - content
        if( msgTokens.length < 3 ){
            return;
        }
        System.out.println( "MSG [" + msgTokens[1] +"][" + msgTokens[2] + "]" );
        if( msgTokens[1].startsWith("#" ) ){
            Channel target = serverChannels.get( msgTokens[1] );
            if( target != null ){
                String decoratedMessage = "MSG " + msgTokens[1] +" " + clientThread.getUserName() + " " + msgTokens[2] ;
                target.rawSend( decoratedMessage );
            }else{
                System.out.println( "Channel nie znaleziony" );
                return;
            }
        }else{ //user
            ClientThread target = findUser( msgTokens[1] );
            if( target != null ){
                target.send( msgTokens[2], clientThread.getUserName() );
            }else{
                System.out.println( "uzytkownik nie znaleziony" );
                return;
            }
        }
    }

    public void handleJoin(ClientThread clientThread, String msg) {
        String channels[] =msg.split(" " );
        for( String channel : channels ) {
            if (channel.startsWith("#")) {
                Channel ch = serverChannels.get(channel);
                if (ch == null) {
                    ch = new Channel(channel);
                    serverChannels.put(channel, ch);
                    clientThread.addChannel(ch );
                }
                ch.addMember(clientThread);
                clientThread.addChannel(ch);
                System.out.println( "Addded " + clientThread.getUserName() + " to channel " + channel );
            }
        }
    }





    public void run() throws Exception {
        ServerSocket server = null;
        try {
            server = new ServerSocket(1337);
        }catch (Exception e ){
            System.out.println(e.getMessage() );
        }


        while( true ){
            Socket client = null;
            try {
                client = server.accept();
                ClientThread c = new ClientThread(client, this );
                connections.add( c );
                c.start();
            }catch( Exception e ){
                System.out.println( e.getStackTrace() );
            }
        }
    }
    public void sendToAll( String msg){
        for( ClientThread cl : registerUser){
              cl.send(msg);
        }
    }
    public ClientThread findUser( String name ){
        for( ClientThread user : registerUser ){
            String u = user.getUserName();

            if( user.getUserName().equals( name ) ){
                return user;
            }
        }
        return null;
    }
}
