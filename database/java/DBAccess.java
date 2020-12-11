package softtest.database.java;

import java.sql.*;
import java.io.*;
import java.util.*;
import java.util.Date;

import softtest.config.java.Config;
import softtest.fsmanalysis.java.ProjectAnalysis;

public class DBAccess {
	/** ���ݿ����� */
	private Connection dbcon;
	private Connection statcon;
	
	private int IP_ID = 0;
	
	private Map getIpCountByCategory() {		
		PreparedStatement select = null;
		Map stat = new HashMap();
		try {
			String strSQL="SELECT defect,category FROM IP WHERE Num > ?";
			select = dbcon.prepareStatement(strSQL);
			select.setInt(1, IP_ID);
			ResultSet result = select.executeQuery();
			
			while (result.next()) {
				String defect = result.getString(1);
				String category = result.getString(2);
				String key = defect.concat(" ").concat(category);
				if (stat.containsKey(key)) {
					int num = (Integer) stat.get(key);
					stat.put(key, num+1);
				} else {
					stat.put(key, 1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Access database connect error",e);
		}
		finally {
			if (select != null) {
				try { select.close(); } catch (Exception e) {}
			}
		}
		return stat;
	}
	
	private int getMaxIpId() {
		int key = 0;
		Statement select = null;
		try {
			String strSQL="SELECT max(Num) FROM IP";
			select = dbcon.createStatement();
			ResultSet result = select.executeQuery(strSQL);
			// ����һ���µ�Ψһ���
			if (result.next()) {
				String str = result.getString(1);
				if (str != null) {
					key = Integer.parseInt(str);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Access database connect error",e);
		}
		finally {
			if (select != null) {
				try { select.close(); } catch (Exception e) {}
			}
		}
		return key;
	}
	
	private int getMaxScanId() {
		int key = 0;
		Statement select = null;
		try {
			String strSQL="SELECT MAX(id) FROM total";
			select = statcon.createStatement();
			ResultSet result = select.executeQuery(strSQL);
			// ����һ���µ�Ψһ���
			if (result.next()) {
				String str = result.getString(1);
				if (str != null) {
					key = Integer.parseInt(str);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Access database connect error",e);
		}
		finally {
			if (select != null) {
				try { select.close(); } catch (Exception e) {}
			}
		}
		return key;
	}
	
	private int getIpCount() {
		return getMaxIpId() - IP_ID;
	}
	
	private void statistics() {
		openStatDataBase("stat\\stat.mdb");
		
		int ipCount = getIpCount();
		String date = (new Date()).toString().replace(' ', '_');		
		
		exportStatisticsAll(ProjectAnalysis.project, 
							date, 
							ProjectAnalysis.usedtime, 
							"java", 
							Config.version, 
							ProjectAnalysis.filecount, 
							ProjectAnalysis.linecount, 
							ipCount, 
							0);
		
		int scanid = getMaxScanId();
		Map ipstat = getIpCountByCategory();
		for (Object obj : ipstat.keySet()) {
			String key = (String) obj;
			String keys[] = key.split(" ");
			String defect = keys[0];
			String category = keys[1];
			exportStatistics(ProjectAnalysis.project, 
							 date,
							 ProjectAnalysis.usedtime, 
							 "java", 
							 Config.version, 
							 ProjectAnalysis.filecount, 
							 ProjectAnalysis.linecount, 
							 defect, 
							 category, 
							 (Integer)ipstat.get(key), 
							 0, 
							 scanid);
		}
	}
	
	/**
	 * ���ͳ����Ϣ
	 * ����һ��ͳ�ƹ���,ÿ��ɨ����ɺ�,�����н���Զ�ͳ�Ƶ�һ�����ݿ���߱���.
	 * ����: ��Ŀ�����������ԡ��ļ������������С�ip������ÿ��ȱ��ip��������ʱ�䡢
	 * ɨ�����ʱ�䡢ȷ�Ϲ�����(�����ֶ�,���û���д)��
	 */	
	private void exportStatisticsAll(String projectName,
			                            String date,
			                            long usedTime,
			                            String language, 
			                            String version,
			                            int fileCount, 
			                            int lineCount, 
			                            int ipCount,
			                            int ackIpCount) {
		PreparedStatement pstmt = null;
		if (statcon == null) {
			throw new RuntimeException("database connection closed.");
		}
		try {
			// Ԥ����ָ��
			String strSQL="insert into total (project,scandate,usedtime,language,dtsversion,filecount,linecount,ipcount,ackipcount) values (?,?,?,?,?,?,?,?,?)";

			pstmt = statcon.prepareStatement(strSQL);
			// ���ò����ֵ
			pstmt.setString(1, projectName);
			pstmt.setString(2, date);
			pstmt.setInt(3, (int)usedTime);
			pstmt.setString(4, language);
			pstmt.setString(5, version);
			pstmt.setInt(6, fileCount);
			pstmt.setInt(7, lineCount);
			pstmt.setInt(8, ipCount);
			pstmt.setInt(9, ackIpCount);
			// ����
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new RuntimeException("Access database connect error",ex);
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					//ex.printStackTrace();
					throw new RuntimeException("Access database connect error",ex);
				}
			}
		}
	}
	
	private void exportStatistics(String projectName, 
										String date, 
										long usedTime, 
										String language,
										String version,
										int fileCount, 
										int lineCount,
										String eclass,
										String ekind, 
										int ipCount, 
										int ackIpCount,
										int scanid) {
		PreparedStatement pstmt = null;
		if (statcon == null) {
			throw new RuntimeException("database connection closed.");
		}
		try {
			// Ԥ����ָ��
			String strSQL="insert into detail (project,scandate,usedtime,language,dtsversion,filecount,linecount,defect,category,cat_ipcount,cat_ackipcount,scanid) " +
					"values (?,?,?,?,?,?,?,?,?,?,?,?)";

			pstmt = statcon.prepareStatement(strSQL);
			// ���ò����ֵ
			pstmt.setString(1, projectName);
			pstmt.setString(2, date);
			pstmt.setInt(3, (int)usedTime);
			pstmt.setString(4, language);
			pstmt.setString(5, version);
			pstmt.setInt(6, fileCount);
			pstmt.setInt(7, lineCount);
			pstmt.setString(8, eclass);
			pstmt.setString(9, ekind);
			pstmt.setInt(10, ipCount);
			pstmt.setInt(11, ackIpCount);
			pstmt.setInt(12, scanid);
			// ����
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new RuntimeException("Access database connect error",ex);
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					//ex.printStackTrace();
					throw new RuntimeException("Access database connect error",ex);
				}
			}
		}
	}

	/** �ļ����� */
	private static void copyFileToFile(String F1, String F2) {
		// ���ļ��Կ�,��F1������F2,��F2������ᱻ����;�������κ��ļ�.
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(F1); // �����ļ�������
			fos = new FileOutputStream(F2);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
		} catch (FileNotFoundException ex) {
			//ex.printStackTrace();
			throw new RuntimeException("File path not found",ex);
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
		String driver = "sun.jdbc.odbc.JdbcOdbcDriver";
		String url = "jdbc:odbc:DRIVER=Microsoft Access Driver (*.mdb);DBQ=" + pathname;
		Connection conn = null;
		Statement stmt = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, "", "");
			stmt = conn.createStatement();
			String sql = "delete * from IP";
			stmt.executeUpdate(sql);
		} catch (ClassNotFoundException ex) {
			//ex.printStackTrace();
			throw new RuntimeException("Access database driver error",ex);
		} catch (SQLException ex) {
			//ex.printStackTrace();
			throw new RuntimeException("Access database connect error",ex);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ex) {
					//ex.printStackTrace();
					throw new RuntimeException("Access database connect error",ex);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ex) {
					//ex.printStackTrace();
					throw new RuntimeException("Access database connect error",ex);
				}
			}
		}
	}

	/** ����һ���µĿյ�mdb�ļ� */
	public static void createMdbFile(String pathname) {
		// ����һ������
		createMdbFile(pathname,"..\\set\\Dbtemplate.mdb");
	}
	
	/** ����һ���µĿյ�mdb�ļ� */
	public static void createMdbFile(String pathname, String template) {
		// ����һ������
		copyFileToFile(template, pathname);
	}
	
	/** ��ȡɨ������ */
	public static List<DBConfig> getScanTypes(String pathname){
		ArrayList<DBConfig> list=new ArrayList<DBConfig>();
		String driver = "sun.jdbc.odbc.JdbcOdbcDriver";
		String url = "jdbc:odbc:DRIVER=Microsoft Access Driver (*.mdb);DBQ=" + pathname;
		Connection conn = null;
		Statement stmt = null;	
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, "", "");
			stmt = conn.createStatement();
			String sql = "";
			sql = "select * from ScanSet";
			
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				if (rs.getBoolean(5)) {
					DBConfig c=new DBConfig();
					c.defect=rs.getString(2);
					c.category=rs.getString(3);
					list.add(c);
				}
			}
		} catch (ClassNotFoundException ex) {
			//ex.printStackTrace();
			throw new RuntimeException("Access database driver error",ex);
		} catch (SQLException ex) {
			//ex.printStackTrace();
			throw new RuntimeException("Access database connect error",ex);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ex) {
					//ex.printStackTrace();
					throw new RuntimeException("Access database connect error",ex);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ex) {
					//ex.printStackTrace();
					throw new RuntimeException("Access database connect error",ex);
				}
			}
		}
		return list;
	}
	
	public void openIPDataBase(String pathname) {
		if (dbcon == null) {
			dbcon = openDataBase(pathname, "..\\set\\Dbtemplate.mdb");
			IP_ID = getMaxIpId();
		}
	}
	
	public void openStatDataBase(String pathname) {
		if (statcon == null) {
			statcon = openDataBase(pathname, "..\\set\\stattemplate.mdb");
		}
	}

	/** �����ݿ����� */
	public void openDataBase(String pathname) {
		dbcon = openDataBase(pathname, "..\\set\\Dbtemplate.mdb");
		IP_ID = getMaxIpId();
	}
	
	/** �����ݿ����� */
	public Connection openDataBase(String pathname, String template) {
		Connection con = null;
		File file = new File(pathname);
		if (!file.exists()) {
			DBAccess.createMdbFile(pathname, template);
		}
		String driver = "sun.jdbc.odbc.JdbcOdbcDriver";
		String url = "jdbc:odbc:DRIVER=Microsoft Access Driver (*.mdb);PWD=" + getPassword() + ";DBQ=" + pathname;
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, "", "");
		} catch (ClassNotFoundException ex) {
			//ex.printStackTrace();
			throw new RuntimeException("Access database driver error",ex);
		} catch (SQLException ex) {
			//ex.printStackTrace();
			throw new RuntimeException("Access database connect error:".concat(pathname),ex);
		}
		return con;
	}
	
	public void writeIP(){
		Hashtable<String, List<IPRecord>> table=new Hashtable<String, List<IPRecord>>();
		for(IPRecord r:list_ip){
			List<IPRecord> list=table.get(r.ekind);
			if(list==null){
				list=new ArrayList<IPRecord>();
				table.put(r.ekind, list);
			}
			list.add(r);
		}
		
		for(Enumeration<List<IPRecord>> e= table.elements();e.hasMoreElements();){
			List<IPRecord> list=e.nextElement();
			for(int i=0;i<list.size()*Config.PERCENT/100&&i<Config.MAXIP;i++){
				IPRecord r=list.get(i);
				if(exportErrorData( r.eclass,  r.ekind,  r.pathname,  r.variable,  r.beginline,  r.errorline,  r.description,  r.code,  r.preconditions,  r.traceinfo)){
					writecount++;
				}
			}
		}
		
	}
	/** �ر����ݿ����� */
	public void closeDataBase() {
		if (!Config.TESTING) {
			statistics();
		}
		
		if (dbcon != null) {
			try {
				dbcon.close();
			} catch (SQLException ex) {
				//ex.printStackTrace();
				throw new RuntimeException("Access database connect error",ex);
			}
		}
		dbcon = null;
		
		if (statcon != null) {
			try {
				statcon.close();
			} catch (SQLException ex) {
				//ex.printStackTrace();
				throw new RuntimeException("Access database connect error",ex);
			}
		}
		statcon = null;
	}
	
	class IPRecord{
		String eclass;
		String ekind;
		String pathname;
		String variable;
		int beginline;
		int errorline;
		String description;
		String code;
		String preconditions;
		String traceinfo;
		public IPRecord(String eclass, String ekind, String pathname, String variable, int beginline, int errorline, String description, String code, String preconditions, String traceinfo) {
			this.eclass = eclass;
			this.ekind = ekind;
			this.pathname = pathname;
			this.variable = variable;
			this.beginline = beginline;
			this.errorline = errorline;
			this.description = description;
			this.code = code;
			this.preconditions = preconditions;
			this.traceinfo = traceinfo;
		}
	}
	public int writecount=0;
	public ArrayList<IPRecord> list_ip=new ArrayList<IPRecord>();
	public void exportErrorDataBuff(String eclass, String ekind, String pathname, String variable, int beginline, int errorline, String description,String code,String preconditions,String traceinfo) {
		list_ip.add(new IPRecord( eclass,  ekind,  pathname,  variable,  beginline,  errorline,  description,  code,  preconditions,  traceinfo));
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
	public boolean exportErrorData(String eclass, String ekind, String pathname, String variable, int beginline, int errorline, String description,String code,String preconditions,String traceinfo) {
		PreparedStatement select = null;
		PreparedStatement pstmt = null;
		if (dbcon == null) {
			throw new RuntimeException("database connection closed.");
		}
		try {
			int maxid = getMaxIpId();
			
			String sql="select * from IP where IPLine=? and Defect=? and Category=? and File=? and StartLine=? and IPLineCode=? and Num>? and Variable=?";
			select = dbcon.prepareStatement(sql);
			// ����ֵ
			select.setString(2, eclass);
			select.setString(3, ekind);
			select.setString(4, pathname);
			select.setInt(5, beginline);
			select.setInt(1, errorline);
			select.setString(6, code);
			select.setInt(7, IP_ID);
			select.setString(8, variable);
			/*
			String sql = "select * from IP where IPLine="+errorline
							+" and Defect=\""+eclass+"\""
							+" and Category=\""+ekind+"\""
							+" and File=\""+pathname+"\""
							+" and StartLine="+beginline
							+" and IPLineCode=\""+code+"\"";*/
			ResultSet rs = select.executeQuery();
			if (rs.next()) {
				return false;
			}
			
			String strSQL="";
			// Ԥ����ָ��
			strSQL="insert into IP (Defect,Category,File,Variable,StartLine,IPLine,Description,IPLineCode,PreConditions,TraceInfo) values (?,?,?,?,?,?,?,?,?,?)";

			pstmt = dbcon.prepareStatement(strSQL);
			// ���ò����ֵ
			pstmt.setString(1, eclass);
			pstmt.setString(2, ekind);
			pstmt.setString(3, pathname);
			pstmt.setString(4, variable);
			pstmt.setInt(5, beginline);
			pstmt.setInt(6, errorline);
			pstmt.setString(7, description);
			pstmt.setString(8, code);
			pstmt.setString(9, preconditions);
			pstmt.setString(10, traceinfo);
			// ����
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new RuntimeException("Access database connect error",ex);
		} finally {
			if (select != null) {
				try {
					select.close();
				} catch (SQLException ex) {
					//ex.printStackTrace();
					throw new RuntimeException("Access database connect error",ex);
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					//ex.printStackTrace();
					throw new RuntimeException("Access database connect error",ex);
				}
			}
		}
		return true;
	}
	/**
	 * �����ݿ�����������
	 * add by cjie
	 */
	public boolean writeResult(int useTime, int fileCount, int lineCount) {
		PreparedStatement select = null;
		PreparedStatement pstmt = null;
		if (dbcon == null) {
			throw new RuntimeException("database connection closed.");
		}
		try {
			String sql="delete from total";
			select = dbcon.prepareStatement(sql);
			select.executeUpdate();
			
			String strSQL="";
			// Ԥ����ָ��
			strSQL="insert into total (useTime,fileCount,lineCount) values (?,?,?)";

			pstmt = dbcon.prepareStatement(strSQL);
			// ���ò����ֵ
			pstmt.setInt(1, useTime);
			pstmt.setInt(2, fileCount);
			pstmt.setInt(3, lineCount);
			// ����
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new RuntimeException("Access database connect error",ex);
		} finally {
			if (select != null) {
				try {
					select.close();
				} catch (SQLException ex) {
					throw new RuntimeException("Access database connect error",ex);
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					throw new RuntimeException("Access database connect error",ex);
				}
			}
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
		List<DBConfig> list=DBAccess.getScanTypes("softtest\\database\\java\\config_CH.mdb");
		for(DBConfig s:list){
			System.out.println(s.category);
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
				if (os.getLineNumber() == endline) {
					buff.append(line.substring(0, endcolumn));
				}else{
					buff.append(line);
				}
				
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
	private String getPassword() {
		return Config.DB_STAT_PASSWORD;
	}
}


