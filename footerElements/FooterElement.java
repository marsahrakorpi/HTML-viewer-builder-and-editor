package footerElements;

import java.util.ArrayList;

public class FooterElement {

	public String html;

	public String elementName = "";
	public String position;
	public String accesskey = " ";
	public String htmlClass = " ";
	public String contenteditable = " ";
	public String contextmenu = " ";
	public String dir = " ";
	public String draggable = " ";
	public String dropzone = " ";
	public String hidden = " ";
	public String id = " ";
	public String lang = " ";
	public String spellcheck = " ";
	public String style = " ";
	public String tabindex = " ";
	public String title = " ";
	public String translate = " ";
	
	public ArrayList<String> attributes;
	public ArrayList<String> allAttributes;
	public String attributesString = "";
	
	public FooterElement() {
		
	}
	
	public FooterElement(ArrayList<String> attributes) {
		setAllVariables(attributes);
	}

	public String getAccesskey() {
		return accesskey;
	}

	public void setAccesskey(String accesskey) {
		this.accesskey = accesskey;
	}

	public String getHtmlClass() {
		return htmlClass;
	}

	public void setHtmlClass(String htmlClass) {
		this.htmlClass = htmlClass;
	}

	public String getContenteditable() {
		return contenteditable;
	}

	public void setContenteditable(String contenteditable) {
		this.contenteditable = contenteditable;
	}

	public String getContextmenu() {
		return contextmenu;
	}

	public void setContextmenu(String contextmenu) {
		this.contextmenu = contextmenu;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getDraggable() {
		return draggable;
	}

	public void setDraggable(String draggable) {
		this.draggable = draggable;
	}

	public String getDropzone() {
		return dropzone;
	}

	public void setDropzone(String dropzone) {
		this.dropzone = dropzone;
	}

	public String getHidden() {
		return hidden;
	}

	public void setHidden(String hidden) {
		this.hidden = hidden;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getSpellcheck() {
		return spellcheck;
	}

	public void setSpellcheck(String spellcheck) {
		this.spellcheck = spellcheck;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getTabindex() {
		return tabindex;
	}

	public void setTabindex(String tabindex) {
		this.tabindex = tabindex;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTranslate() {
		return translate;
	}

	public void setTranslate(String translate) {
		this.translate = translate;
	}
	
	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
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

	public void setAllVariables(ArrayList<String> attributes){
		//System.out.println("Setting all variables");
		String[] parts;
		String s, v;
		//printSwitchStatement();
		for(int i=0; i<attributes.size(); i++) {
			parts = attributes.get(i).split("=\"");
			s = parts[0];
			v = parts[1].substring(0, parts[1].length() -1);
			
			switch(s) {
				case "accesskey":
					setAccesskey(v);
					break;
				case "class":
					setHtmlClass(v);
					break;
				case "contenteditable":
					setContenteditable(v);
					break;
				case "contextmenu":
					setContextmenu(v);
					break;
				case "dir":
					setDir(v);
					break;
				case "draggable":
					setDraggable(v);
					break;
				case "dropzone":
					setDropzone(v);
					break;
				case "hidden":
					setHidden(v);
					break;
				case "id":
					setId(v);
					break;
				case "lang":
					setLang(v);
					break;
				case "spellcheck":
					setSpellcheck(v);
					break;
				case "style":
					setStyle(v);
					break;
				case "tabindex":
					setTabindex(v);
					break;
				case "title":
					setTitle(v);
					break;
				case "translate":
					setTranslate(v);
					break;
			}
			
		}
	}
	
	private void printSwitchStatement() {
		String[] globalHTMLAttributes = {"accesskey","class","contenteditable","contextmenu","dir","draggable","dropzone","hidden","id","lang","spellcheck","style","tabindex","title","translate"};
		for(int i=0; i<globalHTMLAttributes.length; i++) {
			System.out.println("case \""+globalHTMLAttributes[i]+"\":\n\tset"+globalHTMLAttributes[i].substring(0, 1).toUpperCase()+globalHTMLAttributes[i].substring(1)+"(v);\n\tbreak;");
		}
	}
	
}
