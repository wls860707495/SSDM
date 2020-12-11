package softtest.cfg.java;

import java.util.*;

import softtest.ast.java.*;
import softtest.symboltable.java.TypeSet;

/** �������ɿ�����ͼ�ĳ����﷨�������� */
public class ControlFlowVisitor extends JavaParserVisitorAdapter {
	 
	/** �ж��﷨���ڵ�parent�Ƿ�Ϊ��һ���﷨��child�ڵ������ */
	public static boolean isParent(SimpleJavaNode child, SimpleJavaNode parent) {
		boolean bret = false;
		SimpleJavaNode parentNode = (SimpleJavaNode) child.jjtGetParent();
		while (parentNode != null) {
			if (parentNode == parent) {
				bret = true;
				break;
			}
			parentNode = (SimpleJavaNode) parentNode.jjtGetParent();
		}
		return bret;
	}

	/** �����캯�� */
	@Override
	public Object visit(ASTConstructorDeclaration treenode, Object data) {
		ControlFlowData flowdata = new ControlFlowData();

		flowdata.graph = new Graph();
		treenode.setGraph(flowdata.graph);

		// ��Ӻ�������
		SimpleJavaNode simplejavanode = (SimpleJavaNode) treenode.jjtGetParent().jjtGetParent().jjtGetParent();
		String name = simplejavanode.getImage();

		// ����һ������try����
		TryData trydata = new TryData();
		trydata.name="fun";
		flowdata.trystack.push(trydata);

		flowdata.vexnode = flowdata.graph.addVex("func_head_" + name + "_", treenode);
		for (int i = 1; i < treenode.jjtGetNumChildren(); i++) {
			JavaNode javanode = (JavaNode) treenode.jjtGetChild(i);
			javanode.jjtAccept(this, flowdata);
		}

		VexNode in = flowdata.vexnode;
		VexNode eout=null;
		
		//�������е�throw
		trydata = flowdata.trystack.pop();
		if (trydata.thrownodes.size() > 0) {
			// ���Ӻ����쳣����
			eout = flowdata.graph.addVex("func_eout_" + name + "_", treenode);
			ListIterator<VexNode> i = trydata.thrownodes.listIterator();
			while (i.hasNext()) {
				VexNode node1 = i.next();
				if (node1 != null) {
					flowdata.graph.addEdgeWithFlag(node1, eout,true);
				}
			}
		}
		
		VexNode out = flowdata.graph.addVex("func_out_" + name + "_", treenode);
		if(eout!=null){
			flowdata.graph.addEdgeWithFlag(eout, out,false);
		}
		if (in != null) {
			flowdata.graph.addEdgeWithFlag(in, out,false);
		}

		// ������ת���,return Ҳ��������ת��
		LabelData labeldata = flowdata.labeltable.get("return");
		if (labeldata == null) {
			labeldata = new LabelData();
			flowdata.labeltable.put("return", labeldata);
		}
		labeldata.labelnode = out;

		for (Enumeration<LabelData> e = flowdata.labeltable.elements(); e.hasMoreElements();) {
			labeldata = e.nextElement();
			ListIterator<VexNode> i = labeldata.jumpnodes.listIterator();
			while (i.hasNext()) {
				VexNode node1 = i.next();
				VexNode node2 = labeldata.labelnode;
				if (node1 != null && node2 != null) {
					flowdata.graph.addEdgeWithFlag(node1, node2,false);
				}
			}
		}

		return null;
	}

	/** ������ͨ�ĳ�Ա���� */
	@Override
	public Object visit(ASTMethodDeclaration treenode, Object data) {
		if(!(treenode.jjtGetChild(treenode.jjtGetNumChildren() - 1) instanceof ASTBlock)){
			return null;
		}
		ASTBlock javanode = (ASTBlock) (JavaNode) treenode.jjtGetChild(treenode.jjtGetNumChildren() - 1);
		ControlFlowData flowdata = new ControlFlowData();

		flowdata.graph = new Graph();
		treenode.setGraph(flowdata.graph);
		if (javanode != null) {
			// ��Ӻ�������
			SimpleJavaNode simplejavanode = (SimpleJavaNode) treenode.jjtGetChild(1);
			String name = simplejavanode.getImage();

			// ����һ������try����
			TryData trydata = new TryData();
			trydata.name="fun";
			flowdata.trystack.push(trydata);

			flowdata.vexnode = flowdata.graph.addVex("func_head_" + name + "_", treenode);
			javanode.jjtAccept(this, flowdata);
			VexNode in = flowdata.vexnode;
			VexNode eout=null;
			
			//�������е�throw
			trydata = flowdata.trystack.pop();
			if (trydata.thrownodes.size() > 0) {
				// ���Ӻ����쳣����
				eout = flowdata.graph.addVex("func_eout_" + name + "_", treenode);
				ListIterator<VexNode> i = trydata.thrownodes.listIterator();
				while (i.hasNext()) {
					VexNode node1 = i.next();
					if (node1 != null) {
						flowdata.graph.addEdgeWithFlag(node1, eout,true);
					}
				}
				
			}
			
			VexNode out = flowdata.graph.addVex("func_out_" + name + "_", treenode);
			if(eout!=null){
				flowdata.graph.addEdgeWithFlag(eout, out,false);
			}
			if (in != null) {
				flowdata.graph.addEdgeWithFlag(in, out,false);
			}

			// ������ת���,return Ҳ��������ת��
			LabelData labeldata = flowdata.labeltable.get("return");
			if (labeldata == null) {
				labeldata = new LabelData();
				flowdata.labeltable.put("return", labeldata);
			}
			labeldata.labelnode = out;

			for (Enumeration<LabelData> e = flowdata.labeltable.elements(); e.hasMoreElements();) {
				labeldata = e.nextElement();
				ListIterator<VexNode> i = labeldata.jumpnodes.listIterator();
				while (i.hasNext()) {
					VexNode node1 = i.next();
					VexNode node2 = labeldata.labelnode;
					if (node1 != null && node2 != null) {
						flowdata.graph.addEdgeWithFlag(node1, node2,false);
					}
				}
			}
		}
		return null;
	}

	/** �������� */
	@Override
	public Object visit(ASTBlock treenode, Object data) {
		for (int i = 0; i < treenode.jjtGetNumChildren(); i++) {
			JavaNode javanode = (JavaNode) treenode.jjtGetChild(i);
			javanode.jjtAccept(this, data);
		}
		return null;
	}

	/** ���������е���� */
	@Override
	public Object visit(ASTBlockStatement treenode, Object data) {
		JavaNode javanode = (JavaNode) treenode.jjtGetChild(0);
		javanode.jjtAccept(this, data);
		return null;
	}

	/** ������䣬��ο��﷨����java.jjt�ļ� */
	@Override
	public Object visit(ASTStatement treenode, Object data) {
		JavaNode javanode = (JavaNode) treenode.jjtGetChild(0);
		javanode.jjtAccept(this, data);
		return null;
	}

	/** �������� */
	@Override
	public Object visit(ASTEmptyStatement treenode, Object data) {
		ControlFlowData flowdata = (ControlFlowData) data;
		Graph graph = flowdata.graph;
		VexNode in = flowdata.vexnode;
		VexNode out = graph.addVex("empty_", treenode);
		if (in != null) {
			graph.addEdgeWithFlag(in, out,false);
		}
		flowdata.vexnode = out;
		return null;
	}

	/** ����for����г�ʼ���͸�������п��ܻ���ֵ�����б���ο��﷨����java.jjt�ļ� */
	@Override
	public Object visit(ASTStatementExpressionList treenode, Object data) {
		for (int i = 0; i < treenode.jjtGetNumChildren(); i++) {
			JavaNode javanode = (JavaNode) treenode.jjtGetChild(i);
			javanode.jjtAccept(this, data);
		}
		return null;
	}

	/** ������ʽ��� */
	@Override
	public Object visit(ASTStatementExpression treenode, Object data) {
		ControlFlowData flowdata = (ControlFlowData) data;
		Graph graph = flowdata.graph;
		VexNode in = flowdata.vexnode;
		VexNode out = graph.addVex("stmt_", treenode);
			
		if (in != null) {
			graph.addEdgeWithFlag(in, out,false);
		}
		
		//������ʽ�쳣
		addImplicitException(treenode,flowdata,out);
		
		flowdata.vexnode = out;
		return null;
	}

	/** ����if��� */
	@Override
	public Object visit(ASTIfStatement treenode, Object data) {
		VexNode head, out, f_branch, t_branch;
		ControlFlowData flowdata = (ControlFlowData) data;
		VexNode in = flowdata.vexnode;
		Graph g = flowdata.graph;

		// ����if����ͷ���head
		head = g.addVex("if_head_", treenode);
		
		if (in != null) {
			g.addEdgeWithFlag(in, head,false);
		}
		
		//������ʽ�쳣
		addImplicitException(treenode,flowdata,head);

		// ��ͷ�ڵ�Ϊ��ڣ��������֧��䴦��
		head.truetag = true;
		flowdata.vexnode = head;
		JavaNode javanode = (JavaNode) treenode.jjtGetChild(1);// ���֧
		javanode.jjtAccept(this, flowdata);
		t_branch = flowdata.vexnode;
		
		//���֧Ϊ�����
		if(head==t_branch){
			head.truetag = false;
		}

		// ��ͷ�ڵ�Ϊ��ڣ����üٷ�֧��䴦��
		head.falsetag = true;
		if (treenode.jjtGetNumChildren() > 2) {
			// ����2˵����else��֧
			flowdata.vexnode = head;
			javanode = (JavaNode) treenode.jjtGetChild(2);
			javanode.jjtAccept(this, flowdata);
			f_branch = flowdata.vexnode;
		} else {
			f_branch = head;
		}

		// ���ǳ��ڽ��Ĳ�ͬ���
		if (t_branch == null && f_branch == null) {
			// ��ٷ�֧���Ѿ�ת�����ˣ�����Ҫ����if������
			out = null;
		} else {
			// �������ڣ�������
			out = g.addVex("if_out_", treenode);
			//���֧Ϊ�����
			if(head==t_branch){
				head.truetag = true;
			}
			if (t_branch != null) {
				g.addEdgeWithFlag(t_branch, out,false);
			}
			if (f_branch != null) {
				g.addEdgeWithFlag(f_branch, out,false);
			}
		}
		flowdata.vexnode = out;
		return null;
	}

	/** ����while��� */
	@Override
	public Object visit(ASTWhileStatement treenode, Object data) {
		VexNode head, out, subout;
		ControlFlowData flowdata = (ControlFlowData) data;
		VexNode in = flowdata.vexnode;
		Graph g = flowdata.graph;

		//������ǰѭ�������ṹ
		LoopData loopdata = new LoopData();
		loopdata.name = "while";

		// ����while����ͷ���head
		head = g.addVex("while_head_", treenode);
		loopdata.head = head;
		if (in != null) {
			g.addEdgeWithFlag(in, head,false);
		}

		// ��ǰѭ����ջ
		flowdata.loopstack.push(loopdata);
		
		//������ʽ�쳣
		addImplicitException(treenode,flowdata,head);

		// ��ͷ�ڵ�Ϊ��ڣ���������䴦��
		head.truetag = true;
		flowdata.vexnode = head;
		JavaNode javanode = (JavaNode) (treenode).jjtGetChild(1);
		javanode.jjtAccept(this, flowdata);
		subout = flowdata.vexnode;
		if (subout != null) {
			g.addEdgeWithFlag(subout, head,false);
		}

		// ����while�����ڽڵ�
		out = g.addVex("while_out_", treenode);
		head.falsetag = true;
		g.addEdgeWithFlag(head, out,false);

		// ��ǰѭ����ջ
		loopdata = flowdata.loopstack.pop();

		// ����ǰѭ����break
		ListIterator<VexNode> i = loopdata.breaknodes.listIterator();
		while (i.hasNext()) {
			VexNode node1 = i.next();
			if (node1 != null) {
				flowdata.graph.addEdgeWithFlag(node1, out,false);
			}
		}

		// ����ǰѭ����continue
		i = loopdata.continuenodes.listIterator();
		while (i.hasNext()) {
			VexNode node1 = i.next();
			if (node1 != null) {
				flowdata.graph.addEdgeWithFlag(node1, head,false);
			}
		}
		flowdata.vexnode = out;
		return null;
	}

	/** ����������� */
	@Override
	public Object visit(ASTLocalVariableDeclaration treenode, Object data) {
		ControlFlowData flowdata = (ControlFlowData) data;
		Graph graph = flowdata.graph;
		VexNode in = flowdata.vexnode;
		VexNode out = graph.addVex("decl_stmt_", treenode);
		if (in != null) {
			graph.addEdgeWithFlag(in, out,false);
		}
		
		//������ʽ�쳣
		addImplicitException(treenode,flowdata,out);
		
		flowdata.vexnode = out;
		return null;
	}

	/** ����break��� */
	@Override
	public Object visit(ASTBreakStatement treenode, Object data) {
		ControlFlowData flowdata = (ControlFlowData) data;
		Graph graph = flowdata.graph;
		VexNode in = flowdata.vexnode;
		VexNode out = graph.addVex("break_", treenode);
		if (in != null) {
			graph.addEdgeWithFlag(in, out,false);
		}
		// �������Ѿ�ת�����ˣ�������Ϊnull
		flowdata.vexnode = null;

		if (treenode.getImage() == null) {
			// break ���ޱ��
			LoopData loopdata = flowdata.loopstack.peek();
			loopdata.breaknodes.add(out);
		} else {
			// break ���б��
			String name = treenode.getImage();
			name = "#" + name;
			LabelData labeldata = flowdata.labeltable.get(name);
			if (labeldata == null) {
				labeldata = new LabelData();
				flowdata.labeltable.put(name, labeldata);
			}
			labeldata.jumpnodes.add(out);
		}
		return null;
	}

	/** ����continue��� */
	@Override
	public Object visit(ASTContinueStatement treenode, Object data) {
		ControlFlowData flowdata = (ControlFlowData) data;
		Graph graph = flowdata.graph;
		VexNode in = flowdata.vexnode;
		VexNode out = graph.addVex("continue_", treenode);
		if (in != null) {
			graph.addEdgeWithFlag(in, out,false);
		}
		// �������Ѿ�ת�����ˣ�������Ϊnull
		flowdata.vexnode = null;

		if (treenode.getImage() == null) {
			// continue ���ޱ��
			LoopData loopdata = null;
			// ���ҷ�switch��ѭ��
			ListIterator<LoopData> i = flowdata.loopstack.listIterator(flowdata.loopstack.size());
			while (i.hasPrevious()) {
				loopdata = i.previous();
				if (loopdata.name != "switch") {
					break;
				}
			}
			loopdata.continuenodes.add(out);
		} else {
			// continue ���б��
			String name = treenode.getImage();
			LabelData labeldata = flowdata.labeltable.get(name);
			if (labeldata == null) {
				labeldata = new LabelData();
				flowdata.labeltable.put(name, labeldata);
			}
			labeldata.jumpnodes.add(out);
		}
		return null;
	}

	/** ����switch��� */
	@Override
	public Object visit(ASTSwitchStatement treenode, Object data) {
		ControlFlowData flowdata = (ControlFlowData) data;
		VexNode head, out, subout;
		VexNode in = flowdata.vexnode;
		Graph g = flowdata.graph;

		LoopData loopdata = new LoopData();
		loopdata.name = "switch";

		// ����switch����ͷ���head
		head = g.addVex("switch_head_", treenode);
		loopdata.head = head;

		if (in != null) {
			g.addEdgeWithFlag(in, head,false);
		}
		// ��ǰѭ����ջ��switch��ѭ��ͳһ����
		flowdata.loopstack.push(loopdata);
		
		//������ʽ�쳣
		addImplicitException(treenode,flowdata,head);

		// ��nullΪ��ڣ���������䴦��
		flowdata.vexnode = null;
		for (int i = 1; i < treenode.jjtGetNumChildren(); i++) {
			JavaNode javanode = (JavaNode) treenode.jjtGetChild(i);
			javanode.jjtAccept(this, flowdata);
		}
		subout = flowdata.vexnode;

		// �������ڽڵ�
		out = g.addVex("switch_out_", treenode);
		if (subout != null) {
			g.addEdgeWithFlag(subout, out,false);
		}

		// ��ǰѭ����ջ
		loopdata = flowdata.loopstack.pop();

		// ���û��default�Ӿ䣬����һ����head��out�ı�
		if (!loopdata.hasdefault) {
			g.addEdgeWithFlag(head, out,false);
		}

		// ����break
		ListIterator<VexNode> i = loopdata.breaknodes.listIterator();
		while (i.hasNext()) {
			VexNode node1 = i.next();
			if (node1 != null) {
				flowdata.graph.addEdgeWithFlag(node1, out,false);
			}
		}
		flowdata.vexnode = out;
		return null;
	}

	/** ����case,default��� */
	@Override
	public Object visit(ASTSwitchLabel treenode, Object data) {
		ControlFlowData flowdata = (ControlFlowData) data;
		Graph graph = flowdata.graph;
		VexNode in = flowdata.vexnode;
		String name;
		if (treenode.isDefault()) {
			name = "default_";
		} else {
			name = "case_";
		}
		VexNode out = graph.addVex(name, treenode);

		// ���ӵ�ǰswitchͷ��case
		LoopData loopdata = flowdata.loopstack.peek();
		VexNode switchnode = loopdata.head;
		graph.addEdgeWithFlag(switchnode, out,false);

		// ����default��־
		if (treenode.isDefault()) {
			loopdata.hasdefault = true;
		}

		if (in != null) {
			graph.addEdgeWithFlag(in, out,false);
		}
		flowdata.vexnode = out;
		return null;
	}

	/** ����do-while��� */
	@Override
	public Object visit(ASTDoStatement treenode, Object data) {
		ControlFlowData flowdata = (ControlFlowData) data;
		VexNode head, out1, out2, subout;
		VexNode in = flowdata.vexnode;
		Graph g = flowdata.graph;

		LoopData loopdata = new LoopData();
		loopdata.name = "do-while";
		// ����do-while����ͷ���head
		head = g.addVex("do_while_head_", treenode);
		loopdata.head = head;

		if (in != null) {
			g.addEdgeWithFlag(in, head,false);
		}

		// ��ǰѭ����ջ
		flowdata.loopstack.push(loopdata);

		// ��ͷ�ڵ�Ϊ��ڣ���������䴦��
		flowdata.vexnode = head;
		JavaNode javanode = (JavaNode) (treenode).jjtGetChild(0);
		javanode.jjtAccept(this, flowdata);
		subout = flowdata.vexnode;
		out1 = g.addVex("do_while_out1_", (SimpleJavaNode) treenode.jjtGetChild(1));// ���ڽڵ�
		out2 = g.addVex("do_while_out2_", treenode);// ���ճ��ڽڵ�
		if (subout != null) {
			g.addEdgeWithFlag(subout, out1,false);
		}
		out1.truetag = true;
		g.addEdgeWithFlag(out1, head,false);
		out1.falsetag = true;
		g.addEdgeWithFlag(out1, out2,false);
		
		//������ʽ�쳣
		addImplicitException((SimpleJavaNode) treenode.jjtGetChild(1),flowdata,out1);

		// ��ǰѭ����ջ
		loopdata = flowdata.loopstack.pop();

		// ����break
		ListIterator<VexNode> i = loopdata.breaknodes.listIterator();
		while (i.hasNext()) {
			VexNode node1 = i.next();
			if (node1 != null) {
				flowdata.graph.addEdgeWithFlag(node1, out2,false);
			}
		}

		// ����continue
		i = loopdata.continuenodes.listIterator();
		while (i.hasNext()) {
			VexNode node1 = i.next();
			if (node1 != null) {
				flowdata.graph.addEdgeWithFlag(node1, head,false);
			}
		}
		flowdata.vexnode = out2;
		return null;
	}

	/** ����for��� */
	@Override
	public Object visit(ASTForStatement treenode, Object data) {
		// ����for-each
		
		ControlFlowData flowdata = (ControlFlowData) data;
		VexNode head=null, init=null, add=null, out=null, subout=null;
		VexNode in = flowdata.vexnode;
		Graph g = flowdata.graph;

		LoopData loopdata = new LoopData();
		loopdata.name = "for";

		// ����for���ĳ�ʼ���ڵ�
		List results = treenode.findDirectChildOfType(ASTForInit.class);
		if (!results.isEmpty()) {
			init = g.addVex("for_init_", (SimpleJavaNode) results.get(0));
			if (in != null) {
				g.addEdgeWithFlag(in, init,false);
			}
			
			//������ʽ�쳣
			addImplicitException((SimpleJavaNode) results.get(0),flowdata,init);
		} else {
			init = in;
		}
		// ����for����ͷ���head
		head = g.addVex("for_head_", treenode);
		if (init != null) {
			g.addEdgeWithFlag(init, head,false);
		}
		loopdata.head = head;
		
		//������ʽ�쳣
		addImplicitException(treenode,flowdata,head);

		// ��ǰѭ����ջ
		flowdata.loopstack.push(loopdata);

		// ��ͷ�ڵ�Ϊ��ڣ���������䴦��
		head.truetag = true;
		flowdata.vexnode = head;
		// for ���ĳ�ʼ���������������Ӿ䶼����û��
		JavaNode javanode = (JavaNode) (treenode).jjtGetChild(treenode.jjtGetNumChildren() - 1);
		javanode.jjtAccept(this, flowdata);
		subout = flowdata.vexnode;// ��������

		results = treenode.findDirectChildOfType(ASTForUpdate.class);
		if (!results.isEmpty()) {
			add = g.addVex("for_inc_", (SimpleJavaNode) results.get(0));// ����for�����������
			if (subout != null) {
				g.addEdgeWithFlag(subout, add,false);
			}
			//������ʽ�쳣
			addImplicitException((SimpleJavaNode) results.get(0),flowdata,add);
		} else {
			add = subout;
		}
		if (add != null) {
			g.addEdgeWithFlag(add, head,false);
		}

		// ����for���ĳ��ڽڵ�
		out = g.addVex("for_out_", treenode);
		head.falsetag = true;
		g.addEdgeWithFlag(head, out,false);

		// ��ǰѭ����ջ
		loopdata = flowdata.loopstack.pop();
		// ����break
		ListIterator<VexNode> i = loopdata.breaknodes.listIterator();
		while (i.hasNext()) {
			VexNode node1 = i.next();
			if (node1 != null) {
				flowdata.graph.addEdgeWithFlag(node1, out,false);
			}
		}

		// ����continue
		i = loopdata.continuenodes.listIterator();
		while (i.hasNext()) {
			VexNode node1 = i.next();
			if (node1 != null) {
				if(add!=null){
					flowdata.graph.addEdgeWithFlag(node1, add,false);// continue��add�ڵ�
				}
				else{
					flowdata.graph.addEdgeWithFlag(node1, head,false);
				}
			}
		}
		flowdata.vexnode = out;
		return null;
	}

	/** ��������� */
	@Override
	public Object visit(ASTLabeledStatement treenode, Object data) {
		ControlFlowData flowdata = (ControlFlowData) data;
		Graph g = flowdata.graph;
		VexNode in = flowdata.vexnode;

		// ����ͷ�ڵ�
		String name = treenode.getImage();
		VexNode head = g.addVex("lable_head_" + name + "_", treenode);
		if (in != null) {
			g.addEdgeWithFlag(in, head,false);
		}

		// ��ͷ�ڵ�Ϊ��ڣ���������䴦��
		flowdata.vexnode = head;
		JavaNode javanode = (JavaNode) (treenode).jjtGetChild(0);
		javanode.jjtAccept(this, flowdata);
		
		// ����һ��������ĳ��ڽڵ㣬���ڴ���java��break ��block������
		VexNode out = g.addVex("lable_out_" + name + "_", treenode);
		if (flowdata.vexnode != null) {
			g.addEdgeWithFlag(flowdata.vexnode, out,false);
		}
		flowdata.vexnode = out;

		// ��ת��������д��Žڵ�
		// String name=treenode.getImage();
		LabelData labeldata = flowdata.labeltable.get(name);
		if (labeldata == null) {
			labeldata = new LabelData();
			flowdata.labeltable.put(name, labeldata);
		}
		labeldata.labelnode = head;

		// ���break�Ĵ���
		name = "#" + name;
		labeldata = flowdata.labeltable.get(name);
		if (labeldata == null) {
			labeldata = new LabelData();
			flowdata.labeltable.put(name, labeldata);
		}		
		labeldata.labelnode = out;
		
		// ����java��˵�˴��Ѿ����Դ������б��ת����
		name = treenode.getImage();
		labeldata = flowdata.labeltable.get(name);
		if (labeldata != null) {
			ListIterator<VexNode> i = labeldata.jumpnodes.listIterator();
			while (i.hasNext()) {
				VexNode node1 = i.next();
				VexNode node2 = labeldata.labelnode;
				if (node1 != null && node2 != null) {
					flowdata.graph.addEdgeWithFlag(node1, node2,false);
				}
			}
		}
		flowdata.labeltable.remove(name);

		name = "#" + name;
		labeldata = flowdata.labeltable.get(name);
		if (labeldata != null) {
			ListIterator<VexNode> i = labeldata.jumpnodes.listIterator();
			while (i.hasNext()) {
				VexNode node1 = i.next();
				VexNode node2 = labeldata.labelnode;
				if (node1 != null && node2 != null) {
					flowdata.graph.addEdgeWithFlag(node1, node2,false);
				}
			}
		}
		flowdata.labeltable.remove(name);
		return null;
	}

	/** ����return��� */
	@Override
	public Object visit(ASTReturnStatement treenode, Object data) {
		ControlFlowData flowdata = (ControlFlowData) data;
		Graph graph = flowdata.graph;
		VexNode in = flowdata.vexnode;

		VexNode out = graph.addVex("return_", treenode);
		if (in != null) {
			graph.addEdgeWithFlag(in, out,false);
		}
		
		//������ʽ�쳣
		addImplicitException(treenode,flowdata,out);
		
		// ��ӽڵ㵽��ǰtry��throw�б�
		/*
		if ( flowdata.trystack != null && !flowdata.trystack.empty()) {
			List css = treenode.getParentsOfType(ASTCatchStatement.class);
			if (css != null && css.size() > 0) {
				TryData trydata = flowdata.trystack.peek();
				// trydata.thrownodes.add(out);
				trydata.catchouts.add(out);
			} else {
				css = treenode.getParentsOfType(ASTFinallyStatement.class);
				if (css != null && css.size() > 0) {
					TryData trydata = flowdata.trystack.peek();
					// trydata.thrownodes.add(out);
					trydata.catchouts.add(out);
				}
			}
		}*/

		// ��ת��������Ӧλ�ü���ת���ڵ�
		LabelData labeldata = flowdata.labeltable.get("return");
		if (labeldata == null) {
			labeldata = new LabelData();
			flowdata.labeltable.put("return", labeldata);
		}
		labeldata.jumpnodes.add(out);

		// �������Ѿ�ת������
		flowdata.vexnode = null;
		return null;
	}
	
	/**
	 * �ж�a�Ƿ�Ϊb������
	 * @param a
	 * @param b
	 * @return
	 */
	private boolean isSelfOrSuperClass(Class a, Class b) {
		do {
			if (a.equals(b)) {
				return true;
			}
			a = a.getSuperclass();
		}
		while (a != null) ;
		return false;
	}
	
	/**
	 * try-catch-finally������ͼ
	 * ��Ϊÿ�������׳��쳣�Ŀ�����ͼ�ڵ�����Ӧ�Ĳ����쳣��catchͷ�������һ����
	 * δ��������쳣�㴫�ݸ��ϲ�trydata�ṹ���ߺ�����eout
	 * 
	 * yangxiu
	 * 20090504
	 * 
	 * ����2��RL�������71,72
	 */
	public Object visit(ASTTryStatement treenode, Object data) {
		ControlFlowData flowdata = (ControlFlowData) data;
		Graph graph = flowdata.graph;
		VexNode in = flowdata.vexnode;

		TryData trydata = new TryData();
		trydata.name = "try";

		// ����try����ͷ���head
		VexNode head = graph.addVex("try_head_", treenode);
		trydata.head = head;
		if (in != null) {
			graph.addEdgeWithFlag(in, head,false);
		}

		// ��ǰtry��ջ
		flowdata.trystack.push(trydata);

		// ��ͷ�ڵ�Ϊ��ڣ���������䴦��
		flowdata.vexnode = head;
		JavaNode javanode = (JavaNode) (treenode).jjtGetChild(0);
		javanode.jjtAccept(this, flowdata);
		trydata.out = flowdata.vexnode;
		
		// ����try{...}�ڵ��쳣��
		List<VexNode> throwsInTry = new ArrayList<VexNode>(trydata.thrownodes);
		List<Class> exceptionInTry = new ArrayList<Class>(trydata.exceptions);
		trydata.thrownodes.clear();
		trydata.exceptions.clear();
		
		// ����catch{...}�Ŀ�����ͼ
		// ÿ��catchͷ��㶼������ǰ���ڵ�
		// ������trydata.catchheads��
		for (int i = 0; i < treenode.jjtGetNumChildren(); i++) {
			if (treenode.jjtGetChild(i) instanceof ASTCatchStatement) {
				flowdata.vexnode = null;
				javanode = (JavaNode) (treenode).jjtGetChild(i);
				javanode.jjtAccept(this, flowdata);
				flowdata.vexnode = null;
			}
		}
		
		// ��������catch�ڵ���쳣��
		List<VexNode> throwsInCatch = new ArrayList<VexNode>(trydata.thrownodes);
		List<Class> exceptionInCatch = new ArrayList<Class>(trydata.exceptions);
		trydata.thrownodes.clear();
		trydata.exceptions.clear();
		
		// �ϲ�try-catch�ṹ
		TryData trydataup = flowdata.trystack.get(flowdata.trystack.size()-2);
		
		if (!treenode.hasFinally()) {
			// ������throw�ڵ����ӵ���Ӧ��catch�ڵ�
			// throwsInTryɾ����������쳣������δ��������쳣
			List<Edge> elist = new ArrayList<Edge>();
			for (VexNode cat : trydata.catchheads) {
				ASTCatchStatement cs = (ASTCatchStatement) cat.getTreeNode();
				ASTClassOrInterfaceType except = (ASTClassOrInterfaceType) cs.getFirstChildOfType(ASTClassOrInterfaceType.class);
				Class eclass = TypeSet.getCurrentTypeSet().findClassWithoutEx(except.getImage());
				if (eclass == null) {
					continue;
				}
				
				List<VexNode> throwsInTry1 = new ArrayList<VexNode>();
				List<Class> exceptionInTry1 = new ArrayList<Class>();
				for (int i = 0 ; i < throwsInTry.size() ; ++i) {
					if (exceptionInTry.get(i) != null && isSelfOrSuperClass(exceptionInTry.get(i), eclass)) {
						if (throwsInTry.get(i).getEdgeByHead(cat) == null) {
							flowdata.graph.addEdgeWithFlag(throwsInTry.get(i), cat,true);
						}
					}
					else {
						throwsInTry1.add(throwsInTry.get(i));
						exceptionInTry1.add(exceptionInTry.get(i));
					}
				}
				throwsInTry = throwsInTry1;
				exceptionInTry = exceptionInTry1;
			}
			
		} else {// ��finally,javanodeΪfinally���﷨���ڵ�			
			javanode = treenode.getFinally();
			
			// try�ĳ��ڽڵ㸴��finally
			if (trydata.out != null) {
				flowdata.vexnode = trydata.out;
				javanode.jjtAccept(this, flowdata);
				trydata.out = flowdata.vexnode;
			}
			
			if (trydata.catchheads.size() > 0) {// ��catch�ڵ�
				// ������throw�ڵ����ӵ���Ӧ��catch�ڵ�
				// throwsInTryɾ����������쳣������δ��������쳣
				for (VexNode cat : trydata.catchheads) {
					ASTCatchStatement cs = (ASTCatchStatement) cat.getTreeNode();
					ASTClassOrInterfaceType except = (ASTClassOrInterfaceType) cs.getFirstChildOfType(ASTClassOrInterfaceType.class);
					Class eclass = TypeSet.getCurrentTypeSet().findClassWithoutEx(except.getImage());
					if (eclass == null) {
						continue;
					}
					
					List<VexNode> throwsInTry1 = new ArrayList<VexNode>();
					List<Class> exceptionInTry1 = new ArrayList<Class>();
					for (int i = 0 ; i < throwsInTry.size() ; ++i) {
						if (exceptionInTry.get(i) != null && isSelfOrSuperClass(exceptionInTry.get(i), eclass)) {
							if (throwsInTry.get(i).getEdgeByHead(cat) == null) {
								flowdata.graph.addEdgeWithFlag(throwsInTry.get(i), cat,true);
							}
						}
						else {
							throwsInTry1.add(throwsInTry.get(i));
							exceptionInTry1.add(exceptionInTry.get(i));
						}
					}
					throwsInTry = throwsInTry1;
					exceptionInTry = exceptionInTry1;
				}
				
				// Ϊcatch�Ӿ��е��쳣����finally
				for (int index = 0; index < throwsInCatch.size(); index++) {
					flowdata.vexnode = throwsInCatch.get(index);
					javanode.jjtAccept(this, flowdata);
					// ��������thrownodes
					throwsInCatch.set(index, flowdata.vexnode);
				}

				// Ϊÿ��catch�Ӿ���������ڸ���finally
				for (int index = 0; index < trydata.catchouts.size(); index++) {
					flowdata.vexnode = trydata.catchouts.get(index);
					javanode.jjtAccept(this, flowdata);
					// ��������catchout
					trydata.catchouts.set(index, flowdata.vexnode);
				}
			}
			
			// ΪTry��ÿ��δ��������쳣�㸴��finally
			for (int index = 0; index < throwsInTry.size(); index++) {
				flowdata.vexnode = throwsInTry.get(index);
				javanode.jjtAccept(this, flowdata);
				// ��������thrownodes
				throwsInTry.set(index, flowdata.vexnode);
			}

			// Ϊÿ��ת������finally
			SimpleJavaNode trytreenode = treenode;
			SimpleJavaNode finallytreenode = (SimpleJavaNode) javanode;
			if (flowdata.loopstack.size() > 0) {
				LoopData loopdata = flowdata.loopstack.peek();
				// �޸�break
				for (int index = 0; index < loopdata.breaknodes.size(); index++) {
					VexNode node1 = loopdata.breaknodes.get(index);
					if (isParent(node1.treenode, trytreenode) && !isParent(node1.treenode, finallytreenode)) {
						flowdata.vexnode = node1;
						javanode.jjtAccept(this, flowdata);
						// �޸�breakת��
						loopdata.breaknodes.set(index, flowdata.vexnode);
					}
				}
				// �޸�continue
				for (int index = 0; index < loopdata.continuenodes.size(); index++) {
					VexNode node1 = loopdata.continuenodes.get(index);
					if (isParent(node1.treenode, trytreenode) && !isParent(node1.treenode, finallytreenode)) {
						flowdata.vexnode = node1;
						javanode.jjtAccept(this, flowdata);
						// �޸�continueת��
						loopdata.continuenodes.set(index, flowdata.vexnode);
					}
				}
			}

			// �޸�ת��
			for (Enumeration<LabelData> e = flowdata.labeltable.elements(); e.hasMoreElements();) {
				LabelData labeldata = e.nextElement();
				for (int index = 0; index < labeldata.jumpnodes.size(); index++) {
					VexNode node1 = labeldata.jumpnodes.get(index);
					if (isParent(node1.treenode, trytreenode) && !isParent(node1.treenode, finallytreenode)) {
						flowdata.vexnode = node1;
						javanode.jjtAccept(this, flowdata);
						// �޸�ת��
						labeldata.jumpnodes.set(index, flowdata.vexnode);
					}
				}
			}
		}
		
		// try��δ��������쳣�����ϲ�try
		trydataup.thrownodes.addAll(throwsInTry);
		trydataup.exceptions.addAll(exceptionInTry);
		
		// catch�е��쳣���ݸ��ϲ�try
		trydataup.thrownodes.addAll(throwsInCatch);
		trydataup.exceptions.addAll(exceptionInCatch);
		
		// finally�׳����쳣
		// ����finally��trydata.thrownodes�ǿ�����
		trydataup.thrownodes.addAll(trydata.thrownodes);
		trydataup.exceptions.addAll(trydata.exceptions);
		
		boolean needtryout = false;
		// �ж��Ƿ���Ҫtryout�ڵ�
		if (trydata.out != null) {
			needtryout = true;
		}
		ListIterator<VexNode> i = trydata.catchouts.listIterator();
		while (i.hasNext() && !needtryout) {
			VexNode node1 = i.next();
			if (node1 != null) {
				needtryout = true;
				break;
			}
		}

		if (needtryout) {
			// ����try��������out�ڵ�
			VexNode out = graph.addVex("try_out_", treenode);
			// �����������ڵ�out
			if (trydata.out != null) {
				flowdata.graph.addEdgeWithFlag(trydata.out, out,false);
			}
			// �������е�catch���ڽڵ㵽out
			i = trydata.catchouts.listIterator();
			while (i.hasNext()) {
				VexNode node1 = i.next();
				if (node1 != null) {
					flowdata.graph.addEdgeWithFlag(node1, out,false);
				}
			}
			flowdata.vexnode = out;
		} else {
			flowdata.vexnode = null;
		}
		
		//��ǰtry��ջ
		trydata = flowdata.trystack.pop();
		return null;
	}

	/** ����catch��� */
	@Override
	public Object visit(ASTCatchStatement treenode, Object data) {
		ControlFlowData flowdata = (ControlFlowData) data;
		Graph graph = flowdata.graph;
		VexNode in = flowdata.vexnode;

		VexNode head = graph.addVex("catch_head_", treenode);
		if (in != null) {
			graph.addEdgeWithFlag(in, head,false);
		}
		// ���ͷ�ڵ㵽��ǰtry��catchͷ�б�
		TryData trydata = flowdata.trystack.peek();
		trydata.catchheads.add(head);

		// ��ͷΪ��ڽڵ�����Ӿ䴦��
		head.truetag = true;
		flowdata.vexnode = head;
		JavaNode javanode = (JavaNode) (treenode).jjtGetChild(1);
		javanode.jjtAccept(this, flowdata);

		// ����catch���ڵ���ǰtry
		if (flowdata.vexnode != null) {
			trydata.catchouts.add(flowdata.vexnode);
		}

		// ����catchͷΪ����
		head.falsetag = true;
		flowdata.vexnode = head;
		return null;
	}

	/** ����throw��� */
	@Override
	public Object visit(ASTThrowStatement treenode, Object data) {
		ControlFlowData flowdata = (ControlFlowData) data;
		Graph graph = flowdata.graph;
		VexNode in = flowdata.vexnode;

		VexNode head = graph.addVex("throw_", treenode);
		if (in != null) {
			graph.addEdgeWithFlag(in, head,false);
		}
		// ��ӽڵ㵽��ǰtry��throw�б�
		TryData trydata = flowdata.trystack.peek();
		trydata.thrownodes.add(head);
		trydata.exceptions.add((Class)((ExpressionBase)treenode.jjtGetChild(0)).getType());
		//trydata.catchouts.add(head);

		// ����nullΪ����
		flowdata.vexnode = null;
		return null;
	}

	/** ����finally��� */
	@Override
	public Object visit(ASTFinallyStatement treenode, Object data) {
		ControlFlowData flowdata = (ControlFlowData) data;
		Graph graph = flowdata.graph;
		VexNode in = flowdata.vexnode;

		VexNode head = graph.addVex("Finally_", treenode);
		if (in != null) {
			graph.addEdgeWithFlag(in, head,false);
		}
		TryData trydata = flowdata.trystack.peek();
		trydata.finallyhead = head;
		// ��ͷΪ��ڽڵ�����Ӿ䴦��
		flowdata.vexnode = head;
		JavaNode javanode = (JavaNode) (treenode).jjtGetChild(0);
		javanode.jjtAccept(this, flowdata);
		return null;
	}

	/** ����assert��� */
	@Override
	public Object visit(ASTAssertStatement treenode, Object data) {
		ControlFlowData flowdata = (ControlFlowData) data;
		Graph graph = flowdata.graph;
		VexNode in = flowdata.vexnode;
		VexNode out = graph.addVex("Assert_", treenode);
		if (in != null) {
			graph.addEdgeWithFlag(in, out,false);
		}
		
		//������ʽ�쳣
		addImplicitException(treenode,flowdata,out);		
		
		flowdata.vexnode = out;
		return null;
	}

	/** ����synchronized��� */
	@Override
	public Object visit(ASTSynchronizedStatement treenode, Object data) {
		ControlFlowData flowdata = (ControlFlowData) data;
		Graph graph = flowdata.graph;
		VexNode in = flowdata.vexnode;
		VexNode out = graph.addVex("Synchronized_", treenode);
		if (in != null) {
			graph.addEdgeWithFlag(in, out,false);
		}
		//������ʽ�쳣
		addImplicitException(treenode,flowdata,out);		
		
		// ��outΪ��ڽڵ�����Ӿ䴦��
		flowdata.vexnode = out;
		JavaNode javanode = (JavaNode) (treenode).jjtGetChild(1);
		javanode.jjtAccept(this, flowdata);
		return null;
	}
	
	/** ��������̬������ */
	@Override
	public Object visit(ASTInitializer treenode,Object data){
		return null;
	}
	
	private void addImplicitException(SimpleJavaNode treenode ,ControlFlowData flowdata,VexNode vexnode){
		SimpleJavaNode connode=(SimpleJavaNode)treenode.getConcreteNode();
		ImplicitExceptionFinder exfinder=new ImplicitExceptionFinder();
		if(connode!=null&&!(connode instanceof ASTConstructorDeclaration)&&!(connode instanceof ASTMethodDeclaration)){
			connode.jjtAccept(exfinder, null);	
			if(exfinder.isHasImplicitException()){
				TryData trdata=flowdata.trystack.peek();
				if(trdata!=null){
					for (Class ec : exfinder.getExceptions()) {
						trdata.thrownodes.add(vexnode);
						trdata.exceptions.add(ec);
					}
				}
				vexnode.addExceptions(exfinder.getExceptions());
			}
		}
	}
}
