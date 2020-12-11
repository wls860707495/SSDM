package softtest.callgraph.java.method;

import softtest.ast.java.SimpleJavaNode;

/**ǰ���������ɼ����߳�����*/
public abstract class AbstractPreconditionListener {
	/**��ǰ������ǰ����������*/
	protected PreconditionSet set=null;
	
	/**��õ�ǰ������ǰ����������*/
	public PreconditionSet getPreconditionSet() {
		return set;
	}
	
	/**���õ�ǰ������ǰ����������*/
	public void setPreconditionSet(PreconditionSet set) {
		this.set = set;
	}
	
	/**���Ժ��Դ��ڵĳ����๹�췽��*/
	protected AbstractPreconditionListener(){}
	
	/**�����ӿ�*/
	abstract public void listen(SimpleJavaNode node);
}
