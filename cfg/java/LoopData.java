package softtest.cfg.java;

import java.util.*;

/** ��ѭ�����ݵ��� */
public class LoopData {
	/** ���� */
	String name = null;

	/** ѭ������ͷ�ڵ� */
	VexNode head = null;
	
	/** switch�Ƿ���default��־ */
	boolean hasdefault = false;

	/** break�ڵ㼯�� */
	LinkedList<VexNode> breaknodes = new LinkedList<VexNode>();

	/** continue�ڵ㼯�� */
	LinkedList<VexNode> continuenodes = new LinkedList<VexNode>();
}