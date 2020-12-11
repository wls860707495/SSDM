package softtest.callgraph.java.method;

import java.util.*;

import softtest.ast.java.SimpleJavaNode;


/** ����������Ϣ������*/
public abstract class AbstractFeature {
	/**����������Ϣ������keyΪ������value��ǰΪ���ٺ������õ�trace��Ϣ
	 * keyΪ����,valueΪtrace��Ϣ
	 * */
	Hashtable<MapOfVariable,List<String>> table=new Hashtable<MapOfVariable,List<String>>();

	public Hashtable<MapOfVariable,List<String>> getTable(){
		return table;
	}

	/**
	 * ����ܵ��õĽӿڣ����ÿ���������ڵ���д�������ڱ���������ͼ�Ĺ����л���
	 * ��ǰ�������Ŀ�����ͼ�ڵ㲻�ϵ��øýӿڣ�setΪ��ǰ�����ĺ���������Ϣ���ϣ������
	 * Ҫ��ժҪ�����Ӻ���������Ϣ��this��ӵ�ǰ����������set�С�
	 * @param node �������߹��캯����Ӧ�﷨���ڵ�
	 * @param set ��ǰ�����ĺ���������Ϣ����
	 */
	abstract public void listen(SimpleJavaNode node,FeatureSet set);
}
