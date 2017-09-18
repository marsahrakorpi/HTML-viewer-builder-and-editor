package listeners;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jsoup.nodes.Element;

import engine.HTMLDocReader;

public class FieldDocumentListener implements DocumentListener {

	int fieldIndex, elementIndex;
	String tempFile;
	Element element;
	HTMLDocReader reader;
	private boolean debug = false;

	public FieldDocumentListener(int fieldIndex, int elementIndex, HTMLDocReader reader) {
		this.fieldIndex = fieldIndex;
		this.elementIndex = elementIndex;
		this.reader = reader;
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		updateElement(ListListener.field.get(fieldIndex).getText());
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		updateElement(ListListener.field.get(fieldIndex).getText());
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		updateElement(ListListener.field.get(fieldIndex).getText());
	}

	public void updateElement(String value) {

		String attrib = ListListener.label.get(fieldIndex).getText();

		element = HTMLDocReader.bodyElements.get(elementIndex);
		// System.out.println("Attrib: "+attrib+" Value:
		// "+ListListener.field.get(fieldIndex).getText());
		element.attr(attrib, ListListener.field.get(fieldIndex).getText());
		// System.out.println("ATTRIBUTE VALUE IS NOW SET TO: "+element.id());
		// System.out.println(HTMLDocReader.bodyElements.get(elementIndex).outerHtml());

		// WRITE CHANGES TO TEMP FILE
		try {
			if (debug) {
				System.out.println("tmp is" + tempFile);
			}
			try {
				File file = new File(tempFile);
				file.delete();
			} catch (Exception e) {

			}
			File temp = File.createTempFile("HTMLEditTemp", "tmp");
			tempFile = temp.getAbsolutePath();
			if (debug) {
				System.out.println("tmp is now" + tempFile);
			}
			temp.deleteOnExit();
			BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
			bw.write("\n<html>");
			bw.write("\n" + HTMLDocReader.headElements.get(0));
			bw.write("\n" + HTMLDocReader.bodyElements.get(0));
			bw.write("\n</html>");
			bw.close();

			reader.readDoc(tempFile);
			reader.readLinkDoc(tempFile);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
