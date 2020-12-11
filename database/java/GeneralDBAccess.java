package softtest.database.java;

import java.io.*;
import java.util.*;

import com.healthmarketscience.jackcess.*;

public class GeneralDBAccess extends DBAccess {
	private Database db = null;
	
	/** �ļ����� */
	private static void copyFileToFile(String F1, String F2) {
		// ���ļ��Կ�,��F1������F2,��F2������ᱻ����;�������κ��ļ�.
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			F1 = F1.replace('\\', File.separatorChar).replace('/', File.separatorChar);
			F2 = F2.replace('\\', File.separatorChar).replace('/', File.separatorChar);
			
			fis = new FileInputStream(F1); // �����ļ�������
			fos = new FileOutputStream(F2);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
		} catch (FileNotFoundException ex) {
			//ex.printStackTrace();
			throw new RuntimeException("File not found" ,ex);
		} catch (IOException ex) {
			//ex.printStackTrace();
			throw new RuntimeException("IO Exception",ex);
		} finally {
			try {
				if (fis != null)
					fis.close();
				if (fos != null)
					fos.close();
			} catch (IOException ex) {
				//ex.printStackTrace();
				throw new RuntimeException("IO Exception",ex);
			}
		}
	}
	
	/** ���mdb�ļ������� */
	private static void clearData(String pathname) {
		try {
			pathname = pathname.replace('\\', File.separatorChar).replace('/', File.separatorChar);
			
			Database db = Database.open(new File(pathname));
			Table ipTable = db.getTable("IP");
			ipTable.reset();
			while (ipTable.getRowCount() > 0) {
				ipTable.deleteCurrentRow();
			}
			db.close();
		} catch (IOException e) {			
			throw new RuntimeException("error occurred when clearing the mdb file \""+pathname+"\"",e);
		}
	}
	
	/** ����һ���µĿյ�mdb�ļ� */
	public static void createMdbFile(String pathname) {
		// ����һ������
		copyFileToFile("..\\set\\Dbtemplate.Mdb", pathname);
		clearData(pathname);
	}
	
	/** ��ȡɨ������ */
	public static List<DBConfig> getScanTypes(String pathname){
		ArrayList<DBConfig> list=new ArrayList<DBConfig>();
		
		try {
			pathname = pathname.replace('\\', File.separatorChar).replace('/', File.separatorChar);
			
			Database db = Database.open(new File(pathname));
			Table confTable = db.getTable("ScanSet");
			confTable.reset();
			
			while (true) {
				Map<String,Object> row = confTable.getNextRow();
				if (row == null) {
					break;
				}
				
				if (row.get("ScanFlag") instanceof Boolean && row.get("ScanFlag").equals(Boolean.TRUE)) {					
					DBConfig c=new DBConfig();
					c.defect=(String)row.get("Defect");
					c.category=(String)row.get("Category");					
					list.add(c);
				}
			}
		}  catch (IOException e) {			
			throw new RuntimeException("error occurred when scan the mdb file \""+pathname+"\"",e);
		}
		return list;
	}

	/** �����ݿ����� */
	@Override
	public void openDataBase(String pathname) {
		if (db != null) {
			return;
		}
		File file = new File(pathname);
		if (!file.exists()) {
			GeneralDBAccess.createMdbFile(pathname);
		}		
		
		try {
			db = Database.open(file);
		} catch (IOException e) {
			throw new RuntimeException("Access database connect error", e);
		}
	}

	/** �ر����ݿ����� */
	@Override
	public void closeDataBase() {
		if (db != null) {
			try {
				db.close();
			} catch (IOException e) {
				throw new RuntimeException("Access database connect error", e);
			}
		}
		db = null;
	}

	
	@Override
	public void exportErrorDataBuff(String eclass, String ekind, String pathname, String variable, int beginline, int errorline, String description, String code, String preconditions, String traceinfo) {
		exportErrorData(eclass, ekind, pathname, variable, beginline,
				errorline, description, code, preconditions, traceinfo);
	}

	/**
	 * �����ݿ����һ�������¼
	 * @param eclass ȱ��
	 * @param ekind ���
	 * @param pathname �ļ�
	 * @param variable ������
	 * @param beginline ��ʼ��
	 * @param errorline ������
	 * @param description ��������
	 * @param code �����д���
	 * @param preconditions ǰ������
	 * @param traceinfo �纯��������Ϣ
	 */
	@Override
	public boolean exportErrorData(String eclass, String ekind, String pathname, String variable, int beginline, int errorline, String description,String code,String preconditions,String traceinfo) {
		if (db == null) {
			throw new RuntimeException("database connection closed.");
		}
		try {
			Table ipTable = db.getTable("IP");			
			
			// insert into IP (Num,Defect,Category,File,Variable,StartLine,IPLine,IPLineCode)
			// values (?,?,?,?,?,?,?,?)
			ipTable.addRow(null,
					Boolean.FALSE,
							null,
							ekind,
							eclass,
							pathname,
							(variable!=null && variable.length()>50)?variable.substring(0,50):variable,
							beginline,
							errorline,
							code,
							description,
							preconditions,
							traceinfo,
							null,
							null);
		} catch (IOException e) {
			throw new RuntimeException("Access database connect error", e);
		}
		return true;
	}

	public static void main(String args[]) {
		//DBAccess test = new DBAccess();
		// ��һ�λ��Զ�����һ��mdb�ļ����Ժ�ḽ�ӷ�ʽ
		//test.openDataBase("softtest\\database\\java\\test.mdb");
		//test.exportErrorData("test", "test", "test", "test", 4, 4);
		//test.exportErrorData("test", "test", "test", "test", 6, 6);
		//test.closeDataBase();
		//System.out.println("ok");
		/*List<DBConfig> list=GeneralDBAccess.getScanTypes("softtest\\database\\java\\config_CH.mdb");
		for(DBConfig s:list){
			System.out.println(s.category);
		}*/
		try {
			Database db = Database.open(new File("E:\\forlinux\\dts_java_test\\results\\test4.mdb"));
			System.out.print(db.getTable("IP").display());
			for (Column col : db.getTable("IP").getColumns()) {
				System.out.println(col.getName()+" "+col.getLengthInUnits()+" "+col.getType());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}		
		
	}
	
	/**
	 * ���ݲ�����ȡԴ����Ϣ���ļ�·������ʼ�кţ���ʼ�кţ���ֹ�кţ���ֹ�кţ����Ǵ�1��ʼ���� ���Ҫ�������ϣ��кŲ��Ϸ����׳�Խ���쳣
	 */
	public static String getSouceCode(String path, int beginline, int begincolumn,
			int endline, int endcolumn) {
		LineNumberReader os = null;
		String line = null;
		StringBuffer buff = new StringBuffer();
		// �жϲ����Ϸ���
		if ((beginline > endline)
				|| (beginline == endline && begincolumn > endcolumn)) {
			return "";
		}
		if(path==null||path.equals("")||!path.toLowerCase().endsWith(".java")){
			return "";
		}
		try {
			os = new LineNumberReader(new FileReader(path));
			// ������ʼ�д�
			do {
				line = os.readLine();
			} while (line != null && os.getLineNumber() < beginline);

			// �����ʼ���ϵ�Դ�뵽buff
			if (line != null) {
				if (beginline == endline) {
					buff.append(line.substring(begincolumn - 1, endcolumn));
				} else {
					buff.append(line.substring(begincolumn - 1));
				}
			}

			// �����ʼ�к���ֹ�в���ͬһ���������
			while (line != null && os.getLineNumber() < endline) {
				line = os.readLine();
				buff.append("\n");
				buff.append(line.substring(0, endcolumn));
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return buff.toString();
	}
	
	/**
	 * ���ݲ�����ȡԴ����Ϣ���ļ�·������ʼ�кţ���ֹ�кţ� ���Ҫ�������ϲ��Ϸ����׳�Խ���쳣
	 */
	public static String getSouceCode(String path, int beginline, int endline) {
		LineNumberReader os = null;
		String line = null;
		StringBuffer buff = new StringBuffer();
		// �жϲ����Ϸ���
		if (beginline > endline) {
			return "";
		}
		if(path==null||path.equals("")||!path.toLowerCase().endsWith(".java")){
			return "";
		}
		try {
			os = new LineNumberReader(new FileReader(path));
			// ������ʼ�д�
			do {
				line = os.readLine();
			} while (line != null && os.getLineNumber() < beginline);

			// �����ʼ���ϵ�Դ�뵽buff
			if (line != null) {
				if (beginline == endline) {
					buff.append(line);
				} else {
					buff.append(line);
				}
			}

			// �����ʼ�к���ֹ�в���ͬһ���������
			while (line != null && os.getLineNumber() < endline) {
				line = os.readLine();
				buff.append("\n");
				buff.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return buff.toString();
	}
}


