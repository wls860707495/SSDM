package softtest.domain.java;

import java.util.*;

/** ������ */
public class DoubleDomain implements Domain {
	/** ���� */
	public static final DoubleDomain NULL = new DoubleDomain();

	/** ���伯�ϣ���С���󱣳�˳��� */
	private TreeSet<DoubleInterval> intervals = new TreeSet<DoubleInterval>();
	
	/** unknown��ǣ�����ǰ���Ƿ�δ֪*/
	private boolean unknown=false;
	
	/** ����unknown���*/
	public boolean getUnknown(){
		return unknown;
	}
	
	/** ����unknown���*/
	public void setUnknown(boolean unknown){
		this.unknown=unknown;
	}	
	
	/** �õ�һ��δ֪��*/
	public static DoubleDomain getUnknownDomain(){
		DoubleDomain r=new DoubleDomain();
		r.setUnknown(true);
		return r;
	}	
	
	/** �õ�һ��ȫ��*/
	public static DoubleDomain getFullDomain(){
		DoubleDomain r=new DoubleDomain(Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY);
		return r;
	}
	
	/** �õ�һ������*/
	public static DoubleDomain getEmptyDomain(){
		DoubleDomain r=new DoubleDomain(Double.POSITIVE_INFINITY,Double.NEGATIVE_INFINITY);
		return r;
	}
	
	/** �õ����е����伯��*/
	public TreeSet<DoubleInterval> getIntervals(){
		return intervals;
	}

	/** ȱʡ���캯�� */
	public DoubleDomain() {
	}

	/** ��ָ���ĵ�һ����[min,max]������minexcluded��maxexcludedָ��min��max�Ƿ�ȥ����Ϊtrue�Ļ���Ϊ�����䣩 */
	public DoubleDomain(double min, double max, boolean minexcluded, boolean maxexcluded) {
		DoubleInterval interval = new DoubleInterval(min, max, minexcluded, maxexcluded);
		if (!interval.isEmpty()) {
			intervals.add(interval);
		}
	}
	
	/** ��ָ���ĵ�һ����[min,max]������*/
	public DoubleDomain(double min, double max) {
		this(min,max,false,false);
	}

	/** �Զ�����乹���� */
	public DoubleDomain(DoubleInterval[] intervals) {
		for (int i = 0; i < intervals.length; i++) {
			this.mergeWith(intervals[i]);
		}
	}

	/** �Ե�һ���乹���� */
	public DoubleDomain(DoubleInterval interval) {
		this.mergeWith(interval);
	}

	/** ���������򣬿��������л�ȥ�����õĿ����� */
	public DoubleDomain(DoubleDomain domain) {
		for (DoubleInterval interval : domain.intervals) {
			if (!interval.isEmpty()) {
				this.intervals.add(new DoubleInterval(interval));
			}
		}
		unknown=domain.unknown;
	}

	/** �ж����Ƿ�Ϊ�� */
	public boolean isEmpty() {
		if(this.unknown){
			return false;
		}
		DoubleDomain d = new DoubleDomain(this);
		if (d.intervals.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	
	/** �жϵ�ǰ���Ƿ����������x*/
	public boolean contains(double x){
		for (DoubleInterval interval : intervals) {
			if(interval.contains(x)){
				return true;
			}
		}
		return false;
	}
	
	/** �жϵ�ǰ���Ƿ������x*/
	public boolean contains(DoubleDomain x){
		DoubleDomain temp=DoubleDomain.union(this, x);
		if(temp.equals(this)){
			return true;
		}
		return false;
	}

	/** �ж����Ƿ�ֵ����һ������ */
	public boolean isCanonical() {
		DoubleDomain d = new DoubleDomain(this);
		if (d.intervals.size() == 1) {
			for (DoubleInterval interval : d.intervals) {
				if (interval.isCanonical()) {
					return true;
				}
			}
		}
		return false;
	}

	/** �ж����Ƿ���� */
	@Override
	public boolean equals(Object o) {
		if ((o == null) || !(o instanceof DoubleDomain)) {
			return false;
		}
		if (this == o) {
			return true;
		}
		DoubleDomain x = (DoubleDomain) o;
		if(x.unknown&&this.unknown){
			return true;
		}
		
		if(x.unknown||this.unknown){
			return false;
		}
		
		if(isEmpty()&&x.isEmpty()){
			return true;
		}

		DoubleDomain a = new DoubleDomain(this), b = new DoubleDomain(x);
		if (a.intervals.size() == b.intervals.size()) {
			Iterator<DoubleInterval> ia = a.intervals.iterator(), ib = b.intervals.iterator();
			while (ia.hasNext()) {
				DoubleInterval interval1 = ia.next(), interval2 = ib.next();
				if (!interval1.equals(interval2)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/** ��ӡ */
	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		if(unknown){
			b.append("unknown");
		}else if (intervals.isEmpty()) {
			b.append("emptydomain");
		} else {
			boolean first = true;
			for (DoubleInterval interval : intervals) {
				if (first) {
					first = false;
				} else {
					b.append("U");
				}
				b.append(interval);
			}
		}
		return b.toString();
	}

	/** �������������������С��һ���� */
	public DoubleInterval jointoOneInterval() {
		if (this.unknown) {
			return DoubleInterval.fullInterval();
		}
		
		double tmax = 0, tmin = 0;
		if (intervals.size() > 0) {
			tmax = intervals.last().getMax();
			tmin = intervals.first().getMin();
		} else { // EMPTY
			tmax = Double.NEGATIVE_INFINITY;
			tmin = Double.POSITIVE_INFINITY;			
		}
		return new DoubleInterval(tmin, tmax);
	}

	/** ����������뵽���У�������������������������ϲ� */
	public void mergeWith(DoubleInterval newInterval) {
		// �ռ����к����������غϵ����䣬������Ӽ�����ɾ��
		if (newInterval.isEmpty()) {
			return;
		}
		List<DoubleInterval> intersectors = new ArrayList<DoubleInterval>();
		List<DoubleInterval> toRemove = new ArrayList<DoubleInterval>();
		for (DoubleInterval interval : intervals) {
			if (interval.isEmpty() || DoubleInterval.canBeJoined(interval, newInterval)) {
				if (!interval.isEmpty()) {
					intersectors.add(interval);
				}
				toRemove.add(interval);
			}
		}
		intervals.removeAll(toRemove);

		// �������������intersectors�е�������кϲ�
		double min = newInterval.getMin();
		double max = newInterval.getMax();

		for (DoubleInterval intersector : intersectors) {
			if (intersector.getMin() < newInterval.getMin()) {
				min = intersector.getMin();
			}
			if (intersector.getMax() > newInterval.getMax()) {
				max = intersector.getMax();
			}
		}
		// ����������
		intervals.add(new DoubleInterval(min, max));
	}

	/** ������������Ľ������֣����ܷ��ؿ����� */
	private static DoubleInterval intersectionOf(DoubleInterval i1, DoubleInterval i2) {
		double new_min;
		double new_max;
		if (i1.getMin() < i2.getMin()) {
			new_min = i2.getMin();
		} else {
			new_min = i1.getMin();
		}

		if (i1.getMax() > i2.getMax()) {
			new_max = i2.getMax();
		} else {
			new_max = i1.getMax();
		}

		return new DoubleInterval(new_min, new_max);
	}
	
	/** ��ѧ���㣺�ӷ� */
	public static DoubleDomain add(DoubleDomain a, DoubleDomain b) {
		DoubleDomain r=null;
		if(a.unknown||b.unknown){
			r=DoubleDomain.getUnknownDomain();
			return r;
		}
		r = new DoubleDomain();
		for (DoubleInterval i1 : a.intervals) {
			for (DoubleInterval i2 : b.intervals) {
				r.mergeWith(DoubleInterval.add(i1, i2));
			}
		}
		return r;
	}

	/** ��ѧ���㣺�ӷ� */
	public static DoubleDomain add(DoubleDomain a, double x) {
		DoubleDomain r=null;
		if(a.unknown){
			r=DoubleDomain.getUnknownDomain();
			return r;
		}
		r = new DoubleDomain();
		for (DoubleInterval i : a.intervals) {
			r.mergeWith(DoubleInterval.add(i, x));
		}
		return r;
	}

	/** ��ѧ���㣺���� */
	public static DoubleDomain sub(DoubleDomain a, DoubleDomain b) {
		DoubleDomain r=null;
		if(a.unknown||b.unknown){
			r=DoubleDomain.getUnknownDomain();
			return r;
		}
		r = new DoubleDomain();
		for (DoubleInterval i1 : a.intervals) {
			for (DoubleInterval i2 : b.intervals) {
				r.mergeWith(DoubleInterval.sub(i1, i2));
			}
		}
		return r;
	}

	/** ��ѧ���㣺���� */
	public static DoubleDomain sub(DoubleDomain a, double x) {
		DoubleDomain r=null;
		if(a.unknown){
			r=DoubleDomain.getUnknownDomain();
			return r;
		}
		r = new DoubleDomain();
		for (DoubleInterval i : a.intervals) {
			r.mergeWith(DoubleInterval.sub(i, x));
		}
		return r;
	}

	/** ��ѧ���㣺�˷� */
	public static DoubleDomain mul(DoubleDomain a, DoubleDomain b) {
		DoubleDomain r=null;
		if(a.unknown||b.unknown){
			r=DoubleDomain.getUnknownDomain();
			return r;
		}
		r = new DoubleDomain();
		for (DoubleInterval i1 : a.intervals) {
			for (DoubleInterval i2 : b.intervals) {
				r.mergeWith(DoubleInterval.mul(i1, i2));
			}
		}
		return r;
	}

	/** ��ѧ���㣺�˷� */
	public static DoubleDomain mul(DoubleDomain a, double x) {
		DoubleDomain r=null;
		if(a.unknown){
			r=DoubleDomain.getUnknownDomain();
			return r;
		}
		r = new DoubleDomain();
		for (DoubleInterval i : a.intervals) {
			r.mergeWith(DoubleInterval.mul(i, x));
		}
		return r;
	}

	/** ��ѧ���㣺���� */
	public static DoubleDomain div(DoubleDomain a, DoubleDomain b) {
		DoubleDomain r=null;
		if(a.unknown||b.unknown){
			r=DoubleDomain.getUnknownDomain();
			return r;
		}
		r = new DoubleDomain();
		for (DoubleInterval i1 : a.intervals) {
			for (DoubleInterval i2 : b.intervals) {
				r.mergeWith(DoubleInterval.div(i1, i2));
			}
		}
		return r;
	}

	/** ��ѧ���㣺���� */
	public static DoubleDomain div(DoubleDomain a, double x) {
		DoubleDomain r=null;
		if(a.unknown){
			r=DoubleDomain.getUnknownDomain();
			return r;
		}
		r = new DoubleDomain();
		for (DoubleInterval i : a.intervals) {
			r.mergeWith(DoubleInterval.div(i, x));
		}
		return r;
	}
	
	/** ��ѧ���㣺ȡ�ࣨ����ȷ�� */
	public static DoubleDomain mod(DoubleDomain a, DoubleDomain b) {
		DoubleDomain r=null;
		if(a.unknown||b.unknown){
			r=DoubleDomain.getUnknownDomain();
			return r;
		}
		r = new DoubleDomain();
		for (DoubleInterval i1 : a.intervals) {
			for (DoubleInterval i2 : b.intervals) {
				r.mergeWith(DoubleInterval.mod(i1, i2));
			}
		}
		return r;
	}

	/** ��ѧ���㣺ȡ�� */
	public static DoubleDomain mod(DoubleDomain a, int x) {
		DoubleDomain r=null;
		if(a.unknown){
			r=DoubleDomain.getUnknownDomain();
			return r;
		}
		r = new DoubleDomain();
		for (DoubleInterval i : a.intervals) {
			r.mergeWith(DoubleInterval.mod(i, x));
		}
		return r;
	}	

	/** ��ѧ���㣺ȡ����- */
	public static DoubleDomain uminus(DoubleDomain a) {
		DoubleDomain r=null;
		if(a.unknown){
			r=DoubleDomain.getUnknownDomain();
			return r;
		}
		r = new DoubleDomain();
		for (DoubleInterval i1 : a.intervals) {
			r.mergeWith(DoubleInterval.uminus(i1));
		}
		return r;
	}

	/** ��ѧ���㣺sqrt */
	public static DoubleDomain sqrt(DoubleDomain a) {
		DoubleDomain r=null;
		if(a.unknown){
			r=DoubleDomain.getUnknownDomain();
			return r;
		}
		r = new DoubleDomain();
		for (DoubleInterval i1 : a.intervals) {
			r.mergeWith(DoubleInterval.sqrt(i1));
		}
		return r;
	}

	/** ��ѧ���㣺sqr */
	public static DoubleDomain sqr(DoubleDomain a) {
		DoubleDomain r=null;
		if(a.unknown){
			r=DoubleDomain.getUnknownDomain();
			return r;
		}
		r = new DoubleDomain();
		for (DoubleInterval i1 : a.intervals) {
			r.mergeWith(DoubleInterval.sqr(i1));
		}
		return r;
	}

	/** ��ѧ���㣺sin */
	public static DoubleDomain sin(DoubleDomain a) {
		DoubleDomain r=null;
		if(a.unknown){
			r=DoubleDomain.getUnknownDomain();
			return r;
		}
		r = new DoubleDomain();
		for (DoubleInterval i1 : a.intervals) {
			r.mergeWith(DoubleInterval.sin(i1));
		}
		return r;
	}

	/** ��ѧ���㣺cos */
	public static DoubleDomain cos(DoubleDomain a) {
		DoubleDomain r=null;
		if(a.unknown){
			r=DoubleDomain.getUnknownDomain();
			return r;
		}
		r = new DoubleDomain();
		for (DoubleInterval i1 : a.intervals) {
			r.mergeWith(DoubleInterval.cos(i1));
		}
		return r;
	}

	/** ��ѧ���㣺tan */
	public static DoubleDomain tan(DoubleDomain a) {
		DoubleDomain r=null;
		if(a.unknown){
			r=DoubleDomain.getUnknownDomain();
			return r;
		}
		r = new DoubleDomain();
		for (DoubleInterval i1 : a.intervals) {
			r.mergeWith(DoubleInterval.tan(i1));
		}
		return r;
	}

	/** ��ѧ���㣺asin */
	public static DoubleDomain asin(DoubleDomain a) {
		DoubleDomain r=null;
		if(a.unknown){
			r=DoubleDomain.getUnknownDomain();
			return r;
		}
		r = new DoubleDomain();
		for (DoubleInterval i1 : a.intervals) {
			r.mergeWith(DoubleInterval.asin(i1));
		}
		return r;
	}

	/** ��ѧ���㣺acos */
	public static DoubleDomain acos(DoubleDomain a) {
		DoubleDomain r=null;
		if(a.unknown){
			r=DoubleDomain.getUnknownDomain();
			return r;
		}
		r = new DoubleDomain();
		for (DoubleInterval i1 : a.intervals) {
			r.mergeWith(DoubleInterval.acos(i1));
		}
		return r;
	}

	/** ��ѧ���㣺atan */
	public static DoubleDomain atan(DoubleDomain a) {
		DoubleDomain r=null;
		if(a.unknown){
			r=DoubleDomain.getUnknownDomain();
			return r;
		}
		r = new DoubleDomain();
		for (DoubleInterval i1 : a.intervals) {
			r.mergeWith(DoubleInterval.atan(i1));
		}
		return r;
	}

	/** ��ѧ���㣺eָ������ */
	public static DoubleDomain exp(DoubleDomain a) {
		DoubleDomain r=null;
		if(a.unknown){
			r=DoubleDomain.getUnknownDomain();
			return r;
		}
		r = new DoubleDomain();
		for (DoubleInterval i1 : a.intervals) {
			r.mergeWith(DoubleInterval.exp(i1));
		}
		return r;
	}

	/** ��ѧ���㣺�������� */
	public static DoubleDomain log(DoubleDomain a) {
		DoubleDomain r=null;
		if(a.unknown){
			r=DoubleDomain.getUnknownDomain();
			return r;
		}
		r = new DoubleDomain();
		for (DoubleInterval i1 : a.intervals) {
			r.mergeWith(DoubleInterval.log(i1));
		}
		return r;
	}

	/** ��ѧ���㣺ָ������ */
	public static DoubleDomain power(DoubleDomain a, DoubleDomain b) {
		DoubleDomain r=null;
		if(a.unknown||b.unknown){
			r=DoubleDomain.getUnknownDomain();
			return r;
		}
		r = new DoubleDomain();
		for (DoubleInterval i1 : a.intervals) {
			for (DoubleInterval i2 : b.intervals) {
				r.mergeWith(DoubleInterval.power(i1, i2));
			}
		}
		return r;
	}

	/** ������ a��b */
	public static DoubleDomain union(DoubleDomain a, DoubleDomain b) {
		DoubleDomain r=null;
		/*if(a.unknown||b.unknown){
			r=DoubleDomain.getUnknownDomain();
			return r;
		}*/
		if(a.unknown){
			r=new DoubleDomain(b);
			return r;
		}
		if(b.unknown){
			r=new DoubleDomain(a);
			return r;		
		}
		r = new DoubleDomain(a);
		for (DoubleInterval i : b.intervals) {
			r.mergeWith(i);
		}
		return r;
	}

	/** ������ a��b */
	public static DoubleDomain intersect(DoubleDomain a, DoubleDomain b) {
		DoubleDomain r=null;
		if(a.unknown){
			r=new DoubleDomain(b);
			return r;
		}
		if(b.unknown){
			r=new DoubleDomain(a);
			return r;		
		}
		r = new DoubleDomain();
		for (DoubleInterval i1 : a.intervals) {
			for (DoubleInterval i2 : b.intervals) {
				DoubleInterval i3 = intersectionOf(i1, i2);
				r.mergeWith(i3);
			}
		}	
		return r;
	}

	/** ���ϼ��� e-a */
	public static DoubleDomain subtract(DoubleDomain e, DoubleDomain a) {
		DoubleDomain r = inverse(a);
		r = DoubleDomain.intersect(e, r);
		return r;
	}

	/** ����ȡ�� ~a */
	public static DoubleDomain inverse(DoubleDomain a) {
		DoubleDomain r=null;
		if(a.unknown){
			r=DoubleDomain.getUnknownDomain();
			return r;
		}
		r= new DoubleDomain();
		if (a.intervals.isEmpty()) {
			r.mergeWith(DoubleInterval.fullInterval());
		} else {
			Iterator interval_i = a.intervals.iterator();
			DoubleInterval first_interval = (DoubleInterval) interval_i.next();
			if (first_interval.getMin() != Double.NEGATIVE_INFINITY) {
				r.intervals.add(new DoubleInterval(Double.NEGATIVE_INFINITY, DoubleMath.prevfp(first_interval.getMin())));
			}
			double last_max = first_interval.getMax();
			while (interval_i.hasNext()) {
				DoubleInterval interval = (DoubleInterval) interval_i.next();
				r.intervals.add(new DoubleInterval(DoubleMath.nextfp(last_max), DoubleMath.prevfp(interval.getMin())));
				last_max = interval.getMax();
			}
			if (last_max != Double.POSITIVE_INFINITY) {
				r.intervals.add(new DoubleInterval(DoubleMath.nextfp(last_max), Double.POSITIVE_INFINITY));
			}
		}
		return r;
	}
}
