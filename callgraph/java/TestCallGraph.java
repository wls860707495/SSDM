package softtest.callgraph.java;

import java.io.*;
import softtest.ast.java.*;
import softtest.symboltable.java.*;
import java.util.*;

import softtest.ast.java.ASTCompilationUnit;
import softtest.ast.java.JavaCharStream;
import softtest.ast.java.JavaParser;
import softtest.symboltable.java.SymbolFacade;


public class TestCallGraph {
	public static void main(String args[]) throws IOException{
		// ���������﷨��
		System.out.println("���������﷨��...");
		JavaParser parser = new JavaParser(new JavaCharStream(new FileInputStream("test.java")));
		parser.setJDK15();
		ASTCompilationUnit astroot = parser.CompilationUnit();

		// �������ű�
		System.out.println("���ɷ��ű�...");
		new SymbolFacade().initializeWith(astroot);

		CGraph g = new CGraph();
		astroot.getScope().resolveCallRelation(g);
		
		List<CVexNode> list=g.getTopologicalOrderList();
		Collections.reverse(list);
		System.out.println("���ù�ϵ��������");
		for(CVexNode n:list){
			System.out.print(n.getName()+"  ");
		}
		System.out.println();
		
		String name ="c:\\test";
		g.accept(new DumpCGraphVisitor(), name + ".dot");
		System.out.println("����ͼͼ��������ļ�" + name + ".dot");
		try {
			java.lang.Runtime.getRuntime().exec("dot -Tjpg -o " + name + ".jpg " + name + ".dot").waitFor();
		} catch (IOException e1) {
			System.out.println(e1);
		} catch (InterruptedException e2) {
			System.out.println(e2);
		}
		System.out.println("����ͼ��ӡ�����ļ�" + name + ".jpg");
	}
}
