package softtest.cfg.java;

/** VexNode Edge Graph�ĳ������ */
public abstract class Element {
	/** ������ģʽ��accept���� */
	public abstract void accept(GraphVisitor visitor, Object data);
}
