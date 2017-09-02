package actionListeners;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import engine.HTMLDocReader;

public class ListListener implements ListSelectionListener{

	private HTMLDocReader reader = new HTMLDocReader();
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		
		JList<?> list = (JList<?>)e.getSource();
		//System.out.println(list.getSelectedIndex());
		//System.out.print("@"+list.getSelectedValue());
		setElementOptionsPane(list.getSelectedValue());
		
	}

	public void setElementOptionsPane(Object element) {
		
	}
	
}
