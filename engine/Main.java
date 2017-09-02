package engine;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Main {

	private static void createAndShowGUI() {
		
		JFrame.setDefaultLookAndFeelDecorated(false);
		JFrame frame = new JFrame("Program");
		//JPanel contentPane = new JPanel(new BorderLayout());
		//frame.setLayout(new BorderLayout());
		//Menu
		JMenuBar menuBar;
		JMenu menu, submenu;
		JMenuItem menuItem;
		
		//Create menu bar
		menuBar = new JMenuBar();
		
		
		//File menu
		menu = new JMenu("File");
		menu.getAccessibleContext().setAccessibleDescription("File operations");
		menuBar.add(menu);
		menuBar.add(Box.createRigidArea(new Dimension(5,2)));
		//File Menu items
		menu.addSeparator();
		submenu = new JMenu("New");
		menuItem = new JMenuItem("Project");
		submenu.add(menuItem);
		menu.add(submenu);
		menuItem = new JMenuItem("Open");
		menu.add(menuItem);
		
		//Window Menu
		menu = new JMenu("Window");
		menu.getAccessibleContext().setAccessibleDescription("Settings and Preferences");
		menuBar.add(menu);
		//Edit Menu items
		menu.addSeparator();
		menuItem = new JMenuItem("Preferences");
		menu.add(menuItem);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(1280, 720));

		SplitPane splitPane = new SplitPane();
		frame.getContentPane().add(splitPane.getSplitPane());
		
		frame.setJMenuBar(menuBar);
		//frame.add(contentPane);
	    frame.pack();
	    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		
		//FOR DEBUG ONLY, INSTANTLY CLOSES FRAME
		//frame.dispose();
	}
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				createAndShowGUI();
			}
		});

		
	}
	
	public static void log(String log) {
		System.out.println(log);
	}

}

