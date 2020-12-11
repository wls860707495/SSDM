package softtest.cfg.java;

import java.io.*;
import java.util.*;

/** ���ڲ���.dot�ļ��Ŀ�����ͼ������ */
public class DumpEdgesVisitor implements GraphVisitor { // ��ͼ�����ķ����߽ӿ�
	/** ���ʿ�����ͼ�Ľڵ㣬��ӡ�ڵ����֣���ǰ�ı����򼯣������޶��� */
	public void visit(VexNode n, Object data) {
		FileWriter out = (FileWriter) data;
		try {
			String s = null;
			s = "" + n.getDomainSet();
			if (n.getConditionData() != null) {
				s = s + "\\n" + n.getConditionData();
			}
			if (n.getFSMMachineInstanceSet() != null) {
				s = s + "\\n" + n.getFSMMachineInstanceSet();
			}
			if (n.getReturnDomain() != null) {
				s = s + "\\nreturn: " + n.getReturnDomain();
			}
			s = s + "\"";
			if (n.getContradict()) {
				s = s + ",color=red";
			}
			out.write(n.name + "[label=\"" + n.name + "\\n" + s + "];\n");
		} catch (IOException ex) {
		}
	}

	/** ���ʿ�����ͼ�ıߣ���ӡ���� */
	public void visit(Edge e, Object data) {
		FileWriter out = (FileWriter) data;
		try {
			String s = null;
			s = e.tailnode.name + " -> " + e.headnode.name + "[label=\"" + e.name + "\"";
			if (e.getContradict()) {
				s = s + ",color=red";
			}
			s = s + "];\n";
			out.write(s);
			// out.write(e.tailnode.name + " -> " + e.headnode.name +
			// "[label=\"" + e.name + "\"];\n");
		} catch (IOException ex) {
		}
	}

	/** ���ʿ�����ͼ������������ڵ㼯�Ϻͱ߼��� */
	public void visit(Graph g, Object data) {
		try {
			FileWriter out = new FileWriter((String) data);
			out.write("digraph G {\n");

			for (Enumeration<VexNode> e = g.nodes.elements(); e.hasMoreElements();) {
				VexNode n = e.nextElement();
				visit(n, out);
			}

			for (Enumeration<Edge> e = g.edges.elements(); e.hasMoreElements();) {
				Edge edge = e.nextElement();
				visit(edge, out);
			}
			out.write(" }");
			out.close();
		} catch (IOException ex) {
		}
	}
}
