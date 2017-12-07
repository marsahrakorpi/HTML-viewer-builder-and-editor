package engine;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class TreeCellRenderer extends DefaultTreeCellRenderer{

    /**
	 * 
	 */
	private static final long serialVersionUID = 6306243457368358152L;

	@Override
    public Color getBackgroundNonSelectionColor() {
        return (null);
    }

    @Override
    public Color getBackgroundSelectionColor() {
        return new Color(200,200,200);
    }

    @Override
    public Color getBackground() {
        return (null);
    }

    @Override
    public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean sel, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
        final Component ret = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        final DefaultMutableTreeNode node = ((DefaultMutableTreeNode) (value));
        setForeground(new Color(0,0,0));
        this.setText(value.toString());
        return ret;
    }
}

