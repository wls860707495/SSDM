package softtest.cfg.java;

/** ͼ�ķ����߽ӿ� */
public interface GraphVisitor {
	/** �ڵ������ */
	public void visit(VexNode n, Object data);

	/** �߷����� */
	public void visit(Edge e, Object data);

	/** ͼ������ */
	public void visit(Graph g, Object data);
}
