package headElements;

import java.util.ArrayList;

public class Title extends HeadElement{
	
	String title;
	
	public Title(String title) {
		super();
//		System.out.println("Creating TITLE named "+title);
		//this.attributes.add(title="\""+title+"\"");
		this.attributes = new ArrayList<String>();
		this.attributes.add("title=\""+title+"\"");
		this.elementName = "Title";
		setHtml("<title>"+this.title+"</title>");
	}
	


}
