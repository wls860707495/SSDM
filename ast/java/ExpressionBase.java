package softtest.ast.java;

/**���б��ʽ���ڵ�ĳ�����࣬�ṩ������Ϣ�ӿ�֧�֣���ǰ���ܵľ�����ʽ�ڵ������
 * ASTExpression
 * ASTConditionalExpression
 * ASTConditionalOrExpression
 * ASTConditionalAndExpression
 * ASTInclusiveOrExpression
 * ASTExclusiveOrExpression
 * ASTAndExpression
 * ASTEqualityExpression
 * ASTInstanceOfExpression
 * ASTRelationalExpression
 * ASTShiftExpression
 * ASTAdditiveExpression
 * ASTMultiplicativeExpression
 * ASTUnaryExpression
 * ASTPreIncrementExpression
 * ASTPreDecrementExpression
 * ASTUnaryExpressionNotPlusMinus
 * ASTPostfixExpression
 * ASTCastExpression
 * ASTPrimaryExpression
 * ASTPrimaryPrefix
 * ASTPrimarySuffix
 * ASTAllocationExpression
 * ASTName
 * ASTLiteral
 * ASTBooleanLiteral
 * ASTNullLiteral
 * ASTArguments ��ŵ��Ƿ���ֵ����
 * */
public abstract class ExpressionBase extends SimpleJavaNode {

	/**���ʽ������*/
	protected Object type;

	public ExpressionBase(JavaParser p, int i) {
		super(p, i);
	}

	public ExpressionBase(int i) {
		super(i);
	}

	@Override
	public Object jjtAccept(JavaParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}

	/** ��ñ��ʽ������ */
	public Object getType() {
		return type;
	}

	/** ���ñ��ʽ����*/
	public void setType(Object type) {
		this.type = type;
	}
	
	public String getTypeString(){
		return ""+type;
	}
}
