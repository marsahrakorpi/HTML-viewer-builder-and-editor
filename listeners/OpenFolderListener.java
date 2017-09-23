package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
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
			System.out.println(fc.getSelectedFile().toString());
			if(Main.tempDir == null) {
				System.out.print("Main.tempDir is Null");
			} else {
				Path rootPath = Paths.get(Main.tempDir);
				try {
					
					Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS)
						.sorted(Comparator.reverseOrder())
						.map(Path::toFile)
						.peek(System.out::println)
						.forEach(File::delete);
				} catch (IOException e1) {
		
				}
			}
			
			Main.rootFolder = fc.getSelectedFile().toString();
			
			File folder = new File(fc.getSelectedFile().toString());
			File[] listOfFiles = folder.listFiles();

			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					if (listOfFiles[i].getName().equals("index.html") || listOfFiles[i].getName().equals("home.html")
							|| listOfFiles[i].getName().equals("start.html")) {
						Main.tempPageURL = fc.getSelectedFile().toString() + "\\" + listOfFiles[i].getName();
						System.out.print("FOUND MAIN PAGE OF"+Main.tempPageURL);
						reader.copyToTempFile();
						if (reader == null) {
							reader = new HTMLDocReader(Main.tempPageURL);
						} else {
							reader.copyToTempFile();
						}
					} else {
						
					}
				} else if (listOfFiles[i].isDirectory()) {
				}

			}

//			Main.pageURL = fc.getSelectedFile().toString();
//			
			DefaultTreeModel model = (DefaultTreeModel) Main.tree.getModel();
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
			
			root.removeAllChildren();
			root.setUserObject("Root");
			model.reload();

			CreateChildNodes ccn = new CreateChildNodes(new File(Main.tempDir), root);
			new Thread(ccn).start();

			Platform.runLater(new Runnable() {
				public void run() {
					Main.updateFX(Main.tempPageURL);
				}
			});
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
