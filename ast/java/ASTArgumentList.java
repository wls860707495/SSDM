/* Generated By:JJTree: Do not edit this line. ASTArgumentList.java */

package softtest.ast.java;

public class ASTArgumentList extends SimpleJavaNode {
    public ASTArgumentList(int id) {
        super(id);
    }

    public ASTArgumentList(JavaParser p, int id) {
        super(p, id);
    }
    
    public Class[] getParameterTypes() {
    	Class[] types = null;
    	try {
			types = new Class[this.jjtGetNumChildren()];
			for (int i = 0; i < this.jjtGetNumChildren(); i++) {
				ExpressionBase e = (ExpressionBase) this.jjtGetChild(i);
				types[i] = (Class) e.getType();
			}
		} catch (ClassCastException e) {
			// TODO : BUGFIX
			// fields from super class
			//throw new RuntimeException(this.getBeginLine()+":"+this.getBeginColumn(),e);
		}
		return types;
	}

    /**
     * Accept the visitor. *
     */
    @Override
	public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}