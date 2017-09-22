package engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import javafx.application.Platform;

public class HTMLDocReader extends Thread {

	String url;
	Boolean head = false;
	Boolean body = false;
	Boolean footer = false;

	private File input;
	public Document doc, tempDoc;
	private String docStr;
	public static Elements headElements;
	public static Elements bodyElements;
	public static Elements footerElements;

	public HTMLDocReader(String url) {

		super();
		this.url = url;

		try {
			readDoc(this.url);
			copyToTempFile(this.url);
		} catch (IOException e) {

		}
	}

	public void readDoc(String url) throws IOException {

		input = new File(url);
		doc = Jsoup.parse(input, "UTF-8", url);
		headElements = doc.head().select("*");
		bodyElements = doc.body().select("*");
		footerElements = doc.body().select("footer");

	}
	
	public void updateTempDoc() {

		 File file = new File(Main.tempPageURL);
		 try {
		 BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		 bw.write(tempDoc.toString());
		 bw.close();

		 } catch (Exception e) {
		 e.printStackTrace();
		 }
		
		 input = new File(Main.tempPageURL);
		 try {
			tempDoc = Jsoup.parse(input, "UTF-8", Main.tempPageURL);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		Platform.runLater(new Runnable() {
			public void run() {
				Main.updateFX(Main.tempPageURL);
			}
		});
	}
	
	public String readLinkDoc(String url) throws IOException {
		// System.out.println("URL"+url);
		docStr = "";
		this.url = url;
		Thread t = new Thread() {
			public void start() {
				try (BufferedReader br = new BufferedReader(new FileReader(url))) {
					String sCurrentLine;
					while ((sCurrentLine = br.readLine()) != null) {
						docStr += sCurrentLine + "\n";
					}
					br.close();
				} catch (IOException e) {
					String furl = Main.rootFolder + "/" + url;
					try (BufferedReader br = new BufferedReader(new FileReader(furl))) {
						String sCurrentLine;
						while ((sCurrentLine = br.readLine()) != null) {
							docStr += sCurrentLine + "\n";
						}
						br.close();
					} catch (IOException e1) {
						docStr = "IOException. No document found.";
					}
				}
			}
		};
		t.start();
		return docStr;
	}
	
	void setDeletes(File file) {
	    File[] contents = file.listFiles();
	    if (contents != null) {
	        for (File f : contents) {
	        	System.out.println(f);
	        	setDeletes(f);
	        }
	    }
	    file.deleteOnExit();
	}
	
	public void copyToTempFile(String url){
		System.out.println("Copying");
		File sDir = new File(Main.rootFolder);
		try {
			Path tempDir = Files.createTempDirectory("HTMLEdit");
			Main.tempDir = tempDir.toString();
			FileUtils.copyDirectory(sDir, tempDir.toFile());
			
			System.out.println();
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// scan for index.html or equivalent in root folder
		File folder = new File(Main.tempDir);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				if (listOfFiles[i].getName().equals("index.html") || listOfFiles[i].getName().equals("home.html")
						|| listOfFiles[i].getName().equals("start.html")) {
					Main.tempPageURL = Main.tempDir + "\\" + listOfFiles[i].getName();
				}
			} else if (listOfFiles[i].isDirectory()) {

			}
		}
		
		System.out.println(Main.tempPageURL);
		input = new File(Main.tempPageURL);
		try {
			doc = Jsoup.parse(input, "UTF-8", Main.tempPageURL);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Platform.runLater(new Runnable() {
			public void run() {
				Main.updateFX(Main.tempPageURL);
			}
		});

//		
//		DefaultTreeModel model = (DefaultTreeModel) Main.tree.getModel();
//		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
//		
//		root.removeAllChildren();
//		root.setUserObject(Main.tempDir);
//		model.reload();
//		CreateChildNodes ccn = new CreateChildNodes(new File(Main.tempDir), root);
//		new Thread(ccn).start();
	}

}
