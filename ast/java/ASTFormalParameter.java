/* Generated By:JJTree: Do not edit this line. ASTFormalParameter.java */

package softtest.ast.java;

public class ASTFormalParameter extends AccessNode implements Dimensionable {

    private boolean isVarargs;


    public void setVarargs() {
        isVarargs = true;
    }

    public boolean isVarargs() {
        return isVarargs;
    }
    
    public ASTFormalParameter(int id) {
        super(id);
    }

    public ASTFormalParameter(JavaParser p, int id) {
        super(p, id);
    }

    @Override
	public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public boolean isArray() {
        return checkType() + checkDecl() > 0;
    }

    public int getArrayDepth() {
        if (!isArray()) {
            return 0;
        }
        return checkType() + checkDecl();
    }

    public ASTType getTypeNode() {
        for (int i = 0; i < jjtGetNumChildren(); i++) {
            if (jjtGetChild(i) instanceof ASTType) {
                return (ASTType) jjtGetChild(i);
            }
        }
        throw new IllegalStateException("ASTType not found");
    }

    private int checkType() {
        return getTypeNode().getArrayDepth();
    }

    private ASTVariableDeclaratorId getDecl() {
        return (ASTVariableDeclaratorId) jjtGetChild(jjtGetNumChildren()-1);
    }

    private int checkDecl() {
        return getDecl().getArrayDepth();
    }

    @Override
	public void dump(String prefix) {
        System.out.println(collectDumpedModifiers(prefix));
        dumpChildren(prefix);
    }

}
