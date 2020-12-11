/* Generated By:JJTree: Do not edit this line. ASTIfStatement.java */

package softtest.ast.java;

public class ASTIfStatement extends SimpleJavaNode {
    public ASTIfStatement(int id) {
        super(id);
    }

    public ASTIfStatement(JavaParser p, int id) {
        super(p, id);
    }

    private boolean hasElse;

    public void setHasElse() {
        this.hasElse = true;
    }

    public boolean hasElse() {
        return this.hasElse;
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
        System.out.println(toString(prefix) + ":" + (hasElse ? "(has else)" : ""));
        dumpChildren(prefix);
    }
}
