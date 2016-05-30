import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
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
    private JLabel label;
    private conn con;
    private HashMap<String, tabedPanel> channelsTab = new HashMap<>();
    private boolean isConnected = false;
    private database db = null;
    private String userName;
    private String hostName = "localhost";
    private int portNumber = 1337;

    public app() {
        this.setContentPane(this.rootPanel);
        this.setMinimumSize(new Dimension(450, 600));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        configureListner();
        con = new conn(this);
        //runConnectionListening();
        initAutosugestion();
        db = new database();
        db.connect();
        this.createTab("#default");

        textField1.addKeyListener(new KeyAdapter() {
        });
        label.setText("<html><font color='red'>Disconnected</font></html>");
        tabbedPane1.removeTabAt(0);
    }

    private void runConnectionListening() {
        Runnable r1 = () -> {
            try {
                con.listen();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        };
        new Thread(r1).start();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        button1 = new JButton("Send");
    }

    public void handleMessage(String msg) {
        //tutaj cala logika
        String tokens[] = msg.split(" ", 2);
        switch (tokens[0].toUpperCase()) {
            case "MSG":
                this.handleMsg(msg);
                break;
            case "USERS":
                this.handleUsers(msg);
                break;
            default:
                System.out.println("Command [ " + msg + " ] nie znanan");
                break;
        }
    }

    private void handleUsers(String msg) {
        String tokens[] = msg.split(" ", 3);
        if (tokens.length < 3)
            return;
        tabedPanel tab = channelsTab.get(tokens[1]);
        if (tab == null)
            return;
        tab.fillUsers(tokens[2].split(" "));

    }

    private void handleMsg(String msg) {
        String tokens[] = msg.split(" ", 3); //MSG FROM TO content
        tabedPanel tab = channelsTab.get(tokens[1]);
        if (tab == null) {
            createTab(tokens[1]);
            tab = channelsTab.get(tokens[1]);
        }
        if (tokens[1].startsWith("#")) {
            //obsluga channela
            String fromAndContetn[] = tokens[2].split(" ", 2);
            tab.addMessage(fromAndContetn[0], fromAndContetn[1]);
            db.addMessageToHistory( fromAndContetn[0], fromAndContetn[1] );
        } else {
            //wiadomosci od uzytkownika
            tab.addMessage(tokens[1], tokens[2]);
        }

    }

    private void createTab(String title) {
        tabedPanel pane = new tabedPanel();
        channelsTab.put(title, pane);
        tabbedPane1.addTab(title, pane);
        int count = tabbedPane1.getTabCount();
        tabbedPane1.setSelectedIndex(count - 1);
    }

    private void removeTab(String title) {
        if (title.equals("#default")) {
            return;
        }
        for (int i = tabbedPane1.getTabCount() - 1; i >= 0; --i) {
            System.out.println(i + " : " + tabbedPane1.getTitleAt(i));
            if (tabbedPane1.getTitleAt(i).equals(title)) {
                tabbedPane1.removeTabAt(i);
                break;
            }
        }
        channelsTab.remove(title);


    }

    private void configureListner() {
        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    System.out.println(tabbedPane1.getTitleAt(tabbedPane1.getSelectedIndex()));
                    String to = tabbedPane1.getTitleAt(tabbedPane1.getSelectedIndex());
                    String msg = textField1.getText();
                    handleTextField(msg, to);
                    textField1.setText("");
                }
            }
        });
    }

    private void handleTextField(String msg, String tabName) {
        if (msg.startsWith("/")) {
            String tokens[] = msg.split(" ", 2);
            switch (tokens[0].toUpperCase()) {
                case "/EXIT":
                    System.exit(1);
                    break;
                case "/CLOSE":
                    if (tabName.startsWith("#"))
                        con.send("PART " + tabName);
                    removeTab(tabName);
                    break;
                case "/PRIV":
                    createTab(tokens[1]);
                    break;
                case "/NICK":
                    if (tokens.length < 2)
                        return;
                    con.send("NICK " + tokens[1]);
                    break;
                case "/JOIN":
                    if (tokens.length < 2)
                        return;
                    con.send("JOIN " + tokens[1]);
                    con.send("USERS " + tokens[1]);
                    createTab(tokens[1]);
                    break;
                case "/CONNECT":
                    if( isConnected )
                        return;
                    String hostPort[] = tokens[1].split(" ");
                    int port = Integer.parseInt(hostPort[1]);
                    con.connect(hostPort[0], port);
                    runConnectionListening();
                    label.setText("<html><font color='green'>Connected</font></html>");
                    con.send("USERS #default");
                    break;
                case "/USERS":
                    if (tabName.startsWith("#"))
                        con.send("USERS " + tabName);
                    break;
                default:
                    System.out.println("polecenie nie znane");
                    break;
            }
        } else {
            if (!tabName.startsWith("#")) {
                channelsTab.get(tabName).addMessage("I", msg);
            }
            con.send("MSG " + tabName + " " + msg);
        }
    }

    private void initAutosugestion() {
        String[] suggestWords = {
                "/exit", "/close", "/priv", "/join", "/nick", "/users", "/connect"
        };
        //get root frame
        ArrayList<String> suWo = new ArrayList<>(Arrays.asList(suggestWords));
        JFrame frame = this;
        AutoSuggestor autoSuggestor = new AutoSuggestor(textField1, frame, suWo, Color.WHITE.brighter(), Color.BLACK, Color.GRAY, 0.75f) {
            @Override
            boolean wordTyped(String typedWord) {
                System.out.println(typedWord);
                return super.wordTyped(typedWord);
            }
        };

    }

    public void connect(UserConfig userCfg) {
        this.userName = userCfg.getUserName();
        this.hostName = userCfg.getServerName();
        this.portNumber = 1337;
        if( isConnected )
            return;

        con.connect( this.hostName, this.portNumber );
        runConnectionListening();
        label.setText("<html><font color='green'>Connected</font></html>");
        con.send("NICK " + this.userName );
        con.send("USERS #default");
    }
}
