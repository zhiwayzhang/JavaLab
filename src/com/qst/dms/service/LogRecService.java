package com.qst.dms.service;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

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

}
