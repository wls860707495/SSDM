/* Generated By:JJTree: Do not edit this line. ASTArrayInitializer.java */

package softtest.ast.java;

import java.util.*;

public class ASTArrayInitializer extends SimpleJavaNode {
	public ASTArrayInitializer(int id) {
		super(id);
	}

	public ASTArrayInitializer(JavaParser p, int id) {
		super(p, id);
	}

	/**
	 * Accept the visitor. *
	 */
	@Override
	public Object jjtAccept(JavaParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}

	// added by xqing
	/** 当前维的长度 */
	private int len = 0;

	/** 增加当前维长 */
	public void incLength() {
		len++;
	}

	/** 返回当前维长 */
	public int getlen() {
		return len;
	}

	/** 存储维数及每一维的长度 */
	private ArrayList<Integer> dims = new ArrayList<Integer>();

	/** 是否已经统计过维长的标记 */
	private boolean iscaldimsed = false;

	/** 设置统计标志 */
	public void setiscaldimsed(boolean iscaldimsed) {
		this.iscaldimsed = iscaldimsed;
	}

	/** 返回统计标志 */
	public boolean getiscaldimsed() {
		return iscaldimsed;
	}

	/** 返回所有维长 */
	public ArrayList<Integer> getdims() {
		return dims;
	}

	/** 统计所有维长 */
	public void calDims() {
		if (iscaldimsed) {
			return;
		}
		setiscaldimsed(true);
		List list = findDirectChildOfType(ASTVariableInitializer.class);
		dims.add(0, list.size());
		for (int i = 0; i < list.size(); i++) {
			ASTVariableInitializer init = (ASTVariableInitializer) list.get(i);
			init.calDims();
			ArrayList<Integer> initdims = init.getdims();
			for (int j = 0; j < initdims.size(); j++) {
				if (j + 1 >= dims.size()) {
					// 取深度最大的
					dims.add(initdims.get(j));
				} else {
					// 取最长度最小的
					if (initdims.get(j) < dims.get(j + 1)) {
						dims.set(j + 1, initdims.get(j));
					}
				}
			}
		}
	}

}
