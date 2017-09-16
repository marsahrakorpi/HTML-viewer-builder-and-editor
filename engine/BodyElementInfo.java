package engine;

import java.util.List;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

public class BodyElementInfo {
	public String elementName;
	public double index;
	private HTMLDocReader reader;
	private Element element;
	
	public BodyElementInfo(String elementName, double index, HTMLDocReader reader) {		
		this.reader = reader;
		this.elementName = elementName;
		this.index = index;
		this.element = reader.bodyElements.get((int)index);
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
	
	
	
	public String toString() {
		return elementName;
	}

}

