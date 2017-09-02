package bodyElements;

import java.util.ArrayList;
import java.util.Arrays;

public class Heading extends BodyElement {

	public String id;
	public String text;
	public int size;

	
	public Heading() {
		this.text = "Heading";
	}
	
	public Heading(int size, ArrayList<String> attributes, String text) {
		this.elementName = "Heading";
		this.size = size;
		this.text = text;
		this.attributes = attributes;
		setAllVariables(attributes);
	}

}
