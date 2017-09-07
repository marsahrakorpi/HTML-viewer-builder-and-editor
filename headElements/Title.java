package headElements;

import java.util.ArrayList;

public class Title extends HeadElement{
	
	String title;
	
	public Title(String title) {
		super();
		this.title = title;
		this.elementName = "Title";
		this.attributes = new ArrayList<String>();
		this.attributes.add("Title=\""+title+"\"");
		setHtml("<title>"+this.title+"</title>");
	}
	


}
