package softtest.domain.java;

/** �����򼯣�����δ������ */
public class ArbitraryDomain implements Domain {
	/** ��ӡ */
	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("Arbitrary");
		return b.toString();
	}
}
