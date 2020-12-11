package softtest.callgraph.java.method;

import softtest.ast.java.SimpleJavaNode;

public abstract class AbstractPostconditionListener {
	/**��ǰ������ǰ����������*/
	protected PostconditionSet set=null;
	
	/**��õ�ǰ������ǰ����������*/
	public PostconditionSet getPostconditionSet() {
		return set;
	}
	
	/**���õ�ǰ������ǰ����������*/
	public void setPostconditionSet(PostconditionSet set) {
		this.set = set;
	}
	
	/**���Ժ��Դ��ڵĳ����๹�췽��*/
	protected AbstractPostconditionListener(){}
	
	/**�����ӿ�*/
	abstract public void listen(SimpleJavaNode node);
}
