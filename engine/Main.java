package engine;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import listeners.ListListener;

public class Main implements TreeSelectionListener, Runnable {

	private HTMLDocReader reader;

	public static JTabbedPane tabbedPane;
	public JScrollPane elementList;
	public static JScrollPane elementAttributes;
	private static WebView webView;
	private static WebEngine webEngine;
	private final JFXPanel fxPanel = new JFXPanel();

	private DefaultMutableTreeNode root;
	// private DefaultMutableTreeNode model;
	private DefaultTreeModel treeModel;

	private JTree tree;
	private JTree elementTree;

	private DefaultMutableTreeNode parent = null;
	private DefaultMutableTreeNode child = null;
	private DefaultMutableTreeNode nextChild = null;
	private DefaultMutableTreeNode top;

	public static String rootFolder = System.getProperty("user.dir") + "/HTML";
	public static String pageURL = rootFolder + "/index.html";
	public static String cssURL = rootFolder + "/css/style.css";
	public static String jsURL = rootFolder + "/js/script.js";

	static String fileName = "";
	static String filePath = "";
	static String fileType = "";

	public static JTextArea textArea;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Main());
		System.out.println("ROOT DIRECTORY: " + System.getProperty("user.dir"));
		System.out.println("READING PAGE URL: " + pageURL);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		JFrame frame = new JFrame("HTMLEdit");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		reader = new HTMLDocReader(pageURL);

		// MIDDLE

		// HTML RAW TEXT VIEWER
		textArea = new JTextArea(20, 200);
		textArea.setEditable(false);
		JScrollPane tScrollPane = new JScrollPane(textArea);
		textArea.setText(reader.doc.toString());

		tabbedPane = new JTabbedPane();

		JComponent panel1 = tScrollPane;
		tabbedPane.addTab("HTML", null, panel1, "View HTML Document");

		createTabs();

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
		menuItem = new JMenuItem("Open");
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
		File fileRoot = new File(rootFolder);
		root = new DefaultMutableTreeNode(new FileNode(fileRoot));
		treeModel = new DefaultTreeModel(root);

		// DIRECTORY TREE
		tree = new JTree(treeModel);
		tree.setShowsRootHandles(true);
		tree.addTreeSelectionListener(this);
		JScrollPane scrollPane = new JScrollPane(tree);

		// ELEMENTS TREE
		top = new DefaultMutableTreeNode("Document");
		createNodes(top);
		elementTree = new JTree(top);
		elementTree.addTreeSelectionListener(new ListListener(reader));

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
		File f = new File(url);
		webEngine.load(f.toURI().toString());
	}

	private static void updateFX(String url) {
		System.out.println(url);
		File f = new File(url);
		Main.webEngine.load(f.toURI().toString());
		webView.setPrefSize(tabbedPane.getSize().width, tabbedPane.getSize().height);
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

	private void createTabs() {

		Elements links = HTMLDocReader.headElements.select("link");
		for (int i = 0; i < links.size(); i++) {
			JTextArea jT;
			try {
				jT = new JTextArea(reader.readLinkDoc(links.get(i).attr("href")));
				JScrollPane scrollPane = new JScrollPane(jT);
				JComponent c = scrollPane;
				tabbedPane.addTab(links.get(i).attr("href"), null, c, "CSS");
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

	public class CreateChildNodes implements Runnable {

		private DefaultMutableTreeNode root;

		private File fileRoot;

		public CreateChildNodes(File fileRoot, DefaultMutableTreeNode root) {
			this.fileRoot = fileRoot;
			this.root = root;
		}

		@Override
		public void run() {
			createChildren(fileRoot, root);
		}

		private void createChildren(File fileRoot, DefaultMutableTreeNode node) {
			File[] files = fileRoot.listFiles();
			if (files == null)
				return;

			for (File file : files) {
				DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new FileNode(file));
				node.add(childNode);
				if (file.isDirectory()) {
					createChildren(file, childNode);
				}
			}
		}

	}

	public class FileNode {

		private File file;

		public FileNode(File file) {
			this.file = file;
		}

		@Override
		public String toString() {
			String name = file.getName();
			if (name.equals("")) {
				return file.getAbsolutePath();
			} else {
				return name;
			}
		}

		protected JComponent makeTextPanel(String text) {
			JPanel panel = new JPanel(false);
			JLabel filler = new JLabel(text);
			filler.setHorizontalAlignment(JLabel.CENTER);
			panel.setLayout(new GridLayout(1, 1));
			panel.add(filler);
			return panel;
		}

	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
		// Object nodeInfo = node.getUserObject();
		if (!node.isLeaf()) {
			System.out.println("ignoring valueChanged from directory tree");
			return;
		}
		String p = e.getPath().toString();
		p = p.substring(p.indexOf("[") + 1);
		p = p.substring(0, p.indexOf("]"));

		List<String> file = Arrays.asList(p.split("\\s*,\\s*"));

		fileName = file.get(file.size() - 1);
		filePath = rootFolder + "/";
		fileType = "." + fileName.substring(fileName.indexOf(".") + 1);

		for (int i = 1; i < file.size(); i++) {
			filePath += file.get(i) + "/";
		}
		// System.out.println(fileName + " PATH: " + filePath + " FILETYPE OF: " +
		// fileType);

		while (tabbedPane.getTabCount() > 1) {
			tabbedPane.removeTabAt(1);
		}
		// HTML RAW TEXT VIEWER
		JScrollPane tScrollPane = new JScrollPane(textArea);
		// textArea.setText(reader.doc.toString());

		JComponent panel2 = tScrollPane;
		tabbedPane.addTab("HTML", null, panel2, "View HTML Document");

		createTabs();

		try {
			reader.readDoc(filePath);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		if (fileType.equals(".html")) {
			textArea.setText(reader.doc.toString());
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
				updateFX(filePath);
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