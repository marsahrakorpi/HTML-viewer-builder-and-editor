package listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import javax.swing.JCheckBox;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import engine.FileSaver;
import engine.HTMLDocReader;
import engine.Main;
import javafx.application.Platform;

public class CheckListener extends Thread implements ItemListener {

	int elementIndex;
	Element element;
	HTMLDocReader reader;
	JCheckBox checkBox;

	public CheckListener(int elementIndex, HTMLDocReader reader, JCheckBox cb) {
		this.elementIndex = elementIndex;
		this.reader = reader;
		this.checkBox = cb;

		if (HTMLDocReader.bodyElements.get(elementIndex).hasAttr(checkBox.getText())) {
			System.out.println("Doing click");
			checkBox.doClick();
		}

	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		element = HTMLDocReader.tempDoc.body().select("*").get(elementIndex);
		String attribute = checkBox.getText();
		if (checkBox.isSelected()) {
//			System.out.println("SETTING TO HIDDEN");
			// Add atribute to element
			element.attr(checkBox.getText(), "");
//			System.out.println("ELEMENT ATTRIBUTES: " + element.outerHtml());
		} else if(!checkBox.isSelected()){
			// remove attribute from element
//			System.out.println("REMOVING HIDDEN");
			Attributes a = element.attributes();
			List<Attribute> b = a.asList();
			for (int i = 0; i < b.size(); i++) {
				if (b.get(i).getKey().equals(attribute)) {
					element.removeAttr(b.get(i).getKey());
				}
			}
		}

		Thread t = new Thread() {
			public void run() {
				FileSaver.unsavedChanges = true;
//				System.out.println("Running CheckListener Thread");
				File file = new File(Main.tempPageURL);
				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter(file));
					bw.write(HTMLDocReader.tempDoc.toString());
					bw.close();

					reader.readDoc(Main.tempPageURL);
					Main.textArea.setText(reader.doc.toString());
					Platform.runLater(new Runnable() {
						public void run() {
							Main.updateFX(Main.tempPageURL);
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
