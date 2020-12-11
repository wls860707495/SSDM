package softtest.DefUseAnalysis.java;

import java.io.*;

import softtest.ast.java.*;
import softtest.symboltable.java.*;
import softtest.ast.java.ASTCompilationUnit;
import softtest.ast.java.JavaCharStream;
import softtest.ast.java.JavaParser;
import softtest.cfg.java.*;
import softtest.symboltable.java.SymbolFacade;


public class TestDefUseAnalysis {
	public static void main(String args[])throws IOException, ClassNotFoundException{
		String parsefilename="test.java";

		//���������﷨��
		System.out.println("���ɳ����﷨��...");
		JavaParser parser = new JavaParser(new JavaCharStream(new FileInputStream(parsefilename)));
		parser.setJDK15();
		ASTCompilationUnit astroot = parser.CompilationUnit();
		
		//����������ͼ
		System.out.println("���ɿ�����ͼ...");
		astroot.jjtAccept(new ControlFlowVisitor(), null);
		
		//�������ű�
		System.out.println("���ɷ��ű�...");
		new SymbolFacade().initializeWith(astroot);
		
		//������ʹ��
		System.out.println("���ɶ���ʹ����...");
		astroot.jjtAccept(new DUAnaysisVistor() , null);
		
		
		System.out.println("�������.");
	}
}
