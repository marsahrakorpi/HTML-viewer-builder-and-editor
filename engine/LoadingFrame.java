package engine;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

public class LoadingFrame {

	public JFrame loadingFrame = new JFrame();
	public JLabel loadingMessage = new JLabel("Initializing...", SwingConstants.CENTER);
	public JProgressBar progressBar;
	public int maxProgress = 14;
	public int progressValue = 0;

	public LoadingFrame() {
		progressBar = new JProgressBar(0, maxProgress);
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				// try {
				// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				// } catch (ClassNotFoundException | InstantiationException |
				// IllegalAccessException
				// | UnsupportedLookAndFeelException ex) {
				// }

				try {
					// Load the background image
					Image img = ImageIO.read(getClass().getResource("/res/bgImg.jpg"));
					loadingFrame.setUndecorated(true);
					loadingFrame.setVisible(true);
					// Create the frame...
					loadingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

					// Set the frames content pane to use a JLabel
					// whose icon property has been set to use the image
					// we just loaded
					loadingFrame.setContentPane(new JLabel(new ImageIcon(img)));
					// Supply a layout manager for the body of the content
					loadingFrame.setLayout(new BorderLayout());
					Font font = new Font("Arial", 1, 25);
					loadingMessage.setFont(font);
					loadingMessage.setBorder(BorderFactory.createEmptyBorder(10, -50, 10, 10));
					loadingMessage.add(Box.createHorizontalGlue());
					loadingMessage.add(Box.createRigidArea(new Dimension(5, 5)));
					loadingFrame.add(loadingMessage, BorderLayout.CENTER);
					loadingFrame.add(progressBar, BorderLayout.SOUTH);		
					progressBar.setValue(progressValue);
					loadingFrame.pack();
					loadingFrame.setLocationRelativeTo(null);
					loadingFrame.setVisible(true);
				} catch (IOException exp) {
					exp.printStackTrace();
				}
			}
		});
	}

	public void dispose() {
		// System.out.println("Disposing loading frame");
		loadingFrame.dispose();
		Main.frame.requestFocus();
		Main.frame.requestFocus();
		Main.frame.requestFocus();
		Main.frame.requestFocus();
		Main.frame.requestFocus();
		Main.frame.requestFocus();
	}

	public void addProgress() {
		progressValue++;
		progressBar.setValue(progressValue);
		// System.out.println(progressValue);
	}
}
