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
 * MNY(MaNisYous) 매장 거래정보 관리로직 클래스
 * 주요 기능으로는 다음과 같다.
 * 1. 매장 판매용 메뉴 정보 검색
 * 2. 매장 판매 정보 저장
 * 3. 기존 거래정보 검색
 * 4. 거래취소 정보 저장
 * </pre>
 * */
public class MNY_ServerManager {
	
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private String sql;

	/**
	 * 매장에서 판매하는 메뉴 정보를 DB에서 가져와 전달
	 * 1) select 판매 메뉴타입
	 * 2) select 메뉴타입 기준으로 메뉴명과 가격 		-> (HashMap<String, Integer>)menuMap에 저장
	 * 3) select 메뉴타입 기준으로 옵션명과 옵션가격	-> (HashMap<String, Integer>)optionMap에 저장
	 * => 해당 정보 내용 에 저장
	 * => 메뉴타입별 MNYMenu 객체 ArrayList<MNYMenu>에 저장
	 * @return ArrayList<MNYMenu>
	 */
	public ArrayList<MNYMenu> menuSetter() {
		ArrayList<MNYMenu> sellingList = new ArrayList<>();
		
		conn = ConnectionManager.getConnection();
		try {
			// 1) 메뉴의 type DB검색 & 반환
			sql = "SELECT DISTINCT TYPE FROM MENU";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			ResultSet rs_Menu_Option = null; // type의 메뉴&옵션 정보들 검색결과 담을 ResultSet
			while(rs.next()){
				String type = rs.getString(1); // 판매상품의 type 반환
				
				HashMap<String, Integer> menuMap = new HashMap<>();	  // 메뉴 이름과 가격 담을 HashMap
				HashMap<String, Integer> optionMap = new HashMap<>(); // 옵션 이름과 가격 담을 HashMap
				
				// 검색된 type에 해당하는 메뉴명과 가격 DB 검색
				sql = "SELECT ITEM, PRICE FROM MENU WHERE TYPE = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, type);
				
				rs_Menu_Option = pstmt.executeQuery();
				// 하위 메뉴의 이름과 가격 정보를 모두 menuMap에 저장
				while(rs_Menu_Option.next()) 
					menuMap.put(rs_Menu_Option.getString(1), rs_Menu_Option.getInt(2));

				// 검색된 type에 해당하는 옵션명과 가격 DB 검색
				sql = "SELECT OPTION_NAME, ADD_COST FROM MENU_OPTION WHERE TYPE = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, type);
				rs_Menu_Option = pstmt.executeQuery();
				
				// 옵션의 이름과 가격 정보를 모두 menuMap에 저장
				while(rs_Menu_Option.next()) 
					optionMap.put(rs_Menu_Option.getString(1), rs_Menu_Option.getInt(2));
				
				// 검색으로 모인 판매상품 정보를 담을 객체들 sellingList에 저장
				sellingList.add(new MNYMenu(type, menuMap, optionMap));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		
		return sellingList;
	}
	
	
	/**
	 * 매장에서 판매된 메뉴정보를 DB에 저장
	 * 1) insert 주문된 내용 (거래번호 SALE_SEQ.NEXTVAL, 거래시간, 가격총합) DB 테이블 SALE_INFO에 저장
	 * 2) insert 주문된 리스트 내의 모든 메뉴의 정보(거래번호 SALE_SEQ.CURRVAL, 타입_주문메뉴명_옵션, 주문된 가격) DB 테이블 ORDER_INFO에 저장
	 * @param clientIp 
	 * @param orderList: 매장에서 주문된 메뉴들을 담은 리스트
	 * @return DB 저장 성공여부 (성공 = true, 실패 = false)
	 */
	public boolean makeDeal(ArrayList<MNYMenu> orderList, String clientIp) {
		boolean dealResult = false;

		try {
			conn = ConnectionManager.getConnection();
			conn.setAutoCommit(false);
			int sequence = makeSequnceNum();	// 중복 없는 거래번호 얻기 위해, 새로운 squence 번호 생성(SALE_INFO 입력)
			String client_ID = findClientName(clientIp); // 소켓으로 얻은 매장 IP정보로 매장 ID 검색&반환(SALE_INFO 입력)
			
			// 거래정보 DB입력 수행
			sql = "INSERT INTO SALE_INFO (ORDER_NO, STORE_ID, ORDER_TIME, WHOLE_PRICE) VALUES(?, ?, ?, ?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, sequence);
			pstmt.setString(2, client_ID);
			pstmt.setString(3, getTime()); // 현재 날짜와 시간 반환 메소드 사용
			pstmt.setInt(4, sumPrice(orderList)); // 주문된 상품들의 총합 계산 후 반환 메소드 사용

			// 거래정보에 해당하는 상세 거래정보 입력
			if (pstmt.executeUpdate() == 1) {
				sql = "INSERT INTO ORDER_INFO (ORDER_NO, ORDER_ITEM, ORDER_PRICE, QUANTITY) VALUES(?, ?, ?, ?)";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, sequence);
				// 올바른 입력을 확인하기 위한 insertCounter 생성
				int insertCounter = 0;
				for (MNYMenu m : orderList) {
					pstmt.setString(2, m.getOrder_item());
					pstmt.setInt(3, m.getOrder_price());
					pstmt.setInt(4, m.getCount());
					if (pstmt.executeUpdate() == 1) insertCounter++; // 정보 DB 입력 성공시 1증가
					else break; // 입력 종료 혹은 입력 실패시 반복문 탈출
				}
				
				// DB입력 결과 확인
//					1) orderList.size() == 주문된 상품의 수
//					2) insertCounter == DB 입력 성공 행의 수
//						두 수의 크기가 같을 시 입력 성공 -> dealResult = true & commt으로 DB변화 확정
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
	 * 주문된 상품들 가격의 총 합 계산 후 반환 
 	 * @return sum: 리스트에 감긴 주문상품 객체들 가격의 총합
	 */
	private int sumPrice(ArrayList<MNYMenu> list) {
		int sum = 0;
		for (MNYMenu m : list)
			sum += m.getLast_price(); // 옵션이 반영된 가격과 수량이 곱해진 가격을 찾아 합산
		return sum;
	}

	
	/**
	 * 현재 날짜와 시간을 구하고 반환
 	 * @return order_day: 현재 [년/월/일] 시:분:초
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
	 * 거래번호로 사용될 최신의 시퀀스 번호 발생 후 반환
 	 * @return sequence: DB의 SALE_SEQ.NEXTVAL로 최신화된 번호
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
	 * 주문된 거래정보를 입력하기 위해 필요한 매장정보 검색 및 반환
	 * <ip 주소에 해당하는 매장ID 부재시 검색결과 부재 -> 미등록 접속자 프로그램이용 방지>
 	 * @return clientIp: 현재 접속 중인 매장의 IP 번호
	 */
	private String findClientName(String clientIp) throws SQLException {
		sql = "SELECT STORE_ID FROM STORE_INFO WHERE SERVER_INFO = ?";
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, clientIp);
		rs = pstmt.executeQuery();
		String client_ID = ""; // 검색 결과를 담기위한 String 껍데기 생성
		rs.next();
		client_ID = rs.getString(1);
		return client_ID;
	}

	
	/**
	 * 거래번호에 해당하는 기본정보 검색
	 * DB 테이블 SALE_INFO에서 주문된 내용 (거래번호SALE_SEQ, 거래시간, 가격총합)
	 * @param clientIp 
	 * @param dealNo: 거래번호
	 * @return dealed: 거래 기본정보를 담은 리스트 <거래번호, 거래시간, 가격총합>
	 */
	public ArrayList<String> checkDeal(int dealNo, String clientIp) {
		ArrayList<String> dealed = new ArrayList<>();
		// Code Here
		try {
			// 거래정보를 검색 (금액은 통화 문자형으로 변환 출력)
//				CANCEL칼럼: 환불된 거래는 환불시간정보 저장 => 환불되지 않은 NULL 조건 
//			sql = "SELECT ORDER_TIME, TO_CHAR(WHOLE_PRICE, 'L9,999,999,999,999') "
			sql = "SELECT ORDER_TIME, TO_CHAR(WHOLE_PRICE, '9,999,999,999,999') "
					+ "FROM SALE_INFO WHERE ORDER_NO = ? "
					+ "AND CANCEL IS NULL AND STORE_ID = ("
					+ "SELECT STORE_ID FROM STORE_INFO WHERE SERVER_INFO = ?)";
//						서브쿼리: 서버 접속자의 IP정보를 이용 검색 -> DB등록 IP에 해당하는 ID 검색
//							=> IP와 ID 비일치 -> 거래정보 제공 X
			conn = ConnectionManager.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, dealNo);
			pstmt.setString(2, clientIp);
			rs = pstmt.executeQuery();

			// 거래정보 ArrayList<String>에 저장
			if (rs.next()) {
				dealed.add(String.valueOf(dealNo)); // (int)거래번호 String으로 형변환 저장
				dealed.add(rs.getString(1));
				dealed.add(String.valueOf(rs.getString(2))+("円"));
			} else dealed = null;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return dealed;
	}
	
	/**
	 * 거래번호에 해당하는 판매정보들을 검색
	 * DB 테이블 ORDER_INFO에서 주문된 상품 정보 (타입_주문메뉴명_옵션, 주문된 가격)
	 * @param dealNo: 거래번호
	 * @return deal_Items: 거래된 상품 객체 MNYMenu들을 담은 리스트
	 */
	public ArrayList<MNYMenu> findDeal(int dealNo) {
		ArrayList<MNYMenu> deal_Items = new ArrayList<>();
		try {
			// 거래번호에 해당하는 거래상세 내역 검색
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
	 * 테이블 SALE_INFO에 거래번호를 기준으로 환불사실(환불시간) 입력
	 * @param dealNo: 거래번호
	 * @return DB에 정보 추가 성공여부 (성공 = true, 실패 = false)
	 */
	public boolean cancelDeal(int dealNo) {
		boolean resultCancel = false;
		try {
			// 환불사실을 나타내는 환불시간 정보를 해당 거래 데이터에 입력
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
	 * 계약을 해지하는 회원정보 DB에서 삭제
	 * @param answer: 매장ID
	 * @param clientIp: 서버에 접속한 매장의 IP
	 * @return DB에 정보 삭제 성공여부 (성공 = true, 실패 = false)
	 */
	public boolean goodbye(String answer, String clientIp) {
		try {
			// DB 내 매장정보 검색
//				접속된 IP정보를 이용해 DB내 등록된 ID 검색
			String sql = "SELECT SERVER_INFO, STORE_ID FROM STORE_INFO WHERE SERVER_INFO = ? AND STORE_ID = ?";
			conn = ConnectionManager.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, clientIp);
			pstmt.setString(2, answer);
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				// 입력한 ID와 서버접속 IP 정보 비교
//					=> 타 매장의 삭제나 비회원의 이용 제한
				if(!rs.getString(1).equals(clientIp)||!rs.getString(2).equals(answer))
					return false;
			}
			
			// DB 내 등록된 ID와 IP정보와 일치하는 데이터 삭제
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

