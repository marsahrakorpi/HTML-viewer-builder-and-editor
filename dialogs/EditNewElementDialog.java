package dialogs;

import static com.mongodb.client.model.Filters.eq;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.LinearGradientPaint;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RootPaneContainer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.FontUIResource;

import org.apache.commons.io.FileUtils;
import org.bson.Document;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.SelectorList;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleSheet;

import com.helger.css.decl.CSSStyleRule;
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

import engine.FileHandler;
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

public class EditNewElementDialog {

	static String html;
	private static String elementNoTags = "";
	public static String fullHTML = "";
	public static Element el, element;
	private static JTabbedPane tabbedPane;
	private static JTextArea textArea;
	private final JFXPanel fxPanel = new JFXPanel();
	private static WebView webView;
	private static WebEngine webEngine;
	private String tempElementURL;
	private static File tempFile;
	private static JSplitPane previewPane;
	public static ArrayList<JLabel> label;
	public static ArrayList<JTextField> field;
	private String[] tabTitles = { "Global HTML Attributes", "Element Specific Attributes", "Style properties",
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
	private ArrayList<JTextField> attributeValueList = new ArrayList<JTextField>();
	ArrayList<CSSStyleSheetImpl> stylesheets = new ArrayList<CSSStyleSheetImpl>();
	String selectedCSSFile = "";
	String tempCSSFilePath = "";
	File tempCSSFile;
	static CSSStyleSheetImpl stylesheet;
	CSSStyleRuleImpl cssRule;
	MongoClient mongoClient;
	HTMLDocReader reader;

	public EditNewElementDialog(String html, MongoClient mongoClient, MongoDatabase db,
			MongoCollection<Document> elementsCollection, HTMLDocReader reader) throws IOException {

		EditNewElementDialog.html = html;
		this.reader = reader;

		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		final JDialog dialog = new JDialog(Main.frame, "New Element", true);
		RootPaneContainer root = (RootPaneContainer) Main.frame.getRootPane().getTopLevelAncestor();
		root.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		root.getGlassPane().setVisible(true);

		// try {
		//
		// org.jsoup.nodes.Document w3Doc =
		// Jsoup.connect("https://www.w3schools.com/cssref/default.asp").get();
		// Elements tdElements = w3Doc.select("table.w3-table-all td:eq(0)");
		// for (Element e : tdElements) {
		// System.out.println("\"" + e.text() + "\",");
		// }
		// } catch (IOException e2) {
		// // TODO Auto-generated catch block
		// e2.printStackTrace();
		// }

		Dimension d = new Dimension(150, 20);
		// mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		// mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		// mainPanel.add(Box.createHorizontalGlue());
		// mainPanel.add(Box.createRigidArea(new Dimension(5, 5)));

		Document doc = elementsCollection.find(eq("name", html)).first();

		// create all tab panels
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
			field.add(new JTextField(""));
		}
		// p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		for (int i = 0; i < label.size(); i++) {

			// b.setLayout(new FlowLayout(FlowLayout.LEADING));

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
				// updateDoc();

				// SET HIDDEN
			}
		});
		tabPanels[0].add(hiddenCheck);
		tabPanels[0].setAlignmentX(Component.LEFT_ALIGNMENT);

		// Tab 2
		JLabel esa = new JLabel("Element Specific Attributes");
		esa.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		esa.setFont(new Font("Arial", Font.BOLD, 15));
		tabPanels[1].add(esa);

		try {
			JSONArray attrObj;
			JSONObject object = new JSONObject(doc.toJson());
			fullHTML = object.get("tag").toString();

			// Database may return weird tags such as <img></img>.
			// parsing to jsoup elements and then back to string will fix the tags to be
			// appropriate
			el = createJsoupElement();
			fullHTML = element.toString();
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

		// Tab 3
		JLabel cssLabel = new JLabel("Style Properties");
		elementNoTags = html.substring(1, html.length() - 1);
		cssLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		cssLabel.setFont(new Font("Arial", Font.BOLD, 15));
		tabPanels[2].add(cssLabel);
		ArrayList<String> cssFiles = new ArrayList<String>();
		// https://developerlife.com/2008/01/28/swingx-tutorial-task-pane/
		// http://www.csscompiler.com/wp-content/uploads/2013/04/css_wizard.jpg
		Elements links = HTMLDocReader.tempDoc.head().select("link[href]");
		for (int i = 0; i < links.size(); i++) {
			Element e = links.get(i);
			if (e.attr("href").equals("webViewCSS/webViewHighlighter.css")) {
				// System.out.println("Ignoring webViewHighlighter CSS");
			} else {
				String linkHref = Main.tempDir + "/" + e.attr("href");
				cssFiles.add(e.attr("href"));
			}

		}

		String[] cssFilesString = cssFiles.toArray(new String[0]);
		selectedCSSFile = cssFilesString[0];

		tempCSSFile = new File(Main.tempDir + "\\HTMLTempCSS.css");
		tempCSSFile.deleteOnExit();
		tempCSSFilePath = tempCSSFile.getAbsolutePath();

		try {
			String tempCSS = FileUtils.readFileToString(new File(Main.tempDir + "\\" + selectedCSSFile), "UTF-8");
			FileUtils.write(tempCSSFile, tempCSS, "UTF-8");
			InputSource inputSource = new InputSource(
					new StringReader(FileUtils.readFileToString(tempCSSFile, "UTF-8")));
			CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
			stylesheet = (CSSStyleSheetImpl) parser.parseStyleSheet(inputSource, null, null);
		} catch (IOException e1) {

		}

		JComboBox<String> cssFilesComboBox = new JComboBox<String>(cssFilesString);
		cssFilesComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				selectedCSSFile = (String) cssFilesComboBox.getSelectedItem();
				try {
					tempCSSFile.delete();
					tempCSSFile = new File(Main.tempDir + "\\HTMLTempCSS.css");
					tempCSSFile.deleteOnExit();
					tempCSSFilePath = tempCSSFile.getAbsolutePath();
					String tempCSS = FileUtils.readFileToString(new File(Main.tempDir + "\\" + selectedCSSFile),
							"UTF-8");
					FileUtils.write(tempCSSFile, tempCSS, "UTF-8");
					InputSource inputSource = new InputSource(new StringReader(
							FileUtils.readFileToString(new File(Main.tempDir + "\\" + selectedCSSFile), "UTF-8")));
					CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
					stylesheet = (CSSStyleSheetImpl) parser.parseStyleSheet(inputSource, null, null);
					textArea.setText(getRule(elementNoTags).toString());
				} catch (IOException e) {

				}

			}
		});

		// System.out.println("Rules for :" + elementNoTags + " = " +
		// getRule(elementNoTags));
		if (getRule(elementNoTags) == null || getRule(elementNoTags).toString().equals("")) {
			// System.out.println("NO CSS RULES FOR ELEMENT " + elementNoTags + " WERE
			// FOUND");
			String cssStr = FileUtils.readFileToString(tempCSSFile, "UTF-8");
			cssStr += "\n" + elementNoTags + "{\n\n}";
			FileUtils.write(tempCSSFile, cssStr, "UTF-8");
			InputSource inputSource = new InputSource(
					new StringReader(FileUtils.readFileToString(tempCSSFile, "UTF-8")));
			CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
			stylesheet = (CSSStyleSheetImpl) parser.parseStyleSheet(inputSource, null, null);
		}

		tabPanels[2].add(cssFilesComboBox);

		changeUIdefaults();

		JXTaskPaneContainer container = new JXTaskPaneContainer();

		ArrayList<JXTaskPane> taskPanes = new ArrayList<JXTaskPane>();

		JXTaskPane textPropertiesTaskPane = new JXTaskPane();

		JTextField colorValue = new JTextField("");
		textPropertiesTaskPane.setTitle("Text Properties");
		taskPanes.add(textPropertiesTaskPane);
		JPanel textColorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
		textColorPanel.add(new JLabel("Text Color"));
		JCheckBox colorCheck = new JCheckBox("");
		colorCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (colorCheck.isSelected() && !colorValue.getText().equals("")) {
					writeCssProperty(elementNoTags, "color", colorValue.getText());
				}
				if (!colorCheck.isSelected()) {
					removeCSSProperty(elementNoTags, "color");
				}
			}
		});
		textColorPanel.add(colorCheck);
		colorValue.setPreferredSize(d);
		textColorPanel.add(colorValue);
		JButton textColorButton = new JButton("Choose Color");
		textColorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color c = JColorChooser.showDialog(null, "Choose a Color", Color.BLACK);
				if (c != null) {
					colorValue.setText(String.format("#%06x", c.getRGB() & 0x00FFFFFF));
					if (colorCheck.isSelected()) {
						writeCssProperty(elementNoTags, "color", colorValue.getText());
					}
				}
			}
		});
		textColorPanel.add(textColorButton);

		JPanel textAlignPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
		String[] textAlignOptions = { "left", "right", "center", "justify", "initial", "inherit" };

		textAlignPanel.add(new JLabel("Text-align"));

		JCheckBox textAlignCheck = new JCheckBox("");
		JComboBox<String> alignComboBox = new JComboBox<String>(textAlignOptions);

		alignComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (textAlignCheck.isSelected()) {
					writeCssProperty(elementNoTags, "text-align", alignComboBox.getSelectedItem().toString());
				}
				if (!textAlignCheck.isSelected()) {
					return;
				}
			}

		});

		textAlignCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (textAlignCheck.isSelected()) {
					writeCssProperty(elementNoTags, "text-align", alignComboBox.getSelectedItem().toString());
				}
				if (!textAlignCheck.isSelected()) {
					removeCSSProperty(elementNoTags, "text-align");
				}
			}
		});

		textAlignPanel.add(textAlignCheck);
		textAlignPanel.add(alignComboBox);

		JPanel textTransformPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
		String[] textTransformOptions = { "none", "capitalize", "uppercase", "lowercase", "initial", "inherit" };

		textTransformPanel.add(new JLabel("Text-transform"));

		JCheckBox transformCheck = new JCheckBox("");
		JComboBox<String> transformComboBox = new JComboBox<String>(textTransformOptions);

		transformComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (transformCheck.isSelected()) {
					writeCssProperty(elementNoTags, "text-transform", transformComboBox.getSelectedItem().toString());
				}
				if (!transformCheck.isSelected()) {
					return;
				}
			}

		});

		transformCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (transformCheck.isSelected()) {
					writeCssProperty(elementNoTags, "text-transform", transformComboBox.getSelectedItem().toString());
				}
				if (!transformCheck.isSelected()) {
					removeCSSProperty(elementNoTags, "text-transform");
				}
			}
		});

		textTransformPanel.add(transformCheck);
		textTransformPanel.add(transformComboBox);

		textPropertiesTaskPane.add(textColorPanel);
		textPropertiesTaskPane.add(textAlignPanel);
		textPropertiesTaskPane.add(textTransformPanel);

		JXTaskPane fontPropertiesTaskPane = new JXTaskPane();
		fontPropertiesTaskPane.setTitle("Font Properties");
		taskPanes.add(fontPropertiesTaskPane);

		JPanel fontFamilyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));

		JPanel fontSizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
		String[] fontSizes = { "px", "em", "%" };
		fontSizePanel.add(new JLabel("Font-size"));

		JCheckBox fontSizeCheck = new JCheckBox("");
		JComboBox<String> fontSizeComboBox = new JComboBox<String>(fontSizes);
		JTextField fontSizeTextField = new JTextField("");
		fontSizeTextField.setPreferredSize(d);

		fontSizeCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (fontSizeCheck.isSelected()) {
					writeCssProperty(elementNoTags, "font-size",
							fontSizeTextField.getText() + fontSizeComboBox.getSelectedItem().toString());
				}
				if (!fontSizeCheck.isSelected()) {
					removeCSSProperty(elementNoTags, "font-size");
				}
			}
		});

		fontSizeTextField.getDocument().addDocumentListener(new DocumentListener() {
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

			public void update() {
				if (fontSizeCheck.isSelected()) {
					if (fontSizeTextField.getText().equals("") || fontSizeTextField.getText() == null) {
						removeCSSProperty(elementNoTags, "font-size");
					} else {
						writeCssProperty(elementNoTags, "font-size",
								fontSizeTextField.getText() + fontSizeComboBox.getSelectedItem().toString());
					}
				}
			}

		});

		fontSizeComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (fontSizeCheck.isSelected()) {
					writeCssProperty(elementNoTags, "font-size",
							fontSizeTextField.getText() + fontSizeComboBox.getSelectedItem().toString());
				}
				if (!fontSizeCheck.isSelected()) {
					return;
				}
			}

		});

		fontSizePanel.add(fontSizeCheck);
		fontSizePanel.add(fontSizeTextField);
		fontSizePanel.add(fontSizeComboBox);

		JPanel fontStylePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
		String[] fontStyles = { "normal", "italic", "oblique", "initial", "inherit" };

		fontStylePanel.add(new JLabel("Font Style"));
		JCheckBox fontStyleCheck = new JCheckBox("");
		JComboBox<String> fontStyleComboBox = new JComboBox<String>(fontStyles);

		fontStyleCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (fontStyleCheck.isSelected()) {
					writeCssProperty(elementNoTags, "font-style", fontStyleComboBox.getSelectedItem().toString());
				}
				if (!fontStyleCheck.isSelected()) {
					removeCSSProperty(elementNoTags, "font-style");
				}
			}
		});
		fontStyleComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (fontStyleCheck.isSelected()) {
					writeCssProperty(elementNoTags, "font-style", fontStyleComboBox.getSelectedItem().toString());
				}
				if (!fontStyleCheck.isSelected()) {
					return;
				}
			}

		});

		fontStylePanel.add(fontStyleCheck);
		fontStylePanel.add(fontStyleComboBox);

		JPanel fontWeightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
		String[] fontWeights = { "normal", "bold", "bolder", "lighter", "100", "200", "300", "400", "500", "600", "700",
				"800", "900", "initial", "inherit" };

		fontWeightPanel.add(new JLabel("Font Style"));
		JCheckBox fontWeightCheck = new JCheckBox("");
		JComboBox<String> fontWeightComboBox = new JComboBox<String>(fontWeights);

		fontWeightCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (fontWeightCheck.isSelected()) {
					writeCssProperty(elementNoTags, "font-weight", fontWeightComboBox.getSelectedItem().toString());
				}
				if (!fontWeightCheck.isSelected()) {
					removeCSSProperty(elementNoTags, "font-weight");
				}
			}
		});
		fontWeightComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (fontWeightCheck.isSelected()) {
					writeCssProperty(elementNoTags, "font-weight", fontWeightComboBox.getSelectedItem().toString());
				}
				if (!fontWeightCheck.isSelected()) {
					return;
				}
			}

		});
		fontStylePanel.add(fontWeightCheck);
		fontStylePanel.add(fontWeightComboBox);

		fontPropertiesTaskPane.add(fontFamilyPanel);
		fontPropertiesTaskPane.add(fontSizePanel);
		fontPropertiesTaskPane.add(fontStylePanel);
		fontPropertiesTaskPane.add(fontWeightPanel);

		JXTaskPane texTDecorationropertiesTaskPane = new JXTaskPane();
		texTDecorationropertiesTaskPane.setTitle("Text Decoration Properties");
		taskPanes.add(texTDecorationropertiesTaskPane);

		JXTaskPane backgroundandBorderPropertiesTaskPane = new JXTaskPane();
		backgroundandBorderPropertiesTaskPane.setTitle("Background and Border Properties");
		taskPanes.add(backgroundandBorderPropertiesTaskPane);

		JXTaskPane basicBoxPropertiesTaskPane = new JXTaskPane();
		basicBoxPropertiesTaskPane.setTitle("Basic Box Properties");
		taskPanes.add(basicBoxPropertiesTaskPane);

		JXTaskPane flexibleBoxLayoutTaskPane = new JXTaskPane();
		flexibleBoxLayoutTaskPane.setTitle("Flexible Box Layout");
		taskPanes.add(flexibleBoxLayoutTaskPane);

		JXTaskPane writingModesPropertiesTaskPane = new JXTaskPane();
		writingModesPropertiesTaskPane.setTitle("Writing Modes Properties");
		taskPanes.add(writingModesPropertiesTaskPane);

		JXTaskPane tablePropertiesTaskPane = new JXTaskPane();
		tablePropertiesTaskPane.setTitle("Table Properties");
		taskPanes.add(tablePropertiesTaskPane);

		JXTaskPane listsandCountersPropertiesTaskPane = new JXTaskPane();
		listsandCountersPropertiesTaskPane.setTitle("Lists And Counters Properties");
		taskPanes.add(listsandCountersPropertiesTaskPane);

		JXTaskPane animationPropertiesTaskPane = new JXTaskPane();
		animationPropertiesTaskPane.setTitle("Animation Properties");
		taskPanes.add(animationPropertiesTaskPane);

		JXTaskPane transformPropertiesTaskPane = new JXTaskPane();
		transformPropertiesTaskPane.setTitle("Transform Properties");
		taskPanes.add(transformPropertiesTaskPane);

		JXTaskPane transitionsPropertiesTaskPane = new JXTaskPane();
		transitionsPropertiesTaskPane.setTitle("Transition Properties");
		taskPanes.add(transitionsPropertiesTaskPane);

		JXTaskPane basicUserInterfacePropertiesTaskPane = new JXTaskPane();
		basicUserInterfacePropertiesTaskPane.setTitle("Basic User Interface Properties");
		taskPanes.add(basicUserInterfacePropertiesTaskPane);

		JXTaskPane multicolumnLayoutPropertiesTaskPane = new JXTaskPane();
		multicolumnLayoutPropertiesTaskPane.setTitle("Multicolumn Layout Properties");
		taskPanes.add(multicolumnLayoutPropertiesTaskPane);

		JXTaskPane pagedMediaTaskPane = new JXTaskPane();
		pagedMediaTaskPane.setTitle("Paged Media Task Pane");
		taskPanes.add(pagedMediaTaskPane);

		JXTaskPane generatedContentforPagedMediaTaskPane = new JXTaskPane();
		generatedContentforPagedMediaTaskPane.setTitle("Generated Content For Paged Media");
		taskPanes.add(generatedContentforPagedMediaTaskPane);

		JXTaskPane filterEffectsPropertiesTaskPane = new JXTaskPane();
		filterEffectsPropertiesTaskPane.setTitle("Filter Effects");
		taskPanes.add(filterEffectsPropertiesTaskPane);

		JXTaskPane imageValuesandReplacedContentTaskPane = new JXTaskPane();
		imageValuesandReplacedContentTaskPane.setTitle("Image values and Replaced Content");
		taskPanes.add(imageValuesandReplacedContentTaskPane);

		JXTaskPane maskingPropertiesTaskPane = new JXTaskPane();
		maskingPropertiesTaskPane.setTitle("MAskin Properties");
		taskPanes.add(maskingPropertiesTaskPane);

		JXTaskPane speechPropertiesTaskPane = new JXTaskPane();
		speechPropertiesTaskPane.setTitle("Speech Properties");
		taskPanes.add(speechPropertiesTaskPane);

		for (JXTaskPane tp : taskPanes) {
			tp.setCollapsed(true);
			container.add(tp);
		}
		taskPanes.get(0).setCollapsed(false);
		tabPanels[2].add(container);

		// taskpane.add(new AbstractAction() {
		// {
		// putValue(Action.NAME, "task pane item 2 : an action");
		// putValue(Action.SHORT_DESCRIPTION, "perform an action");
		//// putValue(Action.SMALL_ICON, Images.NetworkConnected.getIcon(32, 32));
		// }
		//
		// public void actionPerformed(ActionEvent e) {
		// label.setText("an action performed");
		// }
		// });
		// add the task pane to the taskpanecontainer

		Elements styles = HTMLDocReader.tempDoc.select("link[href]");
		org.jsoup.nodes.Document sdoc = Jsoup.parse(fullHTML);
		for (Element e : styles) {

			sdoc.head().append(e.toString());
		}

		try {
			// System.out.println(tempFile);
			tempFile = new File(Main.tempDir + "\\" + "HTMLEditAttributeTemp.html");
			BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
			bw.write(sdoc.toString());
			bw.close();
			tempFile.deleteOnExit();
		} catch (IOException e) {
			return;
		}

		textArea = new JTextArea(fullHTML);
		textArea.setFont(new Font("Arial", Font.BOLD, 15));
		tabbedPane = new JTabbedPane();
		previewPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, fxPanel, textArea);
		previewPane.setDividerLocation((Toolkit.getDefaultToolkit().getScreenSize().height) / 8);
		JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, previewPane, tabbedPane);

		// creat the tabs
		for (int i = 0; i < tabTitles.length; i++) {
			JScrollPane scrollPane = new JScrollPane(tabPanels[i]);
			scrollPane.getVerticalScrollBar().setUnitIncrement(10);
			JComponent c = scrollPane;
			tabbedPane.addTab(tabTitles[i], null, c, tabTitles[i]);
		}

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
					HTMLDocReader.tempDoc.body().append(textArea.getText());
					reader.updateTempDoc();
					Main.updateFrame();
					for (int i = 1; i < Main.elementTree.getRowCount(); i++) {
						Main.elementTree.expandRow(i);
					}
					mongoClient.close();
					FileSaver.unsavedChanges = true;
					dialog.dispose();
				} catch (Exception e) {
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

		for (int i = 0; i < field.size(); i++) {
			field.get(i).getDocument().addDocumentListener(new newElementFieldDocumentListener(i));
		}

		Platform.runLater(new Runnable() { // this will run initFX as JavaFX-Thread
			@Override
			public void run() {
				initFX(fxPanel, tempFile);
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

		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				if (tabbedPane.getSelectedIndex() == 2) {
					// File input = new File(Main.tempDir+"\\"+selectedCSSFile);
					textArea.setText(getRule(elementNoTags).toString());
				}
				if (tabbedPane.getSelectedIndex() != 2) {
					updateDoc();
				}
			}
		});

		dialog.pack();
		mainSplit.setDividerLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 4);
		dialog.setSize((Toolkit.getDefaultToolkit().getScreenSize().width) / 2,
				(Toolkit.getDefaultToolkit().getScreenSize().height) / 2);
		// center the dialog on screen
		dialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - dialog.getWidth() / 2,
				(Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - dialog.getHeight() / 2);
		root.getGlassPane().setCursor(Cursor.getDefaultCursor());

		textArea.setMaximumSize(new Dimension(previewPane.getWidth(), previewPane.getHeight()));
		textArea.setLineWrap(true);
		updateDoc();
		dialog.setVisible(true);

	}

	private void changeUIdefaults() {
		// JXTaskPaneContainer settings (developer defaults)
		/*
		 * These are all the properties that can be set (may change with new version of
		 * SwingX) "TaskPaneContainer.useGradient", "TaskPaneContainer.background",
		 * "TaskPaneContainer.backgroundGradientStart",
		 * "TaskPaneContainer.backgroundGradientEnd", etc.
		 */
		// setting taskpanecontainer defaults
		UIManager.put("TaskPaneContainer.useGradient", Boolean.FALSE);
		UIManager.put("TaskPaneContainer.background", Color.WHITE);
		// setting taskpane defaults
		UIManager.put("TaskPane.font", new FontUIResource(new Font("Verdana", Font.BOLD, 16)));
		UIManager.put("TaskPane.titleBackgroundGradientStart", Color.WHITE);
		UIManager.put("TaskPane.titleBackgroundGradientEnd", Color.LIGHT_GRAY);
	}

	public Painter getPainter() {
		int width = 100;
		int height = 100;
		Color color1 = Color.WHITE;
		Color color2 = Color.WHITE;
		LinearGradientPaint gradientPaint = new LinearGradientPaint(0.0f, 0.0f, width, height,
				new float[] { 0.0f, 1.0f }, new Color[] { color1, color2 });
		MattePainter mattePainter = new MattePainter(gradientPaint);
		return mattePainter;
	}

	// private static String getFullHTML() {
	// return fullHTML;
	// }
	/*
	 * First get a rule by the selector. Note that class selectors need to be
	 * preceeded with the asterisk [*]. So if the rule selector is .myCssClass the
	 * selector argument needs to be *.myCssClass
	 * 
	 * 
	 */
	public static CSSStyleRuleImpl getRule(String selector) {
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
	public Property getProperty(String selector, String property) {
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

	public void writeCssProperty(String selector, String property, String newValue) {
		Property prop = getProperty(selector, property);
		if (prop != null) {
			CSSValueImpl val = new CSSValueImpl();
			val.setCssText(newValue);
			prop.setValue(val);

			CSSFormat format = new CSSFormat();
			format.setRgbAsHex(true);
			File output = new File(Main.tempDir + "\\" + "HTMLTempCSS.css");
			try {
				// FileUtils.writeStringToFile(file, data, encoding);
				FileUtils.writeStringToFile(output, (String) stylesheet.getCssText(format), "UTF-8");
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						updateFX(tempFile.getAbsolutePath());
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
				System.out.println(stylesheet.getCssText(format));
				FileUtils.writeStringToFile(output, (String) stylesheet.getCssText(format), "UTF-8");
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						updateFX(tempFile.getAbsolutePath());
					}
				});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void removeCSSProperty(String selector, String property) {
		Property prop = getProperty(selector, property);
		System.out.println("Removing property:" + prop);
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
						updateFX(tempFile.getAbsolutePath());
					}
				});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void initFX(final JFXPanel fxPanel, File f) {
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

	private static void updateFX(String url) {
		File f;
		webView.setPrefSize(previewPane.getSize().width, previewPane.getSize().height);
		if (url == null || url.equals("") || url.equals(null)) {
			webEngine.load("htt://www.google.com");
		} else {
			f = new File(url);
			try {
				webEngine.load(f.toURI().toString());
				webEngine.reload();
				if (tabbedPane.getSelectedIndex() == 2) {
					textArea.setText(getRule(elementNoTags).toString());
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

		// Elements styles = HTMLDocReader.tempDoc.select("link[href]");
		// org.jsoup.nodes.Document doc = Jsoup.parse(fullHTML);
		// for (Element s : styles) {
		// doc.head().append(s.outerHtml());
		// }
		org.jsoup.nodes.Document doc = Jsoup.parse(fullHTML);
		doc.head().append("<link rel=\"stylesheet\" href=\"HTMLTempCSS.css\">");
		try {
			// tempFile.delete();
			// tempFile = File.createTempFile("HTMLEditAttributeTemp", ".html");
			// tempFile.deleteOnExit();
		} catch (Exception e) {

		}
		try {
			// System.out.println(tempFile);
			BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
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
				updateFX(tempFile.getAbsolutePath());
			}
		});
	}

	private Element createJsoupElement() {
		element = Jsoup.parseBodyFragment(fullHTML);
		String elementNoTags = html.substring(1, html.length() - 1);
		Elements elementSelector = element.select(elementNoTags);
		Element el = elementSelector.first();
		updateDoc();
		return el;
	}

	public static Element getJsoupElement() {
		return el;
	}

}
