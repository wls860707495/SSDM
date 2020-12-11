/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package softtest.symboltable.java;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
//added by xqing
import java.util.ArrayList;
import java.util.Map;
import softtest.domain.java.*;
import softtest.IntervalAnalysis.java.*;
import softtest.ast.java.ASTArrayInitializer;
import softtest.ast.java.ASTExpression;
import softtest.ast.java.ASTFieldDeclaration;
import softtest.ast.java.ASTLiteral;
import softtest.ast.java.ASTMethodDeclaration;
import softtest.ast.java.ASTMethodDeclarator;
import softtest.ast.java.ASTName;
import softtest.ast.java.ASTVariableDeclarator;
import softtest.ast.java.ASTVariableDeclaratorId;
import softtest.ast.java.ASTVariableInitializer;
import softtest.callgraph.java.*;
import softtest.ast.java.*;
import softtest.cfg.java.*;

public abstract class AbstractScope implements Scope {

	private Scope parent;

	//added by xqing
	/** ���������� */
	private List childrens = new ArrayList();

	/** ��ú��������� */
	public List getChildrens() {
		return childrens;
	}

	public Map getClassDeclarations() {
		return null;
	}

	//added by xqing
	public Map getVariableDeclarations() {
		return null;
	}

	public Map getMethodDeclarations() {
		return null;
	}

	public MethodScope getEnclosingMethodScope() {
		return parent.getEnclosingMethodScope();
	}

	public ClassScope getEnclosingClassScope() {
		return parent.getEnclosingClassScope();
	}

	public SourceFileScope getEnclosingSourceFileScope() {
		return parent.getEnclosingSourceFileScope();
	}

	public void setParent(Scope parent) {
		this.parent = parent;
		//added by xqing
		parent.getChildrens().add(this);
	}

	public Scope getParent() {
		return parent;
	}

	public void addDeclaration(MethodNameDeclaration methodDecl) {
		parent.addDeclaration(methodDecl);
	}

	public void addDeclaration(ClassNameDeclaration classDecl) {
		parent.addDeclaration(classDecl);
	}

	public boolean contains(NameOccurrence occurrence) {
		return findVariableHere(occurrence) != null;
	}

	protected abstract NameDeclaration findVariableHere(NameOccurrence occurrence);

	protected String glomNames(Iterator i) {
		StringBuffer result = new StringBuffer();
		while (i.hasNext()) {
			result.append(i.next().toString());
			result.append(',');
		}
		return result.length() == 0 ? "" : result.toString().substring(0, result.length() - 1);
	}
	
	//added by xqing
	public static int depth=0;
	public String dump(){
		return "";
	}
	public String print(){
		StringBuffer b=new StringBuffer();
		for(int i=0;i<depth;i++){
			b.append("  ");
		}
		b.append(dump()+"\n");
		depth++;
		
		List childrens = this.getChildrens();
		Iterator i = childrens.iterator();
		while (i.hasNext()) {
			AbstractScope s = (AbstractScope) i.next();
			b.append(s.print());
		}
		depth--;
		return b.toString();
	}

	//added by xqing
	/** ������������������������б��������� */
	public void resolveTypes(TypeSet typeset) {
		if(this instanceof ClassScope &&!((ClassScope)this).isAnonymousInnerClass()){
			TypeSet.pushClassName(((ClassScope)this).getClassName());
		}
		Map variableNames = null;
		variableNames = getVariableDeclarations();
		if (variableNames != null) {//�������쳣����ñ�����
			Iterator i = variableNames.keySet().iterator();
			while (i.hasNext()) {
				VariableNameDeclaration v = (VariableNameDeclaration) i.next();
				String typeimage = v.getTypeImage();
				Class type = null;
				try {
					int arrayDepth = v.getArrayDepth();
					StringBuffer sb = new StringBuffer("");
					if (arrayDepth > 0) {
						for (int index = 0; index < arrayDepth; index++) {
							sb.append("[");
						}
						if (typeimage.equals("boolean")) {
							sb.append("Z");
						} else if (typeimage.equals("byte")) {
							sb.append("B");
						} else if (typeimage.equals("char")) {
							sb.append("C");
						} else if (typeimage.equals("double")) {
							sb.append("D");
						} else if (typeimage.equals("float")) {
							sb.append("F");
						} else if (typeimage.equals("int")) {
							sb.append("I");
						} else if (typeimage.equals("long")) {
							sb.append("J");
						} else if (typeimage.equals("short")) {
							sb.append("S");
						} else {
							Class t = typeset.findClass(typeimage);
							sb.append("L" + t.getName() + ";");
						}
						typeimage = sb.toString();
					}

					type = typeset.findClass(typeimage);
					if (type != null) {
						//System.out.println("�ҵ�����"+v+"������:"+type);
					}
				} catch (ClassNotFoundException e) {

				}
				v.setType(type);
			}
		}

		//�ݹ鴦����������
		List childrens = this.getChildrens();
		Iterator i = childrens.iterator();
		while (i.hasNext()) {
			AbstractScope s = (AbstractScope) i.next();
			s.resolveTypes(typeset);
		}
		if(this instanceof ClassScope &&!((ClassScope)this).isAnonymousInnerClass()){
			TypeSet.popClassName();
		}
	}

	/** ���������ù�ϵ */
	public void resolveCallRelation(CGraph g) {
		Map methodNames = null;
		methodNames = getMethodDeclarations();
		if (methodNames != null) {//�������쳣����ú���
			Set entryset = methodNames.entrySet();
			Iterator i = entryset.iterator();
			while (i.hasNext()) {
				Map.Entry e = (Map.Entry) i.next();
				MethodNameDeclaration callee = (MethodNameDeclaration) e.getKey();
				CVexNode ncallee = callee.getCallGraphVex();
				if (ncallee == null) {
					//�����ڵ�
					String str = null;
					ClassScope cscope = getEnclosingClassScope();
					if (cscope != null) {
						str = cscope.getClassName() + "_" + callee.getImage() + "_" + callee.getParameterCount() + "_";
					} else {
						str = "noname" + "_" + callee.getImage() + "_" + callee.getParameterCount() + "_";
					}
					ncallee = g.addVex(str, callee);
				}
				List occs = (List) e.getValue();
				for (Object o : occs) {
					NameOccurrence occ = (NameOccurrence) o;
					MethodScope mscope = occ.getLocation().getScope().getEnclosingMethodScope();
					//ֻ����f()�����Σ�������i.f()���Ρ�
					if(occ.getLocation() instanceof ASTName){
						ASTName name=(ASTName)occ.getLocation();
						if(name.getImage()==null||name.getImage().contains(".")){
							continue;
						}
					}else if(occ.getLocation() instanceof ASTPrimarySuffix){
						ASTPrimarySuffix suffix=(ASTPrimarySuffix)occ.getLocation();
						if(!(suffix.getPrevSibling() instanceof ASTPrimaryPrefix)){
							continue;
						}
						ASTPrimaryPrefix prefix=(ASTPrimaryPrefix)suffix.getPrevSibling();
						if(!prefix.usesThisModifier()){
							continue;	
						}
						
					}else{
						continue;
					}
					
					if (mscope != null && mscope.getAstTreeNode() instanceof ASTMethodDeclaration) {
						//ֻ������ͨ�������������캯��
						ASTMethodDeclaration method = (ASTMethodDeclaration) mscope.getAstTreeNode();
						ASTMethodDeclarator declarator = (ASTMethodDeclarator) method.getFirstDirectChildOfType(ASTMethodDeclarator.class);
						MethodNameDeclaration caller = declarator.getMethodNameDeclaration();
						CVexNode ncaller = caller.getCallGraphVex();
						if (ncaller == null) {
							//�����ڵ�
							String str = mscope.getEnclosingClassScope().getClassName() + "_" + caller.getImage() + "_" + caller.getParameterCount() + "_";
							ncaller = g.addVex(str, caller);
						}

						if (!ncallee.isPreNode(ncaller)) {
							//��ӵ��ù�ϵ
							g.addEdge(ncaller, ncallee);
						}
					}
				}
			}
		}

		// �ݹ鴦����������
		List childrens = this.getChildrens();
		Iterator i = childrens.iterator();
		while (i.hasNext()) {
			AbstractScope s = (AbstractScope) i.next();
			s.resolveCallRelation(g);
		}
	}
	
	/**��ʼ���������ֵĶ���ʹ������*/
	public void initDefUse(){
		Map variableNames = null;
		variableNames = getVariableDeclarations();
		if (variableNames != null) {//�������쳣����ñ�����
			Iterator i = variableNames.entrySet().iterator();
			while (i.hasNext()) {
				Map.Entry e=(Map.Entry)i.next();
				//VariableNameDeclaration v = (VariableNameDeclaration) e.getKey();
				List occs = (List) e.getValue();
				for (Object o : occs) {
					NameOccurrence occ = (NameOccurrence) o;
					occ.setOccurrenceType(occ.checkOccurrenceType());
					VexNode vex=occ.getLocation().getCurrentVexNode();
					if(vex!=null){
						vex.getOccurrences().add(occ);
					}
				}
			}
		}
		
		//	�ݹ鴦����������
		List childrens = this.getChildrens();
		Iterator i = childrens.iterator();
		while (i.hasNext()) {
			AbstractScope s = (AbstractScope) i.next();
			s.initDefUse();
		}
	}

	/** ��ʼ���������� */
	public void initDomains() {
		Map variableNames = null;
		variableNames = getVariableDeclarations();
		if (variableNames != null) {//�������쳣����ñ�����
			Iterator i = variableNames.keySet().iterator();
			List<VariableNameDeclaration> templist=new ArrayList<VariableNameDeclaration> ();
			while (i.hasNext()) {
				VariableNameDeclaration v = (VariableNameDeclaration) i.next();
				templist.add(v);
			}
			Collections.sort(templist);
			i = templist.iterator();
			while (i.hasNext()) {
				VariableNameDeclaration v = (VariableNameDeclaration) i.next();
				Object domain = null;
				//�������Ա������ʼ��final int i=5;
				if (v.getAccessNodeParent() instanceof ASTFieldDeclaration) {
					ASTFieldDeclaration fielddecl = (ASTFieldDeclaration) v.getAccessNodeParent();
					ASTVariableDeclaratorId id = v.getDeclaratorId();
					if (id.jjtGetParent() instanceof ASTVariableDeclarator&&fielddecl.isFinal()) {
						ASTVariableDeclarator declarator = (ASTVariableDeclarator) id.jjtGetParent();
						if (declarator.jjtGetNumChildren() == 2 && declarator.jjtGetChild(1) instanceof ASTVariableInitializer) {
							ASTVariableInitializer init = (ASTVariableInitializer) declarator.jjtGetChild(1);
							if (init.getSingleChildofType(ASTLiteral.class) != null) {
								ExpressionDomainVisitor exprvisitor = new ExpressionDomainVisitor();
								DomainData exprdata = new DomainData();
								exprdata.sideeffect = false;
								init.jjtAccept(exprvisitor, exprdata);
								domain = exprdata.domain;
							}
						}
					}
				}
				
				if (v.isArray()) {
					if (v.getAccessNodeParent() instanceof ASTFieldDeclaration) {
						ASTVariableDeclaratorId id = v.getDeclaratorId();
						if (id.jjtGetParent() instanceof ASTVariableDeclarator) {
							ASTVariableDeclarator declarator = (ASTVariableDeclarator) id.jjtGetParent();
							if (declarator.jjtGetNumChildren() == 2 && declarator.jjtGetChild(1) instanceof ASTVariableInitializer) {
								ASTVariableInitializer init = (ASTVariableInitializer) declarator.jjtGetChild(1);
								ExpressionDomainVisitor exprvisitor = new ExpressionDomainVisitor();
								DomainData expdata = new DomainData();
								expdata.sideeffect = false;
								if (init.jjtGetChild(0) instanceof ASTExpression) {
									// �����int i=0������
									ASTExpression ex=(ASTExpression)init.jjtGetChild(0);
									ex.jjtAccept(exprvisitor, expdata);
									Object rightdomain = expdata.domain;

									expdata.domain = ConvertDomain.DomainSwitch(rightdomain, ClassType.ARRAY);
									expdata.type = ClassType.ARRAY;

									domain = expdata.domain;
								} else if (init.jjtGetChild(0) instanceof ASTArrayInitializer) {
									// ���������ʼ��
									ASTArrayInitializer arraynode = (ASTArrayInitializer) init.jjtGetChild(0);
									arraynode.calDims();
									ArrayList<Integer> dims = arraynode.getdims();
									ArrayDomain adomain = new ArrayDomain(dims.size());
									for (int j = 0; j < dims.size(); j++) {
										adomain.setDimension(j, new IntegerDomain(dims.get(j),dims.get(j),false,false));
									}
									expdata.domain = adomain;
									expdata.type = ClassType.ARRAY;
									domain=expdata.domain;	
								} 
							}
						}
					}
					if(domain==null){
						domain = new ArrayDomain(v.getArrayDepth());
					}else{
						domain = ConvertDomain.DomainSwitch(domain, ClassType.ARRAY);
					}
				} else if (v.isPrimitiveType()) {
					String typeimage = v.getTypeImage();
					if (typeimage.equals("boolean")) {
						if (domain == null) {
							domain = BooleanDomain.getUnknownDomain();
						} else {
							domain = ConvertDomain.DomainSwitch(domain, ClassType.BOOLEAN);
						}
					} else if (typeimage.equals("byte")) {
						if (domain == null) {
							domain = IntegerDomain.getUnknownDomain();
						} else {
							domain = ConvertDomain.DomainSwitch(domain, ClassType.INT);
						}
					} else if (typeimage.equals("char")) {
						if (domain == null) {
							domain = IntegerDomain.getUnknownDomain();
						} else {
							domain = ConvertDomain.DomainSwitch(domain, ClassType.INT);
						}
					} else if (typeimage.equals("double")) {
						if (domain == null) {
							domain =  DoubleDomain.getUnknownDomain();
						} else {
							domain = ConvertDomain.DomainSwitch(domain, ClassType.DOUBLE);
						}
					} else if (typeimage.equals("float")) {
						if (domain == null) {
							domain = DoubleDomain.getUnknownDomain();
						} else {
							domain = ConvertDomain.DomainSwitch(domain, ClassType.DOUBLE);
						}
					} else if (typeimage.equals("int")) {
						if (domain == null) {
							domain =IntegerDomain.getUnknownDomain();
						} else {
							domain = ConvertDomain.DomainSwitch(domain, ClassType.INT);
						}
					} else if (typeimage.equals("long")) {
						if (domain == null) {
							domain = IntegerDomain.getUnknownDomain();
						} else {
							domain = ConvertDomain.DomainSwitch(domain, ClassType.INT);
						}
					} else if (typeimage.equals("short")) {
						if (domain == null) {
							domain = IntegerDomain.getUnknownDomain();
						} else {
							domain = ConvertDomain.DomainSwitch(domain, ClassType.INT);
						}
					} else {
						throw new RuntimeException("Don't know how to get the domain of this primitivetype");
					}
				} else {
					if (domain == null) {
						domain = ReferenceDomain.getUnknownDomain();
					} else {
						domain = ConvertDomain.DomainSwitch(domain, ClassType.REF);
					}
				}
				v.setDomain(domain);
			}
		}

		//�ݹ鴦����������
		List childrens = this.getChildrens();
		Iterator i = childrens.iterator();
		while (i.hasNext()) {
			AbstractScope s = (AbstractScope) i.next();
			s.initDomains();
		}
	}
	public boolean isSelfOrAncestor(Scope ancestor) {
		Scope parent = this;
		while (parent != null) {
			if (parent == ancestor) {
				return true;
			}
			parent = parent.getParent();
		}
		return false;
	}
	
	/** ���������ĳ��֣��ڷ��ű�����ʱ���ҵ��ĺ������������ǲ���ȷ�ģ���Ҫ�ȵ����ʽ���ʹ��������������*/
	/*public void fixMethodOccurence(){
		Map methodNames = null;
		methodNames = getMethodDeclarations();
		if (methodNames != null) {//�������쳣����ú���
			String classname=getEnclosingClassScope().getClassName();
			TypeSet typeset=TypeSet.getCurrentTypeSet();
			Class classtype= typeset.findClassWithoutEx(classname);
			if(classtype==null){
				return;
			}
			Hashtable<Method,MethodNameDeclaration> table=new Hashtable<Method,MethodNameDeclaration>();
			Set entryset = methodNames.entrySet();
			Iterator i = entryset.iterator();
			while (i.hasNext()) {
				Map.Entry e = (Map.Entry) i.next();
				MethodNameDeclaration decl = (MethodNameDeclaration) e.getKey();
				Class[] parmtypes=decl.getParameterClasses();
				String methodname=decl.getImage();
				Method decltype=getMethodOfClass(methodname, parmtypes, classtype);
				if(decltype!=null){
					table.put(decltype, decl);
				}
			}
			
			List<NameOccurrence> toadd =new ArrayList<NameOccurrence>();
			
			entryset = methodNames.entrySet();
			i = entryset.iterator();
			while (i.hasNext()) {
				Map.Entry e = (Map.Entry) i.next();
				MethodNameDeclaration decl = (MethodNameDeclaration) e.getKey();
				Class[] parmtypes=decl.getParameterClasses();
				String methodname=decl.getImage();
				Method decltype=getMethodOfClass(methodname, parmtypes, classtype);
				if(decltype==null){
					continue;
				}
				List<NameOccurrence> todelete =new ArrayList<NameOccurrence>();
				List occs = (List) e.getValue();
				for (Object o : occs) {
					NameOccurrence occ = (NameOccurrence) o;
					if(occ.getLocation() instanceof ASTName){
						ASTName namenode=(ASTName)occ.getLocation();
						parmtypes=namenode.getParameterTypes();
						methodname=namenode.getImage();
						Method realtype=getMethodOfClass(methodname, parmtypes, classtype);
						if(realtype==null||realtype==decltype){
							continue;
						}
						MethodNameDeclaration realdecl=table.get(realtype);
						occ.setDeclaration(realdecl);
						namenode.getNameDeclarationList().remove(decl);
						namenode.setNameDeclaration(realdecl);
						toadd.add(occ);
						todelete.add(occ);
					}else if(occ.getLocation() instanceof ASTPrimarySuffix){
						ASTPrimarySuffix suffix=(ASTPrimarySuffix)occ.getLocation();
						parmtypes=suffix.getParameterTypes();
						methodname=suffix.getImage();
						Method realtype=getMethodOfClass(methodname, parmtypes, classtype);
						if(realtype==null||realtype==decltype){
							continue;
						}
						MethodNameDeclaration realdecl=table.get(realtype);
						occ.setDeclaration(realdecl);
						suffix.setNameDeclaration(realdecl);
						toadd.add(occ);
						todelete.add(occ);
					}
				}
				occs.remove(todelete);
			}
			
			for(NameOccurrence occ:toadd){
				MethodNameDeclaration decl=(MethodNameDeclaration)occ.getDeclaration();
				List<NameOccurrence> occs=(List<NameOccurrence>)methodNames.get(decl);
				Iterator<NameOccurrence> itor=occs.iterator();
				int pos=0;
				while(itor.hasNext()){
					NameOccurrence cur=itor.next();
					if(cur.getLocation().getBeginLine()>occ.getLocation().getBeginLine()||
						(cur.getLocation().getBeginLine()==occ.getLocation().getBeginLine()
						&&cur.getLocation().getBeginColumn()>occ.getLocation().getBeginColumn())){
						break;
					}
					pos++;
				}
				occs.add(pos, occ);
			}
		}

		// �ݹ鴦����������
		List childrens = this.getChildrens();
		Iterator i = childrens.iterator();
		while (i.hasNext()) {
			AbstractScope s = (AbstractScope) i.next();
			s.fixMethodOccurence();
		}
	}

	private Method getMethodOfClass(String name,Class []parameterTypes,Class type){
		Method ret = null;
		Class temp=type;
		while (type != null && ret == null) {
			try {
				ret = type.getDeclaredMethod(name,parameterTypes);
			} catch (NoSuchMethodException e) {
			}
			type=type.getSuperclass();
		}
		if(ret==null){
			//û�ҵ������Բ���ģ��ƥ��
			type=temp;
			ArrayList<Method> same_n=new ArrayList();
			ArrayList<Method> same_n_p=new ArrayList();
			int pramnum=parameterTypes==null?0:parameterTypes.length;
			while (type != null ) {
				Method[] ms= type.getDeclaredMethods();
				for(Method m:ms){
					if(m.getName().equals(name)){
						same_n.add(m);
						if(m.getParameterTypes().length==pramnum){
							same_n_p.add(m);
						}
					}
				}
				type=type.getSuperclass();
			}
			for(Method m:same_n_p){
				Class[] cs=m.getParameterTypes();
				boolean match=true;
				for(int i=0;i<pramnum;i++){
					Class c1=parameterTypes[i];
					Class c2=cs[i];
					if(c1!=null){
						if(!isSelfOrSuperClass(c1,c2)){
							match=false;
							break;
						}
					}else{
						if(c2.isPrimitive()){
							match=false;
							break;
						}
					}
				}
				if(match){
					ret=m;
					break;
				}
			}
			//��û�ҵ������ȡһ������һ����
			if(ret==null){
				if(same_n_p.size()>0){
					ret=same_n_p.get(0);
				}else if(same_n.size()>0){
					ret=same_n.get(0);
				}
			}
		}
		
		return ret;
	}
	
	private boolean isSelfOrSuperClass(Class child,Class c){
		boolean ret=false;
		while(!ret&&child!=null){
			if(child==c){
				ret=true;
			}
			child=child.getSuperclass();
		}
		return ret;
	}*/
}
