import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by kaaml on 30.05.16.
 */
public class database {
    private Connection c = null;


    public void connect(){
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:/home/kaaml/db");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    public void addMessageToHistory( String sender, String msg  ){
        Statement stmt = null;
        try {
           stmt =  c.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql = "INSERT INTO history VALUES (  1," + msg + ", " + " nic, " + sender + "; ";
        try {
            stmt.executeUpdate(sql);
            stmt.close();
            c.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

}
