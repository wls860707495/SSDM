package softtest.domain.java;

/** ������ */
public class ReferenceDomain implements Domain {
	/** ֵ */
	private ReferenceValue value;
		
	/** unknown��ǣ�����ǰ���Ƿ�δ֪*/
	boolean unknown=false;
	
	/** ����unknown���*/
	public boolean getUnknown(){
		return this.unknown;
	}
	
	/** ����unknown���*/
	public void setUnknown(boolean unknown){
		this.unknown=unknown;
	}
	
	/** ��������*/
	public ReferenceDomain(ReferenceDomain domain) {
		this.value = domain.value;
		this.unknown=domain.unknown;
	}	
	
	/** �õ�һ��δ֪��*/
	public static ReferenceDomain getUnknownDomain(){
		ReferenceDomain r=new ReferenceDomain(ReferenceValue.NULL_OR_NOTNULL);
		r.unknown=true;
		return r;
	}
	
	/** �õ�һ��ȫ��*/
	public static ReferenceDomain getFullDomain(){
		ReferenceDomain r=new ReferenceDomain(ReferenceValue.NULL_OR_NOTNULL);
		return r;
	}
	
	/** �õ�һ������*/
	public static ReferenceDomain getEmptyDomain(){
		ReferenceDomain r=new ReferenceDomain(ReferenceValue.EMPTY);
		return r;
	}
	
	/** ��valueΪֵ���������� */
	public ReferenceDomain(ReferenceValue value) {
		this.value = value;
	}
	
	/** ����ֵ */
	public void setValue(ReferenceValue value) {
		this.value = value;
	}
	
	/** ���ֵ */
	public ReferenceValue getValue() {
		return value;
	}
	
	/** ���ϲ����� a��b */
	public static ReferenceDomain union(ReferenceDomain a, ReferenceDomain b) {
		ReferenceDomain r = null;
		/*if(a.unknown||b.unknown){
			r=ReferenceDomain.getUnknownDomain();
			return r;
		}*/
		if(a.unknown){
			if(!b.unknown&&b.getValue()==ReferenceValue.NOTNULL){
				//notnull��unknown ��ȡunknown
				r=ReferenceDomain.getUnknownDomain();
			}else{
				r=new ReferenceDomain(b);
			}
			return r;
		}
		if(b.unknown){
			if(!a.unknown&&a.getValue()==ReferenceValue.NOTNULL){
				//notnull��unknown ��ȡunknown
				r=ReferenceDomain.getUnknownDomain();
			}else{
				r=new ReferenceDomain(a);
			}
			return r;
		}
		switch(a.getValue()){
		case EMPTY:
			switch(b.getValue()){
			case EMPTY:
				r = new ReferenceDomain(ReferenceValue.EMPTY);
				break;
			case NULL:
				r = new ReferenceDomain(ReferenceValue.NULL);
				break;
			case NOTNULL:
				r = new ReferenceDomain(ReferenceValue.NOTNULL);
				break;
			case NULL_OR_NOTNULL:
				r = new ReferenceDomain(ReferenceValue.NULL_OR_NOTNULL);
				break;
			}
			break;
		case NULL:
			switch(b.getValue()){
			case EMPTY:
				r = new ReferenceDomain(ReferenceValue.NULL);
				break;
			case NULL:
				r = new ReferenceDomain(ReferenceValue.NULL);
				break;
			case NOTNULL:
				r = new ReferenceDomain(ReferenceValue.NULL_OR_NOTNULL);
				break;
			case NULL_OR_NOTNULL:
				r = new ReferenceDomain(ReferenceValue.NULL_OR_NOTNULL);
				break;
			}
			break;
		case NOTNULL:
			switch(b.getValue()){
			case EMPTY:
				r = new ReferenceDomain(ReferenceValue.NOTNULL);
				break;
			case NULL:
				r = new ReferenceDomain(ReferenceValue.NULL_OR_NOTNULL);
				break;
			case NOTNULL:
				r = new ReferenceDomain(ReferenceValue.NOTNULL);
				break;
			case NULL_OR_NOTNULL:
				r = new ReferenceDomain(ReferenceValue.NULL_OR_NOTNULL);
				break;
			}
			break;
		case NULL_OR_NOTNULL:
			switch(b.getValue()){
			case EMPTY:
				r = new ReferenceDomain(ReferenceValue.NULL_OR_NOTNULL);
				break;
			case NULL:
				r = new ReferenceDomain(ReferenceValue.NULL_OR_NOTNULL);
				break;
			case NOTNULL:
				r = new ReferenceDomain(ReferenceValue.NULL_OR_NOTNULL);
				break;
			case NULL_OR_NOTNULL:
				r = new ReferenceDomain(ReferenceValue.NULL_OR_NOTNULL);
				break;
			}
			break;
		}
		assert (r!=null);
		return r;
	}
	
	/** ���Ͻ����� a��b */
	public static ReferenceDomain intersect(ReferenceDomain a, ReferenceDomain b) {
		ReferenceDomain r = null;
		if(a.unknown){
			r=new ReferenceDomain(b);
			return r;
		}
		if(b.unknown){
			r=new ReferenceDomain(a);
			return r;
		}
		switch(a.getValue()){
		case EMPTY:
			switch(b.getValue()){
			case EMPTY:
				r = new ReferenceDomain(ReferenceValue.EMPTY);
				break;
			case NULL:
				r = new ReferenceDomain(ReferenceValue.EMPTY);
				break;
			case NOTNULL:
				r = new ReferenceDomain(ReferenceValue.EMPTY);
				break;
			case NULL_OR_NOTNULL:
				r = new ReferenceDomain(ReferenceValue.EMPTY);
				break;
			}
			break;
		case NULL:
			switch(b.getValue()){
			case EMPTY:
				r = new ReferenceDomain(ReferenceValue.EMPTY);
				break;
			case NULL:
				r = new ReferenceDomain(ReferenceValue.NULL);
				break;
			case NOTNULL:
				r = new ReferenceDomain(ReferenceValue.EMPTY);
				break;
			case NULL_OR_NOTNULL:
				r = new ReferenceDomain(ReferenceValue.NULL);
				break;
			}
			break;
		case NOTNULL:
			switch(b.getValue()){
			case EMPTY:
				r = new ReferenceDomain(ReferenceValue.EMPTY);
				break;
			case NULL:
				r = new ReferenceDomain(ReferenceValue.EMPTY);
				break;
			case NOTNULL:
				r = new ReferenceDomain(ReferenceValue.NOTNULL);
				break;
			case NULL_OR_NOTNULL:
				r = new ReferenceDomain(ReferenceValue.NOTNULL);
				break;
			}
			break;
		case NULL_OR_NOTNULL:
			switch(b.getValue()){
			case EMPTY:
				r = new ReferenceDomain(ReferenceValue.EMPTY);
				break;
			case NULL:
				r = new ReferenceDomain(ReferenceValue.NULL);
				break;
			case NOTNULL:
				r = new ReferenceDomain(ReferenceValue.NOTNULL);
				break;
			case NULL_OR_NOTNULL:
				r = new ReferenceDomain(ReferenceValue.NULL_OR_NOTNULL);
				break;
			}
			break;
		}
		assert (r!=null);
		return r;
	}
	
	/** ���ϼ��� e-a */
	public static ReferenceDomain subtract(ReferenceDomain e, ReferenceDomain a) {
		ReferenceDomain r = inverse(a);
		r = intersect(e, r);
		return r;
	}	
	
	/** ����ȡ�� ~a */
	public static ReferenceDomain inverse(ReferenceDomain a){
		ReferenceDomain r = null;
		if(a.unknown){
			r=new ReferenceDomain(a);
			return r;
		}
		switch (a.getValue()){
		case EMPTY:
			r = new ReferenceDomain(ReferenceValue.NULL_OR_NOTNULL);
			break;
		case NULL:
			r = new ReferenceDomain(ReferenceValue.NOTNULL);
			break;
		case NOTNULL:
			r = new ReferenceDomain(ReferenceValue.NULL);
			break;
		case NULL_OR_NOTNULL:
			r = new ReferenceDomain(ReferenceValue.EMPTY);
			break;
		}		
		assert (r!=null);
		return r;
	}
	
	/** ��ӡ */
	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		if(this.unknown){
			b.append("unknown");
		}else{
			b.append(value);
		}
		return b.toString();
	}
	
	/** ���ַ��������� */
	public static ReferenceDomain valueOf(String str){
		ReferenceDomain r=null;
		if(str.equalsIgnoreCase("unknown")){
			r=ReferenceDomain.getUnknownDomain();
		}else{
			r =new ReferenceDomain(ReferenceValue.valueOf(str));
		}
		return r;
	}
	
	/** ���ֵ��ȣ�����Ϊ��� */
	@Override
	public boolean equals(Object o) {
		if ((o == null) || !(o instanceof ReferenceDomain)) {
			return false;
		}
		if (this == o) {
			return true;
		}
		ReferenceDomain x = (ReferenceDomain) o;
		if(x.unknown&&this.unknown){
			return true;
		}
		if(!x.unknown&&!this.unknown&&value == x.value) {
			return true;
		}
		return false;
	}
}
