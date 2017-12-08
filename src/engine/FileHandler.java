package engine;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;

public class FileHandler {

	public FileHandler() {
		
	}

	public static void deleteFolder(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}
	
	public static void copyFileOrFolder(File source, File dest, CopyOption... options) throws IOException {
		if (source.isDirectory())
			copyFolder(source, dest, options);
		else {
			ensureParentFolder(dest);
			copyFile(source, dest, options);
		}
	}

	public static void copyFolder(File source, File dest, CopyOption... options) throws IOException {
		if (!dest.exists())
			dest.mkdirs();
		File[] contents = source.listFiles();
		if (contents != null) {
			for (File f : contents) {
				File newFile = new File(dest.getAbsolutePath() + File.separator + f.getName());
				if (f.isDirectory())
					copyFolder(f, newFile, options);
				else
					copyFile(f, newFile, options);
			}
		}
	}

	public static void copyFile(File source, File dest, CopyOption... options) throws IOException {
		Files.copy(source.toPath(), dest.toPath(), options);
	}

	public static void ensureParentFolder(File file) {
		File parent = file.getParentFile();
		if (parent != null && !parent.exists())
			parent.mkdirs();
	}
}
