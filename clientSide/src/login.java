import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by kaaml on 22.03.16.
 */
public class login extends JFrame {
    private JTextField loginText;
    private JPasswordField passwordText;
    private JRadioButton saveSettingsRadio;
    private JButton loginButton;
    private JPanel root;
    private JTextField serverText;
    private JLabel loginLabel;
    private JLabel passwordLabel;
    private JPasswordField serverPasswordText;
    private boolean ok = false;

    private boolean saveSettings;
    private UserConfig userConfig;
    private app App;


    public login(UserConfig userCfg, app application ) {
        App = application;
        userConfig = userCfg;
        init();
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("Login button clicked");
                String login = loginText.getText();
                String passw = passwordText.getPassword().toString();
                String serverPassword = serverPasswordText.getPassword().toString();
                String serverName = serverText.getText();
                saveSettings = saveSettingsRadio.isSelected();

                if (serverName.isEmpty()) {
                    serverText.setBackground(Color.red);
                    serverText.grabFocus();
                }
                if (login.isEmpty()) {
                    loginText.setBackground(Color.red);
                    loginText.grabFocus();
                }
                if (!login.isEmpty() && !serverName.isEmpty()) {
                    userConfig.setServerName(serverName);
                    userConfig.setServerPassword(serverPassword);
                    userConfig.setUserName(login);
                    userConfig.setUserPassword(passw);
                    userConfig.storeSettings(saveSettings);
                    App.connect( userCfg);
                    ok = true;
                    setVisible(false);
                    dispose();
                    System.out.println("tutaj okno logowania powinno sie zamknac");
                }
            }
        });

        serverText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                String text = serverText.getText();
                if ( text.isEmpty()) {
                    serverText.setBackground(Color.green);
                } else {
                    String[] tokens = text.split( ":", 2 );
                    if( tokens.length ==2 ){
                        if( !tokens[1].matches( "d+") ){
                            serverText.setBackground( Color.red );
                        }
                    }
                    if( !tokens[0].matches( "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$")){
                        serverText.setBackground( Color.red );
                    }

                    //serverText.setBackground(Color.DARK_GRAY);
                }
            }
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                serverText.setBackground( Color.white );
            }
        });

        loginText.addComponentListener(new ComponentAdapter() {
        });
    }




    public void init() {
        this.setContentPane(this.root);
        this.pack();
        this.setVisible(true);
        this.setAlwaysOnTop(true);
    }

    public UserConfig GetConfig() {
        return userConfig;
    }

    public void setUserConfig(UserConfig us) {
        userConfig = us;
    }

}
