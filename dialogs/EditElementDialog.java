package dialogs;

import static com.mongodb.client.model.Filters.eq;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import engine.Main;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import listeners.CheckListener;

public class EditElementDialog {

	String html;
	String fullHTML = "";
	private JTabbedPane tabbedPane;

	private final JFXPanel fxPanel = new JFXPanel();
	private static WebView webView;
	private static WebEngine webEngine;
	private String tempElementURL;
	private File tempFile;

	private String[] tabTitles = { "Global HTML Attributes", "Element Specific Attributes", "Style Attributes",
			"Events" };
	private JPanel[] tabPanels = new JPanel[tabTitles.length];

	private String[] globalHTMLAttributes = { "id", "accesskey", "contenteditable", "contextmenu", "dir", "draggable",
			"dropzone", "lang", "spellcheck", "tabindex", "title", "translate" };

	private String[] cssProperties = { "Color Properties", "Background and Border Properties", "Basic Box Properties",
			"Flexible Box Layout", "Text Properties", "Text Decoration Properties", "Font Properties",
			"Writing Modes Properties", "Table Properties", "Lists and Counters Properties", "Animation Properties",
			"Transform Properties", "Transitions Properties", "Basic User Interface Properties",
			"Multi-column Layout Properties", "Paged Media", "Generated Content for Paged Media",
			"Filter Effects Properties", "Image Values and Replaced Content", "Masking Properties",
			"Speech Properties" };

	private ArrayList<JLabel> attributeNameList = new ArrayList<JLabel>();
	private ArrayList<JTextField> attributeValeList = new ArrayList<JTextField>();

	MongoClient mongoClient;

	public EditElementDialog(String html) {

		this.html = html;

		Block<Document> printBlock = new Block<Document>() {
			@Override
			public void apply(final Document document) {
				System.out.println(document.toJson());
			}
		};

		MongoClient mongoClient = new MongoClient(
				new MongoClientURI("mongodb://user:password@ds151024.mlab.com:51024/htmlelements"));
		MongoDatabase db = mongoClient.getDatabase("htmlelements");
		MongoCollection<Document> elementsCollection = db.getCollection("elements");

		Document doc = elementsCollection.find(eq("name", html)).first();


		// create all tab panels
		for (int i = 0; i < tabTitles.length; i++) {
			tabPanels[i] = new JPanel();
			tabPanels[i].setLayout(new BoxLayout(tabPanels[i], BoxLayout.PAGE_AXIS));
			tabPanels[i].setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			tabPanels[i].add(Box.createHorizontalGlue());
			tabPanels[i].add(Box.createRigidArea(new Dimension(5, 5)));
		}

		
		// tab 1

		JLabel l = new JLabel("Global HTML Attributes");
		l.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		l.setFont(new Font("Arial", Font.BOLD, 15));
		tabPanels[0].add(l);

		ArrayList<JLabel> label = new ArrayList<JLabel>();
		ArrayList<JTextField> field = new ArrayList<JTextField>();
		for (int i = 0; i < globalHTMLAttributes.length; i++) {
			label.add(new JLabel(globalHTMLAttributes[i]));
			field.add(new JTextField(""));
		}
		for (int i = 0; i < label.size(); i++) {
			tabPanels[0].add(label.get(i));
			tabPanels[0].add(field.get(i));
			label.get(i).setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
			field.get(i).setMaximumSize(new Dimension(275, 20));
			field.get(i).setHorizontalAlignment(JTextField.LEFT);
		}
		JCheckBox hiddenCheck = new JCheckBox("hidden");
		hiddenCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
				// SET HIDDEN
			}
		});
		// tabPanels[0].add(hiddenCheck);

		
		//Tab 2
		

		
		try {
			JSONArray attrObj;
			JSONObject object = new JSONObject(doc.toJson());
			attrObj = object.getJSONArray("attributes");
			
			for (int i = 0; i < attrObj.length(); i++) {
				JSONObject obb = attrObj.optJSONObject(i);
				System.out.println(obb);
				Iterator<String> iterator = obb.keys();
				while (iterator.hasNext()) {
					String currentKey = iterator.next();
					JSONArray arrFromKey = obb.getJSONArray(currentKey);
					System.out.println(currentKey);
					tabPanels[1].add(new JLabel(currentKey));
					for (int j = 0; j < arrFromKey.length(); j++) {
						System.out.println(arrFromKey.get(j));
						tabPanels[1].add(new JLabel(arrFromKey.get(j).toString()));
					}
				}
			}
		} catch (Exception e1) {
			tabPanels[1].add(new JLabel("Element does not have attributes that are specific to it."));
			System.out.println("Document not found in database matching "+html);
		}

		try {
			fullHTML = doc.getString("tag");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			fullHTML = html;
		}

		try {
			tempFile = File.createTempFile("HTMLEditAttributeTemp", ".html");
			BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
			bw.write(fullHTML);
			bw.close();
			tempFile.deleteOnExit();
			tempElementURL = tempFile.getAbsolutePath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		final JDialog dialog = new JDialog(Main.frame, "Edit HTML Element", true);

		JTextArea textArea = new JTextArea(fullHTML);
		textArea.setFont(new Font("Arial", Font.BOLD, 15));
		tabbedPane = new JTabbedPane();
		JSplitPane previewPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, fxPanel, textArea);
		previewPane.setDividerLocation((Toolkit.getDefaultToolkit().getScreenSize().height) / 6);
		JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, previewPane, tabbedPane);

		// creat the tabs
		for (int i = 0; i < tabTitles.length; i++) {
			JComponent c = tabPanels[i];
			tabbedPane.addTab(tabTitles[i], null, c, tabTitles[i]);
		}

		// tab 3 content

		JPanel bottomPanel = new JPanel();
		FlowLayout layout = new FlowLayout(FlowLayout.TRAILING, 20, 20);
		bottomPanel.setLayout(layout);
		JButton confirmButton = new JButton("Confirm");
		JButton cancelButton = new JButton("Cancel");
		bottomPanel.add(confirmButton);
		bottomPanel.add(cancelButton);
		
		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mongoClient.close();
				dialog.dispose();
			}
		});

		Platform.runLater(new Runnable() { // this will run initFX as JavaFX-Thread
			@Override
			public void run() {
				initFX(fxPanel, tempElementURL);
			}
		});

		mainPanel.add(mainSplit, BorderLayout.CENTER);
		mainPanel.add(bottomPanel, BorderLayout.PAGE_END);
		dialog.setContentPane(mainPanel);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				try {
					tempFile.delete();
				} catch (Exception e) {

				}
				mongoClient.close();
				dialog.dispose();
			}
		});
		dialog.pack();
		mainSplit.setDividerLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 4);
		dialog.setSize((Toolkit.getDefaultToolkit().getScreenSize().width) / 2,
				(Toolkit.getDefaultToolkit().getScreenSize().height) / 2);
		// center the dialog on screen
		dialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - dialog.getWidth() / 2,
				(Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - dialog.getHeight() / 2);
		dialog.setVisible(true);
	}

	private static void initFX(final JFXPanel fxPanel, String url) {
		Group group = new Group();
		Scene scene = new Scene(group);
		fxPanel.setScene(scene);
		webView = new WebView();
		webView.isResizable();
		group.getChildren().add(webView);

		// Obtain the webEngine to navigate
		webEngine = webView.getEngine();
		try {
			File f = new File(url);
			System.out.println(f);
			webEngine.load(f.toURI().toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block

		}
	}

	private static void updateFX(String url) {
		File f;
		if (url == null || url.equals("") || url.equals(null)) {
			webEngine.load("htt://www.google.com");
		} else {
			f = new File(url);
			try {
				webEngine.load(f.toURI().toString());
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				return;
			}
		}

	}

}
