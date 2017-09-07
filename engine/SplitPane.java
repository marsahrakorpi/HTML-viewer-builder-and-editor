package engine;

import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

import actionListeners.ListListener;
import bodyElements.Heading;

public class SplitPane extends JFrame
						implements ListSelectionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7812588604708320503L;
	
	HTMLDocReader reader;
	
	public JList<String> list;
	public JList<String> subList;
	
	public List<Object> headerObjectsList;
	public List<Object> bodyObjectsList;
	public List<Object> footerObjectsList;
	
	public List<Heading> headings;
	
	public ArrayList<String> html;
	
	public DefaultListModel<String> listModel;
	public DefaultListModel<String> subModel;
	public JScrollPane listScrollPane;
	public JSplitPane splitPane;
	public JSplitPane leftSplitPane, rightSplitPane;
	public JTextPane textPane;
	public JTextArea textArea;
	public JLabel text;
	public static JScrollPane elementOptions;
	public JEditorPane editorPane;
	public HTMLEditorKit editor;
	public AbstractDocument doc;

	public JList<String> flist;
	
	private ListListener listListener;
	
	public String pageURL = "HTML/index.html";
	public String cssURL = "HTML/css/style.css";
	public String jsURL ="HTML/js/script.js";
	
	private String[] documentObjects = {"Header", "Body", "Footer"};
	
	private String headerText = "HEADER TEXT";
	private String bodyText = "BODY TEXT";
	private String footerText = "FOOTER TEXT";
	
	public SplitPane() {
		super("SplitPane");
		/**
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * DEPRECATED
		 * WILL BE DELETED SOON
		 * USED ONLY AS CODE REFERENCE
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 */
		reader = new HTMLDocReader(pageURL);
		list = new JList<String>(documentObjects);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectedIndex(0);
		list.addListSelectionListener(this);
	
		//add new element button
		JButton b1 = new JButton("Add Element");
		b1.setVerticalTextPosition(AbstractButton.CENTER);
		b1.setHorizontalTextPosition(AbstractButton.CENTER);
	
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(b1);
		
		listModel = new DefaultListModel<String>();
		listModel.addElement("Haloo");
		buildHeaderMenu();
		buildBodyMenu();
		buildFooterMenu();

		subList = new JList<String>(listModel);
		subList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		subList.setSelectedIndex(0);
		subList.addListSelectionListener(new ListListener(reader));

		listScrollPane = new JScrollPane(list);

		
		/*
		 * 
		 * 
		 * RIGHT SIDE PANEL
		 * 
		 * 
		 */
		
		//Create text pane and config
		//ELEMENT OPTIONS
		elementOptions = new JScrollPane();

		//HTML PAGE VIEWER
		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editor = new HTMLEditorKit();
		setEditorPaneDocument(pageURL, cssURL, jsURL);
		JScrollPane eScrollPane = new JScrollPane(editorPane);
		
		//HTML RAW TEXT VIEWER
		textArea = new JTextArea(20, 200);
		textArea.setEditable(false);
		JScrollPane tScrollPane = new JScrollPane(textArea);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		JComponent panel1 = eScrollPane;
		tabbedPane.addTab("Preview", null, panel1, "Preview the page");
		
		JComponent panel2 = tScrollPane;
        tabbedPane.addTab("HTML", null, panel2,
                "View HTML Document");

        //CREATE LEFT SIDE PANES
		JSplitPane listSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, list, subList);
		JSplitPane subListScrollPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, buttonPanel, listSplit);
		//CREATE RIGHT SIDE SCROLL PANES
		JScrollPane optionsScrollPane = new JScrollPane(elementOptions);
		JScrollPane previewScrollPane = new JScrollPane(tabbedPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		//CREATE LEDT AND RIGHT SIDE SPLIT PANE
		leftSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, subListScrollPane);
		rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, previewScrollPane, optionsScrollPane); 

		
		//CREATE MAIN SPLIT PANE
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplitPane, rightSplitPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(300);
		
		//splitPane.setPreferredSize(new Dimension (400,200));
		updateText(documentObjects[list.getSelectedIndex()]);
		editorPane.setCaretPosition(0);
		textArea.setCaretPosition(0);

	}
	
    protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }
     
    public void setEditorPaneDocument(String pageURL, String cssURL, String jsURL) {
		try {
			URL url = new File(pageURL).toURI().toURL();
			URL styleURL = new File(cssURL).toURI().toURL();
			if (url != null) {
			    editorPane.setEditorKit(editor);
				try {
					editor.getStyleSheet().importStyleSheet(new File(styleURL.getFile()).toURI().toURL());
					
				} catch (MalformedURLException ex) {
					 System.err.println("Couldn't find file: "+url);
				}
				Document doc = editor.createDefaultDocument();
				editorPane.setDocument(doc);
				editorPane.setText(getHTMLFromURL(url));
				//editorPane.setPage(url);

			    
			} else {
			    System.err.println("Couldn't find file: "+url);
			}
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }
    
	
	public String getHTMLFromURL(URL url){
		html.clear();
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
	
	public void buildHeaderMenu() {
		
	}
	
	public void buildBodyMenu() {

	}
	
	public void buildFooterMenu() {
		
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
	
	public void removeListModelObjects() {
		DefaultListModel<String> listModel = (DefaultListModel<String>) subList.getModel();
		listModel.removeAllElements();
	}

	
	public JSplitPane getSplitPane() {
		return splitPane;
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		JList<?> list = (JList<?>)e.getSource();
		updateText(documentObjects[list.getSelectedIndex()]);
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

}



