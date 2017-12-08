package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import engine.HTMLDocReader;
import engine.Main;
import javafx.application.Platform;

public class OpenFileListener implements ActionListener {
	
	HTMLDocReader reader;
	
	public OpenFileListener(HTMLDocReader reader) {
		this.reader = reader;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setCurrentDirectory(new File( System.getProperty("user.dir")));
		int returnVal = fc.showOpenDialog(fc);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			Main.pageURL = fc.getSelectedFile().toString();
			DefaultTreeModel model = (DefaultTreeModel) Main.tree.getModel();
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
			root.removeAllChildren();
			root.setUserObject(fc.getSelectedFile().getName());
			model.reload();
			Main.pageURL = fc.getSelectedFile().getName();
			try {
				reader.readDoc(fc.getSelectedFile().toString());
				Platform.runLater(new Runnable() {
					public void run() {
						Main.updateFX(fc.getSelectedFile().toString());
					}
				});
			} catch (IOException e) {
				return;
			}
		}
	}

}
