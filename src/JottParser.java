package src;

import src.ParseTreeNode;

import java.util.List;

import java.util.List;

public class JottParser {
	public static ParseTreeNode parseTokens(List<String> tokenList) {
		ParseTreeNode root = new ParseTreeNode(null, ParseTreeNode.NodeType.PROGRAM);
		root.addChild(new ParseTreeNode(root, ParseTreeNode.NodeType.STMT_LIST));


		return null; // TODO: make something you can return that isn't NULL
	}
}