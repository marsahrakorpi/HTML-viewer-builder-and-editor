package engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class HTMLDocReader extends Thread {

	String url;
	Boolean head = false;
	Boolean body = false;
	Boolean footer = false;

	private File input;
	public Document doc;
	private String docStr;
	public static Elements headElements;
	public static Elements bodyElements;
	public static Elements footerElements;

	public HTMLDocReader(String url) {

		super();
		this.url = url;

		try {
			readDoc(this.url);
		} catch (IOException e) {

		}
	}

	public void readDoc(String url) throws IOException {

		 System.out.println("doc Reader reading URL"+url);
		input = new File(url);
		doc = Jsoup.parse(input, "UTF-8", url);
//		 System.out.println(doc);
		headElements = doc.head().select("*");
		bodyElements = doc.body().select("*");
		footerElements = doc.body().select("footer");

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

}
