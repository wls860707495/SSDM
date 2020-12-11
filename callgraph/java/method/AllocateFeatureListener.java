package softtest.callgraph.java.method;

import softtest.ast.java.SimpleJavaNode;

/**RL������Դ���������ߣ�����*/
public class AllocateFeatureListener extends AbstractFeatureListener{

	/**ȫ��ֻ��һ��Ψһ�Ķ���ͨ��getInstance()���*/
	private static AllocateFeatureListener onlyone=new AllocateFeatureListener();
	
	/**˽�еĹ��캯������ֹnew*/
	private AllocateFeatureListener(){}
	
	/**���ȫ��Ψһ�Ķ���*/
	public static AllocateFeatureListener getInstance(){
		return onlyone; 
	}
	
	/**ʵ�ּ����ӿ�*/
	@Override
	public void listen(SimpleJavaNode node) {
		new AllocateFeature().listen(node, set);
	}
}
