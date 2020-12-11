package softtest.callgraph.java;

/** ������ù�ϵͼ�еıߵ��� */
public class CEdge extends CElement implements Comparable<CEdge> {
	/** ���� */
	String name = null;

	/** ͷ��� */
	CVexNode headnode = null;

	/** β���� */
	CVexNode tailnode = null;
	
	/** ���ڱȽϵ����� */
	int snumber = 0;
	
	/** �Ը����ִ���һ���ߣ���ʱͷβ�ڵ㶼Ϊ�գ����趨 */
	public CEdge(String name) {
		this.name = name;
	}

	/** ��ָ�������֣�β�ڵ��ͷ�ڵ㴴���� */
	public CEdge(String name, CVexNode tailnode, CVexNode headnode) {
		this.name = name;
		this.headnode = headnode;
		this.tailnode = tailnode;
		headnode.inedges.put(name, this);
		tailnode.outedges.put(name, this);
	}

	/** ������ģʽ��accept���� */
	@Override
	public void accept(CGraphVisitor visitor, Object data) {
		visitor.visit(this, data);
	}

	/** ���β�ڵ� */
	public CVexNode getTailNode(){
		return tailnode;
	}
	
	/** ���ͷ�ڵ� */
	public CVexNode getHeadNode(){
		return headnode;
	}
	
	/** ������� */
	public String getName(){
		return name;
	}
	
	/** �Ƚϱߵ�˳���������� */
	public int compareTo(CEdge e) {
		if (snumber == e.snumber) {
			return 0;
		} else if (snumber > e.snumber) {
			return 1;
		} else {
			return -1;
		}
	}
}