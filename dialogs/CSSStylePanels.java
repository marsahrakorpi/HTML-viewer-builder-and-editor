package dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.io.FileUtils;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import com.steadystate.css.dom.CSSRuleListImpl;
import com.steadystate.css.dom.CSSStyleDeclarationImpl;
import com.steadystate.css.dom.CSSStyleRuleImpl;
import com.steadystate.css.dom.CSSStyleSheetImpl;
import com.steadystate.css.dom.CSSValueImpl;
import com.steadystate.css.dom.Property;
import com.steadystate.css.format.CSSFormat;

import engine.Main;
import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

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

	public CSSStylePanels(String cssSelector, CSSStyleSheetImpl stylesheet, File tempHTMLFile, File tempCSSFile,
			String elementNoTags, Dimension d, JTextArea textArea, JTabbedPane tabbedPane, String className) {

		this.cssSelector = cssSelector;
		CSSStylePanels.stylesheet = stylesheet;
		this.tempHTMLFile = tempHTMLFile;
		this.tempCSSFile = tempCSSFile;
		this.elementNoTags = elementNoTags;
		this.d = d;
		this.previewPane = previewPane;
		this.textArea = textArea;
		this.tabbedPane = tabbedPane;
		this.className = className;

		container = new JXTaskPaneContainer();

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
					writeCssProperty(cssSelector, "color", colorValue.getText());
				}
				if (!colorCheck.isSelected()) {
					removeCSSProperty(cssSelector, "color");
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
						writeCssProperty(cssSelector, "color", colorValue.getText());
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
					writeCssProperty(cssSelector, "color", colorValue.getText());
				}
			}

		});
		textColorPanel.add(textColorButton);

		// CHECKS
		if (getProperty(cssSelector, "color") != null) {
			colorCheck.setSelected(true);
			colorValue.setText(getProperty(cssSelector, "color").getValue().toString());
		}

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
					writeCssProperty(cssSelector, "text-align", alignComboBox.getSelectedItem().toString());
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
					writeCssProperty(cssSelector, "text-align", alignComboBox.getSelectedItem().toString());
				}
				if (!textAlignCheck.isSelected()) {
					removeCSSProperty(cssSelector, "text-align");
				}
			}
		});

		textAlignPanel.add(textAlignCheck);
		textAlignPanel.add(alignComboBox);

		// CHECKS
		if (getProperty(cssSelector, "text-align") != null) {
			textAlignCheck.setSelected(true);
			alignComboBox.setSelectedItem(getProperty(cssSelector, "color").getValue().toString());
		}

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
					writeCssProperty(cssSelector, "text-transform", transformComboBox.getSelectedItem().toString());
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
					writeCssProperty(cssSelector, "text-transform", transformComboBox.getSelectedItem().toString());
				}
				if (!transformCheck.isSelected()) {
					removeCSSProperty(cssSelector, "text-transform");
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
					writeCssProperty(cssSelector, "font-size",
							fontSizeTextField.getText() + fontSizeComboBox.getSelectedItem().toString());
				}
				if (!fontSizeCheck.isSelected()) {
					removeCSSProperty(cssSelector, "font-size");
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
						removeCSSProperty(cssSelector, "font-size");
					} else {
						writeCssProperty(cssSelector, "font-size",
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
					writeCssProperty(cssSelector, "font-size",
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
					writeCssProperty(cssSelector, "font-weight", fontStyleComboBox.getSelectedItem().toString());
				}
				if (!fontStyleCheck.isSelected()) {
					removeCSSProperty(cssSelector, "font-weight");
				}
			}
		});
		fontStyleComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (fontStyleCheck.isSelected()) {
					writeCssProperty(cssSelector, "font-weight", fontStyleComboBox.getSelectedItem().toString());
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

		fontWeightPanel.add(new JLabel("Font Weight"));
		JCheckBox fontWeightCheck = new JCheckBox("");
		JComboBox<String> fontWeightComboBox = new JComboBox<String>(fontWeights);

		fontWeightCheck.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (fontWeightCheck.isSelected()) {
					writeCssProperty(cssSelector, "font-weight", fontWeightComboBox.getSelectedItem().toString());
				}
				if (!fontWeightCheck.isSelected()) {
					removeCSSProperty(cssSelector, "font-weight");
				}
			}
		});
		fontWeightComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (fontWeightCheck.isSelected()) {
					writeCssProperty(cssSelector, "font-weight", fontWeightComboBox.getSelectedItem().toString());
				}
				if (!fontWeightCheck.isSelected()) {
					return;
				}
			}

		});
		fontWeightPanel.add(fontWeightCheck);
		fontWeightPanel.add(fontWeightComboBox);

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
						updateFX(tempHTMLFile.getAbsolutePath());
					}
				});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void updateFX(String url) {
		System.out.println("updateFX");
		File f = new File(url);
		if (className.equals("EditNewElementDialog")) {
			try {
				EditNewElementDialog.webView.setPrefSize(EditNewElementDialog.previewPane.getSize().width,
						EditNewElementDialog.previewPane.getSize().height);
			} catch (Exception e1) {

			}
			if (url == null || url.equals("") || url.equals(null)) {
				EditNewElementDialog.webEngine.load("htt://www.google.com");
			} else {
				try {
					EditNewElementDialog.webEngine.load(f.toURI().toString());
					EditNewElementDialog.webEngine.reload();
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

	}

	public JXTaskPaneContainer getContainer() {
		return container;
	}
	
	public void remove() {
		container.removeAll();
	}

}
