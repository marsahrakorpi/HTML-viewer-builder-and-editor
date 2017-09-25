package engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

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
			copyToTempFile();
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
		// Threading here greatly reduces update lag on the JavaFX webview
		Thread t = new Thread() {
			public void run() {
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

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		};
		t.start();

		Platform.runLater(new Runnable() {
			public void run() {
				Main.updateFX(Main.tempPageURL);
			}
		});
		
	}
	

	public String readLinkDoc(String url) throws IOException {
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
					String furl = Main.tempDir + "\\" + url;
					System.out.println(furl);
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

	public void copyToTempFile() {
		System.out.println("Copying");
		System.out.println(Main.rootFolder);
			
		// copy to temp
		File sDir = new File(Main.rootFolder);
		try {
			Path tempDir = Files.createTempDirectory("HTMLEdit");
			Main.tempDir = tempDir.toString();
			FileUtils.copyDirectory(sDir, tempDir.toFile());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// copy program css files to temp

		try {
			Path webViewDir = Paths.get(Main.tempDir+"\\webViewCSS");
			Path webViewHighligherPath = Paths.get(webViewDir.toString()+"\\webViewHighlighter.css");
			Files.createDirectory(webViewDir);
			String webViewHighlighterString = ".java-highlighted-element{\r\n" + 
					"			background-color: rgb(145, 184, 247);\r\n" + 
					"			background-color: rgba(145, 184, 247, .5);\r\n" + 
					"			border: 1px solid red;\r\n" + 
					"		}	";
			Files.createFile(webViewHighligherPath);
			BufferedWriter bw = new BufferedWriter(new FileWriter(webViewHighligherPath.toFile()));
			bw.write(webViewHighlighterString);
			bw.close();
//			FileUtils.copyFileToDirectory(new File(System.getProperty("user.dir") + "\\webViewCSS"),
//					new File(Main.tempDir));
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		// set temp dir to delete on exit
		Path rootPath = Paths.get(Main.tempDir);
		try {
			Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS).sorted(Comparator.reverseOrder()).map(Path::toFile)
					.forEach(File::deleteOnExit);
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

		// parse tempDoc
		input = new File(Main.tempPageURL);
		try {
			tempDoc = Jsoup.parse(input, "UTF-8", Main.tempPageURL);
		} catch (IOException e) {
			// TODO Auto-generated catch block

		}
		try {
			Main.textArea.setText(tempDoc.toString());
		} catch (Exception e){
			
		}
		Elements webCSS = tempDoc.select("[href*=\"webViewCSS/webViewHighlighter.css\"]");
		if (webCSS.size() == 0) {
			tempDoc.select("head").append("<link rel=\"stylesheet\" href=\"webViewCSS/webViewHighlighter.css\">");
		}

		Platform.runLater(new Runnable() {
			public void run() {
				Main.updateFX(Main.tempPageURL);
			}
		});

	}

}
