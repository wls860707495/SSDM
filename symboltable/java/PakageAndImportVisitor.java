//added by xqing
package softtest.symboltable.java;

import softtest.ast.java.ASTCompilationUnit;
import softtest.ast.java.ASTImportDeclaration;
import softtest.ast.java.ASTPackageDeclaration;
import softtest.ast.java.JavaParserVisitorAdapter;
import softtest.ast.java.SimpleJavaNode;
import softtest.ast.java.SimpleNode;
import softtest.ast.java.*;

/** �����������import�����ĳ����﷨�������ߣ��������ʹ��� */
public class PakageAndImportVisitor extends JavaParserVisitorAdapter {
    @Override
	public Object visit(SimpleJavaNode node, Object data) {
    	//��ֹ���ӽڵ����
        //node.childrenAccept(this, data);
        return null;
    }
    @Override
	public Object visit(ASTCompilationUnit node, Object data) {
    	node.childrenAccept(this, data);
        return null;
    }
    @Override
	public Object visit(ASTPackageDeclaration node, Object data) {
    	TypeSet typeset=(TypeSet)data;
    	SimpleNode treenode=(SimpleNode)node.jjtGetChild(0);
    	typeset.setASTCompilationUnitPackage(treenode.getImage());
        return null;
    }

    @Override
	public Object visit(ASTImportDeclaration node, Object data) {
    	TypeSet typeset=(TypeSet)data;
    	SimpleNode treenode=(SimpleNode)node.jjtGetChild(0);
    	if(node.isStatic()){
    		typeset.addStaticImport(treenode.getImage());
    	}
    	else {
    		if(node.isImportOnDemand()){
    			typeset.addImport(treenode.getImage()+".*");
    		}else{
    			typeset.addImport(treenode.getImage());
    		}
    	}
        return null;
    }
}
