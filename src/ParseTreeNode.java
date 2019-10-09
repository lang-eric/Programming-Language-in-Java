
import java.util.ArrayList;
import java.util.List;

public class ParseTreeNode {
	private ParseTreeNode parent;
	private String token;
	private List<ParseTreeNode> children;
	private NodeType type;


	private ParseTreeNode(ParseTreeNode parent) {
		this.parent = parent;
		this.children = new ArrayList<ParseTreeNode>();
	}

	public ParseTreeNode(ParseTreeNode parent, NodeType type) {
		this(parent);
		this.type = type;
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

	public ParseTreeNode getChild(NodeType type) {
		for(ParseTreeNode child: this.children) {
			if(child.type.equals(type))
				return child;
		}
		return null;
	}

	public ParseTreeNode getParent() { return this.parent; }

	public NodeType getNodeType() { return this.type; }

	public void setToken(String term) { this.token = term; }
}