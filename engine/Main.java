package engine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sun.glass.events.KeyEvent;

import dialogs.NewElementDialog;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import listeners.ElementHighlightingListener;
import listeners.ListListener;
import listeners.OpenFileListener;
import listeners.OpenFolderListener;

public class Main extends Thread implements TreeSelectionListener, Runnable {

	public final static JFrame frame = new JFrame("HTMLEdit");

	private static String projectName;
	private static String createProjectFolder, createProjectStart;

	private static HTMLDocReader reader;
	private FileSaver tempFileSaver;
	public static JTabbedPane tabbedPane;
	public JScrollPane elementList;
	public static JScrollPane elementAttributes;
	private JPanel buttonPanelMain, buttonPanelLeft, buttonPanelMiddle, buttonPanelRight;
	public static WebView webView;
	private static WebEngine webEngine;
	private final JFXPanel fxPanel = new JFXPanel();

	public static File fileRoot;
	public static DefaultMutableTreeNode root;
	// private DefaultMutableTreeNode model;
	private DefaultTreeModel treeModel;

	public static JTree tree;
	public static JTree elementTree;

	private static DefaultMutableTreeNode parent = null;
	private static DefaultMutableTreeNode child = null;
	private static DefaultMutableTreeNode nextChild = null;
	private static DefaultMutableTreeNode top;

	public static String tempDir;
	public static String rootFolder;
	public static String pageURL;
	public static String tempPageURL;
	private static String tempCSSURL = "webViewCSS/webViewHighlighter.css";
	public static String tempCSSURLAbsolute;

	public static String fileName = "";
	public static String filePath = "";
	public static String fileType = "";

	public static JTextArea textArea;

	public static void main(String[] args) {
		loadWorkDirectories();
		System.out.println("ROOT DIRECTORY: " + System.getProperty("user.dir"));
		System.out.println("READING PAGE URL: " + pageURL);
	}

	public static void loadWorkDirectories() {
		// LOAD PROGRAM PROPERTIES.
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream("config.properties");
			// load a properties file
			prop.load(input);
			// get the property value and print it out
			rootFolder = prop.getProperty("rootFolder");
			// if config finds a root folder but for some reason it does not exist, create a
			// new one
			// ie if the project folder has been manually deleted by the user
			if (rootFolder == null || rootFolder.equals("") || !new File(rootFolder).exists()) {
				createProjectfolder();
			}
		} catch (IOException ex) {
			// IF NO PROPERTIES FOUND, MAKE USER SELECT A HOME FOLDER
			// THEN WRITES A PROPERTIES FILE TO SAVE PROPERTIES
			createProjectfolder();

		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		// scan for index.html or equivalent in root folder
		File folder = new File(rootFolder);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				if (listOfFiles[i].getName().equals("index.html") || listOfFiles[i].getName().equals("home.html")
						|| listOfFiles[i].getName().equals("start.html")) {
					pageURL = rootFolder + "\\" + listOfFiles[i].getName();

				}
			} else if (listOfFiles[i].isDirectory()) {

			}
		}
		SwingUtilities.invokeLater(new Main());
	}

	private static void createProjectfolder() {

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);

		String programLocation = System.getProperty("user.dir");
		String defaultProjectFolder = "\\HTMLEdit";
		projectName = "myProject";
		if (programLocation.substring(programLocation.length() - 1).equals("\\")) {
			programLocation = programLocation.substring(programLocation.length() - 1);
		}
		createProjectFolder = programLocation + defaultProjectFolder + "\\" + projectName;
		createProjectStart = programLocation + defaultProjectFolder;
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		// JOptionPane optionPane = new JOptionPane(
		// "No project folder found. \nPlease select a project folder to use. \nConfirm
		// to set and create a project folder.",
		// JOptionPane.INFORMATION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION);
		final JDialog dialog = new JDialog(frame, "Create a New Project", true);
		JLabel projectPath = new JLabel();

		projectPath.setText("A project folder will be created in \n\n" + createProjectStart + "\\" + projectName);

		JPanel projectNamePanel = new JPanel(new BorderLayout());
		JPanel buttonPanel = new JPanel();

		FlowLayout layout = new FlowLayout(FlowLayout.CENTER, 20, 10);
		buttonPanel.setLayout(layout);

		JButton confirmButton = new JButton("Confirm");
		JButton browseButton = new JButton("Browse");
		JButton cancelButton = new JButton("Cancel");

		buttonPanel.add(confirmButton);
		buttonPanel.add(browseButton);
		buttonPanel.add(cancelButton);

		JLabel projectNameLabel = new JLabel();
		JTextField projectNameTextField = new JTextField();

		projectNameLabel.setText("Project Name: ");
		projectNameTextField.setText(projectName);

		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (projectNameTextField.getText().equals("")) {
					JOptionPane.showMessageDialog(frame, "Please enter a project name.");
					return;
				}

				// write root folder to properties
				if (!Files.exists(new File(createProjectFolder).toPath())) {
					try {
						dialog.dispose();
						File directory = new File(createProjectFolder);

						// have to create parent dir first. Otherwise will result in
						// NoSuchFileException,
						// Because it is tring to create a directory in a directory that does not exist.
						Files.createDirectories(directory.toPath().getParent());
						Files.createDirectory(directory.toPath());

						// index.html
						Files.createFile(new File(directory + "\\index.html").toPath());
						BufferedWriter bw = new BufferedWriter(new FileWriter(directory + "\\index.html"));
						bw.write("<!DOCTYPE html>\n" + "<html>" + "\t<head>\n" + "<title>"
								+ projectNameTextField.getText() + "</title>\n"
								+ "\t<link rel=\"stylesheet\" href=\"css/style.css\">\n"
								+ "\t<script type=\"text/javascript\" src=\"js/script.js\"\n></script>" + "\t</head>\n"
								+ "\t<body>\n" + "\t\t<h1>" + projectNameTextField.getText() + "</h1>\n"
								+ "\t\t<h3>Hello World!</h3>\n" + "\t<body>\n" + "</html>");
						bw.close();

						// css/style.css
						Path cssDir = Paths.get(directory + "\\css");
						Path stylesheet = Paths.get(cssDir.toString() + "\\style.css");
						Files.createDirectory(cssDir);
						Files.createFile(stylesheet);

						BufferedWriter bw2 = new BufferedWriter(new FileWriter(cssDir + "\\style.css"));
						bw2.write(
								"h1{\n" + "\tcolor:red;\n" + "\tfont-size: 32px;\n" + "\ttext-align: center;\n" + "}");
						bw2.close();

						// js/script.js
						Path jsDir = Paths.get(directory + "\\javascript");
						Path script = Paths.get(jsDir.toString() + "\\script.js");
						Files.createDirectory(jsDir);
						Files.createFile(script);

						BufferedWriter bw3 = new BufferedWriter(new FileWriter(jsDir + "\\script.js"));
						bw3.write("<!DOCTYPE html>\n" + "<html>" + "\t<head>\n" + "<title>"
								+ projectNameTextField.getText() + "</title>\n"
								+ "\t<link rel=\"stylesheet\" href=\"css/style.css\">\n"
								+ "\t<script type=\"text/javascript\" src=\"js/script.js\"\n" + "\t</head>\n"
								+ "\t<body>\n" + "\t\t<h1>" + projectNameTextField.getText() + "</h1>\n"
								+ "\t\t<h3>Hello World!</h3>\n" + "\t<body>\n" + "</html>");
						bw3.close();

					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					System.out.println("Project with that name already exists");
					JOptionPane.showMessageDialog(frame, "A project with that name already exists.");
					return;
					/*
					 * TO DO: CREATE POPUP MESSAGE FOR: ERROR: A PROJECT WITH THAT NAME ALREADY
					 * EXISTS. OPTIONS TO USE THIS FOLDER, OR CANCEL AND CHHOSE A NEW FOLDER.
					 * 
					 */

				}
				rootFolder = createProjectFolder;
				Properties outProp = new Properties();
				OutputStream output = null;
				try {
					output = new FileOutputStream("config.properties");
					// set the properties value
					outProp.setProperty("rootFolder", rootFolder);
					// save properties to project root folder
					outProp.store(output, null);
				} catch (IOException io) {
					io.printStackTrace();
				} finally {
					if (output != null) {
						try {
							output.close();
						} catch (IOException e2) {
							e2.printStackTrace();
						}
					}
				}
				try {
					FileHandler.deleteFolder(new File(Main.tempDir));
				} catch (Exception ioE) {
					System.out.println("Cannot delete temp files: Temp files do not exist");
				}
				setWorkDirectories(rootFolder);
				try {
					reader.copyToTempFile();
				} catch (Exception e1) {
				}
				updateFrame();
			}
		});

		browseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Browse");
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
				int returnVal = fc.showOpenDialog(fc);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					String gotFile = fc.getSelectedFile().toString();
					System.out.println(gotFile);
					if (gotFile.substring(gotFile.length() - 1).equals("\\")) {
						gotFile = gotFile.substring(0, gotFile.length() - 1);
					}
					createProjectFolder = gotFile + "\\" + projectName;
					createProjectStart = createProjectFolder;
					projectPath.setText("A project folder will be created in \n\n" + createProjectFolder);
				} else {
					dialog.setVisible(true);

				}
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});

		// projectName listener to set correct build paths
		projectNameTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				update();
			}

			private void update() {
				projectName = projectNameTextField.getText();
				createProjectFolder = "";
				createProjectFolder = createProjectStart + "\\" + projectName;
				projectPath.setText("A project folder will be created in \n\n" + createProjectFolder);
			}

		});
		projectNamePanel.add(projectNameLabel, BorderLayout.NORTH);
		projectNamePanel.add(projectNameTextField, BorderLayout.CENTER);
		mainPanel.add(projectNamePanel, BorderLayout.NORTH);
		mainPanel.add(projectPath, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		projectPath.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		// optionPane.setOptions(options);
		// get user screen size in order to center the dialog
		dialog.setContentPane(mainPanel);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});

		dialog.setSize(700, 150);
		// center the dialog on screen
		dialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - dialog.getWidth() / 2,
				(Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - dialog.getHeight() / 2);
		dialog.setVisible(true);

	}

	public static void setWorkDirectories(String root) {

		rootFolder = root;

		// scan for index.html or equivalent in root folder
		File folder = new File(rootFolder);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				if (listOfFiles[i].getName().equals("index.html") || listOfFiles[i].getName().equals("home.html")
						|| listOfFiles[i].getName().equals("start.html")) {
					pageURL = rootFolder + "\\" + listOfFiles[i].getName();

				}
			} else if (listOfFiles[i].isDirectory()) {
			}
		}

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		// JFrame frame = new JFrame("HTMLEdit");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		try {
			reader = new HTMLDocReader(pageURL);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// File was not found.
		}
		tempFileSaver = new FileSaver(reader);
		// MIDDLE

		// HTML RAW TEXT VIEWER
		textArea = new JTextArea(20, 200);
		textArea.setEditable(false);
		JScrollPane tScrollPane = new JScrollPane(textArea);
		try {
			Elements links = reader.tempDoc.head().select("[href=\"webViewCSS/webViewHighlighter.css\"]");
			if (!links.isEmpty()) {
				for (Element el : links) {
					el.remove();
				}
			} else {

			}
			textArea.setText(reader.tempDoc.toString());
			reader.tempDoc.select("head")
					.append("<link rel=\"stylesheet\" href=\"webViewCSS/webViewHighlighter.css\">");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// File was not found.

		}

		tabbedPane = new JTabbedPane();

		JComponent panel1 = tScrollPane;
		tabbedPane.addTab("HTML", null, panel1, "View HTML Document");

		try {
			createTabs();
		} catch (Exception e) {
			// TODO Auto-generated catch block

		}

		// MENU
		JMenuBar menuBar;
		JMenu menu, submenu;
		JMenuItem menuItem;
		// Create menu bar
		menuBar = new JMenuBar();
		menu = new JMenu("File");
		menu.getAccessibleContext().setAccessibleDescription("File operations");
		menuBar.add(menu);
		menuBar.add(Box.createRigidArea(new Dimension(5, 2)));

		// File Menu items
		menu.addSeparator();
		submenu = new JMenu("New");

		menuItem = new JMenuItem("Project");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createProjectfolder();
			}
		});
		menuItem.setPreferredSize(new Dimension(100, 20));
		submenu.add(menuItem);
		submenu.addSeparator();
		menuItem = new JMenuItem("File");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		menuItem.setPreferredSize(new Dimension(100, 20));
		submenu.add(menuItem);

		menuItem = new JMenuItem("Folder");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});

		menuItem.setPreferredSize(new Dimension(100, 20));
		submenu.add(menuItem);

		menu.add(submenu);
		menuItem = new JMenuItem("Open File");
		menuItem.addActionListener(new OpenFileListener(reader));
		menu.add(menuItem);
		menuItem = new JMenuItem("Open Folder");
		menuItem.addActionListener(new OpenFolderListener(reader));
		menu.add(menuItem);

		// Window Menu
		menu = new JMenu("Window");
		menu.getAccessibleContext().setAccessibleDescription("Settings and Preferences");
		menuBar.add(menu);
		// Edit Menu items
		menu.addSeparator();
		menuItem = new JMenuItem("Preferences");
		menu.add(menuItem);
		frame.setJMenuBar(menuBar);

		// TOP BUTTONS PANEL
		buttonPanelMain = new JPanel(new BorderLayout());

		FlowLayout layout = new FlowLayout(FlowLayout.LEADING, 20, 15);

		buttonPanelLeft = new JPanel();
		buttonPanelMiddle = new JPanel();
		buttonPanelRight = new JPanel();

		buttonPanelMain.add(buttonPanelLeft, BorderLayout.LINE_START);
		buttonPanelMain.add(buttonPanelMiddle, BorderLayout.CENTER);
		buttonPanelMain.add(buttonPanelRight, BorderLayout.LINE_END);

		buttonPanelLeft.setLayout(layout);
		buttonPanelMiddle.setLayout(layout);
		buttonPanelRight.setLayout(layout);
		// buttonPanelLeft.setPreferredSize(new Dimension(0, 50));

		createButtons();

		// LEFT
		try {
			fileRoot = new File(tempDir);
		} catch (Exception e1) {

		}
		root = new DefaultMutableTreeNode(new FileNode(fileRoot));
		root.setUserObject("Root");
		treeModel = new DefaultTreeModel(root);

		// DIRECTORY TREE
		tree = new JTree(treeModel);
		tree.setShowsRootHandles(true);
		tree.addTreeSelectionListener(this);
		JScrollPane scrollPane = new JScrollPane(tree);

		// ELEMENTS TREE
		top = new DefaultMutableTreeNode("Document");
		try {
			createNodes(top);
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		elementTree = new JTree(top);
		elementTree.addTreeSelectionListener(new ListListener(reader));
		elementTree.addTreeSelectionListener(new ElementHighlightingListener(reader));
		elementTree.addMouseListener(new ElementTreeMouseListener(reader));

		// RIGHT
		elementList = new JScrollPane(elementTree);
		elementAttributes = new JScrollPane();
		elementAttributes.getVerticalScrollBar().setUnitIncrement(20);

		JSplitPane elementSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, elementList, elementAttributes);
		elementSplitPane.setDividerLocation(500);
		elementSplitPane.setPreferredSize(new Dimension(400, 0));

		JPanel mainPane = new JPanel(new BorderLayout());
		scrollPane.setPreferredSize(new Dimension(200, 0));

		mainPane.add(buttonPanelMain, BorderLayout.PAGE_START);
		mainPane.add(scrollPane, BorderLayout.LINE_START);
		mainPane.add(tabbedPane, BorderLayout.CENTER);
		mainPane.add(elementSplitPane, BorderLayout.LINE_END);

		textArea.setCaretPosition(0);

		frame.getContentPane().add(mainPane);
		frame.setMinimumSize(new Dimension(1200, 800));
		frame.pack();
		// frame.setLocationByPlatform(true);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);

		Platform.runLater(new Runnable() { // this will run initFX as JavaFX-Thread
			@Override
			public void run() {
				initFX(fxPanel, pageURL);
			}
		});

		JComponent browserPanel = fxPanel;
		tabbedPane.insertTab("Preview", null, browserPanel, "Preview HTML", 0);
		tabbedPane.setSelectedIndex(0);

		// frame.dispose();
		CreateChildNodes ccn = new CreateChildNodes(fileRoot, root);
		new Thread(ccn).start();

		frame.addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(ComponentEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void componentMoved(ComponentEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void componentResized(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				try {
					webView.setPrefSize(tabbedPane.getSize().width, tabbedPane.getSize().height);
				} catch (NullPointerException e) {

				}

			}

			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub

			}

		});
		if (tempFileSaver == null) {
			tempFileSaver = new FileSaver(reader);
		}
		Object[] options = { "Save & exit", "Exit Without Saving", "Cancel" };
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				if (FileSaver.unsavedChanges) {
					int result = JOptionPane.showOptionDialog(frame,
							"You have unsaved changes. Are you sure you want to exit?", "Unsaved Changes",
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
					if (result == JOptionPane.YES_OPTION) {
						tempFileSaver.save();
						try {
							FileHandler.deleteFolder(new File(Main.tempDir));
						} catch (Exception e) {
							System.out.println("Cannot delete temp files: Temp files do not exist");
						}
						System.exit(0);
					}
					if (result == JOptionPane.NO_OPTION) {
						try {
							FileHandler.deleteFolder(new File(Main.tempDir));
						} catch (Exception e) {
							System.out.println("Cannot delete temp files: Temp files do not exist");
						}
						System.exit(0);
					}
					if (result == JOptionPane.CANCEL_OPTION) {

					}
				} else {
					try {
						FileHandler.deleteFolder(new File(Main.tempDir));
					} catch (Exception e) {
						System.out.println("Cannot delete temp files: Temp files do not exist");
					}
					System.exit(0);
				}
			}
		});

		Action createNewProjectAction = new AbstractAction("CreateNewProject") {

			private static final long serialVersionUID = -1448980122018647815L;

			@Override
			public void actionPerformed(ActionEvent e) {
				createProjectfolder();
			}
		};

		Action saveAction = new AbstractAction("Save") {

			private static final long serialVersionUID = 5997018196165864216L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				tempFileSaver.save();
			}

		};

		// ctrl+s keybind
		KeyStroke saveKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);
		KeyStroke createNewPtojectKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
		// register action in ActionMap
		mainPane.getActionMap().put("Save", saveAction);
		mainPane.getActionMap().put("CreateNewProject", createNewProjectAction);
		mainPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(saveKeyStroke, "Save");
		mainPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(createNewPtojectKeyStroke,
				"CreateNewProject");

		updateFrame();
	}

	private static void initFX(final JFXPanel fxPanel, String url) {
		Group group = new Group();
		Scene scene = new Scene(group);
		fxPanel.setScene(scene);
		Main.webView = new WebView();
		webView.isResizable();
		group.getChildren().add(webView);
		webView.setPrefSize(tabbedPane.getSize().width, tabbedPane.getSize().height);

		// Obtain the webEngine to navigate
		Main.webEngine = webView.getEngine();
		try {
			File f = new File(url);
			webEngine.load(f.toURI().toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block

		}
	}

	public static void updateFX(String url) {
		File f;
		if (url == null || url.equals("") || url.equals(null)) {
			Main.webEngine.load("htt://www.google.com");
		} else {
			f = new File(url);
			try {
				Main.webEngine.load(f.toURI().toString());
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				return;
			}
		}

	}

	// For creating nodes, the variable int index will number the elements in order.
	// This is used to later direct commands to the correct html element via
	// BodyElementInfo.
	private static void createNodes(DefaultMutableTreeNode top) {

		parent = new DefaultMutableTreeNode("Head");
		top.add(parent);

		for (int i = 1; i < HTMLDocReader.headElements.size(); i++) {
			child = new DefaultMutableTreeNode(
					new HeadElementInfo(reader.tempDoc.head().select("*").get(i).nodeName(), i));
			parent.add(child);
		}

		parent = new DefaultMutableTreeNode("Body");
		for (int i = 1; i < reader.tempDoc.body().select("*").size(); i++) {

			if (reader.tempDoc.body().select("*").get(i).nodeName().equals("div")) {
				Element element = HTMLDocReader.bodyElements.get(i);
				i += createDivTree(parent, child, i, element);
			} else {
				child = new DefaultMutableTreeNode(
						new BodyElementInfo(reader.tempDoc.body().select("*").get(i).nodeName() + " "
								+ reader.tempDoc.body().select("*").get(i).id(), i));
				parent.add(child);
			}

		}
		top.add(parent);

	}

	// Creates tabbedPane tabs based on link and script elements found in doc
	private static void createTabs() {

		Elements links = reader.tempDoc.head().select("link");
		System.out.println(links);
		for (int i = 0; i < links.size(); i++) {
			JTextArea jT;
			try {
				// DO NOT ADD THE TEMP CSS FILE TO THE TABS
				if (links.get(i).attr("href").equals(tempCSSURL)) {

				} else {
					jT = new JTextArea();
					jT.setText(reader.readLinkDoc(links.get(i).attr("href")));
					JScrollPane scrollPane = new JScrollPane(jT);
					JComponent c = scrollPane;
					tabbedPane.addTab(links.get(i).attr("href"), null, c, "CSS");
				}

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		Elements scripts = reader.tempDoc.select("script");
		for (int j = 0; j < scripts.size(); j++) {
			JTextArea jT;
			try {
				jT = new JTextArea(reader.readLinkDoc(links.get(j).attr("src")));
				JScrollPane scrollPane = new JScrollPane(jT);
				JComponent c = scrollPane;
				tabbedPane.addTab("Script", null, c, "Script");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	private void createButtons() {

		Thread t = new Thread() {
			public void run() {
				// Load and set button images
				// Create functionality buttons
				Image smolIcon;

				// LEFT
				JButton newProjectButton = new JButton();
				JButton saveButton = new JButton();

				// MIDDLE
				JButton newFileButton = new JButton("");
				JButton newFolderButton = new JButton();
				
				// RIGHT
				JButton newElementButton = new JButton("New HTML Element");

				int smolIconWidth = 22;
				int smolIconHeight = 22;

				/*
				 * TO DO:: Currently if one resource is not found, anything following it will
				 * not be loaded. Separate into separate try/catches so loading will not stop if
				 * an error is encountered
				 * 
				 */
				try {

					// LEFT
					Image newProjectIcon = ImageIO.read(getClass().getResource("/res/newProjectIcon.png"));
					Image saveButtonIcon = ImageIO.read(getClass().getResource("/res/saveButtonIcon.png"));


					// MIDDLE
					Image newFolderIcon = ImageIO.read(getClass().getResource("/res/newFolderIcon.jpg"));
					Image newFileIcon = ImageIO.read(getClass().getResource("/res/newFileIcon.jpg"));
					
					// RIGHT
					Image newElementIcon = ImageIO.read(getClass().getResource("/res/newElementIcon.png"));

					// LEFT
					smolIcon = newProjectIcon.getScaledInstance(smolIconWidth, smolIconHeight,
							java.awt.Image.SCALE_SMOOTH);
					newProjectButton.setIcon(new ImageIcon(smolIcon));
					newProjectButton.setBorder(null);
					newProjectButton.setToolTipText("Create a new Project (Ctrl+shift+N");

					smolIcon = saveButtonIcon.getScaledInstance(smolIconWidth, smolIconHeight,
							java.awt.Image.SCALE_SMOOTH);
					saveButton.setIcon(new ImageIcon(smolIcon));
					saveButton.setBorder(null);
					saveButton.setToolTipText("Save (Ctrl+S)");

					// MIDDLE
					smolIcon = newFileIcon.getScaledInstance(smolIconWidth, smolIconHeight,
							java.awt.Image.SCALE_SMOOTH);
					newFolderButton.setIcon(new ImageIcon(smolIcon));
					newFolderButton.setBorder(null);
					newFolderButton.setOpaque(true);
					newFolderButton.setContentAreaFilled(false);
					newFolderButton.setBorderPainted(false);
					newFolderButton.setToolTipText("New File (Ctrl+N)");

					smolIcon = newFolderIcon.getScaledInstance(smolIconWidth, smolIconHeight,
							java.awt.Image.SCALE_SMOOTH);
					newFileButton.setIcon(new ImageIcon(smolIcon));
					newFileButton.setBorder(null);
					newFileButton.setOpaque(true);
					newFileButton.setContentAreaFilled(false);
					newFileButton.setBorderPainted(false);
					newFileButton.setToolTipText("New Folder (Ctrl+N)");
					
					// RIGHT
					smolIcon = newElementIcon.getScaledInstance(smolIconWidth, smolIconHeight,
							java.awt.Image.SCALE_SMOOTH);
					newElementButton.setIcon(new ImageIcon(smolIcon));
					// newElementButton.setBorder(null);
					newElementButton.setToolTipText("New HTML Element (Control + H)");

				} catch (IOException e) {
					System.out.println("Could not find a functionality button resource");
				}

				// LEFT
				buttonPanelLeft.add(newProjectButton);
				buttonPanelLeft.add(saveButton);

				// MIDDLE
				buttonPanelMiddle.add(newFolderButton);
				buttonPanelMiddle.add(newFileButton);
				
				// RIGHT
				buttonPanelRight.add(newElementButton);

				// LEFT LISTENERS
				newProjectButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						createProjectfolder();
					}
				});

				saveButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						tempFileSaver.save();
					}
				});


				// MIDDLE LISTENERS
				newFolderButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// new folder Creator method
					}
				});

				newFileButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// new file Creator method
					}
				});

				// RIGHT LISTENERS
				newElementButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						new NewElementDialog();
					}
				});

			}
		};
		t.start();
	}

	// if the doc cotains a dic, this will start mapping it our and creating nodes
	// for the element tree
	private static int createDivTree(DefaultMutableTreeNode parent, DefaultMutableTreeNode child, int index,
			Element element) {
		// int i = index;
		int skipAmount = 0;
		int secondSkipAmount = 0;
		Elements divElements = element.getAllElements();
		child = new DefaultMutableTreeNode(
				new BodyElementInfo(divElements.get(0).nodeName() + " " + divElements.get(0).id(), index));
		index++;
		parent.add(child);
		for (int j = 1; j < divElements.size(); j++) {
			if (divElements.get(j).nodeName().equals("div")) {
				nextChild = new DefaultMutableTreeNode(
						new BodyElementInfo(divElements.get(j).nodeName() + " " + divElements.get(j).id(), index));
				secondSkipAmount += getDivContent(child, nextChild, index, divElements.get(j));
				skipAmount += secondSkipAmount;
				j += secondSkipAmount;
				index += secondSkipAmount;
				child.add(nextChild);
			} else {
				child.add(new DefaultMutableTreeNode(new BodyElementInfo(divElements.get(j).nodeName(), index)));
				index++;
			}
			skipAmount += 1;
		}
		return skipAmount;
	}

	private static int getDivContent(DefaultMutableTreeNode child, DefaultMutableTreeNode nextChild, int index,
			Element div) {
		int skipAmount = 0;
		int secondSkipAmount = 0;
		Elements divElements = div.getAllElements();
		index++;
		for (int i = 1; i < divElements.size(); i++) {
			if (divElements.get(i).nodeName().equals("div")) {
				DefaultMutableTreeNode whatAmIDoingWithMyLife = new DefaultMutableTreeNode(
						new BodyElementInfo(divElements.get(i).nodeName() + " " + divElements.get(i).id(), index));
				secondSkipAmount += getDivContent(nextChild, whatAmIDoingWithMyLife, index, divElements.get(i));
				i += secondSkipAmount;
				index += secondSkipAmount;
				nextChild.add(whatAmIDoingWithMyLife);
			} else {
				nextChild.add(new DefaultMutableTreeNode(new BodyElementInfo(divElements.get(i).nodeName(), index)));
				index++;
			}
			skipAmount += 1;
		}
		return skipAmount + secondSkipAmount;
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
		// Object nodeInfo = node.getUserObject();
		if (!node.isLeaf()) {
			return;
		}
		String p = e.getPath().toString();
		p = p.substring(p.indexOf("[") + 1);
		p = p.substring(0, p.indexOf("]"));

		List<String> file = Arrays.asList(p.split("\\s*,\\s*"));

		fileName = file.get(file.size() - 1);
		filePath = tempDir + "/";
		fileType = "." + fileName.substring(fileName.indexOf(".") + 1);

		for (int i = 1; i < file.size(); i++) {
			filePath += file.get(i);
		}
		// System.out.println(fileName + " PATH: " + filePath + " FILETYPE OF: " +
		// fileType);
		updateFrame();
	}

	public static void updateFrame() {

		while (tabbedPane.getTabCount() > 1) {
			tabbedPane.removeTabAt(1);
		}

		if (reader == null) {
			reader = new HTMLDocReader(filePath);
		} else {
			try {
				reader.readDoc(filePath);
			} catch (IOException e1) {

			}
		}

		// HTML RAW TEXT VIEWER

		try {
			JScrollPane tScrollPane = new JScrollPane(textArea);
			textArea.setText(reader.tempDoc.toString());
			textArea.setCaretPosition(0);
			JComponent panel2 = tScrollPane;
			tabbedPane.addTab("HTML", null, panel2, "View HTML Document");
		} catch (Exception e2) {

		}

		try {
			createTabs();
		} catch (Exception e2) {
			reader = new HTMLDocReader(filePath);
			createTabs();
		}

		if (fileType.equals(".html")) {
			textArea.setText(reader.tempDoc.toString());
			tempPageURL = filePath;
		}
		if (fileType.equals(".css")) {
			try {
				textArea.setText(reader.readLinkDoc(filePath));
			} catch (IOException e1) {

			}
		}
		if (fileType.equals(".js")) {
			try {
				textArea.setText(reader.readLinkDoc(filePath));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		Platform.runLater(new Runnable() {
			public void run() {
				updateFX(tempPageURL);
			}
		});

		// RESET ELEMENTS TREE
		DefaultTreeModel model = (DefaultTreeModel) elementTree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		root.removeAllChildren();
		root.setUserObject("Root");
		createNodes(top);
		model.reload();

		DefaultMutableTreeNode currentNode = root.getNextNode();

		do {
			if (currentNode.getLevel() == 1) {
				elementTree.expandPath(new TreePath(currentNode.getParent()));
			}
			currentNode = currentNode.getNextNode();
		} while (currentNode != null);
	}
}