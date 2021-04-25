package dictionary;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.Console;
import java.io.IOException;
import java.io.PrintStream;

public class ServerUI {
    private JTextArea textLogs;
    public JTextField textName;
    public JPanel contentPane;
    public JButton addButton;
    public JButton removeButton;
    public JTextField textDef;
    public JList list1;

    ServerUI() {
        PrintStream printStream = new PrintStream(new CustomOutputStream(textLogs));
        System.setOut(printStream);
        System.setErr(printStream);

    }
}
