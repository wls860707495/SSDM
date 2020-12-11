package softtest.cfg.java;

import softtest.ast.java.*;
import softtest.symboltable.java.*;

import java.util.*;
import softtest.DefUseAnalysis.java.*;
import softtest.IntervalAnalysis.java.*;
import softtest.ast.java.ASTMethodDeclaration;
import softtest.ast.java.SimpleJavaNode;
import softtest.fsm.java.*;
import softtest.symboltable.java.NameOccurrence;
import softtest.symboltable.java.VariableNameDeclaration;

/** ͼ�Ķ����� */
public class VexNode extends Element implements Comparable<VexNode> {
	/** ���� */
	String name;

	/** ��߼��� */
	Hashtable<String, Edge> inedges = new Hashtable<String, Edge>();

	/** ���߼��� */
	Hashtable<String, Edge> outedges = new Hashtable<String, Edge>();
	
	/** �������׳����쳣���� */
	Set<Class> exceptions = new HashSet<Class>();

	/** �ڵ��Ӧ�ĳ����﷨���ڵ� */
	SimpleJavaNode treenode = null;

	/** ���֧��־ */
	boolean truetag = false;

	/** �ٷ�֧��־ */
	boolean falsetag = false;

	/** ���ʱ�־ */
	boolean visited = false;

	/** �����޶��� */
	ConditionData condata = null;

	/** �ڵ�����ı����� */
	private DomainSet domainset = null;
	
	/** ���㵱ǰ�ڵ�֮ǰ�ı����� */
	private DomainSet lastdomainset=null;

	/** ���ڱȽϵ����� */
	int snumber = 0;

	/** �ýڵ��Ƿ�ì�ܱ�־�����ڿ�����ͼ�еĲ��ɴ�·�� */
	boolean contradict = false;
	
	private Graph g=null;

	/** ״̬��ʵ������ */
	FSMMachineInstanceSet fsminstanceset = new FSMMachineInstanceSet();
	
	/** �ڵ��ϵı������ּ��� */
	ArrayList<NameOccurrence> occs=new ArrayList<NameOccurrence>();
	
	/** �ܹ����ﵱǰ�ڵ�ı���������� */
	LiveDefsSet liveDefs = new LiveDefsSet();
	
	public void addExceptions(Set<Class> ee) {
		exceptions.addAll(ee);
	}
	
	public Set<Class> getExceptions() {
		return exceptions;
	}
	
	/** ��ýڵ��ϵı������ּ��� */
	public ArrayList<NameOccurrence> getOccurrences(){
		return occs;
	}
	
	/**
	 * @param location Ҫ���ҵĳ��ֶ�Ӧ���﷨���ڵ�
	 * @return �������֣����û���ҵ��򷵻�null
	 */
	public NameOccurrence findOccurrence(SimpleJavaNode location){
		NameOccurrence ret=null;
		for(NameOccurrence occ:occs){
			if(occ.getLocation()==location){
				ret=occ;
				break;
			}
		}
		return ret;
	}
	
	/**
	 * @param v Ҫ���ҵĳ��ֶ�Ӧ�ı�������
	 * @return ���еķ��������ĳ���
	 */
	public List<NameOccurrence> findOccurrenceOfVarDecl(VariableNameDeclaration v){
		ArrayList<NameOccurrence> ret=new ArrayList<NameOccurrence>();
		for(NameOccurrence occ:occs){
			if(occ.getLocation() instanceof ASTName){
				ASTName name=(ASTName)occ.getLocation();
				for(NameDeclaration v1:name.getNameDeclarationList()){
					if(v1==v){
						ret.add(occ);
					}
				}
			}
		}
		return ret;
	}
	
	/** ���ýڵ��ϵı������ּ��� */
	public void setOccurrences(ArrayList<NameOccurrence> occs){
		this.occs=occs;
	}
	
	/** ��ýڵ㵽�ﶨ�弯�� */
	public LiveDefsSet getLiveDefs(){
		return liveDefs;
	}
	
	/** ���ýڵ㵽�ﶨ�弯�� */
	public void setLiveDefs(LiveDefsSet liveDefs){
		this.liveDefs=liveDefs;
	}
	
	/** ���״̬��ʵ������ */
	public FSMMachineInstanceSet getFSMMachineInstanceSet() {
		return fsminstanceset;
	}

	/** ����״̬��ʵ������ */
	public void setFSMMachineInstanceSet(FSMMachineInstanceSet fsminstanceset) {
		this.fsminstanceset = fsminstanceset;
	}
	
	/**�����������*/
	public void setSnumber(int snumber){
		this.snumber=snumber;
	}
	
	/**����������*/
	public int getSnumber(){
		return this.snumber;
	}

	/** ��ָ�������ֺ��﷨���ڵ㴴��������ͼ�ڵ� */
	public VexNode(String name, SimpleJavaNode treenode) {
		this.name = name;
		this.treenode = treenode;
		treenode.setVexNode(this);
	}

	/** ������ͼ�����ߵ�accept */
	@Override
	public void accept(GraphVisitor visitor, Object data) {
		visitor.visit(this, data);
	}

	/** ���ýڵ�����ı����� */
	public void setDomainSet(DomainSet domainset) {
		this.domainset = domainset;
	}

	/** ��ýڵ�����ı����� */
	public DomainSet getDomainSet() {
		return domainset;
	}
	
	/** ���ýڵ�֮ǰ�ı����� */
	public void setLastDomainSet(DomainSet lastdomainset) {
		this.lastdomainset = lastdomainset;
	}

	/** ��ýڵ�֮ǰ�ı����� */
	public DomainSet getLastDomainSet() {
		return lastdomainset;
	}

	/** ���ýڵ�����������޶��� */
	public void setConditionData(ConditionData condata) {
		this.condata = condata;
	}

	/** ��ýڵ�����������޶��� */
	public ConditionData getConditionData() {
		return condata;
	}

	/** ��ָ���ı�����ͬ��ǰ�ı������ںϣ��ں��߼���ͨ�����ڶ�����������ʱ���򼯺ϲ� */
	public void mergeDomainSet(DomainSet d) {
		if (domainset == null) {
			domainset = new DomainSet();
		}
		if (d != null) {
			// ȥ����Щ����������ı���
			DomainSet temp = new DomainSet();
			Set entryset = d.getTable().entrySet();
			Iterator i = entryset.iterator();
			Object d1 = null;
			VariableNameDeclaration v1 = null;
			while (i.hasNext()) {
				Map.Entry e = (Map.Entry) i.next();
				v1 = (VariableNameDeclaration) e.getKey();
				d1 = e.getValue();
				if (getTreeNode().getScope().isSelfOrAncestor(v1.getDeclareScope())) {
					temp.getTable().put(v1, d1);
				}
			}
			domainset.mergeDomainSet(temp,this);
		} else {
			domainset.mergeDomainSet(d,this);
		}
	}
	
	/** ɾ����û�б�Ҫ����ڽڵ��ϵ��� */
	public void removeRedundantDomain(){
		if(domainset!=null){
			domainset.removeRedundantDomain();
		}
	}

	/** �޸ı���v���� */
	public Object addDomain(VariableNameDeclaration v, Object domain) {
		if (domainset == null) {
			domainset = new DomainSet();
		}
		return domainset.addDomain(v, domain);
	}

	/** ���ر���v�����������v�����ڽڵ���������򷵻�null */
	public Object getDomain(VariableNameDeclaration v) {
		if (domainset == null) {
			return null;
		} else {
			return domainset.getDomain(v);
		}
	}

	/** ���ر���v�����������v�����ڽڵ���������򷵻�ȱʡ�� */
	public Object getDomainWithoutNull(VariableNameDeclaration v) {
		Object domain = getDomain(v);
		if (domain == null) {
			domain = v.getDomain();
		}
		return domain;
	}
	
	/** ���ر���v����ڵ�ʱ�����������v�����ڽڵ���������򷵻�ȱʡ�� */
	public Object getLastDomainWithoutNull(VariableNameDeclaration v) {
		Object domain=null;
		if (lastdomainset != null) {
			domain=lastdomainset.getDomain(v);
		} 
		if (domain == null) {
			domain = v.getDomain();
		}
		return domain;
	}

	/** ���ýڵ���ʱ�־ */
	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	/** ��ýڵ���ʱ�־ */
	public boolean getVisited() {
		return visited;
	}

	/** ��ýڵ��Ƿ�ì�ܱ�־ */
	public boolean getContradict() {
		return contradict;
	}

	/** ���ýڵ��Ƿ�ì�ܱ�־ */
	public void setContradict(boolean contradict) {
		this.contradict = contradict;
	}

	/** ���ýڵ�������﷨���ڵ� */
	public void setTreeNode(SimpleJavaNode treenode) {
		this.treenode = treenode;
	}

	/** ��ýڵ�������﷨���ڵ� */
	public SimpleJavaNode getTreeNode() {
		return treenode;
	}

	/** ��ýڵ����� */
	public String getName() {
		return name;
	}

	/** �����߼��� */
	public Hashtable<String, Edge> getInedges() {
		return inedges;
	}

	/** ��ó��߼��� */
	public Hashtable<String, Edge> getOutedges() {
		return outedges;
	}

	/** �Ƚ������˳���������� */
	public int compareTo(VexNode e) {
		if (snumber == e.snumber) {
			return 0;
		} else if (snumber > e.snumber) {
			return 1;
		} else {
			return -1;
		}
	}

	/** ��״̬��ʵ������set�ϲ���fsminstanceset�� */
	public void mergeFSMMachineInstances(FSMMachineInstanceSet set) {
		fsminstanceset.mergeFSMMachineInstances(set);
	}
	
	public void mergFSMMachineInstancesWithoutConditon(FSMMachineInstanceSet set){
		fsminstanceset.mergFSMMachineInstancesWithoutConditon(set);
	}

	/** �õ���������ֵ�� */
	public Object getReturnDomain() {
		if (treenode instanceof ASTMethodDeclaration) {
			ASTMethodDeclaration method = (ASTMethodDeclaration) treenode;
			return method.getDomain();
		}
		return null;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		return sb.toString();
	}
	
	/** �жϽڵ��ǲ�������ĩβ�ڵ㣬��if��out */
	public boolean isBackNode(){
		if(name.startsWith("if_out")||name.startsWith("while_out")
		||name.startsWith("for_out")||name.startsWith("switch_out")
		||name.startsWith("do_while_out2")||name.startsWith("func_out")
		||name.startsWith("try_out")||name.startsWith("lable_out")
		||name.startsWith("func_eout")){
			return true;
		}
		return false;
	}	
	
	/**
	 * ���ҵ�ǰ�ڵ㵽��һ���ڵ�֮��ı�
	 * @param head ָ������һ���ڵ�
	 * @return ��ǰ�ڵ㵽��һ���ڵ��������һ���ߣ��򷵻ظı䣬���򷵻�null
	 */
	public Edge getEdgeByHead(VexNode head){
		for(Edge e:outedges.values()){
			if(e.headnode==head){
				return e;
			}
		}
		return null;
	}

	public Graph getGraph() {
		return g;
	}

	public void setGraph(Graph g) {
		this.g = g;
	}
	
	private SimpleJavaNode cascadeNode;

	public SimpleJavaNode getCascadeNode() {
		return cascadeNode;
	}

	public void setCascadeNode(SimpleJavaNode cascadeNode) {
		this.cascadeNode = cascadeNode;
	}
}
