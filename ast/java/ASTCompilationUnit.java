/* Generated By:JJTree: Do not edit this line. ASTCompilationUnit.java */

package softtest.ast.java;

public class ASTCompilationUnit extends SimpleJavaNode implements CompilationUnit {
    public ASTCompilationUnit(int id) {
        super(id);
    }

    public ASTCompilationUnit(JavaParser p, int id) {
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
