package softtest.callgraph.java;

import java.util.*;

import softtest.ast.java.ASTMethodDeclaration;
import softtest.symboltable.java.*;
import softtest.ast.java.*;
import softtest.symboltable.java.MethodNameDeclaration;

/** ���ù�ϵͼ�Ķ����� */
public class CVexNode extends CElement implements Comparable<CVexNode> {
	/** ���� */
	String name;

	/** ��߼��� */
	Hashtable<String, CEdge> inedges = new Hashtable<String, CEdge>();

	/** ���߼��� */
	Hashtable<String, CEdge> outedges = new Hashtable<String, CEdge>();

	/** ���ʱ�־ */
	boolean visited = false;

	/** ���ڱȽϵ����� */
	int snumber = 0;
	
	/** ���������������õ�����ȼ���*/
	int indegree = 0;
	
	/** ��Ӧ�ĺ������� */
	MethodNameDeclaration mnd = null;

	/** ��ָ�������ִ������ù�ϵͼ�ڵ� */
	public CVexNode(String name,MethodNameDeclaration mnd) {
		this.name = name;
		this.mnd=mnd;
		mnd.setCallGraphVex(this);
	}
	
	/** ���ú������� */
	public void setMethodNameDeclaration(MethodNameDeclaration mnd){
		this.mnd=mnd;
	}
	
	/** ��ú������� */
	public MethodNameDeclaration getMethodNameDeclaration(){
		return this.mnd;
	}
	
	/**��ú����﷨���ڵ�*/
	public ASTMethodDeclaration getMethodDeclaration(){
		return (ASTMethodDeclaration)mnd.getMethodNameDeclaratorNode().jjtGetParent();
	}

	/** ������ͼ�����ߵ�accept */
	@Override
	public void accept(CGraphVisitor visitor, Object data) {
		visitor.visit(this, data);
	}
	
	/** ���ýڵ���ʱ�־ */
	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	/** ��ýڵ���ʱ�־ */
	public boolean getVisited() {
		return visited;
	}

	/** ��ýڵ����� */
	public String getName() {
		return name;
	}

	/** �����߼��� */
	public Hashtable<String, CEdge> getInedges() {
		return inedges;
	}

	/** ��ó��߼��� */
	public Hashtable<String, CEdge> getOutedges() {
		return outedges;
	}

	/** �Ƚ������˳���������� */
	public int compareTo(CVexNode e) {
		if (snumber == e.snumber) {
			return 0;
		} else if (snumber > e.snumber) {
			return 1;
		} else {
			return -1;
		}
	}
	
	/** ���һ���ڵ��Ƿ���ǰ�� */
	public boolean isPreNode(CVexNode p){
		for(Enumeration e=inedges.elements();e.hasMoreElements();){
			CEdge edge=(CEdge)e.nextElement();
			if(p==edge.getTailNode()){
				return true;
			}
		}
		return false;
	}
}