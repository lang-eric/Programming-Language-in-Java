import src.JottTokenizer;

import java.util.List;
import java.util.Objects;

public class JottParser {

	/* Values and their corresponding types as indicated in JottTokenizer.
	 * '+' - plus
	 * '-' - minus
	 * '*' - mult
	 * '\' - divide
	 * '^' - power
	 * ',' - comma
	 * ';' - end_stmt
	 * '(' - start_paren
	 * ')' - end_paren
	 * '=' - assign
	 * 'String' - type_String
	 * 'Integer' - type_Integer
	 * 'Double' - type_Double
	 * 'print(' - print
	 * 'charAt(' - charAt
	 * 'concat(' - concat
	 * A set of letters and numbers that begins with an uppercase letter            - upper_keyword
	 * A set of letters and numbers that begins with a lower letter                 - lower_keyword
	 * A set of letters, numbers, and spaces surrounded by quotation marks ('"')    - string
	 *
	 * NodeType constants, as indicated in ParseTreeNode:
	 * PROGRAM, STMT_LIST, $$, STMT, END_STMT, EXPR, START_PAREN, END_PAREN,
	 *	CHAR, L_CHAR, U_CHAR, DIGIT, SIGN, ID, PRINT, ASMT, OP,
	 *	DBL, DBL_EXPR, INT, I_EXPR, STR_LITERAL, STR, STR_EXPR
	 */

	public static ParseTreeNode parseTokens(List<JottTokenizer.Token> tokenList) {
		int tokIndex = 0;
		ParseTreeNode root = new ParseTreeNode(null, ParseTreeNode.NodeType.PROGRAM);
		root.addChild(new ParseTreeNode(root, ParseTreeNode.NodeType.STMT_LIST));
		root.addChild(new ParseTreeNode(root, ParseTreeNode.NodeType.$$));

		expandStmtList(tokenList, tokIndex, root.getChild(ParseTreeNode.NodeType.STMT_LIST));

		return root;
	}

	private static String lookAhead(List<JottTokenizer.Token> tokenList, int tokIndex) {
		if(tokIndex < tokenList.size() - 1)
			return tokenList.get(tokIndex + 1).getType();
		else
			return null;
	}

	private static void expandStmtList(List<JottTokenizer.Token> tokenList, int tokIndex, ParseTreeNode stmtList) {
		ParseTreeNode stmt = new ParseTreeNode(stmtList, ParseTreeNode.NodeType.STMT);
		ParseTreeNode stmtListNext = new ParseTreeNode(stmtList, ParseTreeNode.NodeType.STMT_LIST);
		stmtList.addChild(stmt);
		stmtList.addChild(stmtListNext);
		expandStmt(tokenList, tokIndex, stmt);

		if(isFinalStatement(tokenList, tokIndex)) {
			stmtListNext.setToken("epsilon");
		} else {
			expandStmtList(tokenList, tokIndex, stmtListNext);
		}
	}

	private static void expandStmt(List<JottTokenizer.Token> tokenList, int tokIndex, ParseTreeNode stmt) {
		if(tokenList.get(tokIndex).getType().equals("print")) {
			ParseTreeNode prt = new ParseTreeNode(stmt, ParseTreeNode.NodeType.PRINT);
			prt.addChild(new ParseTreeNode(prt, ParseTreeNode.NodeType.START_PAREN));
			tokIndex += 1;
			ParseTreeNode expr = new ParseTreeNode(prt, ParseTreeNode.NodeType.EXPR);
			prt.addChild(expr);
			expandExpr(tokenList, tokIndex, expr);
			if(tokenList.get(tokIndex).getType().equals("end_paren")) {
				if(Objects.requireNonNull(lookAhead(tokenList, tokIndex)).equals("end_stmt")) {
					prt.addChild(new ParseTreeNode(prt, ParseTreeNode.NodeType.END_PAREN));
					prt.addChild(new ParseTreeNode(prt, ParseTreeNode.NodeType.END_STMT));
					tokIndex += 2;
				} else {
					//throw exception: expected end_stmt token (semicolon) not found
				}
			} else {
				//throw exception: expected end parenthesis not found
			}
			stmt.addChild(prt);
		}
		//TODO: implement <asmt> case

		//TODO: implement <expr><end_stmt> case
	}

	private static void expandExpr(List<JottTokenizer.Token> tokenList, int tokIndex, ParseTreeNode expr) {
		//TODO: implement <i_expr> case

		//TODO: implement <d_expr> case

		//TODO: implement <s_expr> case

		//TODO: implement <id> case
	}

	private static boolean isFinalStatement(List<JottTokenizer.Token> tokenList, int tokIndex) {
		int i = tokIndex;
		while(!(tokenList.get(i).getType().equals("end_stmt")) && i < tokenList.size()) {
			i++;
		}
		return (i == tokenList.size() - 1 || i == tokenList.size());
	}
}
