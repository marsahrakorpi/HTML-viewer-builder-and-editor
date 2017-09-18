package listeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class FieldKeyListener implements KeyListener {

	int index;

	public FieldKeyListener(int index) {
		this.index = index;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			updateElement();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void updateElement() {

	}

}
