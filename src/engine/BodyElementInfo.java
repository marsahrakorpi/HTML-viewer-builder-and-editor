package engine;

import java.util.List;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

public class BodyElementInfo {
	public String elementName;
	public int index;
	private Element element;
	HTMLDocReader reader;
	public BodyElementInfo(String elementName, int index, HTMLDocReader reader) {		
		this.elementName = elementName;
		this.index = index;
		this.reader=reader;
		this.element = HTMLDocReader.tempDoc.body().select("*").get(index);
//		System.out.println("creating element "+elementName+"with index of "+index);
	}
	
	public List<Attribute> getAttributes() {
		Attributes attributes = element.attributes();
		
		return attributes.asList();
	}

	public String getHTML() {
		String HTML = element.html();
		return HTML;
	}
	
	public String getOuterHTML() {
		String HTML = element.outerHtml();
		return HTML;
	}
	
	public String getId() {
		return element.id();
	}
	
	public String toString() {
		return elementName;
	}

}
