import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.StringJoiner;

/**
 * Created by kaaml on 27.05.16.
 */
public class app extends JFrame {
    private JPanel rootPanel;
    private JTextField textField1;
    private JTabbedPane tabbedPane1;
    private JTextPane textPane1;
    private JList list1;
    private JButton button1;
    private JScrollPane scrollPane1;
    private conn con;
    private HashMap<String, tabedPanel > channelsTab = new HashMap<>();

    public app() {
        this.setContentPane(this.rootPanel);
        this.setMinimumSize(new Dimension(450, 600));
        this.pack();
        this.setVisible(true);
        configureListner();
        con = new conn(this);
        Runnable r1 = () -> {
            try {
                con.listen( );
            }catch( Exception e ){
                System.out.println( e.getMessage() );
            }
        };
        new Thread( r1 ).start();

       this.createTab("#default" );

        textField1.addKeyListener(new KeyAdapter() {
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        button1 = new JButton( "Send" );
    }
    public void handleMessage(String msg ){
        //tutaj cala logika
        String tokens[] = msg.split(" ", 2 );
        switch( tokens[0].toUpperCase() ){
            case "MSG":
                this.handleMsg(msg );
                break;
            default :
                System.out.println( "Command [ " + msg + " ] nie znanan" );
                break;
        }
    }

    private void handleMsg(String msg) {
        String tokens[] = msg.split( " ", 3 ); //MSG FROM TO content
        tabedPanel tab = channelsTab.get( tokens[1] );
        if( tab == null ){
            createTab(tokens[1] );
            //this.tabbedPane1.setSelectedIndex( 2 );
            //this.tabbedPane1.setTabComponentAt( 2 )
            tab = channelsTab.get( tokens[1] );
        }
        if( tokens[1].startsWith("#" ) ){
            //obsluga channela
            String fromAndContetn[] = tokens[2].split( " ", 2 );
            tab.addMessage( fromAndContetn[0], fromAndContetn[1] );
        }else{
            //wiadomosci od uzytkownika
            tab.addMessage( tokens[1], tokens[2]);
        }

    }
    private void createTab(String title ){
        tabedPanel pane = new tabedPanel();
        channelsTab.put( title, pane );
        tabbedPane1.addTab( title,  pane );
    }

    private void configureListner(){
        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if( e.getKeyCode() == KeyEvent.VK_ENTER ){
                   System.out.println( tabbedPane1.getTitleAt( tabbedPane1.getSelectedIndex() ));
                    //con.send( msg ^ textfield.getText() );
                    textField1.setText("");
                }
            }
        });


    }
}
