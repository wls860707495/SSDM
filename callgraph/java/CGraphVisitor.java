package softtest.callgraph.java;

/** ͼ�ķ����߽ӿ� */
public interface CGraphVisitor {
	/** �ڵ������ */
	public void visit(CVexNode n, Object data);

	/** �߷����� */
	public void visit(CEdge e, Object data);

	/** ͼ������ */
	public void visit(CGraph g, Object data);
}
