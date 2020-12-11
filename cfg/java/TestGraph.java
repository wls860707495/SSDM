package softtest.cfg.java;

import softtest.ast.java.*;
//import softtest.symboltable.java.*;
import java.io.*;

import softtest.ast.java.ASTCompilationUnit;
import softtest.ast.java.ASTConstructorDeclaration;
import softtest.ast.java.ASTMethodDeclaration;
import softtest.ast.java.JavaCharStream;
import softtest.ast.java.JavaParser;
import softtest.ast.java.JavaParserVisitorAdapter;
import softtest.ast.java.SimpleJavaNode;
import softtest.symboltable.java.ExpressionTypeFinder;
import softtest.symboltable.java.OccurrenceFinder;
import softtest.symboltable.java.PakageAndImportVisitor;
import softtest.symboltable.java.ScopeAndDeclarationFinder;
import softtest.symboltable.java.TypeSet;

class PrintGraphVisitor extends JavaParserVisitorAdapter {
	/** �������캯���Ŀ������ϵ��������� */
	@Override
	public Object visit(ASTConstructorDeclaration treenode, Object data) {
		Graph g = treenode.getGraph();
		if(g==null){
			return null;
		}
		SimpleJavaNode simplejavanode = (SimpleJavaNode) treenode.jjtGetParent().jjtGetParent().jjtGetParent();
		String name = "test_temp\\" + simplejavanode.getImage();
		g.accept(new DumpEdgesVisitor(), name + ".dot");
		System.out.println("������ͼ��������ļ�" + name + ".dot");
		try {
			java.lang.Runtime.getRuntime().exec("dot -Tjpg -o " + name + ".jpg " + name + ".dot").waitFor();
		} catch (IOException e1) {
			System.out.println(e1);
		} catch (InterruptedException e2) {
			System.out.println(e2);
		}
		System.out.println("������ͼ��ӡ�����ļ�" + name + ".jpg");
		return null;
	}

	/** ������ͨ��Ա�����Ŀ������ϵ��������� */
	@Override
	public Object visit(ASTMethodDeclaration treenode, Object data) {
		Graph g = treenode.getGraph();
		if(g==null){
			return null;
		}
		// ������ļ���������
		SimpleJavaNode simplejavanode = (SimpleJavaNode) treenode.jjtGetChild(1);
		String name = "test_temp\\" + simplejavanode.getImage();
		g.accept(new DumpEdgesVisitor(), name + ".dot");
		System.out.println("������ͼ��������ļ�" + name + ".dot");
		try {
			java.lang.Runtime.getRuntime().exec("dot -Tjpg -o " + name + ".jpg " + name + ".dot").waitFor();
		} catch (IOException e1) {
			System.out.println(e1);
		} catch (InterruptedException e2) {
			System.out.println(e2);
		}
		System.out.println("������ͼ��ӡ�����ļ�" + name + ".jpg");
		return null;
	}
}
public class TestGraph {
	public static void main(String args[]) throws IOException, ClassNotFoundException {
		String parsefilename="test_temp\\test.java";
	
		//������ļ�
		//DTSJavaCompiler compiler = new DTSJavaCompiler(null,null, null);
		//boolean b=compiler.compileProject("temp", "temp");
		//if(!b){
		//	DTSJavaCompiler.printCompileInfo(compiler.getDiagnostics());
		//}	
		
		//���������﷨��
		System.out.println("���ɳ����﷨��...");
		JavaParser parser = new JavaParser(new JavaCharStream(new FileInputStream(parsefilename)));
		parser.setJDK15();
		ASTCompilationUnit astroot = parser.CompilationUnit();
		
		//�������ű�
		//new SymbolFacade().initializeWith(astroot);
		ScopeAndDeclarationFinder sc = new ScopeAndDeclarationFinder();
		astroot.jjtAccept(sc, null);	
		
		//����������Ϣ
		//System.out.println("��������...");
		new TypeSet("temp");
		astroot.jjtAccept(new PakageAndImportVisitor(), TypeSet.getCurrentTypeSet());
		astroot.getScope().resolveTypes(TypeSet.getCurrentTypeSet());
		astroot.jjtAccept(new ExpressionTypeFinder(), TypeSet.getCurrentTypeSet());
		
		//������ֺ���������
		OccurrenceFinder of = new OccurrenceFinder();
		astroot.jjtAccept(of, null);
		
		//����������ͼ
		astroot.jjtAccept(new ControlFlowVisitor(), null);
		
		astroot.jjtAccept(new PrintGraphVisitor(), null);

	}
}