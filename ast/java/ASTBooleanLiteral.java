/* Generated By:JJTree: Do not edit this line. ASTBooleanLiteral.java */

package softtest.ast.java;

public class ASTBooleanLiteral extends ExpressionBase {
    public ASTBooleanLiteral(int id) {
        super(id);
    }

    public ASTBooleanLiteral(JavaParser p, int id) {
        super(p, id);
    }

    private boolean isTrue;

    public void setTrue() {
        isTrue = true;
    }

    public boolean isTrue() {
        return this.isTrue;
    }


    /**
     * Accept the visitor. *
     */
    @Override
	public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
	public void dump(String prefix) {
        String out = isTrue ? "true" : "false";
        System.out.println(toString(prefix) + ":" + out);
        dumpChildren(prefix);
    }

}