package softtest.callgraph.java;

import java.io.*;
import java.util.*;

/** ���ڲ���.dot�ļ��ĵ��ù�ϵͼ������ */
public class DumpCGraphVisitor implements CGraphVisitor{
	/** ���ʿ�����ͼ�Ľڵ㣬��ӡ�ڵ����֣���ǰ�ı����򼯣������޶��� */
	public void visit(CVexNode n, Object data) {
		FileWriter out = (FileWriter) data;
		try {
			String s = null;
			s = ""+"\"";
			out.write(n.name + "[label=\"" + n.name + "\\n" + s + "];\n");
		} catch (IOException ex) {
		}
	}

	/** ���ʿ�����ͼ�ıߣ���ӡ���� */
	public void visit(CEdge e, Object data) {
		FileWriter out = (FileWriter) data;
		try {
			String s = null;
			s = e.tailnode.name + " -> " + e.headnode.name + "[label=\"" + e.name + "\"";
			s = s + "];\n";
			out.write(s);
		} catch (IOException ex) {
		}
	}

	/** ���ʿ�����ͼ������������ڵ㼯�Ϻͱ߼��� */
	public void visit(CGraph g, Object data) {
		try {
			FileWriter out = new FileWriter((String) data);
			out.write("digraph G {\n");

			for (Enumeration<CVexNode> e = g.nodes.elements(); e.hasMoreElements();) {
				CVexNode n = e.nextElement();
				visit(n, out);
			}

			for (Enumeration<CEdge> e = g.edges.elements(); e.hasMoreElements();) {
				CEdge edge = e.nextElement();
				visit(edge, out);
			}
			out.write(" }");
			out.close();
		} catch (IOException ex) {
		}
	}
}
