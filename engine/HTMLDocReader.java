package engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import bodyElements.BodyElement;
import bodyElements.Div;
import bodyElements.Heading;
import bodyElements.P;
import footerElements.FooterElement;
import headElements.HeadElement;
import headElements.Link;
import headElements.Title;

public class HTMLDocReader {

	String url;
	Boolean head = false;
	Boolean body = false;
	Boolean footer = false;

	public LinkedList<HeadElement> headElement = new LinkedList<HeadElement>();
	public LinkedList<BodyElement> bodyElement = new LinkedList<BodyElement>();
	public LinkedList<FooterElement> footerElement = new LinkedList<FooterElement>();

	public ArrayList<String> html;
	private ArrayList<String> headAttributes = new ArrayList<String>();
	private ArrayList<String> bodyAttributes = new ArrayList<String>();
	private ArrayList<String> footerAttributes = new ArrayList<String>();
	private String[] headElements = { "title", "base", "link", "meta", "script", "style" };
	private String[] bodyElements = { "h", "p", "br", "hr", "div", "blockquote", "pre" };
	private String[] footerElements = { "" };

	private String[] globalHTMLAttributes = { "accesskey", "class", "contenteditable", "contextmenu", "dir",
			"draggable", "dropzone", "hidden", "id", "lang", "spellcheck", "style", "tabindex", "title", "translate" };

	private boolean moreThanOneLine = false;
	private ArrayList<String> multiLineTag = new ArrayList<String>();

	public static ArrayList<Heading> headings = new ArrayList<Heading>();
	public static ArrayList<P> paragraphs = new ArrayList<P>();
	public static ArrayList<Div> divs = new ArrayList<Div>();

	public HTMLDocReader() {

	}

	public HTMLDocReader(String url) {

		super();
		this.url = url;
		System.out.println("HTMLDocReader reading page: " + url);
		readDoc(this.url);
	}

	public void readDoc(String docURL) {
		headElement.clear();
		bodyElement.clear();
		footerElement.clear();
		
		// read html file
		try (BufferedReader br = new BufferedReader(new FileReader(docURL))) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				// html.add(sCurrentLine);
				// System.out.println(sCurrentLine);
				if (sCurrentLine.equals("<head>")) {
					head = true;
				}
				if (sCurrentLine.equals("</head>")) {
					head = false;
				}
				if (sCurrentLine.equals("<body>")) {
					body = true;
				}
				if (sCurrentLine.equals("</body>")) {
					body = false;
				}

				if (head) {
					// check for head element on current line
					findHeadElementsFromDoc(sCurrentLine);
				}
				if (body) {
					// check for body element on current line
					if (!moreThanOneLine) {
						findBodyElementsFromDoc(sCurrentLine);
					} else {
						findAllLines(sCurrentLine);
					}
				}
			}
		} catch (IOException e) {

		}

	}

	public void findAllLines(String line) {
		// if the html spans multiple lines, this method will keep reading the file
		// until the complete tag has been found.
		if (line.equals("<body>")) {
			return;
		}
		multiLineTag.add(line);
		// System.out.println("Adding to array: "+line);
		if (Pattern.compile(Pattern.quote("</"), Pattern.CASE_INSENSITIVE).matcher(line).find()) {
			moreThanOneLine = false;
			String ls = "";
			for (String s : multiLineTag) {
				ls += s;
			}
			// System.out.println("ARRAYLIST AS STRING: " + ls);
			multiLineTag.clear();
			findBodyElementsFromDoc(ls);
		}

	}

	public void findHeadElementsFromDoc(String line) {

		if (line.equals("<head>") || line.equals("</head>") || line.equals("")) {
			return;
		} else {
			// System.out.println(line);
			try {
				String element = line.substring(line.indexOf("<"));
				element.substring(0, line.indexOf(">"));
				for (int i = 0; i < headElements.length; i++) {
					if (Pattern.compile(Pattern.quote("<" + headElements[i]), Pattern.CASE_INSENSITIVE).matcher(element)
							.find()) {
						createHeadElementObject(headElements[i], line);
					}
				}

			} catch (Exception e) {

			}
		}
	}

	public void findBodyElementsFromDoc(String line) {

		String s = "";

		for (int i = 0; i < bodyElements.length; i++) {
			Pattern p = Pattern.compile(">([^\"]*)</");
			Matcher m = p.matcher(line);
			while (m.find()) {
				// System.out.println(m.group(1));
				s = m.group(1);
			}
		}
		// check if tags span more than one line
		if ((s.equals(null) || s.equals("") || s == null || s == "")
				&& (!s.equals("<body>") || !s.equals("<head>") || !s.equals("<footer>"))) {
			moreThanOneLine = true;
			findAllLines(line);
		} else {
			for (int i = 0; i < bodyElements.length; i++) {
				if (Pattern.compile(Pattern.quote("<" + bodyElements[i]), Pattern.CASE_INSENSITIVE).matcher(line)
						.find()) {
					// System.out.println("Found "+bodyElements[i]+" at line: "+line);
					if (!moreThanOneLine) {
						createBodyElementObject(bodyElements[i], line);
					}
				}
			}
		}

	}

	public void findFooterElements(String line) {

	}

	public void createHeadElementObject(String element, String line) {
//		System.out.println("Creating object from " + element);

		switch (element) {
		case "title":
			addHeadElement(new Title("title"));
			break;
		case "base":
			break;
		case "link":
			addHeadElement(new Link(getHeadElementAttributes(line)));
			break;
		case "meta":
			break;
		case "script":
			break;
		case "style":
			break;
		default:
			break;
		}
	}

	public void createBodyElementObject(String element, String line) {

		String tagContent = "";
		// System.out.println("Creating object from " + element+ "LINE: "+line);
		// "h","p","br","hr","div","blockquote","pre"
		switch (element) {
		case "h":
			for (int size = 1; size < 7; size++) {
				if (Pattern.compile(Pattern.quote("<h" + size), Pattern.CASE_INSENSITIVE).matcher(line).find()) {
					tagContent = findElementContent(line);
					addBodyElement(new Heading(size, getBodyElementAttributes(line), tagContent));
				}
			}
			break;
		case "p":
			//check if a global html attribute exists in the tag
			for (int i = 0; i < globalHTMLAttributes.length; i++) {
				if (Pattern.compile(Pattern.quote(globalHTMLAttributes[i] + "="), Pattern.CASE_INSENSITIVE).matcher(line).find()) {
					// System.out.println("FOUND P ATTRIBUTE: "+globalHTMLAttributes[i]);
					// attributes.addAll(globalHtmlAttrivutes[i]+"=\""+m)
					tagContent = findElementContent(line);
				}
			}
			addBodyElement(new P(getBodyElementAttributes(line), tagContent));
			break;

		case "br":
			break;
		case "hr":
			break;
		case "div":
			break;
		case "blockquote":
			break;
		case "pre":
			break;
		case "a":
			break;
		case "audio":
			break;
		case "":
			break;
		default:
			break;
		}

	}

	public String findElementContent(String line) {
		String content = "";
		Pattern p = Pattern.compile(">([^\"]*)</");
		Matcher m = p.matcher(line);
		while (m.find()) {
			// System.out.println(m.group(1));
			content = m.group(1);
		}

		return content;
	}

	public ArrayList<String> getHeadElementAttributes(String line) {
		headAttributes.clear();
//		 System.out.println(line);
		Pattern p = Pattern.compile("\"([^\"]*)\"");
		Matcher m = p.matcher(line);
		
		if(line.contains("link")) {
			for (int i = Link.linkAttributes.length-1;i > -1; i--) {
	//			System.out.println(Link.linkAttributes[i]);
				if (Pattern.compile(Pattern.quote(Link.linkAttributes[i] + "="), Pattern.CASE_INSENSITIVE).matcher(line)
						.find()) {
					if (m.find()) {
						System.out.println("LINK ATTRIBUTE: "+Link.linkAttributes[i]+"=\""+m.group(1)+"\"");
						headAttributes.add(Link.linkAttributes[i] + "=\"" + m.group(1) + "\"");
						String linkHref = m.group(1).substring( 0,m.group(1).indexOf("\"")+1);
						System.out.println(linkHref);
						if(m.group(1).contains("href")) {
							
							System.out.println(Main.rootFolder+"/"+linkHref);
							JTextArea textArea = new JTextArea(20,200);
							textArea.setEditable(false);
							JScrollPane scrollPane = new JScrollPane(textArea);
							JComponent panel = scrollPane;
							Main.tabbedPane.addTab(linkHref, null, panel, "View linked document");
						//value = value.substring(0,value.indexOf("\""));
						}
					}
				}
			}
		}
		
		return headAttributes;
	}
	
	public ArrayList<String> getBodyElementAttributes(String line) {
		bodyAttributes.clear();
//		 System.out.println(line);
		Pattern p = Pattern.compile("\"([^\"]*)\"");
		Matcher m = p.matcher(line);
		for (int i =  globalHTMLAttributes.length-1; i > -1; i--) {
			if (Pattern.compile(Pattern.quote(globalHTMLAttributes[i] + "="), Pattern.CASE_INSENSITIVE).matcher(line)
					.find()) {
				if (m.find()) {
//					 System.out.println(globalHTMLAttributes[i]+"=\""+m.group(1)+"\"");
					bodyAttributes.add(globalHTMLAttributes[i] + "=\"" + m.group(1) + "\"");
				}
			}
		}

		return bodyAttributes;
	}

	public void addHeadElement(HeadElement element) {
		this.headElement.add(element);
	}
	
	public void addBodyElement(BodyElement element) {
		this.bodyElement.add(element);
	}
	
	public void addFooterElmeent(FooterElement element) {
		this.footerElement.add(element);
	}
	
	public String getElementType(String elementName) {
		String type = "NOT FOUND";

		System.out.println(elementName);
		for(HeadElement h : headElement) {
			if(h.getElementName().equals(elementName)) {
				return type="Head";
			}
		}
		for(BodyElement b : bodyElement) {
			if(b.getElementName().equals(elementName)) {
				return type="Body";
			}
		}
		for(FooterElement f : footerElement) {
			if(f.getElementName().equals(elementName)) {
				return type="Footer";
			}
		}
		return type;
	}
	

}
