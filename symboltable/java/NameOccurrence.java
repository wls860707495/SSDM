/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package softtest.symboltable.java;

import softtest.ast.java.*;
// added by xqing
import java.util.*;

import softtest.ast.java.ASTAssignmentOperator;
import softtest.ast.java.ASTExpression;
import softtest.ast.java.ASTName;
import softtest.ast.java.ASTPostfixExpression;
import softtest.ast.java.ASTPreDecrementExpression;
import softtest.ast.java.ASTPreIncrementExpression;
import softtest.ast.java.ASTPrimaryExpression;
import softtest.ast.java.ASTPrimaryPrefix;
import softtest.ast.java.ASTStatementExpression;
import softtest.ast.java.Node;
import softtest.ast.java.SimpleNode;

public class NameOccurrence {

    private SimpleNode location;
    private String image;
    private NameOccurrence qualifiedName;

    private boolean isMethodOrConstructorInvocation;
    private int argumentCount;   

    public NameOccurrence(SimpleNode location, String image) {
        this.location = location;
        this.image = image;
        // added by xqing
        this.occurrenceType=OccurrenceType.USE;
    }

    public void setIsMethodOrConstructorInvocation() {
        isMethodOrConstructorInvocation = true;
    }

    public void setArgumentCount(int count) {
        argumentCount = count;
    }

    public int getArgumentCount() {
        return argumentCount;
    }

    public boolean isMethodOrConstructorInvocation() {
        return isMethodOrConstructorInvocation;
    }

    public void setNameWhichThisQualifies(NameOccurrence qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public NameOccurrence getNameForWhichThisIsAQualifier() {
        return qualifiedName;
    }

    public boolean isPartOfQualifiedName() {
        return qualifiedName != null;
    }

    public SimpleNode getLocation() {
        return location;
    }

    public boolean isOnRightHandSide() {
        SimpleNode node = (SimpleNode) location.jjtGetParent().jjtGetParent().jjtGetParent();
        return node instanceof ASTExpression && node.jjtGetNumChildren() == 3;
    }


    public boolean isOnLeftHandSide() {
        // I detest this method with every atom of my being
        SimpleNode primaryExpression;
        if (location.jjtGetParent() instanceof ASTPrimaryExpression) {
            primaryExpression = (SimpleNode) location.jjtGetParent().jjtGetParent();
        } else if (location.jjtGetParent().jjtGetParent() instanceof ASTPrimaryExpression) {
            primaryExpression = (SimpleNode) location.jjtGetParent().jjtGetParent().jjtGetParent();
        } else {
            throw new RuntimeException("Found a NameOccurrence that didn't have an ASTPrimary Expression as parent or grandparent.  Parent = " + location.jjtGetParent() + " and grandparent = " + location.jjtGetParent().jjtGetParent());
        }

        if (isStandAlonePostfix(primaryExpression)) {
            return true;
        }

        if (primaryExpression.jjtGetNumChildren() <= 1) {
            return false;
        }

        if (!(primaryExpression.jjtGetChild(1) instanceof ASTAssignmentOperator)) {
            return false;
        }

        if (isPartOfQualifiedName() /* or is an array type */) {
            return false;
        }

        if (isCompoundAssignment(primaryExpression)) {
            return false;
        }

        return true;
    }

    private boolean isCompoundAssignment(SimpleNode primaryExpression) {
        return ((ASTAssignmentOperator) (primaryExpression.jjtGetChild(1))).isCompound();
    }

    private boolean isStandAlonePostfix(SimpleNode primaryExpression) {
        if (!(primaryExpression instanceof ASTPostfixExpression) || !(primaryExpression.jjtGetParent() instanceof ASTStatementExpression)) {
            return false;
        }

        ASTPrimaryPrefix pf = (ASTPrimaryPrefix) ((ASTPrimaryExpression) primaryExpression.jjtGetChild(0)).jjtGetChild(0);
        if (pf.usesThisModifier()) {
            return true;
        }

        return thirdChildHasDottedName(primaryExpression);
    }

    private boolean thirdChildHasDottedName(SimpleNode primaryExpression) {
        Node thirdChild = primaryExpression.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
        return thirdChild instanceof ASTName && ((ASTName) thirdChild).getImage().indexOf('.') == -1;
    }

    public boolean isSelfAssignment() {
        Node l = location;
        while (true) {
            Node p = l.jjtGetParent();
            Node gp = p.jjtGetParent();
            Node node = gp.jjtGetParent();
            if (node instanceof ASTPreDecrementExpression || node instanceof ASTPreIncrementExpression || node instanceof ASTPostfixExpression) {
                return true;
            }
    
            if (node instanceof ASTStatementExpression) {
                ASTStatementExpression exp = (ASTStatementExpression) node;
                if (exp.jjtGetNumChildren() >= 2 && exp.jjtGetChild(1) instanceof ASTAssignmentOperator) {
                    ASTAssignmentOperator op = (ASTAssignmentOperator) exp.jjtGetChild(1);
                    if (op.isCompound()) {
                        return true;
                    }
                }
            }
    
            // deal with extra parenthesis: "(i)++"
            if (p instanceof ASTPrimaryPrefix && p.jjtGetNumChildren() == 1 &&
                    gp instanceof ASTPrimaryExpression && gp.jjtGetNumChildren() == 1&&
                    node instanceof ASTExpression && node.jjtGetNumChildren() == 1 &&
                    node.jjtGetParent() instanceof ASTPrimaryPrefix && node.jjtGetParent().jjtGetNumChildren() == 1) {
                l = node;
                continue;
            }
    
            return false;
        }
    }

    public boolean isThisOrSuper() {
        return image.equals("this") || image.equals("super");
    }

    @Override
	public boolean equals(Object o) {
        NameOccurrence n = (NameOccurrence) o;
        return n.getImage().equals(getImage());
    }

    @Override
	public int hashCode() {
        return getImage().hashCode();
    }

    public String getImage() {
        return image;
    }

    @Override
	public String toString() {
        return getImage() + ":" + location.getBeginLine() + ":" + location.getClass() + (this.isMethodOrConstructorInvocation() ? "(method call)" : "");
    }
    
    // added by xqing
    /** �Ƿ�Ϊ�������Լ����� */
    public boolean isSelfIncOrDec(){
    	boolean b=false;
    	if(location.getSingleParentofType(ASTPreDecrementExpression.class)!=null
    	||location.getSingleParentofType(ASTPreIncrementExpression.class)!=null){
    		b=true;
    	}else{
    		ASTPostfixExpression post=(ASTPostfixExpression)location.getSingleParentofType(ASTPostfixExpression.class);
    		if(post!=null&&post.getImage()!=null){
    			String image=post.getImage();
    			if(image.equals("++")||image.equals("--")){
    				b=true;
    			}
    		}
    	}
    	return b;
    }
    
    /** �������� */
    public enum OccurrenceType{
    	/** ���� */
    	DEF,
    	/** ʹ�� */
    	USE
    }
    
    /** ��������,ֻ��Ա����ĳ��� */
    private OccurrenceType occurrenceType;
    
    /** ���� */
    private NameDeclaration decl;
    
    /** �������� */
    public void setDeclaration(NameDeclaration decl){
    	this.decl=decl;
    }
    
    /** ������� */
    public NameDeclaration getDeclaration(){
    	return decl;
    }
    
    /** ���ó������� */ 
    public void setOccurrenceType(OccurrenceType occurrenceType){
    	this.occurrenceType=occurrenceType;
    }
    
    /** ��ó������� */
    public OccurrenceType getOccurrenceType(){
    	return this.occurrenceType;
    } 
    
    /** ʹ��-�������������˿��Ե��ﱾʹ�ó��ֵ����ж�����֣����ڶ�����֣�������Ϊnull,�����е�Ԫ�ض�Ϊ������� */
    private List<NameOccurrence> use_def = null;
    
    /** ����ʹ��-������ */
    public void setUseDefList(List<NameOccurrence> use_def){
    	this.use_def=use_def;
    }
    
    /** ���ʹ��-������ */
    public List<NameOccurrence> getUseDefList(){
    	return use_def;
    }
    
    /** ��ʹ��-��������Ӷ��壬��֤���ظ���� */
    public boolean addUseDef(NameOccurrence occ){
    	if(occ.getOccurrenceType()==OccurrenceType.DEF){
    		for(NameOccurrence o:use_def){
    			if(o.getLocation()==occ.getLocation()){
    				return false;
    			}
    		}
    		use_def.add(occ);
    		return true;
    	}
    	return false;
    }
    
    /** ����-ʹ�����������˱�����������п��Ե����ʹ�ó��֣�����ʹ�ó��֣�������Ϊnull,�����е�Ԫ�ض�Ϊʹ�ó��� */
    private List<NameOccurrence> def_use = null;
    
    /** ���ö���-ʹ���� */
    public void setDefUseList(List<NameOccurrence> def_use){
    	this.def_use=def_use;
    }
    
    /** ��ö���-ʹ���� */
    public List<NameOccurrence> getDefUseList(){
    	return def_use;
    }
    
    /** ����-ʹ������Ӷ��壬��֤���ظ���� */
    public boolean addDefUse(NameOccurrence occ){
    	if(occ.getOccurrenceType()==OccurrenceType.USE){
    		for(NameOccurrence o:def_use){
    			if(o.getLocation()==occ.getLocation()){
    				return false;
    			}
    		}
    		def_use.add(occ);
    		return true;
    	}
    	return false;
    }
    
    /** ����-ȡ���������������˱�����������п��Ե���Ķ�����֣�����ʹ�ó��֣�������Ϊnull,�����е�Ԫ�ض�Ϊ������� */
    private List<NameOccurrence> def_undef = null;
    
    /** ���ö���-ȡ�������� */
    public void setDefUndefList(List<NameOccurrence> def_undef){
    	this.def_undef=def_undef;
    }
    
    /** ��ö���-ȡ�������� */
    public List<NameOccurrence> getDefUndefList(){
    	return def_undef;
    }
    
    /** ����-ȡ������Ӷ��壬��֤���ظ���� */
    public boolean addDefUndef(NameOccurrence occ){
    	if(occ.getOccurrenceType()==OccurrenceType.DEF){
    		for(NameOccurrence o:def_undef){
    			if(o.getLocation()==occ.getLocation()){
    				return false;
    			}
    		}
    		def_undef.add(occ);
    		return true;
    	}
    	return false;
    }
    
    /** ȡ������-�������������˿��Ե��ﱾ������ֵ����ж�����֣�����ʹ�ó��֣�������Ϊnull,�����е�Ԫ�ض�Ϊ������� */
    private List<NameOccurrence> undef_def = null;
    
    /** ����ȡ������-������ */
    public void setUndefDefList(List<NameOccurrence> undef_def){
    	this.undef_def=undef_def;
    }
    
    /** ���ȡ������-������ */
    public List<NameOccurrence> getUndefDefList(){
    	return undef_def;
    }
    
    /** ����-ȡ������Ӷ��壬��֤���ظ���� */
    public boolean addUndefDef(NameOccurrence occ){
    	if(occ.getOccurrenceType()==OccurrenceType.DEF){
    		for(NameOccurrence o:undef_def){
    			if(o.getLocation()==occ.getLocation()){
    				return false;
    			}
    		}
    		undef_def.add(occ);
    		return true;
    	}
    	return false;
    }
    
    /** ���������� */
    public OccurrenceType checkOccurrenceType(){
    	OccurrenceType type=OccurrenceType.USE;
    	if(isOnLeftHandSide()||isSelfAssignment()){
    		type=OccurrenceType.DEF;
    	}
    	if(type==OccurrenceType.USE){
    		if(use_def==null){
    			use_def=new LinkedList<NameOccurrence>();
    		}
    		def_use=null;
    		def_undef=null;
    		undef_def=null;
    	}else{
    		use_def=null;
    		if(def_use==null){
    			def_use=new LinkedList<NameOccurrence>();
    		}
    		if(def_undef==null){
    			def_undef=new LinkedList<NameOccurrence>();
    		}
    		if(undef_def==null){
    			undef_def=new LinkedList<NameOccurrence>();
    		}
    	}
    	return type;
    }
}
