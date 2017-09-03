package engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bodyElements.BodyElement;
import bodyElements.Div;
import bodyElements.Heading;
import bodyElements.P;

public class HTMLDocReader {

	String url;
	Boolean head = false;
	Boolean body = false;
	Boolean footer = false;

//	public LinkedList<HeaderElement> headerElement = new LinkedList<HeaderElement>();
	public LinkedList<BodyElement> bodyElement = new LinkedList<BodyElement>();
//	public LinkedList<FooterElement> footerElement = new LinkedList<FooterElement>();
	
	private String[] headElements = {"<title", "<base", "<link", "<meta", "<script", "<style"};
	private String[] bodyElements = {"h","p","br","hr","div","blockquote","pre"};
	private String[] footerElements = {""};
	
	private String[] globalHTMLAttributes = {"accesskey","class","contenteditable","contextmenu","dir","draggable","dropzone","hidden","id","lang","spellcheck","style","tabindex","title","translate"};
	
	public static ArrayList<Heading> headings = new ArrayList<Heading>();
	public static ArrayList<P> paragraphs = new ArrayList<P>();
	public static ArrayList<Div> divs = new ArrayList<Div>();

	
	public HTMLDocReader() {
		
	}
	
	public HTMLDocReader(String url) {
	
		super();
		this.url = url;
		System.out.println("HTMLDocReader reading page: "+url);
		readDoc();
	}
	
	public void readDoc() {
				
		//read html file
		try (BufferedReader br = new BufferedReader(new FileReader(url))){
			String sCurrentLine;
			while((sCurrentLine  = br.readLine()) != null) {
				//html.add(sCurrentLine);
				//System.out.println(sCurrentLine);
				if(sCurrentLine.equals("<head>")) {
					head = true;
				}
				if(sCurrentLine.equals("</head>")) {
					head = false;
				}
				if(sCurrentLine.equals("<body>")) {
					body=true;
				}
				if(sCurrentLine.equals("</body>")) {
					body=false;
				}
				
				if(head) {
					//check for head element on current line
					findHeadElementsFromDoc(sCurrentLine);
				}
				if(body) {
					//check for body element on current line
					findBodyElementsFromDoc(sCurrentLine);
				}
			}
		} catch (IOException e){
			
		}
		
		for(int i=0; i<headings.size(); i++) {
			System.out.println(headings.get(i).getHtml());
		}
		for(int i=0; i<paragraphs.size(); i++) {
			System.out.println(paragraphs.get(i).getHtml());
		}

	}
	
	private String globalHTMLAttribute(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	public void findHeadElementsFromDoc(String line) {
		
		for(int i=0; i<headElements.length; i++) {
			if(line.equals(headElements[i])) {
				//System.out.println("Found element "+headElements[i]);
			}
		}
		
	}
	
	public void findBodyElementsFromDoc(String line) {
		
		for(int i=0; i<bodyElements.length; i++) {
			if(Pattern.compile(Pattern.quote("<"+bodyElements[i]), Pattern.CASE_INSENSITIVE).matcher(line).find()) {
				//System.out.println("Found "+bodyElements[i]+" at line: "+line);
				createBodyElementObject(bodyElements[i], line);
			}
		}
	}
	
	public void findFooterElements(String line) {
		
	}
	
	
	public void createBodyElementObject(String element, String line) {
		ArrayList<String> attributes = new ArrayList<String>();
		attributes.clear();
		String tagText = "";
		//System.out.println("Creating object from " + element);
		//"h","p","br","hr","div","blockquote","pre"
		switch(element) {
			case "h":

				for(int i=0; i<globalHTMLAttributes.length; i++) {
					if(Pattern.compile(Pattern.quote(globalHTMLAttributes[i]+"="), Pattern.CASE_INSENSITIVE).matcher(line).find()) {
						//System.out.println("FOUND ATTRIBUTE: "+globalHTMLAttributes[i]);
						Pattern p = Pattern.compile("\"([^\"]*)\"");
						Matcher m = p.matcher(line);
						while (m.find()) {
						  //System.out.println(globalHTMLAttributes[i]+"=\""+m.group(1)+"\"");
							attributes.add(globalHTMLAttributes[i]+"=\""+m.group(1)+"\"");
						}
					}
				}
				for(int i=1; i<7; i++){
					if(Pattern.compile(Pattern.quote("<h"+i), Pattern.CASE_INSENSITIVE).matcher(line).find()) {
						Pattern p = Pattern.compile(">([^\"]*)</h");
						Matcher m = p.matcher(line);
						while (m.find()) {
						    //System.out.println(m.group(1));
						    tagText = m.group(1);
						}
						addBodyElement(new Heading(i, attributes, tagText));
						
					}
				}
				break;
			case "p":
				for(int i=0; i<globalHTMLAttributes.length; i++) {
					if(Pattern.compile(Pattern.quote(globalHTMLAttributes[i]+"="), Pattern.CASE_INSENSITIVE).matcher(line).find()) {
						System.out.println("FOUND P ATTRIBUTE: "+globalHTMLAttributes[i]);
						Pattern p = Pattern.compile(globalHTMLAttributes[i]+"=\"([^\"]*)\"");
						Matcher m = p.matcher(line);
						while (m.find()) {
							attributes.add(globalHTMLAttributes[i]+"=\""+m.group(1)+"\"");
						}
					}
				}
				addBodyElement(new P(attributes, tagText));
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
			default:
				break;
		}
		
	}
	
	public void addBodyElement(BodyElement element) {
		this.bodyElement.add(element);
	}
	
}
