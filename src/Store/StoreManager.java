package Store;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import vo.MNYMenu;

public class StoreManager {
	private final int PORT = 6666;	// 포트 번호
	
	private Socket socket;
	private InputStream is;
	private OutputStream os;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private ArrayList<MNYMenu> list;

	/**
	 * 생성자
	 */
	public StoreManager() {
		try{
			// 포트 번호를 통해 소켓을 생성한다
			socket = new Socket("10.10.8.21", PORT);
			System.out.println("[INFO] 서버 소켓 생성 성공");
			
			// 스트림 생성
			is = socket.getInputStream();			
			os = socket.getOutputStream();	
			System.out.println("[INFO] 스트림 생성 성공");
			
			// 오브젝트 스트림 생성
			oos = new ObjectOutputStream(os);
			ois = new ObjectInputStream(is);
			System.out.println("[INFO] 오브젝트 스트림 생성 성공");
			
		} catch(Exception e) {
			System.out.println("[INFO] 접속 도중 에러가 나타났습니다");
			 e.printStackTrace();
			closeStreams();
			System.exit(0);
		}
	}
	
	/**
	 * 판매를 위한 ArrayList<MNYMenu> sellingList를 본사에 요청
	 * @return sellingList: 판매 상품 정보 준비
	 */
	public ArrayList<MNYMenu> menuSetter() {
		ArrayList<MNYMenu> sellingList = null;
//		Object[] request = { "판매준비", null };		
		Object[] request = { "販売準備", null };		
		sellingList = (ArrayList<MNYMenu>)this.sendRequest(request);
		return sellingList;
	}

	/**
	 * 판매된 내용인 ArrayList<MNYMenu> orderList를 본사에 발송 
	 * 	-> 판매 내용이 서버를 통해 본사 DB에 정상적으로 등록시 true 반환
	 * @param orderList: 주문된 상품을 담은 리스트
	 */
	public boolean makeDeal(ArrayList<MNYMenu> orderList) {
//		Object[] request={ "판매", orderList };		
		Object[] request={ "販売", orderList };		
		boolean resultDeal = (boolean) sendRequest(request);
		return resultDeal;
	}

	/**
	 * 거래번호에 해당하는 거래 기본정보(거래번호, 거래시간, 거래가격) 본사에 요청
	 * @param dealNo: 거래의 존재를 확인하려는 거래번호
	 * @return dealCheck: 거래 기본정보(거래번호, 거래시간, 거래가격)를 담을 ArrayList
	 */
	public ArrayList<String> checkDeal (int dealNo) {
		ArrayList<String> dealCheck = null;
//		Object[] request = { "거래확인", dealNo };
		Object[] request = { "取引確認", dealNo };
		dealCheck = (ArrayList<String>) sendRequest(request);
		return dealCheck;
	}
	
	/**
	 * 거래번호에 해당하는 거래정보를 본사에 요정
	 * 	-> 해당 거래번호의 거래정보 반환
	 * @param dealNo: 거래의 존재를 확인하려는 거래번호
	 * @return dealInfo: 거래된 주문의 상세 상품정보를 담은 리스트
	 */
	public ArrayList<MNYMenu> findDeal (int dealNo) {
		ArrayList<MNYMenu> dealInfo = null;
//		Object[] request = { "거래정보", dealNo };
		Object[] request = { "取引情報", dealNo };
		dealInfo = (ArrayList<MNYMenu>) sendRequest(request);
		return dealInfo;
	}
	
	/**
	 * 거래취소된  ArrayList<MNYMenu> orderList를 본사에 발송
	 * 	-> 거래취소 내용이 서버를 통해 본사 DB에 정상적으로 등록시 true 반환
	 * @param dealNo: 거래된 주문의 번호
	 */
	public boolean refundDeal(int dealNo) {
//		Object[] request={ "거래취소", dealNo };		
		Object[] request={ "取引の取り消し", dealNo };		
		boolean result = (Boolean) sendRequest(request);
		return result;
	}
	
	/**
	 * 서버로 요청을 보낸다
	 * @param request 서버로 보낼 메시지
	 * @return 받는 메시지
	 */
	public Object sendRequest(Object[] request){
		Object response = null;
		try{
			oos.writeObject(request);	
			response = ois.readObject();
		}catch(Exception e){
			e.printStackTrace();
		}
		return response;
	}
	
	/**
	 * 열어 놓은 스트림들을 모두 닫는다.
	 */
	public void closeStreams() {
		try {
			if(is != null) is.close();		// 인풋 스트림이 존재하면 닫는다
			if(os != null) os.close();		// 아웃풋 스트림이 존재시 닫음
			if(ois != null) ois.close();	// 오브젝트 인풋 스트림이 존재시 닫음
			if(oos != null) oos.close();	// 오브젝트 아웃풋 스트림이 존재시 닫음
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public boolean goodbye(String answer) {
		boolean result = false;
//		Object[] request = { "탈퇴", answer };
		Object[] request = { "脱退", answer };
		result = (boolean) sendRequest(request);
		return result;
	}

}
