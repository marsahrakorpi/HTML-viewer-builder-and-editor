package engine;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sun.glass.events.KeyEvent;

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

public class Main implements TreeSelectionListener, Runnable {

	private HTMLDocReader reader;
	private TempFileSaver tempFileSaver;
	public static JTabbedPane tabbedPane;
	public JScrollPane elementList;
	public static JScrollPane elementAttributes;
	public static WebView webView;
	private static WebEngine webEngine;
	private final JFXPanel fxPanel = new JFXPanel();

	public static File fileRoot;
	public static DefaultMutableTreeNode root;
	// private DefaultMutableTreeNode model;
	private DefaultTreeModel treeModel;

	public static JTree tree;
	private JTree elementTree;

	private DefaultMutableTreeNode parent = null;
	private DefaultMutableTreeNode child = null;
	private DefaultMutableTreeNode nextChild = null;
	private DefaultMutableTreeNode top;

	// public static String rootFolder = System.getProperty("user.dir");
	public static String tempDir;
	public static String rootFolder;
	public static String pageURL;
	public static String tempPageURL;
	public static String tempCSSURL;
	public static String tempCSSURLAbsolute;
	// public static String cssURL = rootFolder + "";
	// public static String jsURL = rootFolder + "";
	// public static String rootFolder = System.getProperty("user.dir") + "/HTML";
	// public static String pageURL = rootFolder + "/index.html";
	// public static String cssURL = rootFolder + "/css/style.css";
	// public static String jsURL = rootFolder + "/js/script.js";

	public static String fileName = "";
	public static String filePath = "";
	public static String fileType = "";

	public static JTextArea textArea;

	public static void main(String[] args) {
		loadWorkDirectories();
		SwingUtilities.invokeLater(new Main());
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

		} catch (IOException ex) {
			// IF NO PROPERTIES FOUND, MAKE USER SELECT A HOME FOLDER
			// THEN WRITES A PROPERTIES FILE TO SAVE PROPERTIES
			final JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
			int returnVal = fc.showOpenDialog(fc);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				rootFolder = fc.getSelectedFile().toString();
				Properties outProp = new Properties();
				OutputStream output = null;
				try {
					output = new FileOutputStream("config.properties");
					// set the properties value
					outProp.setProperty("rootFolder", fc.getSelectedFile().toString());
					// save properties to project root folder
					outProp.store(output, null);
				} catch (IOException io) {
					io.printStackTrace();
				} finally {
					if (output != null) {
						try {
							output.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
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

		JFrame frame = new JFrame("HTMLEdit");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		try {
			reader = new HTMLDocReader(pageURL);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// File was not found.
		}

		// MIDDLE

		// HTML RAW TEXT VIEWER
		textArea = new JTextArea(20, 200);
		textArea.setEditable(false);
		JScrollPane tScrollPane = new JScrollPane(textArea);
		try {
			textArea.setText(reader.doc.toString());
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

		// TOP
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.setPreferredSize(new Dimension(0, 50));
		// LEFT
		fileRoot = new File(tempDir);
		root = new DefaultMutableTreeNode(new FileNode(fileRoot));
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

		// RIGHT
		elementList = new JScrollPane(elementTree);
		elementAttributes = new JScrollPane();
		elementAttributes.getVerticalScrollBar().setUnitIncrement(20);

		JSplitPane elementSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, elementList, elementAttributes);
		elementSplitPane.setDividerLocation(300);
		elementSplitPane.setPreferredSize(new Dimension(400, 0));

		JPanel mainPane = new JPanel(new BorderLayout());
		scrollPane.setPreferredSize(new Dimension(200, 0));

		mainPane.add(buttonPanel, BorderLayout.PAGE_START);
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
		tempFileSaver = new TempFileSaver(reader);
		Object[] options = { "Save & exit", "Do Not Save", "Cancel" };
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				if (TempFileSaver.unsavedChanges) {
					int result = JOptionPane.showOptionDialog(frame,
							"You have unsaved changes. Are you sure you want to exit?", "Unsaved Changes",
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
					if(result == JOptionPane.YES_OPTION) {
						tempFileSaver.save();
						try {
							FileUtils.forceDelete(new File(tempDir));
						} catch (IOException e) {

						}
						System.exit(0);
					}
					if(result == JOptionPane.NO_OPTION) {
						try {
							FileUtils.forceDelete(new File(tempDir));
						} catch (IOException e) {

						}
						System.exit(0);
					}
					if(result == JOptionPane.CANCEL_OPTION) {
						
					}
				} else {
					try {
						FileUtils.forceDelete(new File(tempDir));
					} catch (IOException e) {

					}
					System.exit(0);
				}
			}
		});

		Action saveAction = new AbstractAction("Save") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 5997018196165864216L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				tempFileSaver.save();
			}

		};

		// ctrl+s keybind
		KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);
		// register action in ActionMap
		mainPane.getActionMap().put("Save", saveAction);
		mainPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyStroke, "Save");

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
			Main.webEngine.load("");
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
	// This is used to later direct commands to the correct html element.
	private void createNodes(DefaultMutableTreeNode top) {

		parent = new DefaultMutableTreeNode("Head");
		top.add(parent);

		for (int i = 1; i < HTMLDocReader.headElements.size(); i++) {
			child = new DefaultMutableTreeNode(new HeadElementInfo(HTMLDocReader.headElements.get(i).nodeName(), i));
			parent.add(child);
		}

		parent = new DefaultMutableTreeNode("Body");
		for (int i = 1; i < HTMLDocReader.bodyElements.size(); i++) {

			if (HTMLDocReader.bodyElements.get(i).nodeName().equals("div")) {
				Element element = HTMLDocReader.bodyElements.get(i);
				i += createDivTree(parent, child, i, element);
			} else {
				child = new DefaultMutableTreeNode(new BodyElementInfo(
						HTMLDocReader.bodyElements.get(i).nodeName() + " " + HTMLDocReader.bodyElements.get(i).id(),
						i));
				parent.add(child);
			}

		}
		top.add(parent);

	}

	// Creates tabbedPane tabs based on link and script elements found in doc
	private void createTabs() {

		Elements links = HTMLDocReader.headElements.select("link");
		for (int i = 0; i < links.size(); i++) {
			JTextArea jT;
			try {
				// DO NOT ADD THE TEMP CSS FILE TO THE TABS
				if (links.get(i).attr("href").equals(tempCSSURL)) {

				} else {
					jT = new JTextArea(reader.readLinkDoc(links.get(i).attr("href")));
					JScrollPane scrollPane = new JScrollPane(jT);
					JComponent c = scrollPane;
					tabbedPane.addTab(links.get(i).attr("href"), null, c, "CSS");
				}

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		Elements scripts = HTMLDocReader.headElements.select("script");
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

	// if the doc cotains a dic, this will start mapping it our and creating nodes
	// for the element tree
	private int createDivTree(DefaultMutableTreeNode parent, DefaultMutableTreeNode child, int index, Element element) {
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

	private int getDivContent(DefaultMutableTreeNode child, DefaultMutableTreeNode nextChild, int index, Element div) {
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
		System.out.println(fileName + " PATH: " + filePath + " FILETYPE OF: " + fileType);

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
			textArea.setText(reader.doc.toString());
			JComponent panel2 = tScrollPane;
			tabbedPane.addTab("HTML", null, panel2, "View HTML Document");
		} catch (Exception e2) {

		}

		createTabs();

		if (fileType.equals(".html")) {
			textArea.setText(reader.doc.toString());
			pageURL = filePath;
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
		model.reload();
		createNodes(top);
		DefaultMutableTreeNode currentNode = root.getNextNode();

		do {
			if (currentNode.getLevel() == 1) {
				elementTree.expandPath(new TreePath(currentNode.getParent()));
			}
			currentNode = currentNode.getNextNode();
		} while (currentNode != null);
	}
}