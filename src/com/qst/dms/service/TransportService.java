package com.qst.dms.service;

import java.io.*;
import java.security.cert.CertPath;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import com.mysql.cj.util.DnsSrv;
import com.qst.dms.db.DBUtil;
import com.qst.dms.entity.*;

public class TransportService {
	// 物流数据采集
	public Transport inputTransport() {
		Transport trans = null;

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
			System.out.println("请输入地址：");
			// 接收键盘输入的字符串信息
			String address = scanner.next();
			// 数据状态是“采集”
			int type = DataBase.GATHER;

			// 提示用户输入登录用户名
			System.out.println("请输入货物经手人：");
			// 接收键盘输入的字符串信息
			String handler = scanner.next();
			// 提示用户输入主机IP
			System.out.println("请输入 收货人:");
			// 接收键盘输入的字符串信息
			String reciver = scanner.next();
			// 提示用于输入物流状态
			System.out.println("请输入物流状态：1发货中，2送货中，3已签收");
			// 接收物流状态
			int transportType = scanner.nextInt();
			// 创建物流信息对象
			trans = new Transport(id, nowDate, address, type, handler, reciver,
					transportType);
		} catch (Exception e) {
			System.out.println("采集的日志信息不合法");
		}
		// 返回物流对象
		return trans;
	}

	// 物流信息输出
	public void showTransport(Transport... transports) {
		for (Transport e : transports) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}

	// 匹配的物流信息输出，可变参数
	public void showMatchTransport(MatchedTransport... matchTrans) {
		for (MatchedTransport e : matchTrans) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}

	// 匹配的物流信息输出，参数是集合
	public void showMatchTransport(ArrayList<MatchedTransport> matchTrans) {
		for (MatchedTransport e : matchTrans) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}

	// 匹配物流信息保存，参数是集合
	public void saveMatchedTransport(ArrayList<MatchedTransport> matchTrans) {
		// 创建一个ObjectOutputStream对象输出流，并连接文件输出流
		// 以可追加的方式创建文件输出流，数据保存到MatchedTransports.txt文件中
		try (ObjectOutputStream obs = new ObjectOutputStream(
				new FileOutputStream("MatchedTransports.txt", true))) {
			// 循环保存对象数据
			for (MatchedTransport e : matchTrans) {
				if (e != null) {
					// 把对象写入到文件中
					obs.writeObject(e);
					obs.flush();
				}
			}
			// 文件末尾保存一个null对象，代表文件结束
			obs.writeObject(null);
			obs.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}




	// 读匹配物流信息保存，参数是集合
	public ArrayList<MatchedTransport> readMatchedTransport() {
		ArrayList<MatchedTransport> matchTrans = new ArrayList<>();
		// 创建一个ObjectInputStream对象输入流，并连接文件输入流，读MatchedTransports.txt文件中
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				"MatchedTransports.txt"))) {
			MatchedTransport matchTran;
			// 循环读文件中的对象
//			while ((matchTran = (MatchedTransport) ois.readObject()) != null) {
//				// 将对象添加到泛型集合中
//				matchTrans.add(matchTran);
//			}
			while (true) {
				try {
					matchTran = (MatchedTransport) ois.readObject();
					matchTrans.add(matchTran);
				} catch (EOFException ex) {
					break;
				}
			}

		} catch (Exception ex) {
			//ex.printStackTrace();
		}
		return matchTrans;
	}


	public void saveAndAppendTransport(ArrayList<MatchedTransport> matchedTransports) {
		AppendObjectOutputStream aoos = null;
		File file = new File("MatchedTransports.txt");
		try {
			AppendObjectOutputStream.file = file;
			aoos = new AppendObjectOutputStream(file);
			for (MatchedTransport e : matchedTransports) {
				if (e != null) {
					aoos.writeObject(e);
					aoos.flush();
				}
			}

		} catch (Exception ex) {

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

	public void saveMatchTransportToDB(ArrayList<MatchedTransport> matchTrans) {
		DBUtil db = new DBUtil();
		try {
			db.getConnection();
			for (MatchedTransport matchedTransport : matchTrans) {
				Transport send = matchedTransport.getSend();
				Transport trans = matchedTransport.getTrans();
				Transport receive = matchedTransport.getReceive();
				String sql = "INSERT INTO gather_transport(id,time,address,type,handler,reciver,transporttype) VALUES(?,?,?,?,?,?,?)";
				Object[] temp = new Object[] {
						send.getId(), new Timestamp(send.getTime().getTime()), send.getAddress(), send.getType(),
						send.getHandler(), send.getReciver(), send.getTransportType()
				};
				db.executeUpdate(sql, temp);
				temp = new Object[] {
						trans.getId(), new Timestamp(trans.getTime().getTime()), trans.getAddress(), trans.getType(),
						trans.getHandler(), trans.getReciver(), trans.getTransportType()
				};
				db.executeUpdate(sql, temp);
				temp = new Object[] {
						receive.getId(), new Timestamp(receive.getTime().getTime()), receive.getAddress(), receive.getType(),
						receive.getHandler(), receive.getReciver(), receive.getTransportType()
				};
				db.executeUpdate(sql,temp);
				sql = "INSERT INTO matched_transport(sendid,transid,receiveid) VALUES(?,?,?)";
				Object[] object = new Object[] {
						send.getId(), trans.getId(), receive.getId()
				};
				db.executeUpdate(sql, object);
			}
			db.closeAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public ArrayList<MatchedTransport> readMatchedTransportFromDB() {
		ArrayList<MatchedTransport> matchedTransports = new ArrayList<MatchedTransport>();
		DBUtil db = new DBUtil();
		try {
			db.getConnection();
//			String sql = "SELECT s.ID,s.TIME,s.ADDRESS,s.TYPE,s.HANDLER,s.RECIVER,s.TRANSPORTTYPE,"
//					   + "t.ID,t.TIME,t.ADDRESS,t.TYPE,t.HANDLER,t.RECIVER,t.TRANSPORTTYPE,"
//					   + "r.ID,r.TIME,r.ADDRESS,r.TYPE,r.HANDLER,r.RECIVER,r.TRANSPORTTYPE "
//					   + "FROM MATCHED_TRANSPORT m,GATHER_TRANSPORT s,GATHER_TRANSPORT t,GATHER_TRANSPORT r "
//					   + "WHERE m.SENDID=s.ID AND m.TRANSID=t.ID AND m.RECEIVEID=r.ID";
			String sql = " SELECT s.id,s.time,s.address,s.type,s.handler,s.reciver,s.transporttype,t.id,t.time,t.address,t.type,t.handler,t.reciver,t.transporttype,r.id,r.time,r.address,r.type,r.handler,r.reciver,r.transporttype FROM matched_transport m,gather_transport s,gather_transport t,gather_transport r WHERE m.sendid=s.id AND m.transid=t.id AND m.receiveid=r.id";
			ResultSet resultSet = db.executeQuery(sql, null);
			while (resultSet.next()) {
				Transport send = new Transport(resultSet.getInt(1), resultSet.getDate(2), resultSet.getString(3),
						resultSet.getInt(4), resultSet.getString(5), resultSet.getString(6), resultSet.getInt(7));
				Transport trans = new Transport(resultSet.getInt(8), resultSet.getDate(9), resultSet.getString(10),
						resultSet.getInt(11), resultSet.getString(12), resultSet.getString(13), resultSet.getInt(14));
				Transport receive = new Transport(resultSet.getInt(15), resultSet.getDate(16), resultSet.getString(17),
						resultSet.getInt(18), resultSet.getString(19), resultSet.getString(20), resultSet.getInt(21));
				MatchedTransport matchedTransport = new MatchedTransport(send, trans, receive);
				matchedTransports.add(matchedTransport);
			}
			db.closeAll();
			return matchedTransports;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return matchedTransports;
	}

	public ResultSet readTransResult() {
		DBUtil db = new DBUtil();
		ResultSet rs=null;
		try {
			// 获取数据库链接
			Connection conn = db.getConnection();
			// 查询匹配日志，设置ResultSet可以使用除了next()之外的方法操作结果集
			Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);

			String sql = "SELECT * from gather_transport";
			rs = st.executeQuery(sql);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

}