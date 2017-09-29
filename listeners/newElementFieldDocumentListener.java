package listeners;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jsoup.nodes.Element;

import dialogs.EditNewElementDialog;

public class newElementFieldDocumentListener implements DocumentListener {

	int index;
	Element element;

	public newElementFieldDocumentListener(int i) {
		// TODO Auto-generated constructor stub
		this.index = i;
		this.element = EditNewElementDialog.getJsoupElement();
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

		String attributeKey = EditNewElementDialog.label.get(index).getText();
		System.out.println(attributeKey);
		if(EditNewElementDialog.field.get(index).getText().equals("") || EditNewElementDialog.field.get(index).getText()==null) {
			element.removeAttr(attributeKey);
		} else {
			System.out.println(element);
			System.out.println(attributeKey);
			System.out.println(EditNewElementDialog.field.get(index).getText());
			element.attr(attributeKey, EditNewElementDialog.field.get(index).getText());
		}
		EditNewElementDialog.fullHTML = element.toString();
		EditNewElementDialog.updateDoc();
	}

}
