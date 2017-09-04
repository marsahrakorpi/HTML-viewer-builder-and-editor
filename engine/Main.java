package engine;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Panel;
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

public class Main implements TreeSelectionListener,ListSelectionListener, Runnable{
	public Main() {
	}


	private static final long serialVersionUID = -1174190681269377671L;

	private HTMLDocReader reader;

	
	private JPanel contentPane;
	private Panel projectExplorer;
	private JList<String> list;
	private JList<String> subList;
	
    private DefaultMutableTreeNode root;
    private DefaultTreeModel treeModel;
    private JTree tree;
	
	private DefaultListModel<String> folderListModel;
	private DefaultListModel<String> fileListModel;
	private DefaultListModel<String> listModel;
	private DefaultListModel<String> subModel;
	
	public String rootFolder = "D:/JavaDev/Frame/HTML";
	public String pageURL = "D:/JavaDev/Frame/HTML/index.html";
	public String cssURL = "HTML/css/style.css";
	public String jsURL = "HTML/js/script.js";
	
	private String[] documentObjects = {"Header", "Body", "Footer"};
	
	public ArrayList<String> html;
	
	private JEditorPane editorPane;
	private HTMLEditorKit editor;
	private JTextArea textArea, cssTextArea, jsTextArea;
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Main());
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		reader = new HTMLDocReader(pageURL);
        JFrame frame = new JFrame("File Browser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //MENU
		JMenuBar menuBar;
		JMenu menu, submenu;
		JMenuItem menuItem;
		//Create menu bar
		menuBar = new JMenuBar();
		menu = new JMenu("File");
		menu.getAccessibleContext().setAccessibleDescription("File operations");
		menuBar.add(menu);
		menuBar.add(Box.createRigidArea(new Dimension(5,2)));
		//File Menu items
		menu.addSeparator();
		submenu = new JMenu("New");
		menuItem = new JMenuItem("Project");
		submenu.add(menuItem);
		menu.add(submenu);
		menuItem = new JMenuItem("Open");
		menu.add(menuItem);
		
		//Window Menu
		menu = new JMenu("Window");
		menu.getAccessibleContext().setAccessibleDescription("Settings and Preferences");
		menuBar.add(menu);
		//Edit Menu items
		menu.addSeparator();
		menuItem = new JMenuItem("Preferences");
		menu.add(menuItem);
		frame.setJMenuBar(menuBar);
        
        //TOP
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setPreferredSize(new Dimension(0,50));
        //LEFT
        File fileRoot = new File(rootFolder);
        root = new DefaultMutableTreeNode(new FileNode(fileRoot));
        treeModel = new DefaultTreeModel(root);

        //tree
        tree = new JTree(treeModel);
        tree.setShowsRootHandles(true);
        tree.addTreeSelectionListener(this);
        JScrollPane scrollPane = new JScrollPane(tree);
       
        //MIDDLE
        JScrollPane elementList = new JScrollPane();
        JScrollPane elementAttributes = new JScrollPane();
        
        JSplitPane elementSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, elementList, elementAttributes);
        elementSplitPane.setDividerLocation(400);
        elementSplitPane.setPreferredSize(new Dimension(400,0));
        
        //RIGHT
        //editorPane
		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editor = new HTMLEditorKit();
		setEditorPaneDocument(pageURL);
		JScrollPane eScrollPane = new JScrollPane(editorPane);
		
		//HTML RAW TEXT VIEWER
		textArea = new JTextArea(20, 200);
		textArea.setEditable(false);
		JScrollPane tScrollPane = new JScrollPane(textArea);
        textArea.setText(getHTMLFromArrayList(html));
		
        //csseditorPane
        cssTextArea = new JTextArea(20,200);
        JScrollPane cssScrollPane = new JScrollPane(cssTextArea);
        
        //jseditorpane
        jsTextArea = new JTextArea(20,200);
        JScrollPane jsScrollPane = new JScrollPane(jsTextArea);
        
		JTabbedPane tabbedPane = new JTabbedPane();
		JComponent panel1 = eScrollPane;
		tabbedPane.addTab("Preview", null, panel1, "Preview the page");
		
		JComponent panel2 = tScrollPane;
        tabbedPane.addTab("HTML", null, panel2,
                "View HTML Document");
        
		JComponent panel3 = cssScrollPane;
        tabbedPane.addTab("CSS", null, panel3,
                "View CSS Document");
        
		JComponent panel4 = jsScrollPane;
        tabbedPane.addTab("JS", null, panel4,
                "View JavaScript Document");
        
        JScrollPane rSidePane = new JScrollPane();
        rSidePane.setPreferredSize(new Dimension(100,0));
        //tabbedPane.setPreferredSize(new Dimension(2000,0));
        JSplitPane rSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabbedPane, rSidePane);
        JPanel mainPane = new JPanel(new BorderLayout());
        scrollPane.setPreferredSize(new Dimension(200,0));
        
        mainPane.add(buttonPanel, BorderLayout.PAGE_START);
        mainPane.add(scrollPane, BorderLayout.LINE_START);
        mainPane.add(tabbedPane, BorderLayout.CENTER);
        mainPane.add(elementSplitPane, BorderLayout.LINE_END);

		editorPane.setCaretPosition(0);
		textArea.setCaretPosition(0);
        
        frame.getContentPane().add(mainPane);
        frame.setMinimumSize(new Dimension(1200,800));
        //frame.setLocationByPlatform(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);

        CreateChildNodes ccn =  new CreateChildNodes(fileRoot, root);
        new Thread(ccn).start();
	}


    @SuppressWarnings("unused")
	public void setEditorPaneDocument(String pageURL) {
		try {
//			URL url = new File(pageURL).toURI().toURL();
			File file = new File(pageURL);
			URL styleURL = new File(cssURL).toURI().toURL();
			if (file != null) {
			    editorPane.setEditorKit(editor);
				try {
					editor.getStyleSheet().importStyleSheet(new File(styleURL.getFile()).toURI().toURL());	
				} catch (MalformedURLException ex) {
					 System.err.println("Couldn't find file: "+file);
				}
				Document doc = editor.createDefaultDocument();
				editorPane.setDocument(doc);
				editorPane.setText(getHTMLFromURL(file.toURL()));
				//editorPane.setPage(url);

			    
			} else {
			    System.err.println("Couldn't find file: "+file);
			}
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }
	
	public String getHTMLFromURL(URL url){
		
		html = new ArrayList<String>();
		
		//read html file
		try (BufferedReader br = new BufferedReader(new FileReader(url.getFile()))){
			String sCurrentLine;
			while((sCurrentLine  = br.readLine()) != null) {
				html.add(sCurrentLine);
				//System.out.println(sCurrentLine);
			}
		} catch (IOException e){
			
		}
		
		//convert arraylist to string
		StringBuilder sb = new StringBuilder();
		for (String str : html)
		{
		    sb.append(str);
		    //sb.append("<br>");
		}
		//System.out.println(sb.toString());
		return sb.toString();
	}
	
	public String getHTMLFromArrayList(ArrayList<String> al) {
		//convert arraylist to string
		StringBuilder sb = new StringBuilder();
		sb.append(" ");
		for (String str : al)
		{
		    sb.append(str);
		    sb.append("\n");
		}
		
		//System.out.println(sb.toString());
		return sb.toString();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		JList<?> list = (JList<?>)e.getSource();
		updateText(documentObjects[list.getSelectedIndex()]);
	}
	

    public class CreateChildNodes implements Runnable {

        private DefaultMutableTreeNode root;

        private File fileRoot;

        public CreateChildNodes(File fileRoot, 
                DefaultMutableTreeNode root) {
            this.fileRoot = fileRoot;
            this.root = root;
        }

        @Override
        public void run() {
            createChildren(fileRoot, root);
        }

        private void createChildren(File fileRoot, 
                DefaultMutableTreeNode node) {
            File[] files = fileRoot.listFiles();
            if (files == null) return;

            for (File file : files) {
                DefaultMutableTreeNode childNode = 
                        new DefaultMutableTreeNode(new FileNode(file));
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
    }
	
	protected void updateText (String name) {

		switch(name) {
		case "All":
			textArea.setText(getHTMLFromArrayList(html));
			removeListModelObjects();
			break;
		case "Header":
			setListModelHeaderObjects();
			break;
		case "Body":
			setListModelBodyObjects();
			break;
		case "Footer":
			setListModelFooterObjects();
			break;
		default:
				break;
		}
	}
	
	public void removeListModelObjects() {
		DefaultListModel<String> listModel = (DefaultListModel<String>) subList.getModel();
		listModel.removeAllElements();
	}
	
	public void setListModelHeaderObjects() {
		removeListModelObjects();
		
	}
	
	public void setListModelBodyObjects() {
		removeListModelObjects(); 
		for(int i=0; i<reader.bodyElement.size(); i++) {
			listModel.addElement(reader.bodyElement.get(i).getElementName()+(i+1)+", "+reader.bodyElement.get(i).getId());
		}
	}
	
	public void setListModelFooterObjects() {
		removeListModelObjects();
		listModel.addElement("Footer");
	}

    protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		String p = e.getPath().toString();
		p = p.substring(p.indexOf("[")+1);
		p = p.substring(0,p.indexOf("]"));
		
		List<String> file = Arrays.asList(p.split("\\s*,\\s*"));
		
		System.out.println("P STRING: "+p);
		
		String fileName = file.get(file.size()-1);
		String filePath = rootFolder+"/";
		String fileType = fileName.substring(fileName.indexOf(".")+1);
		
		for(int i=1; i<file.size(); i++) {
			filePath+=file.get(i)+"/";
		}
		System.out.println(fileName+" PATH: "+filePath+" FILETYPE OF: "+fileType);

		if(fileType.equals("html")){
			setEditorPaneDocument(filePath);
			textArea.setText(getHTMLFromArrayList(html));
		}
		if(fileType.equals("css")) {
			setEditorPaneDocument(filePath);
			cssTextArea.setText(getHTMLFromArrayList(html));
		}
		if(fileType.equals("js")) {
			setEditorPaneDocument(filePath);
			jsTextArea.setText(getHTMLFromArrayList(html));
		}
	}
}