import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.time.*;

class Records {
    private JFrame frame;
    private JTextArea textArea;
    private JComboBox<String> sortComboBox;
    private JRadioButton ascendingButton;
    private JRadioButton descendingButton;
    private JButton addButton;
    private JButton removeButton;
    private JButton exportButton;
    private ArrayList<String[]> records;

    // Add Record window
    private JFrame addFrame;
    private JTextField nameField;
    private JComboBox<String> monthComboBox;
    private JComboBox<Integer> dayComboBox;
    private JComboBox<Integer> yearComboBox;

    public Records() {
        frame = new JFrame("List of Records");
        frame.setSize(375, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel recordPanel = new JPanel();
        recordPanel.setLayout(new BorderLayout());

        textArea = new JTextArea();
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
                showAddRecordWindow();
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

    public void showAddRecordWindow() {
        addFrame = new JFrame("Add Record");
        addFrame.setSize(400, 200);
        addFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        addFrame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_END;
        inputPanel.add(new JLabel("Name:"), c);

        c.gridx++;
        c.anchor = GridBagConstraints.LINE_START;
        nameField = new JTextField("", 20);
        inputPanel.add(nameField, c);

        c.gridx--;
        c.gridy++;
        c.anchor = GridBagConstraints.LINE_END;
        inputPanel.add(new JLabel("Birthday:"), c);

        c.gridx++;
        c.anchor = GridBagConstraints.LINE_START;
        JPanel datePanel = new JPanel();
        datePanel.setLayout(new FlowLayout());
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        monthComboBox = new JComboBox<String>(months);
        datePanel.add(monthComboBox);
        Integer[] days = new Integer[31];
        for (int i = 1; i <= 31; i++) days[i - 1] = i;
        dayComboBox = new JComboBox<Integer>(days);
        datePanel.add(dayComboBox);
        Integer[] years = new Integer[Year.now().getValue() - 1899];
        for (int i = 1900; i <= Year.now().getValue(); i++) years[i - 1900] = i;
        yearComboBox = new JComboBox<Integer>(years);
        datePanel.add(yearComboBox);
        inputPanel.add(datePanel, c);

        addFrame.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton saveAndGoBackButton = new JButton("Save and Go Back");
        saveAndGoBackButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (addRecord()) {
                    addFrame.setVisible(false);
                }
            }
        });
        buttonPanel.add(saveAndGoBackButton);

        JButton saveAndAddAnotherButton = new JButton("Save & Add Another");
        saveAndAddAnotherButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addRecord();
            }
        });
        buttonPanel.add(saveAndAddAnotherButton);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addFrame.setVisible(false);
            }
        });
        buttonPanel.add(backButton);

        addFrame.add(buttonPanel, BorderLayout.SOUTH);

        addFrame.setVisible(true);
    }

    public boolean addRecord() {
        String name=nameField.getText().trim();
        if(name.isEmpty()){
            System.out.println("Please enter a name.");
            JOptionPane.showMessageDialog(addFrame,"Please enter a name.");
            return false;
        }

        int month=monthComboBox.getSelectedIndex()+1;
        int day=(int)dayComboBox.getSelectedItem();
        int year=(int)yearComboBox.getSelectedItem();

        LocalDate birthday;
        try{
            birthday=LocalDate.of(year,month,day);

            if(birthday.isAfter(LocalDate.now())){
                throw new IllegalArgumentException();
            }

        }catch(Exception e){
            System.out.println("Invalid birthday. Please enter a valid date.");
            JOptionPane.showMessageDialog(addFrame,"Invalid birthday. Please enter a valid date.");
            return false;
        }

        long age=Period.between(birthday,LocalDate.now()).getYears();

        String[] rowData={name,String.format("%02d/%02d/%04d",month,day,year),String.valueOf(age)};
        records.add(rowData);

        updateTextArea();

        JOptionPane.showMessageDialog(addFrame,"Record added.");

        return true;
    }

    public void removeRecord() {
        if(records.size()==0){
            JOptionPane.showMessageDialog(frame,"No records to remove.");
            return;
        }

        JFrame removeFrame = new JFrame("Remove Record");
        removeFrame.setSize(600, 200);
        removeFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        removeFrame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_END;
        inputPanel.add(new JLabel("Name:"), c);

        c.gridx++;
        c.anchor = GridBagConstraints.LINE_START;
        JTextField nameField = new JTextField("", 20);
        inputPanel.add(nameField, c);

        removeFrame.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton removeAndGoBackButton = new JButton("Remove and Go Back");
        removeAndGoBackButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (removeRecord(nameField.getText().trim())) {
                    removeFrame.setVisible(false);
                }
            }
        });
        buttonPanel.add(removeAndGoBackButton);

        JButton saveAndRemoveAnotherButton = new JButton("Save & Remove Another");
        saveAndRemoveAnotherButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeRecord(nameField.getText().trim());
            }
        });
        buttonPanel.add(saveAndRemoveAnotherButton);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeFrame.setVisible(false);
            }
        });
        buttonPanel.add(backButton);

        removeFrame.add(buttonPanel, BorderLayout.SOUTH);

        removeFrame.setVisible(true);
    }

    private boolean removeRecord(String name) {
        if(name.isEmpty()){
            JOptionPane.showMessageDialog(frame,"Please enter a name.");
            return false;
        }

        boolean found = false;

        for(int i=0;i<records.size();i++){
            if(records.get(i)[0].equals(name)){
                records.remove(i);
                found = true;
                break;
            }
        }

        if(found){
            updateTextArea();
            JOptionPane.showMessageDialog(frame,"Record removed.");
            return true;

        }else{
            JOptionPane.showMessageDialog(frame,"An IllegalArgumentException Caught: Name Not Found!");
            return false;

        }
    }

    public void exportToCSV() {
        if(records.size()==0){
            System.out.println("No Records");
            JOptionPane.showMessageDialog(frame,"No records to export.");
            return;
        }

        String filename=getCurrentDateTime()+".csv";

        try{
            BufferedWriter writer=new BufferedWriter(new FileWriter(filename));

            writer.write("NAMES,BIRTHDAY,AGE\n");

            for(String[] record:records){
                writer.write(record[0]+","+record[1]+","+record[2]+"\n");
            }

            writer.close();

            JOptionPane.showMessageDialog(frame,"Records exported to "+filename+".");

            System.out.println("Records successful exported to "+filename+".");

        }catch(IOException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,"Error exporting records.");
        }
    }

    private void updateTextArea(){
        textArea.setText("");

        int sortColumn=sortComboBox.getSelectedIndex();

        Collections.sort(records,new Comparator<String[]>(){
            public int compare(String[] a,String[] b){
                if(sortColumn==2){
                    int ageA=Integer.parseInt(a[2]);
                    int ageB=Integer.parseInt(b[2]);
                    return ageA-ageB;
                }else{
                    return a[sortColumn].compareTo(b[sortColumn]);
                }
            }
        });

        if(descendingButton.isSelected()){
            Collections.reverse(records);
        }

        textArea.append("\tName\tBirthdate\tAge\t\n");

        for(String[] record:records){
            textArea.append("\t"+record[0]+"\t"+record[1]+"\t"+record[2]+"\t\n");
        }
    }

    private String getCurrentDateTime(){
        SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMddHHmmss");
        Date date=new Date(System.currentTimeMillis());
        return formatter.format(date);
    }
}

