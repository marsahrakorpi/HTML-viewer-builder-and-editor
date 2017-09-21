package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import engine.CreateChildNodes;
import engine.HTMLDocReader;
import engine.Main;
import javafx.application.Platform;

public class OpenFolderListener implements ActionListener {

	HTMLDocReader reader;
	private String pageURL;
	public OpenFolderListener(HTMLDocReader reader) {
		this.reader = reader;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			
			Main.pageURL = fc.getSelectedFile().toString();
			
			DefaultTreeModel model = (DefaultTreeModel) Main.tree.getModel();
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
			
			root.removeAllChildren();
			root.setUserObject(fc.getSelectedFile().getName());
			model.reload();
			
			Main.setWorkDirectories(fc.getSelectedFile().toString());
			CreateChildNodes ccn = new CreateChildNodes(fc.getSelectedFile(), root);
			new Thread(ccn).start();
			
			File folder = new File(fc.getSelectedFile().toString());
			File[] listOfFiles = folder.listFiles();

			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					if (listOfFiles[i].getName().equals("index.html") || listOfFiles[i].getName().equals("home.html")
							|| listOfFiles[i].getName().equals("start.html")) {
						pageURL = fc.getSelectedFile().toString() + "\\" + listOfFiles[i].getName();
						if (reader == null) {
							reader = new HTMLDocReader(pageURL);
						} else {
							try {
								reader.readDoc(pageURL);
							} catch (IOException e1) {

							}
						}
					} else {
						pageURL = "";
					}
				} else if (listOfFiles[i].isDirectory()) {
				}
				
				Platform.runLater(new Runnable() {
					public void run() {
						Main.updateFX(pageURL);
					}
				});
			}
			try {
				FileInputStream in;
				FileOutputStream out;
				
				in = new FileInputStream("config.properties");
				Properties props = new Properties();
				props.load(in);
				in.close();
				
				out = new FileOutputStream("config.properties");
				props.setProperty("rootFolder", fc.getSelectedFile().toString());
				props.store(out, null);
				out.close();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

		}
	}

}
