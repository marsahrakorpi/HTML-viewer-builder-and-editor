package dialogs;

import static com.mongodb.client.model.Filters.eq;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
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
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RootPaneContainer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.io.FileUtils;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.css.sac.InputSource;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.steadystate.css.dom.CSSRuleListImpl;
import com.steadystate.css.dom.CSSStyleDeclarationImpl;
import com.steadystate.css.dom.CSSStyleRuleImpl;
import com.steadystate.css.dom.CSSStyleSheetImpl;
import com.steadystate.css.dom.CSSValueImpl;
import com.steadystate.css.dom.Property;
import com.steadystate.css.format.CSSFormat;
import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;

import engine.BodyElementInfo;
import engine.FileSaver;
import engine.HTMLDocReader;
import engine.Main;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import listeners.newElementFieldDocumentListener;

public class EditElementDialog {

	// Variables
	private static String html; // short form HTML
	public static String fullHTML; // full HTML
	private static String elementNoTags;
	private static BodyElementInfo bElement;

	// ATTRIBUTES
	private String[] globalHTMLAttributes = { "id", "accesskey", "contenteditable", "contextmenu", "dir", "draggable",
			"dropzone", "lang", "spellcheck", "tabindex", "title", "translate" };

	// ELEMENTS
	public static Element el; // short, ie <h1>Element</h1>
	public static Element element; // full html doc form

	// TEMP FILES
	public static File tempCSSFile;
	public static File tempHTMLFile;

	// CSS
	ArrayList<String> cssFiles;
	private static CSSStyleSheetImpl stylesheet;
	private static String cssSelector;

	// JAVA FX
	private final JFXPanel fxPanel = new JFXPanel();
	public static WebView webView;
	public static WebEngine webEngine;

	// UI
	final JDialog dialog;
	private String[] tabTitles = { "Global Attributes", "Element Attributes", "Style properties", "Events" };
	private JPanel[] tabPanels = new JPanel[tabTitles.length];
	private static CSSStylePanels cssPanels;

	public static ArrayList<JLabel> label;
	public static ArrayList<JTextField> field;

	public static JTextArea textArea;
	public static JTabbedPane tabbedPane;
	public static JSplitPane previewPane;

	// Classes
	private HTMLDocReader reader;

	// New Element Boolean
	boolean isNewElement = true;

	public EditElementDialog(String html, MongoClient mongoClient, MongoDatabase db,
			MongoCollection<Document> elementsCollection, HTMLDocReader reader, boolean newElement,
			BodyElementInfo bElement) {

		this.html = html;
		this.reader = reader;
		this.isNewElement = newElement;
		this.bElement = bElement;

		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		dialog = new JDialog(Main.frame, "New Element", true);
		RootPaneContainer root = (RootPaneContainer) Main.frame.getRootPane().getTopLevelAncestor();
		root.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		root.getGlassPane().setVisible(true);

		Dimension d = new Dimension(150, 23);

		Document doc = elementsCollection.find(eq("name", html)).first();

		JSONArray attrObj;
		JSONObject object = new JSONObject(doc.toJson());

		if (isNewElement) {
			fullHTML = object.get("tag").toString();
		} else {
			fullHTML = bElement.getOuterHTML();
		}

		// Database may return weird tags such as <img></img>.
		// parsing to jsoup elements and then back to string will fix the tags to be
		// appropriate
		el = createJsoupElement();
		// fullHTML = element.toString();

		// file handling

		// CSS TEMP

		// get css fuiles from doc and store in array
		cssFiles = new ArrayList<String>();
		Elements links = HTMLDocReader.tempDoc.head().select("link[href]");
		for (int i = 0; i < links.size(); i++) {
			Element e = links.get(i);
			if (e.attr("href").equals("webViewCSS/webViewHighlighter.css")) {
				// System.out.println("Ignoring webViewHighlighter CSS");
			} else {
				cssFiles.add(e.attr("href"));
			}
		}

		// copy the first css doc found into the tempCSS.
		// This will be used as the default
		try {
			// System.out.println(tempFile);
			String firstFoundCss = FileUtils.readFileToString(new File(Main.tempDir + "\\" + cssFiles.get(0)), "UTF-8");
			tempCSSFile = new File(Main.tempDir + "\\" + "HTMLTempCSS.css");
			BufferedWriter bw = new BufferedWriter(new FileWriter(tempCSSFile));
			bw.write(firstFoundCss);
			bw.close();
			tempCSSFile.deleteOnExit();

			// Also set the stylesheet
			InputSource inputSource = new InputSource(
					new StringReader(FileUtils.readFileToString(tempCSSFile, "UTF-8")));
			CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
			stylesheet = (CSSStyleSheetImpl) parser.parseStyleSheet(inputSource, null, null);
		} catch (IOException e2) {

		}

		// HTML TEMP

		// Create temporary HTML doc
		Elements styles = HTMLDocReader.tempDoc.select("link[href]");
		org.jsoup.nodes.Document sdoc = Jsoup.parse(fullHTML);
		for (Element e1 : styles) {
			sdoc.head().append(e1.toString());
		}
		sdoc.head().append("<link rel=\"stylesheet\" href=\"HTMLTempCSS.css\"");
		try {
			// System.out.println(tempFile);
			tempHTMLFile = new File(Main.tempDir + "\\" + "HTMLEditAttributeTemp.html");
			BufferedWriter bw = new BufferedWriter(new FileWriter(tempHTMLFile));
			bw.write(sdoc.toString());
			bw.close();
			tempHTMLFile.deleteOnExit();
		} catch (IOException e2) {

		}

		// Create tabs

		for (int i = 0; i < tabTitles.length; i++) {
			tabPanels[i] = new JPanel();
			tabPanels[i].setLayout(new BoxLayout(tabPanels[i], BoxLayout.PAGE_AXIS));
			tabPanels[i].setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			tabPanels[i].add(Box.createHorizontalGlue());
			tabPanels[i].add(Box.createRigidArea(new Dimension(5, 5)));
			tabPanels[i].revalidate();
		}

		// tab 1

		JLabel l = new JLabel("Global HTML Attributes");
		l.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		l.setFont(new Font("Arial", Font.BOLD, 15));
		tabPanels[0].add(l);

		label = new ArrayList<JLabel>();
		field = new ArrayList<JTextField>();
		for (int i = 0; i < globalHTMLAttributes.length; i++) {
			label.add(new JLabel(globalHTMLAttributes[i]));
			if (el.hasAttr(globalHTMLAttributes[i])) {
				field.add(new JTextField(el.attr(globalHTMLAttributes[i])));
			} else {
				field.add(new JTextField(""));
			}

		}
		for (int i = 0; i < label.size(); i++) {

			tabPanels[0].add(label.get(i));
			tabPanels[0].add(field.get(i));
			label.get(i).setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
			label.get(i).setAlignmentX(Component.LEFT_ALIGNMENT);
			field.get(i).setAlignmentX(Component.LEFT_ALIGNMENT);
			field.get(i).setPreferredSize(d);
			field.get(i).setHorizontalAlignment(JTextField.LEFT);
		}
		tabPanels[0].revalidate();
		JCheckBox hiddenCheck = new JCheckBox("hidden");
		hiddenCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				// el = getJsoupElement();
				if (hiddenCheck.isSelected()) {
					el.attr(hiddenCheck.getText(), "");
				} else if (!hiddenCheck.isSelected()) {
					el.removeAttr(hiddenCheck.getText());
				}
				fullHTML = element.toString();
				updateDoc();

				// SET HIDDEN
			}
		});

		tabPanels[0].add(hiddenCheck);
		tabPanels[0].setAlignmentX(Component.LEFT_ALIGNMENT);

		/*
		 * 
		 * TAB 2, ELEMENT SPECIFIC ATTRIBUTES. THIS WILL ALSO CREATE FULL HTML TAGS AND
		 * ELEMENTS
		 * 
		 */
		// MONGO DB JSON PARSING
		try {

			attrObj = object.getJSONArray("attributes");
			JPanel valuePanel = new JPanel();
			// valuePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.PAGE_AXIS));
			valuePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			valuePanel.add(Box.createHorizontalGlue());
			valuePanel.add(Box.createRigidArea(new Dimension(5, 5)));
			for (int i = 0; i < attrObj.length(); i++) {
				JSONObject objectFromArray = attrObj.getJSONObject(i);
				// System.out.println(objectFromArray);
				Iterator<String> iterator = objectFromArray.keys();
				while (iterator.hasNext()) {
					// THIS IS THE KEY THAT IS USED TO MAKE THE ATTRIBUTE NAMES JLABEL

					JPanel optionsPanel = new JPanel();

					String key = iterator.next();
					JLabel keyLabel = new JLabel(key);
					keyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
					optionsPanel.add(keyLabel);
					// KEY ARRAY
					JSONArray keyArray = objectFromArray.getJSONArray(key);

					optionsPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
					optionsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
					optionsPanel.setMaximumSize(new Dimension(500, 75));
					for (int j = 0; j < keyArray.length(); j++) {
						// System.out.println(keyArray.get(j));
						JSONObject lastObject = keyArray.getJSONObject(j);
						// DETERMINES WHAT TYPE OF COMPONENT TO CREATE BY READING THE OBJECT'S NAME
						String type = lastObject.names().get(0).toString();
						// System.out.println("Type equals: "+type+"for key "+key);
						if (type.equals("text")) {
							JTextField elementText = new JTextField();
							elementText.setPreferredSize(d);
							optionsPanel.add(elementText);
							elementText.setText(el.text());
							elementText.getDocument().addDocumentListener(new DocumentListener() {

								@Override
								public void changedUpdate(DocumentEvent e) {
									// TODO Auto-generated method stub
									updateElement();
								}

								@Override
								public void insertUpdate(DocumentEvent e) {
									// TODO Auto-generated method stub
									updateElement();
								}

								@Override
								public void removeUpdate(DocumentEvent e) {
									// TODO Auto-generated method stub
									updateElement();
								}

								public void updateElement() {
									el.text(elementText.getText());
									fullHTML = element.toString();
									updateDoc();
								}

							});

						}
						if (type.equals("value")) {
							JSONArray valueArray = lastObject.getJSONArray("value");
							List<String> valueList = new ArrayList<String>();
							valueList.add("");
							for (int k = 0; k < valueArray.length(); k++) {
								valueList.add(valueArray.get(k).toString());
							}
							String[] valueStringList = valueList.toArray(new String[valueList.size()]);
							if (valueStringList[1].equals("true")) {
								JCheckBox checkBox = new JCheckBox(key);
								checkBox.addItemListener(new ItemListener() {
									@Override
									public void itemStateChanged(ItemEvent e) {
										// TODO Auto-generated method stub
										// el = getJsoupElement();
										if (checkBox.isSelected()) {
											el.attr(checkBox.getText(), "");
										} else if (!checkBox.isSelected()) {
											el.removeAttr(checkBox.getText());
										}
										fullHTML = element.toString();
										updateDoc();
									}
								});
								optionsPanel.add(checkBox);
							} else {
								JComboBox<String> valueComboBox = new JComboBox<String>(valueStringList);
								valueComboBox.setMaximumSize(d);
								valueComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
								valueComboBox.addItemListener(new ItemListener() {
									@Override
									public void itemStateChanged(ItemEvent e) {
										// System.out.println(html);
										el = getJsoupElement();
										if (e.getItem().equals("")) {
											// System.out.println("removing");
											el.removeAttr(key);
										} else {
											el.attr(key, e.getItem().toString());
										}
										fullHTML = element.toString();
										updateDoc();
									}

								});
								optionsPanel.add(valueComboBox);
							}
						}

						if (type.equals("unit")) {
							JSONArray unitArray = lastObject.getJSONArray("unit");
							if (unitArray.length() == 1) {
								// System.out.println(unitArray.get(0));

								if (unitArray.getString(0).equals("pixels")) {
									// NumberFormat format = NumberFormat.getIntegerInstance();
									// NumberFormatter formatter = new NumberFormatter(format);
									// formatter.setValueClass(Integer.class);
									// formatter.setMinimum(0);
									// formatter.setMaximum(Integer.MAX_VALUE);
									// formatter.setAllowsInvalid(false);
									// // If you want the value to be committed on each keystroke instead of focus
									// lost
									// formatter.setCommitsOnValidEdit(true);
									//
									// JFormattedTextField numberTextField = new JFormattedTextField(formatter);
									JTextField numberTextField = new JTextField();
									numberTextField.setPreferredSize(d);
									numberTextField.getDocument().addDocumentListener(new DocumentListener() {

										@Override
										public void changedUpdate(DocumentEvent e) {
											// TODO Auto-generated method stub
											updateElement();
										}

										@Override
										public void insertUpdate(DocumentEvent e) {
											// TODO Auto-generated method stub
											updateElement();
										}

										@Override
										public void removeUpdate(DocumentEvent e) {
											// TODO Auto-generated method stub
											updateElement();
										}

										public void updateElement() {
											if (numberTextField.getText().equals("")
													|| numberTextField.getText() == null) {
												el.removeAttr(key);
											} else {
												el.attr(key, numberTextField.getText());
											}
											fullHTML = element.toString();
											updateDoc();
										}

									});
									optionsPanel.add(numberTextField);
								} else {
									JTextField textField = new JTextField();
									textField.setPreferredSize(d);
									textField.getDocument().addDocumentListener(new DocumentListener() {

										@Override
										public void changedUpdate(DocumentEvent e) {
											// TODO Auto-generated method stub
											updateElement();
										}

										@Override
										public void insertUpdate(DocumentEvent e) {
											// TODO Auto-generated method stub
											updateElement();
										}

										@Override
										public void removeUpdate(DocumentEvent e) {
											// TODO Auto-generated method stub
											updateElement();
										}

										public void updateElement() {
											if (textField.getText().equals("") || textField.getText() == null) {
												el.removeAttr(key);
											} else {
												el.attr(key, textField.getText());
											}
											fullHTML = element.toString();
											updateDoc();
										}

									});
									optionsPanel.add(textField);
								}

								JLabel unitLabel = new JLabel(" - " + unitArray.getString(0));
								Font font = new Font("Arial", 2, 12);
								unitLabel.setFont(font);
								optionsPanel.add(unitLabel);
							}
						}

						if (type.equals("action")) {
							JSONArray actionArray = lastObject.getJSONArray("action");
							if (actionArray.get(0).toString().equals("upload")) {
								JButton uploadButton = new JButton("Upload");
								optionsPanel.add(uploadButton);
							}
						}
					}
					valuePanel.add(optionsPanel);
				}
			}

			tabPanels[1].add(valuePanel);
		} catch (Exception e1) {
			e1.printStackTrace();
			// tabPanels[1].add(new JLabel("Element does not have attributes that are
			// specific to it."));
		}

		/*
		 * 
		 * TAB 3 CSS STYLES AND HANDLING CHANGES AKA MY LIFE IS A MESS
		 * 
		 */

		JLabel cssLabel = new JLabel("Style Properties");
		elementNoTags = html.substring(1, html.length() - 1);
		cssLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		cssLabel.setFont(new Font("Arial", Font.BOLD, 15));
		tabPanels[2].add(cssLabel);

		JPanel filesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel fileLabel = new JLabel("Apply rule in File: ");

		JComboBox<String> cssFilesComboBox = new JComboBox<String>(cssFiles.toArray(new String[cssFiles.size()]));

		cssFilesComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent f) {
				try {
					tempCSSFile.delete();
					String existingCSSFile = FileUtils.readFileToString(
							new File(Main.tempDir + "\\" + cssFilesComboBox.getSelectedItem().toString()), "UTF-8");
					tempCSSFile = new File(Main.tempDir + "\\" + "HTMLTempCSS.css");
					BufferedWriter bw = new BufferedWriter(new FileWriter(tempCSSFile));
					bw.write(existingCSSFile);
					bw.close();
					tempCSSFile.deleteOnExit();
				} catch (IOException e) {

				}
			}
		});

		filesPanel.add(fileLabel);
		filesPanel.add(cssFilesComboBox);

		tabPanels[2].add(filesPanel);

		cssPanels = new CSSStylePanels(cssSelector, stylesheet, tempHTMLFile, tempCSSFile, elementNoTags, d, textArea,
				tabbedPane, "EditNewElementDialog");

		// JPanel classSelectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel classSelectLabel = new JLabel("Class: ");
		ArrayList<String> cssClasses = new ArrayList<String>();
		JComboBox<String> classComboBox = new JComboBox<String>();
		classSelectLabel.setVisible(false);
		classComboBox.setVisible(false);

		JButton newClassButton = new JButton("New Class");
		newClassButton.setVisible(false);
		JPanel referPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel referLabel = new JLabel("Refer to this element by: ");
		JLabel warningLabel = new JLabel("Will change the style of all " + elementNoTags + " elements.");
		warningLabel.setForeground(Color.RED);
		String[] selectors = { "tag", "id", "class" };
		JComboBox<String> cssSelectorComboBox = new JComboBox<String>(selectors);
		cssSelectorComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent s) {
				classSelectLabel.setVisible(false);
				classComboBox.setVisible(false);
				newClassButton.setVisible(false);
				classComboBox.removeAllItems();
				switch (cssSelectorComboBox.getSelectedItem().toString()) {

				case "tag":
					cssSelector = elementNoTags;
					warningLabel.setText("Will affect the style of all " + elementNoTags + " elements.");
					String css;
					// update stylesheet
					InputSource inputSource;
					CSSOMParser parser;
					try {
						// css = FileUtils.readFileToString(tempCSSFile, "UTF-8");
						css = FileUtils.readFileToString(
								new File(Main.tempDir + "\\" + cssFilesComboBox.getSelectedItem()), "UTF-8");
						css += "\n" + cssSelector + "{";
						for (int i = 0; i < cssPanels.properties.size(); i++) {
							css += "\n" + cssPanels.properties.get(i) + ":" + cssPanels.values.get(i);
						}
						css += "\n}";
						// textArea.setText(css);
						FileUtils.write(tempCSSFile, css, "UTF-8");

						inputSource = new InputSource(
								new StringReader(FileUtils.readFileToString(tempCSSFile, "UTF-8")));
						parser = new CSSOMParser(new SACParserCSS3());
						stylesheet = (CSSStyleSheetImpl) parser.parseStyleSheet(inputSource, null, null);
						tabPanels[2].remove(cssPanels.getContainer());
						cssPanels = new CSSStylePanels(cssSelector, stylesheet, tempHTMLFile, tempCSSFile,
								elementNoTags, d, textArea, tabbedPane, "EditNewElementDialog");
						tabPanels[2].add(cssPanels.getContainer());
					} catch (IOException e2) {

					}

					textArea.setText(cssPanels.getCSSText(cssSelector));
					break;
				case "id":
					String id = el.attr("id");
					Elements idInDoc = null;
					try {
						idInDoc = HTMLDocReader.tempDoc.select("#" + id);

						if (idInDoc.size() > 1) {
							JOptionPane.showMessageDialog(dialog, "One or more elements with the ID \"" + el.attr("id")
									+ "\" have been detected.\n Please set a new ID for this element in the global HTML attributes.",
									"Multiple Same ID", JOptionPane.ERROR_MESSAGE);
						}
					} catch (Exception e1) {
						// no id set for element

					}
					if (id.equals("") || id == null) {
						cssSelector = elementNoTags;
						JOptionPane.showMessageDialog(dialog,
								"No ID found for this element.\n Please set an ID in the global HTML attributes.",
								"No ID", JOptionPane.ERROR_MESSAGE);
						cssSelectorComboBox.setSelectedIndex(0);
					} else {
						cssSelector = "#" + id;
						warningLabel.setText("");
						if (getRule(cssSelector) == null) {
							// IF RULE DOES NOT EXIST, CREATE RULE
							try {
								// CREATE ID RULE IN TEMP CSS
								// System.out.println(cssSelector);
								if (getRule(cssSelector) == null) {
									css = FileUtils.readFileToString(
											new File(Main.tempDir + "\\" + cssFilesComboBox.getSelectedItem()),
											"UTF-8");
									css += "\n" + cssSelector + "{\n}";
									// textArea.setText(css);
									FileUtils.write(tempCSSFile, css, "UTF-8");
									// update stylesheet
									inputSource = new InputSource(
											new StringReader(FileUtils.readFileToString(tempCSSFile, "UTF-8")));
									parser = new CSSOMParser(new SACParserCSS3());
									stylesheet = (CSSStyleSheetImpl) parser.parseStyleSheet(inputSource, null, null);
									tabPanels[2].remove(cssPanels.getContainer());
									cssPanels = new CSSStylePanels(cssSelector, stylesheet, tempHTMLFile, tempCSSFile,
											elementNoTags, d, textArea, tabbedPane, "EditNewElementDialog");
									tabPanels[2].add(cssPanels.getContainer());
									cssPanels.setCssSelector(cssSelector);
									textArea.setText(cssPanels.getCSSText(cssSelector));
								}

								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										updateFX(tempHTMLFile.getAbsolutePath());
									}
								});
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							// set text area as the rule
							try {
								css = FileUtils.readFileToString(
										new File(Main.tempDir + "\\" + cssFilesComboBox.getSelectedItem()), "UTF-8");
								css += cssSelector + "{";
								for (int i = 0; i < cssPanels.properties.size(); i++) {
									css += "\n" + cssPanels.properties.get(i) + ":" + cssPanels.values.get(i);
								}
								css += "\n}";
								FileUtils.write(tempCSSFile, css, "UTF-8");
								// update stylesheet
								inputSource = new InputSource(
										new StringReader(FileUtils.readFileToString(tempCSSFile, "UTF-8")));
								parser = new CSSOMParser(new SACParserCSS3());
								stylesheet = (CSSStyleSheetImpl) parser.parseStyleSheet(inputSource, null, null);

								tabPanels[2].remove(cssPanels.getContainer());
								cssPanels = new CSSStylePanels(cssSelector, stylesheet, tempHTMLFile, tempCSSFile,
										elementNoTags, d, textArea, tabbedPane, "EditNewElementDialog");
								tabPanels[2].add(cssPanels.getContainer());
								cssPanels.setCssSelector(cssSelector);
								textArea.setText(cssPanels.getCSSText(cssSelector));
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

						}
					}

					break;
				case "class":

					warningLabel.setText("");
					classSelectLabel.setVisible(true);
					classComboBox.setVisible(true);
					newClassButton.setVisible(true);
					try {
						css = FileUtils.readFileToString(
								new File(Main.tempDir + "\\" + cssFilesComboBox.getSelectedItem()), "UTF-8");
						css += cssSelector + "{";
						for (int i = 0; i < cssPanels.properties.size(); i++) {
							css += "\n" + cssPanels.properties.get(i) + ":" + cssPanels.values.get(i);
						}
						css += "\n}";
						FileUtils.write(tempCSSFile, css, "UTF-8");
						// update stylesheet
						inputSource = new InputSource(
								new StringReader(FileUtils.readFileToString(tempCSSFile, "UTF-8")));
						parser = new CSSOMParser(new SACParserCSS3());
						stylesheet = (CSSStyleSheetImpl) parser.parseStyleSheet(inputSource, null, null);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					CSSRuleListImpl rules = (CSSRuleListImpl) stylesheet.getCssRules();
					CSSStyleRuleImpl rule;
					for (int i = 0; i < rules.getLength(); i++) {
						rule = (CSSStyleRuleImpl) rules.item(i);
						if (rule.toString().substring(0, 1).equals(".")) {
							// cssClasses.add(rule.getSelectorText());
							classComboBox.addItem(rule.getSelectorText());
						}
					}
					classComboBox.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							try {
								cssSelector = classComboBox.getSelectedItem().toString();
								Attributes classElements = el.attributes();
								for (Attribute a : classElements) {
									if (a.getKey().equals("class")) {
										classElements.remove(a.getKey());
									}
								}
								el.addClass(cssSelector.substring(1, cssSelector.length()));
								tabPanels[2].remove(cssPanels.getContainer());
								cssPanels = new CSSStylePanels(cssSelector, stylesheet, tempHTMLFile, tempCSSFile,
										elementNoTags, d, textArea, tabbedPane, "EditNewElementDialog");
								tabPanels[2].add(cssPanels.getContainer());
								textArea.setText(cssPanels.getCSSText(cssSelector));
								fullHTML = element.toString();
								updateDoc();
							} catch (Exception e) {

							}
						}
					});

					cssSelector = classComboBox.getItemAt(0);
					Attributes classElements = el.attributes();
					for (Attribute a : classElements) {
						if (a.getKey().equals("class")) {
							classElements.remove(a.getKey());
						}
					}
					try {
						el.addClass(cssSelector.substring(1, cssSelector.length()));
					} catch (Exception e) {
						// no class exists
					}
					tabPanels[2].remove(cssPanels.getContainer());
					cssPanels = new CSSStylePanels(cssSelector, stylesheet, tempHTMLFile, tempCSSFile, elementNoTags, d,
							textArea, tabbedPane, "EditNewElementDialog");
					tabPanels[2].add(cssPanels.getContainer());
					textArea.setText(cssPanels.getCSSText(cssSelector));
					fullHTML = element.toString();
					updateDoc();

					break;
				default:
					break;
				}
			}
		});

		referPanel.add(referLabel);
		referPanel.add(cssSelectorComboBox);
		referPanel.add(warningLabel);
		referPanel.add(classSelectLabel);
		referPanel.add(classComboBox);
		referPanel.add(newClassButton);
		tabPanels[2].add(referPanel);

		newClassButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				final JDialog newClassDialog = new JDialog(dialog, "Enter a Class Name", true);
				JPanel mPanel = new JPanel();
				mPanel.setLayout(new BoxLayout(mPanel, BoxLayout.PAGE_AXIS));
				JLabel titleLabel = new JLabel("Class Name");
				titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
				mPanel.add(titleLabel);
				JTextField nameField = new JTextField("");
				nameField.setMaximumSize(d);
				mPanel.add(nameField);
				JPanel bPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

				JButton confButton = new JButton("Confirm");
				JButton cancButton = new JButton("Cancel");
				bPanel.add(confButton);
				bPanel.add(cancButton);

				confButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						try {
							System.out.println("conf");
							String css = FileUtils.readFileToString(
									new File(Main.tempDir + "\\" + cssFilesComboBox.getSelectedItem()), "UTF-8");
							css += "\n" + "." + nameField.getText() + "{\n}";
							// textArea.setText(css);
							FileUtils.write(tempCSSFile, css, "UTF-8");
							// update stylesheet
							InputSource inputSource = new InputSource(
									new StringReader(FileUtils.readFileToString(tempCSSFile, "UTF-8")));
							CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
							stylesheet = (CSSStyleSheetImpl) parser.parseStyleSheet(inputSource, null, null);
							el.addClass(nameField.getText());
							cssSelector = "."+nameField.getText();
							System.out.println(cssSelector);
							classComboBox.addItem(cssSelector);
							classComboBox.setSelectedItem(cssSelector);
							tabPanels[2].remove(cssPanels.getContainer());
							cssPanels = new CSSStylePanels(cssSelector, stylesheet, tempHTMLFile, tempCSSFile,
									elementNoTags, d, textArea, tabbedPane, "EditNewElementDialog");
							tabPanels[2].add(cssPanels.getContainer());
							cssPanels.setCssSelector(cssSelector);
							textArea.setText(cssPanels.getCSSText(cssSelector));
							fullHTML = element.toString();
							updateDoc();
							newClassDialog.dispose();
						} catch (IOException e) {
							e.printStackTrace();
							newClassDialog.dispose();
						}
					}
				});

				cancButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						dialog.dispose();
					}
				});
				mPanel.add(bPanel);
				newClassDialog.add(mPanel);
				newClassDialog.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent we) {
						newClassDialog.dispose();
					}
				});

				newClassDialog.pack();

				newClassDialog.setLocation(dialog.getX() + dialog.getWidth() / 2 - newClassDialog.getWidth() / 2,
						dialog.getY() + dialog.getHeight() / 2 - newClassDialog.getHeight() / 2);
				newClassDialog.setMinimumSize(new Dimension(300,100));
				newClassDialog.setVisible(true);
			}

		});

		tabPanels[2].add(cssPanels.getContainer());

		textArea = new JTextArea(fullHTML);
		textArea.setFont(new Font("Arial", Font.BOLD, 15));
		tabbedPane = new JTabbedPane();
		previewPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, fxPanel, textArea);
		JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, previewPane, tabbedPane);

		// creat the tabs
		for (int m = 0; m < tabTitles.length; m++) {
			JScrollPane scrollPane = new JScrollPane(tabPanels[m]);
			scrollPane.getVerticalScrollBar().setUnitIncrement(10);
			JComponent c = scrollPane;
			tabbedPane.addTab(tabTitles[m], null, c, tabTitles[m]);
		}

		// LISTENER FOR TABs TO CHANGE BETWEEN HTML TEXT VIEW AND CSS TEXT VIEW
		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				if (tabbedPane.getSelectedIndex() == 2) {
					// File input = new File(Main.tempDir+"\\"+selectedCSSFile);
					try {
						textArea.setText(getRule(cssSelector).toString());
					} catch (Exception e) {
						textArea.setText(elementNoTags + "{\t}");
					}
				}
				if (tabbedPane.getSelectedIndex() != 2) {
					updateDoc();
				}
			}
		});

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
				try {
					// CHECK IF ELEMENT WITH THIS ID ALREADY EXISTS

					String id = el.attr("id");

					try {
						Elements idInDoc = HTMLDocReader.tempDoc.select("#" + id);
						if (idInDoc.size() > 0 && isNewElement) {
							JOptionPane.showMessageDialog(dialog, "One or more elements with the ID \"" + el.attr("id")
									+ "\" have been detected.\n Please set a new ID for this element in the global HTML attributes.",
									"Multiple Same ID", JOptionPane.ERROR_MESSAGE);
							return;
						}
					} catch (Exception e) {

					}

					if (isNewElement) {
						HTMLDocReader.tempDoc.body().append(fullHTML);
					} else if (!isNewElement) {
						Element oldElementInDoc = HTMLDocReader.tempDoc.body().select("*").get(bElement.index);
						System.out.println("EL" + el);
						System.out.println("Old" + oldElementInDoc);
						oldElementInDoc.replaceWith(el);
					}

					String cssText = cssSelector + "{";
					for (int i = 0; i < cssPanels.properties.size(); i++) {
						cssText += "\n" + cssPanels.properties.get(i) + ": " + cssPanels.values.get(i) + ";";
					}
					cssText += "\n}";
					CSSFormat format = new CSSFormat();
					format.setRgbAsHex(true);
					InputSource inputSource = new InputSource(new StringReader(FileUtils.readFileToString(
							new File(Main.tempDir + "\\" + cssFilesComboBox.getSelectedItem().toString()), "UTF-8")));
					CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
					stylesheet = (CSSStyleSheetImpl) parser.parseStyleSheet(inputSource, null, null);

					if (getRule(cssSelector) != null) {
						getRule(cssSelector).setCssText(cssText);
						FileUtils.write(new File(Main.tempDir + "\\" + cssFilesComboBox.getSelectedItem().toString()),
								stylesheet.getCssText(format), "UTF-8");
					} else {
						FileUtils.write(new File(Main.tempDir + "\\" + cssFilesComboBox.getSelectedItem().toString()),
								stylesheet.getCssText(format) + "\n" + cssText, "UTF-8");
					}

					tempCSSFile.delete();
					tempHTMLFile.delete();
					reader.updateTempDoc();
					Main.updateFrame();
					Platform.runLater(new Runnable() {
						public void run() {
							Main.reloadWebEngine();
						}
					});
					for (int i = 1; i < Main.elementTree.getRowCount(); i++) {
						Main.elementTree.expandRow(i);
					}
					mongoClient.close();
					FileSaver.unsavedChanges = true;
					dialog.dispose();
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mongoClient.close();
				dialog.dispose();
			}
		});

		for (int j = 0; j < field.size(); j++) {
			field.get(j).getDocument().addDocumentListener(new newElementFieldDocumentListener(j));
		}

		Platform.runLater(new Runnable() { // this will run initFX as JavaFX-Thread
			@Override
			public void run() {
				initFX(fxPanel, tempHTMLFile);
			}
		});

		mainPanel.add(mainSplit, BorderLayout.CENTER);
		mainPanel.add(bottomPanel, BorderLayout.PAGE_END);
		dialog.setContentPane(mainPanel);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				try {
					tempCSSFile.delete();
					tempHTMLFile.delete();
				} catch (Exception e) {

				}
				mongoClient.close();
				dialog.dispose();
			}
		});

		dialog.pack();

		mainSplit.setDividerLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 3 - 100);
		previewPane.setDividerLocation(dialog.getHeight() / 4);
		dialog.setSize((Toolkit.getDefaultToolkit().getScreenSize().width) / 2,
				(Toolkit.getDefaultToolkit().getScreenSize().height) / 2);
		// center the dialog on screen
		dialog.setLocation(
				(Toolkit.getDefaultToolkit().getScreenSize().width) / 2 + Main.frame.getX() - dialog.getWidth() / 2,
				(Toolkit.getDefaultToolkit().getScreenSize().height) / 2 + Main.frame.getY() - dialog.getHeight() / 2);
		// dialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 -
		// dialog.getWidth() / 2,
		// (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - dialog.getHeight()
		// / 2);

		root.getGlassPane().setCursor(Cursor.getDefaultCursor());
		System.out.println(el);
		textArea.setMaximumSize(new Dimension(previewPane.getWidth(), previewPane.getHeight()));
		textArea.setLineWrap(true);
		updateDoc();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				webEngine.reload();
			}
		});
		dialog.setVisible(true);

	}

	private void initFX(final JFXPanel fxPanel, File f) {
		Group group = new Group();
		Scene scene = new Scene(group);
		fxPanel.setScene(scene);
		webView = new WebView();
		webView.isResizable();
		group.getChildren().add(webView);

		// Obtain the webEngine to navigate
		webEngine = webView.getEngine();
		try {
			webEngine.load(f.toURI().toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block

		}
	}

	public static void updateFX(String url) {
		File f = new File(url);
		webView.setPrefSize(previewPane.getSize().width, previewPane.getSize().height);
		if (url == null || url.equals("") || url.equals(null)) {
			webEngine.load("htt://www.google.com");
		} else {
			try {
				webEngine.load(f.toURI().toString());
				webEngine.reload();
				if (tabbedPane.getSelectedIndex() == 2) {
					textArea.setText(cssPanels.getCSSText(cssSelector));
				}
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				return;
			} catch (Exception e) {
				return;
			}
		}

	}

	public static void updateDoc() {

		org.jsoup.nodes.Document doc = Jsoup.parse(fullHTML);
		doc.head().append("<link rel=\"stylesheet\" href=\"HTMLTempCSS.css\">");
		try {
			// tempFile.delete();
			// tempFile = File.createTempFile("HTMLEditAttributeTemp", ".html");
			// tempFile.deleteOnExit();
		} catch (Exception e) {

		}
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(tempHTMLFile));
			bw.write(doc.toString());
			bw.close();
			textArea.setText(doc.select(html.substring(1, html.length() - 1)).toString());
			// textArea.setText(doc.toString());
		} catch (IOException e) {
			return;
		} catch (NullPointerException e) {
			return;
		}
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				updateFX(tempHTMLFile.getAbsolutePath());
			}
		});
	}

	private static CSSStyleRuleImpl getRule(String selector) {
		CSSStyleSheetImpl ss = stylesheet;
		CSSRuleListImpl rules = (CSSRuleListImpl) ss.getCssRules();
		CSSStyleRuleImpl rule;
		/* need to loop over all the rules and select the one that matches. */
		for (int i = 0; i < rules.getLength(); i++) {
			rule = (CSSStyleRuleImpl) rules.item(i);
			if (rule.getSelectorText().equals(selector)) {
				return rule;
			}
		}
		return null;
	}

	// a method to read a property of a specific rule:
	private Property getProperty(String selector, String property) {
		Property retProp = null;
		CSSStyleRuleImpl rule = getRule(selector);
		if (rule != null) {
			CSSStyleDeclarationImpl style = (CSSStyleDeclarationImpl) rule.getStyle();
			List<Property> props = style.getProperties();
			for (Property prop : props) {
				if (prop.getName().equals(property)) {
					return prop;
				}
			}
		}
		return retProp;
	}

	private void writeCssProperty(String selector, String property, String newValue) {
		Property prop = getProperty(selector, property);
		if (prop != null) {
			try {
				CSSValueImpl val = new CSSValueImpl();
				val.setCssText(newValue);
				prop.setValue(val);
			} catch (Exception e1) {
				// catch syntax errors, do nothing, doesn't matter
			}

			CSSFormat format = new CSSFormat();
			format.setRgbAsHex(true);
			File output = new File(Main.tempDir + "\\" + "HTMLTempCSS.css");
			try {
				// FileUtils.writeStringToFile(file, data, encoding);
				FileUtils.writeStringToFile(output, (String) stylesheet.getCssText(format), "UTF-8");
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						updateFX(tempHTMLFile.getAbsolutePath());
					}
				});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			CSSStyleRuleImpl r = getRule(elementNoTags);
			r.setCssText(elementNoTags + "{" + r.getStyle() + ";" + property + ":" + newValue + "}");
			CSSFormat format = new CSSFormat();
			format.setRgbAsHex(true);
			File output = new File(Main.tempDir + "\\" + "HTMLTempCSS.css");
			try {
				// FileUtils.writeStringToFile(file, data, encoding);
				FileUtils.writeStringToFile(output, (String) stylesheet.getCssText(format), "UTF-8");
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						updateFX(tempHTMLFile.getAbsolutePath());
					}
				});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void removeCSSProperty(String selector, String property) {
		Property prop = getProperty(selector, property);
		if (prop != null) {
			// CSSValueImpl val = new CSSValueImpl();
			// val.setCssText(null);
			prop.setValue(null);
			CSSFormat format = new CSSFormat();
			format.setRgbAsHex(true);
			File output = new File(Main.tempDir + "\\" + "HTMLTempCSS.css");
			try {
				// FileUtils.writeStringToFile(file, data, encoding);
				FileUtils.writeStringToFile(output, (String) stylesheet.getCssText(format), "UTF-8");
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						updateFX(tempHTMLFile.getAbsolutePath());
					}
				});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private Element createJsoupElement() {
		element = Jsoup.parseBodyFragment(fullHTML);
		elementNoTags = html.substring(1, html.length() - 1);
		cssSelector = elementNoTags;
		Elements elementSelector = element.select(elementNoTags);
		Element el = elementSelector.first();
		updateDoc();
		return el;
	}

	public static Element getJsoupElement() {
		return el;
	}

}
