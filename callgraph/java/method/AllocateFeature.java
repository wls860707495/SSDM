package softtest.callgraph.java.method;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.jaxen.JaxenException;

import softtest.ast.java.ASTAllocationExpression;
import softtest.ast.java.ASTAssignmentOperator;
import softtest.ast.java.ASTClassOrInterfaceType;
import softtest.ast.java.ASTConstructorDeclaration;
import softtest.ast.java.ASTExpression;
import softtest.ast.java.ASTMethodDeclaration;
import softtest.ast.java.ASTName;
import softtest.ast.java.ASTPrimaryExpression;
import softtest.ast.java.ASTPrimaryPrefix;
import softtest.ast.java.ASTPrimarySuffix;
import softtest.ast.java.ASTResultType;
import softtest.ast.java.ASTReturnStatement;
import softtest.ast.java.ASTType;
import softtest.ast.java.ASTVariableDeclarator;
import softtest.ast.java.ASTVariableDeclaratorId;
import softtest.ast.java.ASTVariableInitializer;
import softtest.ast.java.ExpressionBase;
import softtest.ast.java.JavaParserVisitorAdapter;
import softtest.ast.java.SimpleJavaNode;
import softtest.cfg.java.VexNode;
import softtest.fsmanalysis.java.ProjectAnalysis;
import softtest.symboltable.java.LocalScope;
import softtest.symboltable.java.NameOccurrence;
import softtest.symboltable.java.VariableNameDeclaration;

/**
 * AllocateFeature��������һ�������Ƿ������Դ�����������
 * �������������������֣�
 * 1. ��������ֵ���·������Դ;
 * 2. �޸ķ�����������ĳ�Ա���ԡ�
 * 
 * @author ����
 *
 */
public class AllocateFeature extends AbstractFeature{	
	/**
	 * ������Ա�����ķ���ֵ�Ƿ�Ϊ�·������Դ
	 */
	private boolean isAllocateFunction = false;
	
	/**
	 * ��������������Դ�����trace��Ϣ
	 */
	private List<String> traceinfo = null;
	
	public List<String> getTraceInfo() {
		return traceinfo;
	}
	
	/**
	 * ������Ա�����ķ���ֵ�Ƿ�Ϊ�·������Դ
	 * @return this.isAllocateFunction
	 */
	public boolean isAllocateFunction() {
		return this.isAllocateFunction;
	}
	
	private String current_func = null;
	
	@Override
	public void listen(SimpleJavaNode node, FeatureSet set) {
		if (node instanceof ASTMethodDeclaration) {
			current_func = ((ASTMethodDeclaration) node).getMethodName();
		} else if (node instanceof ASTConstructorDeclaration) {
			current_func = ((ASTConstructorDeclaration) node).getMethodName();
		}
		
		AllocateFeatureVisitor vsitor = new AllocateFeatureVisitor();
		node.jjtAccept(vsitor, null);
		if (isAllocateFunction) {
			set.addFeature(this);
		}
	}
	
	// ������Դ�ķ���
	private static String RES_STRINGS4[] = { "getConnection",
			"createStatement", "executeQuery", "getResultSet",
			"prepareStatement", "prepareCall", "accept","open",
			"openStream","getChannel" };
	private static String regex4 = null;
	
	// ����RES_STRINGS4��Ӧ��������ʽ
	static {
		StringBuffer sb = new StringBuffer();
		sb.append("^(");
		for (String s : RES_STRINGS4) {
			sb.append("(.+\\." + s + ")|");
		}
		if (RES_STRINGS4.length > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append(")$");
		regex4 = sb.toString();
	}
	
	// ��Դʵ��
	private static String RES_STRINGS[] = { "FileOutputStream",
			"PipedOutputStream", "FileWriter", "PipedWriter",
			"FileInputStream", "PipedInputStream", "FileReader",
			"RandomAccessFile", "ZipOutputStream", "GZIPOutputStream",
			"PrintWriter", "AudioInputStream", "GZIPInputStream",
			"ZipInputStream", "FileInputStream", "PipedInputStream",
			"PipedReader", "ZipFile", "JNLPRandomAccessFile", "Connection",
			"ResultSet", "Statement", "PooledConnection", "MidiDevice",
			"Receiver", "Transmitter", "DatagramSocketImpl", "ServerSocket",
			"Socket", "SocketImpl", "JMXConnector", "RMIConnection",
			"RMIConnectionImpl", "RMIConnectionImpl_Stub", "RMIConnector",
			"RMIServerImpl", "ConsoleHandler", "FileHandler", "Handler",
			"MemoryHandler", "SocketHandler", "StreamHandler", "Scanner",
			"StartTlsResponse", "PreparedStatement","ImageInputStream",
			"InitialLdapContext","LdapContext","DatagramChannel","URL", 
			"InputStream","InitialDirContext","DirContext","ReadableByteChannel","OutputStream","DirectoryDialog","MessageBox",
			};
	//added by yang"DataInputStream"
	private static HashSet res_strings = new HashSet<String>(Arrays.asList(RES_STRINGS));
	
	/**
	 * �����߱�֤expr���ӽڵ�����xpath: 
	 * ./PrimaryExpression[not(./PrimarySuffix)]/PrimaryPrefix/AllocationExpression[./ClassOrInterfaceType]
	 * 
	 * ��� new XXXX () �Ƿ���з�����Դ����
	 *
	 * @param expr ASTExpression�ӽڵ���һ��AllocationExpression
	 * @return �Ƿ���з�����Դ����
	 */
	private boolean checkNewAllocExpression(ASTExpression expr) {
		if (expr == null) {
			return false;
		}
		
		List aeList = expr.findXpath(".//AllocationExpression[./ClassOrInterfaceType]");
		for (Object obj : aeList) {
			ASTAllocationExpression ae = (ASTAllocationExpression) obj;
			ASTClassOrInterfaceType coit = (ASTClassOrInterfaceType)(ae.jjtGetChild(0));
			if (coit.getImage() == null || coit.getImage().trim().length() == 0) {
				continue;
			}
			if (res_strings.contains(coit.getImage())) {
				// ȷ�����з�����Դ���������Է���
				List<String> newlist = new ArrayList<String>();
				if(softtest.config.java.Config.LANGUAGE==0){
					newlist.add("�ļ�:"+ProjectAnalysis.getCurrent_file()+" ��:"+expr.getBeginLine()+" ����:"+current_func);
				}else{
					newlist.add("file:"+ProjectAnalysis.getCurrent_file()+" line:"+expr.getBeginLine()+" Method:"+current_func);
				}
				
				traceinfo = newlist;
				return true;
			}
		}
		return false;
	}
	
	/**	 
	 * �����߱�֤expr���ӽڵ�����xpath: 
	 * ./PrimaryExpression[count(PrimarySuffix)>=1 and count(PrimaryPrefix)=1 and ./PrimaryPrefix/Name and ./PrimarySuffix/Arguments]
	 * 
	 * ��麯������ֵ�Ƿ������Դ��������
	 * 
	 * 1. �麯��ժҪ���Ƿ�Ϊ������Դ����
	 * 2. �������Դ�������Ƿ�Ϊ������Դ����
	 * 3. ��node.getType()���÷����Ƿ��return��Դ����
	 *
	 * ���磺
	 * x.getConnection()
	 * or getConnection()
	 * or a.b.getA().c.getConnection()
	 * 
	 * @param expr ASTExpression����x.y(z)�ĺ�������
	 * @return �Ƿ���з�����Դ����
	 */
	private boolean checkFuncCallExpression(ASTExpression expr) {
		if (expr == null || expr.jjtGetNumChildren()!=1 || expr.jjtGetChild(0).jjtGetNumChildren()<2) {
			return false;
		}
		
		// ��ȡtype
		ASTPrimaryExpression pe = (ASTPrimaryExpression)expr.jjtGetChild(0);
		Object pr = pe.jjtGetChild(pe.jjtGetNumChildren()-2);
		Object type = null;
		if ( (pr instanceof ASTPrimaryPrefix) || (pr instanceof ASTPrimarySuffix) ) {
			type = ((ExpressionBase)pr).getType();
		} else {
			return false;
		}
		
		// ��ȡ������
		String methodName = null;
		if ( (pr instanceof ASTPrimaryPrefix) && (((ASTPrimaryPrefix)pr).jjtGetChild(0) instanceof ASTName) ) {
			methodName = ((ASTName)((ASTPrimaryPrefix)pr).jjtGetChild(0)).getImage();
		}
		else if ( (pr instanceof ASTPrimarySuffix) && (((ASTPrimarySuffix)pr).jjtGetNumChildren() == 0)) {
			methodName = ((ASTPrimarySuffix)pr).getImage();
		}
		else {
			// δ֪�쳣�����Ĭ�Ϸ���false
			return false;
		}
		if (methodName == null || methodName.trim().length() == 0) {
			return false;
		}
		
		// 1. �麯��ժҪ���Ƿ�Ϊ������Դ����
		MethodNode methodnode=null;
		if (type != null && (type instanceof Method)) {
			methodnode=MethodNode.findMethodNode(type);
			if(methodnode != null){
				MethodSummary summary = methodnode.getMethodsummary();
				
				// �õ�����ժҪ����ѯ�Ƿ���з�����Դ����
				if (summary != null) {
					for (AbstractFeature ff : summary.getFeatrues().getTable().values()) {
						if (!(ff instanceof AllocateFeature)) {
							continue;
						}
						
						if (((AllocateFeature)ff).isAllocateFunction()) {
							List<String> newlist = new ArrayList<String>(((AllocateFeature)ff).getTraceInfo());
							/*
							String name = methodName;
							if (type instanceof Method) {
								name = ((Method) type).toGenericString();
							} else if (type instanceof Constructor) {
								name = ((Constructor) type).toGenericString();
							}
							*/
							if(softtest.config.java.Config.LANGUAGE==0){
								newlist.add("�ļ�:"+ProjectAnalysis.getCurrent_file()+" ��:"+expr.getBeginLine()+" ����:"+current_func);
							}else{
								newlist.add("file:"+ProjectAnalysis.getCurrent_file()+" line:"+expr.getBeginLine()+" Method:"+current_func);
							}
							
							traceinfo = newlist;
							return true;
						}
						
						// ȷ���Ƿ���з�����Դ���������Է���
						return false;
					}
					return false;
				}
			}
		}
		
		// 2. �������Դ�������Ƿ�Ϊ������Դ����		
		// ����Ԥ����ķ�������������ƥ�䡣ƥ��ɹ�����Ϊ��������һ��������Դ�ĺ�����
		if (methodName.matches(regex4)) {
			List<String> newlist = new ArrayList<String>();			
			/*
			String name = methodName;
			if (type instanceof Method) {
				name = ((Method) type).toGenericString();
			} else if (type instanceof Constructor) {
				name = ((Constructor) type).toGenericString();
			}
			*/
			if(softtest.config.java.Config.LANGUAGE==0){
				newlist.add("�ļ�:"+ProjectAnalysis.getCurrent_file()+" ��:"+expr.getBeginLine()+" ����:"+current_func);
			}else{
				newlist.add("file:"+ProjectAnalysis.getCurrent_file()+" line:"+expr.getBeginLine()+" Method:"+current_func);
			}
			traceinfo = newlist;
			return true;
		}
		
		// 3. ��node.getType()���÷����Ƿ��return��Դ����
		// to be implemented. to do or not to do???
				
		return false;
	}
	
	/**
	 * ��鶨�������������һ�ζ����Ƿ�Ϊ������Դ
	 * 
	 * �����߱�֤expr���ӽڵ�����xpath:
	 * ./PrimaryExpression[not(./PrimarySuffix)]/PrimaryPrefix/Name
	 * 
	 * @param expr ASTExpression
	 * @return
	 */
	private boolean checkVarExpression(ASTExpression expr) {		
		ASTName nn = (ASTName)expr.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
		if (!(nn.getNameDeclaration() instanceof VariableNameDeclaration)) {
			return false;
		}
		
		VariableNameDeclaration vnd = (VariableNameDeclaration) nn.getNameDeclaration();
		
		// ���nn���ڵĿ������ڵ�vex
		VexNode vex = nn.getCurrentVexNode();
		if (vex == null) {
			return false;
		}
		
		// ����ʼ���Ƿ������Դ
		if (vnd.getDeclareScope() instanceof LocalScope && expr.getScope().isSelfOrAncestor(vnd.getDeclareScope()) && expr.getScope().getEnclosingMethodScope().equals(vnd.getDeclareScope().getEnclosingMethodScope())) {
			ASTVariableDeclaratorId vdi = vnd.getDeclaratorId();
			if (vdi.jjtGetParent() instanceof ASTVariableDeclarator && vdi.getNextSibling() instanceof ASTVariableInitializer) {
				ASTVariableInitializer vi = (ASTVariableInitializer) vdi.getNextSibling();
				if (vi.jjtGetNumChildren() == 1 && vi.jjtGetChild(0) instanceof ASTExpression) {
					// ��鸳ֵ���ұߵı��ʽ�Ƿ������Դ(��ӵݹ�)
					if (checkExpression((ASTExpression) vi.jjtGetChild(0))) {
						return true;
					}
				}
			}
		}
		
		// ��������vex���ֵ�vnd
		// UseDefList������������ʼ���Ķ��壬��Ҫ���⴦������ to be implemented
		for (NameOccurrence occ : vex.getOccurrences()) {
			if (occ.getDeclaration() != vnd) {
				continue;
			}
			
			if(occ.getUseDefList()==null){
				continue;
			}
			
			// �����vnd�ĸ�ֵ
			for (NameOccurrence no : occ.getUseDefList()) {
				if ( !no.isOnLeftHandSide()) {
					continue;
				}
				ASTPrimaryExpression pe = (ASTPrimaryExpression)no.getLocation().getFirstParentOfType(ASTPrimaryExpression.class);
				if (pe != null && pe.getNextSibling() instanceof ASTAssignmentOperator) {
					ASTAssignmentOperator ao = (ASTAssignmentOperator) pe.getNextSibling();
					if ( ao.getNextSibling() instanceof ASTExpression) {
						// ��鸳ֵ���ұߵı��ʽ�Ƿ������Դ(��ӵݹ�)
						ASTExpression e = (ASTExpression)ao.getNextSibling();
						
						while (e.jjtGetNumChildren()==3 && e.jjtGetChild(1) instanceof ASTAssignmentOperator && e.jjtGetChild(2) instanceof ASTExpression) {
							e = (ASTExpression) e.jjtGetChild(2);
						}
						
						if (e != expr && checkExpression(e)) {
							return true;
						}
					}
				}
			}
		}
		
		return false;		
	}
	
	/**
	 * �����ʽ�Ƿ������Դ
	 *
	 * @param expr ASTExpression
	 * @return �Ƿ������Դ
	 */
	private boolean checkExpression(ASTExpression node) {
		if (node == null) {
			return false;
		}
		
		ASTMethodDeclaration md=(ASTMethodDeclaration)node.getFirstParentOfType(ASTMethodDeclaration.class);
		if (md!=null) {
			if (md.jjtGetChild(0).jjtGetChild(0)instanceof ASTType) {
				ASTType type=(ASTType)md.jjtGetChild(0).jjtGetChild(0);
				String typeImage=type.getTypeImage();
				StringBuffer sb = new StringBuffer();
				sb.append("^(");
				for (String s : RES_STRINGS) {
					sb.append("(" + s + ")|");
				}
				if (RES_STRINGS.length > 0) {
					sb.deleteCharAt(sb.length() - 1);
				}
				sb.append(")$");
				String reg = sb.toString();
				if (typeImage.matches(reg)) {
					return true;
				}
			}
		}
		
		
//		try {
//			/*
//			 * return new XXXX();
//			 * ���صĶ�������Դʵ�����װ��Դ��ʵ��
//			 */
//			if (node.findChildNodesWithXPath("./PrimaryExpression[not(./PrimarySuffix)]/PrimaryPrefix/AllocationExpression[./ClassOrInterfaceType]").size() != 0) {
//				return checkNewAllocExpression(node);
//			}
//			
//			/*
//			 * return v;
//			 * ���ر��������һ�ζ����Ǹ�ֵ������Դʵ��
//			 * 
//			 * ��鶨�������������һ�ζ����Ƿ�Ϊ������Դ
//			 */
//			if (node.findChildNodesWithXPath("./PrimaryExpression[not(./PrimarySuffix)]/PrimaryPrefix/Name").size() != 0) {
//				return checkVarExpression(node);
//			}
//			
//			/*
//			 * return x.getConnection();
//			 * or return getConnection();
//			 * or return a.b.getA().c.getConnection();
//			 * 
//			 * ����ֵ�ĵ��õĺ���������Դ��������
//			 * 
//			 * 1. �麯��ժҪ���Ƿ�Ϊ������Դ����
//			 * 2. �������Դ�������Ƿ�Ϊ������Դ����
//			 * 3. ��node.getType()���÷����Ƿ��return��Դ����
//			 */
//			if (node.findChildNodesWithXPath("./PrimaryExpression[count(PrimarySuffix)>=1 and count(PrimaryPrefix)=1 and ./PrimaryPrefix/Name and ./PrimarySuffix/Arguments]").size() != 0) {
//				return checkFuncCallExpression(node);
//			}
//		} catch (JaxenException e) {
//			throw new RuntimeException("xpath error @ AllocateFeature.java : checkExpression(ASTExpression node)",e);
//		}
		return false;
	}
	
	/**
	 * ���node�ڵ��Ƿ��Ǹ�ֵ���ʽ�ڵ�
	 * @param node ��Ҫ���Ľڵ㣬ͨ��ΪStatementExpression��Expression
	 * @return �Ƿ��Ǹ�ֵ���ʽ�ڵ�
	 */
	private boolean checkAssignmentExpression(SimpleJavaNode node) {
		if (node.jjtGetNumChildren()==3 
				&& node.jjtGetChild(1) instanceof ASTAssignmentOperator 
				&& ((ASTAssignmentOperator) node.jjtGetChild(1)).getImage().equals("=")
				&& node.jjtGetChild(0) instanceof ASTPrimaryExpression 
				&& node.jjtGetChild(2) instanceof ASTExpression) {			
			if (checkAssignmentExpressionLeft((ASTPrimaryExpression) node.jjtGetChild(0)) 
					&& checkAssignmentExpressionRight((ASTExpression) node.jjtGetChild(2)) ) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * ��鸳ֵ����ߵ��﷨���ڵ��Ƿ�����
	 * PrimaryExpression
	 *   --PrimaryPrefix
	 *     --Name
	 * @param node ��ֵ����ߵ��﷨���ڵ�
	 * @return node�����ӽڵ��Ƿ�ΪPrimaryExpression-PrimaryPrefix-Name�ṹ
	 */
	private boolean checkAssignmentExpressionLeft(SimpleJavaNode node) {
		if (node instanceof ASTPrimaryExpression 
				&& node.jjtGetNumChildren()==1 
				&& node.jjtGetChild(0).jjtGetNumChildren()==1 
				&& node.jjtGetChild(0).jjtGetChild(0) instanceof ASTName) {
			ASTName name = (ASTName) node.jjtGetChild(0).jjtGetChild(0);
			if (name.getNameDeclaration() instanceof VariableNameDeclaration) {
				VariableNameDeclaration vnd = (VariableNameDeclaration) name.getNameDeclaration();
				if (vnd.getDeclareScope() == name.getScope().getEnclosingClassScope()) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean checkAssignmentExpressionRight(SimpleJavaNode node) {
		if (node instanceof ASTExpression && checkExpression((ASTExpression) node)) {
			return true;
		}
		return false;
	}
	
	/**
	 * �Ժ���������Ϣ���е�����
	 * ��Ҫ���ǵ���������¼��֣�
	 * 
	 * 1. d.getxx(...) �ڷ�����������ָ����DriverManager.getConnection(a,b,c)������ֵΪĳ����Դ�ĺ�������;
	 * 2. d.getxx(...) �ڷ�����������ָ����DriverManager.getConnection(a,b,c)������ֵΪvoid�������ɵ���Դʵ������d.xx�ĺ�������;
	 * 3. gen(...) �ڷ�����������ָ����getConnection(a,b,c)������ֵΪĳ����Դ�ĺ�������;
	 * 4. gen(...) �ڷ�����������ָ����getConnection(a,b,c)������ֵΪvoid�������ɵ���Դʵ������this.xx�ĺ�������;
	 * 
	 */
	private class AllocateFeatureVisitor extends JavaParserVisitorAdapter{
		/**
		 * ͨ�����return��䣬ȷ����������ֵ�Ƿ�Ϊ�·�����Դ��
		 * 1. return new FileReader("abc");
		 * 2. return DriverManager.getConnection();
		 * 3. return v;
		 *    (1) v = new FileReader("abc");
		 *    (2) v = DriverManager.getConnection();
		 */
		@Override
		public Object visit(ASTReturnStatement node, Object data) {
			if (node == null) {
				return null;
			}
			
			if (node.jjtGetNumChildren() == 1 && node.jjtGetChild(0) instanceof ASTExpression) {
				isAllocateFunction = checkExpression((ASTExpression)node.jjtGetChild(0));
			}
			return null;
		}
		
		/*
		public Object visit(ASTStatementExpression node, Object data) {
			if (checkAssignmentExpression(node)) {
				ASTName name = (ASTName) node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
				VariableNameDeclaration vnd = (VariableNameDeclaration) name.getNameDeclaration();
				List<String> list = new ArrayList<String>();
				list.add("file:"+ProjectAnalysis.current_file+"line:"+node.getBeginLine()+"\n");
				table.put(new MapOfVariable(vnd), list);
			}
			return super.visit(node, data);
		}
		
		public Object visit(ASTExpression node, Object data) {
			if (checkAssignmentExpression(node)) {
				ASTName name = (ASTName) node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
				VariableNameDeclaration vnd = (VariableNameDeclaration) name.getNameDeclaration();
				List<String> list = new ArrayList<String>();
				list.add("file:"+ProjectAnalysis.current_file+"line:"+node.getBeginLine()+"\n");
				table.put(new MapOfVariable(vnd), list);
			}
			return super.visit(node, data);
		}
		
		public Object visit(ASTArguments node, Object data) {
			if (node.jjtGetParent() instanceof ASTPrimarySuffix) {
				ASTPrimarySuffix suffix = (ASTPrimarySuffix) node.jjtGetParent();
				if (suffix.getLastSibling() instanceof ExpressionBase) {
					ExpressionBase eb = (ExpressionBase) suffix.getLastExpression();
					if (eb.getType() instanceof Method) {
						MethodNode mn = MethodNode.findMethodNode(eb.getType());
						if (mn != null && mn.getMethodsummary() != null) {
							MethodSummary summary = mn.getMethodsummary();
							// �õ�����ժҪ����ѯ�Ƿ���з�����Դ����
							for (AbstractFeature ff : summary.getFeatrues().getTable().values()) {
								if (!(ff instanceof AllocateFeature)) {
									continue;
								}
								AllocateFeature fea = (AllocateFeature) ff;
								Hashtable<MapOfVariable, List<String>> fun = fea.getTable();
								
								for (MapOfVariable mov : fun.keySet()) {
									if (mov.findVariable(eb) != null) {
										
									}
								}
								
								// ȷ���Ƿ���з�����Դ���������Է���
								//return ((AllocateFeature) ff).isAllocateFunction();
							}
						}
					}
				}
			}
			return super.visit(node, data);
		}
		*/
		
		/*
		 * 
		 * ASTPrimaryExpression
		 * --ASTPrimaryPrefix
		 *   --ASTName(*)
		 * --ASTPrimarySuffix
		 *   --ASTArguments
		 * 
		 * @param node ASTName
		 * @return null
		 */
		/*
		public Object visit(ASTName node, Object data) {
			// �������
			if (node == null || node.getImage() == null) {
				return null;
			}
			
			// ASTName�ĸ��ڵ������ASTPrimaryPrefix, �����ڵ������ASTPrimaryExpression
			if ( !(node.jjtGetParent() instanceof ASTPrimaryPrefix) || !(node.jjtGetParent().jjtGetParent() instanceof ASTPrimaryExpression) ) {
				return null;
			}
			
			// ASTPrimaryExpressionֻ�������ӽڵ�ASTPrimaryPrefix��ASTPrimarySuffix��
			{
				
				ASTPrimaryExpression pe = (ASTPrimaryExpression)node.jjtGetParent().jjtGetParent();
				
				if (pe.jjtGetNumChildren() != 2) {
					return null;
				}
				if (pe.jjtGetChild(1).jjtGetNumChildren() != 1 || !(pe.jjtGetChild(1).jjtGetChild(0) instanceof ASTArguments)) {
					return null;
				}
			}
			
			if (node.getImage().matches("^[A-Za-z_][A-Za-z0-9_]*\\.[A-Za-z_][A-Za-z0-9_]*+$")) {
				// ���� x.close, y._a
				
			}
			else if (node.getImage().matches("^[A-Za-z_][A-Za-z0-9_]*$")) {
				// ���� close, _a, _a0
			}
			else {
				// Ŀǰ�����������������
				// do nothing here
			}
			
			return null;
		}
		*/
		/*
		public Object visit1(ASTName node, Object data) {
			//p.f();
			if(!node.getImage().contains(".")){
				//������û�а������÷���
				return null;
			}
			List<NameDeclaration> decllist = node.getNameDeclarationList();
			Iterator<NameDeclaration> decliter = decllist.iterator();
			MethodScope methodscope=node.getScope().getEnclosingMethodScope();
			while (decliter.hasNext()) {
				NameDeclaration decl = decliter.next();
				if (!(decl instanceof VariableNameDeclaration)){
					//����Ƿ�Ϊ����
					continue;
				}
				VariableNameDeclaration v = (VariableNameDeclaration) decl;
				
				ClassScope classcope=v.getDeclareScope().getEnclosingClassScope();
				if(node.getImage().startsWith(classcope.getClassName()+".")){
					//���� ����.��Ա ���
					continue;
				}	
				
				if(table.contains(v)){
					//����Ƿ��Ѿ���ӹ�
					continue;
				}
				if(!methodscope.isSelfOrAncestor(v.getDeclareScope())){
					//����Ƿ�Ϊ ��Ա������������
					continue;
				}
				VexNode vex=node.getCurrentVexNode();
				if(vex==null){
					//��鵱ǰ������ͼ�ڵ��Ƿ�Ϊ��
					continue;
				}
				Object domain = vex.getDomainWithoutNull(v);
				if (domain == null || DomainSet.getDomainType(domain) != ClassType.REF) {
					//����Ƿ�Ϊ �����ͱ���
					continue;
				}
				
				ReferenceDomain refdomain=(ReferenceDomain)domain;
				if(!refdomain.getUnknown()){
					continue;
				}
				
				for(NameOccurrence occ:vex.getOccurrences()){
					if(occ.getDeclaration()==v){
						if(occ.getUseDefList()!=null){
							continue;
						}
					}
				}
				
				List<SimpleJavaNode> list = new ArrayList<SimpleJavaNode>();
				list.add(node);
				table.put(v, list);
			}
			return null;
		}

		@Override
		public Object visit(ASTArguments node, Object data) {
			//f(p,a);
			if(node.jjtGetParent() instanceof ASTPrimarySuffix &&
					node.jjtGetParent().jjtGetParent() instanceof ASTPrimaryExpression){
				ASTPrimarySuffix suffix=(ASTPrimarySuffix)node.jjtGetParent();
				ASTPrimaryExpression primary=(ASTPrimaryExpression)node.jjtGetParent().jjtGetParent();
				ExpressionBase last=(ExpressionBase)primary.jjtGetChild(suffix.getIndexOfParent()-1);
				if(last.getType() instanceof Method){
					MethodNode methodnode=ProjectAnalysis.getMcgraph().getMethodNode(last.getType());
					if(methodnode==null){
						return super.visit(node, data);
					}
					MethodSummary summary = methodnode.getMethodsummary();
					if (summary == null) {
						return super.visit(node, data);
					}

					for (AbstractPrecondition pre : summary.getPreconditons().getTable().values()) {
						if (!(pre instanceof NpdPrecondition)) {
							continue;
						}
						NpdPrecondition npdpre = (NpdPrecondition) pre;
						Hashtable<VariableNameDeclaration,List<SimpleJavaNode>> usedvar = npdpre.check(last);
						Iterator<Map.Entry<VariableNameDeclaration,List<SimpleJavaNode>>> i=usedvar.entrySet().iterator();
						while (i.hasNext()) {
							Map.Entry<VariableNameDeclaration,List<SimpleJavaNode>> entry=i.next();
							VariableNameDeclaration v=entry.getKey();
							List<SimpleJavaNode> list=entry.getValue();
							
							VexNode vex=node.getCurrentVexNode();
							if(vex==null){
								//��鵱ǰ������ͼ�ڵ��Ƿ�Ϊ��
								continue;
							}
							Object domain = vex.getDomainWithoutNull(v);
							if (domain == null || DomainSet.getDomainType(domain) != ClassType.REF) {
								//����Ƿ�Ϊ �����ͱ���
								continue;
							}
														
							for(NameOccurrence occ:vex.getOccurrences()){
								if(occ.getDeclaration()==v){
									if(occ.getUseDefList()!=null){
										continue;
									}
								}
							}
							list.add(node);
							table.put(v, list);
						}
					}
				}
			}
			return super.visit(node, data);
		}
		*/
	}

}
