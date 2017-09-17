package engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class HTMLDocReader {

	String url;
	Boolean head = false;
	Boolean body = false;
	Boolean footer = false;

	private File input;
	public Document doc;

	private String[] globalHTMLAttributes = { "accesskey", "class", "contenteditable", "contextmenu", "dir",
			"draggable", "dropzone", "hidden", "id", "lang", "spellcheck", "style", "tabindex", "title", "translate" };


	public static Elements headElements;
	public static Elements bodyElements;
	public static Elements footerElements;

	public HTMLDocReader() {

	}

	public HTMLDocReader(String url) {

		super();
		this.url = url;

		try {
			readDoc(this.url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void readDoc(String url) throws IOException{

//		System.out.println("URL"+url);
		input = new File(url);
		doc = Jsoup.parse(input, "UTF-8", url);
//		System.out.println(doc);
		headElements = doc.head().select("*");
		bodyElements = doc.body().select("*");
		footerElements = doc.body().select("footer");
	
	}

	public String readLinkDoc(String url) throws IOException{

		String doc = "";
		try (BufferedReader br = new BufferedReader(new FileReader(url))) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				doc+=sCurrentLine+"\n";
			}
			br.close();
		} catch (IOException e){
			System.out.println("Document "+url+" Not found.");
			url = Main.rootFolder+"/"+url;
			System.out.println("Trying with document "+url);
			try (BufferedReader br = new BufferedReader(new FileReader(url))) {
				String sCurrentLine;
				while ((sCurrentLine = br.readLine()) != null) {
					doc+=sCurrentLine+"\n";
				}
				br.close();
			} catch (IOException e1) {
				return doc = "IOException. No document found.";
			}
		}
		return doc;
	}

	

}
