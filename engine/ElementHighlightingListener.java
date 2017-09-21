package engine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javafx.application.Platform;

public class ElementHighlightingListener extends Thread implements TreeSelectionListener {

	HTMLDocReader reader;
	
	public ElementHighlightingListener(HTMLDocReader reader) {
		// TODO Auto-generated constructor stub
		this.reader = reader;
		try {
			removeAllHighlights();
		} catch (Exception e) {

		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		// TODO Auto-generated method stub
		removeAllHighlights();
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
				
//				System.out.println(HTMLDocReader.bodyElements.get(bElement.index));
				highlightElement(HTMLDocReader.bodyElements.get(bElement.index));
			}
		}
	}

	private void highlightElement(Element element) {
		
		element.addClass("java-highlighted-element");
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
	
	private void removeAllHighlights(){
		Elements elements = HTMLDocReader.bodyElements;
		for(Element e: elements) {
			//removes the highlight css class
			e.removeClass("java-highlighted-element");
			//if there are no classes assigned to the element, remove the class attribute from the tag
			//if other classes have been assigned to the tag, this will not remove them.
			if(e.attr("class").equals("")) {
				e.removeAttr("class");
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
