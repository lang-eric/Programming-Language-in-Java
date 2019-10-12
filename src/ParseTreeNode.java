
import java.util.ArrayList;
import java.util.List;

public class ParseTreeNode {

	private ParseTreeNode parent;
	private String token;
	private List<ParseTreeNode> children;
	private NodeType type;
	private String value;


//	private ParseTreeNode(ParseTreeNode parent, NodeType program) {
//		this.parent = parent;
//		this.children = new ArrayList<ParseTreeNode>();
//	}

	/**
	 * Leaf node constructor
	 * @param parent
	 * @param value
	 */
	public ParseTreeNode(ParseTreeNode parent, NodeType type, String value){
		this.parent = parent;
		this.type = type;
		this.children = new ArrayList<>();
		this.value = value;
	}

	public ParseTreeNode(ParseTreeNode parent, NodeType type) {
		this.parent = parent;
		this.type = type;
		this.children = new ArrayList<>();
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

	public List<ParseTreeNode> getAllChildren(){
		return children;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {this.value = value; }

	public ParseTreeNode getParent() { return this.parent; }

	public void setParent(ParseTreeNode parent) {this.parent = parent; }

	public NodeType getNodeType() { return this.type; }

	public void setToken(String term) { this.token = term; }

	public void removeChild(int idx) {
		if (idx >= children.size() || idx < 0) {
			System.out.println("Error");
		}
		else {
			children.remove(idx);
		}
	}

	public void removeAllChild() {
		children = new ArrayList<>();
	}
}