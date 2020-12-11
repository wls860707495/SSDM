package softtest.ccd.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.Random;
import softtest.ast.java.*;
import softtest.symboltable.java.*;

import softtest.ast.java.ASTAdditiveExpression;
import softtest.ast.java.ASTArguments;
import softtest.ast.java.ASTAssignmentOperator;
import softtest.ast.java.ASTBlock;
import softtest.ast.java.ASTBlockStatement;
import softtest.ast.java.ASTBooleanLiteral;
import softtest.ast.java.ASTClassOrInterfaceType;
import softtest.ast.java.ASTDoStatement;
import softtest.ast.java.ASTExpression;
import softtest.ast.java.ASTFieldDeclaration;
import softtest.ast.java.ASTForStatement;
import softtest.ast.java.ASTIfStatement;
import softtest.ast.java.ASTImportDeclaration;
import softtest.ast.java.ASTLiteral;
import softtest.ast.java.ASTMethodDeclaration;
import softtest.ast.java.ASTMultiplicativeExpression;
import softtest.ast.java.ASTName;
import softtest.ast.java.ASTNullLiteral;
import softtest.ast.java.ASTPackageDeclaration;
import softtest.ast.java.ASTPrimitiveType;
import softtest.ast.java.ASTRelationalExpression;
import softtest.ast.java.ASTResultType;
import softtest.ast.java.ASTShiftExpression;
import softtest.ast.java.ASTStatement;
import softtest.ast.java.ASTStatementExpression;
import softtest.ast.java.ASTVariableDeclaratorId;
import softtest.ast.java.ASTWhileStatement;
import softtest.ast.java.SimpleJavaNode;
import softtest.ast.java.SimpleNode;
import softtest.cfg.java.*;
import softtest.config.java.Config;
import softtest.symboltable.java.NameOccurrence;

public class StmtsSliceMatcher2 {

	public static int P = 59;
	public static int lastMod = 1;
	public static int lastHash = 0;
	public static int minTiles = 4;
	public static int deep = 2;// ���Ϊ 1, �� A + B * C��ֻ�н���B/C����ʱ�Ż�ȡB/C���������Ϊ��ϣֵ��Դ,��Ϊ1ʱ���ῼ����߲�����������
	public static int curDeep = 0; // ��¼��ǰ������ʽ�����
	public static boolean doExtend = true; // �����Ƿ������չƥ��Ŀ���

	//public static StmtMatchCollector2 collector = new StmtMatchCollector2();
	//public static StmtMatchCollector3 collector = new StmtMatchCollector3();
	public static CollectAllMatch collector = new CollectAllMatch();

	public static List<Statement> statements = new ArrayList();


	public static void jjtGenHashCode(SimpleJavaNode cur, Map group) {//, List stmts) {
		int hscode = cur.getId();
		
		/// ֻ��ASTBlockStatment���Լ�����������浥����Statement�ڵ�
		//  ����statements������
		if ( ! inSkipList(cur)) {
			//ASTBlockStatement bstst = (ASTBlockStatement) cur;
			Statement  stmt = new Statement(cur);
			stmt.setIndex(statements.size());
			statements.add(stmt);
		}
		
		
		// ���ȼ�������ӽڵ�� hashCode
		for (int i = 0; i < cur.jjtGetNumChildren(); i++) {
			// �������Ҫ������ƥ��ı��ʽ�ڵ㣬���¼��ǰ���ڱ��ʽ�����
			if( isParamedExpression(cur) ) {
				curDeep ++;
			}
			jjtGenHashCode((SimpleJavaNode) cur.jjtGetChild(i), group);
			if( isParamedExpression(cur) ) {
				curDeep --;
			}
		}
		for (int i = 0; i < cur.jjtGetNumChildren(); i++) {
			// �����if, while�ڵ㣬��ʹ��if/while����� expression �ڵ�� "if"/"while"
			// ��Ϊif/while �ڵ� hashCode �����ز�
			// ����� do �ڵ㣬����Ҫȡchild(1)��Ϊ�����ز�
			// ����� for �ڵ㣬����Ҫȡchild(1)��Ϊ�����ز�
			if(cur instanceof ASTIfStatement
			|| cur instanceof ASTWhileStatement) {
				hscode = ((SimpleJavaNode) cur.jjtGetChild(0)).getHashCode();
				break;
			} else
			if(cur instanceof ASTDoStatement ) {
				hscode = ((SimpleJavaNode) cur.jjtGetChild(1)).getHashCode();
				break;
			} else
			if( cur instanceof ASTForStatement) {
				// for ������ 0 - 4 ���ӽڵ�
				if( cur.jjtGetNumChildren() > 1 && cur.jjtGetChild(1) instanceof ASTExpression) {
					hscode = ((SimpleJavaNode) cur.jjtGetChild(1)).getHashCode();
				} else {
					hscode = 0;
				}
				break;
			}
			// ʵ�ָ�ֵ����Ĳ�����ƥ�䣬��߱�������������
			else if(cur instanceof ASTStatementExpression && cur.jjtGetNumChildren() == 3 && cur.jjtGetChild(1) instanceof ASTAssignmentOperator) {
				hscode = ((SimpleJavaNode) cur.jjtGetChild(2)).getHashCode();
				break;
			}
			else if(cur instanceof ASTAdditiveExpression || cur instanceof ASTMultiplicativeExpression) {//curDeep
				// ʵ�ֶ������������
				if( curDeep > deep ) {
					hscode = genHashCodeForOperator(cur.getImage());//����һ����ȵı��ʽֻʹ�����������Ϊ��ϣ����ֵ��Դ
					break;
				} else {
					hscode = P * hscode + ((SimpleNode) cur.jjtGetChild(i)).getHashCode();
				}
			}
			else if(cur instanceof ASTMethodDeclaration) {
				ASTResultType typeNode = (ASTResultType)cur.getFirstDirectChildOfType(ASTResultType.class);
				if(null != typeNode) {
					if(typeNode.jjtGetNumChildren() > 0) {
						hscode = cur.getId()*P + typeNode.getHashCode();
						break;
					}
				}
				Random r = new Random();
				hscode += r.nextInt();//����Ժ����ָ�Ļ����Ƿ��ܹ������������ƥ�䣿 
			}
			else if(cur instanceof ASTFieldDeclaration) { // ֻȡ������Ϊ��ϣ��Դ
				hscode = ((SimpleJavaNode) cur.jjtGetChild(0)).getHashCode();
			}
			else if(cur instanceof ASTArguments) {// ֻȡ������Ϊ��ϣ��Դ
				hscode = ((ASTArguments) cur).getArgumentCount();// + cur.getId();
				break;
			}
			/*else if(cur instanceof ASTLocalVariableDeclaration) {// ֻȡ������Ϊ��ϣ��Դ
				hscode = ((SimpleJavaNode) cur.jjtGetChild(0)).getHashCode();
			}*/
			else {
				hscode = P * hscode	+ ((SimpleNode) cur.jjtGetChild(i)).getHashCode();
			}
		}
		setHashCode(cur, hscode);
		// ֻ����BlockStatement���͵Ľڵ�
		if (inSkipList(cur)) {
			return;
		}

		Object o = group.get(hscode);
		if (o == null) {
			group.put(hscode, cur);
		} else if (o instanceof SimpleJavaNode) {
			List l = new ArrayList();
			l.add(o);
			l.add(cur);
			group.put(hscode, l);
		} else {
			List l = (List) o;
			l.add(cur);
		}
	}
	
	public static Map findNMatchedStatements() {
		System.out.println("[ statements ]");
		
		Map m = new HashMap();
		if(statements.size() <= 1) {
			return m;
		}
		
		lastHash = 0;
		Statement bst = null;
		int i = 0;
		// �����ʼKR��ϣֵ
		for (; i < minTiles && i < statements.size(); i++) {
			bst = statements.get(i);
			lastHash = P * lastHash + bst.getHashCode();
		}
		Statement fst = statements.get(i-minTiles>=0? i-minTiles:0);
		m.put(lastHash, fst);
		fst.setKRHashcode(lastHash);
		
		// ��ÿ����������KR��ϣֵ
		for (int j = minTiles; j < statements.size(); j++) {
			bst = statements.get(j);
			Statement lastBS = statements.get(j-minTiles);
			
			int lastKR = lastBS.getHashCode();
			lastHash = P * lastHash + bst.getHashCode() - lastMod * lastKR;
			
			Statement sec = statements.get(j-minTiles+1);
			sec.setKRHashcode(lastHash);

			Object o = m.get(lastHash);

			//log("|:" + lastHash + "  " + sec.getBeginLine());///////////////////////////

			if (o == null) {
				//m.put(lastHash, bst);//m.put(lastHash, lastBS);
				m.put(lastHash, sec);
			} else if (o instanceof Statement) {
				List l = new ArrayList();
				l.add(o);
				//l.add(bst);//l.add(lastBS);
				l.add(sec);
				m.put(lastHash, l);
			} else {
				List l = (List) o;
				//l.add(bst);//l.add(lastBS);
				l.add(sec);
			}
		}
		return m;
	}

	/**
	 * ֻ��BlockStatement, ��switch�ĸ�����䣬MethodDeclaration 
	 * FieldDeclaration, MethodDeclaration
	 * ����false 
	 */
	private static boolean inSkipList(SimpleJavaNode sk) {
		if (sk.jjtGetNumChildren() == 0) {
			return true;
		} else if (sk instanceof ASTImportDeclaration) {
			return true;
		} else if (sk instanceof ASTPackageDeclaration) {
			return true;
		}
		else
		if (sk instanceof ASTBlockStatement
		//|| sk instanceof ASTBlock || sk instanceof ASTMethodDeclaration || sk instanceof ASTClassOrInterfaceBodyDeclaration
		) {
			return false;
		}
		else
		if(sk instanceof ASTStatement && sk.jjtGetNumChildren()>0
		&& ! (sk.jjtGetChild(0) instanceof ASTBlock)) {
			if(sk.jjtGetParent() instanceof ASTIfStatement
			|| sk.jjtGetParent() instanceof ASTForStatement
			|| sk.jjtGetParent() instanceof ASTWhileStatement
			|| sk.jjtGetParent() instanceof ASTDoStatement) {
				return false;
			}
		}
		else
		if (sk instanceof ASTMethodDeclaration || sk instanceof ASTFieldDeclaration) {
			return false;
		}
		return true;
	}

	//  ���ݽڵ�����ͣ�����
	private static void setHashCode(SimpleJavaNode sk, int code) {
		// �����ASTName  û�к��ӵ� ASTClassOrInterfaceType 
		// ��Ҷ�ڵ㣬����Hashֵ���ټ������ı�Hash�Ľ����
		String txt = null;
		if (sk instanceof ASTName) {
			txt = sk.getImage();
		} else if (sk instanceof ASTClassOrInterfaceType) {
			if (sk.jjtGetNumChildren() == 0) {
				txt = sk.getImage();
			}
		} else if (sk instanceof ASTPrimitiveType) {
			txt = sk.getImage();
		} else if (sk instanceof ASTVariableDeclaratorId) {
			txt = sk.getImage();
		} else if (sk instanceof ASTLiteral) {
			//  NullLiteral
			if (sk.jjtGetNumChildren() > 0
			&& sk.jjtGetChild(0) instanceof ASTBooleanLiteral) {
				if (((ASTBooleanLiteral) sk.jjtGetChild(0)).isTrue()) {
					txt = "true";
				} else {
					txt = "false";
				}
			} else if (sk.jjtGetNumChildren() > 0
			&& sk.jjtGetChild(0) instanceof ASTNullLiteral) {
				txt = "null";
			}
			txt = sk.getImage();
		} else if (sk instanceof ASTIfStatement) {
			txt = "if";
		} else if (sk instanceof ASTWhileStatement) {
			txt = "while";
		} else if (sk instanceof ASTDoStatement) {
			txt = "do";
		} else if (sk instanceof ASTForStatement) {
			txt = "for";
		}
		
		for (int i = 0; txt!=null && i < txt.length(); i++) {
			code = code * P + txt.charAt(i);
		}
		sk.setHashCode(code);
	}

	// 
	public static void collect(Map krMatches) {
		for (Iterator i = krMatches.values().iterator(); i.hasNext();) {
			Object o = i.next();
			if (o instanceof List) {
				o = reverse((List) o);
				collector.collect((List) o);
			}
			i.remove();
		}
	}

	private static List reverse(List list) {
		Object sts[] = list.toArray();
		for (int i = 0; i < sts.length; i++) {
			for (int j = 1; j < sts.length - i; j++) {
				if (((Statement) sts[j]).getIndex() < ((Statement) sts[j - 1]).getIndex()) {
					Object tmp = sts[j];
					sts[j] = sts[j - 1];
					sts[j - 1] = tmp;
				}
			}
		}
		List ret = new ArrayList();
		
		for (Object s : sts) {
			ret.add(s);
			//log(" " + ((ASTBlockStatement) s).getIndex());
		}
		//log("");
		return ret;
	}

	public static List getMatches() {
		//List matches = collector.getMatches();
		Map matches = collector.getMatches();
		List<OneMatch> rmv     = new LinkedList<OneMatch>();
		List<ExtendMatch> adds = new LinkedList<ExtendMatch>(); 
		List ret;
		
		if( ! doExtend) { // �����Ƿ������չƥ��Ŀ���
			ret = new ArrayList(matches.values());
			return ret;
		}
		//  ��ԭ���������ƵĻ����ϣ��ٽ�һ������Occurences���м�顣
		System.err.println("--------------- Begin Extending Matches --------------");
		/*for (Iterator i = matches.iterator(); i.hasNext();) {
			OneMatch match = (OneMatch) i.next();
			//log(match.toString());// ///////////////////////////
			for (Iterator iterMtch = match.iterator(); iterMtch.hasNext();) {
				Statement mark = (Statement) iterMtch.next();
			}
		}*/
		// ����ÿ��ƥ��
		for (Iterator i = matches.keySet().iterator(); i.hasNext();) {
			//log("====================== OneMatch ======================");
			
			OneMatch match = (OneMatch) matches.get( i.next() );
			//log(match.toString());// ///////////////////////////
			Statement stmt1 = match.getFirstMark();
			Statement stmt2 = match.getSecondMark();
			int     len = match.getStmtCount();
			int     begin1 = stmt1.getIndex();
			int     begin2 = stmt2.getIndex();
			
			// ��Щ���������в���ƥ�䷶Χ�ڵĳ������ڵ������ statements �е�����
			Set<Integer>  idxsOfOccs1 = getOutRangeIdxsOfOccs(begin1, len);
			Set<Integer>  idxsOfOccs2 = getOutRangeIdxsOfOccs(begin2, len);
			/*
			//log("++++ begin: " + begin1 + "  end:" + (begin1+len-1));
			for(Integer it : idxsOfOccs1) {
				//log("--|--" + it + "  (" + statements.get(it).getBeginLine() + ")");
			}
			//log("++++ begin: " + begin2 + "  end:" + (begin2+len-1));
			for(Integer it : idxsOfOccs2) {
				//log("--|--" + it + "  (" + statements.get(it).getBeginLine() + ")");
			}
			*/
			// ��[begin1,len)Ҳ�ӵ���չƥ������ȥ��
			for(int beg = begin1; beg < begin1 + len; beg++) {
				idxsOfOccs1.add(beg);
			}
			for(int beg = begin2; beg < begin2 + len; beg++) {
				idxsOfOccs2.add(beg);
			}
			// ������չƥ��
			ExtendMatch  extMatch = doExtendMatch(match, idxsOfOccs1, idxsOfOccs2);
			/**  �����չƥ��Ƚ����룬�����ԭƥ�䣬���뷵���б���  **/
			if(extMatch.getArounds1().size() > 0) {
				rmv.add(match);
				adds.add(extMatch);
				//log("--|--");
			}
		}
		System.err.println("--------------- Removing match from matches --------------");
		for(Iterator<OneMatch> i = rmv.iterator(); i.hasNext(); ) {
			matches.remove( i.next().getMatchCode() );
		}
		System.err.println("--------------- Adding ExtMatch to matches --------------");
		for(Iterator<ExtendMatch> i = adds.iterator(); i.hasNext(); ) {
			ExtendMatch em = i.next();
			matches.put( em.getMatchCode(), em);
		}
		
		ret = new ArrayList(matches.values());
		return ret;
	}

	// locΪĳ���������ֶ�Ӧ�ĳ����﷨���ڵ�
	// ���ظýڵ��Ӧ�� ASTBlockStatement ���� ASTStatement��if/while/do/for�������ĵ���䣩
	// ������-1,��������Ǹ�����
	public static int getIndexInStatements(SimpleJavaNode  loc) {
		SimpleJavaNode parent = (SimpleJavaNode)loc.jjtGetParent();
		while(parent != null) {
			if (parent instanceof ASTBlockStatement) {
				return parent.getStmt().getIndex();
			}
			else
			if (parent instanceof ASTStatement) {
				if(parent.jjtGetParent() instanceof ASTIfStatement
				|| parent.jjtGetParent() instanceof ASTDoStatement
				|| parent.jjtGetParent() instanceof ASTWhileStatement
				|| parent.jjtGetParent() instanceof ASTForStatement) {
					return parent.getStmt().getIndex();
				}
			}
			parent = (SimpleJavaNode) parent.jjtGetParent();
		}
		return -1;
	}
	
	/** ���� node ��Ӧ�ĳ����﷨���ڵ�Ϊ ASTBlockStatement �� ASTStatement, 
	 * ��ASTBlockStatementû�й����Ŀ������ڵ㣬ֻ��ͨ�����ӽڵ���ҿ������ڵ�
	 * ASTBlockStatement.child[0].child[0]
	 * ASTStatement.child[0]
	 * **/
	private static List<VexNode> getVexNodeList(SimpleJavaNode  node) {
		if(node instanceof ASTBlockStatement) {
			return ((SimpleJavaNode)node.jjtGetChild(0).jjtGetChild(0)).getVexNode();
		} else 
		if(node instanceof ASTStatement) {
			return ((SimpleJavaNode)node.jjtGetChild(0)).getVexNode();
		}
		return null;
	}
	/**
	 * ��ȡ����䴮statements��ƥ�䲿��[begin, begin+len-1]�����г����������
	 * �ڷ�Χ[begin, begin+len-1]֮���������������statements�е������� 
	 */
	private static Set<Integer> getOutRangeIdxsOfOccs(int begin, int len) {
		Statement stmt1 = statements.get(begin);
		Set<Integer>        locOfOccs = new HashSet<Integer>();
		for(int idx = 0; idx < len; idx++) {
			// ��ǰ��ƥ��������������������г���
			List<NameOccurrence> allOccs = new LinkedList<NameOccurrence>();
			stmt1 = statements.get(begin + idx);
			SimpleJavaNode astNode1 = stmt1.astNode;
			//log("AST: " + astNode1 + " (" + astNode1.getBeginLine() + ") ");//  AST2:" + astNode2);
			
			List<VexNode> vexList = getVexNodeList(astNode1);//astNode1.getCurrentVexList();
			if(vexList != null) {
				for(VexNode v : vexList) {
					allOccs.addAll(v.getOccurrences());
				}
			} else {
				//log("List<VexNode> is null");
			}
			
			// ����ƥ�����
			for (NameOccurrence occ : allOccs) {
				SimpleJavaNode loc = (SimpleJavaNode)occ.getLocation();
				// ʹ�ó���
				if(occ.getOccurrenceType() == NameOccurrence.OccurrenceType.USE) {
					//log("useocc of Occs: " + occ);
					
					List<NameOccurrence> defOccs = occ.getUseDefList();
					for(NameOccurrence  defOcc : defOccs) {
						//log("defocc of useocc: " + defOcc);
						SimpleJavaNode locOfdef = (SimpleJavaNode)defOcc.getLocation();
						// ��ø�ʹ�ó��ֶ�Ӧ�Ķ���������������statements�е�λ��
						int defIdxOfStmts = getIndexInStatements(locOfdef);
						if (defIdxOfStmts < begin && defIdxOfStmts != -1) {
							//log("0==add:" + locOfdef.printNode());
							locOfOccs.add(defIdxOfStmts);
						}
						// ��ӣ��ö�����ֵ�����ʹ�ó���
						List<NameOccurrence> defuse = defOcc.getDefUseList();
						for(NameOccurrence  useOcc : defuse) {
							//log("useocc of defocc of useocc: " + useOcc);
							SimpleJavaNode locOfUse = (SimpleJavaNode)useOcc.getLocation();
							// ��ø�ʹ�ó������������statements�е�λ��
							int useIdxOfStmts = getIndexInStatements(locOfUse);
							if (useIdxOfStmts < begin && useIdxOfStmts != -1
							|| useIdxOfStmts >= begin + len ) {
								//log("add useocc of defocc of useocc: " + useOcc + "  " + locOfUse.getBeginLine());
								locOfOccs.add(useIdxOfStmts);
							}
						}
					}
				}
				// �������
				else {
					//log("defocc of Occs: " + occ);
					// ����-ȡ��������
					List<NameOccurrence> defUndefOccs = occ.getDefUndefList();
					for(NameOccurrence  duOcc : defUndefOccs) {
						SimpleJavaNode locOfdef = (SimpleJavaNode)duOcc.getLocation();
						// ���ȡ����ǰ����Ķ���������������statements�е�λ��,���ڷ�Χ֮�⣬�����
						int defIdxOfStmts = getIndexInStatements(locOfdef);
						if (defIdxOfStmts >= begin + len) {
							//log("1==add:" + locOfdef);
							locOfOccs.add(defIdxOfStmts);
						}
					}
					// ����-ʹ����
					List<NameOccurrence> defUseOccs = occ.getDefUseList();
					for(NameOccurrence  duOcc : defUseOccs) {
						SimpleJavaNode locOfdef = (SimpleJavaNode)duOcc.getLocation();
						// ��øö�����ֺ����ʹ�ó������������statements�е�λ��
						int defIdxOfStmts = getIndexInStatements(locOfdef);
						if (defIdxOfStmts >= begin + len) {
							//log("2==add:" + locOfdef);
							locOfOccs.add(defIdxOfStmts);
						}
					}
					// ȡ������-������
					List<NameOccurrence> udefdefOccs = occ.getUndefDefList();
					for(NameOccurrence  duOcc : udefdefOccs) {
						SimpleJavaNode locOfdef = (SimpleJavaNode)duOcc.getLocation();
						// ��õ�ǰ������ȡ���Ķ���������������statements�е�λ��
						int defIdxOfStmts = getIndexInStatements(locOfdef);
						if (defIdxOfStmts < begin && defIdxOfStmts != -1) {
							//log("3==add:" + locOfdef);
							locOfOccs.add(defIdxOfStmts);
						}
					}
				}
			}// for(NameOccurrence:Occs)
		}// for(int idx = 0; idx < len; idx++)
		return locOfOccs;
	}

	/** ��ƥ��˫����ǰ�����������ǰ�����Ϊ�߽�Ѱ���µ�ƥ���
	 * idxsOfOccs1/2 �ǲ���match��Χ�ڵ�������������
	 */
	public static ExtendMatch doExtendMatch(OneMatch match, Set<Integer> idxsOfOccs1, Set<Integer> idxsOfOccs2) {
		ExtendMatch ematch = new ExtendMatch(match);
		List<Integer> idxs1 = new ArrayList<Integer>(idxsOfOccs1.size());
		List<Integer> idxs2 = new ArrayList<Integer>(idxsOfOccs2.size());
		idxs1.addAll(idxsOfOccs1);
		idxs2.addAll(idxsOfOccs2);
		Collections.sort(idxs1);
		Collections.sort(idxs2);
		
		/** �Դ�����ǰ��������֮��������䰴�չ�ϣֵ�����ҳ��������о���
		 *  ��ͬ��ϣֵ����䡣 ����ʹ�ó����﷨���ڵ��ڵĹ�ϣֵ */
		/** stmts1/2��Statement�Ѿ�����  **/
		List<Statement>  stmts1 = getSortedStatements(idxs1, match.getFirstMark().getIndex(), match.getStmtCount());//new ArrayList<Statement>(idxsOfOccs1.size());
		List<Statement>  stmts2 = getSortedStatements(idxs2, match.getSecondMark().getIndex(), match.getStmtCount());
		
		/** ��ƥ��ĵ������ematch�� */
		fillMatchPoints(ematch, stmts1, stmts2);
		//log("******************\n" + ematch.toString());
		return ematch;
	}
	public static void fillMatchPoints(ExtendMatch ematch, List<Statement> stmts1, List<Statement> stmts2) {
		for(int i = 0, j = 0; i < stmts1.size() && j < stmts2.size(); ) {
			if(stmts1.get(i).getHashCode() < stmts2.get(j).getHashCode()) {
				i++;
			} else if(stmts1.get(i).getHashCode() > stmts2.get(j).getHashCode()) {
				j++;
			}
			// �����ܵľ��ж����ͬ�Ĺ�ϣֵ�Ľڵ���� Arounds
			while(i < stmts1.size() && j < stmts2.size()
			&&    stmts1.get(i).getHashCode() == stmts2.get(j).getHashCode()) {
				if(stmts1.get(i).getIndex() < ematch.getFirstMark().getIndex()
				|| stmts1.get(i).getIndex() >= ematch.getFirstMark().getIndex() + ematch.getCoreLen()) {
					ematch.addArounds1(stmts1.get(i).getIndex());
				}
				if(stmts2.get(j).getIndex() < ematch.getSecondMark().getIndex()
				|| stmts2.get(j).getIndex() >= ematch.getSecondMark().getIndex() + ematch.getCoreLen()) {
					ematch.addArounds2(stmts2.get(j).getIndex());
				}
				if( i + 1 < stmts1.size()
				&&  stmts1.get(i+1).getHashCode() == stmts2.get(j).getHashCode()) {
					i++;
				}
				else
				if( j + 1 < stmts2.size()
				&&  stmts1.get(i).getHashCode() == stmts2.get(j+1).getHashCode()) {
					j++;
				} else {
					i++;
					j++;
				}
			}
		}
	}
	/**
	 * @param idxs   ��������䴮��˳��������������б�
	 * @param begin  ��ص�ƥ��ĺ˵Ŀ�ʼ����
	 * @param len    ��ص�ƥ��ĺ˵ĳ���
	 * @return       ʹ�����Ĺ�ϣֵ��Ϊ�Ƚϼ�ֵ
	 */
	public static List<Statement> getSortedStatements(List<Integer> idxs, int begin, int len) {
		List<Statement>  rets = new ArrayList<Statement>();
		if(idxs.size() == 0) {
			return rets;
		}
		/**  [A, B][match.mark1.begin, match.mark1.end][C, D]  **/
		int A = idxs.get(0);
		int B = begin - 1;
		int C = begin + len;
		int D = idxs.get(idxs.size()-1);
		//log("()()" + A +" " + B +" " + C +" " + D);
		
		for(int i = A; i <= B; i++) {
			rets.add(statements.get(i));
		}
		for(int i = C; i <= D; i++) {
			rets.add(statements.get(i));
		}
		class HashCompare implements Comparator {
			public int compare(Object o1, Object o2) {
				if(!(o1 instanceof Statement)
				|| !(o2 instanceof Statement)) {
					return 0;
				}
				Statement st1 = (Statement)o1;
				Statement st2 = (Statement)o2;
				long l = new Long(st1.getHashCode()) - new Long(st2.getHashCode());
				if(l < 0)
					return -1;
				else if(l == 0)
					return 0;
				else 
					return 1;
			}
		}
		Collections.sort(rets, new HashCompare());
		for(int i = 0; i < rets.size(); i ++) {
			//log("HashCode:  " + rets.get(i).getHashCode());
		}
		return rets;
	}
	
	public static boolean isParamedExpression(SimpleJavaNode cur) {
		if(cur instanceof ASTAdditiveExpression
		|| cur instanceof ASTMultiplicativeExpression
		|| cur instanceof ASTShiftExpression
		|| cur instanceof ASTRelationalExpression) {
			return true;
		}
		else {
			return false;
		}
	}
	public static int    genHashCodeForOperator(String img) {
		if(img.length() == 1) {
			return img.charAt(0);
		}
		else {
			String ops [] = img.split("#");
			int hs = 0;
			for(int i = 0; i < ops.length; i++) {
				hs += ops[i].charAt(0);
			}
			return  hs;
		}
	}
	public static Statement stmtAt(int offset, Statement cur) {
		return statements.get(offset + cur.getIndex());
	}
	
	
	public static void logc(String str) {
		logc("StmtsSliceMatcher::" + str);
	}
	
	public static void log(String str) {
		if( Config.DEBUG ) {
			System.out.println(str);
		}
	}
}


