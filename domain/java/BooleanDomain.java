package softtest.domain.java;

/** ������ */
public class BooleanDomain implements Domain {
	/** ֵ */
	private BooleanValue value;
	
	/** unknown��ǣ�����ǰ���Ƿ�δ֪*/
	boolean unknown=false;
	
	/** �õ�һ��δ֪��*/
	public static BooleanDomain getUnknownDomain(){
		BooleanDomain r= new BooleanDomain(BooleanValue.TRUE_OR_FALSE);
		r.setUnknown(true);
		return r;
	}
	
	/** �õ�һ��ȫ��*/
	public static BooleanDomain getFullDomain(){
		BooleanDomain r= new BooleanDomain(BooleanValue.TRUE_OR_FALSE);
		return r;
	}
	
	/** �õ�һ������*/
	public static BooleanDomain getEmptyDomain(){
		BooleanDomain r= new BooleanDomain(BooleanValue.EMPTY);
		return r;
	}
	
	/** ����unknown���*/
	public void setUnknown(boolean unknown){
		this.unknown=unknown;
	}
	
	/** ��������*/
	public BooleanDomain(BooleanDomain domain){
		this.value=domain.value;
		this.unknown=domain.unknown;
	}
	
	/** ����unknown���*/
	public boolean getUnknown(){
		return unknown;
	}

	/** ��valueΪֵ���첼���� */
	public BooleanDomain(BooleanValue value) {
		this.value = value;
	}

	/** ���ֵ */
	public BooleanValue getValue() {
		return value;
	}

	/** ����ֵ */
	public void setValue(BooleanValue value) {
		this.value = value;
		unknown=false;
	}

	/** ���ϲ����� a��b */
	public static BooleanDomain union(BooleanDomain a, BooleanDomain b) {
		BooleanDomain r = null;
		/*if(a.unknown||b.unknown){
			r=BooleanDomain.getUnknownDomain();
			return r;
		}*/
		if(a.unknown){
			r=new BooleanDomain(b);
			return r;
		}
		if(b.unknown){
			r=new BooleanDomain(a);
			return r;
		}
		switch (a.getValue()){
		case EMPTY:
			switch (b.getValue()){
			case EMPTY:
				r = new BooleanDomain(BooleanValue.EMPTY);
				break;
			case FALSE:
				r = new BooleanDomain(BooleanValue.FALSE);
				break;
			case TRUE:
				r = new BooleanDomain(BooleanValue.TRUE);
				break;
			case TRUE_OR_FALSE:
				r = new BooleanDomain(BooleanValue.TRUE_OR_FALSE);
				break;
			}
			break;
		case FALSE:
			switch (b.getValue()){
			case EMPTY:
				r = new BooleanDomain(BooleanValue.FALSE);
				break;
			case FALSE:
				r = new BooleanDomain(BooleanValue.FALSE);
				break;
			case TRUE:
				r = new BooleanDomain(BooleanValue.TRUE_OR_FALSE);
				break;
			case TRUE_OR_FALSE:
				r = new BooleanDomain(BooleanValue.TRUE_OR_FALSE);
				break;
			}
			break;
		case TRUE:
			switch (b.getValue()){
			case EMPTY:
				r = new BooleanDomain(BooleanValue.TRUE);
				break;
			case FALSE:
				r = new BooleanDomain(BooleanValue.TRUE_OR_FALSE);
				break;
			case TRUE:
				r = new BooleanDomain(BooleanValue.TRUE);
				break;
			case TRUE_OR_FALSE:
				r = new BooleanDomain(BooleanValue.TRUE_OR_FALSE);
				break;
			}
			break;
		case TRUE_OR_FALSE:
			switch (b.getValue()){
			case EMPTY:
				r = new BooleanDomain(BooleanValue.TRUE_OR_FALSE);
				break;
			case FALSE:
				r = new BooleanDomain(BooleanValue.TRUE_OR_FALSE);
				break;
			case TRUE:
				r = new BooleanDomain(BooleanValue.TRUE_OR_FALSE);
				break;
			case TRUE_OR_FALSE:
				r = new BooleanDomain(BooleanValue.TRUE_OR_FALSE);
				break;
			}
			break;
		}
		assert (r!=null);
		return r;
	}
	
	/** ���Ͻ����� a��b */
	public static BooleanDomain intersect(BooleanDomain a, BooleanDomain b) {
		BooleanDomain r = null;
		if(a.unknown){
			r=new BooleanDomain(b);
			return r;
		}
		if(b.unknown){
			r=new BooleanDomain(a);
			return r;
		}
		switch (a.getValue()){
		case EMPTY:
			switch (b.getValue()){
			case EMPTY:
				r = new BooleanDomain(BooleanValue.EMPTY);
				break;
			case FALSE:
				r = new BooleanDomain(BooleanValue.EMPTY);
				break;
			case TRUE:
				r = new BooleanDomain(BooleanValue.EMPTY);
				break;
			case TRUE_OR_FALSE:
				r = new BooleanDomain(BooleanValue.EMPTY);
				break;
			}
			break;
		case FALSE:
			switch (b.getValue()){
			case EMPTY:
				r = new BooleanDomain(BooleanValue.EMPTY);
				break;
			case FALSE:
				r = new BooleanDomain(BooleanValue.FALSE);
				break;
			case TRUE:
				r = new BooleanDomain(BooleanValue.EMPTY);
				break;
			case TRUE_OR_FALSE:
				r = new BooleanDomain(BooleanValue.FALSE);
				break;
			}
			break;
		case TRUE:
			switch (b.getValue()){
			case EMPTY:
				r = new BooleanDomain(BooleanValue.EMPTY);
				break;
			case FALSE:
				r = new BooleanDomain(BooleanValue.EMPTY);
				break;
			case TRUE:
				r = new BooleanDomain(BooleanValue.TRUE);
				break;
			case TRUE_OR_FALSE:
				r = new BooleanDomain(BooleanValue.TRUE);
				break;
			}
			break;
		case TRUE_OR_FALSE:
			switch (b.getValue()){
			case EMPTY:
				r = new BooleanDomain(BooleanValue.EMPTY);
				break;
			case FALSE:
				r = new BooleanDomain(BooleanValue.FALSE);
				break;
			case TRUE:
				r = new BooleanDomain(BooleanValue.TRUE);
				break;
			case TRUE_OR_FALSE:
				r = new BooleanDomain(BooleanValue.TRUE_OR_FALSE);
				break;
			}
			break;
		}
		assert (r!=null);
		return r;
	}

	/** ���ϼ��� e-a */
	public static BooleanDomain subtract(BooleanDomain e, BooleanDomain a) {
		BooleanDomain r = inverse(a);
		r = intersect(e, r);
		return r;
	}	
	
	/** ����ȡ�� ~a */
	public static BooleanDomain inverse(BooleanDomain a){
		BooleanDomain r = null;
		if(a.unknown){
			r=new BooleanDomain(a);
			return r;
		}
		switch (a.getValue()){
		case EMPTY:
			r = new BooleanDomain(BooleanValue.TRUE_OR_FALSE);
			break;
		case FALSE:
			r = new BooleanDomain(BooleanValue.TRUE);
			break;
		case TRUE:
			r = new BooleanDomain(BooleanValue.FALSE);
			break;
		case TRUE_OR_FALSE:
			r = new BooleanDomain(BooleanValue.EMPTY);
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
	public static BooleanDomain valueOf(String str){
		BooleanDomain r=null;
		if(str.equalsIgnoreCase("unknown")){
			r=BooleanDomain.getUnknownDomain();
		}else{
			r =new BooleanDomain(BooleanValue.valueOf(str));
		}
		return r;
	}
	
	/** ����������ֵ��ȣ�����Ϊ��� */
	@Override
	public boolean equals(Object o) {
		if ((o == null) || !(o instanceof BooleanDomain)) {
			return false;
		}
		if (this == o) {
			return true;
		}
		BooleanDomain x = (BooleanDomain) o;
		if(x.unknown&&this.unknown){
			return true;
		}
		if (!x.unknown&&!this.unknown&&value == x.value) {
			return true;
		}
		return false;
	}
}
