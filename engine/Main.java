package engine;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import listeners.ListListener;

public class Main implements TreeSelectionListener, ListSelectionListener, Runnable {
	public Main() {
		
	}

	private HTMLDocReader reader;

	public static JTabbedPane tabbedPane;
	public JScrollPane elementList;
	public static JScrollPane elementAttributes;
	private JList<String> allElementsList, headerElementsList, bodyElementsList, footerElementsList;

	
	private DefaultMutableTreeNode root;
	private DefaultMutableTreeNode model;
	private DefaultTreeModel treeModel;
	
	private JTree tree;
	private JTree elementTree;
	
	private int index = 0;
	private DefaultMutableTreeNode parent = null;
	private DefaultMutableTreeNode child = null;
	private DefaultMutableTreeNode nextChild = null;
	private DefaultListModel<String> allElementsModel, headerElementsModel, bodyElementsModel, footerElementsModel;
	DefaultMutableTreeNode top;

	public static String rootFolder = System.getProperty("user.dir")+"/HTML";
	public static String pageURL = rootFolder+"/index.html";
	public static String cssURL = rootFolder+"/css/style.css";
	public static String jsURL = rootFolder+"/js/script.js";

	private String[] documentObjects = { "Header", "Body", "Footer" };

	public ArrayList<String> html;

	private JEditorPane editorPane;
	private HTMLEditorKit editor;
	private JTextArea textArea, cssTextArea, jsTextArea;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Main());
		System.out.println("ROOT DIRECTORY: "+System.getProperty("user.dir"));
		System.out.println("READING PAGE URL: "+pageURL);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		// MIDDLE
		// editorPane
		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editor = new HTMLEditorKit();
		setEditorPaneDocument(pageURL);
		JScrollPane eScrollPane = new JScrollPane(editorPane);

		// HTML RAW TEXT VIEWER
		textArea = new JTextArea(20, 200);
		textArea.setEditable(false);
		JScrollPane tScrollPane = new JScrollPane(textArea);
		textArea.setText(getHTMLFromArrayList(html));

		tabbedPane = new JTabbedPane();
		JComponent panel1 = eScrollPane;
		tabbedPane.addTab("Preview", null, panel1, "Preview the page");

		JComponent panel2 = tScrollPane;
		tabbedPane.addTab("HTML", null, panel2, "View HTML Document");
		
		reader = new HTMLDocReader(pageURL);
		
		createTabs();
		
		JFrame frame = new JFrame("HTMLEdit");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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


		//ELEMENTS TREE
		top = new DefaultMutableTreeNode("Document");
		createNodes(top);
		elementTree = new JTree(top);
		elementTree.addTreeSelectionListener(new ListListener(reader));
		
		// RIGHT
		elementList = new JScrollPane(elementTree);
		elementAttributes = new JScrollPane();

		JSplitPane elementSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, elementList, elementAttributes);
		elementSplitPane.setDividerLocation(300);
		elementSplitPane.setPreferredSize(new Dimension(400, 0));

		JPanel mainPane = new JPanel(new BorderLayout());
		scrollPane.setPreferredSize(new Dimension(200, 0));

		mainPane.add(buttonPanel, BorderLayout.PAGE_START);
		mainPane.add(scrollPane, BorderLayout.LINE_START);
		mainPane.add(tabbedPane, BorderLayout.CENTER);
		mainPane.add(elementSplitPane, BorderLayout.LINE_END);

		editorPane.setCaretPosition(0);
		textArea.setCaretPosition(0);

		frame.getContentPane().add(mainPane);
		frame.setMinimumSize(new Dimension(1200, 800));
		// frame.setLocationByPlatform(true);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
//		frame.dispose();
		CreateChildNodes ccn = new CreateChildNodes(fileRoot, root);
		new Thread(ccn).start();
	}
	
	private void createNodes(DefaultMutableTreeNode top) {
		index = 0;

		List<DefaultMutableTreeNode> secondChild = new ArrayList<DefaultMutableTreeNode>();
		
		parent = new DefaultMutableTreeNode("Head");
		top.add(parent);
		
		for(int i=1; i<reader.headElements.size(); i++) {
			child = new DefaultMutableTreeNode(new HeadElementInfo(reader.headElements.get(i).nodeName(), i));
			parent.add(child);
		}
		
		parent = new DefaultMutableTreeNode("Body");
		for(int i=1; i<reader.bodyElements.size(); i++) {
			
			if(reader.bodyElements.get(i).nodeName().equals("div")) {
				Element element = reader.bodyElements.get(i);
				i+=createDivTree(parent, child, i, element);
			} else {
				child = new DefaultMutableTreeNode(new BodyElementInfo(reader.bodyElements.get(i).nodeName(), i, reader));
				parent.add(child);
			}
			
		}
		top.add(parent);

	}
	
	private int createDivTree(DefaultMutableTreeNode parent, DefaultMutableTreeNode child, int index, Element element) {
		int i = index;
		int skipAmount = 0;
		int secondSkipAmount = 0;
		Elements divElements = element.getAllElements();
//		System.out.println(divElements);
		child = new DefaultMutableTreeNode(new BodyElementInfo(divElements.get(0).nodeName(), i, reader));
		parent.add(child);
		for(int j=1; j<divElements.size(); j++) {
			if(divElements.get(j).nodeName().equals("div")) {
				nextChild = new DefaultMutableTreeNode(new BodyElementInfo(divElements.get(j).nodeName(), j, reader));
				secondSkipAmount+=getDivContent(child, nextChild, divElements.get(j));
				skipAmount+=secondSkipAmount;
				j+=secondSkipAmount;
				child.add(nextChild);
			} else {
				child.add(new DefaultMutableTreeNode(new BodyElementInfo(divElements.get(j).nodeName(), i, reader)));
			}
			skipAmount +=1;
		}
		return skipAmount;
	}
	
	private int getDivContent(DefaultMutableTreeNode child, DefaultMutableTreeNode nextChild, Element div) {
		int skipAmount = 0;
		int secondSkipAmount = 0;
		Elements divElements = div.getAllElements();
		for(int i=1; i<divElements.size(); i++) {
			if(divElements.get(i).nodeName().equals("div")) {
				DefaultMutableTreeNode whatAmIDoingWithMyLife = new DefaultMutableTreeNode(new BodyElementInfo(divElements.get(i).nodeName(), i, reader));
				secondSkipAmount+=getDivContent(nextChild, whatAmIDoingWithMyLife, divElements.get(i));
				i+=secondSkipAmount;
				nextChild.add(whatAmIDoingWithMyLife);
			} else {
				nextChild.add(new DefaultMutableTreeNode(divElements.get(i).nodeName()));
			}
			skipAmount+=1;
		}
		return skipAmount+secondSkipAmount;
	}
	
	private void createTabs() {

		Elements links = reader.headElements.select("link");
		for(int i=0; i<links.size(); i++) {
			JTextArea jT;
			try {
				jT = new JTextArea(reader.readLinkDoc(links.get(i).attr("href")));
				JScrollPane scrollPane = new JScrollPane(jT);
				JComponent c = scrollPane;
				tabbedPane.addTab(links.get(i).attr("href"), null, c, "CSS");
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
		}
		}
		Elements scripts = reader.headElements.select("script");
		for(int j=0; j<scripts.size(); j++) {
				JTextArea jT;
				try {
					jT = new JTextArea(reader.readLinkDoc(links.get(j).attr("src")));
					JScrollPane scrollPane = new JScrollPane(jT);
					JComponent c = scrollPane;
					tabbedPane.addTab("Script", null, c, "Script");
				}
				catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unused")
	public void setEditorPaneDocument(String pageURL) {
		try {
			// URL url = new File(pageURL).toURI().toURL();
			File file = new File(pageURL);
			URL styleURL = new File(cssURL).toURI().toURL();
			if (file != null) {
				editorPane.setEditorKit(editor);
				try {
					editor.getStyleSheet().importStyleSheet(new File(styleURL.getFile()).toURI().toURL());
				} catch (MalformedURLException ex) {
					System.err.println("Couldn't find file: " + file);
				}
				Document doc = editor.createDefaultDocument();
				editorPane.setDocument(doc);
				editorPane.setText(getHTMLFromURL(file.toURL()));
				// editorPane.setPage(url);

			} else {
				System.err.println("Couldn't find file: " + file);
			}
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public String getHTMLFromURL(URL url) {

		html = new ArrayList<String>();

		// read html file
		try (BufferedReader br = new BufferedReader(new FileReader(url.getFile()))) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				html.add(sCurrentLine);
				// System.out.println(sCurrentLine);
			}
		} catch (IOException e) {

		}

		// convert arraylist to string
		StringBuilder sb = new StringBuilder();
		for (String str : html) {
			sb.append(str);
			// sb.append("<br>");
		}
		// System.out.println(sb.toString());
		return sb.toString();
	}

	public String getHTMLFromArrayList(ArrayList<String> al) {
		// convert arraylist to string
		StringBuilder sb = new StringBuilder();
		sb.append(" ");
		for (String str : al) {
			sb.append(str);
			sb.append("\n");
		}

		// System.out.println(sb.toString());
		return sb.toString();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		JList<?> list = (JList<?>) e.getSource();
//		updateText(documentObjects[list.getSelectedIndex()]);
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
	
		protected void updateText(String name) {
	
			switch (name) {
			case "All":
				textArea.setText(getHTMLFromArrayList(html));
				removeListModelObjects();
				break;
			case "Header":
	
				break;
			case "Body":
	
				break;
			case "Footer":
	
				break;
			default:
				break;
			}
		}
	
		public void removeListModelObjects() {
			DefaultListModel<String> listModel = (DefaultListModel<String>) allElementsList.getModel();
			listModel.removeAllElements();
		}
	
		public void setListModelObjects() {
	
		}
	
		public void getListModelObjects() {
	
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
		String p = e.getPath().toString();
		p = p.substring(p.indexOf("[") + 1);
		p = p.substring(0, p.indexOf("]"));

		List<String> file = Arrays.asList(p.split("\\s*,\\s*"));

		//System.out.println("P STRING: " + p);

		String fileName = file.get(file.size() - 1);
		String filePath = rootFolder + "/";
		String fileType = "."+fileName.substring(fileName.indexOf(".") + 1);

		for (int i = 1; i < file.size(); i++) {
			filePath += file.get(i) + "/";
		}
//		System.out.println(fileName + " PATH: " + filePath + " FILETYPE OF: " + fileType);
		
		tabbedPane.removeAll();
		
		JScrollPane eScrollPane = new JScrollPane(editorPane);

		// HTML RAW TEXT VIEWER
		JScrollPane tScrollPane = new JScrollPane(textArea);
		textArea.setText(getHTMLFromArrayList(html));

		JComponent panel1 = eScrollPane;
		tabbedPane.addTab("Preview", null, panel1, "Preview the page");

		JComponent panel2 = tScrollPane;
		tabbedPane.addTab("HTML", null, panel2, "View HTML Document");
		
		createTabs();
		//resetRightSide panes

		if (fileType.equals(".html")) {
			setEditorPaneDocument(filePath);
			try {
				reader.readDoc(filePath);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			textArea.setText(getHTMLFromArrayList(html));
		}
		if (fileType.equals(".css")) {
			setEditorPaneDocument(filePath);
			try {
				cssTextArea.setText(getHTMLFromArrayList(html));
			} catch (NullPointerException e1) {
			
			}
		}
		if (fileType.equals(".js")) {
			setEditorPaneDocument(filePath);
			jsTextArea.setText(getHTMLFromArrayList(html));
		}

		//RESET ELEMENTS TREE
		DefaultTreeModel model = (DefaultTreeModel)elementTree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
		root.removeAllChildren();
		model.reload();
		createNodes(top);
		DefaultMutableTreeNode currentNode = root.getNextNode();

		do {
			System.out.println(currentNode.getLevel());
			if(currentNode.getLevel()==1) {
				elementTree.expandPath(new TreePath(currentNode.getParent()));
			}
				currentNode = currentNode.getNextNode();
		} while (currentNode != null);
	}
}