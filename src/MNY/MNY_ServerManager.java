package MNY;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import vo.MNYMenu;
/**
 * <pre>
 * MNY(MaNisYous) ���� �ŷ����� �������� Ŭ����
 * �ֿ� ������δ� ������ ����.
 * 1. ���� �Ǹſ� �޴� ���� �˻�
 * 2. ���� �Ǹ� ���� ����
 * 3. ���� �ŷ����� �˻�
 * 4. �ŷ���� ���� ����
 * </pre>
 * */
public class MNY_ServerManager {
	
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private String sql;

	/**
	 * ���忡�� �Ǹ��ϴ� �޴� ������ DB���� ������ ����
	 * 1) select �Ǹ� �޴�Ÿ��
	 * 2) select �޴�Ÿ�� �������� �޴���� ���� 		-> (HashMap<String, Integer>)menuMap�� ����
	 * 3) select �޴�Ÿ�� �������� �ɼǸ�� �ɼǰ���	-> (HashMap<String, Integer>)optionMap�� ����
	 * => �ش� ���� ���� �� ����
	 * => �޴�Ÿ�Ժ� MNYMenu ��ü ArrayList<MNYMenu>�� ����
	 * @return ArrayList<MNYMenu>
	 */
	public ArrayList<MNYMenu> menuSetter() {
		ArrayList<MNYMenu> sellingList = new ArrayList<>();
		
		conn = ConnectionManager.getConnection();
		try {
			// 1) �޴��� type DB�˻� & ��ȯ
			sql = "SELECT DISTINCT TYPE FROM MENU";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			ResultSet rs_Menu_Option = null; // type�� �޴�&�ɼ� ������ �˻���� ���� ResultSet
			while(rs.next()){
				String type = rs.getString(1); // �ǸŻ�ǰ�� type ��ȯ
				
				HashMap<String, Integer> menuMap = new HashMap<>();	  // �޴� �̸��� ���� ���� HashMap
				HashMap<String, Integer> optionMap = new HashMap<>(); // �ɼ� �̸��� ���� ���� HashMap
				
				// �˻��� type�� �ش��ϴ� �޴���� ���� DB �˻�
				sql = "SELECT ITEM, PRICE FROM MENU WHERE TYPE = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, type);
				
				rs_Menu_Option = pstmt.executeQuery();
				// ���� �޴��� �̸��� ���� ������ ��� menuMap�� ����
				while(rs_Menu_Option.next()) 
					menuMap.put(rs_Menu_Option.getString(1), rs_Menu_Option.getInt(2));

				// �˻��� type�� �ش��ϴ� �ɼǸ�� ���� DB �˻�
				sql = "SELECT OPTION_NAME, ADD_COST FROM MENU_OPTION WHERE TYPE = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, type);
				rs_Menu_Option = pstmt.executeQuery();
				
				// �ɼ��� �̸��� ���� ������ ��� menuMap�� ����
				while(rs_Menu_Option.next()) 
					optionMap.put(rs_Menu_Option.getString(1), rs_Menu_Option.getInt(2));
				
				// �˻����� ���� �ǸŻ�ǰ ������ ���� ��ü�� sellingList�� ����
				sellingList.add(new MNYMenu(type, menuMap, optionMap));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		
		return sellingList;
	}
	
	
	/**
	 * ���忡�� �Ǹŵ� �޴������� DB�� ����
	 * 1) insert �ֹ��� ���� (�ŷ���ȣ SALE_SEQ.NEXTVAL, �ŷ��ð�, ��������) DB ���̺� SALE_INFO�� ����
	 * 2) insert �ֹ��� ����Ʈ ���� ��� �޴��� ����(�ŷ���ȣ SALE_SEQ.CURRVAL, Ÿ��_�ֹ��޴���_�ɼ�, �ֹ��� ����) DB ���̺� ORDER_INFO�� ����
	 * @param clientIp 
	 * @param orderList: ���忡�� �ֹ��� �޴����� ���� ����Ʈ
	 * @return DB ���� �������� (���� = true, ���� = false)
	 */
	public boolean makeDeal(ArrayList<MNYMenu> orderList, String clientIp) {
		boolean dealResult = false;

		try {
			conn = ConnectionManager.getConnection();
			conn.setAutoCommit(false);
			int sequence = makeSequnceNum();	// �ߺ� ���� �ŷ���ȣ ��� ����, ���ο� squence ��ȣ ����(SALE_INFO �Է�)
			String client_ID = findClientName(clientIp); // �������� ���� ���� IP������ ���� ID �˻�&��ȯ(SALE_INFO �Է�)
			
			// �ŷ����� DB�Է� ����
			sql = "INSERT INTO SALE_INFO (ORDER_NO, STORE_ID, ORDER_TIME, WHOLE_PRICE) VALUES(?, ?, ?, ?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, sequence);
			pstmt.setString(2, client_ID);
			pstmt.setString(3, getTime()); // ���� ��¥�� �ð� ��ȯ �޼ҵ� ���
			pstmt.setInt(4, sumPrice(orderList)); // �ֹ��� ��ǰ���� ���� ��� �� ��ȯ �޼ҵ� ���

			// �ŷ������� �ش��ϴ� �� �ŷ����� �Է�
			if (pstmt.executeUpdate() == 1) {
				sql = "INSERT INTO ORDER_INFO (ORDER_NO, ORDER_ITEM, ORDER_PRICE, QUANTITY) VALUES(?, ?, ?, ?)";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, sequence);
				// �ùٸ� �Է��� Ȯ���ϱ� ���� insertCounter ����
				int insertCounter = 0;
				for (MNYMenu m : orderList) {
					pstmt.setString(2, m.getOrder_item());
					pstmt.setInt(3, m.getOrder_price());
					pstmt.setInt(4, m.getCount());
					if (pstmt.executeUpdate() == 1) insertCounter++; // ���� DB �Է� ������ 1����
					else break; // �Է� ���� Ȥ�� �Է� ���н� �ݺ��� Ż��
				}
				
				// DB�Է� ��� Ȯ��
//					1) orderList.size() == �ֹ��� ��ǰ�� ��
//					2) insertCounter == DB �Է� ���� ���� ��
//						�� ���� ũ�Ⱑ ���� �� �Է� ���� -> dealResult = true & commt���� DB��ȭ Ȯ��
				if (insertCounter == orderList.size()) {
					dealResult = true;
					conn.commit();
				} else {
					conn.rollback();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return dealResult;
	}

	/**
	 * �ֹ��� ��ǰ�� ������ �� �� ��� �� ��ȯ 
 	 * @return sum: ����Ʈ�� ���� �ֹ���ǰ ��ü�� ������ ����
	 */
	private int sumPrice(ArrayList<MNYMenu> list) {
		int sum = 0;
		for (MNYMenu m : list)
			sum += m.getLast_price(); // �ɼ��� �ݿ��� ���ݰ� ������ ������ ������ ã�� �ջ�
		return sum;
	}

	
	/**
	 * ���� ��¥�� �ð��� ���ϰ� ��ȯ
 	 * @return order_day: ���� [��/��/��] ��:��:��
	 */
	private String getTime() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		String order_day = "[" + year + "/" + month + "/" + day + "] " + hour + ":" + minute + ":" + second;
		return order_day;
	}

	/**
	 * �ŷ���ȣ�� ���� �ֽ��� ������ ��ȣ �߻� �� ��ȯ
 	 * @return sequence: DB�� SALE_SEQ.NEXTVAL�� �ֽ�ȭ�� ��ȣ
	 */
	private int makeSequnceNum() throws SQLException {
		sql = "SELECT SALE_SEQ.NEXTVAL FROM DUAL";
		pstmt = conn.prepareStatement(sql);
		rs = pstmt.executeQuery();
		rs.next();
		int sequence = rs.getInt(1);
		return sequence;
	}

	
	/**
	 * �ֹ��� �ŷ������� �Է��ϱ� ���� �ʿ��� �������� �˻� �� ��ȯ
	 * <ip �ּҿ� �ش��ϴ� ����ID ����� �˻���� ���� -> �̵�� ������ ���α׷��̿� ����>
 	 * @return clientIp: ���� ���� ���� ������ IP ��ȣ
	 */
	private String findClientName(String clientIp) throws SQLException {
		sql = "SELECT STORE_ID FROM STORE_INFO WHERE SERVER_INFO = ?";
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, clientIp);
		rs = pstmt.executeQuery();
		String client_ID = ""; // �˻� ����� ������� String ������ ����
		rs.next();
		client_ID = rs.getString(1);
		return client_ID;
	}

	
	/**
	 * �ŷ���ȣ�� �ش��ϴ� �⺻���� �˻�
	 * DB ���̺� SALE_INFO���� �ֹ��� ���� (�ŷ���ȣSALE_SEQ, �ŷ��ð�, ��������)
	 * @param clientIp 
	 * @param dealNo: �ŷ���ȣ
	 * @return dealed: �ŷ� �⺻������ ���� ����Ʈ <�ŷ���ȣ, �ŷ��ð�, ��������>
	 */
	public ArrayList<String> checkDeal(int dealNo, String clientIp) {
		ArrayList<String> dealed = new ArrayList<>();
		// Code Here
		try {
			// �ŷ������� �˻� (�ݾ��� ��ȭ ���������� ��ȯ ���)
//				CANCELĮ��: ȯ�ҵ� �ŷ��� ȯ�ҽð����� ���� => ȯ�ҵ��� ���� NULL ���� 
//			sql = "SELECT ORDER_TIME, TO_CHAR(WHOLE_PRICE, 'L9,999,999,999,999') "
			sql = "SELECT ORDER_TIME, TO_CHAR(WHOLE_PRICE, '9,999,999,999,999') "
					+ "FROM SALE_INFO WHERE ORDER_NO = ? "
					+ "AND CANCEL IS NULL AND STORE_ID = ("
					+ "SELECT STORE_ID FROM STORE_INFO WHERE SERVER_INFO = ?)";
//						��������: ���� �������� IP������ �̿� �˻� -> DB��� IP�� �ش��ϴ� ID �˻�
//							=> IP�� ID ����ġ -> �ŷ����� ���� X
			conn = ConnectionManager.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, dealNo);
			pstmt.setString(2, clientIp);
			rs = pstmt.executeQuery();

			// �ŷ����� ArrayList<String>�� ����
			if (rs.next()) {
				dealed.add(String.valueOf(dealNo)); // (int)�ŷ���ȣ String���� ����ȯ ����
				dealed.add(rs.getString(1));
				dealed.add(String.valueOf(rs.getString(2))+("��"));
			} else dealed = null;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return dealed;
	}
	
	/**
	 * �ŷ���ȣ�� �ش��ϴ� �Ǹ��������� �˻�
	 * DB ���̺� ORDER_INFO���� �ֹ��� ��ǰ ���� (Ÿ��_�ֹ��޴���_�ɼ�, �ֹ��� ����)
	 * @param dealNo: �ŷ���ȣ
	 * @return deal_Items: �ŷ��� ��ǰ ��ü MNYMenu���� ���� ����Ʈ
	 */
	public ArrayList<MNYMenu> findDeal(int dealNo) {
		ArrayList<MNYMenu> deal_Items = new ArrayList<>();
		try {
			// �ŷ���ȣ�� �ش��ϴ� �ŷ��� ���� �˻�
			String sql = "SELECT ORDER_ITEM, ORDER_PRICE FROM ORDER_INFO WHERE ORDER_NO = ?";
			conn = ConnectionManager.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, dealNo);
			rs = pstmt.executeQuery();

			while (rs.next())
				deal_Items.add(new MNYMenu(rs.getString(1), rs.getInt(2)));

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
			return deal_Items;
	}
	
	
	/**
	 * ���̺� SALE_INFO�� �ŷ���ȣ�� �������� ȯ�һ��(ȯ�ҽð�) �Է�
	 * @param dealNo: �ŷ���ȣ
	 * @return DB�� ���� �߰� �������� (���� = true, ���� = false)
	 */
	public boolean cancelDeal(int dealNo) {
		boolean resultCancel = false;
		try {
			// ȯ�һ���� ��Ÿ���� ȯ�ҽð� ������ �ش� �ŷ� �����Ϳ� �Է�
			String sql = "UPDATE SALE_INFO SET CANCEL = ? WHERE ORDER_NO = ?";
			conn = ConnectionManager.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);

			String order_day = getTime();
			pstmt.setString(1, order_day);
			pstmt.setInt(2, dealNo);

			if (pstmt.executeUpdate() == 1) {
				conn.commit();
				resultCancel = true;
			} else
				conn.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}	
		return resultCancel;
	}


	
	/**
	 * ����� �����ϴ� ȸ������ DB���� ����
	 * @param answer: ����ID
	 * @param clientIp: ������ ������ ������ IP
	 * @return DB�� ���� ���� �������� (���� = true, ���� = false)
	 */
	public boolean goodbye(String answer, String clientIp) {
		try {
			// DB �� �������� �˻�
//				���ӵ� IP������ �̿��� DB�� ��ϵ� ID �˻�
			String sql = "SELECT SERVER_INFO, STORE_ID FROM STORE_INFO WHERE SERVER_INFO = ? AND STORE_ID = ?";
			conn = ConnectionManager.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, clientIp);
			pstmt.setString(2, answer);
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				// �Է��� ID�� �������� IP ���� ��
//					=> Ÿ ������ ������ ��ȸ���� �̿� ����
				if(!rs.getString(1).equals(clientIp)||!rs.getString(2).equals(answer))
					return false;
			}
			
			// DB �� ��ϵ� ID�� IP������ ��ġ�ϴ� ������ ����
			sql = "DELETE FROM STORE_INFO WHERE SERVER_INFO = ? AND STORE_ID = ?";
			conn = ConnectionManager.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, clientIp);
			pstmt.setString(2, answer);
			if (pstmt.executeUpdate() == 1) {
				conn.commit();
				return true;
			}
			else conn.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}	
		return false;
	}
	
	
	
	public void close() {
		try {
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

