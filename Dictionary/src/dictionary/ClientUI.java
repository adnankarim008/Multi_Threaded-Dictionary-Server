package dictionary;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.Console;
import java.io.IOException;
import java.io.PrintStream;

public class ClientUI {
    public JButton updateButton;
    private JTextArea textLogs;
    public JTextField textName;
    public JPanel contentPane;
    public JButton addButton;
    public JButton removeButton;
    public JTextField textDef;
    public JButton queryButton;


    ClientUI() {
        PrintStream printStream = new PrintStream(new CustomOutputStream(textLogs));
        System.setOut(printStream);
        System.setErr(printStream);

    }
}
