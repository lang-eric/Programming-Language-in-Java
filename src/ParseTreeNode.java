import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;

public class ParseTreeNode {
	private ParseTreeNode parent;
	private String token;
	private List<ParseTreeNode> children;

	enum type {
		program, stmt_list, stmt, expr;
	}

	public ParseTreeNode(ParseTreeNode parent) {
		this.parent = parent;
	}

	/**
	 * Adds a new child node to this node's children list.
	 * Note that this method does not create a new node;
	 * a node instance must be provided.
	 *
	 * @param child the ParseTreeNode to add to this node's
	 *              children list
	 */
	public void addChild(ParseTreeNode child) {
		children.add(child);
	}


}
