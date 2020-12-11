package softtest.callgraph.java.method;

import java.util.*;

import softtest.ast.java.SimpleJavaNode;


/**ǰ������������*/
abstract public class AbstractPrecondition {
	/**ǰ�����������ı�����keyΪ������value��ǰΪ���ٺ������õ�trace��Ϣ*/
	Hashtable<MapOfVariable,List<String>> table=new Hashtable<MapOfVariable,List<String>>();
	

	/**
	 * ����ܵ��õĽӿڣ����ÿ���������ڵ���д�������ڱ���������ͼ�Ĺ����л���
	 * ��ǰ�������Ŀ�����ͼ�ڵ㲻�ϵ��øýӿڣ�setΪ��ǰ������ǰ���������ϣ������
	 * Ҫ��ժҪ������ǰ��������this��ӵ�ǰ����������set�С�
	 * @param node �������߹��캯����Ӧ�﷨���ڵ�
	 * @param set ��ǰ������ǰ����������
	 */
	abstract public void listen(SimpleJavaNode node,PreconditionSet set);
	
	public Hashtable<MapOfVariable,List<String>> getTable(){
		return table;
	}
}
