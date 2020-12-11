package softtest.domain.java;

/** �������� */
public class DoubleInterval implements Comparable<DoubleInterval> {
	/** �½� */
	private double min;

	/** �Ͻ� */
	private double max;

	/** ��min,maxָ���ı����䣬[min��max] */
	public DoubleInterval(double min, double max) {
		if (Double.isNaN(min) || Double.isNaN(max)) {
			throw new RuntimeException("DoubleInterval(min,max):min and max must not be NaN");
		}
		
		if (min == max && (min == Double.POSITIVE_INFINITY || min == Double.NEGATIVE_INFINITY)) {
			this.min = Double.POSITIVE_INFINITY;
			this.max = Double.NEGATIVE_INFINITY;
		} else {
			this.min = min;
			this.max = max;
		}
	}

	/** ��x,xָ���ı����䣬[x��x] */
	public DoubleInterval(double x) {
		if (Double.isNaN(x)) {
			throw new RuntimeException("DoubleInterval(min,max):min and max must not be NaN");
		}
		if ((Double.NEGATIVE_INFINITY < x) && (x < Double.POSITIVE_INFINITY)) {
			this.min=x;
			this.max=x;
		} else {
			throw new RuntimeException("DoubleInterval(x): must have -inf<x<inf");
		}
	}

	/** ���������������䣬[-inf��inf] */
	public DoubleInterval() {
		min = Double.NEGATIVE_INFINITY;
		max = Double.POSITIVE_INFINITY;
	}

	/** ��min,maxָ��������,minexcluded��maxexcludedΪtrue�ֱ�ָʾmin��max�������������� */
	public DoubleInterval(double min, double max, boolean minexcluded, boolean maxexcluded) {
		double tmin = min, tmax = max;
		if (Double.isNaN(min) || Double.isNaN(max)) {
			throw new RuntimeException("DoubleInterval(min,max):min and max must not be NaN");
		}
		if (minexcluded) {
			tmin = DoubleMath.nextfp(min);
		}
		if (maxexcluded) {
			tmax = DoubleMath.prevfp(max);
		}
		
		if (tmin == max && (tmin == Double.POSITIVE_INFINITY || tmin == Double.NEGATIVE_INFINITY)) {
			this.min = Double.POSITIVE_INFINITY;
			this.max = Double.NEGATIVE_INFINITY;
		} else {		
			this.min = tmin;
			this.max = tmax;
		}
	}

	/** �������� */
	public DoubleInterval(DoubleInterval x) {
		this.min = x.min;
		this.max = x.max;
	}

	/** �ж����������Ƿ���ȣ��Ͻ���½�ֱ������Ϊ������� */
	@Override
	public boolean equals(Object o) {
		if ((o == null) || !(o instanceof DoubleInterval)) {
			return false;
		}
		if (this == o) {
			return true;
		}
		DoubleInterval x = (DoubleInterval) o;
		if(x.isEmpty()&&this.isEmpty()){
			return true;
		}
		return ((max == x.max) && (min == x.min));
	}

	/** ����½� */
	public double getMin() {
		return min;
	}

	/** ����Ͻ� */
	public double getMax() {
		return max;
	}

	/** �����½� */
	public void setMin(double min) {
		this.min = min;
	}

	/** �����Ͻ� */
	public void setMax(double max) {
		this.max = max;
	}

	/** ��ӡ */
	@Override
	public String toString() {
		return "[" + min + "," + max + "]";
	}

	/** �Ƚ������˳���������� */
	public int compareTo(DoubleInterval interval) {
		if (min == interval.min) {
			return 0;
		} else if (min > interval.min) {
			return 1;
		} else {
			return -1;
		}
	}

	/** �õ�һ���յ����� */
	public static DoubleInterval emptyInterval() {
		DoubleInterval z = new DoubleInterval(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
		return z;
	}

	/** �õ�һ����������[-inf,inf] */
	public static DoubleInterval fullInterval() {
		DoubleInterval z = new DoubleInterval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		return z;
	}

	/** �ж�һ�������Ƿ�Ϊ�� */
	public boolean isEmpty() {
		return min > max;
	}
	
	/** �ж����������Ƿ���ܺϲ� */
	public static boolean canBeJoined(DoubleInterval interval1, DoubleInterval interval2) {
		if (interval1.isEmpty() || interval2.isEmpty()) {
			return false;
		}
		if (interval1.getMax() >= DoubleMath.prevfp(interval2.getMin()) && interval1.getMin() <= DoubleMath.nextfp(interval2.getMax())) {
			return true;
		}
		return false;
	}

	/** �ж�һ�������Ƿ�ֻ����һ���� */
	public boolean isCanonical() {
		return min == max;
	}

	/** �ж������Ƿ����x */
	public boolean contains(double x) {
		return (x >= min && x <= max);
	}

	/** ��������ȡ��������һ������ */
	public static DoubleInterval intersect(DoubleInterval x, DoubleInterval y) {
		return new DoubleInterval(Math.max(x.min, y.min), Math.min(x.max, y.max));
	}

	/** ��������ȡ��������һ������ */
	public static DoubleInterval union(DoubleInterval x, DoubleInterval y) {
		if(x.isEmpty()){
			return new DoubleInterval(y);
		}
		if(y.isEmpty()){
			return new DoubleInterval(x);
		}
		return new DoubleInterval(Math.min(x.min, y.min), Math.max(x.max, y.max));
	}

	/** ��ѧ���㣺�ӷ� */
	public static DoubleInterval add(DoubleInterval x, DoubleInterval y) {
		DoubleInterval z = null;
		if(x.isEmpty()||y.isEmpty()){
			z=DoubleInterval.emptyInterval();
			return z;
		}
		z=new DoubleInterval();
		z.min = DoubleMath.add_lo(x.min, y.min);
		z.max = DoubleMath.add_hi(x.max, y.max);
		return (z);
	}

	/** ��ѧ���㣺�ӷ� */
	public static DoubleInterval add(DoubleInterval x, double d) {
		return add(x, new DoubleInterval(d));
	}

	/** ��ѧ���㣺���� */
	public static DoubleInterval sub(DoubleInterval x, DoubleInterval y) {
		DoubleInterval z = null;
		if(x.isEmpty()||y.isEmpty()){
			z=DoubleInterval.emptyInterval();
			return z;
		}
		z=new DoubleInterval();
		z.min = DoubleMath.sub_lo(x.min, y.max);
		z.max = DoubleMath.sub_hi(x.max, y.min);
		return (z);
	}

	/** ��ѧ���㣺���� */
	public static DoubleInterval sub(DoubleInterval x, double d) {
		return sub(x, new DoubleInterval(d));
	}

	/** ��ѧ���㣺�˷� */
	public static DoubleInterval mul(DoubleInterval x, DoubleInterval y) {
		DoubleInterval z = null;
		if(x.isEmpty()||y.isEmpty()){
			z=DoubleInterval.emptyInterval();
			return z;
		}
		z=new DoubleInterval();

		if (((x.min == 0.0) && (x.max == 0.0)) || ((y.min == 0.0) && (y.max == 0.0))) {
			z.min = 0.0;
			z.max = DoubleMath.NegZero;
		} else if (x.min >= 0.0) {
			if (y.min >= 0.0) {
				z.min = Math.max(0.0, DoubleMath.mul_lo(x.min, y.min));
				z.max = DoubleMath.mul_hi(x.max, y.max);
			} else if (y.max <= 0.0) {
				z.min = DoubleMath.mul_lo(x.max, y.min);
				z.max = Math.min(0.0, DoubleMath.mul_hi(x.min, y.max));
			} else {
				z.min = DoubleMath.mul_lo(x.max, y.min);
				z.max = DoubleMath.mul_hi(x.max, y.max);
			}
		} else if (x.max <= 0.0) {
			if (y.min >= 0.0) {
				z.min = DoubleMath.mul_lo(x.min, y.max);
				z.max = Math.min(0.0, DoubleMath.mul_hi(x.max, y.min));
			} else if (y.max <= 0.0) {
				z.min = Math.max(0.0, DoubleMath.mul_lo(x.max, y.max));
				z.max = DoubleMath.mul_hi(x.min, y.min);
			} else {
				z.min = DoubleMath.mul_lo(x.min, y.max);
				z.max = DoubleMath.mul_hi(x.min, y.min);
			}
		} else {
			if (y.min >= 0.0) {
				z.min = DoubleMath.mul_lo(x.min, y.max);
				z.max = DoubleMath.mul_hi(x.max, y.max);
			} else if (y.max <= 0.0) {
				z.min = DoubleMath.mul_lo(x.max, y.min);
				z.max = DoubleMath.mul_hi(x.min, y.min);
			} else {
				z.min = Math.min(DoubleMath.mul_lo(x.max, y.min), DoubleMath.mul_lo(x.min, y.max));
				z.max = Math.max(DoubleMath.mul_hi(x.min, y.min), DoubleMath.mul_hi(x.max, y.max));
			}
		}

		// System.out.println("mul("+x+","+y+")="+z);

		return (z);
	}

	/** ��ѧ���㣺�˷� */
	public static DoubleInterval mul(DoubleInterval x, double d) {
		return mul(x, new DoubleInterval(d));
	}

	/** ��ѧ���㣺���� */
	public static DoubleInterval div(DoubleInterval x, DoubleInterval y) {		
		/*if ((y.min == 0.0) && (y.max == 0.0))
			throw new RuntimeException("div(X,Y): Division by Zero");
		else
			return odiv(x, y);*/
		return odiv(x, y);
	}

	/** ��ѧ���㣺���� */
	public static DoubleInterval div(DoubleInterval x, double d) {
		return div(x, new DoubleInterval(d));
	}

	/** ��ѧ���㣺0�����ų��� */
	public static DoubleInterval odiv(DoubleInterval x, DoubleInterval y) {
		DoubleInterval z = null;
		if(x.isEmpty()||y.isEmpty()){
			z=DoubleInterval.emptyInterval();
			return z;
		}
		z=new DoubleInterval();

		/*if (((x.min <= 0.0) && (0.0 <= x.max)) && ((y.min <= 0.0) && (0.0 <= y.max))) {
			z.min = Double.NEGATIVE_INFINITY;
			z.max = Double.POSITIVE_INFINITY;
		} else*/{
			if (y.min == 0.0)
				y.min = 0.0;
			if (y.max == 0.0){
				if(y.min==0.0){
					y.max = 0.0;
				}else{
					y.max = DoubleMath.NegZero;
				}
			}

			if (x.min >= 0.0) {
				if(x.min==0.0&&x.max==0.0){
					z.min=0.0;
					z.max=0.0;
				}else if (y.min >= 0.0) {
					z.min = Math.max(0.0, DoubleMath.div_lo(x.min, y.max));
					z.max = DoubleMath.div_hi(x.max, y.min);
				} else if (y.max <= 0.0) {
					z.min = DoubleMath.div_lo(x.max, y.max);
					z.max = Math.min(0.0, DoubleMath.div_hi(x.min, y.min));
				} else {
					z.min = Double.NEGATIVE_INFINITY;
					z.max = Double.POSITIVE_INFINITY;
				}
			} else if (x.max <= 0.0) {
				if (y.min >= 0.0) {
					z.min = DoubleMath.div_lo(x.min, y.min);
					z.max = Math.min(0.0, DoubleMath.div_hi(x.max, y.max));
				} else if (y.max <= 0.0) {
					z.min = Math.max(0.0, DoubleMath.div_lo(x.max, y.min));
					z.max = DoubleMath.div_hi(x.min, y.max);
				} else {
					z.min = Double.NEGATIVE_INFINITY;
					z.max = Double.POSITIVE_INFINITY;
				}
			} else {
				if (y.min >= 0.0) {
					z.min = DoubleMath.div_lo(x.min, y.min);
					z.max = DoubleMath.div_hi(x.max, y.min);
				} else if (y.max <= 0.0) {
					z.min = DoubleMath.div_lo(x.max, y.max);
					z.max = DoubleMath.div_hi(x.min, y.max);
				} else {
					z.min = Double.NEGATIVE_INFINITY;
					z.max = Double.POSITIVE_INFINITY;
				}
			}
		}
		return new DoubleInterval(z.min,z.max);

	}

	/** ��ѧ���㣺-���� */
	public static DoubleInterval uminus(DoubleInterval x) {
		DoubleInterval z = null;
		if(x.isEmpty()){
			z=DoubleInterval.emptyInterval();
			return z;
		}
		z=new DoubleInterval();
		z.min = -x.max;
		z.max = -x.min;
		return (z);
	}
	
	// a%b=a-a/b*b;���Ľ����䲻��ȷ
	/** ��ѧ���㣺���� */
	public static DoubleInterval mod(DoubleInterval a, DoubleInterval b) {
		DoubleInterval z = null;
		if(a.isEmpty()||b.isEmpty()){
			z=DoubleInterval.emptyInterval();
			return z;
		}
		z=new DoubleInterval();
		DoubleInterval c=div(a, b);
		if(c.min>0){
			c.min=Math.floor(c.min);
		}else{
			c.min=Math.ceil(c.min);
		}
		
		if(c.max>0){
			c.max=Math.floor(c.max);
		}else{
			c.max=Math.ceil(c.max);
		}
		z = sub(a, mul(c, b));
		return (z);
	}

	/** ��ѧ���㣺���� */
	public static DoubleInterval mod(DoubleInterval a, int d) {
		DoubleInterval z = new DoubleInterval();
		DoubleInterval b = new DoubleInterval(d);
		z = mod(a,b);
		return (z);
	}	

	/** ��ѧ���㣺exp */
	public static DoubleInterval exp(DoubleInterval x) {
		DoubleInterval z = null;
		if(x.isEmpty()){
			z=DoubleInterval.emptyInterval();
			return z;
		}
		z=new DoubleInterval();
		z.min = DoubleMath.exp_lo(x.min);
		z.max = DoubleMath.exp_hi(x.max);
		// System.out.println("exp("+x+")= "+z);
		return (z);
	}

	/** ��ѧ���㣺log */
	public static DoubleInterval log(DoubleInterval x) {
		DoubleInterval z = null;
		if(x.isEmpty()){
			z=DoubleInterval.emptyInterval();
			return z;
		}
		z=new DoubleInterval();
		if (x.max <= 0)
			throw new RuntimeException("log(X): X<=0 not allowed");

		if (x.min < 0)
			x.min = 0.0;

		z.min = DoubleMath.log_lo(x.min);
		z.max = DoubleMath.log_hi(x.max);
		// System.out.println("log("+x+")= "+z);
		return (z);
	}

	/** ��ѧ���㣺sin */
	public static DoubleInterval sin(DoubleInterval x) {
		DoubleInterval z = null;
		if(x.isEmpty()){
			z=DoubleInterval.emptyInterval();
			return z;
		}
		z=new DoubleInterval();
		DoubleInterval y = new DoubleInterval();
		if (DoubleMath.Rounding) {
			y = div(x, new DoubleInterval(DoubleMath.prevfp(2 * Math.PI), DoubleMath.nextfp(2 * Math.PI)));
		} else {
			y = div(x, new DoubleInterval(2 * Math.PI, 2 * Math.PI));
		}
		z = sin2pi(y);
		return (z);
	}

	/** ��ѧ���㣺cos */
	public static DoubleInterval cos(DoubleInterval x) {
		DoubleInterval z = null;
		if(x.isEmpty()){
			z=DoubleInterval.emptyInterval();
			return z;
		}
		z=new DoubleInterval();		
		DoubleInterval y = new DoubleInterval();
		if (DoubleMath.Rounding) {
			y = div(x, new DoubleInterval(DoubleMath.prevfp(2 * Math.PI), DoubleMath.nextfp(2 * Math.PI)));
		} else {
			y = div(x, new DoubleInterval(2 * Math.PI, 2 * Math.PI));
		}
		z = cos2pi(y);
		return (z);
	}

	/** ��ѧ���㣺tan */
	public static DoubleInterval tan(DoubleInterval x) {
		DoubleInterval z = null;
		if(x.isEmpty()){
			z=DoubleInterval.emptyInterval();
			return z;
		}
		z=new DoubleInterval();		
		DoubleInterval y = new DoubleInterval();
		if (DoubleMath.Rounding) {
			y = div(x, new DoubleInterval(DoubleMath.prevfp(2 * Math.PI), DoubleMath.nextfp(2 * Math.PI)));
		} else {
			y = div(x, new DoubleInterval(2 * Math.PI, 2 * Math.PI));
		}
		z = tan2pi(y);
		return (z);
	}

	/** ��ѧ���㣺asin */
	public static DoubleInterval asin(DoubleInterval x) {
		DoubleInterval z = null;
		if(x.isEmpty()){
			z=DoubleInterval.emptyInterval();
			return z;
		}
		z=new DoubleInterval();
		x = intersect(x, new DoubleInterval(-1.0, 1.0));
		z.min = DoubleMath.asin_lo(x.min);
		z.max = DoubleMath.asin_hi(x.max);
		return (z);
	}

	/** ��ѧ���㣺acos */
	public static DoubleInterval acos(DoubleInterval x) {
		DoubleInterval z = null;
		if(x.isEmpty()){
			z=DoubleInterval.emptyInterval();
			return z;
		}
		z=new DoubleInterval();
		z.min = DoubleMath.acos_lo(x.max);
		z.max = DoubleMath.acos_hi(x.min);
		return (z);
	}

	/** ��ѧ���㣺atan */
	public static DoubleInterval atan(DoubleInterval x) {
		DoubleInterval z = null;
		if(x.isEmpty()){
			z=DoubleInterval.emptyInterval();
			return z;
		}
		z=new DoubleInterval();
		z.min = DoubleMath.atan_lo(x.min);
		z.max = DoubleMath.atan_hi(x.max);
		return (z);
	}

	/** ��ѧ���㣺?? */
	public static DoubleInterval sinRange(int a, int b) {
		switch (4 * a + b) {
		case 0:
			return (new DoubleInterval(-1.0, 1.0));
		case 1:
			return (new DoubleInterval(1.0, 1.0));
		case 2:
			return (new DoubleInterval(0.0, 1.0));
		case 3:
			return (new DoubleInterval(-1.0, 1.0));
		case 4:
			return (new DoubleInterval(-1.0, 0.0));
		case 5:
			return (new DoubleInterval(-1.0, 1.0));
		case 6:
			return (new DoubleInterval(0.0, 0.0));
		case 7:
			return (new DoubleInterval(-1.0, 0.0));
		case 8:
			return (new DoubleInterval(-1.0, 0.0));
		case 9:
			return (new DoubleInterval(-1.0, 1.0));
		case 10:
			return (new DoubleInterval(-1.0, 1.0));
		case 11:
			return (new DoubleInterval(-1.0, -1.0));
		case 12:
			return (new DoubleInterval(0.0, 0.0));
		case 13:
			return (new DoubleInterval(0.0, 1.0));
		case 14:
			return (new DoubleInterval(0.0, 1.0));
		case 15:
			return (new DoubleInterval(-1.0, 1.0));
		}
		System.out.println("ERROR in sinRange(" + a + "," + b + ")");
		return new DoubleInterval(-1, 1);
	}

	/** ��ѧ���㣺sin2pi0DI */
	static DoubleInterval sin2pi0DI(double x) {
		return new DoubleInterval(DoubleMath.sin2pi_lo(x), DoubleMath.sin2pi_hi(x));
	}

	/** ��ѧ���㣺cos2pi0DI */
	static DoubleInterval cos2pi0DI(double x) {
		return new DoubleInterval(DoubleMath.cos2pi_lo(x), DoubleMath.cos2pi_hi(x));
	}

	/** ��ѧ���㣺eval_sin2pi */
	static DoubleInterval eval_sin2pi(double x, int a) {
		switch (a) {
		case 0:
			return sin2pi0DI(x);
		case 1:
			return cos2pi0DI(x);
		case 2:
			return uminus(sin2pi0DI(x));
		case 3:
			return uminus(cos2pi0DI(x));
		}
		System.out.println("ERROR in eval_sin2pi(" + x + "," + a + ")");
		return new DoubleInterval();
	}

	/** ��ѧ���㣺sin2pi */
	public static DoubleInterval sin2pi(DoubleInterval x) {
		// DoubleInterval r = new DoubleInterval();
		DoubleInterval z = null;
		DoubleInterval y1 = null, y2 = null;
		int a = 0, b = 0;
		// double t1 = 0, t2 = 0;
		// double w;

		double m1, m2, n1, n2, z1, z2, width;
		int j1, j2;
		long mlo, mhi;

		// System.out.println("ENTERING sin2pi("+x+")");

		if (Double.isInfinite(x.min) || Double.isInfinite(x.max)) {
			return new DoubleInterval(-1.0, 1.0);
		}

		m1 = Math.rint(4 * x.min);
		j1 = (int) Math.round(m1 - 4 * Math.floor(m1 / 4.0));
		z1 = DoubleMath.sub_lo(x.min, m1 / 4.0);
		n1 = Math.floor(m1 / 4.0);

		m2 = Math.rint(4 * x.max);
		j2 = (int) Math.round(m2 - 4 * Math.floor(m2 / 4.0));
		z2 = DoubleMath.sub_hi(x.max, m2 / 4.0);
		n2 = Math.floor(m2 / 4.0);

		// System.out.println("in sin2pi: "+" x.min="+x.min+" x.max="+x.max);
		// System.out.println(" : "+" m1="+m1+" m2="+m2);
		// System.out.println(" : "+" z1="+z1+" z2="+z2);
		// System.out.println(" : "+" j1="+j1+" j2="+j2);
		// System.out.println(" : "+" n1="+n1+" n2="+n2);

		if ((z1 <= -0.25) || (z1 >= 0.25) || (z2 <= -0.25) || (z2 >= 0.25))
			return new DoubleInterval(-1.0, 1.0);

		mlo = (z1 >= 0) ? j1 : j1 - 1;
		mhi = (z2 <= 0) ? j2 : j2 + 1;

		width = (mhi - mlo + 4 * (n2 - n1));

		// System.out.println(" : "+" mlo="+mlo+" mhi="+mhi);
		// System.out.println(" : "+" width"+width);

		if (width > 4)
			return new DoubleInterval(-1.0, 1.0);

		y1 = eval_sin2pi(z1, j1);
		y2 = eval_sin2pi(z2, j2);

		z = union(y1, y2);

		a = (int) ((mlo + 4) % 4);
		b = (int) ((mhi + 3) % 4);

		// System.out.println("in sin2pi: "+" y1="+y1+" y2="+y2+" z="+z+
		// "\n j1="+j1+" j2="+j2+" mlo="+mlo+" mhi="+mhi +
		// "\n w ="+width+" a="+a+" b="+b+"\n sinRange="+sinRange(a,b));
		// if (r.min < 0) a = (a+3)%4;
		// if (r.max < 0) b = (b+3)%4;

		if (width <= 1)
			return z;
		else {
			// return union(z,sinRange(a,b));
			return union(z, sinRange(a, b));
		}
	}

	/** ��ѧ���㣺cos2pi */
	public static DoubleInterval cos2pi(DoubleInterval x) {
		// DoubleInterval r = new DoubleInterval();
		DoubleInterval z = null;
		DoubleInterval y1 = null, y2 = null;
		int a = 0, b = 0;
		// double t1 = 0, t2 = 0;
		// double w;

		double m1, m2, n1, n2, z1, z2, width;
		int j1, j2;
		long mlo, mhi;

		if (Double.isInfinite(x.min) || Double.isInfinite(x.max)) {
			return new DoubleInterval(-1.0, 1.0);
		}

		m1 = Math.rint(4 * x.min);
		j1 = (int) Math.round(m1 - 4 * Math.floor(m1 / 4.0));
		z1 = DoubleMath.sub_lo(x.min, m1 / 4.0);
		n1 = Math.floor(m1 / 4.0);

		m2 = Math.rint(4 * x.max);
		j2 = (int) Math.round(m2 - 4 * Math.floor(m2 / 4.0));
		z2 = DoubleMath.sub_hi(x.max, m2 / 4.0);
		n2 = Math.floor(m2 / 4.0);

		if ((z1 <= -0.25) || (z1 >= 0.25) || (z2 <= -0.25) || (z2 >= 0.25))
			return new DoubleInterval(-1.0, 1.0);

		mlo = (z1 >= 0) ? j1 : j1 - 1;
		mhi = (z2 <= 0) ? j2 : j2 + 1;

		width = (mhi - mlo + 4 * (n2 - n1));

		if (width > 4)
			return new DoubleInterval(-1.0, 1.0);

		y1 = eval_sin2pi(z1, (j1 + 1) % 4);
		y2 = eval_sin2pi(z2, (j2 + 1) % 4);

		z = union(y1, y2);

		a = (int) ((mlo + 4 + 1) % 4);
		b = (int) ((mhi + 3 + 1) % 4);

		// System.out.println("in sin2pi: "+" y1="+y1+" y2="+y2+" z="+z+
		// "\n j1="+j1+" j2="+j2+" mlo="+mlo+" mhi="+mhi +
		// "\n w ="+width+" a="+a+" b="+b+"\n sinRange="+sinRange(a,b));
		// if (r.min < 0) a = (a+3)%4;
		// if (r.max < 0) b = (b+3)%4;

		if (width <= 1)
			return z;
		else {
			// return union(z,sinRange(a,b));
			return union(z, sinRange(a, b));
		}
	}

	/** ��ѧ���㣺tan2pi */
	public static DoubleInterval tan2pi(DoubleInterval x) {
		return (div(sin2pi(x), cos2pi(x)));
	}

	/** ��ѧ���㣺asin2pi */
	public static DoubleInterval asin2pi(DoubleInterval x) {
		DoubleInterval z = null;
		if(x.isEmpty()){
			z=DoubleInterval.emptyInterval();
			return z;
		}
		z=new DoubleInterval();
		x = intersect(x, new DoubleInterval(-1.0, 1.0));
		z.min = DoubleMath.asin2pi_lo(x.min);
		z.max = DoubleMath.asin2pi_hi(x.max);
		return (z);
	}

	/** ��ѧ���㣺acos2pi */
	public static DoubleInterval acos2pi(DoubleInterval x) {
		DoubleInterval z = null;
		if(x.isEmpty()){
			z=DoubleInterval.emptyInterval();
			return z;
		}
		z=new DoubleInterval();
		z.min = DoubleMath.acos2pi_lo(x.max);
		z.max = DoubleMath.acos2pi_hi(x.min);
		return (z);
	}

	/** ��ѧ���㣺atan2pi */
	public static DoubleInterval atan2pi(DoubleInterval x) {
		DoubleInterval z = null;
		if(x.isEmpty()){
			z=DoubleInterval.emptyInterval();
			return z;
		}
		z=new DoubleInterval();
		z.min = DoubleMath.atan2pi_lo(x.min);
		z.max = DoubleMath.atan2pi_hi(x.max);
		return (z);
	}

	/** ȡ�е� */
	public static DoubleInterval midpoint(DoubleInterval x) {
		DoubleInterval z = null;
		if(x.isEmpty()){
			z=DoubleInterval.emptyInterval();
			return z;
		}
		z=new DoubleInterval();
		z.min = (x.min + x.max) / 2.0;
		z.max = z.min;

		if ((Double.NEGATIVE_INFINITY < z.min) && (Double.POSITIVE_INFINITY > z.min)) {
			return (z);
		} else if ((Double.NEGATIVE_INFINITY == x.min)) {
			if (x.max > 0.0) {
				z.min = 0.0;
				z.max = z.min;
				return (z);
			} else if (x.max == 0.0) {
				z.min = -1.0;
				z.max = z.min;
				return (z);
			} else {
				z.min = x.max * 2;
				z.max = z.min;
				return (z);
			}
		} else if ((Double.POSITIVE_INFINITY == x.max)) {
			if (x.min < 0.0) {
				z.min = 0.0;
				z.max = z.min;
				return (z);
			} else if (x.min == 0.0) {
				z.min = 1.0;
				z.max = z.min;
				return (z);
			} else {
				z.min = x.min * 2;
				z.max = z.min;
				return (z);
			}
		} else {
			z.min = x.min;
			z.max = x.max;
			System.out.println("Error in DoubleInterval.midpoint");
			return (z);
		}
	}

	/** ��˵� */
	public static DoubleInterval leftendpoint(DoubleInterval x) {
		DoubleInterval z = new DoubleInterval();
		z.min = x.min;
		if ((Double.NEGATIVE_INFINITY < z.min) && (Double.POSITIVE_INFINITY > z.min)) {
			z.max = z.min;
			return (z);
		} else {
			z.min = DoubleMath.nextfp(x.min);
			z.max = z.min;
			return (z);
		}
	}

	/** �Ҷ˵� */
	public static DoubleInterval rightendpoint(DoubleInterval x) {
		DoubleInterval z = new DoubleInterval();
		z.min = x.max;
		if ((Double.NEGATIVE_INFINITY < z.min) && (Double.POSITIVE_INFINITY > z.min)) {
			z.max = z.min;
			return (z);
		} else {
			z.min = DoubleMath.prevfp(x.max);
			z.max = z.min;
			return (z);
		}
	}

	/** �������㣺ָ�� (x**y) computed as exp(y*log(x)) */
	public static DoubleInterval power(DoubleInterval x, DoubleInterval y) {

		if (x.max <= 0)
			throw new RuntimeException("power(X,Y): X<=0 not allowed");
		else if (x.min < 0) {
			x.min = 0.0;
		}

		DoubleInterval z = exp(mul(y, log(x)));

		return z;

	}

	/** �������㣺sqrt */
	public static DoubleInterval sqrt(DoubleInterval x) {
		DoubleInterval z = null;
		if(x.isEmpty()){
			z=DoubleInterval.emptyInterval();
			return z;
		}
		z=new DoubleInterval();
		z.min = DoubleMath.sqrt_lo(x.min);
		z.max = DoubleMath.sqrt_hi(x.max);
		return (z);
	}

	/** �������㣺sqr */
	public static DoubleInterval sqr(DoubleInterval x) {
		DoubleInterval z = null;
		if(x.isEmpty()){
			z=DoubleInterval.emptyInterval();
			return z;
		}
		z=new DoubleInterval();
		if (x.min == 0) {
			z.min = 0;
			z.max = DoubleMath.nextfp(x.max * x.max);
		} else if (x.min > 0) {
			z.min = DoubleMath.prevfp(x.min * x.min);
			z.max = DoubleMath.nextfp(x.max * x.max);
		} else if (x.max == 0) {
			z.min = 0;
			z.max = DoubleMath.nextfp(x.min * x.min);
		} else if (x.max < 0) {
			z.min = DoubleMath.prevfp(x.max * x.max);
			z.max = DoubleMath.nextfp(x.min * x.min);
		} else {
			z.min = 0;
			if (-x.min > x.max) {
				z.max = DoubleMath.nextfp(x.min * x.min);
			} else {
				z.max = DoubleMath.nextfp(x.max * x.max);
			}
		}
		return z;
	}
}
