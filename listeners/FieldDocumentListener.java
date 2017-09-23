package listeners;

import java.util.List;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import engine.HTMLDocReader;
import engine.Main;
import engine.FileSaver;
import javafx.application.Platform;

public class FieldDocumentListener implements DocumentListener {

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
		updateElement();
	}


	public void updateElement() {
		FileSaver.unsavedChanges = true;
		String attrib = ListListener.label.get(fieldIndex).getText();

		element = reader.tempDoc.body().select("*").get(elementIndex);
		String attributeToRemove = ListListener.label.get(fieldIndex).getText();
		if (ListListener.field.get(fieldIndex).getText().equals("")
				|| ListListener.field.get(fieldIndex).getText() == null) {
			System.out.println("removing");
			// remove attribute from element
			Attributes a = element.attributes();
			System.out.println(a);
			List<Attribute> b = a.asList();
			for (int i = 0; i < b.size(); i++) {
				
				if (b.get(i).getKey().equals(attributeToRemove)) {
					System.out.println("Removing attribute "+b.get(i).getKey());
					element.removeAttr(b.get(i).getKey());
				}
			}
		} else {
			element.attr(attrib, ListListener.field.get(fieldIndex).getText());
		}
		Main.textArea.setText(reader.tempDoc.toString());
		Platform.runLater(new Runnable() {
			public void run() {
				Main.updateFX(Main.tempPageURL);
			}
		});

	}

}
