package softtest.cfg.java;

import java.util.*;

/** ���쳣���ݵ��� */
public class TryData {
	/** ���� */
	String name = null;

	/** try����ͷ�ڵ� */
	VexNode head = null;

	/** try���ĳ��ڽڵ� */
	VexNode out = null;

	/** catch�Ӿ�ͷ�ڵ㼯�� */
	LinkedList<VexNode> catchheads = new LinkedList<VexNode>();

	/** catch�Ӿ���ڽڵ㼯�� */
	LinkedList<VexNode> catchouts = new LinkedList<VexNode>();

	/** throw���ڵ㼯�� */
	LinkedList<VexNode> thrownodes = new LinkedList<VexNode>();
	LinkedList<Class> exceptions = new LinkedList<Class>();

	/** finally�Ӿ��ͷ�ڵ� */
	VexNode finallyhead = null;
}
