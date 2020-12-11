package softtest.DefUseAnalysis.java;

import java.util.*;

import softtest.ast.java.*;
import softtest.symboltable.java.NameOccurrence;
import softtest.symboltable.java.VariableNameDeclaration;
import softtest.symboltable.java.*;

public class LiveDefsSet {
	/** �������ﶨ����ֹ�ϣ�� */
	Hashtable<VariableNameDeclaration, Hashtable<SimpleNode, NameOccurrence>> table = new Hashtable<VariableNameDeclaration, Hashtable<SimpleNode, NameOccurrence>>();

	/** ��ñ������ﶨ����ֹ�ϣ�� */
	public Hashtable<VariableNameDeclaration, Hashtable<SimpleNode, NameOccurrence>> getTable() {
		return table;
	}

	/** ���ñ������ﶨ����ֹ�ϣ�� */
	public void setTable(Hashtable<VariableNameDeclaration, Hashtable<SimpleNode, NameOccurrence>> table) {
		this.table = table;
	}

	/** ���ñ������¶��� */
	public void setNewDef(NameOccurrence occ) {
		if (occ.getOccurrenceType() != NameOccurrence.OccurrenceType.DEF) {
			return;
		}
		if (occ.getDeclaration() == null || !(occ.getDeclaration() instanceof VariableNameDeclaration)) {
			return;
		}
		VariableNameDeclaration v = (VariableNameDeclaration) occ.getDeclaration();
		Hashtable<SimpleNode, NameOccurrence> occs = table.get(v);
		if (occs == null) {
			occs = new Hashtable<SimpleNode, NameOccurrence>();
			table.put(v, occs);
		}

		for (Enumeration<NameOccurrence> e = occs.elements(); e.hasMoreElements();) {
			NameOccurrence o = e.nextElement();
			o.addDefUndef(occ);
			occ.addUndefDef(o);
		}
		occs.clear();
		occs.put(occ.getLocation(), occ);
	}

	/** ���ӱ������ﶨ����� */
	public void addLiveDef(NameOccurrence occ) {
		if (occ.getOccurrenceType() != NameOccurrence.OccurrenceType.DEF) {
			return;
		}
		if (occ.getDeclaration() == null || !(occ.getDeclaration() instanceof VariableNameDeclaration)) {
			return;
		}
		VariableNameDeclaration v = (VariableNameDeclaration) occ.getDeclaration();
		Hashtable<SimpleNode, NameOccurrence> occs = table.get(v);
		if (occs == null) {
			occs = new Hashtable<SimpleNode, NameOccurrence>();
			table.put(v, occs);
		}
		occs.put(occ.getLocation(), occ);
	}

	/** �ϲ����ﶨ��� */
	public void mergeLiveDefs(LiveDefsSet set) {
		Set<Map.Entry<VariableNameDeclaration, Hashtable<SimpleNode, NameOccurrence>>> entryset = set.getTable().entrySet();
		Iterator<Map.Entry<VariableNameDeclaration, Hashtable<SimpleNode, NameOccurrence>>> i = entryset.iterator();
		while (i.hasNext()) {
			Map.Entry<VariableNameDeclaration, Hashtable<SimpleNode, NameOccurrence>> entry = i.next();
			// VariableNameDeclaration v=entry.getKey();
			Hashtable<SimpleNode, NameOccurrence> occs = entry.getValue();
			for (Enumeration<NameOccurrence> e = occs.elements(); e.hasMoreElements();) {
				NameOccurrence o = e.nextElement();
				addLiveDef(o);
			}
		}
	}

	/** ��ñ����ĵ��ﶨ�� */
	public ArrayList<NameOccurrence> getVariableLiveDefs(VariableNameDeclaration v) {
		ArrayList<NameOccurrence> list = new ArrayList<NameOccurrence>();
		Hashtable<SimpleNode, NameOccurrence> occs = table.get(v);
		if (occs != null) {
			for (Enumeration<NameOccurrence> e = occs.elements(); e.hasMoreElements();) {
				NameOccurrence o = e.nextElement();
				list.add(o);
			}
		}
		return list;
	}
	
	/** ��ӡ */
	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		Set<Map.Entry<VariableNameDeclaration, Hashtable<SimpleNode, NameOccurrence>>> entryset = getTable().entrySet();
		Iterator<Map.Entry<VariableNameDeclaration, Hashtable<SimpleNode, NameOccurrence>>> i = entryset.iterator();
		while (i.hasNext()) {
			Map.Entry<VariableNameDeclaration, Hashtable<SimpleNode, NameOccurrence>> entry = i.next();
			VariableNameDeclaration v=entry.getKey();
			b.append("   "+v.getImage()+":");
			Hashtable<SimpleNode, NameOccurrence> occs = entry.getValue();
			for (Enumeration<NameOccurrence> e = occs.elements(); e.hasMoreElements();) {
				NameOccurrence o = e.nextElement();
				b.append(" ["+o.getLocation().getBeginLine()+","+o.getLocation().getBeginColumn()+"]");
			}
		}
		return b.toString();
	}
}
