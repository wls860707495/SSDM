package softtest.callgraph.java;

/** CVexNode CEdge CGraph�ĳ������ */
public abstract class CElement {
	/** ������ģʽ��accept���� */
	public abstract void accept(CGraphVisitor visitor, Object data);
}
