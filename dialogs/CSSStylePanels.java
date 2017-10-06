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

				Color c = JColorChooser.showDialog(Main.frame, "Choose a Color", Color.BLACK);
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
					writeCssProperty(getCssSelector(), "font-family", fontFamilyCComboBox.getSelectedItem().toString());
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
					writeCssProperty(getCssSelector(), "font-family", fontFamilyCComboBox.getSelectedItem().toString());
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

				Color d = JColorChooser.showDialog(Main.frame, "Choose a Color", Color.BLACK);
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
					removeCSSProperty(getCssSelector(), "background-color");
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

				Color c = JColorChooser.showDialog(Main.frame, "Choose a Color", Color.BLACK);
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

		// UPLOAD URL
		JButton backgroundImageButton = new JButton("Upload Image");
		backgroundImageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// UPLOAD CODE HERE
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
			backgroundImageValue.setText(getProperty(getCssSelector(), "background-image").getValue().toString());
		}

		// MASTERS
		backgroundPropertiesTaskPane.add(backgroundColorPanel);
		backgroundPropertiesTaskPane.add(backgroundImagePanel);

		JXTaskPane borderPropertiesTaskPane = new JXTaskPane();
		borderPropertiesTaskPane.setTitle("Border Properties");

		JPanel borderStylePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		String[] borderStyles = { "hidden", "dotted", "dashed", "solid", "double", "groove", "ridge", "inset", "outset",
				"initial", "inherit" };

		borderStylePanel.add(new JLabel("Border Style"));
		JCheckBox borderStyleCheck = new JCheckBox("");
		JComboBox<String> borderStyleComboBox = new JComboBox<String>(borderStyles);

		borderStyleCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (borderStyleCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "border-style",
							borderStyleComboBox.getSelectedItem().toString());
				}
				if (!borderStyleCheck.isSelected()) {
					removeCSSProperty(getCssSelector(), "border-style");
				}
			}
		});
		borderStyleComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (borderStyleCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "border-style",
							borderStyleComboBox.getSelectedItem().toString());
				}
				if (!borderStyleCheck.isSelected()) {
					return;
				}
			}

		});
		borderStylePanel.add(borderStyleCheck);
		borderStylePanel.add(borderStyleComboBox);

		if (getProperty(cssSelector, "border-style") != null) {
			borderStyleCheck.setSelected(true);
			borderStyleComboBox.setSelectedItem(getProperty(getCssSelector(), "border-style").getValue().toString());
		}

		JPanel borderWidthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		String[] borderWidths = { "medium", "thin", "thick", "px", "em", "%" };
		borderWidthPanel.add(new JLabel("Border Width"));

		JCheckBox borderWidthCheck = new JCheckBox("");
		JComboBox<String> borderWidthComboBox = new JComboBox<String>(borderWidths);
		JTextField borderWidthTextField = new JTextField("");
		borderWidthTextField.setPreferredSize(d);
		borderWidthTextField.setVisible(false);

		borderWidthCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (borderWidthCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "border-width",
							borderWidthTextField.getText() + borderWidthComboBox.getSelectedItem().toString());
				}
				if (!borderWidthCheck.isSelected()) {

					removeCSSProperty(getCssSelector(), "border-width");
				}
			}
		});

		borderWidthTextField.getDocument().addDocumentListener(new DocumentListener() {
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
				if (borderWidthCheck.isSelected()) {
					if (borderWidthTextField.getText().equals("") || borderWidthTextField.getText() == null) {
						removeCSSProperty(getCssSelector(), "border-width");
					} else {
						writeCssProperty(getCssSelector(), "border-width",
								borderWidthTextField.getText() + borderWidthComboBox.getSelectedItem().toString());
					}
				}
			}

		});

		borderWidthComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (borderWidthCheck.isSelected()) {
					System.out.println(borderWidthComboBox.getSelectedItem());
					if (borderWidthComboBox.getSelectedItem().toString().equals("medium")
							|| borderWidthComboBox.getSelectedItem().toString().equals("thin")
							|| borderWidthComboBox.getSelectedItem().toString().equals("thick")) {
						borderWidthTextField.setText("");
						borderWidthTextField.setVisible(false);
					} else {
						System.out.println("Setting visible");
						borderWidthTextField.setVisible(true);
						borderWidthPanel.revalidate();
					}
					writeCssProperty(getCssSelector(), "border-width",
							borderWidthTextField.getText() + borderWidthComboBox.getSelectedItem().toString());
				}
				if (!borderWidthCheck.isSelected()) {
					return;
				}
			}

		});

		borderWidthPanel.add(borderWidthCheck);
		borderWidthPanel.add(borderWidthTextField);
		borderWidthPanel.add(borderWidthComboBox);

		// CHECKS
		if (getProperty(cssSelector, "border-width") != null) {
			borderWidthCheck.setSelected(true);
			String borderWidthValue = getProperty(getCssSelector(), "border-width").getValue().toString();
			if (borderWidthValue.equals("medium") || borderWidthValue.equals("thin")
					|| borderWidthValue.equals("thick")) {
				borderWidthTextField.setVisible(false);
				borderWidthComboBox.setSelectedItem(borderWidthValue);
			} else {
				String numVal = borderWidthValue.replaceAll("[^0-9]", "");
				String unit = borderWidthValue.replaceAll("[0-9]", "");
				borderWidthTextField.setVisible(true);
				borderWidthTextField.setText(numVal);
				borderWidthComboBox.setSelectedItem(unit);
			}
		}

		JPanel borderColorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
		JTextField borderColorValue = new JTextField("");
		borderColorPanel.add(new JLabel("Border Color"));
		JCheckBox borderColorCheck = new JCheckBox("");
		borderColorCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (borderColorCheck.isSelected() && !borderColorValue.getText().equals("")) {
					writeCssProperty(getCssSelector(), "border-color", borderColorValue.getText());
				}
				if (!borderColorCheck.isSelected()) {
					removeCSSProperty(getCssSelector(), "color");
				}
			}
		});
		borderColorPanel.add(borderColorCheck);
		borderColorValue.setPreferredSize(d);
		borderColorPanel.add(borderColorValue);
		JButton borderColorButton = new JButton("Choose Color");
		borderColorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				Color c = JColorChooser.showDialog(Main.frame, "Choose a Color", Color.BLACK);
				if (c != null) {
					borderColorValue.setText(String.format("#%06x", c.getRGB() & 0x00FFFFFF));
					if (borderColorCheck.isSelected()) {
						writeCssProperty(getCssSelector(), "border-color", borderColorValue.getText());
					}
				}
			}
		});
		borderColorValue.getDocument().addDocumentListener(new DocumentListener() {
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
				if (borderColorCheck.isSelected() && !borderColorValue.getText().equals("")) {
					writeCssProperty(getCssSelector(), "border-color", borderColorValue.getText());
				}
			}

		});
		borderColorPanel.add(borderColorButton);

		// CHECKS
		if (getProperty(cssSelector, "border-color") != null) {
			borderColorCheck.setSelected(true);
			borderColorValue.setText(getProperty(getCssSelector(), "border-color").getValue().toString());
		}

		// MASTERS
		borderPropertiesTaskPane.add(borderStylePanel);
		borderPropertiesTaskPane.add(borderWidthPanel);
		borderPropertiesTaskPane.add(borderColorPanel);

		taskPanes.add(borderPropertiesTaskPane);

		JXTaskPane posAndDimenTaskPane = new JXTaskPane();
		posAndDimenTaskPane.setTitle("Position and Dimensions");

		// Display
		JPanel displayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		String[] display = { "inline", "block", "flex", "inline-block", "inline-flex", "run-in" };

		displayPanel.add(new JLabel("Display"));
		JCheckBox displayCheck = new JCheckBox("");
		JComboBox<String> displayComboBox = new JComboBox<String>(display);

		displayCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (displayCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "display", displayComboBox.getSelectedItem().toString());
				}
				if (!displayCheck.isSelected()) {
					removeCSSProperty(getCssSelector(), "display");
				}
			}
		});
		displayComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (displayCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "display", displayComboBox.getSelectedItem().toString());
				}
				if (!displayCheck.isSelected()) {
					return;
				}
			}

		});
		displayPanel.add(displayCheck);
		displayPanel.add(displayComboBox);
		if (getProperty(cssSelector, "display") != null) {
			displayCheck.setSelected(true);
			displayComboBox.setSelectedItem(getProperty(getCssSelector(), "display").getValue().toString());
		}

		// Position
		JPanel positionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		String[] positions = { "static", "absolute", "fixed", "relative", "sticky" };

		positionPanel.add(new JLabel("Position"));
		JCheckBox positionCheck = new JCheckBox("");
		JComboBox<String> positionComboBox = new JComboBox<String>(positions);

		positionCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (positionCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "position", positionComboBox.getSelectedItem().toString());
				}
				if (!positionCheck.isSelected()) {
					removeCSSProperty(getCssSelector(), "position");
				}
			}
		});
		positionComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (positionCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "position", positionComboBox.getSelectedItem().toString());
				}
				if (!positionCheck.isSelected()) {
					return;
				}
			}

		});
		positionPanel.add(positionCheck);
		positionPanel.add(positionComboBox);
		if (getProperty(cssSelector, "position") != null) {
			positionCheck.setSelected(true);
			positionComboBox.setSelectedItem(getProperty(getCssSelector(), "position").getValue().toString());
		}

		// FLOAT
		JPanel floatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		String[] floatOptions = { "left", "right" };

		floatPanel.add(new JLabel("Float"));
		JCheckBox floatcheck = new JCheckBox("");
		JComboBox<String> floatComboBox = new JComboBox<String>(floatOptions);

		floatcheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (floatcheck.isSelected()) {
					writeCssProperty(getCssSelector(), "float", floatComboBox.getSelectedItem().toString());
				}
				if (!floatcheck.isSelected()) {
					removeCSSProperty(getCssSelector(), "float");
				}
			}
		});
		floatComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (floatcheck.isSelected()) {
					writeCssProperty(getCssSelector(), "float", floatComboBox.getSelectedItem().toString());
				}
				if (!floatcheck.isSelected()) {
					return;
				}
			}

		});
		floatPanel.add(floatcheck);
		floatPanel.add(floatComboBox);
		if (getProperty(cssSelector, "float") != null) {
			floatcheck.setSelected(true);
			floatComboBox.setSelectedItem(getProperty(getCssSelector(), "float").getValue().toString());
		}

		String[] posOptions = { "auto", "px", "em", "%" };
		// WIDTH

		JPanel widthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		widthPanel.add(new JLabel("Width"));

		JCheckBox widthCheck = new JCheckBox("");
		JComboBox<String> widthComboBox = new JComboBox<String>(posOptions);
		JTextField widthTextField = new JTextField("");
		widthTextField.setPreferredSize(d);
		widthTextField.setVisible(false);

		widthCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (widthCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "width",
							widthTextField.getText() + widthComboBox.getSelectedItem().toString());
				}
				if (!widthCheck.isSelected()) {

					removeCSSProperty(getCssSelector(), "width");
				}
			}
		});

		widthTextField.getDocument().addDocumentListener(new DocumentListener() {
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
				if (widthCheck.isSelected()) {
					if (widthTextField.getText().equals("") || widthTextField.getText() == null) {
						removeCSSProperty(getCssSelector(), "width");
					} else {
						writeCssProperty(getCssSelector(), "width",
								widthTextField.getText() + widthComboBox.getSelectedItem().toString());
					}
				}
			}

		});

		widthComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (widthCheck.isSelected()) {
					System.out.println(widthComboBox.getSelectedItem());
					if (widthComboBox.getSelectedItem().toString().equals("auto")) {
						widthTextField.setText("");
						widthTextField.setVisible(false);
					} else {
						widthTextField.setVisible(true);
						widthPanel.revalidate();
					}
					writeCssProperty(getCssSelector(), "width",
							widthTextField.getText() + widthComboBox.getSelectedItem().toString());
				}
				if (!widthCheck.isSelected()) {
					return;
				}
			}

		});

		widthPanel.add(widthCheck);
		widthPanel.add(widthTextField);
		widthPanel.add(widthComboBox);

		// CHECKS
		if (getProperty(cssSelector, "width") != null) {
			widthCheck.setSelected(true);
			String widthValue = getProperty(getCssSelector(), "width").getValue().toString();
			if (widthValue.equals("auto")) {
				widthTextField.setVisible(false);
				widthComboBox.setSelectedItem(widthValue);
			} else {
				String numVal = widthValue.replaceAll("[^0-9]", "");
				String unit = widthValue.replaceAll("[0-9]", "");
				widthTextField.setVisible(true);
				widthTextField.setText(numVal);
				widthComboBox.setSelectedItem(unit);
			}
		}

		// HEIGHT

		JPanel heightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		heightPanel.add(new JLabel("height"));

		JCheckBox heightCheck = new JCheckBox("");
		JComboBox<String> heightComboBox = new JComboBox<String>(posOptions);
		JTextField heightTextField = new JTextField("");
		heightTextField.setPreferredSize(d);
		heightTextField.setVisible(false);

		heightCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (heightCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "height",
							heightTextField.getText() + heightComboBox.getSelectedItem().toString());
				}
				if (!heightCheck.isSelected()) {

					removeCSSProperty(getCssSelector(), "height");
				}
			}
		});

		heightTextField.getDocument().addDocumentListener(new DocumentListener() {
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
				if (heightCheck.isSelected()) {
					if (heightTextField.getText().equals("") || heightTextField.getText() == null) {
						removeCSSProperty(getCssSelector(), "height");
					} else {
						writeCssProperty(getCssSelector(), "height",
								heightTextField.getText() + heightComboBox.getSelectedItem().toString());
					}
				}
			}

		});

		heightComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (heightCheck.isSelected()) {
					System.out.println(heightComboBox.getSelectedItem());
					if (heightComboBox.getSelectedItem().toString().equals("auto")) {
						heightTextField.setText("");
						heightTextField.setVisible(false);
					} else {
						heightTextField.setVisible(true);
						heightPanel.revalidate();
					}
					writeCssProperty(getCssSelector(), "height",
							heightTextField.getText() + heightComboBox.getSelectedItem().toString());
				}
				if (!heightCheck.isSelected()) {
					return;
				}
			}

		});

		heightPanel.add(heightCheck);
		heightPanel.add(heightTextField);
		heightPanel.add(heightComboBox);

		// CHECKS
		if (getProperty(cssSelector, "height") != null) {
			heightCheck.setSelected(true);
			String heightValue = getProperty(getCssSelector(), "height").getValue().toString();
			if (heightValue.equals("auto")) {
				heightTextField.setVisible(false);
				heightComboBox.setSelectedItem(heightValue);
			} else {
				String numVal = heightValue.replaceAll("[^0-9]", "");
				String unit = heightValue.replaceAll("[0-9]", "");
				heightTextField.setVisible(true);
				heightTextField.setText(numVal);
				heightComboBox.setSelectedItem(unit);
			}
		}

		// TOP

		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		topPanel.add(new JLabel("Top"));

		JCheckBox topCheck = new JCheckBox("");
		JComboBox<String> topComboBox = new JComboBox<String>(posOptions);
		JTextField topTextField = new JTextField("");
		topTextField.setPreferredSize(d);
		topTextField.setVisible(false);

		topCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (topCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "top",
							topTextField.getText() + topComboBox.getSelectedItem().toString());
				}
				if (!topCheck.isSelected()) {

					removeCSSProperty(getCssSelector(), "top");
				}
			}
		});

		topTextField.getDocument().addDocumentListener(new DocumentListener() {
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
				if (topCheck.isSelected()) {
					if (topTextField.getText().equals("") || topTextField.getText() == null) {
						removeCSSProperty(getCssSelector(), "top");
					} else {
						writeCssProperty(getCssSelector(), "top",
								topTextField.getText() + topComboBox.getSelectedItem().toString());
					}
				}
			}

		});

		topComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (topCheck.isSelected()) {
					System.out.println(topComboBox.getSelectedItem());
					if (topComboBox.getSelectedItem().toString().equals("auto")) {
						topTextField.setText("");
						topTextField.setVisible(false);
					} else {
						topTextField.setVisible(true);
						topPanel.revalidate();
					}
					writeCssProperty(getCssSelector(), "top",
							topTextField.getText() + topComboBox.getSelectedItem().toString());
				}
				if (!topCheck.isSelected()) {
					return;
				}
			}

		});

		topPanel.add(topCheck);
		topPanel.add(topTextField);
		topPanel.add(topComboBox);

		// CHECKS
		if (getProperty(cssSelector, "top") != null) {
			topCheck.setSelected(true);
			String topValue = getProperty(getCssSelector(), "top").getValue().toString();
			if (topValue.equals("auto")) {
				topTextField.setVisible(false);
				topComboBox.setSelectedItem(topValue);
			} else {
				String numVal = topValue.replaceAll("[^0-9]", "");
				String unit = topValue.replaceAll("[0-9]", "");
				topTextField.setVisible(true);
				topTextField.setText(numVal);
				topComboBox.setSelectedItem(unit);
			}
		}
		// BOTTOM

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		bottomPanel.add(new JLabel("Bottom"));

		JCheckBox bottomCheck = new JCheckBox("");
		JComboBox<String> bottomComboBox = new JComboBox<String>(posOptions);
		JTextField bottomTextField = new JTextField("");
		bottomTextField.setPreferredSize(d);
		bottomTextField.setVisible(false);

		bottomCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (bottomCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "bottom",
							bottomTextField.getText() + bottomComboBox.getSelectedItem().toString());
				}
				if (!bottomCheck.isSelected()) {

					removeCSSProperty(getCssSelector(), "bottom");
				}
			}
		});

		bottomTextField.getDocument().addDocumentListener(new DocumentListener() {
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
				if (bottomCheck.isSelected()) {
					if (bottomTextField.getText().equals("") || bottomTextField.getText() == null) {
						removeCSSProperty(getCssSelector(), "bottom");
					} else {
						writeCssProperty(getCssSelector(), "bottom",
								bottomTextField.getText() + bottomComboBox.getSelectedItem().toString());
					}
				}
			}

		});

		bottomComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (bottomCheck.isSelected()) {
					System.out.println(bottomComboBox.getSelectedItem());
					if (bottomComboBox.getSelectedItem().toString().equals("auto")) {
						bottomTextField.setText("");
						bottomTextField.setVisible(false);
					} else {
						bottomTextField.setVisible(true);
						bottomPanel.revalidate();
					}
					writeCssProperty(getCssSelector(), "bottom",
							bottomTextField.getText() + bottomComboBox.getSelectedItem().toString());
				}
				if (!bottomCheck.isSelected()) {
					return;
				}
			}

		});

		bottomPanel.add(bottomCheck);
		bottomPanel.add(bottomTextField);
		bottomPanel.add(bottomComboBox);

		// CHECKS
		if (getProperty(cssSelector, "bottom") != null) {
			bottomCheck.setSelected(true);
			String bottomValue = getProperty(getCssSelector(), "bottom").getValue().toString();
			if (bottomValue.equals("auto")) {
				bottomTextField.setVisible(false);
				bottomComboBox.setSelectedItem(bottomValue);
			} else {
				String numVal = bottomValue.replaceAll("[^0-9]", "");
				String unit = bottomValue.replaceAll("[0-9]", "");
				bottomTextField.setVisible(true);
				bottomTextField.setText(numVal);
				bottomComboBox.setSelectedItem(unit);
			}
		}
		// LEFT
		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		leftPanel.add(new JLabel("Left"));

		JCheckBox leftCheck = new JCheckBox("");
		JComboBox<String> leftComboBox = new JComboBox<String>(posOptions);
		JTextField leftTextField = new JTextField("");
		leftTextField.setPreferredSize(d);
		leftTextField.setVisible(false);

		leftCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (leftCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "left",
							leftTextField.getText() + leftComboBox.getSelectedItem().toString());
				}
				if (!leftCheck.isSelected()) {

					removeCSSProperty(getCssSelector(), "left");
				}
			}
		});

		leftTextField.getDocument().addDocumentListener(new DocumentListener() {
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
				if (leftCheck.isSelected()) {
					if (leftTextField.getText().equals("") || leftTextField.getText() == null) {
						removeCSSProperty(getCssSelector(), "left");
					} else {
						writeCssProperty(getCssSelector(), "left",
								leftTextField.getText() + leftComboBox.getSelectedItem().toString());
					}
				}
			}

		});

		leftComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (leftCheck.isSelected()) {
					System.out.println(leftComboBox.getSelectedItem());
					if (leftComboBox.getSelectedItem().toString().equals("auto")) {
						leftTextField.setText("");
						leftTextField.setVisible(false);
					} else {
						leftTextField.setVisible(true);
						leftPanel.revalidate();
					}
					writeCssProperty(getCssSelector(), "left",
							leftTextField.getText() + leftComboBox.getSelectedItem().toString());
				}
				if (!leftCheck.isSelected()) {
					return;
				}
			}

		});

		leftPanel.add(leftCheck);
		leftPanel.add(leftTextField);
		leftPanel.add(leftComboBox);

		// CHECKS
		if (getProperty(cssSelector, "left") != null) {
			leftCheck.setSelected(true);
			String leftValue = getProperty(getCssSelector(), "left").getValue().toString();
			if (leftValue.equals("auto")) {
				leftTextField.setVisible(false);
				leftComboBox.setSelectedItem(leftValue);
			} else {
				String numVal = leftValue.replaceAll("[^0-9]", "");
				String unit = leftValue.replaceAll("[0-9]", "");
				leftTextField.setVisible(true);
				leftTextField.setText(numVal);
				leftComboBox.setSelectedItem(unit);
			}
		}
		// RIGHT

		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		rightPanel.add(new JLabel("Right"));
		JCheckBox rightCheck = new JCheckBox("");
		JComboBox<String> rightComboBox = new JComboBox<String>(posOptions);
		JTextField rightTextField = new JTextField("");
		rightTextField.setPreferredSize(d);
		rightTextField.setVisible(false);

		rightCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (rightCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "right",
							rightTextField.getText() + rightComboBox.getSelectedItem().toString());
				}
				if (!rightCheck.isSelected()) {

					removeCSSProperty(getCssSelector(), "right");
				}
			}
		});

		rightTextField.getDocument().addDocumentListener(new DocumentListener() {
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
				if (rightCheck.isSelected()) {
					if (rightTextField.getText().equals("") || rightTextField.getText() == null) {
						removeCSSProperty(getCssSelector(), "right");
					} else {
						writeCssProperty(getCssSelector(), "right",
								rightTextField.getText() + rightComboBox.getSelectedItem().toString());
					}
				}
			}

		});

		rightComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (rightCheck.isSelected()) {
					System.out.println(rightComboBox.getSelectedItem());
					if (rightComboBox.getSelectedItem().toString().equals("auto")) {
						rightTextField.setText("");
						rightTextField.setVisible(false);
					} else {
						rightTextField.setVisible(true);
						rightPanel.revalidate();
					}
					writeCssProperty(getCssSelector(), "right",
							rightTextField.getText() + rightComboBox.getSelectedItem().toString());
				}
				if (!rightCheck.isSelected()) {
					return;
				}
			}

		});

		rightPanel.add(rightCheck);
		rightPanel.add(rightTextField);
		rightPanel.add(rightComboBox);

		// CHECKS
		if (getProperty(cssSelector, "left") != null) {
			rightCheck.setSelected(true);
			String rightValue = getProperty(getCssSelector(), "right").getValue().toString();
			if (rightValue.equals("auto")) {
				rightTextField.setVisible(false);
				rightComboBox.setSelectedItem(rightValue);
			} else {
				String numVal = rightValue.replaceAll("[^0-9]", "");
				String unit = rightValue.replaceAll("[0-9]", "");
				rightTextField.setVisible(true);
				rightTextField.setText(numVal);
				rightComboBox.setSelectedItem(unit);
			}
		}

		// MASTERS
		posAndDimenTaskPane.add(displayPanel);
		posAndDimenTaskPane.add(positionPanel);
		posAndDimenTaskPane.add(floatPanel);
		posAndDimenTaskPane.add(widthPanel);
		posAndDimenTaskPane.add(heightPanel);
		posAndDimenTaskPane.add(topPanel);
		posAndDimenTaskPane.add(bottomPanel);
		posAndDimenTaskPane.add(leftPanel);
		posAndDimenTaskPane.add(rightPanel);

		taskPanes.add(posAndDimenTaskPane);

		JXTaskPane marginAndPaddingTaskPane = new JXTaskPane();
		marginAndPaddingTaskPane.setTitle("Margins and Padding");

		// TOP

		JPanel marginTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		marginTopPanel.add(new JLabel("margin-top"));

		JCheckBox marginTopCheck = new JCheckBox("");
		JComboBox<String> marginTopComboBox = new JComboBox<String>(posOptions);
		JTextField marginTopTextField = new JTextField("");
		marginTopTextField.setPreferredSize(d);
		marginTopTextField.setVisible(false);

		marginTopCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (marginTopCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "margin-top",
							marginTopTextField.getText() + marginTopComboBox.getSelectedItem().toString());
				}
				if (!marginTopCheck.isSelected()) {

					removeCSSProperty(getCssSelector(), "margin-top");
				}
			}
		});

		marginTopTextField.getDocument().addDocumentListener(new DocumentListener() {
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
				if (marginTopCheck.isSelected()) {
					if (marginTopTextField.getText().equals("") || marginTopTextField.getText() == null) {
						removeCSSProperty(getCssSelector(), "margin-top");
					} else {
						writeCssProperty(getCssSelector(), "margin-top",
								marginTopTextField.getText() + marginTopComboBox.getSelectedItem().toString());
					}
				}
			}

		});

		marginTopComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (marginTopCheck.isSelected()) {
					System.out.println(marginTopComboBox.getSelectedItem());
					if (marginTopComboBox.getSelectedItem().toString().equals("auto")) {
						marginTopTextField.setText("");
						marginTopTextField.setVisible(false);
					} else {
						marginTopTextField.setVisible(true);
						marginTopPanel.revalidate();
					}
					writeCssProperty(getCssSelector(), "margin-top",
							marginTopTextField.getText() + marginTopComboBox.getSelectedItem().toString());
				}
				if (!marginTopCheck.isSelected()) {
					return;
				}
			}

		});

		marginTopPanel.add(marginTopCheck);
		marginTopPanel.add(marginTopTextField);
		marginTopPanel.add(marginTopComboBox);

		// CHECKS
		if (getProperty(cssSelector, "margin-top") != null) {
			marginTopCheck.setSelected(true);
			String marginTopValue = getProperty(getCssSelector(), "margin-top").getValue().toString();
			if (marginTopValue.equals("auto")) {
				marginTopTextField.setVisible(false);
				marginTopComboBox.setSelectedItem(marginTopValue);
			} else {
				String numVal = marginTopValue.replaceAll("[^0-9]", "");
				String unit = marginTopValue.replaceAll("[0-9]", "");
				marginTopTextField.setVisible(true);
				marginTopTextField.setText(numVal);
				marginTopComboBox.setSelectedItem(unit);
			}
		}
		// margin-bottom

		JPanel marginBottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		marginBottomPanel.add(new JLabel("margin-bottom"));

		JCheckBox marginBottomCheck = new JCheckBox("");
		JComboBox<String> marginBottomComboBox = new JComboBox<String>(posOptions);
		JTextField marginBottomTextField = new JTextField("");
		marginBottomTextField.setPreferredSize(d);
		marginBottomTextField.setVisible(false);

		marginBottomCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (marginBottomCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "margin-bottom",
							marginBottomTextField.getText() + marginBottomComboBox.getSelectedItem().toString());
				}
				if (!marginBottomCheck.isSelected()) {

					removeCSSProperty(getCssSelector(), "margin-bottom");
				}
			}
		});

		marginBottomTextField.getDocument().addDocumentListener(new DocumentListener() {
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
				if (marginBottomCheck.isSelected()) {
					if (marginBottomTextField.getText().equals("") || marginBottomTextField.getText() == null) {
						removeCSSProperty(getCssSelector(), "margin-bottom");
					} else {
						writeCssProperty(getCssSelector(), "margin-bottom",
								marginBottomTextField.getText() + marginBottomComboBox.getSelectedItem().toString());
					}
				}
			}

		});

		marginBottomComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (marginBottomCheck.isSelected()) {
					System.out.println(marginBottomComboBox.getSelectedItem());
					if (marginBottomComboBox.getSelectedItem().toString().equals("auto")) {
						marginBottomTextField.setText("");
						marginBottomTextField.setVisible(false);
					} else {
						marginBottomTextField.setVisible(true);
						marginBottomPanel.revalidate();
					}
					writeCssProperty(getCssSelector(), "margin-bottom",
							marginBottomTextField.getText() + marginBottomComboBox.getSelectedItem().toString());
				}
				if (!marginBottomCheck.isSelected()) {
					return;
				}
			}

		});

		marginBottomPanel.add(marginBottomCheck);
		marginBottomPanel.add(marginBottomTextField);
		marginBottomPanel.add(marginBottomComboBox);

		// CHECKS
		if (getProperty(cssSelector, "margin-bottom") != null) {
			marginBottomCheck.setSelected(true);
			String marginBottomValue = getProperty(getCssSelector(), "margin-bottom").getValue().toString();
			if (marginBottomValue.equals("auto")) {
				marginBottomTextField.setVisible(false);
				marginBottomComboBox.setSelectedItem(marginBottomValue);
			} else {
				String numVal = marginBottomValue.replaceAll("[^0-9]", "");
				String unit = marginBottomValue.replaceAll("[0-9]", "");
				marginBottomTextField.setVisible(true);
				marginBottomTextField.setText(numVal);
				marginBottomComboBox.setSelectedItem(unit);
			}
		}
		// margin-left
		JPanel marginLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		marginLeftPanel.add(new JLabel("margin-left"));

		JCheckBox marginLeftCheck = new JCheckBox("");
		JComboBox<String> marginLeftComboBox = new JComboBox<String>(posOptions);
		JTextField marginLeftTextField = new JTextField("");
		marginLeftTextField.setPreferredSize(d);
		marginLeftTextField.setVisible(false);

		marginLeftCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (marginLeftCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "margin-left",
							marginLeftTextField.getText() + marginLeftComboBox.getSelectedItem().toString());
				}
				if (!marginLeftCheck.isSelected()) {

					removeCSSProperty(getCssSelector(), "margin-left");
				}
			}
		});

		marginLeftTextField.getDocument().addDocumentListener(new DocumentListener() {
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
				if (marginLeftCheck.isSelected()) {
					if (marginLeftTextField.getText().equals("") || marginLeftTextField.getText() == null) {
						removeCSSProperty(getCssSelector(), "margin-left");
					} else {
						writeCssProperty(getCssSelector(), "margin-left",
								marginLeftTextField.getText() + marginLeftComboBox.getSelectedItem().toString());
					}
				}
			}

		});

		marginLeftComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (marginLeftCheck.isSelected()) {
					System.out.println(marginLeftComboBox.getSelectedItem());
					if (marginLeftComboBox.getSelectedItem().toString().equals("auto")) {
						marginLeftTextField.setText("");
						marginLeftTextField.setVisible(false);
					} else {
						marginLeftTextField.setVisible(true);
						marginLeftPanel.revalidate();
					}
					writeCssProperty(getCssSelector(), "margin-left",
							marginLeftTextField.getText() + marginLeftComboBox.getSelectedItem().toString());
				}
				if (!marginLeftCheck.isSelected()) {
					return;
				}
			}

		});

		marginLeftPanel.add(marginLeftCheck);
		marginLeftPanel.add(marginLeftTextField);
		marginLeftPanel.add(marginLeftComboBox);

		// CHECKS
		if (getProperty(cssSelector, "margin-left") != null) {
			marginLeftCheck.setSelected(true);
			String marginLeftValue = getProperty(getCssSelector(), "margin-left").getValue().toString();
			if (marginLeftValue.equals("auto")) {
				marginLeftTextField.setVisible(false);
				marginLeftComboBox.setSelectedItem(marginLeftValue);
			} else {
				String numVal = marginLeftValue.replaceAll("[^0-9]", "");
				String unit = marginLeftValue.replaceAll("[0-9]", "");
				marginLeftTextField.setVisible(true);
				marginLeftTextField.setText(numVal);
				marginLeftComboBox.setSelectedItem(unit);
			}
		}
		// margin-right

		JPanel marginRightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		marginRightPanel.add(new JLabel("margin-right"));
		JCheckBox marginRightCheck = new JCheckBox("");
		JComboBox<String> marginRightComboBox = new JComboBox<String>(posOptions);
		JTextField marginRightTextField = new JTextField("");
		marginRightTextField.setPreferredSize(d);
		marginRightTextField.setVisible(false);

		marginRightCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (marginRightCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "margin-right",
							marginRightTextField.getText() + marginRightComboBox.getSelectedItem().toString());
				}
				if (!marginRightCheck.isSelected()) {

					removeCSSProperty(getCssSelector(), "margin-right");
				}
			}
		});

		marginRightTextField.getDocument().addDocumentListener(new DocumentListener() {
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
				if (marginRightCheck.isSelected()) {
					if (marginRightTextField.getText().equals("") || marginRightTextField.getText() == null) {
						removeCSSProperty(getCssSelector(), "margin-right");
					} else {
						writeCssProperty(getCssSelector(), "margin-right",
								marginRightTextField.getText() + marginRightComboBox.getSelectedItem().toString());
					}
				}
			}

		});

		marginRightComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (marginRightCheck.isSelected()) {
					System.out.println(marginRightComboBox.getSelectedItem());
					if (marginRightComboBox.getSelectedItem().toString().equals("auto")) {
						marginRightTextField.setText("");
						marginRightTextField.setVisible(false);
					} else {
						marginRightTextField.setVisible(true);
						marginRightPanel.revalidate();
					}
					writeCssProperty(getCssSelector(), "margin-right",
							marginRightTextField.getText() + marginRightComboBox.getSelectedItem().toString());
				}
				if (!marginRightCheck.isSelected()) {
					return;
				}
			}

		});

		marginRightPanel.add(marginRightCheck);
		marginRightPanel.add(marginRightTextField);
		marginRightPanel.add(marginRightComboBox);

		// CHECKS
		if (getProperty(cssSelector, "margin-left") != null) {
			marginRightCheck.setSelected(true);
			String marginRightValue = getProperty(getCssSelector(), "margin-right").getValue().toString();
			if (marginRightValue.equals("auto")) {
				marginRightTextField.setVisible(false);
				marginRightComboBox.setSelectedItem(marginRightValue);
			} else {
				String numVal = marginRightValue.replaceAll("[^0-9]", "");
				String unit = marginRightValue.replaceAll("[0-9]", "");
				marginRightTextField.setVisible(true);
				marginRightTextField.setText(numVal);
				marginRightComboBox.setSelectedItem(unit);
			}
		}

		marginAndPaddingTaskPane.add(marginTopPanel);
		marginAndPaddingTaskPane.add(marginBottomPanel);
		marginAndPaddingTaskPane.add(marginLeftPanel);
		marginAndPaddingTaskPane.add(marginRightPanel);

		taskPanes.add(marginAndPaddingTaskPane);

		JXTaskPane flexibleBoxLayoutTaskPane = new JXTaskPane();
		flexibleBoxLayoutTaskPane.setTitle("Flexible Box Layout");

		JPanel alignContentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, flowLayoutWidth, flowLayoutHeight));
		String[] alignOptions = { "stretch", "center", "flex-start", "flex-end", "space-between", "space-around" };

		alignContentPanel.add(new JLabel("Align Content"));
		JCheckBox alignContentCheck = new JCheckBox("");
		JComboBox<String> alignContentComboBox = new JComboBox<String>(alignOptions);

		alignContentCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (alignContentCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "align-content", alignContentComboBox.getSelectedItem().toString());
				}
				if (!alignContentCheck.isSelected()) {
					removeCSSProperty(getCssSelector(), "align-content");
				}
			}
		});
		alignContentComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (alignContentCheck.isSelected()) {
					writeCssProperty(getCssSelector(), "align-content", alignContentComboBox.getSelectedItem().toString());
				}
				if (!alignContentCheck.isSelected()) {
					return;
				}
			}

		});
		alignContentPanel.add(alignContentCheck);
		alignContentPanel.add(alignContentComboBox);
		if (getProperty(cssSelector, "align-content") != null) {
			alignContentCheck.setSelected(true);
			alignContentComboBox.setSelectedItem(getProperty(getCssSelector(), "align-content").getValue().toString());
		}
		
		

		flexibleBoxLayoutTaskPane.add(alignContentPanel);

		taskPanes.add(flexibleBoxLayoutTaskPane);

//		JXTaskPane writingModesPropertiesTaskPane = new JXTaskPane();
//		writingModesPropertiesTaskPane.setTitle("Writing Modes Properties");
//		taskPanes.add(writingModesPropertiesTaskPane);
//
//		JXTaskPane tablePropertiesTaskPane = new JXTaskPane();
//		tablePropertiesTaskPane.setTitle("Table Properties");
//		taskPanes.add(tablePropertiesTaskPane);
//
//		JXTaskPane listsandCountersPropertiesTaskPane = new JXTaskPane();
//		listsandCountersPropertiesTaskPane.setTitle("Lists And Counters Properties");
//		taskPanes.add(listsandCountersPropertiesTaskPane);
//
//		JXTaskPane animationPropertiesTaskPane = new JXTaskPane();
//		animationPropertiesTaskPane.setTitle("Animation Properties");
//		taskPanes.add(animationPropertiesTaskPane);
//
//		JXTaskPane transformPropertiesTaskPane = new JXTaskPane();
//		transformPropertiesTaskPane.setTitle("Transform Properties");
//		taskPanes.add(transformPropertiesTaskPane);
//
//		JXTaskPane transitionsPropertiesTaskPane = new JXTaskPane();
//		transitionsPropertiesTaskPane.setTitle("Transition Properties");
//		taskPanes.add(transitionsPropertiesTaskPane);
//
//		JXTaskPane basicUserInterfacePropertiesTaskPane = new JXTaskPane();
//		basicUserInterfacePropertiesTaskPane.setTitle("Basic User Interface Properties");
//		taskPanes.add(basicUserInterfacePropertiesTaskPane);
//
//		JXTaskPane multicolumnLayoutPropertiesTaskPane = new JXTaskPane();
//		multicolumnLayoutPropertiesTaskPane.setTitle("Multicolumn Layout Properties");
//		taskPanes.add(multicolumnLayoutPropertiesTaskPane);
//
//		JXTaskPane pagedMediaTaskPane = new JXTaskPane();
//		pagedMediaTaskPane.setTitle("Paged Media Task Pane");
//		taskPanes.add(pagedMediaTaskPane);
//
//		JXTaskPane generatedContentforPagedMediaTaskPane = new JXTaskPane();
//		generatedContentforPagedMediaTaskPane.setTitle("Generated Content For Paged Media");
//		taskPanes.add(generatedContentforPagedMediaTaskPane);
//
//		JXTaskPane filterEffectsPropertiesTaskPane = new JXTaskPane();
//		filterEffectsPropertiesTaskPane.setTitle("Filter Effects");
//		taskPanes.add(filterEffectsPropertiesTaskPane);
//
//		JXTaskPane imageValuesandReplacedContentTaskPane = new JXTaskPane();
//		imageValuesandReplacedContentTaskPane.setTitle("Image values and Replaced Content");
//		taskPanes.add(imageValuesandReplacedContentTaskPane);
//
//		JXTaskPane maskingPropertiesTaskPane = new JXTaskPane();
//		maskingPropertiesTaskPane.setTitle("Masking Properties");
//		taskPanes.add(maskingPropertiesTaskPane);
//
//		JXTaskPane speechPropertiesTaskPane = new JXTaskPane();
//		speechPropertiesTaskPane.setTitle("Speech Properties");
//		taskPanes.add(speechPropertiesTaskPane);

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
