package actionListeners;

import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import engine.HTMLDocReader;
import engine.Main;
import engine.SplitPane;

public class ListListener implements ListSelectionListener{

	private HTMLDocReader reader;
	private String[] globalHTMLAttributes = {"accesskey","class","contenteditable","contextmenu","dir","draggable","dropzone","hidden","id","lang","spellcheck","style","tabindex","title","translate"};
	JLabel elementName = new JLabel("INIT",JLabel.CENTER);
	private ArrayList<JLabel> label = new ArrayList<JLabel>();
	private ArrayList<JTextField> field = new ArrayList<JTextField>();
	public JPanel p;
	public ListListener(HTMLDocReader reader) {
		this.reader = reader;
		p = new JPanel();
	}

	public ListListener() {
		p = new JPanel();
	}
	@Override
	public void valueChanged(ListSelectionEvent e) {
		
		JList<?> list = (JList<?>)e.getSource();
		try{
			p.removeAll();
			SplitPane.elementOptions.setViewportView(p);
		} catch (Exception exc) {
			
		}
		//System.out.println(list.getSelectedIndex());
		//System.out.print("@"+list.getSelectedValue());
		
		if(list.getSelectedValue()!=null) {
			//System.out.println(reader.bodyElement.get(list.getSelectedIndex()).getAttributes());
			setElementOptionsPane(list.getSelectedIndex());
		}
		if(list.getSelectedIndex()>=0) {
			System.out.println(reader.bodyElement.get(list.getSelectedIndex()).getId());
		}

	}

	public void setElementOptionsPane(int index) {
		//System.out.println(reader.bodyElement.get(index).getAttributes()+reader.bodyElement.get(index).getContent());

		Dimension d = new Dimension(275, 20);	
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
		p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		p.add(Box.createHorizontalGlue());
		p.add(Box.createRigidArea(new Dimension(10, 10)));
		label.clear();
		field.clear();
		elementName.setText(reader.bodyElement.get(index).getElementName()+"\n\n");
		elementName.setBorder(BorderFactory.createEmptyBorder(0,10,10,0));
		elementName.setFont(new Font("Arial", Font.BOLD, 25));
		p.add(elementName);
		label.add(new JLabel("Text"));
		field.add(new JTextField(reader.bodyElement.get(index).getContent()));
		label.add(new JLabel("Id"));
		field.add(new JTextField(reader.bodyElement.get(index).getId()));
		for(int i=0; i<globalHTMLAttributes.length; i++) {
			if(!globalHTMLAttributes[i].equals("id")) {
			label.add(new JLabel(globalHTMLAttributes[i]));
			
			switch(globalHTMLAttributes[i]) {
			case "accesskey":
				field.add(new JTextField(reader.bodyElement.get(index).getAccesskey()));
				break;
			case "class":
				field.add(new JTextField(reader.bodyElement.get(index).getHtmlClass()));
				break;
			case "contenteditable":
				field.add(new JTextField(reader.bodyElement.get(index).getContenteditable()));
				break;
			case "contextmenu":
				field.add(new JTextField(reader.bodyElement.get(index).getContextmenu()));
				break;
			case "dir":
				field.add(new JTextField(reader.bodyElement.get(index).getDir()));
				break;
			case "draggable":
				field.add(new JTextField(reader.bodyElement.get(index).getDraggable()));
				break;
			case "dropzone":
				field.add(new JTextField(reader.bodyElement.get(index).getDropzone()));
				break;
			case "hidden":
				field.add(new JTextField(reader.bodyElement.get(index).getHidden()));
				break;
			case "lang":
				field.add(new JTextField(reader.bodyElement.get(index).getLang()));
				break;
			case "spellcheck":
				field.add(new JTextField(reader.bodyElement.get(index).getSpellcheck()));
				break;
			case "style":
				field.add(new JTextField(reader.bodyElement.get(index).getStyle()));
				break;
			case "tabindex":
				field.add(new JTextField(reader.bodyElement.get(index).getTabindex()));
				break;
			case "title":
				field.add(new JTextField(reader.bodyElement.get(index).getTitle()));
				break;
			case "translate":
				field.add(new JTextField(reader.bodyElement.get(index).getTranslate()));
				break;
			default:
				field.add(new JTextField(" "));
				}
			}
	}
		

		for(int i=0; i<label.size(); i++) {
			p.add(label.get(i));
			p.add(field.get(i));
			label.get(i).setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
			field.get(i).setMaximumSize(d);
			field.get(i).setHorizontalAlignment(JTextField.LEFT);
		}

		Main.elementAttributes.setViewportView(p);

		
//		for(int i=0; i<globalHTMLAttributes.length; i++) {
//			System.out.println("public String "+globalHTMLAttributes[i]+" = \"\";");
//		}
//		public String position;
//		public String accesskey;
//		public String htmlClass;
//		public String contenteditable;
//		public String contextmenu;
//		public String dir;
//		public String draggable;
//		public String dropzone;
//		public String hidden;
//		public String id;
//		public String lang;
//		public String spellcheck;
//		public String style;
//		public String tabindex;
//		public String title;
//		public String translate;
		
	}
	
}
