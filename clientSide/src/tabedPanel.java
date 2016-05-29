import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by kaaml on 28.05.16.
 */
public class tabedPanel extends JPanel {
    private JList list1;
    private JScrollPane scrollPane1;
    private JTextPane textPane1;


    public tabedPanel(){
        this.setLayout( new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        list1 = new JList();
        final DefaultListModel defaultListModel1 = new DefaultListModel();
        list1.setModel(defaultListModel1);
        this.add(list1, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(100, 50), null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setHorizontalScrollBarPolicy(31);
        scrollPane1.setVerticalScrollBarPolicy(20);
        this.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        textPane1 = new JTextPane();
        textPane1.setEditable(false);
        scrollPane1.setViewportView(textPane1);
    }

    public void addMessage(String who, String msg ){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String formatedMsg = "< " + dateFormat.format(cal.getTime()) + " > @" + who + ": " + msg + "\n";
        textPane1.setText( textPane1.getText() + formatedMsg );
    }
    public void fillUsers( String []array ){
        //TODO
        DefaultListModel model = (DefaultListModel)list1.getModel();
        for( String user : array ){
            model.addElement( "@" + user );
        }
        list1.setModel( model );
    }

}
