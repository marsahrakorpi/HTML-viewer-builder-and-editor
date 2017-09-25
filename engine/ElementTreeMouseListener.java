package engine;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jsoup.nodes.Element;

public class ElementTreeMouseListener extends Thread implements MouseListener {

	HTMLDocReader reader;
	BodyElementInfo bElement;
	TreeSelectionEvent event;
	JPopupMenu popup;
	JMenuItem menuItem;

	public ElementTreeMouseListener(HTMLDocReader reader) {
		this.reader = reader;

		popup = new JPopupMenu();
		menuItem = new JMenuItem("Edit Element");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				editBodyElement(bElement);
			}
		});
		popup.add(menuItem);
		menuItem = new JMenuItem("Remove Element");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				removeBodyElement(bElement);
			}
		});
		popup.add(menuItem);

	}

	private void editBodyElement(BodyElementInfo bElement) {
		Element element = reader.tempDoc.body().select("*").get(bElement.index);
		FileSaver.unsavedChanges = true;
	}

	private void removeBodyElement(BodyElementInfo bElement) {
		Element element = reader.tempDoc.body().select("*").get(bElement.index);
		System.out.println(element);
		reader.tempDoc.body().select("*").get(bElement.index).remove();
		reader.updateTempDoc();
		Main.updateFrame();

		FileSaver.unsavedChanges = true;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if (SwingUtilities.isRightMouseButton(e)) {
			System.out.println("Right Mouse Clicked at" + Main.elementTree.getPathForLocation(e.getX(), e.getY()));
			TreePath p = Main.elementTree.getPathForLocation(e.getX(), e.getY());
			if(p==null) {
				return;
			}
			if (p.getPathCount() > 2) {
				if (p.getPathComponent(1).toString().equals("Body")) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) p.getLastPathComponent();
					if (node == null) {
						System.out.println("node null");
						return;
					}

					Object nodeInfo = node.getUserObject();
					bElement = (BodyElementInfo) nodeInfo;
					if(node.isLeaf()) {
						popup.show(Main.elementTree.getComponentAt(e.getX(), e.getY()), e.getX(), e.getY());
					}
//					popup.show(Main.elementTree.getComponent(), e.getX(), e.getY());

				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
