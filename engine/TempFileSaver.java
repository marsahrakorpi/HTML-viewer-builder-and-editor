package engine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TempFileSaver extends Thread{

	public static boolean unsavedChanges;
	HTMLDocReader reader;
	public TempFileSaver(HTMLDocReader reader){
		unsavedChanges = false;
		this.reader = reader;
	}
	
	
	public void save() {

		//removes the highlight class from all attributes
		//if an element has been highlighted before, it will leave an empty class attribute
		//checks for empty class attributes and removes them, will leave it if a class has been assigned
		for(int i=0; i<HTMLDocReader.bodyElements.size(); i++) {
			Element element = HTMLDocReader.bodyElements.get(i);
			element.removeClass("java-highlighted-element");
			if(element.className().equals("")) {
				element.removeAttr("class");
			}
		}
		
		//removes webView CSS from document

		String href = Main.tempCSSURL;
		System.out.println(href);
		Elements links = reader.tempDoc.head().select("[href="+href+"]");
		if(!links.isEmpty()) {
			for(Element e: links) {
				e.remove();
			}
		} else {
			
		}
		
		Thread t = new Thread() {
			public void run() {

				File file = new File(Main.pageURL);
				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter(file));
					bw.write(reader.tempDoc.toString());
					bw.close();

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};
		t.start();
		unsavedChanges = false;
		File f = new File( Main.tempCSSURLAbsolute);
		f.delete();
		File m = new File(Main.tempPageURL);
		m.delete();
		
		reader.copyToTempFile(Main.pageURL);
		
	}

}
