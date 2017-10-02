package engine;

import java.io.File;

public class DeletionHook extends Thread {
	
	public void run() {
		FileHandler.deleteFolder(new File(Main.tempDir));
	}

}
