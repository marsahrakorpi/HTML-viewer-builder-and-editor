package dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import engine.HTMLDocReader;
import engine.Main;

public class NewElementDialog implements TreeSelectionListener{

	String currentTagSelection = "";
	String currentTagFullHtml = "";
	JLabel selectedElementLabel;
	HTMLDocReader reader;
	
	public NewElementDialog(HTMLDocReader reader) {
		// TODO Auto-generated constructor stub
		this.reader = reader;
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		final JDialog dialog = new JDialog(Main.frame, "Select an Element", true);

		JPanel topPanel = new JPanel();
		
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("HTML Elements");
		JTree tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		createNodes(top);
		JScrollPane treeView = new JScrollPane(tree);
		
		JPanel bottomPanel = new JPanel(new BorderLayout());
		
		JPanel buttonPanel = new JPanel();
		FlowLayout layout = new FlowLayout(FlowLayout.CENTER, 20, 20);
		buttonPanel.setLayout(layout);
		
		selectedElementLabel = new JLabel("", SwingConstants.CENTER);
		
		JButton confirmButton = new JButton("Confirm");
		JButton cancelButton = new JButton("Cancel");
		
		buttonPanel.add(confirmButton);
		buttonPanel.add(cancelButton);
		
		bottomPanel.add(selectedElementLabel, BorderLayout.PAGE_START);
		bottomPanel.add(buttonPanel, BorderLayout.CENTER);
		
		mainPanel.add(topPanel, BorderLayout.PAGE_START);
		mainPanel.add(treeView, BorderLayout.CENTER);
		mainPanel.add(bottomPanel, BorderLayout.PAGE_END);
		
		tree.expandRow(0);
		tree.addTreeSelectionListener(this);
		dialog.setContentPane(mainPanel);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		/*
		 * Loads MongoDB here to spread loading times around
		 * Otherwise EditNewElementDialog will have a noticeable delay when opening
		 */
		MongoClient mongoClient = new MongoClient(
				new MongoClientURI("mongodb://user:password@ds151024.mlab.com:51024/htmlelements"));
		MongoDatabase db = mongoClient.getDatabase("htmlelements");
		MongoCollection<Document> elementsCollection = db.getCollection("elements");

		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				dialog.dispose();
			}
		});
		
		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(currentTagSelection.equals("")) {
					return;
				} else {
					dialog.dispose();
//					StringBuilder str = new StringBuilder(currentTagSelection);
//					str.insert(1, "/");
//					String html = currentTagSelection+"New Element"+str;
					new EditNewElementDialog(currentTagSelection, mongoClient, db, elementsCollection, reader);

				}
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();
				
			}
		});
		dialog.setSize((Toolkit.getDefaultToolkit().getScreenSize().width) / 4,
				(Toolkit.getDefaultToolkit().getScreenSize().height) / 2);
		// center the dialog on screen
		dialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - dialog.getWidth() / 2,
				(Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - dialog.getHeight() / 2);
		dialog.setVisible(true);
		

	

	}
	private static String getTreeText(TreeModel model, Object object, String indent) {
	    String myRow = indent + object + "\n";
//		String myRow = "";
	    for (int i = 0; i < model.getChildCount(object); i++) {
	        myRow += getTreeText(model, model.getChild(object, i), indent + "  ");
	    }
	    return myRow;
	}
	
	private void createNodes(DefaultMutableTreeNode top) {
		DefaultMutableTreeNode category = null;
		DefaultMutableTreeNode element = null;
		
		category = new DefaultMutableTreeNode("Basic HTML");
		top.add(category);

		element = new DefaultMutableTreeNode(new ElementInfo("<h1>","Defines a heading of Size 1"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<h2>","Defines a heading of Size 2"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<h3>","Defines a heading of Size 3"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<h4>","Defines a heading of Size 4"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<h5>","Defines a heading of Size 5"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<h6>","Defines a heading of Size 6"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<p>"," Defines a paragraph"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<br>","Inserts a single line break"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<hr>","	Defines a thematic change in the content"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<!--...-->","	Defines a comment"));
		category.add(element);
		
		category = new DefaultMutableTreeNode("Formatting");
		top.add(category);
		element = new DefaultMutableTreeNode(new ElementInfo("<acronym>"," Not supported in HTML5. Use <abbr> instead. Defines an acronym"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<abbr>","	Defines an abbreviation or an acronym"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<address>","	Defines contact information for the author/owner of a document/article"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<b>","	Defines bold text"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<bdi>","	Isolates a part of text that might be formatted in a different direction from other text outside it"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<bdo>","	Overrides the current text direction"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<big>","	Not supported in HTML5. Use CSS instead. Defines big text"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<blockquote>","	Defines a section that is quoted from another source"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<center>","	Not supported in HTML5. Use CSS instead. Defines centered text"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<cite>","	Defines the title of a work"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<code>","	Defines a piece of computer code"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<del>","	Defines text that has been deleted from a document"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<dfn>","	Represents the defining instance of a term"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<em>","	Defines emphasized text "));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<font>","	Not supported in HTML5. Use CSS instead. Defines font, color, and size for text"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<i>","	Defines a part of text in an alternate voice or mood"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<ins>","	Defines a text that has been inserted into a document"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<kbd>","	Defines keyboard input"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<mark>","	Defines marked/highlighted text"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<meter>","	Defines a scalar measurement within a known range (a gauge)"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<pre>","	Defines preformatted text"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<progress>","	Represents the progress of a task"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<q>","	Defines a short quotation"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<rp>","	Defines what to show in browsers that do not support ruby annotations"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<rt>","	Defines an explanation/pronunciation of characters (for East Asian typography)"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<ruby>","	Defines a ruby annotation (for East Asian typography)"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<s>","	Defines text that is no longer correct"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<samp>","	Defines sample output from a computer program"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<small>","	Defines smaller text"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<strike>","	Not supported in HTML5. Use <del> or <s> instead. Defines strikethrough text"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<strong>","	Defines important text"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<sub>","	Defines subscripted text"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<sup>","	Defines superscripted text"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<time>","	Defines a date/time"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<tt>","	Not supported in HTML5. Use CSS instead. Defines teletype text"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<u>","	Defines text that should be stylistically different from normal text"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<var>","	Defines a variable"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<wbr>","	Defines a possible line-break"));

		category = new DefaultMutableTreeNode("Forms and Input");
		top.add(category);
		element = new DefaultMutableTreeNode(new ElementInfo("<form>","	Defines an HTML form for user input"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<input>","	Defines an input control"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<textarea>","	Defines a multiline input control (text area)"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<button>","	Defines a clickable button"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<select>","	Defines a drop-down list"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<optgroup>","	Defines a group of related options in a drop-down list"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<option>","	Defines an option in a drop-down list"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<label>","	Defines a label for an <input> element"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<fieldset>","	Groups related elements in a form"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<legend>","	Defines a caption for a <fieldset> element"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<datalist>","	Specifies a list of pre-defined options for input controls"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<keygen>","	Defines a key-pair generator field (for forms)"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<output>","	Defines the result of a calculation"));

		
		category = new DefaultMutableTreeNode("Frames");
		top.add(category);
		element = new DefaultMutableTreeNode(new ElementInfo("<frame>","	Not supported in HTML5. Defines a window (a frame) in a frameset"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<frameset>","	Not supported in HTML5. Defines a set of frames"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<noframes>","	Not supported in HTML5. Defines an alternate content for users that do not support frames"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<iframe>","	Defines an inline frame"));
		
		
		category = new DefaultMutableTreeNode("Images");
		top.add(category);
		
		element = new DefaultMutableTreeNode(new ElementInfo("<img>","<img src=\"\">","	Defines an image"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<map>","	Defines a client-side image-map"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<area>","	Defines an area inside an image-map"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<canvas>","	Used to draw graphics, on the fly, via scripting (usually JavaScript)"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<figcaption>","	Defines a caption for a <figure> element"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<figure>","	Specifies self-contained content"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<picture>","	Defines a container for multiple image resources"));
		
		
		category = new DefaultMutableTreeNode("Audio / Video");
		top.add(category);
		element = new DefaultMutableTreeNode(new ElementInfo("<audio>","Defines sound content"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<source>","Defines multiple media resources for media elements (<video> , <audio> and <picture>)"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<track","Defines text tracks for media elements (<video> and <audio>)"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<video>","Defines a video or movie"));
		
		category = new DefaultMutableTreeNode("Links");
		top.add(category);
		element = new DefaultMutableTreeNode(new ElementInfo("<a>","	Defines a hyperlink"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<link>","	Defines the relationship between a document and an external resource (most used to link to style sheets)"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<nav>","	Defines navigation links"));

		category = new DefaultMutableTreeNode("Lists");
		top.add(category);
		element = new DefaultMutableTreeNode(new ElementInfo("<ul>","	Defines an unordered list"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<ol>","	Defines an ordered list"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<li>","	Defines a list item"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<dir>","	Not supported in HTML5. Use <ul> instead. Defines a directory list"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<dl>","	Defines a description list"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<dt>","	Defines a term/name in a description list"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<dd>","	Defines a description of a term/name in a description list"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<menu>","	Defines a list/menu of commands"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<menuitem>","	Defines a command/menu item that the user can invoke from a popup menu"));

		
		category = new DefaultMutableTreeNode("Tables");
		top.add(category);
		element = new DefaultMutableTreeNode(new ElementInfo("<table>","	Defines a table"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<caption>","	Defines a table caption"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<th>","	Defines a header cell in a table"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<tr>","	Defines a row in a table"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<td>","	Defines a cell in a table"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<thead>","	Groups the header content in a table"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<tbody>","	Groups the body content in a table"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<tfoot>","	Groups the footer content in a table"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<col>","	Specifies column properties for each column within a <colgroup> element <colgroup> Specifies a group of one or more columns in a table for formatting"));	

		
		category = new DefaultMutableTreeNode("Styles and Semantics");
		top.add(category);
		element = new DefaultMutableTreeNode(new ElementInfo("<style>","	Defines style information for a document"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<div>","	Defines a section in a document"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<span>","	Defines a section in a document"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<header>","	Defines a header for a document or section"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<footer>","	Defines a footer for a document or section"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<main>","	Specifies the main content of a document"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<section>","	Defines a section in a document"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<article>","	Defines an article"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<aside>","	Defines content aside from the page content"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<details>","	Defines additional details that the user can view or hide"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<dialog>","	Defines a dialog box or window"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<summary>","	Defines a visible heading for a <details> element"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<data>","	Links the given content with a machine-readable translation"));
		
		
		category = new DefaultMutableTreeNode("Programming");
		top.add(category);
		element = new DefaultMutableTreeNode(new ElementInfo("<script>","	Defines a client-side script"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<noscript>","	Defines an alternate content for users that do not support client-side scripts"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<applet>","	Not supported in HTML5. Use <embed> or <object> instead. Defines an embedded applet"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<embed>","	Defines a container for an external (non-HTML) application"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<object>","	Defines an embedded object"));
		category.add(element);
		element = new DefaultMutableTreeNode(new ElementInfo("<param>","	Defines a parameter for an object"));
		
		
	}

	private class ElementInfo {
		public String elementTag;
		public String elementDescription;
		public String fullHtml = "";
		public String[][] attributes = {{}};

		
		public ElementInfo(String tag, String fullHtml, String desc) {
			elementTag = tag;
			this.fullHtml = fullHtml;
			elementDescription = desc;
		}

		public ElementInfo(String tag, String desc) {
			elementTag = tag;
			elementDescription = desc;
		}

		public String toString() {
			return elementTag+ " \t " + elementDescription;
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
		if (node == null) {
			System.out.println("node null");
			return;
		}

		Object nodeInfo = node.getUserObject();
		if (node.isLeaf()) {
			ElementInfo eInfo = (ElementInfo) nodeInfo;
			currentTagSelection = eInfo.elementTag;
			currentTagFullHtml = eInfo.fullHtml;
			selectedElementLabel.setText("Create "+currentTagSelection+" Element?");
		} if(!node.isLeaf()) {
			currentTagSelection = "";
		}
		System.out.println("Current Selection:"+currentTagSelection);
	}

}
