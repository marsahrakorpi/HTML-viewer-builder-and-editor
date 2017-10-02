package dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.FontUIResource;

import org.apache.commons.io.FileUtils;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;

import com.steadystate.css.dom.CSSRuleListImpl;
import com.steadystate.css.dom.CSSStyleDeclarationImpl;
import com.steadystate.css.dom.CSSStyleRuleImpl;
import com.steadystate.css.dom.CSSStyleSheetImpl;
import com.steadystate.css.dom.Property;

import engine.Main;
import javafx.application.Platform;

public class CSSStylePanels {
	JXTaskPaneContainer container;
	String cssSelector;
	static CSSStyleSheetImpl stylesheet;
	File tempHTMLFile;
	File tempCSSFile;
	String elementNoTags;
	Dimension d;
	JSplitPane previewPane;
	JTextArea textArea;
	JTabbedPane tabbedPane;
	String className;
	String cssString;

	public ArrayList<String> properties, values;

	public CSSStylePanels(String cssSelector, CSSStyleSheetImpl stylesheet, File tempHTMLFile, File tempCSSFile,
			String elementNoTags, Dimension d, JTextArea textArea, JTabbedPane tabbedPane, String className) {

		this.cssSelector = cssSelector;
		CSSStylePanels.stylesheet = stylesheet;
		this.tempHTMLFile = tempHTMLFile;
		this.tempCSSFile = tempCSSFile;
		this.elementNoTags = elementNoTags;
		this.d = d;
		this.textArea = textArea;
		this.tabbedPane = tabbedPane;
		this.className = className;
		properties = new ArrayList<String>();
		values = new ArrayList<String>();

		System.out.println("CSSSTyle constructor");

		int flowLayoutWidth = 5;
		int flowLayoutHeight = 5;

		changeUIdefaults();
		container = new JXTaskPaneContainer();

		ArrayList<JXTaskPane> taskPanes = new ArrayList<JXTaskPane>();

		JXTaskPane textPropertiesTaskPane = new JXTaskPane();

		JTextField colorValue = new JTextField("");
		textPropertiesTaskPane.setTitle("Text Properties");
		taskPanes.add(textPropertiesTaskPane);
		JPanel textColorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		textColorPanel.add(new JLabel("Text Color"));
		JCheckBox colorCheck = new JCheckBox("");
		colorCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (colorCheck.isSelected() && !colorValue.getText().equals("")) {
					writeCssProperty(getCssSelector(), "color", colorValue.getText());
				}
				if (!colorCheck.isSelected()) {
					removeCSSProperty(getCssSelector(), "color");
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
						writeCssProperty(getCssSelector(), "color", colorValue.getText());
					}
				}
			}
		});
		colorValue.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				update();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				update();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				update();
			}

			private void update() {
				if (colorCheck.isSelected() && !colorValue.getText().equals("")) {
					writeCssProperty(getCssSelector(), "color", colorValue.getText());
				}
			}

		});
		textColorPanel.add(textColorButton);

		// CHECKS
		if (getProperty(cssSelector, "color") != null) {
			colorCheck.setSelected(true);
			colorValue.setText(getProperty(getCssSelector(), "color").getValue().toString());
		}

		JPanel textAlignPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		String[] textAlignOptions = { "left", "right", "center", "justify", "initial", "inherit" };

		textAlignPanel.add(new JLabel("Text Align"));

		JCheckBox textAlignCheck = new JCheckBox("");
		JComboBox<String> alignComboBox = new JComboBox<String>(textAlignOptions);

		textAlignCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (textAlignCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "text-align", alignComboBox.getSelectedItem().toString());
				}
				if (!textAlignCheck.isSelected()) {
					removeCSSProperty(getCssSelector(), "text-align");
				}
			}
		});

		alignComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (textAlignCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "text-align", alignComboBox.getSelectedItem().toString());
				}
				if (!textAlignCheck.isSelected()) {
					return;
				}
			}

		});

		textAlignPanel.add(textAlignCheck);
		textAlignPanel.add(alignComboBox);

		// CHECKS
		if (getProperty(cssSelector, "text-align") != null) {
			textAlignCheck.setSelected(true);
			alignComboBox.setSelectedItem(getProperty(getCssSelector(), "text-align").getValue().toString());
		}

		JPanel textTransformPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		String[] textTransformOptions = { "none", "capitalize", "uppercase", "lowercase", "initial", "inherit" };

		textTransformPanel.add(new JLabel("Text	Transform"));

		JCheckBox transformCheck = new JCheckBox("");
		JComboBox<String> transformComboBox = new JComboBox<String>(textTransformOptions);

		transformCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (transformCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "text-transform",
							transformComboBox.getSelectedItem().toString());
				}
				if (!transformCheck.isSelected()) {
					removeCSSProperty(getCssSelector(), "text-transform");
				}
			}
		});

		transformComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (transformCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "text-transform",
							transformComboBox.getSelectedItem().toString());
				}
				if (!transformCheck.isSelected()) {
					return;
				}
			}

		});

		textTransformPanel.add(transformCheck);
		textTransformPanel.add(transformComboBox);

		// CHECKS
		if (getProperty(cssSelector, "text-transform") != null) {
			transformCheck.setSelected(true);
			transformComboBox.setSelectedItem(getProperty(getCssSelector(), "text-align").getValue().toString());
		}

		// MASTER
		textPropertiesTaskPane.add(textColorPanel);
		textPropertiesTaskPane.add(textAlignPanel);
		textPropertiesTaskPane.add(textTransformPanel);

		JXTaskPane fontPropertiesTaskPane = new JXTaskPane();
		fontPropertiesTaskPane.setTitle("Font Properties");
		taskPanes.add(fontPropertiesTaskPane);

		JPanel fontFamilyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		String[] fontFamilies = { "Arial Black", "Book Antiqua", "Comic Sans MS", "Courier New", "Courier New",
				"Lucida Grande", "Lucida Sans Unicode", "Palatino Linotype", "Times New Roman", "Trebuchet MS" };
		fontFamilyPanel.add(new JLabel("Font Family"));

		JCheckBox fontFamilyCheck = new JCheckBox("");
		JComboBox<String> fontFamilyCComboBox = new JComboBox<String>(fontFamilies);

		fontFamilyCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (fontFamilyCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "font-family",
							fontFamilyCComboBox.getSelectedItem().toString());
				}
				if (!fontFamilyCheck.isSelected()) {
					removeCSSProperty(getCssSelector(), "font-family");
				}
			}
		});
		fontFamilyCComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (fontFamilyCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "font-family",
							fontFamilyCComboBox.getSelectedItem().toString());
				}
				if (!fontFamilyCheck.isSelected()) {
					return;
				}
			}

		});
		fontFamilyPanel.add(fontFamilyCheck);
		fontFamilyPanel.add(fontFamilyCComboBox);
		if (getProperty(cssSelector, "font-weight") != null) {
			fontFamilyCheck.setSelected(true);
			fontFamilyCComboBox.setSelectedItem(getProperty(getCssSelector(), "font-family").getValue().toString());
		}

		JPanel fontSizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		String[] fontSizes = { "px", "em", "%" };
		fontSizePanel.add(new JLabel("Font Size"));

		JCheckBox fontSizeCheck = new JCheckBox("");
		JComboBox<String> fontSizeComboBox = new JComboBox<String>(fontSizes);
		JTextField fontSizeTextField = new JTextField("");
		fontSizeTextField.setPreferredSize(d);

		fontSizeCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (fontSizeCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "font-size",
							fontSizeTextField.getText() + fontSizeComboBox.getSelectedItem().toString());
				}
				if (!fontSizeCheck.isSelected()) {
					removeCSSProperty(getCssSelector(), "font-size");
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
						removeCSSProperty(getCssSelector(), "font-size");
					} else {
						writeCssProperty(getCssSelector(), "font-size",
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
					writeCssProperty(getCssSelector(), "font-size",
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

		// CHECKS
		if (getProperty(cssSelector, "font-size") != null) {
			fontSizeCheck.setSelected(true);
			String fontSizeValue = getProperty(getCssSelector(), "font-size").getValue().toString();
			String numVal = fontSizeValue.replaceAll("[^0-9]", "");
			String unit = fontSizeValue.replaceAll("[0-9]", "");
			fontSizeTextField.setText(numVal);
			fontSizeComboBox.setSelectedItem(unit);
		}

		JPanel fontStylePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		String[] fontStyles = { "normal", "italic", "oblique", "initial", "inherit" };

		fontStylePanel.add(new JLabel("Font Style"));
		JCheckBox fontStyleCheck = new JCheckBox("");
		JComboBox<String> fontStyleComboBox = new JComboBox<String>(fontStyles);

		fontStyleCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (fontStyleCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "font-style", fontStyleComboBox.getSelectedItem().toString());
				}
				if (!fontStyleCheck.isSelected()) {
					removeCSSProperty(getCssSelector(), "font-style");
				}
			}
		});
		fontStyleComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (fontStyleCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "font-style", fontStyleComboBox.getSelectedItem().toString());
				}
				if (!fontStyleCheck.isSelected()) {
					return;
				}
			}

		});
		fontStylePanel.add(fontStyleCheck);
		fontStylePanel.add(fontStyleComboBox);
		// CHECKS
		if (getProperty(cssSelector, "font-style") != null) {
			fontStyleCheck.setSelected(true);
			fontStyleComboBox.setSelectedItem(getProperty(getCssSelector(), "font-style").getValue().toString());
		}

		JPanel fontWeightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		String[] fontWeights = { "normal", "bold", "bolder", "lighter", "100", "200", "300", "400", "500", "600", "700",
				"800", "900", "initial", "inherit" };

		fontWeightPanel.add(new JLabel("Font Weight"));
		JCheckBox fontWeightCheck = new JCheckBox("");
		JComboBox<String> fontWeightComboBox = new JComboBox<String>(fontWeights);

		fontWeightCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (fontWeightCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "font-weight", fontWeightComboBox.getSelectedItem().toString());
				}
				if (!fontWeightCheck.isSelected()) {
					removeCSSProperty(getCssSelector(), "font-weight");
				}
			}
		});
		fontWeightComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (fontWeightCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "font-weight", fontWeightComboBox.getSelectedItem().toString());
				}
				if (!fontWeightCheck.isSelected()) {
					return;
				}
			}

		});
		fontWeightPanel.add(fontWeightCheck);
		fontWeightPanel.add(fontWeightComboBox);
		// CHECKS
		if (getProperty(cssSelector, "font-weight") != null) {
			fontWeightCheck.setSelected(true);
			fontWeightComboBox.setSelectedItem(getProperty(getCssSelector(), "font-weight").getValue().toString());
		}

		// MASTERS
		fontPropertiesTaskPane.add(fontFamilyPanel);
		fontPropertiesTaskPane.add(fontSizePanel);
		fontPropertiesTaskPane.add(fontStylePanel);
		fontPropertiesTaskPane.add(fontWeightPanel);

		JXTaskPane texTDecorationropertiesTaskPane = new JXTaskPane();
		texTDecorationropertiesTaskPane.setTitle("Text Decoration Properties");

		JPanel textDecorationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		String[] textDecorations = { "none", "underline", "overline", "line-through", "initial", "inherit" };

		textDecorationPanel.add(new JLabel("Text-decoration"));
		JCheckBox textDecorationCheck = new JCheckBox("");
		JComboBox<String> textDecorationComboBox = new JComboBox<String>(textDecorations);

		textDecorationCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (textDecorationCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "text-decoration",
							textDecorationComboBox.getSelectedItem().toString());
				}
				if (!textDecorationCheck.isSelected()) {
					removeCSSProperty(getCssSelector(), "font-weight");
				}
			}
		});
		textDecorationComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (textDecorationCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "text-decoration",
							textDecorationComboBox.getSelectedItem().toString());
				}
				if (!textDecorationCheck.isSelected()) {
					return;
				}
			}

		});
		textDecorationPanel.add(textDecorationCheck);
		textDecorationPanel.add(textDecorationComboBox);

		JPanel textDecorationColorPanel = new JPanel(
				new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		JTextField textDecorationColorValue = new JTextField("");

		textDecorationColorPanel.add(new JLabel("Text Decoration Color"));
		JCheckBox textDecorationColorCheck = new JCheckBox("");
		textDecorationColorCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (textDecorationColorCheck.isSelected() && !textDecorationColorCheck.getText().equals("")) {
					writeCssProperty(getCssSelector(), "text-decoration-color", textDecorationColorValue.getText());
				}
				if (!textDecorationColorCheck.isSelected()) {
					removeCSSProperty(getCssSelector(), "text-decoration-color");
				}
			}
		});
		textDecorationColorPanel.add(textDecorationColorCheck);
		textDecorationColorValue.setPreferredSize(d);
		textDecorationColorPanel.add(textDecorationColorValue);
		JButton textDecorationColorButton = new JButton("Choose Color");
		textDecorationColorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				Color d = JColorChooser.showDialog(null, "Choose a Color", Color.BLACK);
				if (d != null) {
					textDecorationColorValue.setText(String.format("#%06x", d.getRGB() & 0x00FFFFFF));
					if (textDecorationColorCheck.isSelected()) {
						writeCssProperty(getCssSelector(), "text-decoration-color", textDecorationColorValue.getText());
					}
				}
			}
		});
		textDecorationColorValue.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				update();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				update();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				update();
			}

			private void update() {
				if (textDecorationColorCheck.isSelected() && !textDecorationColorValue.getText().equals("")) {
					writeCssProperty(getCssSelector(), "text-decoration-color", textDecorationColorValue.getText());
				}
			}

		});

		textDecorationColorPanel.add(textDecorationColorButton);

		// CHECKS
		if (getProperty(cssSelector, "text-decoration") != null) {
			textDecorationColorCheck.setSelected(true);
			textDecorationColorValue
					.setText(getProperty(getCssSelector(), "text-decoration-color").getValue().toString());
		}

		JPanel textDecorationStylePanel = new JPanel(
				new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		String[] textDecorationStyles = { "solid", "double", "dotted", "dashed", "wavy", "initial", "inherit" };

		textDecorationStylePanel.add(new JLabel("Text Decoration Style"));
		JCheckBox textDecorationStylesCheck = new JCheckBox("");
		JComboBox<String> textDecoratioStylesComboBox = new JComboBox<String>(textDecorationStyles);

		textDecorationStylesCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (textDecorationStylesCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "text-decoration-style",
							textDecoratioStylesComboBox.getSelectedItem().toString());
				}
				if (!textDecorationStylesCheck.isSelected()) {
					removeCSSProperty(getCssSelector(), "font-weight");
				}
			}
		});
		textDecoratioStylesComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (textDecorationStylesCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "text-decoration-style",
							textDecoratioStylesComboBox.getSelectedItem().toString());
				}
				if (!textDecorationStylesCheck.isSelected()) {
					return;
				}
			}

		});
		textDecorationStylePanel.add(textDecorationStylesCheck);
		textDecorationStylePanel.add(textDecoratioStylesComboBox);
		// CHECKS
		if (getProperty(cssSelector, "ttext-decoration-style") != null) {
			textDecorationStylesCheck.setSelected(true);
			textDecoratioStylesComboBox
					.setSelectedItem(getProperty(getCssSelector(), "font-weight").getValue().toString());
		}

		// MASTERS
		texTDecorationropertiesTaskPane.add(textDecorationPanel);
		texTDecorationropertiesTaskPane.add(textDecorationColorPanel);
		texTDecorationropertiesTaskPane.add(textDecorationStylePanel);

		taskPanes.add(texTDecorationropertiesTaskPane);

		JXTaskPane backgroundPropertiesTaskPane = new JXTaskPane();
		backgroundPropertiesTaskPane.setTitle("Background Properties");
		taskPanes.add(backgroundPropertiesTaskPane);
		
		JPanel backgroundColorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		JTextField backgroundColorValue = new JTextField("");
		backgroundColorPanel.add(new JLabel("Background Color"));
		JCheckBox backgroundColorCheck = new JCheckBox("");
		backgroundColorCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (backgroundColorCheck.isSelected() && !backgroundColorValue.getText().equals("")) {
					writeCssProperty(getCssSelector(), "background-color", backgroundColorValue.getText());
				}
				if (!backgroundColorCheck.isSelected()) {
					removeCSSProperty(getCssSelector(), "color");
				}
			}
		});
		backgroundColorPanel.add(backgroundColorCheck);
		backgroundColorValue.setPreferredSize(d);
		backgroundColorPanel.add(backgroundColorValue);
		JButton backgroundColorButton = new JButton("Choose Color");
		backgroundColorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				Color c = JColorChooser.showDialog(null, "Choose a Color", Color.BLACK);
				if (c != null) {
					backgroundColorValue.setText(String.format("#%06x", c.getRGB() & 0x00FFFFFF));
					if (backgroundColorCheck.isSelected()) {
						writeCssProperty(getCssSelector(), "background-color", backgroundColorValue.getText());
					}
				}
			}
		});
		backgroundColorValue.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				update();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				update();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				update();
			}

			private void update() {
				if (backgroundColorCheck.isSelected() && !backgroundColorValue.getText().equals("")) {
					writeCssProperty(getCssSelector(), "background-color", backgroundColorValue.getText());
				}
			}

		});
		backgroundColorPanel.add(backgroundColorButton);

		// CHECKS
		if (getProperty(cssSelector, "background-color") != null) {
			backgroundColorCheck.setSelected(true);
			backgroundColorValue.setText(getProperty(getCssSelector(), "background-color").getValue().toString());
		}
		
		
		JPanel backgroundImagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		JTextField backgroundImageValue = new JTextField("");
		backgroundImagePanel.add(new JLabel("Background Image"));
		JCheckBox backgroundImageCheck = new JCheckBox("");
		
		backgroundImageCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (backgroundImageCheck.isSelected() && !backgroundImageValue.getText().equals("")) {
					writeCssProperty(getCssSelector(), "background-image", backgroundImageValue.getText());
				}
				if (!backgroundImageCheck.isSelected()) {
					removeCSSProperty(getCssSelector(), "background-image");
				}
			}
		});
		backgroundImagePanel.add(backgroundImageCheck);
		backgroundImageValue.setPreferredSize(d);
		backgroundImagePanel.add(backgroundImageValue);
		
		//UPLOAD URL
		JButton backgroundImageButton = new JButton("Upload Image");
		backgroundImageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//UPLOAD CODE HERE
			}
		});
		backgroundImageValue.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				update();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				update();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				update();
			}

			private void update() {
				if (backgroundImageCheck.isSelected() && !backgroundImageValue.getText().equals("")) {
					writeCssProperty(getCssSelector(), "background-image", backgroundImageValue.getText());
				}
			}

		});
		backgroundImagePanel.add(backgroundImageButton);
		// CHECKS
		if (getProperty(cssSelector, "background-image") != null) {
			backgroundImageCheck.setSelected(true);
			backgroundImageValue.setText(getProperty(getCssSelector(),"background-image").getValue().toString());
		}
		
		//MASTERS
		backgroundPropertiesTaskPane.add(backgroundColorPanel);
		backgroundPropertiesTaskPane.add(backgroundImagePanel);
		
		JXTaskPane borderPropertiesTaskPane = new JXTaskPane();
		borderPropertiesTaskPane.setTitle("Border Properties");
		taskPanes.add(borderPropertiesTaskPane);

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
		maskingPropertiesTaskPane.setTitle("Masking Properties");
		taskPanes.add(maskingPropertiesTaskPane);

		JXTaskPane speechPropertiesTaskPane = new JXTaskPane();
		speechPropertiesTaskPane.setTitle("Speech Properties");
		taskPanes.add(speechPropertiesTaskPane);

		for (JXTaskPane tp : taskPanes) {
			tp.setCollapsed(true);
			container.add(tp);
		}
		taskPanes.get(0).setCollapsed(false);

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
		// removeCSSProperty(selector, property);

		boolean hasMatch = false;
		int index = 0;
		for (int i = 0; i < properties.size(); i++) {
			if (properties.get(i).equals(property)) {
				index = i;
				hasMatch = true;
			}
		}

		if (hasMatch) {
			values.set(index, newValue);
		} else {
			properties.add(property);
			values.add(newValue);
		}

		CSSStyleRuleImpl r = getRule(selector);

		cssString = selector + "{\n";
		for (int j = 0; j < properties.size(); j++) {
			cssString += properties.get(j) + ": " + values.get(j) + ";\n";
		}
		cssString += "}";

		File output = new File(Main.tempDir + "\\" + "HTMLTempCSS.css");
		try {
			// FileUtils.writeStringToFile(file, data, encoding);
			FileUtils.writeStringToFile(output, cssString, "UTF-8");
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

	private void removeCSSProperty(String selector, String property) {
		for (int i = 0; i < properties.size(); i++) {
			if (properties.get(i).equals(property)) {
				properties.remove(i);
				values.remove(i);
			}
		}
		cssString = selector + "{\n";
		for (int j = 0; j < properties.size(); j++) {
			cssString += "      " + properties.get(j) + ": " + values.get(j) + ";\n";
		}
		cssString += "}";
		;
		File output = new File(Main.tempDir + "\\" + "HTMLTempCSS.css");
		try {
			// FileUtils.writeStringToFile(file, data, encoding);
			FileUtils.writeStringToFile(output, cssString, "UTF-8");
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

	private void updateFX(String url) {
		File f = new File(url);
		if (className.equals("EditNewElementDialog")) {
			try {
				EditElementDialog.webView.setPrefSize(EditElementDialog.previewPane.getSize().width,
						EditElementDialog.previewPane.getSize().height);
			} catch (Exception e1) {

			}
			if (url == null || url.equals("") || url.equals(null)) {
				EditElementDialog.webEngine.load("htt://www.google.com");
			} else {
				try {
					EditElementDialog.webEngine.load(f.toURI().toString());
					EditElementDialog.webEngine.reload();
					if (EditElementDialog.tabbedPane.getSelectedIndex() == 2) {
						EditElementDialog.textArea.setText(cssString);
					}
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					return;
				} catch (Exception e) {
					return;
				}
			}
		}

	}

	public JXTaskPaneContainer getContainer() {
		return container;
	}

	public void remove() {
		container.removeAll();
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
		UIManager.put("TaskPaneContainer.background", new Color(240, 240, 240));
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

	public void setCssSelector(String cssSelector) {
		this.cssSelector = cssSelector;
	}

	public String getCssSelector() {
		return this.cssSelector;
	}

	public String getCSSText(String cssSelector) {
		String cssText = cssSelector + "{\n";
		for (int i = 0; i < properties.size(); i++) {
			cssText += properties.get(i) + ": " + values.get(i) + ";\n";
		}
		cssText += "}";
		return cssText;
	}

}
