package bodyElements;

import java.util.ArrayList;

public class Heading extends BodyElement {

	public String id;
	//public String text;
	public int size;

	
	public Heading() {
		this.content = "Heading";
	}
	
	public Heading(int size, ArrayList<String> attributes, String text) {
		this.elementName = "Heading";
		this.elementTagName = "h";
		this.size = size;
		this.content = text;
		this.attributes = attributes;
		setAllVariables(attributes);
	}

}
