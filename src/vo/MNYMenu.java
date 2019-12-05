package vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;

public class MNYMenu implements Serializable {

	private String type;				// 상품의 타입을 결정 
	private HashMap<String, Integer> menuMap = new HashMap<>();		// 상품 타입에 맞는 하위 메뉴이름과 가격 저장
	private HashMap<String, Integer> optionMap = new HashMap<>();	// 상품 타입에 맞는 옵션 이름과 가격 저장
	
	private String order_item;			// 주문된 상품의 이름 ex) 커피, 아메리카노, 옵션L -> 커피_아메리카노_L 저장
	private int order_price = 0;		// 주문된 상품의 가격 ex) 아메리카노 4000 + 옵션 L 500 = 4500 저장
	private int count = 0;				// 주문된 수량, 객체 생성시 1, order_item가 동일한 객체 생성시 1 증가(count++)
	
	/**
	 * DB에서 가져온 매장판매용 메뉴정보(메뉴타입, <상품명&상품가격>, <옵션명&옵션가격>)를 저장하는 MENUMenu 객체 생성
	 * @param type 판매하는 메뉴 타입 (ex. coffee)
	 * @param menuList: 타입에 해당하는 <상품명, 상품가격> 목록 (ex. <아메리카노, 4000>)
	 * @param optionMap: 메뉴에 해당하는 <옵션명, 옵션가격> 목록 (ex. <L, 500>)
	 */
	public MNYMenu(String type, HashMap<String, Integer> menuMap, HashMap<String, Integer> optionMap) {
		this.type = type;				// ex) 커피
		this.menuMap = menuMap;			// ex) 4000
		this.optionMap = optionMap;		// ex) 아메리카노
	}
	
	/**
	 * 매장에서 주문된 메뉴정보(메뉴타입, 상품명, 가격)를 저장하는 MENUMenu 객체 생성
	 * @param type: 판매하는 메뉴 타입 (ex. coffee)
	 * @param order_item: 주문된 옵션이 반영된 판매상품 이름 (ex. 커피_아메리카노_L)
	 * @param order_price: 주문된 옵션이 반영된 판매상품 가격 (ex. 4000+500)
	 */
	public MNYMenu(String order_item, int order_price) {
		this.order_item = order_item;		// ex) 커피_아메리카노_L
		this.order_price = order_price;		// ex) 4000+500
		this.count = 1;						// 객체 생성 == 메뉴입력을 의미 -> 수량 의미하는 필드변수 count = 1
	}

	/**
	 * 매장에서 주문된 메뉴정보(메뉴타입, 상품타입_상품명_옵션명, (가격+옵션가격)*수량)를 보여주기 위한 메소드
	 */
	@Override
	public String toString() {
//		return "메뉴:  " + order_item + " ,  가격:  " + (order_price*count)+"원,  수량:  " + count+"개";
		return "メニュー:  " + order_item + " ,  値段:  " + (order_price*count)+"円,  数量:  " + count+"個";
	}
	
	/**
	 * 동일한 타입,메뉴,옵션 선택시 수량을 증가시키는 메소드
	 */
	public void setCount() {
		this.count++;
	}
	
	/**
	 * 주문상품과 수량을 반영한 정보로 총가격 계산에 사용
	 */
	public int getLast_price() {
		return this.order_price*this.count;
	}

	public String getType() {
		return type;
	}

	public String getOrder_item() {
		return order_item;
	}

	public int getOrder_price() {
		return order_price;
	}
	
	public HashMap<String, Integer> getMenuMap() {
		return menuMap;
	}

	public HashMap<String, Integer> getOptionMap() {
		return optionMap;
	}

	public int getCount() {
		return count;
	}

	
}
