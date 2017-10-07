package engine;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.io.FilenameUtils;

public class CreateChildNodes implements Runnable {

	private DefaultMutableTreeNode root;

	private File fileRoot;

	public CreateChildNodes(File fileRoot, DefaultMutableTreeNode root) {
		this.fileRoot = fileRoot;
		this.root = root;
	}

	@Override
	public void run() {
		try {
			createChildren(fileRoot, root);
			
		} catch (Exception e) {
			return;
		}
	}

	private void createChildren(File fileRoot, DefaultMutableTreeNode node) {
		File[] files = fileRoot.listFiles();
		if (files == null)
			return;

		for (File file : files) {
			if (file.getName().equals("webViewCSS")) {

			} else {

				DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new FileNode(file));
				// do not add to directory tree if the file is a temp file
				if (FilenameUtils.getExtension(file.getAbsolutePath()).equals("tmp")) {
				} else {
					node.add(childNode);
				}
				if (file.isDirectory()) {
					createChildren(file, childNode);
				}
			}
		}
	}

}
