/* Generated By:JJTree: Do not edit this line. ASTTypeDeclaration.java */

package softtest.ast.java;

public class ASTTypeDeclaration extends SimpleJavaNode {
    public ASTTypeDeclaration(int id) {
        super(id);
    }

    public ASTTypeDeclaration(JavaParser p, int id) {
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
