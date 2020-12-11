package softtest.cfg.java;

import java.util.*;

/** ���ڿ�����ͼ���ɹ����е����ݴ��ݣ�������Ϣ���ݣ���ο�ControlFlowVisitor */
public class ControlFlowData {
	/** ��ǰ������ͼ�ĳ��ڽڵ� */
	VexNode vexnode = null; 

	/** ��ǰ������ͼ */
	Graph graph = null; 

	/** ѭ������ջ */
	Stack<LoopData> loopstack = new Stack<LoopData>(); 

	/** ת�����ݱ� */
	Hashtable<String,LabelData> labeltable = new Hashtable<String,LabelData>();

	/** try����ջ */
	Stack<TryData> trystack = new Stack<TryData>();
}