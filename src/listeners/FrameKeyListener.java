package listeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class FrameKeyListener implements KeyListener {

	public FrameKeyListener() {
		System.out.println("KeyListener init");
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println("key presed");
		// TODO Auto-generated method stub
		if(e.getKeyCode() == KeyEvent.VK_S && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
			System.out.println("Control+s detected");
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		System.out.println("key released");

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("key typed");
	}

}
