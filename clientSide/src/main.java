import java.io.File;

/**
 * Created by kaaml on 17.05.16.
 */
public class main {
    public static void main(String [] args) {

        UserConfig cfg = new UserConfig();
        app myApp = new app();
        login log = new login(cfg, myApp);




    }
}
