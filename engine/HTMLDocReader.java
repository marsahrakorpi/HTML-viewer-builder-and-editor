package engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bodyElements.BodyElement;
import bodyElements.Div;
import bodyElements.Heading;
import bodyElements.P;
import footerElements.FooterElement;
import headElements.HeadElement;

public class HTMLDocReader {

	String url;
	Boolean head = false;
	Boolean body = false;
	Boolean footer = false;

	public LinkedList<HeadElement> headElement = new LinkedList<HeadElement>();
	public LinkedList<BodyElement> bodyElement = new LinkedList<BodyElement>();
	public LinkedList<FooterElement> footerElement = new LinkedList<FooterElement>();

	public ArrayList<String> html;
	private ArrayList<String> attributes = new ArrayList<String>();

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
		readDoc();
	}

	public void readDoc() {

		// read html file
		try (BufferedReader br = new BufferedReader(new FileReader(url))) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				// html.add(sCurrentLine);
				 //System.out.println(sCurrentLine);
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

	private String globalHTMLAttribute(int i) {

		return null;
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

		if (line.equals("<head>") || line.equals("</head>") || line.equals("")){
			return;
		} else {
			//System.out.println(line);
			try {
				String element = line.substring(line.indexOf("<"));
				element.substring(0, line.indexOf(">"));
				for(int i=0; i<headElements.length; i++) {
					if (Pattern.compile(Pattern.quote("<" + headElements[i]), Pattern.CASE_INSENSITIVE).matcher(element).find()) {
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
		//check if tags span more than one line
		if ((s.equals(null) || s.equals("") || s == null || s == "")
				&& (!s.equals("<body>") || !s.equals("<head>") || !s.equals("<footer>"))) {
			moreThanOneLine = true;
			findAllLines(line);
		} else {
			for (int i = 0; i < bodyElements.length; i++) {
				if (Pattern.compile(Pattern.quote("<" + bodyElements[i]), Pattern.CASE_INSENSITIVE).matcher(line).find()) {
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
		System.out.println("Creating object from " + element);
//		for(int i=0; i<headElements.length; i++) {
//			System.out.println("case \""+headElements[i]+"\":\n\tbreak;");
//		}
		switch (element) {
			case "title":
				break;
			case "base":
				break;
			case "link":
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
					addBodyElement(new Heading(size, getElementAttributes(line), tagContent));
				}
			}
			break;
		case "p":
			for (int i = 0; i < globalHTMLAttributes.length; i++) {
				if (Pattern.compile(Pattern.quote(globalHTMLAttributes[i] + "="), Pattern.CASE_INSENSITIVE)
						.matcher(line).find()) {
					// System.out.println("FOUND P ATTRIBUTE: "+globalHTMLAttributes[i]);
					// attributes.addAll(globalHtmlAttrivutes[i]+"=\""+m)
					tagContent = findElementContent(line);
				}
			}
			addBodyElement(new P(getElementAttributes(line), tagContent));
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

	public ArrayList<String> getElementAttributes(String line) {
		attributes.clear();
		// System.out.println(line);
		Pattern p = Pattern.compile("\"([^\"]*)\"");
		Matcher m = p.matcher(line);
		for (int i = 0; i < globalHTMLAttributes.length; i++) {
			if (Pattern.compile(Pattern.quote(globalHTMLAttributes[i] + "="), Pattern.CASE_INSENSITIVE).matcher(line)
					.find()) {
				while (m.find()) {
					// System.out.println(globalHTMLAttributes[i]+"=\""+m.group(1)+"\"");
					attributes.add(globalHTMLAttributes[i] + "=\"" + m.group(1) + "\"");
				}
			}
		}

		return attributes;
	}

	public void addBodyElement(BodyElement element) {
		this.bodyElement.add(element);
	}

}
