package listeners;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jsoup.nodes.Element;

import engine.HTMLDocReader;

public class StyleFieldDocumentListener implements DocumentListener {

	int fieldIndex, elementIndex;
	String tempFile;
	Element element;
	HTMLDocReader reader;

	public StyleFieldDocumentListener(int fieldIndex, int elementIndex, HTMLDocReader reader) {
		this.fieldIndex = fieldIndex;
		this.elementIndex = elementIndex;
		this.reader = reader;
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub
		updateElement();
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub
		updateElement();
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub
		updateElement();
	}

	public void updateElement() {
		String attrib = ListListener.styleLabel.get(fieldIndex).getText();

		element = HTMLDocReader.bodyElements.get(elementIndex);
		System.out.println(attrib);
	}
}
