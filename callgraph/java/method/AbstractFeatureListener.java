package softtest.callgraph.java.method;

import softtest.ast.java.SimpleJavaNode;

public abstract class AbstractFeatureListener {
	/**��ǰ������������Ϣ����*/
	protected FeatureSet set=null;
	
	/**��õ�ǰ������������Ϣ����*/
	public FeatureSet getFeatureSet() {
		return set;
	}
	
	/**���õ�ǰ������������Ϣ����*/
	public void setFeatureSet(FeatureSet set) {
		this.set = set;
	}
	
	/**���Ժ��Դ��ڵĳ����๹�췽��*/
	protected AbstractFeatureListener(){}
	
	/**�����ӿ�*/
	abstract public void listen(SimpleJavaNode node);
}
