package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jsoup.nodes.Element;

import engine.HTMLDocReader;

public class StyleFieldActionListener implements ActionListener {
	;
	int fieldIndex, elementIndex;
	Element element;

	public StyleFieldActionListener(String type, int elementIndex) {
		this.elementIndex = elementIndex;
		this.element = HTMLDocReader.bodyElements.get(elementIndex);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Action");

	}

}
