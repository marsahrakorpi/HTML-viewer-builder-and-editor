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
import java.util.stream.Collectors;

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
import javax.swing.ScrollPaneConstants;
import javax.swing.ScrollPaneLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

import actionListeners.ListListener;
import bodyElements.BodyElement;
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
	public JPanel elementOptions;
	public JEditorPane editorPane;
	public HTMLEditorKit editor;
	public AbstractDocument doc;

	public JList<String> flist;
	
	private ListListener listListener;
	
	public String pageURL = "HTML/index.html";
	public String cssURL = "HTML/css/style.css";
	public String jsURL ="HTML/js/script.js";
	
	private String[] documentObjects = {"All", "Header", "Body", "Footer"};
	
	private String headerText = "HEADER TEXT";
	private String bodyText = "BODY TEXT";
	private String footerText = "FOOTER TEXT";
	
	public SplitPane() {
		super("SplitPane");
		
		/*
		 * 
		 * 
		 * 	LEFT SIDE PANEL
		 * 
		 * 
		 */
		
		list = new JList<String>(documentObjects);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectedIndex(0);
		list.addListSelectionListener(this);
		
		
		//add new element button
		JButton b1 = new JButton("Add Element");
		b1.setVerticalTextPosition(AbstractButton.CENTER);
		b1.setHorizontalTextPosition(AbstractButton.CENTER);
		

		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.add(b1);
		
		listModel = new DefaultListModel<String>();
		listModel.addElement("Haloo");
		buildHeaderMenu();
		buildBodyMenu();
		buildFooterMenu();
/*
		for(Object val : headerObjectsList) {
			String valStr = val.toString();
			listModel.addElement(valStr);
			System.out.println(valStr);
		}
*/		
		subList = new JList<String>(listModel);
		subList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		subList.setSelectedIndex(0);
		subList.addListSelectionListener(new ListListener());
		
		listScrollPane = new JScrollPane(list);
		JSplitPane subListScrollPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, buttonPanel, subList);

		//create left menu panes
		leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listScrollPane, subListScrollPane);
		
		/*
		 * 
		 * 
		 * RIGHT SIDE PANEL
		 * 
		 * 
		 */
		
		//Create text pane and config
		
		//ELEMENT OPTIONS
		elementOptions = new JPanel(new BorderLayout());
		
		//PREVIEW SCROLL PANE
		//TABBED PANE
		
		//HTML PAGE VIEWER
		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editor = new HTMLEditorKit();
		setEditorPaneDocument(pageURL, cssURL, jsURL);
		reader = new HTMLDocReader(pageURL);
		//HTML RAW TEXT VIEWER
		textArea = new JTextArea(20, 200);
		textArea.setEditable(false);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		JComponent panel1 = editorPane;
		tabbedPane.addTab("Preview", null, panel1, "Preview the page");
		
		JComponent panel2 = textArea;
        tabbedPane.addTab("HTML", null, panel2,
                "View HTML Document");
		
		//CREATE RIGHT SIDE SCROLL PANES
		JScrollPane optionsScrollPane = new JScrollPane(elementOptions);
		JScrollPane previewScrollPane = new JScrollPane(tabbedPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		//CREATE RIGHT SIDE SPLIT PANE
		rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, optionsScrollPane, previewScrollPane); 
		
		//CREATE MAIN SPLIT PANE
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplitPane, rightSplitPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(150);
		
		Dimension minimumSize = new Dimension(300, 20);
		listScrollPane.setMinimumSize(minimumSize);
		optionsScrollPane.setMinimumSize(new Dimension(300, 0));
		
		tabbedPane.setPreferredSize(new Dimension(400, 200));
		splitPane.setPreferredSize(new Dimension (400,200));
		updateText(documentObjects[list.getSelectedIndex()]);

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
		for(int i=0; i<HTMLDocReader.headings.size(); i++) {
			listModel.addElement(HTMLDocReader.headings.get(i).getElementName()+(i+1)+", "+HTMLDocReader.headings.get(i).getId());
		}
		for(int i=0; i<HTMLDocReader.headings.size(); i++) {
			listModel.addElement(HTMLDocReader.headings.get(i).getElementName()+(i+1)+", "+HTMLDocReader.headings.get(i).getId());
		}
		
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



