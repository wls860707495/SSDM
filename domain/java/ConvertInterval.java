package softtest.domain.java;

/** ����ת�� */
public class ConvertInterval {
	/** ����������ת��Ϊ�������� */
	public static IntegerInterval DoubleToInteger(DoubleInterval d) {
		IntegerInterval i = new IntegerInterval(Math.round(d.getMin()), Math.round(d.getMax()));
		return i;
	}

	/** ����������ת��Ϊ�������� */
	public static DoubleInterval IntegerToDouble(IntegerInterval i) {
		DoubleInterval d = new DoubleInterval(i.getRealMin(), i.getRealMax());
		return d;
	}

	public static void main(String args[]) {
		DoubleInterval d = new DoubleInterval(-3.3, 5.5);
		IntegerInterval i = DoubleToInteger(d);
		System.out.println(i);
	}
}
