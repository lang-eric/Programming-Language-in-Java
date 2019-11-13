
import java.util.ArrayList;
import java.util.List;

public class ParseTreeNode {

	private ParseTreeNode parent;
	private String token;
	private List<ParseTreeNode> children;
	private NodeType type;
	private String value;
	private String line;
	private int line_number;
	private String file;


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
		this.line = null;
		this.file = null;
	}

	public ParseTreeNode(ParseTreeNode parent, NodeType type) {
		this.parent = parent;
		this.type = type;
		this.children = new ArrayList<>();
		this.value = null;
	}

	public ParseTreeNode getRootNode() {
		if(this.parent == null) {
			return this;
		} else {
			return this.getParent().getRootNode();
		}
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

//	public ParseTreeNode getChild(NodeType type) {
//		for(ParseTreeNode child: this.children) {
//			if(child.type.equals(type))
//				return child;
//		}
//		return null;
//	}

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

	/**
	 * Tests if the node instance is a leaf node or not.
	 * @return true if this node's children list is empty; false otherwise
	 */
	public boolean isLeafNode() {
		return this.children.isEmpty();
	}

	public void setLineString(String line) {
		this.line = line;
	}

	public String getLineString() {
		return line;
	}

	public void setLine_number(int n) {
		line_number = n;
	}

	public int getLine_number() {
		return line_number;
	}

	public void setFileName(String file) {
		this.file = file;
	}

	public String getFileName() {
		return file;
	}

	@Override
	public String toString() {
		if(this.value == null) {
			return this.type.toString();
		}
		else {
			return this.value;
		}
	}
}