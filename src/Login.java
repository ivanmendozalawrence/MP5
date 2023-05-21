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
    private JLabel header;
    private JPanel panel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private HashMap<String, String> credentials;
    private int attempts;

    public Login() {
        panel = new JPanel();
        frame = new JFrame("Login");
        frame.setSize(420, 180);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        header = new JLabel("Login", SwingConstants.CENTER);
        header.setPreferredSize(new Dimension(frame.getWidth(), 30));
        frame.add(header, BorderLayout.NORTH);
        panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));
        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);
        panel.add(new JLabel());
        loginButton = new JButton("Login");
        loginButton.setBorderPainted(false);
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        panel.add(loginButton);
        frame.add(panel, BorderLayout.CENTER);
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
        if (attempts >= 3) {
            JOptionPane.showOptionDialog(frame, "Too many failed attempts. The application will now terminate.", "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{"OK"}, null);
            System.exit(0);
        }

        String username = usernameField.getText();
        String password = passwordField.getText();

        if (credentials.containsKey(username) && credentials.get(username).equals(password)) {
            System.out.println("Login successful!");
            attempts = 0;
            new Records();
        } else {
            System.out.println("Login failed!");
            attempts++;
            JOptionPane.showMessageDialog(frame, "Invalid username or password. Please try again.");
        }
    }
}
