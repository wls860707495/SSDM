package softtest.callgraph.java.method;

import softtest.ast.java.SimpleJavaNode;

/**��������������*/
abstract public class AbstractPostcondition {
	
	/**
	 * ����ܵ��õĽӿڣ����ÿ���������ڵ���д�������ڱ���������ͼ�Ĺ����л���
	 * ��ǰ�������Ŀ�����ͼ�ڵ㲻�ϵ��øýӿڣ�setΪ��ǰ�����ĺ����������ϣ������
	 * Ҫ��ժҪ�����Ӻ���������this��ӵ�ǰ����������set�С�
	 * @param node �������߹��캯����Ӧ�﷨���ڵ�
	 * @param set ��ǰ�����ĺ�����������
	 */
	abstract public void listen(SimpleJavaNode node,PostconditionSet set); 
}
