package engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import bodyElements.Div;
import bodyElements.Heading;
import bodyElements.P;

public class HTMLDocReader {

	String url;
	Boolean head = false;
	Boolean body = false;
	Boolean footer = false;

	private File input;
	private Document doc;
	
//	public LinkedList<HeadElement> headElement = new LinkedList<HeadElement>();
//	public LinkedList<BodyElement> bodyElement = new LinkedList<BodyElement>();
//	public LinkedList<FooterElement> footerElement = new LinkedList<FooterElement>();
//
//	public ArrayList<String> html;
//	private ArrayList<String> headAttributes = new ArrayList<String>();
//	private ArrayList<String> bodyAttributes = new ArrayList<String>();
//	private ArrayList<String> footerAttributes = new ArrayList<String>();
//	private String[] headElements = { "title", "base", "link", "meta", "script", "style" };
//	private String[] bodyElements = { "h", "p", "br", "hr", "div", "blockquote", "pre" };
//	private String[] footerElements = { "" };

	private String[] globalHTMLAttributes = { "accesskey", "class", "contenteditable", "contextmenu", "dir",
			"draggable", "dropzone", "hidden", "id", "lang", "spellcheck", "style", "tabindex", "title", "translate" };

//	private boolean moreThanOneLine = false;
//	private ArrayList<String> multiLineTag = new ArrayList<String>();

	public static ArrayList<Heading> headings = new ArrayList<Heading>();
	public static ArrayList<P> paragraphs = new ArrayList<P>();
	public static ArrayList<Div> divs = new ArrayList<Div>();
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
//			System.out.println("Document "+url+" Not found.");
			url = Main.rootFolder+"/"+url;
//			System.out.println("Trying with document "+url);
			try (BufferedReader br = new BufferedReader(new FileReader(url))) {
				String sCurrentLine;
				while ((sCurrentLine = br.readLine()) != null) {
					doc+=sCurrentLine+"\n";
				}
				br.close();
			} catch (IOException e1) {
				doc = "IOException. No document found.";
			}
		}
		return doc;
	}
//	public void readDoc(String docURL) {
//		headElement.clear();
//		bodyElement.clear();
//		footerElement.clear();
//		System.out.println("READING DOCUMENT" + docURL);
//		// read html file
//		try (BufferedReader br = new BufferedReader(new FileReader(docURL))) {
//			String sCurrentLine;
//			while ((sCurrentLine = br.readLine()) != null) {
//				// html.add(sCurrentLine);
//				 System.out.println(sCurrentLine);
//				if (sCurrentLine.equals("<head>")) {
//					head = true;
//				}
//				if (sCurrentLine.equals("</head>")) {
//					head = false;
//				}
//				if (sCurrentLine.equals("<body>")) {
//					body = true;
//				}
//				if (sCurrentLine.equals("</body>")) {
//					body = false;
//				}
//
//				if (head) {
//					// check for head element on current line
//					findHeadElementsFromDoc(sCurrentLine);
//				}
//				if (body) {
//					// check for body element on current line
//					if (!moreThanOneLine) {
//						findBodyElementsFromDoc(sCurrentLine);
//					} else {
//						findAllLines(sCurrentLine);
//					}
//				}
//			}
//			br.close();
//		} catch (IOException e) {
//
//		}
//	}
//	
//	public String readDocToString(String docURL) {
//		System.out.println("READING DOCUMENT "+docURL);
//		String documentAsString = "";
//		try (BufferedReader reader = new BufferedReader(new FileReader(docURL))) {
//			String sCurrentLine;
//			while((sCurrentLine = reader.readLine()) != null) {
//				documentAsString+=sCurrentLine+"\n";
//			}
//			reader.close();
//		} catch (IOException e) {
//			documentAsString = "ERROR READING FILE.";
//		} 
//		
//		return documentAsString;
//		
//	}
//
//	public void findAllLines(String line) {
//		// if the html spans multiple lines, this method will keep reading the file
//		// until the complete tag has been found.
//		if (line.equals("<body>")) {
//			return;
//		}
//		multiLineTag.add(line);
//		// System.out.println("Adding to array: "+line);
//		if (Pattern.compile(Pattern.quote("</"), Pattern.CASE_INSENSITIVE).matcher(line).find()) {
//			moreThanOneLine = false;
//			String ls = "";
//			for (String s : multiLineTag) {
//				ls += s;
//			}
//			// System.out.println("ARRAYLIST AS STRING: " + ls);
//			multiLineTag.clear();
//			findBodyElementsFromDoc(ls);
//		}
//
//	}
//
//	public void findHeadElementsFromDoc(String line) {
//		if (line.equals("<head>") || line.equals("</head>") || line.equals("")) {
//			return;
//		} else {
////			 System.out.println(line);
//			try {
//				String element = line.substring(line.indexOf("<"));
//				element.substring(0, line.indexOf(">"));
//				for (int i = 0; i < headElements.length; i++) {
//					if (Pattern.compile(Pattern.quote("<" + headElements[i]), Pattern.CASE_INSENSITIVE).matcher(element)
//							.find()) {
//						createHeadElementObject(headElements[i], line);
//					}
//				}
//
//			} catch (Exception e) {
//
//			}
//		}
//	}
//
//	public void findBodyElementsFromDoc(String line) {
//
//		String s = "";
//
//		for (int i = 0; i < bodyElements.length; i++) {
//			Pattern p = Pattern.compile(">([^\"]*)</");
//			Matcher m = p.matcher(line);
//			while (m.find()) {
//				// System.out.println(m.group(1));
//				s = m.group(1);
//			}
//		}
//		// check if tags span more than one line
//		if ((s.equals(null) || s.equals("") || s == null || s == "")
//				&& (!s.equals("<body>") || !s.equals("<head>") || !s.equals("<footer>"))) {
//			moreThanOneLine = true;
//			findAllLines(line);
//		} else {
//			for (int i = 0; i < bodyElements.length; i++) {
//				if (Pattern.compile(Pattern.quote("<" + bodyElements[i]), Pattern.CASE_INSENSITIVE).matcher(line)
//						.find()) {
//					// System.out.println("Found "+bodyElements[i]+" at line: "+line);
//					if (!moreThanOneLine) {
//						createBodyElementObject(bodyElements[i], line);
//					}
//				}
//			}
//		}
//
//	}
//
//	public void findFooterElements(String line) {
//
//	}
//
//	public void createHeadElementObject(String element, String line) {
////		System.out.println("Creating object from " + element);
//
//		switch (element) {
//		case "title":
//			addHeadElement(new Title(findElementContent(line)));
//			break;
//		case "base":
//			break;
//		case "link":
//			addHeadElement(new Link(getHeadElementAttributes(line)));
//			break;
//		case "meta":
//			break;
//		case "script":
//			break;
//		case "style":
//			break;
//		default:
//			break;
//		}
//	}
//
//	public void createBodyElementObject(String element, String line) {
//
//		String tagContent = "";
//		// System.out.println("Creating object from " + element+ "LINE: "+line);
//		// "h","p","br","hr","div","blockquote","pre"
//		switch (element) {
//		case "h":
//			for (int size = 1; size < 7; size++) {
//				if (Pattern.compile(Pattern.quote("<h" + size), Pattern.CASE_INSENSITIVE).matcher(line).find()) {
//					tagContent = findElementContent(line);
//					addBodyElement(new Heading(size, getBodyElementAttributes(line), tagContent));
//				}
//			}
//			break;
//		case "p":
//			//check if a global html attribute exists in the tag
//			for (int i = 0; i < globalHTMLAttributes.length; i++) {
//				if (Pattern.compile(Pattern.quote(globalHTMLAttributes[i] + "="), Pattern.CASE_INSENSITIVE).matcher(line).find()) {
//					 System.out.println("FOUND P ATTRIBUTE: "+globalHTMLAttributes[i]);
//					tagContent = findElementContent(line);
//				}
//			}
//			addBodyElement(new P(getBodyElementAttributes(line), tagContent));
//			break;
//
//		case "br":
//			break;
//		case "hr":
//			break;
//		case "div":
//			break;
//		case "blockquote":
//			break;
//		case "pre":
//			break;
//		case "a":
//			break;
//		case "audio":
//			break;
//		case "":
//			break;
//		default:
//			break;
//		}
//
//	}
//
//	public String findElementContent(String line) {
//		String content = "";
//		Pattern p = Pattern.compile(">([^\"]*)</");
//		Matcher m = p.matcher(line);
//		while (m.find()) {
//			// System.out.println(m.group(1));
//			content = m.group(1);
//		}
//
//		return content;
//	}
//
//	public ArrayList<String> getHeadElementAttributes(String line) {
//		headAttributes.clear();
//		System.out.println(line);
//		Pattern p = Pattern.compile("\"([^\"]*)\"");
//		Matcher m = p.matcher(line);
//		
//		if(line.contains("title")) {
//			line = line.substring(line.indexOf(">")+1);
//			line = line.substring(0, line.indexOf("<"));
//			System.out.println("TITLE FOUND:" +line);
//			headAttributes.add("title="+"\""+line+"\"");
//			System.out.println("RETURNING");
//			return headAttributes;
//		}
//		
//		if(line.contains("link")) {
//			for (int i = Link.linkAttributes.length-1;i > -1; i--) {
////				System.out.println(Link.linkAttributes[i]);
//				if (Pattern.compile(Pattern.quote(Link.linkAttributes[i] + "="), Pattern.CASE_INSENSITIVE).matcher(line)
//						.find()) {
//					if (m.find()) {
////						System.out.println("Attribute found:" + Link.linkAttributes[i] + "=\"" + m.group(1) + "\"");
//						createTabbedPaneFromLink(Link.linkAttributes[i] + "=\"" + m.group(1) + "\"");
//						headAttributes.add(Link.linkAttributes[i] + "=\"" + m.group(1) + "\"");
//					}
//				}
//			}
//		}
//
//		return headAttributes;
//	}
//	
//	public void createTabbedPaneFromLink(String link) {
////		System.out.println("CREATING TABBED PANE FROM "+link);
//		Pattern p = Pattern.compile("\"([^\"]*)\"");
//		Matcher m = p.matcher(link);
//
//		if(link.contains("href=")) {
//			if (Pattern.compile(Pattern.quote("href="), Pattern.CASE_INSENSITIVE).matcher(link)
//					.find()) {
//				if(m.find()) {
////					System.out.println(m.group(1));
//					System.out.println("LINK LINK LINK :::"+m.group(1));
//					//read linked file
//					JTextArea textArea = new JTextArea(20, 200);
//					textArea.setEditable(false);
////					System.out.println(Main.rootFolder+"/"+m.group(1));
//					//textArea.setText(readDocToString(Main.rootFolder+"/"+m.group(1)));
//					JScrollPane scrollPane = new JScrollPane(textArea);
//					JComponent panel = scrollPane;
//					Main.tabbedPane.addTab(m.group(1), null, panel, "View Linked File");
//				}
//			}
//		} 		
//		
//	}
//	
//	public ArrayList<String> getBodyElementAttributes(String line) {
//		bodyAttributes.clear();
//		List<String> list = new ArrayList<String>();
//		Pattern x = Pattern.compile("\"([^\"]*)\"");
//		Matcher v = x.matcher(line);
//		Matcher m = Pattern.compile("([a-zA-Z]*)=").matcher(line);
//		while (m.find()) {
//			list.add(m.group(1));
//			if(v.find()) {
//				list.add(v.group(1));
//			}
//		}
//		for(int i=0; i<list.size(); i+=2) {
////			System.out.println(list.get(i)+"=\""+list.get(i+1)+"\"");
//			bodyAttributes.add(list.get(i)+"=\""+list.get(i+1)+"\"");
//		}
//		
//		return bodyAttributes;
//	}
//
//	public void addHeadElement(HeadElement element) {
//		this.headElement.add(element);
//	}
//	
//	public void addBodyElement(BodyElement element) {
//		this.bodyElement.add(element);
//	}
//	
//	public void addFooterElmeent(FooterElement element) {
//		this.footerElement.add(element);
//	}
//	
//	public String getElementType(String elementName) {
//		String type = "NOT FOUND";
//
////		System.out.println(elementName);
//		for(HeadElement h : headElement) {
//			if(h.getElementName().equals(elementName)) {
//				return type="Head";
//			}
//		}
//		for(BodyElement b : bodyElement) {
//			if(b.getElementName().equals(elementName)) {
//				return type="Body";
//			}
//		}
//		for(FooterElement f : footerElement) {
//			if(f.getElementName().equals(elementName)) {
//				return type="Footer";
//			}
//		}
//		return type;
//	}
	

}
