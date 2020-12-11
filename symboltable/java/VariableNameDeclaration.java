/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package softtest.symboltable.java;

import java.util.ArrayList;
import java.util.Map;

import java.util.*;

import softtest.ast.java.ASTFormalParameter;
import softtest.ast.java.ASTPrimitiveType;
import softtest.ast.java.ASTReferenceType;
import softtest.ast.java.ASTType;
import softtest.ast.java.ASTVariableDeclaratorId;
import softtest.ast.java.AccessNode;
import softtest.ast.java.SimpleNode;
import softtest.deadlock.java.Alias.AliasObject;


public class VariableNameDeclaration extends AbstractNameDeclaration implements Comparable<VariableNameDeclaration> {

    public VariableNameDeclaration(ASTVariableDeclaratorId node) {
        super(node);
    }

    @Override
	public Scope getScope() {
        return node.getScope().getEnclosingClassScope();
    }

    public boolean isArray() {
        ASTVariableDeclaratorId astVariableDeclaratorId = (ASTVariableDeclaratorId) node;
        ASTType typeNode = astVariableDeclaratorId.getTypeNode();
        //added by xqing
        //return ((Dimensionable) (typeNode.jjtGetParent())).isArray();
	    int arrayDepth=astVariableDeclaratorId.getArrayDepth();
	    arrayDepth+=typeNode.getArrayDepth();
        return arrayDepth>0;
    }
    
    //added by xqing
    public int getArrayDepth(){
        ASTVariableDeclaratorId astVariableDeclaratorId = (ASTVariableDeclaratorId) node;
        ASTType typeNode = astVariableDeclaratorId.getTypeNode();
        int arrayDepth=astVariableDeclaratorId.getArrayDepth();
	    arrayDepth+=typeNode.getArrayDepth();
        return arrayDepth;   	
    }

    public boolean isExceptionBlockParameter() {
        return ((ASTVariableDeclaratorId) node).isExceptionBlockParameter();
    }

    public boolean isPrimitiveType() {
        return getAccessNodeParent().jjtGetChild(0).jjtGetChild(0) instanceof ASTPrimitiveType;
    }

    public String getTypeImage() {
        if (isPrimitiveType()) {
            return ((SimpleNode) (getAccessNodeParent().jjtGetChild(0).jjtGetChild(0))).getImage();
        }
        return ((SimpleNode) getAccessNodeParent().jjtGetChild(0).jjtGetChild(0).jjtGetChild(0)).getImage();
    }

    /**
     * Note that an array of primitive types (int[]) is a reference type.
     */
    public boolean isReferenceType() {
        return getAccessNodeParent().jjtGetChild(0).jjtGetChild(0) instanceof ASTReferenceType;
    }

    public AccessNode getAccessNodeParent() {
        if (node.jjtGetParent() instanceof ASTFormalParameter) {
            return (AccessNode) node.jjtGetParent();
        }
        return (AccessNode) node.jjtGetParent().jjtGetParent();
    }

    public ASTVariableDeclaratorId getDeclaratorId() {
        return (ASTVariableDeclaratorId) node;
    }

    @Override
	public boolean equals(Object o) {
        VariableNameDeclaration n = (VariableNameDeclaration) o;
        //return n.node.getImage().equals(node.getImage());
        //added by xqing
        return n.node.equals(node);
    }

    @Override
	public int hashCode() {
        return node.getImage().hashCode();
    }

    @Override
	public String toString() {
        return "Variable: image = '" + node.getImage() + "', line = " + node.getBeginLine();
    }
    
    //added by xqing
    /** �������� */
    private Class type=null;
    
    /** ������ʼ���� */
    private Object domain=null;
    
    /** �������� */
    public void setType(Class type){
    	this.type=type;
    }
    
    /** ������� */
    public Class getType(){
    	return type;
    }
    
    /** ���ó�ʼ�� */
    public void setDomain(Object domain){
    	this.domain=domain;
    }
    
    /** ��ó�ʼ�� */
    public Object getDomain(){
    	return domain;
    }
       
    public Scope getDeclareScope() {
        return node.getScope();
    }
    
    public List getOccs(){
    	List list=new ArrayList();
    	Map variableNames = null;
		variableNames = getDeclareScope().getVariableDeclarations();
		if (variableNames == null) {
			return list;
		}
		ArrayList occs = (ArrayList) variableNames.get(this);
		if (occs == null) {
			return list;
		}
		return occs;
    }
    
    /** �Ƚ�˳���������� */
	public int compareTo(VariableNameDeclaration e) {
		if(this.isPrimitiveType()&&e.isArray()){
			return -1;
		}else if(e.isPrimitiveType()&&this.isArray()){
			return 1;
		}
		if (node.getBeginLine() == e.node.getBeginLine()) {
			return 0;
		} else if (node.getBeginLine() > e.node.getBeginLine()) {
			return 1;
		} else {
			return -1;
		}
	}
	
	

	/************************************************************************************/
//	������Ӧ�Ķ���
//	added by bgl @2010-5-19
	/** ָ����� */
	private AliasObject aliasObject;
	
	/** ����ָ����� */
	public void setAliasObject(AliasObject aliasObject) {
		this.aliasObject = aliasObject;
	}

	/** ���ָ����� */
	public AliasObject getAliasObject() {
		return aliasObject;
	}
	
	/************************************************************************************/	
}
