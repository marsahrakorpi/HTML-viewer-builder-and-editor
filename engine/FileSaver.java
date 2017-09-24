package engine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.StandardCopyOption;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FileSaver extends Thread {

	public static boolean unsavedChanges;
	HTMLDocReader reader;

	public FileSaver(HTMLDocReader reader) {
		unsavedChanges = false;
		this.reader = reader;
	}

	public void save() {

		//return if there are no unsaved changes
		if(!unsavedChanges) {
			return;
		}
		
		// removes the highlight class from all attributes
		// if an element has been highlighted before, it will leave an empty class
		// attribute
		// checks for empty class attributes and removes them, will leave it if a class
		// has been assigned
		for (int i = 0; i < reader.tempDoc.body().select("*").size(); i++) {
			Element element = reader.tempDoc.body().select("*").get(i);
			element.removeClass("java-highlighted-element");
			element.removeClass(".java-highlighted-element");
			System.out.println(element);
			if (element.className().equals("")) {
				element.removeAttr("class");
			}
		}

		// removes webView CSS from document

		Elements links = reader.tempDoc.head().select("[href=\"webViewCSS/webViewHighlighter.css\"]");

		for (Element e : links) {
			e.remove();
		}

		System.out.println(reader.tempDoc);
		//Write tempDoc to the tempPage, which will then be copied over to original root folder
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(Main.tempPageURL));
			bw.write(reader.tempDoc.toString());
			bw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("Saving document failed. Changes have not been saved.");
			return;
		}
		
		//Move folder contents to project's root folder
		//Delete the webview CSS
		//Delete temp files
		try {
			FileHandler.deleteFolder(new File(Main.rootFolder));
			FileHandler.copyFolder(new File(Main.tempDir), new File(Main.rootFolder), StandardCopyOption.REPLACE_EXISTING);
			FileHandler.deleteFolder(new File(Main.rootFolder+"\\webViewCSS")); //delete the program's webView highlighter
			//DELETE TEMP FOLDER
			FileHandler.deleteFolder(new File(Main.tempDir));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		unsavedChanges = false;
		//Create new temp files and set other program variables
		reader.copyToTempFile();

	}
	



}
