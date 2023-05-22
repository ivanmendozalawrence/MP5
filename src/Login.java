import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Login {
    private JFrame frame;
    private JPanel panel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private HashMap<String, String> credentials;
    private int attempts = 4;

    public Login() {
        panel = new JPanel();
        frame = new JFrame("Login Screen");
        frame.setSize(420, 180);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(1, 0));
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Username"), c);
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        usernameField = new JTextField("", 20);
        panel.add(usernameField, c);
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Password"), c);
        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        passwordField = new JPasswordField("", 20);
        panel.add(passwordField, c);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        loginButton = new JButton("Login");
        loginButton.setBorderPainted(false);
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        panel.add(loginButton, c);
        frame.add(panel);
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);

        credentials = new HashMap<String, String>();
        loadCredentials();
    }

    public void loadCredentials() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("loginCredentials.txt"));
            String username;
            while ((username = reader.readLine()) != null) {
                String password = reader.readLine();
                credentials.put(username, password);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void login() {
        if (attempts == 1) {
            System.out.println("Too many failed attempts, closing the application.");
            JOptionPane.showOptionDialog(frame, "Too many failed attempts. The application will now terminate.", "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{"OK"}, null);
            System.exit(0);
        }

        String username = usernameField.getText();
        String password = passwordField.getText();

        if (credentials.containsKey(username) && credentials.get(username).equals(password)) {
            System.out.println("Login successful!");
            attempts = 0;
            new Records();
            frame.dispose();
        } else {
            System.out.println("Login failed!");
            attempts--;
            JOptionPane.showMessageDialog(frame, "Invalid username or password. \n\t\t\tPlease try again "+attempts+" attempts left");
        }
    }
}
