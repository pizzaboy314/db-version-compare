package classes;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;


public class Worker extends JPanel implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static JFrame resultFrame;
	
	private static JTextArea prodSourceVersions;
	private static JTextArea locSourceVersions;
	private static JTextArea resultVersions;
	private static JFileChooser fc;
	private static File resultFile;
	private static String resultString;
	
	public static void main(String[] args) {
		resultWindow();
		
		resultFrame.setVisible(true);
		resultFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

//		compareVersions();
	}
	
	public static void compareVersions() {
		String prodVersionString = prodSourceVersions.getText().trim().replace("\t", "").replace("\n", "").replace("\r\n", "");
		String locVersionString = locSourceVersions.getText().trim().replace("\t", "").replace("\n", "").replace("\r\n", "");
		List<String> prodVersionsList = Arrays.asList(prodVersionString.split(","));
		List<String> locVersionsList = Arrays.asList(locVersionString.split(","));
		
		StringBuilder result = new StringBuilder();
		String delimiter = "";
		for(String version : prodVersionsList) {
			if(!locVersionsList.contains(version)) {
				result.append(delimiter);
				delimiter = ", ";
				result.append(version);
			}
		}
		resultString = result.toString();
		resultVersions.setText(resultString);
	}
	
    public void actionPerformed(ActionEvent e) {
        // do things
    }
	
	public synchronized static void resultWindow() {
		resultFrame = new JFrame("DB Version Compare");
		File resultPath = new File(System.getProperty("user.dir"));
		fc = new JFileChooser(resultPath);

		FileFilter filter = new FileNameExtensionFilter("Text file (*.txt)", "txt");
		fc.addChoosableFileFilter(filter);
		fc.setFileFilter(filter);

		resultFrame.setBounds(0, 0, 1300, 650);

		prodSourceVersions = new JTextArea();
		prodSourceVersions.setText("production version list here");
		prodSourceVersions.setEditable(true);
		prodSourceVersions.setLineWrap(true);
		
		locSourceVersions = new JTextArea();
		locSourceVersions.setText("local version list here");
		locSourceVersions.setEditable(true);
		locSourceVersions.setLineWrap(true);
		
		resultVersions = new JTextArea();
		resultVersions.setText("results will show here");
		resultVersions.setEditable(false);
		resultVersions.setLineWrap(true);

		JButton saveFile = new JButton("Save Results");
		saveFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showSaveDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					String filepath = f.getAbsolutePath();
					String filename = f.getName();

					if (!filename.contains(".txt")) {
						resultFile = new File(filepath + ".txt");
					} else {
						resultFile = f;
					}

					try {
						Files.write(Paths.get(resultFile.getAbsolutePath()), resultString.getBytes());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		JButton gbutton = new JButton("Isolate Missing Versions");
		gbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				compareVersions();
			}
		});
		
		JPanel generateControls = new JPanel();
		generateControls.setLayout(new FlowLayout());
		generateControls.add(gbutton);
		
		JPanel textFields = new JPanel();
		textFields.setLayout(new FlowLayout());
		JScrollPane prodScrollPane = new JScrollPane(prodSourceVersions,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JScrollPane locScrollPane = new JScrollPane(locSourceVersions,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JScrollPane resultScrollPane = new JScrollPane(resultVersions,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		prodScrollPane.setPreferredSize(new Dimension(400,500));
		locScrollPane.setPreferredSize(new Dimension(400,500));
		resultScrollPane.setPreferredSize(new Dimension(400,500));
		textFields.add(prodScrollPane);
		textFields.add(locScrollPane);
		textFields.add(resultScrollPane);
		
		JPanel saveControls = new JPanel();
		saveControls.setLayout(new FlowLayout());
		saveControls.add(saveFile);

		resultFrame.getContentPane().add(textFields, BorderLayout.CENTER);
		resultFrame.getContentPane().add(saveControls, BorderLayout.SOUTH);
		resultFrame.getContentPane().add(generateControls, BorderLayout.NORTH);
	}

}