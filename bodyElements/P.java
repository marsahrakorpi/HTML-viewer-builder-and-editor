package bodyElements;

import java.util.ArrayList;

public class P extends BodyElement{

	//public String text;
	
	public P() {
		super();
		//System.out.println("Creating PARAGRAPH ELEMENT");
	}
	
	public P(ArrayList<String> attributes, String text) {
		super();
		this.elementName = "Paragraph";
		this.elementTagName = "p";
		this.content = text.trim(); //remove whitespaces before actual content
		this.attributes = attributes;
		attributesString = String.join(",", attributes);
		this.html = "<p "+attributesString+">"+text+"</p>";
		setAllVariables(attributes);
		//System.out.println("p:"+this.html);
		
	}
	

}
