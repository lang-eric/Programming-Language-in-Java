package src;

import java.util.List;

public class ParseTreeNode {
	private ParseTreeNode parent;
	private String token;
	private List<ParseTreeNode> children;
	private NodeType type;

	enum NodeType {
		PROGRAM, STMT_LIST, STMT, END_STMT, EXPR, START_PAREN, END_PAREN,
		CHAR, L_CHAR, U_CHAR, DIGIT, SIGN, ID, PRINT, ASMT, OP,
		DBL, DBL_EXPR, INT, I_EXPR, STR_LITERAL, STR, STR_EXPR
	}

	public ParseTreeNode(ParseTreeNode parent) {
		this.parent = parent;
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


}