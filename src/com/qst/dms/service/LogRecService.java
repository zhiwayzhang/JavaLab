package com.qst.dms.service;
import java.io.*;
import java.security.cert.CertPath;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import com.mysql.cj.log.Log;
import com.qst.dms.db.DBUtil;
import com.qst.dms.entity.*;
//日志业务类



public class LogRecService {
	public static boolean checkIP(String ip) {
		if (ip != null && !ip.isEmpty()) {
			// 正则表达式
			String rules = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
					+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
					+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
					+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
			return ip.matches(rules);
		}
		System.out.println("数据有误");
		return false;
	}


	// 日志数据采集
	public LogRec inputLog() {
		LogRec log = null;
		// 建立一个从键盘接收数据的扫描器
		Scanner scanner = new Scanner(System.in);
		try {
			// 提示用户输入ID标识
			System.out.println("请输入ID标识：");
			// 接收键盘输入的整数
			int id = scanner.nextInt();
			// 获取当前系统时间
			Date nowDate = new Date();
			// 提示用户输入地址
			scanner.nextLine();
			System.out.println("请输入地址：");
			// 接收键盘输入的字符串信息
			//scanner.nextLine();
			String address = scanner.nextLine();
			// 数据状态是“采集”
			int type = DataBase.GATHER;
			// 提示用户输入登录用户名
			System.out.println("请输入用户名:");
			// 接收键盘输入的字符串信息
			//scanner.nextLine();
			String user = scanner.nextLine();
			// 提示用户输入主机IP
			System.out.println("请输入主机IP:");
			// 接收键盘输入的字符串信息
			//scanner.nextLine();
			String ip = scanner.nextLine();
			while (!checkIP(ip)) {
				System.out.println("IP地址输入有误，请重新输入");
				ip = scanner.nextLine();
			}
			// 提示用户输入登录状态、登出状态
			System.out.println("请输入登录状态:1是登录，0是登出");
			int logType = scanner.nextInt();
			while (logType != 1 && logType != 0) {
				System.out.println("输入有误，请重新输入");
				logType = scanner.nextInt();
			}
			// 创建日志对象
			log = new LogRec(id, nowDate, address, type, user, ip, logType);
		} catch (Exception e) {
			System.out.println("采集的日志信息不合法");
		}
		// 返回日志对象
		return log;
	}

	// 日志信息输出
	public void showLog(LogRec... logRecs) {
		for (LogRec e : logRecs) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}

	// 匹配日志信息输出，可变参数
	public void showMatchLog(MatchedLogRec... matchLogs) {
		for (MatchedLogRec e : matchLogs) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}

	// 匹配日志信息输出,参数是集合
	public void showMatchLog(ArrayList<MatchedLogRec> matchLogs) {
		for (MatchedLogRec e : matchLogs) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}

	//匹配日志信息保存
	public void saveMatchLog(ArrayList<MatchedLogRec> matchLogs) {
		// new object
		try (AppendObjectOutputStream aobs = new AppendObjectOutputStream(
				new File("MatchedLogs.txt"))) {
			for (MatchedLogRec e : matchLogs) {
				if (e != null) {
					aobs.writeObject(e);
					aobs.flush();
				}
			}
			aobs.writeObject(null);
			aobs.flush();
		}  catch (Exception ex) {
			ex.printStackTrace();
		}
	}
//	public ArrayList<MatchedTransport> readMatchedTransport() {
//		ArrayList<MatchedTransport> matchTrans = new ArrayList<>();
//		// 创建一个ObjectInputStream对象输入流，并连接文件输入流，读MatchedTransports.txt文件中
//		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
//				"MatchedTransports.txt"))) {
//			MatchedTransport matchTran;
//			// 循环读文件中的对象
//			while ((matchTran = (MatchedTransport) ois.readObject()) != null) {
//				// 将对象添加到泛型集合中
//				matchTrans.add(matchTran);
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return matchTrans;
//	}
	public ArrayList<MatchedLogRec> readMatchedLog() {
		ArrayList<MatchedLogRec> matchedLogRecs = new ArrayList<>();
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				"MatchedLogs.txt"
		)) ) {
			MatchedLogRec matchedLogRec;
			while (true) {
				try {
					matchedLogRec = (MatchedLogRec) ois.readObject();
					matchedLogRecs.add(matchedLogRec);
				} catch (EOFException ex) {
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return matchedLogRecs;
	}

	public void saveAndAppendMatchedLogRec(ArrayList<MatchedLogRec> matchedLogRecs) {
		AppendObjectOutputStream aoos = null;
		File file = new File("MatchedLogs.txt");
		try {
			AppendObjectOutputStream.file = file;
			aoos = new AppendObjectOutputStream(file);

			for (MatchedLogRec e : matchedLogRecs) {
				if (e != null) {
					aoos.writeObject(e);
					aoos.flush();
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (aoos != null) {
				try {
					aoos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public ArrayList<MatchedLogRec> readMatchedLogFromDB() {
		ArrayList<MatchedLogRec> matchedLogRecs = new ArrayList<MatchedLogRec>();
		DBUtil db = new DBUtil();
		try {
			db.getConnection();
			String sql = "SELECT i.id,i.time,i.address,i.type,i.username,i.ip,i.logtype,"
					   + "o.id,o.time,o.address,o.type,o.username,o.ip,o.logtype "
					   + "FROM matched_logrec m,gather_logrec i,gather_logrec o "
					   + "WHERE m.loginid=i.id AND m.logoutid=o.id";
			ResultSet rs = db.executeQuery(sql, null);
			while (rs.next()) {
				LogRec login = new LogRec(rs.getInt(1), rs.getDate(2),
						rs.getString(3), rs.getInt(4), rs.getString(5),
						rs.getString(6), rs.getInt(7));
				LogRec logout = new LogRec(rs.getInt(8), rs.getDate(9),
						rs.getString(10), rs.getInt(11), rs.getString(12),
						rs.getString(13), rs.getInt(14));
				MatchedLogRec matchedLogRec = new MatchedLogRec(login, logout);
				matchedLogRecs.add(matchedLogRec);
			}
			db.closeAll();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return matchedLogRecs;
	}



	public void saveMatchedLogToDB(ArrayList<MatchedLogRec> matchedLogRecs) {
		DBUtil db = new DBUtil();
		try {
			// get access
			db.getConnection();
			for (MatchedLogRec matchedLogRec : matchedLogRecs) {
				LogRec login = matchedLogRec.getLogin();
				LogRec logout = matchedLogRec.getLogout();
				String sql_save = "INSERT INTO gather_logrec(time,address,type,username,ip,logtype) VALUES(?,?,?,?,?,?)";
				Object[] temp = new Object[] {
						new Timestamp(login.getTime().getTime()), login.getAddress(),
						login.getType(),login.getUser(), login.getIp(),
						login.getLogType()
				};
				int loginKey = db.executeSQLAndReturnPrimaryKey(sql_save, temp);
				//db.executeUpdate(sql_save, temp);
				login.setId(loginKey);
				CertPath certPath = null;
				temp = new Object[] {
						new Timestamp(logout.getTime().getTime()), logout.getAddress(),
						logout.getType(), logout.getUser(), logout.getIp(),
						logout.getLogType()
				};
				int logoutKey = db.executeSQLAndReturnPrimaryKey(sql_save, temp);
				//db.executeUpdate(sql_save, temp);
				logout.setId(logoutKey);
				String sql_save_log = "INSERT INTO matched_logrec(loginid,logoutid) VALUES(?,?)";
				temp = new Object[] {
						login.getId(), logout.getId()
				};
				db.executeUpdate(sql_save_log, temp);
			}
			db.closeAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ResultSet readLogResult() {
		DBUtil db = new DBUtil();
		ResultSet rs=null;
		try {
			// 获取数据库链接
			Connection conn=db.getConnection();
			// 查询匹配日志，设置ResultSet可以使用除了next()之外的方法操作结果集
			Statement st=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);

			String sql = "SELECT i.username,i.time,o.time,i.address FROM matched_logrec m,gather_logrec i,gather_logrec o WHERE m.loginid=i.id AND m.logoutid=o.id";
			rs = st.executeQuery(sql);


		}catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

}

