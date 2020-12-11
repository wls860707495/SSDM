package softtest.domain.java;

/** ֧�����������������ѧ���� */
public class IntegerMath {
	/** ������һ������,��󲻳��������� */
	public static long nextInt(long x) {
		if (x == Long.MAX_VALUE) return x;
		else return x+1;
	}

	/** ������һ��������,��������ķ��� */
	public static long prevInt(long x) {
		if (x == Long.MIN_VALUE) return x;
		else return x-1;
	}
	
	public static double add(double x, double y) {
		double z = x + y;
		if (z >= Long.MAX_VALUE) return Double.POSITIVE_INFINITY;
		else if (z <= Long.MIN_VALUE) return Double.NEGATIVE_INFINITY;
		else return x+y;
	}

	public static long add(long x, long y) {
		double z = (double)x + y;
		if (z >= Long.MAX_VALUE) return Long.MAX_VALUE;
		else if (z <= Long.MIN_VALUE) return Long.MIN_VALUE;
		else return x+y;
	}

	public static double sub(double x, double y) {
		double z = x - y;
		if (z >= Long.MAX_VALUE) return Double.POSITIVE_INFINITY;
		else if (z <= Long.MIN_VALUE) return Double.NEGATIVE_INFINITY;
		else return x-y;
	}
	
	public static long sub(long x, long y) {
		double z = (double)x - y;
		if (z >= Long.MAX_VALUE) return Long.MAX_VALUE;
		else if (z <= Long.MIN_VALUE) return Long.MIN_VALUE;
		else return x-y;
	}
	
	public static double mul(double x, double y) {
		double z = x * y;
		if (z >= Long.MAX_VALUE) return Double.POSITIVE_INFINITY;
		else if (z <= Long.MIN_VALUE) return Double.NEGATIVE_INFINITY;
		else return x*y;
	}

	public static long mul(long x, long y) {
		double z = (double)x * y;
		if (z >= Long.MAX_VALUE) return Long.MAX_VALUE;
		else if (z <= Long.MIN_VALUE) return Long.MIN_VALUE;
		else return x*y;
	}
	
	public static double div(double x, double y) {
		if (x == 0) return 0;
		if (y == 0 && x > 0) return Double.POSITIVE_INFINITY;
		if (y == 0 && x < 0) return Double.NEGATIVE_INFINITY;
		
		double z = x / y;
		if (z >= Long.MAX_VALUE) return Double.POSITIVE_INFINITY;
		else if (z <= Long.MIN_VALUE) return Double.NEGATIVE_INFINITY;
		else return x/y;
	}

	public static long div(long x, long y) {
		if (x == 0) return 0;
		if (y == 0 && x > 0) return Long.MAX_VALUE;
		if (y == 0 && x < 0) return Long.MIN_VALUE;
		return x/y;
	}
}
