package softtest.DefUseAnalysis.java;

import java.util.*;
import softtest.symboltable.java.*;
import softtest.cfg.java.*;
import softtest.symboltable.java.NameOccurrence;
import softtest.symboltable.java.VariableNameDeclaration;

public class DUControlFlowVisitor implements GraphVisitor {
	/** �Խڵ���з��� */
	public void visit(VexNode n, Object data) {
		calculateIN(n,data);
		calculateOUT(n,data);
	}

	/** �Ա߽��з��� */
	public void visit(Edge e, Object data) {

	}

	/** ��ͼ���з��� */
	public void visit(Graph g, Object data) {

	}

	/** ����In */
	public void calculateIN(VexNode n, Object data) {
		List<Edge> list = new ArrayList<Edge>();
		for (Enumeration<Edge> e = n.getInedges().elements(); e.hasMoreElements();) {
			list.add(e.nextElement());
		}
		Collections.sort(list);

		// ����ǰ���ڵ��U(in)
		VexNode pre = null;
		Iterator<Edge> iter = list.iterator();
		while (iter.hasNext()) {
			Edge edge = iter.next();
			pre = edge.getTailNode();
			
			if (edge.getName().startsWith("F")) {
				//����һ��ѭ��
				if (n.getName().startsWith("while_out") || n.getName().startsWith("for_out")) {
					visit(pre, data);
				}
			}
			
			//��Ͽ��������󲢼�
			n.getLiveDefs().mergeLiveDefs(pre.getLiveDefs());
		}
		//System.out.println(n.getName()+"    "+n.getLiveDefs());
	}

	/** ����Out */
	public void calculateOUT(VexNode n, Object data) {
		for(NameOccurrence occ:n.getOccurrences()){
			if(!(occ.getDeclaration() instanceof VariableNameDeclaration)){
				continue;
			}
			LiveDefsSet  livedefs=n.getLiveDefs();
			VariableNameDeclaration v=(VariableNameDeclaration)occ.getDeclaration();
			if(occ.getOccurrenceType()==NameOccurrence.OccurrenceType.USE){
				for(NameOccurrence o: livedefs.getVariableLiveDefs(v)){
					o.addDefUse(occ);
					occ.addUseDef(o);
				}
			}else{
				livedefs.setNewDef(occ);
			}
		}
	}
}
