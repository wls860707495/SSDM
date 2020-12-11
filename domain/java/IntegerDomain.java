package softtest.domain.java;

import java.util.*;

/** ������ */
public class IntegerDomain implements Domain {
	/** ���伯�ϣ���С���󱣳�˳��� */
	private TreeSet<IntegerInterval> intervals = new TreeSet<IntegerInterval>();
	
	/** unknown��ǣ�����ǰ���Ƿ�δ֪*/
	boolean unknown=false;
	
	/** ����unknown���*/
	public boolean getUnknown(){
		return unknown;
	}
	
	/** ����unknown���*/
	public void setUnknown(boolean unknown){
		this.unknown=unknown;
	}		

	/** ȱʡ���캯�� */
	public IntegerDomain() {
	}
	
	/** �õ�һ��δ֪��*/
	public static IntegerDomain getUnknownDomain(){
		IntegerDomain r=new IntegerDomain();
		r.setUnknown(true);
		return r;
	}
	
	/** �õ�һ��ȫ��*/
	public static IntegerDomain getFullDomain(){
		IntegerDomain r=new IntegerDomain(Long.MIN_VALUE,Long.MAX_VALUE);
		return r;
	}
	
	/** �õ�һ������*/
	public static IntegerDomain getEmptyDomain(){
		IntegerDomain r=new IntegerDomain(Long.MAX_VALUE,Long.MIN_VALUE);
		return r;
	}
	
	/** �õ����е����伯��*/
	public TreeSet<IntegerInterval> getIntervals(){
		return intervals;
	}

	/** ��ָ���ĵ�һ����[min,max]������minexcluded��maxexcludedָ��min��max�Ƿ�ȥ����Ϊtrue�Ļ���Ϊ�����䣩 */
	public IntegerDomain(long min, long max, boolean minexcluded, boolean maxexcluded) {
		IntegerInterval interval = new IntegerInterval(min, max, minexcluded, maxexcluded);
		if (!interval.isEmpty()) {
			intervals.add(interval);
		}
	}
	
	/** ��ָ���ĵ�һ����[min,max]������*/
	public IntegerDomain(long min,long max){
		this(min,max,false,false);
	}

	/** �������ָ�����伯�ϵ��� */
	public IntegerDomain(IntegerInterval[] intervals) {
		for (int i = 0; i < intervals.length; i++) {
			this.mergeWith(intervals[i]);
		}
	}

	/** ����ֻ����һ��ָ��������� */
	public IntegerDomain(IntegerInterval interval) {
		this.mergeWith(interval);
	}

	/** �������캯�� */
	public IntegerDomain(IntegerDomain domain) {
		for (IntegerInterval interval : domain.intervals) {
			if (!interval.isEmpty()) {
				this.intervals.add(new IntegerInterval(interval));
			}
		}
		this.unknown=domain.unknown;
	}

	/** �ж����Ƿ�Ϊ�� */
	public boolean isEmpty() {
		if(unknown){
			return false;
		}
		IntegerDomain d = new IntegerDomain(this);
		if (d.intervals.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	
	/** �жϵ�ǰ���Ƿ������x*/
	public boolean contains(int x){
		for (IntegerInterval interval : intervals) {
			if(interval.contains(x)){
				return true;
			}
		}
		return false;
	}
	
	/** �жϵ�ǰ���Ƿ������x*/
	public boolean contains(IntegerDomain x){
		IntegerDomain temp=IntegerDomain.union(this, x);
		if(temp.equals(this)){
			return true;
		}
		return false;
	}

	/** �ж����Ƿ�ֻ����һ����ֵ */
	public boolean isCanonical() {
		IntegerDomain d = new IntegerDomain(this);
		if (d.intervals.size() == 1) {
			for (IntegerInterval interval : d.intervals) {
				if (interval.isCanonical()) {
					return true;
				}
			}
		}
		return false;
	}

	/** �ж��������Ƿ���ȣ�ÿ��������ȱ���Ϊ��������� */
	@Override
	public boolean equals(Object o) {
		if ((o == null) || !(o instanceof IntegerDomain)) {
			return false;
		}
		if (this == o) {
			return true;
		}
		IntegerDomain x = (IntegerDomain) o;
		
		if(x.unknown&&this.unknown){
			return true;
		}
		if(x.unknown||this.unknown){
			return false;
		}
		
		if(isEmpty()&&x.isEmpty()){
			return true;
		}

		IntegerDomain a = new IntegerDomain(this), b = new IntegerDomain(x);
		if (a.intervals.size() == b.intervals.size()) {
			Iterator<IntegerInterval> ia = a.intervals.iterator(), ib = b.intervals.iterator();
			while (ia.hasNext()) {
				IntegerInterval interval1 = ia.next(), interval2 = ib.next();
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
		if(this.unknown){
			b.append("unknown");
		}else if (intervals.isEmpty()) {
			b.append("emptydomain");
		} else {
			boolean first = true;
			for (IntegerInterval interval : intervals) {
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

	/** ���һ�����������С���䣬����Ϊһ������ */
	public IntegerInterval jointoOneInterval() {
		if (this.unknown) {
			return IntegerInterval.fullInterval();
		}
		
		long tmax = 0, tmin = 0;		
		if (intervals.size() > 0) {
			tmax = intervals.last().getMax();
			tmin = intervals.first().getMin();
		} else {
			tmax = Long.MIN_VALUE;
			tmin = Long.MAX_VALUE;
		}
		return new IntegerInterval(tmin, tmax);
	}


	/** ���µ��������ϲ������Ǻϲ�ʱ�����������ں� */
	public void mergeWith(IntegerInterval newInterval) {
		// �ռ����к����������غϵ����䣬������Ӽ�����ɾ��
		if (newInterval.isEmpty()) {
			return;
		}
		List<IntegerInterval> intersectors = new ArrayList<IntegerInterval>();
		List<IntegerInterval> toRemove = new ArrayList<IntegerInterval>();
		for (IntegerInterval interval : intervals) {
			if (interval.isEmpty() || IntegerInterval.canBeJoined(interval, newInterval)) {
				if (!interval.isEmpty()) {
					intersectors.add(interval);
				}
				toRemove.add(interval);
			}
		}
		intervals.removeAll(toRemove);

		// �������������intersectors�е�������кϲ�
		long min = newInterval.getMin();
		long max = newInterval.getMax();

		for (IntegerInterval intersector : intersectors) {
			if (intersector.getMin() < newInterval.getMin()) {
				min = intersector.getMin();
			}
			if (intersector.getMax() > newInterval.getMax()) {
				max = intersector.getMax();
			}
		}
		// ����������
		intervals.add(new IntegerInterval(min, max));
	}

	/** ȡ��������Ľ���������һ���µ����� */
	private static IntegerInterval intersectionOf(IntegerInterval i1, IntegerInterval i2) {
		long new_min;
		long new_max;
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

		return new IntegerInterval(new_min, new_max);
	}

	/** ��ѧ���㣺�ӷ� */
	public static IntegerDomain add(IntegerDomain a, IntegerDomain b) {
		IntegerDomain r=null;
		if(a.unknown||b.unknown){
			r=IntegerDomain.getUnknownDomain();
			return r;
		}
		r=new IntegerDomain();
		for (IntegerInterval i1 : a.intervals) {
			for (IntegerInterval i2 : b.intervals) {
				r.mergeWith(IntegerInterval.add(i1, i2));
			}
		}
		return r;
	}

	/** ��ѧ���㣺�ӷ� */
	public static IntegerDomain add(IntegerDomain a, int x) {
		IntegerDomain r=null;
		if(a.unknown){
			r=IntegerDomain.getUnknownDomain();
			return r;
		}
		r=new IntegerDomain();
		for (IntegerInterval i : a.intervals) {
			r.mergeWith(IntegerInterval.add(i, x));
		}
		return r;
	}

	/** ��ѧ���㣺���� */
	public static IntegerDomain sub(IntegerDomain a, IntegerDomain b) {
		IntegerDomain r=null;
		if(a.unknown||b.unknown){
			r=IntegerDomain.getUnknownDomain();
			return r;
		}
		r=new IntegerDomain();
		for (IntegerInterval i1 : a.intervals) {
			for (IntegerInterval i2 : b.intervals) {
				r.mergeWith(IntegerInterval.sub(i1, i2));
			}
		}
		return r;
	}

	/** ��ѧ���㣺���� */
	public static IntegerDomain sub(IntegerDomain a, int x) {
		IntegerDomain r=null;
		if(a.unknown){
			r=IntegerDomain.getUnknownDomain();
			return r;
		}
		r=new IntegerDomain();
		for (IntegerInterval i : a.intervals) {
			r.mergeWith(IntegerInterval.sub(i, x));
		}
		return r;
	}

	/** ��ѧ���㣺�˷� */
	public static IntegerDomain mul(IntegerDomain a, IntegerDomain b) {
		IntegerDomain r=null;
		if(a.unknown||b.unknown){
			r=IntegerDomain.getUnknownDomain();
			return r;
		}
		r=new IntegerDomain();
		for (IntegerInterval i1 : a.intervals) {
			for (IntegerInterval i2 : b.intervals) {
				r.mergeWith(IntegerInterval.mul(i1, i2));
			}
		}
		return r;
	}

	/** ��ѧ���㣺�˷� */
	public static IntegerDomain mul(IntegerDomain a, int x) {
		IntegerDomain r=null;
		if(a.unknown){
			r=IntegerDomain.getUnknownDomain();
			return r;
		}
		r=new IntegerDomain();
		for (IntegerInterval i : a.intervals) {
			r.mergeWith(IntegerInterval.mul(i, x));
		}
		return r;
	}

	/** ��ѧ���㣺���� */
	public static IntegerDomain div(IntegerDomain a, IntegerDomain b) {
		IntegerDomain r=null;
		if(a.unknown||b.unknown){
			r=IntegerDomain.getUnknownDomain();
			return r;
		}
		r=new IntegerDomain();
		for (IntegerInterval i1 : a.intervals) {
			for (IntegerInterval i2 : b.intervals) {
				r.mergeWith(IntegerInterval.div(i1, i2));
			}
		}
		return r;
	}

	/** ��ѧ���㣺���� */
	public static IntegerDomain div(IntegerDomain a, int x) {
		IntegerDomain r=null;
		if(a.unknown){
			r=IntegerDomain.getUnknownDomain();
			return r;
		}
		r=new IntegerDomain();
		for (IntegerInterval i : a.intervals) {
			r.mergeWith(IntegerInterval.div(i, x));
		}
		return r;
	}

	/** ��ѧ���㣺����������ȷ�� */
	public static IntegerDomain mod(IntegerDomain a, IntegerDomain b) {
		IntegerDomain r=null;
		if(a.unknown||b.unknown){
			r=IntegerDomain.getUnknownDomain();
			return r;
		}
		r=new IntegerDomain();
		for (IntegerInterval i1 : a.intervals) {
			for (IntegerInterval i2 : b.intervals) {
				r.mergeWith(IntegerInterval.mod(i1, i2));
			}
		}
		return r;
	}

	/** ��ѧ���㣺���� */
	public static IntegerDomain mod(IntegerDomain a, int x) {
		IntegerDomain r=null;
		if(a.unknown){
			r=IntegerDomain.getUnknownDomain();
			return r;
		}
		r=new IntegerDomain();
		for (IntegerInterval i : a.intervals) {
			r.mergeWith(IntegerInterval.mod(i, x));
		}
		return r;
	}

	/** ȡ�� */
	public static IntegerDomain uminus(IntegerDomain a) {
		IntegerDomain r=null;
		if(a.unknown){
			r=IntegerDomain.getUnknownDomain();
			return r;
		}
		r=new IntegerDomain();
		for (IntegerInterval i1 : a.intervals) {
			r.mergeWith(IntegerInterval.uminus(i1));
		}
		return r;
	}

	/** �����󲢼� r=a��b */
	public static IntegerDomain union(IntegerDomain a, IntegerDomain b) {
		IntegerDomain r=null;
		/*if(a.unknown||b.unknown){
			r=IntegerDomain.getUnknownDomain();
			return r;
		}*/
		if(a.unknown){
			r=new IntegerDomain(b);
			return r;
		}
		if(b.unknown){
			r=new IntegerDomain(a);
			return r;		
		}
		r=new IntegerDomain(a);
		for (IntegerInterval i : b.intervals) {
			r.mergeWith(i);
		}
		return r;
	}

	/** �����󽻼� r=a��b */
	public static IntegerDomain intersect(IntegerDomain a, IntegerDomain b) {
		IntegerDomain r=null;
		if(a.unknown){
			r=new IntegerDomain(b);
			return r;
		}
		if(b.unknown){
			r=new IntegerDomain(a);
			return r;		
		}
		r = new IntegerDomain();
		for (IntegerInterval i1 : a.intervals) {
			for (IntegerInterval i2 : b.intervals) {
				IntegerInterval i3 = intersectionOf(i1, i2);
				r.mergeWith(i3);
			}
		}
		return r;
	}

	/** ���ϼ������ r=e-a */
	public static IntegerDomain subtract(IntegerDomain e, IntegerDomain a) {
		IntegerDomain r = inverse(a);
		r = IntegerDomain.intersect(e, r);
		return r;
	}

	/** �����󲹼� r=~a */
	public static IntegerDomain inverse(IntegerDomain a) {
		IntegerDomain r=null;
		if(a.unknown){
			r=IntegerDomain.getUnknownDomain();
			return r;
		}
		
		r= new IntegerDomain();
		if (a.intervals.isEmpty()) {
			r.mergeWith(IntegerInterval.fullInterval());
		} else {
			Iterator interval_i = a.intervals.iterator();
			IntegerInterval first_interval = (IntegerInterval) interval_i.next();
			if (first_interval.getMin() != Long.MIN_VALUE) {
				r.intervals.add(new IntegerInterval(Long.MIN_VALUE, first_interval.getMin() - 1));
			}
			long last_max = first_interval.getMax();
			while (interval_i.hasNext()) {
				IntegerInterval interval = (IntegerInterval) interval_i.next();
				if(last_max!=Long.MAX_VALUE){
					r.intervals.add(new IntegerInterval(last_max + 1, interval.getMin() - 1));
				}
				last_max = interval.getMax();
			}
			if (last_max != Long.MAX_VALUE) {
				r.intervals.add(new IntegerInterval(last_max + 1, Long.MAX_VALUE));
			}
		}

		return r;
	}

	public static void main(String args[]) {
		
		IntegerDomain interval=new IntegerDomain(Long.MIN_VALUE,Long.MAX_VALUE);
		interval.mergeWith(new IntegerInterval(Long.MIN_VALUE,2));
		
		//IntegerDomain i = new IntegerDomain(0,0, false, false);
		//System.out.println(i);
		//i=IntegerDomain.union(i,new IntegerDomain(Long.MIN_VALUE, 11, false, false));
		System.out.println(interval);

		//System.out.print(IntegerDomain.subtract(i, new IntegerDomain(0, 7, false, false)));
	}
}
