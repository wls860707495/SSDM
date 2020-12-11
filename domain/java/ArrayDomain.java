package softtest.domain.java;

import java.util.*;

/** ��������򣬸��������ά�� */
public class ArrayDomain implements Domain {
	/** �����ά����ÿһά�ĳ��� */
	ArrayList<IntegerDomain> dimensions;

	/** ����ͨ����������ֵ�ĸ�ֵ����Ϊ�� */
	//add by zhouhb
	//2011.5.8
	private ReferenceDomain value;

	/** ȱʡ���캯�� */
	public ArrayDomain() {
		dimensions = new ArrayList<IntegerDomain>();
		value = new ReferenceDomain(ReferenceValue.NULL_OR_NOTNULL);//added by zhb
		value.unknown=true;//added by zhb
	}

	/** ����ά��Ϊnum�������� */
	public ArrayDomain(int num) {
		this();
		for (int i = 0; i < num; i++) {
			dimensions.add(IntegerDomain.getUnknownDomain());
		}
		//by yang:value = new ReferenceDomain(ReferenceValue.NULL_OR_NOTNULL);//added by zhb
		value.unknown=true;//added by yang 2011-06-07 14:48
		
	}


		
	public static ArrayDomain getUnknownDomain(){
		ArrayDomain r= new ArrayDomain(1);
		r.value=new ReferenceDomain(ReferenceValue.NULL_OR_NOTNULL);//added by zhb
		r.value.unknown=true;//added by zhb
		return r;
	}

	/**
	 * @author yang
	 * @since 2011-05-13 16:48
	 * @param r
	 * @return ת��Ϊ�������͵��������͵�ֵ
	 */
	public static ArrayDomain getRefToArrayDomain(ReferenceDomain r) {
		// TODO Auto-generated method stub
		ArrayDomain array= new ArrayDomain(1);
		array.value=new ReferenceDomain(r);		
		return array;
	}
	
	
	public static ArrayDomain getFullDomain(){
		ArrayDomain r= new ArrayDomain(1);
		r.value=new ReferenceDomain(ReferenceValue.NULL_OR_NOTNULL);
		return r;
	}
	
	public ArrayDomain(ArrayDomain domain){
		this();
		for (int i = 0; i < domain.getAllDimensions().size(); i++) {
			dimensions.add(domain.getDimension(i));
		}
		//value = new ReferenceDomain(ReferenceValue.NOTNULL);//added by zhb
		value = new ReferenceDomain(ReferenceValue.NULL_OR_NOTNULL);//ADDED BY YANG
	}
	


	/** ����indexά�ĳ��ȣ���û�г�ʼ����������Χʱ���ȷ���-1 */
	public IntegerDomain getDimension(int index) {
		if (index >= dimensions.size()) {
			return IntegerDomain.getUnknownDomain();//-1;
		} else {
			return dimensions.get(index);
		}
	}
	
	/** ����indexά�ĳ���Ϊd */
	public void setDimension(int index, IntegerDomain d) {
		if(index>=dimensions.size()){
			for(int i=0;i<=index-dimensions.size();i++){
				dimensions.add(IntegerDomain.getUnknownDomain());
			}
		}
		if(!d.isEmpty()&&d.jointoOneInterval().getMax()==Long.MAX_VALUE){
			d=IntegerDomain.getUnknownDomain();
		}
		dimensions.set(index, d);
	}
	
	/** ��������ά */
	public ArrayList<IntegerDomain> getAllDimensions(){
		return dimensions;
	}
	
	/** ��������ά */
	public void setAllDimensions(ArrayList<IntegerDomain> dimensions){
	    this.dimensions=dimensions;
	 }

	  //�ܺ粮 10:12:39 
	public ReferenceDomain getvalue(){
		return value;
		}

		public void setvalue(ReferenceDomain value){
		this.value=value;
		}

	
	
	/** ������Ľ���ά��ȡ���ֵ����ÿһά��ȡ��Сֵ */
	public static ArrayDomain intersect(ArrayDomain a, ArrayDomain b) {
		ArrayDomain r=null;
		// ȡά�������Ǹ�����ÿһά��ȡ��
		int max = a.dimensions.size() < b.dimensions.size() ? b.dimensions.size() : a.dimensions.size();
		int min = a.dimensions.size() > b.dimensions.size() ? b.dimensions.size() : a.dimensions.size();
		
		r = new ArrayDomain(max);

		for (int i = 0; i < min; i++) {
			r.setDimension(i, IntegerDomain.intersect(a.dimensions.get(i),b.dimensions.get(i)));
		}
		
		for(int i=min;i<max;i++){
			r.setDimension(i,a.dimensions.size() > b.dimensions.size()?a.dimensions.get(i):b.dimensions.get(i));
		}
		r.value=ReferenceDomain.intersect(a.value, b.value);//added by zhb
		return r;
	}
	
	/** ������Ĳ���ά��ȡ���ֵ����ÿһά��ȡ���ֵ */
	public static ArrayDomain union(ArrayDomain a, ArrayDomain b) {
		ArrayDomain r=null;
		// ȡά�������Ǹ�����ÿһά��ȡ��
		int max = a.dimensions.size() < b.dimensions.size() ? b.dimensions.size() : a.dimensions.size();
		int min = a.dimensions.size() > b.dimensions.size() ? b.dimensions.size() : a.dimensions.size();
		
		r = new ArrayDomain(max);

		for (int i = 0; i < min; i++) {
			r.setDimension(i, IntegerDomain.union(a.dimensions.get(i),b.dimensions.get(i)));
		}
		
		for(int i=min;i<max;i++){
			r.setDimension(i,a.dimensions.size() > b.dimensions.size()?a.dimensions.get(i):b.dimensions.get(i));
		}
		r.value=ReferenceDomain.union(a.value, b.value);//added by zhb
		return r;
	}
	

	
	public static ArrayDomain subtract(ArrayDomain a,ArrayDomain b){
		ArrayDomain r=null;

		int max = a.dimensions.size() < b.dimensions.size() ? b.dimensions.size() : a.dimensions.size();
		int min = a.dimensions.size() > b.dimensions.size() ? b.dimensions.size() : a.dimensions.size();
		
		r = new ArrayDomain(max);

		for (int i = 0; i < min; i++) {
			r.setDimension(i, IntegerDomain.subtract(a.dimensions.get(i),b.dimensions.get(i)));
		}
		
		for(int i=min;i<max;i++){
			r.setDimension(i,a.dimensions.size() > b.dimensions.size()?a.dimensions.get(i):IntegerDomain.getUnknownDomain());
		}
		r.value=ReferenceDomain.subtract(a.value, b.value);
		return r;
	}
	

	
	/** ��ӡ */
	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("Array");
		for (int i = 0; i < dimensions.size(); i++) {
			b.append("{"+dimensions.get(i)+"}");
		}
		b.append(value.toString());//added by zhb
		return b.toString();
	}
	


	
	public boolean isEmpty(){
		for(int i=0;i < dimensions.size(); i++){
			if(dimensions.get(i).isEmpty()){
				return true;
			}
		}
		return false;
	}


	
	public static ArrayDomain getEmptyDomain(){
		ArrayDomain ret=new ArrayDomain(1);
		ret.setDimension(0, IntegerDomain.getEmptyDomain());
		return ret;
	}


	/** �ж������������Ƿ���ȣ�ά����ȼ�ÿһά����Ҳ��ȵ���������Ϊ���
	 * ��ӵĴ��������ж�������������������Ƿ���� added by yang */
	@Override
	public boolean equals(Object o) {
		if ((o == null) || !(o instanceof ArrayDomain)) {
			return false;
		}
		if (this == o) {
			return true;
		}
		ArrayDomain x = (ArrayDomain) o;
		
		//added by yang 2011-05-13
		if(dimensions.get(0)!=null && x.dimensions.get(0)!=null && dimensions.get(0).getUnknown()&& x.dimensions.get(0).getUnknown()){
			if(this.value.equals(x.value))
				return true;
		}
			
		
		else
		//end yang
		{
			if (dimensions.size() == x.dimensions.size()) {
			for (int i = 0; i < dimensions.size(); i++) {
				if (!dimensions.get(i).equals(x.dimensions.get(i))) {
					return false;
				}
			}
			return true;
		}//added by yang
		}
		return false;
	}

	
}
