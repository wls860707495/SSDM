/* Generated By:JJTree: Do not edit this line. ASTAnnotation.java */

package softtest.ast.java;

public class ASTAnnotation extends SimpleJavaNode {
    public ASTAnnotation(int id) {
        super(id);
    }

    public ASTAnnotation(JavaParser p, int id) {
        super(p, id);
    }

    /**
     * Accept the visitor.
     */
    @Override
	public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
