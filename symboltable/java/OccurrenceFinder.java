package softtest.symboltable.java;

import softtest.ast.java.*;
import java.util.*;

import softtest.ast.java.ASTClassOrInterfaceType;
import softtest.ast.java.ASTMethodDeclaration;
import softtest.ast.java.ASTPrimaryExpression;
import softtest.ast.java.ASTResultType;
import softtest.ast.java.JavaParserVisitorAdapter;

public class OccurrenceFinder extends JavaParserVisitorAdapter {

    @Override
	public Object visit(ASTPrimaryExpression node, Object data) {
        NameFinder nameFinder = new NameFinder(node);

        // Maybe do some sort of State pattern thingy for when NameDeclaration
        // is null/not null?
        NameDeclaration decl = null;

        List names = nameFinder.getNames();
        for (Iterator i = names.iterator(); i.hasNext();) {
        	// BUGFOUND 20090408
        	
            NameOccurrence occ = (NameOccurrence) i.next();
            Search search = new Search(occ);
            if (decl == null) {
                // doing the first name lookup
                search.execute();
                decl = search.getResult();

                if (decl == null) {
                    // we can't find it, so just give up
                    // when we decide to do full symbol resolution
                    // force this to either find a symbol or throw a SymbolNotFoundException                	
                    break;
                	// why break??
                }
            } else {
                //added by xqing
                if((decl instanceof VariableNameDeclaration)||(decl instanceof MethodNameDeclaration)){
                	//����Ǳ����򷽷�����������ͣ�Ȼ��ȡ�����͵�����,Ȼ������ȷ�����������в���
                	if(decl instanceof VariableNameDeclaration){
                		VariableNameDeclaration v=(VariableNameDeclaration)decl;                		
                		String typeimage=v.getTypeImage();
                		Search t = new Search(typeimage);
                		t.execute(decl.getScope());
                		if(t.getResult() instanceof ClassNameDeclaration){
                			decl=t.getResult();
                			search.execute(decl.getScope());
                        	decl = search.getResult();
                		}else{
                			//edit by xqing 2009-03-10
                			//�Ҳ������������ټ���
                			break;
                		}
                	}else{
                		MethodNameDeclaration m=(MethodNameDeclaration)decl;
                		String typeimage=null;
                		ASTMethodDeclaration method=(ASTMethodDeclaration)m.getMethodNameDeclaratorNode().jjtGetParent();
                		ASTResultType resulttype=(ASTResultType)method.getFirstDirectChildOfType(ASTResultType.class);
                		if(resulttype!=null){
                			ASTClassOrInterfaceType classtype=(ASTClassOrInterfaceType)resulttype.getSingleChildofType(ASTClassOrInterfaceType.class);
                			if(classtype!=null){
                				typeimage=classtype.getImage();
                			}
                		}
                		Search t = new Search(typeimage);
                		t.execute(decl.getScope());
                		if(t.getResult() instanceof ClassNameDeclaration){
                			decl=t.getResult();
                			search.execute(decl.getScope());
                        	decl = search.getResult();
                		}else{
                			//�Ҳ������������ټ��� edit by xqing 2009-03-10
                			break;
                		}
                	}                	
                }else{
                	//���ǰһ�������������������������������������
                	search.execute(decl.getScope());
                	decl = search.getResult();
                }
            }
        }
        return super.visit(node, data);
    }

}
