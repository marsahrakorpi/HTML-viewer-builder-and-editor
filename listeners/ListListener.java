package listeners;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.tree.DefaultMutableTreeNode;

import engine.BodyElementInfo;
import engine.HTMLDocReader;
import engine.HeadElementInfo;
import engine.Main;

public class ListListener implements TreeSelectionListener {

	private HTMLDocReader reader;
	public DefaultMutableTreeNode elementTree;

	String[] styleAttributes = { "color", "text-align", "font-size", "font-style", "height", "width", "margin",
			"margin-bottom", "margin-top", "margin-left", "margin-right", "padding", "padding-top", "padding-bottom",
			"padding-left", "padding-right", "position", "visibility" };

	String[] globalHTMLAttributes = { "id", "accesskey", "class", "contenteditable", "contextmenu", "dir", "draggable",
			"dropzone", "lang", "spellcheck", "tabindex", "title", "translate" };

	String[] fonts = { "Arial", "Helvetica", "Times New Roman", "Times", "Courier New", "Courier", "Veradana",
			"Georgia", "Palatino", "Garamond", "Bookman", "Comic Sans MS", "Trebuchet MS", "Arial Black", "Impact" };
	String[] fontStyles = { "normal", "italic", "bold", "oblique" };
	JLabel elementName = new JLabel("INIT", JLabel.CENTER);
	Highlighter.HighlightPainter painter;
	public static ArrayList<JLabel> label = new ArrayList<JLabel>();
	public static ArrayList<JTextField> field = new ArrayList<JTextField>();
	public static ArrayList<JLabel> styleLabel = new ArrayList<JLabel>();
	public static ArrayList<JTextField> styleField = new ArrayList<JTextField>();
	public JPanel p;
	private Dimension d;

	public ListListener(HTMLDocReader reader) {
		this.reader = reader;
		p = new JPanel();
		d = new Dimension(275, 20);
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
		p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		p.add(Box.createHorizontalGlue());
		p.add(Box.createRigidArea(new Dimension(5, 5)));
		painter = new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		Main.textArea.getHighlighter().removeAllHighlights();
		
		try {
			p.removeAll();
			Main.elementAttributes.setViewportView(p);
		} catch (Exception exc) {

		}
		label.clear();
		field.clear();

		if (e.getPath().getPathCount() > 2) {
			// HEAD
			if (e.getPath().getPathComponent(1).toString().equals("Head")) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
				if (node == null) {
					System.out.println("node null");
					return;
				}

				Object nodeInfo = node.getUserObject();
				if (node.isLeaf()) {
					HeadElementInfo hElement = (HeadElementInfo) nodeInfo;

					elementName.setText(hElement.elementName.toUpperCase() + "\n\n");
					elementName.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
					elementName.setFont(new Font("Arial", Font.BOLD, 15));
					p.add(elementName);

				}
			}

			// BODY
			if (e.getPath().getPathComponent(1).toString().equals("Body")) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
				if (node == null) {
					System.out.println("node null");
					return;
				}

				Object nodeInfo = node.getUserObject();
				BodyElementInfo bElement = (BodyElementInfo) nodeInfo;
				// System.out.println(bElement.elementName+reader.bodyElements.get(bElement.index).nodeName());
				styleLabel.clear();
				label.clear();
				styleField.clear();
				field.clear();

				elementName.setText(bElement.elementName.toUpperCase() + "\n\n");
				elementName.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
				elementName.setFont(new Font("Arial", Font.BOLD, 20));
				p.add(elementName);

				for (int i = 0; i < styleLabel.size(); i++) {
					p.add(styleLabel.get(i));
					p.add(styleField.get(i));
					styleLabel.get(i).setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
					styleField.get(i).setMaximumSize(d);
					styleField.get(i).setHorizontalAlignment(JTextField.LEFT);
				}

				// GLOBAL HTML ATTRIBUTE FIELDS
				JLabel l = new JLabel("Global HTML Attributes");
				l.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
				l.setFont(new Font("Arial", Font.BOLD, 15));
				p.add(l);

				for (int i = 0; i < globalHTMLAttributes.length; i++) {
					label.add(new JLabel(globalHTMLAttributes[i]));
					// find the correct html element in the HTMLReader by refering to the
					// bodyElementInfo index
					if (reader.tempDoc.body().select("*").get(bElement.index).hasAttr(globalHTMLAttributes[i])) {
						field.add(new JTextField(
								reader.tempDoc.body().select("*").get(bElement.index).attr(globalHTMLAttributes[i])));
					} else {
						field.add(new JTextField(""));

					}
					// Add listeners to text fields
					field.get(i).getDocument()
							.addDocumentListener(new FieldDocumentListener(i, bElement.index, reader));
					field.get(i).addKeyListener(new FieldKeyListener(i));
				}

				for (int i = 0; i < label.size(); i++) {
					p.add(label.get(i));
					p.add(field.get(i));
					label.get(i).setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
					field.get(i).setMaximumSize(d);
					field.get(i).setHorizontalAlignment(JTextField.LEFT);
				}

				JCheckBox hiddenCheck = new JCheckBox("hidden");
				hiddenCheck.addItemListener(new CheckListener(bElement.index, reader, hiddenCheck));
				p.add(hiddenCheck);

//				int offset = reader.doc.toString().indexOf(HTMLDocReader.bodyElements.get(bElement.index).outerHtml());
//				int length = HTMLDocReader.bodyElements.get(bElement.index).toString().length();
//
//				/*
//				 * 
//				 * TO DO: FIX DIV HIGHLIGHTING CURRENTL ONLY SINGULAR ELEMENTS ARE HIGHLIGHTED
//				 * 
//				 */
//
//				while (offset != -1) {
//					try {
//						Main.textArea.getHighlighter().addHighlight(offset, offset + length, painter);
//						offset = reader.doc.toString()
//								.indexOf(HTMLDocReader.bodyElements.get(bElement.index).toString(), offset + 1);
//					} catch (BadLocationException ble) {
//						// TODO Auto-generated catch block
//						ble.printStackTrace();
//					}
//
//				}

			}
		}

		// set labels and fields

		Main.elementAttributes.setViewportView(p);

	}

}
