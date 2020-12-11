package softtest.cfg.java;

/** ���������ͼ�еıߵ��� */
public class Edge extends Element implements Comparable<Edge> {
	/** ���� */
	String name = null;

	/** ͷ��� */
	VexNode headnode = null;

	/** β���� */
	VexNode tailnode = null;
	
	/** ���ڱȽϵ����� */
	int snumber = 0;
	
	/** �÷�֧�Ƿ�ì�ܱ�־�����ڿ�����ͼ�еĲ��ɴ�·�� */
	boolean contradict=false;

	/** �Ը����ִ���һ���ߣ���ʱͷβ�ڵ㶼Ϊ�գ����趨 */
	public Edge(String name) {
		this.name = name;
	}

	/** ��ָ�������֣�β�ڵ��ͷ�ڵ㴴���� */
	public Edge(String name, VexNode tailnode, VexNode headnode) {
		this.name = name;
		this.headnode = headnode;
		this.tailnode = tailnode;
		headnode.inedges.put(name, this);
		tailnode.outedges.put(name, this);
	}

	/** ������ģʽ��accept���� */
	@Override
	public void accept(GraphVisitor visitor, Object data) {
		visitor.visit(this, data);
	}

	/** ���β�ڵ� */
	public VexNode getTailNode(){
		return tailnode;
	}
	
	/** ���ͷ�ڵ� */
	public VexNode getHeadNode(){
		return headnode;
	}
	
	/** ������� */
	public String getName(){
		return name;
	}
	
	/** ��÷�֧�Ƿ�ì�ܱ�־ */
	public boolean getContradict(){
		return contradict;
	}
	
	/** ���÷�֧�Ƿ�ì�ܱ�־ */
	public void setContradict(boolean contradict){
		this.contradict=contradict;
	}
	
	/** �Ƚϱߵ�˳���������� */
	public int compareTo(Edge e) {
		if (snumber == e.snumber) {
			return 0;
		} else if (snumber > e.snumber) {
			return 1;
		} else {
			return -1;
		}
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		return sb.toString();
	}
}
