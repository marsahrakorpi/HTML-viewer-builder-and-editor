package headerElements;

public class Meta {

	public String elementName = "Meta";
	public String meta = "";
	public String charset = "utf-8";
	public String id = "meta";
	
	public Meta() {
		this.setCharset(charset);
	}
	

	public String getCharset() {
		return this.charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
	
}
