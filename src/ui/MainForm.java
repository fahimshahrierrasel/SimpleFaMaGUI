package ui;

import service.FamaOperation;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.BorderLayout;
import java.io.*;

public class MainForm {

    private JFrame frame;
    private JTextPane txtPnRuleText;
    private JComboBox comboBoxOperation;

    private FamaOperation famaOperation = null;

    private String[] options = new String[]{
            "Validation",
            "Products",
            "Number of Products",
            "Commonality",
            "Variability",
            "Valid Product",
            "Valid Configuration",
            "Error Detection",
            "Error Explanations",
            "Invalid Product Explanation",
            "Core Features",
            "Variant Features"
    };
    private JEditorPane dtrpnOutput;

    /**
     * Create the application.
     */
    public MainForm() {
        initialize();
        frame.setVisible(true);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JPanel panelRuleEditor = new JPanel();
        panelRuleEditor.setBorder(new TitledBorder(null, "Rule Editor", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panelRuleEditor.setBounds(6, 11, 480, 285);
        frame.getContentPane().add(panelRuleEditor);
        panelRuleEditor.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPaneRuleEditor = new JScrollPane();
        panelRuleEditor.add(scrollPaneRuleEditor, BorderLayout.CENTER);

        txtPnRuleText = new JTextPane();
        txtPnRuleText.setToolTipText("Enter FaMa Rules");
        scrollPaneRuleEditor.setViewportView(txtPnRuleText);

        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel.setBounds(486, 11, 308, 285);
        frame.getContentPane().add(panel);
        panel.setLayout(null);

        JLabel lblOperations = new JLabel("Operations");
        lblOperations.setBounds(19, 22, 69, 16);
        panel.add(lblOperations);

        comboBoxOperation = new JComboBox(options);
        comboBoxOperation.setBounds(20, 40, 265, 30);
        panel.add(comboBoxOperation);

        JButton btnCheck = new JButton("Check");
        btnCheck.setEnabled(false);

        btnCheck.addActionListener(e -> {
            String selectedOperation = comboBoxOperation.getSelectedItem().toString();
            dtrpnOutput.setText(famaOperation.getOperationOutput(selectedOperation));
        });

        btnCheck.setBounds(168, 82, 117, 29);
        panel.add(btnCheck);

        JButton btnSelectFile = new JButton("Load File");
        btnSelectFile.setBounds(6, 297, 127, 29);
        frame.getContentPane().add(btnSelectFile);

        JButton btnLoadRule = new JButton("Load Rule");
        btnLoadRule.addActionListener(e -> {
            if (txtPnRuleText.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "There is no rule available, please write or load one!");
            } else {
                writeRuleToDefaultFile();
                initializeFama();
                btnCheck.setEnabled(true);
            }
        });
        btnLoadRule.setBounds(132, 297, 117, 29);
        frame.getContentPane().add(btnLoadRule);

        JPanel panelOutput = new JPanel();
        panelOutput.setBorder(new TitledBorder(null, "Output", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panelOutput.setBounds(6, 327, 788, 245);
        frame.getContentPane().add(panelOutput);
        panelOutput.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPaneOutput = new JScrollPane();
        panelOutput.add(scrollPaneOutput, BorderLayout.CENTER);
        
        dtrpnOutput = new JEditorPane();
        dtrpnOutput.setEditable(false);
        scrollPaneOutput.setViewportView(dtrpnOutput);
        btnSelectFile.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            jfc.setDialogTitle("Select FaMa File");
            jfc.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("*.fm", "fm");
            jfc.addChoosableFileFilter(filter);

            int returnValue = jfc.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                String filePath = jfc.getSelectedFile().getPath();
                putRuleToTextPane(filePath);
            }
        });
    }

    private void initializeFama() {
        famaOperation = null;
        try {
            famaOperation = new FamaOperation("tmp/default.fm");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    private void putRuleToTextPane(String filePath) {
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            InputStreamReader reader = new InputStreamReader(fileInputStream, "UTF-8");

            int character;

            StringBuilder sb = new StringBuilder();

            while ((character = reader.read()) != -1) {
                sb.append((char) character);
            }
            reader.close();
            txtPnRuleText.setText(sb.toString());

        } catch (IOException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    private void writeRuleToDefaultFile() {
        try {
            File tmpDirectory = new File("tmp");
            if (!tmpDirectory.exists())
                tmpDirectory.mkdir();
            FileOutputStream outputStream = new FileOutputStream("tmp/default.fm");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write(txtPnRuleText.getText());
            bufferedWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
