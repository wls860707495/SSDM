/* Generated By:JJTree: Do not edit this line. ASTTypeParameter.java */

package softtest.ast.java;

public class ASTTypeParameter extends SimpleJavaNode {
    public ASTTypeParameter(int id) {
        super(id);
    }

    public ASTTypeParameter(JavaParser p, int id) {
        super(p, id);
    }


    /**
     * Accept the visitor. *
     */
    @Override
	public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}