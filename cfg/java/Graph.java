package softtest.cfg.java;

import java.util.*;

import softtest.ast.java.SimpleJavaNode;

import softtest.ast.java.*;

/** ���������ͼ���� */
public class Graph extends Element {
	/** ·������(ɾ��ì�ܱߺ��) */
	private int pathcount = 0;
	
	/** ����ʵ�ʵļ��������ڲ������� */
	private int nodecount = 0;

	/** ����ʵ�ʵļ��������ڲ������� */
	private int edgecount = 0;

	/** ��㼯�� */
	public Hashtable<String, VexNode> nodes = new Hashtable<String, VexNode>();

	/** �߼��� */
	public Hashtable<String, Edge> edges = new Hashtable<String, Edge>();

	/** ȱʡ�������캯�� */
	public Graph() {

	}

	/** ����һ���ڵ㣬����ýڵ��Ѿ����ڣ����׳��쳣 */
	VexNode addVex(VexNode vex) {
		if (nodes.get(vex.name) != null) {
			throw new RuntimeException("The vexnode has already existed.");
		}
		nodes.put(vex.name, vex);
		vex.snumber = nodecount++;
		return vex;
	}

	/** ����һ��ָ�����ƵĽڵ㣬���趨�ýڵ�����ĳ����﷨�ڵ㣬���յ����ƽ�Ϊname+nodecount */
	VexNode addVex(String name, SimpleJavaNode treenode) {
		VexNode vex = new VexNode(name + nodecount, treenode);
		vex.setGraph(this);
		return addVex(vex);
	}

	/** ����һ���ڵ㣬���趨�ýڵ�����ĳ����﷨�ڵ� */
	VexNode addVex(SimpleJavaNode treenode) {
		String name = "" + nodecount;
		return addVex(name, treenode);
	}

	/** ����һ��ָ��β��ͷ�ڵ�ıߣ����趨���� */
	Edge addEdge(VexNode tailnode, VexNode headnode, String name) {
		if (headnode == null || tailnode == null) {
			throw new RuntimeException("An edge's head or tail cannot be null.");
		}
		if (edges.get(name) != null) {
			throw new RuntimeException("The edge has already existed.");
		}
		if (nodes.get(headnode.name) != headnode || nodes.get(tailnode.name) != tailnode) {
			throw new RuntimeException("There is a contradiction.");
		}

		if (headnode.inedges.get(name) != null) {
			throw new RuntimeException("There is a contradiction.");
		}

		if (tailnode.outedges.get(name) != null) {
			throw new RuntimeException("There is a contradiction.");
		}

		Edge e = new Edge(name, tailnode, headnode);
		edges.put(name, e);
		e.snumber = edgecount++;

		return e;
	}

	/** ����һ��ָ��β��ͷ�ڵ�ıߣ����趨���� */
	Edge addEdge(String tail, String head, String name) {
		VexNode tailnode = nodes.get(tail);
		VexNode headnode = nodes.get(head);
		return addEdge(tailnode, headnode, name);
	}

	/** ����һ��ָ��β��ͷ�ڵ�ı� */
	Edge addEdge(String tail, String head) {
		String name = "" + edgecount;
		return addEdge(tail, head, name);
	}

	/** ����һ��ָ��β��ͷ�ڵ�ı� */
	Edge addEdge(VexNode tailnode, VexNode headnode) {
		String name = "" + edgecount;
		return addEdge(tailnode, headnode, name);
	}

	/** ����һ��ָ��β��ͷ�ڵ�ıߣ����趨����Ϊname+edgecount */
	Edge addEdge(String name, VexNode tailnode, VexNode headnode) {
		name = name + edgecount;
		return addEdge(tailnode, headnode, name);
	}

	/** ����һ��ָ��β��ͷ�ڵ�ıߣ����趨����ܵ���ٷ�֧��������֧������T_��ͷ���ٷ�֧��F_��ͷ,eflagΪ�쳣�߱�� */
	Edge addEdgeWithFlag(VexNode tailnode, VexNode headnode,boolean eflag) {
		String name = "";
		Edge edge = null;
		if((tailnode.truetag||tailnode.falsetag)&&eflag){
			//catch���ķ�֧���ܴ��ڳ�ͻ
			//throw new RuntimeException("illeagal arguement");
		}
		if (tailnode.truetag) {
			name = "T_";
			tailnode.truetag = false;
		} else if (tailnode.falsetag) {
			name = "F_";
			tailnode.falsetag = false;
		}else if(eflag){
			name = "E_";
		}
		edge = addEdge(name, tailnode, headnode);
		return edge;
	}	

	/** ɾ��ָ���ıߣ�����Ҳ����ñ߻��߸ñ߲���ͼ�еı����׳��쳣 */
	void removeEdge(Edge e) {
		if (edges.get(e.name) != e || e == null) {
			throw new RuntimeException("Cannot find the edge.");
		}
		if (e.headnode == null || e.tailnode == null) {
			throw new RuntimeException("There is a contradiction.");
		}
		if (e.headnode.inedges.get(e.name) != e || e.tailnode.outedges.get(e.name) != e) {
			throw new RuntimeException("There is a contradiction.");
		}

		e.headnode.inedges.remove(e.name);
		e.tailnode.outedges.remove(e.name);
		edges.remove(e.name);

	}

	/** ɾ��ָ���ı� */
	void removeEdge(String name) {
		Edge e = edges.get(name);
		removeEdge(e);
	}

	/** ɾ��ָ���ڵ��������� */
	void removeInedges(VexNode vex) {
		LinkedList<Edge> temp = new LinkedList<Edge>();
		temp.clear();
		for (Enumeration<Edge> e = vex.inedges.elements(); e.hasMoreElements();) {
			temp.add(e.nextElement());
		}
		ListIterator<Edge> i = temp.listIterator();
		while (i.hasNext()) {
			Edge edge = i.next();
			removeEdge(edge);
		}
	}

	/** ɾ��ָ���ڵ�����г��� */
	void removeOutedges(VexNode vex) {
		LinkedList<Edge> temp = new LinkedList<Edge>();
		temp.clear();
		for (Enumeration<Edge> e = vex.outedges.elements(); e.hasMoreElements();) {
			temp.add(e.nextElement());
		}
		ListIterator<Edge> i = temp.listIterator();
		while (i.hasNext()) {
			Edge edge = i.next();
			removeEdge(edge);
		}
	}

	/** ɾ��ָ���ڵ㼰������ı� */
	void removeVex(VexNode vex) {
		if (nodes.get(vex.name) != vex || vex == null) {
			throw new RuntimeException("Cannot find the vexnode.");
		}
		removeInedges(vex);
		removeOutedges(vex);
		nodes.remove(vex.name);
	}

	/** ɾ��ָ���ڵ��������� */
	void removeVex(String name) {
		VexNode vex = nodes.get(name);
		removeVex(vex);
	}

	/** ������ͼ�����ߵ�accept���� */
	@Override
	public void accept(GraphVisitor visitor, Object data) {
		visitor.visit(this, data);
	}

	/** ���ָ���ڵ��һ��û�з��ʵ����ڽڵ㣬���ܷ���null,�ڵ�˳������Ȼ˳����� */
	public VexNode getAdjUnvisitedVertex(VexNode v) {
		if (v.outedges.size() <= 0) {
			return null;
		}
		List<VexNode> list = new ArrayList<VexNode>();
		for (Enumeration<Edge> e = v.outedges.elements(); e.hasMoreElements();) {
			Edge edge = e.nextElement();
			if (!edge.headnode.getVisited()) {
				list.add(edge.headnode);
			}
		}
		Collections.sort(list);
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	/** ������ȱ�����visitorΪ�Խڵ�ķ����ߣ�dataΪ���� */
	public void dfs(GraphVisitor visitor, Object data) {
		// �ҵ�������ͼ��ڿ�ʼ
		VexNode first = null;
		Stack<VexNode> stack = new Stack<VexNode>();
		for (Enumeration e = nodes.elements(); e.hasMoreElements();) {
			VexNode n = (VexNode) e.nextElement();
			if (!n.getVisited() && n.inedges.size() == 0) {
				first = n;
				break;
			}
		}
		if (first == null) {
			throw new RuntimeException("������ͼ��ڴ���");
		}
		first.accept(visitor, data);
		first.setVisited(true);
		stack.push(first);
		while (!stack.isEmpty()) {
			VexNode next = getAdjUnvisitedVertex(stack.peek());
			if (next == null) {
				stack.pop();
			} else {
				next.accept(visitor, data);
				next.setVisited(true);
				stack.push(next);
			}
		}
		// ������Щ������ͼ��ڵ��ﲻ�˵Ľڵ㣬�����÷�����
		for (Enumeration<VexNode> e = nodes.elements(); e.hasMoreElements();) {
			VexNode n = e.nextElement();
			if (!n.getVisited()) {
				// first.accept(visitor, data);
				first.setVisited(true);
				stack.push(first);
				while (!stack.isEmpty()) {
					VexNode next = getAdjUnvisitedVertex(stack.peek());
					if (next == null) {
						stack.pop();
					} else {
						// next.accept(visitor, data);
						next.setVisited(true);
						stack.push(next);
					}
				}
			}
		}

		// �����ʱ�־�������û�false
		for (Enumeration<VexNode> e = nodes.elements(); e.hasMoreElements();) {
			VexNode n = e.nextElement();
			n.setVisited(false);
		}
	}

	/** �ڵ�˳����� */
	public void numberOrderVisit(GraphVisitor visitor, Object data) {
		List<VexNode> list = new ArrayList<VexNode>();
		for (Enumeration<VexNode> e = nodes.elements(); e.hasMoreElements();) {
			VexNode vex = e.nextElement();
			list.add(vex);
		}
		Collections.sort(list);

		Iterator<VexNode> i = list.iterator();
		while (i.hasNext()) {
			i.next().accept(visitor, data);
		}
	}
	
	/** ������нڵ�ķ��ʱ�־ */
	public void clearVisited(){
		for (Enumeration<VexNode> e = nodes.elements(); e.hasMoreElements();) {
			VexNode n = e.nextElement();
			n.setVisited(false);
		}
	}

	/** ������б��ϵ�ì�ܱ�־ */
	public void clearEdgeContradict() {
		for (Enumeration<Edge> e = edges.elements(); e.hasMoreElements();) {
			Edge n = e.nextElement();
			n.setContradict(false);
		}
	}

	/** ������нڵ��ϵ�ì�ܱ�־ */
	public void clearVexNodeContradict() {
		for (Enumeration<VexNode> e = nodes.elements(); e.hasMoreElements();) {
			VexNode n = e.nextElement();
			n.setContradict(false);
		}
	}
	
	/**
	 * @param fromvex �����ڵ�
	 * @param tovex	Ŀ�Ľڵ�
	 * @param tempe ���ܾ����ı�
	 * @return ����ɴ��򷵻�true�����򷵻�false
	 */
	public static boolean checkVexReachable(VexNode fromvex,VexNode tovex,Edge tempe){
		if(fromvex==null||tovex==null||tempe==null){
			return false;
		}
		if(fromvex==tovex){
			return true;
		}
		Stack<VexNode> stack = new Stack<VexNode>();
		HashSet<VexNode> table=new HashSet<VexNode>();
		stack.push(fromvex);
		table.add(fromvex);
		while (!stack.isEmpty()) {
			VexNode v=stack.pop();
			for (Enumeration<Edge> e = v.outedges.elements(); e.hasMoreElements();) {
				Edge edge = e.nextElement();
				if(edge==tempe){
					continue;
				}
				VexNode tempnode=edge.headnode;
				if (!table.contains(tempnode)) {
					if(tempnode==tovex){
						return true;
					}
					table.add(tempnode);
					stack.push(tempnode);
				}
			}
		}
		return false;
	}
	
	/**
	 * @param fromvex �����ڵ�
	 * @param tovex	Ŀ�Ľڵ�
	 * @return һ��·��
	 */
	public static List<VexNode> findAPath(VexNode fromvex,VexNode tovex){
		List<VexNode> ret=new ArrayList<VexNode>();
		if(fromvex==null||tovex==null){
			return ret;
		}
		if(fromvex==tovex){
			ret.add(fromvex);
			return ret;
		}
		Stack<VexNode> stack = new Stack<VexNode>();
		HashSet<VexNode> table=new HashSet<VexNode>();
		stack.push(fromvex);
		table.add(fromvex);
		
		while (!stack.isEmpty()) {
			VexNode current=stack.peek();
			VexNode next=null;
			for (Enumeration<Edge> e = current.outedges.elements(); e.hasMoreElements();) {
				Edge edge = e.nextElement();
				VexNode tempnode=edge.headnode;
				if(!table.contains(tempnode)){
					next=tempnode;
				}
			}
			if (next == null) {
				stack.pop();
			} else {
				if(next==tovex){
					for(VexNode v:stack){
						ret.add(v);
					}
					ret.add(tovex);
					return ret;
				}else{
					table.add(next);
					stack.push(next);
				}
			}
		}		
		
		
		return ret;
	}
	
	/**
	 * @return ������ڽڵ�
	 */
	public VexNode getEntryNode(){
		for (Enumeration<VexNode> e = nodes.elements(); e.hasMoreElements();) {
			VexNode vex = e.nextElement();
			if(vex.getName().startsWith("func_head_")){
				return vex;
			}
		}
		throw new RuntimeException("Cannot find entry node.");
	}
	
	/**
	 * @return ���س��ڽڵ�
	 */
	public VexNode getExitNode(){
		for (Enumeration<VexNode> e = nodes.elements(); e.hasMoreElements();) {
			VexNode vex = e.nextElement();
			if(vex.getName().startsWith("func_out_")){
				return vex;
			}
		}
		throw new RuntimeException("Cannot find exit node.");
	}

	/**
	 * @return the pathcount
	 */
	public int getPathcount() {
		return pathcount;
	}

	/**
	 * @param pathcount the pathcount to set
	 */
	public void setPathcount(int pathcount) {
		this.pathcount = pathcount;
	}
}