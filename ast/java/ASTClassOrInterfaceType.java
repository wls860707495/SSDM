/* Generated By:JJTree: Do not edit this line. ASTClassOrInterfaceType.java */

package softtest.ast.java;

public class ASTClassOrInterfaceType extends SimpleJavaNode {
    public ASTClassOrInterfaceType(int id) {
        super(id);
    }

    public ASTClassOrInterfaceType(JavaParser p, int id) {
        super(p, id);
    }


    /**
     * Accept the visitor. *
     */
    @Override
	public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
    
    private Class type;
    public void setType(Class type){
        this.type = type;
    }
    
    public Class getType(){
        return type;
    }

}