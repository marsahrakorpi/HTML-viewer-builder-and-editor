package listeners;

import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import bodyElements.BodyElementInfo;
import engine.HTMLDocReader;
import engine.Main;
import headElements.HeadElementInfo;

public class ListListener implements TreeSelectionListener{

	private HTMLDocReader reader;
	public DefaultMutableTreeNode elementTree;
	private String[] globalHTMLAttributes = {"accesskey","class","contenteditable","contextmenu","dir","draggable","dropzone","hidden","id","lang","spellcheck","style","tabindex","title","translate"};
	JLabel elementName = new JLabel("INIT",JLabel.CENTER);
	public static ArrayList<JLabel> label = new ArrayList<JLabel>();
	public static ArrayList<JTextField> field = new ArrayList<JTextField>();
	public JPanel p;
	private Dimension d;
	
	public ListListener(HTMLDocReader reader) {
		this.reader = reader;
		p = new JPanel();
		d = new Dimension(275, 20);	
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
		p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		p.add(Box.createHorizontalGlue());
		p.add(Box.createRigidArea(new Dimension(10, 10)));
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		try{
			p.removeAll();
			Main.elementAttributes.setViewportView(p);
		} catch (Exception exc) {
			
		}
		label.clear();
		field.clear();

//		System.out.println(e.getPath().getPathCount());
		if( e.getPath().getPathCount() > 2) {	
		//HEAD
			if(e.getPath().getPathComponent(1).toString().equals("Head")) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
				if(node == null) {
					System.out.println("node null");
					return;
				}
		
				Object nodeInfo = node.getUserObject();		
				if(node.isLeaf()) {
					HeadElementInfo hElement = (HeadElementInfo)nodeInfo;
					
	
					elementName.setText(hElement.elementName.toUpperCase()+"\n\n");
					elementName.setBorder(BorderFactory.createEmptyBorder(0,10,10,0));
					elementName.setFont(new Font("Arial", Font.BOLD, 25));
					p.add(elementName);
								
					
				}
			}
	
			//BODY
			if(e.getPath().getPathComponent(1).toString().equals("Body")) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
				if(node == null) {
					System.out.println("node null");
					return;
				}
		
				Object nodeInfo = node.getUserObject();		
				if(node.isLeaf()) {
					BodyElementInfo bElement = (BodyElementInfo)nodeInfo;
					
					label.clear();
					field.clear();
					
					elementName.setText(bElement.elementName.toUpperCase()+"\n\n");
					elementName.setBorder(BorderFactory.createEmptyBorder(0,10,10,0));
					elementName.setFont(new Font("Arial", Font.BOLD, 25));
					p.add(elementName);
					for(int i=0; i<bElement.getAttributes().size(); i++) {
						//System.out.println(bElement.getAttributes().get(i).getKey()+"::"+bElement.getAttributes().get(i).getValue());
						label.add(new JLabel(bElement.getAttributes().get(i).getKey()));
						field.add(new JTextField(bElement.getAttributes().get(i).getValue()));
					}
					//System.out.println(bElement.getAttributes().get(0).getKey());		
				}
			}
		}
		
		//set labels and fields
		
		for(int i=0; i<label.size(); i++) {
			p.add(label.get(i));
			p.add(field.get(i));
			label.get(i).setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
			field.get(i).setMaximumSize(d);
			field.get(i).setHorizontalAlignment(JTextField.LEFT);
		}
		
		Main.elementAttributes.setViewportView(p);
		
	}
	


	public void setHeadElementOptionsPane(int index) {


		
	}
	
	public void setBodyElementOptionsPane() {
		
	}

	
}
