import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class Records {
    private JFrame frame;
    private JTextArea textArea;
    private JComboBox<String> sortComboBox;
    private JRadioButton ascendingButton;
    private JRadioButton descendingButton;
    private JButton addButton;
    private JButton removeButton;
    private JButton exportButton;
    private ArrayList<String[]> records;

    public Records() {
        frame = new JFrame("List of Records");
        frame.setSize(375, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel recordPanel = new JPanel();
        recordPanel.setLayout(new BorderLayout());

        textArea = new JTextArea(8, 0);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        recordPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel sortingPanel = new JPanel();
        sortingPanel.setLayout(new FlowLayout());
        sortingPanel.add(new JLabel("Sort by:"));
        String[] columnNames = {"Name", "Birthday", "Age"};
        sortComboBox = new JComboBox<String>(columnNames);
        sortComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateTextArea();
            }
        });
        sortingPanel.add(sortComboBox);

        JPanel orderPanel = new JPanel();
        ascendingButton = new JRadioButton("Ascending");
        ascendingButton.setSelected(true);
        ascendingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateTextArea();
            }
        });
        orderPanel.add(ascendingButton);

        descendingButton = new JRadioButton("Descending");
        descendingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateTextArea();
            }
        });
        orderPanel.add(descendingButton);

        ButtonGroup group = new ButtonGroup();
        group.add(ascendingButton);
        group.add(descendingButton);

        sortingPanel.add(orderPanel);

        recordPanel.add(sortingPanel, BorderLayout.SOUTH);

        frame.add(recordPanel, BorderLayout.CENTER);

        JPanel functionPanel = new JPanel();
        functionPanel.setLayout(new GridLayout(1, 3));

        addButton = new JButton("Add a Record");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addRecord();
            }
        });

        functionPanel.add(addButton);

        removeButton = new JButton("Remove a Record");
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeRecord();
            }
        });

        functionPanel.add(removeButton);

        exportButton = new JButton("Export to CSV File");
        exportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportToCSV();
            }
        });

        functionPanel.add(exportButton);

        frame.add(functionPanel, BorderLayout.SOUTH);

        records = new ArrayList<String[]>();

        updateTextArea();

        frame.setVisible(true);
    }

    public void addRecord() {
        String name = JOptionPane.showInputDialog(frame, "Enter name:");
        if (name == null || name.isEmpty()) return;

        String birthday = JOptionPane.showInputDialog(frame, "Enter birthday (MM/DD/YYYY):");
        if (birthday == null || birthday.isEmpty()) return;

        String ageStr = JOptionPane.showInputDialog(frame, "Enter age:");
        if (ageStr == null || ageStr.isEmpty()) return;

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 0) throw new NumberFormatException();

            String[] rowData = {name, birthday, ageStr};
            records.add(rowData);

            updateTextArea();

            JOptionPane.showMessageDialog(frame, "Record added.");

            return;

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid age. Please enter a valid number.");
            return;
        }
    }

    public void removeRecord() {
        if (records.size() == 0) {
            JOptionPane.showMessageDialog(frame, "No records to remove.");
            return;
        }

        String[] options = new String[records.size()];
        for (int i = 0; i < records.size(); i++) {
            options[i] = records.get(i)[0];
        }

        String name = (String) JOptionPane.showInputDialog(frame, "Select record to remove:", "Remove Record", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (name == null || name.isEmpty()) return;

        for (int i = 0; i < records.size(); i++) {
            if (records.get(i)[0].equals(name)) {
                records.remove(i);
                break;
            }
        }

        updateTextArea();

        JOptionPane.showMessageDialog(frame, "Record removed.");
    }

    public void exportToCSV() {
        if (records.size() == 0) {
            JOptionPane.showMessageDialog(frame, "No records to export.");
            return;
        }

        String filename = getCurrentDateTime() + ".csv";

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

            writer.write("NAMES,BIRTHDAY,AGE\n");

            for (String[] record : records) {
                writer.write(record[0] + "," + record[1] + "," + record[2] + "\n");
            }

            writer.close();

            JOptionPane.showMessageDialog(frame, "Records exported to " + filename + ".");

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error exporting records.");
        }
    }

    private void updateTextArea() {
        textArea.setText("");

        int sortColumn = sortComboBox.getSelectedIndex();

        Collections.sort(records, new Comparator<String[]>() {
            public int compare(String[] a, String[] b) {
                if (sortColumn == 2) {
                    int ageA = Integer.parseInt(a[2]);
                    int ageB = Integer.parseInt(b[2]);
                    return ageA - ageB;
                } else {
                    return a[sortColumn].compareTo(b[sortColumn]);
                }
            }
        });

        if (descendingButton.isSelected()) {
            Collections.reverse(records);
        }

        textArea.append("\tName\tBirthdate\tAge\t\n");

        for (String[] record : records) {
            textArea.append("\t" + record[0] + "\t" + record[1] + "\t" + record[2] + "\t\n");
        }
    }

    private String getCurrentDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }
}
