package headElements;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JTextField;

import actionListeners.ListListener;

public class HeadElement {

	public String html;
	public String elementName = "";
	public String elementType = "Head";
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

	public String getElementType() {
		return elementType;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType;
	}
	
	public static void createAttributeLabels(ArrayList<String> attribs) {
//		System.out.println("CREATING ATTRIBUTE LABELS FROM ATTRIBUTES "+attribs);
		String attrib, value;
		
		for(String a: attribs) {
//			System.out.println(a);
			attrib = a.substring(0, a.indexOf("="));
			value = a.substring(a.indexOf("\""));
			value = value.substring(value.indexOf("\"")+1);
			value = value.substring(0,value.indexOf("\""));
			
			ListListener.label.add(new JLabel(attrib));
			ListListener.field.add(new JTextField(value));
		}
	}

	
}
