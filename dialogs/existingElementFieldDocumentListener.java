package dialogs;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jsoup.nodes.Element;

public class existingElementFieldDocumentListener implements DocumentListener {

	int index;
	Element element;

	public existingElementFieldDocumentListener(int i) {
		// TODO Auto-generated constructor stub
		this.index = i;
		this.element = EditElementDialog.getJsoupElement();
	}

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
		element = EditElementDialog.getJsoupElement();
		String attributeKey = EditElementDialog.label.get(index).getText();
		if(EditElementDialog.field.get(index).getText().equals("") || EditElementDialog.field.get(index).getText()==null) {
		} else {
			element.attr(attributeKey, EditElementDialog.field.get(index).getText());
		}
		EditElementDialog.fullHTML = element.toString();
		EditElementDialog.updateDoc();
	}

}
