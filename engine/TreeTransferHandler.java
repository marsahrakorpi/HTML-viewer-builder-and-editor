package engine;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jsoup.nodes.Element;

class TreeTransferHandler extends TransferHandler {
	/**
	 * 
	 */
	private static final long serialVersionUID = 856995958486511107L;
	DataFlavor nodesFlavor;
	DataFlavor[] flavors = new DataFlavor[1];
	DefaultMutableTreeNode[] nodesToRemove;
	HTMLDocReader reader;
	DefaultMutableTreeNode[] nodes;
	DefaultMutableTreeNode target;
	DefaultMutableTreeNode firstNode;
	DefaultTreeModel modelM;
	DefaultMutableTreeNode parent;
	BodyElementInfo bElement;
	int movingFromIndex, movingToIndex;

	public TreeTransferHandler(HTMLDocReader reader) {
		try {
			String mimeType = DataFlavor.javaJVMLocalObjectMimeType + ";class=\""
					+ javax.swing.tree.DefaultMutableTreeNode[].class.getName() + "\"";
			nodesFlavor = new DataFlavor(mimeType);
			flavors[0] = nodesFlavor;
			this.reader = reader;
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFound: " + e.getMessage());
		}
	}

	public boolean canImport(TransferHandler.TransferSupport support) {
		if (!support.isDrop()) {
			return false;
		}
		support.setShowDropLocation(true);
		if (!support.isDataFlavorSupported(nodesFlavor)) {
			return false;
		}
		// Do not allow a drop on the drag source selections.
		JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
		JTree tree = (JTree) support.getComponent();
		int dropRow = tree.getRowForPath(dl.getPath());
		int[] selRows = tree.getSelectionRows();
		for (int i = 0; i < selRows.length; i++) {
			if (selRows[i] == dropRow) {
				return false;
			}
		}
		// Do not allow MOVE-action drops if a non-leaf node is
		// selected unless all of its children are also selected.
		int action = support.getDropAction();
		if (action == MOVE) {
			return haveCompleteNode(tree);
		}
		// Do not allow a non-leaf node to be copied to a level
		// which is less than its source level.
		TreePath dest = dl.getPath();
		target = (DefaultMutableTreeNode) dest.getLastPathComponent();
		TreePath path = tree.getPathForRow(selRows[0]);
		firstNode = (DefaultMutableTreeNode) path.getLastPathComponent();
		if (firstNode.getChildCount() > 0 && target.getLevel() < firstNode.getLevel()) {
			return false;
		}
		return true;
	}

	private boolean haveCompleteNode(JTree tree) {
		int[] selRows = tree.getSelectionRows();
		TreePath path = tree.getPathForRow(selRows[0]);
		DefaultMutableTreeNode first = (DefaultMutableTreeNode) path.getLastPathComponent();
		int childCount = first.getChildCount();
		// first has children and no children are selected.
		if (childCount > 0 && selRows.length == 1)
			return false;
		// first may have children.
		for (int i = 1; i < selRows.length; i++) {
			path = tree.getPathForRow(selRows[i]);
			DefaultMutableTreeNode next = (DefaultMutableTreeNode) path.getLastPathComponent();
			if (first.isNodeChild(next)) {
				// Found a child of first.
				if (childCount > selRows.length - 1) {
					// Not all children of first are selected.
					return false;
				}
			}
		}
		return true;
	}

	protected Transferable createTransferable(JComponent c) {
		JTree tree = (JTree) c;
		TreePath[] paths = tree.getSelectionPaths();
		if (paths != null) {
			// Make up a node array of copies for transfer and
			// another for/of the nodes that will be removed in
			// exportDone after a successful drop.
			List<DefaultMutableTreeNode> copies = new ArrayList<DefaultMutableTreeNode>();
			List<DefaultMutableTreeNode> toRemove = new ArrayList<DefaultMutableTreeNode>();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[0].getLastPathComponent();
			DefaultMutableTreeNode copy = copy(node);

			if (paths[0].getPathComponent(1).toString().equals("Body")) {
				Object nodeInfo = (Object) node.getUserObject();
				bElement = (BodyElementInfo) nodeInfo;
				movingFromIndex = bElement.index;
			}

			copies.add(copy);
			toRemove.add(node);
			for (int i = 1; i < paths.length; i++) {
				DefaultMutableTreeNode next = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
				// Do not allow higher level nodes to be added to list.
				if (next.getLevel() < node.getLevel()) {
					break;
				} else if (next.getLevel() > node.getLevel()) { // child node
					copy.add(copy(next));
					// node already contains child
				} else { // sibling
					copies.add(copy(next));
					toRemove.add(next);
				}
			}
			nodes = copies.toArray(new DefaultMutableTreeNode[copies.size()]);
			nodesToRemove = toRemove.toArray(new DefaultMutableTreeNode[toRemove.size()]);

			return new NodesTransferable(nodes);
		}
		return null;
	}

	private DefaultMutableTreeNode copy(TreeNode node) {
		DefaultMutableTreeNode n = (DefaultMutableTreeNode) node;
		return (DefaultMutableTreeNode) n.clone();
	}

	/**
	 * Defensive copy used in createTransferable.
	 * 
	 * @param n
	 */
	// private DefaultMutableTreeNode copy(TreeNode node) {
	//
	// return new DefaultMutableTreeNode(node);
	// }

	private TreeNode getParents(TreeNode n) {
		TreeNode node = n.getParent();
		if (node.toString().equals("Body")) {
			node = getParents(node);
		}

		return node;
	}

	protected void exportDone(JComponent source, Transferable data, int action) {
		JTree tree = (JTree) source;
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();

		if (action == 0) {
			System.out.println("CREATING DIV");

			Element addToDiv = HTMLDocReader.tempDoc.body().select("*").get(bElement.index);
			String toDivHTML = addToDiv.outerHtml();
			// System.out.println(parent);

			BodyElementInfo nodeElement;
			// System.out.println(parent.getUserObject());
			try {
				Object nodeInfo = parent.getUserObject();
				nodeElement = (BodyElementInfo) nodeInfo;
				if (nodeElement.elementName.equals("div")) {

				}
			} catch (Exception e1) {

				DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getChildAt(movingToIndex + 1);
				Object nodeInfo = node.getUserObject();
				nodeElement = (BodyElementInfo) nodeInfo;
			}

			System.out.println(bElement.index + "__" + nodeElement.index);

			Element secondElement = HTMLDocReader.tempDoc.body().select("*").get(nodeElement.index);

			if (secondElement.nodeName().equals("div")) {
				System.out.println("EQUALS DIV");
				secondElement.prepend(toDivHTML);
				addToDiv.remove();
			} else {
				String newDivHTML = toDivHTML + secondElement.outerHtml();
				Element nd = new Element("div");
				nd.append(addToDiv.outerHtml());
				nd.append(secondElement.outerHtml());
				addToDiv.after(nd);
				addToDiv.remove();
				secondElement.remove();
			}

			try {
				reader.updateTempDoc();
				Main.updateFrame();

			} catch (Exception e) {
				e.printStackTrace();
				for (int i = 0; i < nodesToRemove.length; i++) {
					model.removeNodeFromParent(nodesToRemove[i]);
				}
				return;
			}

		}

		else if ((action & MOVE) == MOVE) {
			System.out.println(movingFromIndex + "::" + movingToIndex);
			if (parent.toString().equals("Root")) {
				try {
					for (int i = 0; i < nodesToRemove.length; i++) {
						model.removeNodeFromParent(nodesToRemove[i]);
					}
					Main.updateFrame();
				} catch (Exception e) {
					return;
				}
				return;
			}
			// IF MOVING FROM A PLACE TO THE SAME PLACE, DO NOT DO ANYTHING
			if ((movingFromIndex == movingToIndex) && movingToIndex != 0) {
				// Remove nodes saved in nodesToRemove in createTransferable.
				try {
					for (int i = 0; i < nodesToRemove.length; i++) {
						model.removeNodeFromParent(nodesToRemove[i]);
					}
				} catch (Exception e) {
					return;
				}
				return;
			}

			if (movingToIndex == 0) {
				movingToIndex++;
			}

			Element fromElement = HTMLDocReader.tempDoc.body().select("*").get(bElement.index);

			// System.out.println(parent);

			BodyElementInfo nodeElement;
			// System.out.println(parent.getUserObject());
			try {
				Object nodeInfo = parent.getUserObject();
				nodeElement = (BodyElementInfo) nodeInfo;
				if (nodeElement.elementName.equals("div")) {

				}
			} catch (Exception e1) {
				DefaultMutableTreeNode node;
				if (movingFromIndex < movingToIndex) {
					node = (DefaultMutableTreeNode) parent.getChildAt(movingToIndex+1);
				} else {
					node = (DefaultMutableTreeNode) parent.getChildAt(movingToIndex-1);
				}
				Object nodeInfo = node.getUserObject();
				nodeElement = (BodyElementInfo) nodeInfo;
			}
			System.out.println(nodeElement.elementName);
			// System.out.println(fromElement);
			// System.out.println(nodeElement.getOuterHTML());

			Element toElement = HTMLDocReader.tempDoc.body().select("*").get(nodeElement.index);

			System.out.println(toElement.nodeName());
			if (toElement.nodeName().equals("div") && !fromElement.nodeName().equals("div")) {
				toElement.prepend(fromElement.outerHtml());
				fromElement.remove();
			} else {
				if (movingFromIndex < movingToIndex) {
					toElement.before(fromElement);
				} else {
					toElement.after(fromElement);
				}
				
			}

		}
		try {
			reader.updateTempDoc();
			Main.updateFrame();

		} catch (Exception e) {
			e.printStackTrace();
			for (int i = 0; i < nodesToRemove.length; i++) {
				model.removeNodeFromParent(nodesToRemove[i]);
			}
			return;
		}

		// Remove nodes saved in nodesToRemove in createTransferable.
		for (int i = 0; i < nodesToRemove.length; i++) {
			try {
				model.removeNodeFromParent(nodesToRemove[i]);
			} catch (Exception e) {

			}
		}

	}

	public int getSourceActions(JComponent c) {
		return MOVE;
	}

	public boolean importData(TransferHandler.TransferSupport support) {
		if (!canImport(support)) {
			return false;
		}
		// Extract transfer data.
		DefaultMutableTreeNode[] nodes = null;
		try {
			Transferable t = support.getTransferable();
			nodes = (DefaultMutableTreeNode[]) t.getTransferData(nodesFlavor);
		} catch (UnsupportedFlavorException ufe) {
			System.out.println("UnsupportedFlavor: " + ufe.getMessage());
		} catch (java.io.IOException ioe) {
			System.out.println("I/O error: " + ioe.getMessage());
		}
		// Get drop location info.
		JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
		int childIndex = dl.getChildIndex();
		TreePath dest = dl.getPath();

		parent = (DefaultMutableTreeNode) dest.getLastPathComponent();
		JTree tree = (JTree) support.getComponent();
		modelM = (DefaultTreeModel) tree.getModel();

		// Configure for drop mode.
		int index = childIndex; // DropMode.INSERT
		// if (childIndex == -1) { // DropMode.ON
		// index = parent.getChildCount();
		// }
		System.out.println(childIndex);
		movingToIndex = index;
		// Add data to model.
		for (int i = 0; i < nodes.length; i++) {
			modelM.insertNodeInto(nodes[i], parent, index++);
		}

		return true;
	}

	public String toString() {
		return getClass().getName();
	}

	public class NodesTransferable implements Transferable {
		DefaultMutableTreeNode[] nodes;

		public NodesTransferable(DefaultMutableTreeNode[] nodes) {
			this.nodes = nodes;
		}

		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
			if (!isDataFlavorSupported(flavor))
				throw new UnsupportedFlavorException(flavor);
			return nodes;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return flavors;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return nodesFlavor.equals(flavor);
		}
	}

	public int getNumberOfNodes(TreeModel model) {
		return getNumberOfNodes(model, model.getRoot());
	}

	private int getNumberOfNodes(TreeModel model, Object node) {
		int count = 1;
		int nChildren = model.getChildCount(node);
		for (int i = 0; i < nChildren; i++) {
			count += getNumberOfNodes(model, model.getChild(node, i));
		}
		return count;
	}
}