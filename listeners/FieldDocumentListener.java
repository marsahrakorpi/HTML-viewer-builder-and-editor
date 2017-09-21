package listeners;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import engine.HTMLDocReader;
import engine.Main;
import javafx.application.Platform;

public class FieldDocumentListener extends Thread implements DocumentListener {

	int fieldIndex, elementIndex;
	String tempFile;
	Element element;
	HTMLDocReader reader;
	boolean debug = false;

	public FieldDocumentListener(int fieldIndex, int elementIndex, HTMLDocReader reader) {
		this.fieldIndex = fieldIndex;
		this.elementIndex = elementIndex;
		this.reader = reader;
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		updateElement();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		updateElement();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		removeUpdate();

	}

	public void removeUpdate() {

		element = HTMLDocReader.bodyElements.get(elementIndex);
		String attributeToRemove = ListListener.label.get(fieldIndex).getText();
		if (ListListener.field.get(fieldIndex).getText().equals("")
				|| ListListener.field.get(fieldIndex).getText() == null) {

			// remove attribute from element
			Attributes a = element.attributes();
			List<Attribute> b = a.asList();
			for (int i = 0; i < b.size(); i++) {
				if (b.get(i).getKey().equals(attributeToRemove)) {
					element.removeAttr(b.get(i).getKey());
				}
			}

			Thread t = new Thread() {
				public void run() {

					File file = new File(Main.pageURL);
					try {
						BufferedWriter bw = new BufferedWriter(new FileWriter(file));
						bw.write("\n<html>");
						bw.write("\n" + HTMLDocReader.headElements.get(0));
						bw.write("\n" + HTMLDocReader.bodyElements.get(0));
						bw.write("\n</html>");
						bw.close();

						reader.readDoc(Main.pageURL);
						reader.readLinkDoc(Main.pageURL);
						Main.textArea.setText(reader.doc.toString());
						Platform.runLater(new Runnable() {
							public void run() {
								Main.updateFX(Main.pageURL);
							}
						});

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			};
			t.start();
		}

	}

	public void updateElement() {

		Thread t = new Thread() {
			public void run() {
				String attrib = ListListener.label.get(fieldIndex).getText();

				element = HTMLDocReader.bodyElements.get(elementIndex);
				element.attr(attrib, ListListener.field.get(fieldIndex).getText());

				File file = new File(Main.pageURL);
				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter(file));
					bw.write("\n<html>");
					bw.write("\n" + HTMLDocReader.headElements.get(0));
					bw.write("\n" + HTMLDocReader.bodyElements.get(0));
					bw.write("\n</html>");
					bw.close();

					reader.readDoc(Main.pageURL);
					reader.readLinkDoc(Main.pageURL);
					Main.textArea.setText(reader.doc.toString());
					Platform.runLater(new Runnable() {
						public void run() {
							Main.updateFX(Main.pageURL);
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};
		t.start();

	}

}
