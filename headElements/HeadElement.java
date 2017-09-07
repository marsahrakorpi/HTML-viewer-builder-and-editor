package headElements;

import java.util.ArrayList;

public class HeadElement {

	public String html;
	public String elementName;
	
	public ArrayList<String> attributes;
	public ArrayList<String> allAttributes;
	public String attributesString = "";
	
	public HeadElement() {
		
	}
	
	public HeadElement(ArrayList<String> attributes) {
		
	}


	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}


	public ArrayList<String> getAttributes() {

		return attributes;
	}

	public void setAttributes(ArrayList<String> attributes) {
		this.attributes = attributes;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}
	
	public String getElementName() {
		return elementName;
	}

	
}
