package Store;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import vo.MNYMenu;

// 매장의 거래에 해당하는 기능을 수행하는 JFrame
public class StoreUI_trade extends JFrame{

	private JPanel 	panel_type1, panel_type2;
	private JLabel label_priceName, label_price;
	private JButton btn_insert,btn_order, btn_cancel;
	private BtnHandler mybtn = new BtnHandler();
	
	private JRadioButton 	radio_type1, radio_type2,
					radio_menu1_1, radio_menu1_2, radio_menu1_3,
					radio_option1_1, radio_option1_2,
					radio_menu2_1, radio_menu2_2, radio_menu2_3,
					radio_option2_1, radio_option2_2;
	
	private ButtonGroup group_types = new ButtonGroup();
	private ButtonGroup group_type1_menu = new ButtonGroup();
	private ButtonGroup group_type2_menu = new ButtonGroup();
	private ButtonGroup group_type1_option = new ButtonGroup();
	private ButtonGroup group_type2_option = new ButtonGroup();
	
	private JList orderJList;
	
	private ArrayList<MNYMenu> sellingList;
	private ArrayList<MNYMenu> orderList = new ArrayList<>();
	
	// 기능 추가 전까지 필요 없는 내용
	private ArrayList<JRadioButton> type1_radList = new ArrayList<>();
	private ArrayList<JRadioButton> type2_radList = new ArrayList<>();
	
	private StoreManager sm;
	
	public StoreUI_trade(StoreManager sm, ArrayList<MNYMenu> sellingList) {
		this.sm = sm;
		this.sellingList = sellingList;
		if(sellingList==null||sellingList.size()<=0){
			JOptionPane.showMessageDialog(null, "먼저 개장준비 버튼을 눌러주세요");
			dispose();
			return;
		}
		
		setTitle("메뉴판매");
		getContentPane().setLayout(null);
		
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();			// 스크린 사이즈 반환 보관
		int screenWidth = screenSize.width;					// 스크린 넓이 크기 보관
		int screenheight = screenSize.height;				// 스크린 높이 크기 보관
		setSize(screenSize.width/2, screenSize.height/2);
		setUndecorated(true);
		setLocationRelativeTo(null);
		
		
		// 타입 JRadioButton 설정
		MNYMenu type1 = sellingList.get(0);		// 판매 상품 type1 정보를 담은 객체 저장
		MNYMenu type2 = sellingList.get(1);		// 판매 상품 type2 정보를 담은 객체 저장
		
		radio_type1 = new JRadioButton(type1.getType(), true);	// type1의 JRadioButton 생성&설정(초기 선택)
		radio_type1.setBounds(10, 8, 105, 27);
		getContentPane().add(radio_type1);
		type_RadSet(radio_type1);
		group_types.add(radio_type1);
		
		radio_type2 = new JRadioButton(type2.getType());		// type2의 JRadioButton 생성&설정
		radio_type2.setBounds(10, 171, 105, 27);
		getContentPane().add(radio_type2);
		type_RadSet(radio_type2);
		group_types.add(radio_type2);
		
		
		// type1의 메뉴와 옵션을 담을 Panel 생성&설정
		panel_type1 = new JPanel();								
		panel_type1.setBounds(125, 0, 343, 163);
		getContentPane().add(panel_type1);
		panel_type1.setLayout(null);
		
		// type1의 하위 메뉴 이름 JRadioButton 설정
		Set<String> type1_menus = type1.getMenuMap().keySet();		// type1 메뉴명이 담긴 HashMap key -> Set 저장
		ArrayList<String> names_options = new ArrayList();			// type1 메뉴명이 담긴 Set 저장할 (ArrayList)names_options 생성
		for(String menu : type1_menus) names_options.add(menu);		// type1 메뉴명이 담긴 Set -> (ArrayList)names_options 저장
		
		radio_menu1_1 = new JRadioButton(names_options.get(0));
		radio_menu1_1.setBounds(50, 18, 139, 27);
		menu1_RadSet(radio_menu1_1);

		radio_menu1_2 = new JRadioButton(names_options.get(1));
		radio_menu1_2.setBounds(50, 49, 139, 27);
		menu1_RadSet(radio_menu1_2);
		
		radio_menu1_3 = new JRadioButton(names_options.get(2));
		radio_menu1_3.setBounds(50, 80, 139, 27);
		menu1_RadSet(radio_menu1_3);
		
		names_options.clear();
		
		// type1 하위 Option
		Set<String> type1_options = type1.getOptionMap().keySet();		// type1 옵션명이 담긴 HashMap key -> Set 저장
		for(String option : type1_options) names_options.add(option);	// type1 옵션명이 담긴 Set -> (ArrayList)names_options 저장
		
		radio_option1_1 = new JRadioButton(names_options.get(0));
		radio_option1_1.setBounds(220, 18, 139, 27);
		menu1_RadSet(radio_option1_1);
		
		radio_option1_2 = new JRadioButton(names_options.get(1));
		radio_option1_2.setBounds(220, 49, 139, 27);
		menu1_RadSet(radio_option1_2);
		
		names_options.clear();	// 재사용 위해 names_options 내부 비우기
		
		// type2 하위 Menu
		Set<String> type2_menus = type2.getMenuMap().keySet();			// type2 메뉴명이 담긴 HashMap key -> Set 저장
		for(String menu : type2_menus) names_options.add(menu);			// type2 메뉴명이 담긴 Set -> (ArrayList)names_options 저장
		
		panel_type2 = new JPanel();
		panel_type2.setLayout(null);
		panel_type2.setBounds(125, 165, 343, 163);
		getContentPane().add(panel_type2);
		
		radio_menu2_1 = new JRadioButton(names_options.get(0));
		radio_menu2_1.setBounds(50, 18, 139, 27);
		menu2_RadSet(radio_menu2_1);
		
		radio_menu2_2 = new JRadioButton(names_options.get(1));
		radio_menu2_2.setBounds(50, 49, 139, 27);
		menu2_RadSet(radio_menu2_2);
		
		radio_menu2_3 = new JRadioButton(names_options.get(2));
		radio_menu2_3.setBounds(50, 80, 139, 27);
		menu2_RadSet(radio_menu2_3);

		names_options.clear();	// 재사용 위해 names_options 내부 비우기
		
		// type2 하위 Menu의 Option
		Set<String> type2_options = type2.getOptionMap().keySet();		// type2 옵션명이 담긴 HashMap key -> Set 저장
		for(String option : type2_options) names_options.add(option);	// type2 옵션명이 담긴 Set -> (ArrayList)names_options 저장
		
		radio_option2_1 = new JRadioButton(names_options.get(0));
		radio_option2_1.setBounds(220, 18, 139, 27);
		menu2_RadSet(radio_option2_1);
		
		radio_option2_2 = new JRadioButton(names_options.get(1));
		radio_option2_2.setBounds(220, 49, 139, 27);
		menu2_RadSet(radio_option2_2);
		
		group_type1_menu.add(radio_menu1_1);
		group_type1_menu.add(radio_menu1_2);
		group_type1_menu.add(radio_menu1_3);
		group_type1_option.add(radio_option1_1);
		group_type1_option.add(radio_option1_2);
		
		group_type2_menu.add(radio_menu2_1);
		group_type2_menu.add(radio_menu2_2);
		group_type2_menu.add(radio_menu2_3);
		group_type2_option.add(radio_option2_1);
		group_type2_option.add(radio_option2_2);
		
//		btn_insert = new JButton("입력");
		btn_insert = new JButton("入力");
		btn_insert.setBounds(360, 369, 105, 27);
		getContentPane().add(btn_insert);
		
//		btn_order = new JButton("주문");
		btn_order = new JButton("注文");
		btn_order.setBounds(697, 408, 105, 42);
		btn_order.setEnabled(false);
		getContentPane().add(btn_order);
		
//		btn_cancel = new JButton("상위");
		btn_cancel = new JButton("上位");
		btn_cancel.setBounds(815, 408, 61, 42);
		getContentPane().add(btn_cancel);
		
//		label_priceName = new JLabel("주문가격 : ");
		label_priceName = new JLabel("注文値段: ");
		label_priceName.setBounds(482, 9, 79, 25);
		getContentPane().add(label_priceName);
		
		label_price = new JLabel("0");
		label_price.setBounds(568, 9, 270, 25);
		getContentPane().add(label_price);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(492, 46, 426, 282);
		getContentPane().add(scrollPane);
		
		orderJList = new JList();
		scrollPane.setViewportView(orderJList);
		
		btn_insert.addActionListener(mybtn);
		btn_order.addActionListener(mybtn);
		btn_cancel.addActionListener(mybtn);
		
		setVisible(true);
		
	}

	
	/**
	 * type1에 해당하는 메뉴와 옵션들의 라디오 버튼 세팅 
	 * @return radio_Btn: type1의 메뉴와 옵션의 라디오버튼
	 */
	private void menu1_RadSet(JRadioButton radio_Btn) {
		radio_Btn.setActionCommand(radio_Btn.getText());
		radio_Btn.addActionListener(mybtn);
		radio_Btn.setEnabled(true);
		type1_radList.add(radio_Btn);
		panel_type1.add(radio_Btn);
	}
	/**
	 * type2에 해당하는 메뉴와 옵션들의 라디오 버튼 세팅 
	 * @return radio_Btn: type2의 메뉴와 옵션의 라디오버튼
	 */
	private void menu2_RadSet(JRadioButton radio_Btn) {
		radio_Btn.setActionCommand(radio_Btn.getText());
		radio_Btn.addActionListener(mybtn);
		radio_Btn.setEnabled(false);
		type2_radList.add(radio_Btn);
		panel_type2.add(radio_Btn);
	}
	/**
	 * type에 해당하는 라디오 버튼 세팅 
	 * @return radio_Btn: type의 라디오버튼
	 */
	private void type_RadSet(JRadioButton radio_Btn) {
		radio_Btn.setActionCommand(radio_Btn.getText());
		radio_Btn.addActionListener(mybtn);
		group_types.add(radio_Btn);
	}
	
	
	class BtnHandler implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// type 라디오버튼 선택에 따른 초기화
			if(e.getSource()==radio_type1) {
				radioBtnSwitch();
				group_type2_menu.clearSelection();
				group_type2_option.clearSelection();
			}
			else if(e.getSource()==radio_type2) {
				radioBtnSwitch();
				group_type1_menu.clearSelection();
				group_type1_option.clearSelection();
			}
			
			
			// 입력 버튼 클릭에 따른 주문 메뉴 리스트에 추가
			if(e.getSource()==btn_insert){
				
				// 라디오버튼으로 선택된 메뉴 정보(type, order_item, order_price)들 저장 시작
				// 1) order_item
				String menu_option = null;
				menu_option = find_order_item();			// 라디오 버튼으로 선택된 상품명(order_item) 반환메소드 
//															menu와 option이 결합한 형태(menu_option)로
//															아래에서 menu와 option의 해당 price 구할때 사용
				
				if(menu_option == null) return;			// 라디오 버튼 미선택으로 상품면 반환 실패시 과정 종료
				
				
				// 2) type
				String type = group_types.getSelection().getActionCommand();	// 선택된 상품의 type 반환
				
				
				// 3) order_price
//				 		라디오버튼으로 선택된 type의 정보를 담고 있는 객체 탐색 
//						-> 객체 내 menuMap과 optionMap 활용 주문된 가격 order_price 계산
				
				// 3-1) 선택된 type와 sellingList의 type(DB제약:Primary Key)을 비교 -> 동일한 타입을 지니고 있는 객체 반환
				MNYMenu selected_menu = null;									// 선택된 상품의 정보를 담을 객체 껍데기(selected_menu) 설정
				for(MNYMenu check : sellingList) {								// type이 동일한 리스트 내 객체 탐색
					if(check.getType().equals(type)) selected_menu = check;		// type이 동일한 객체 selected_menu에 반환
				}
				
				// 3-2) 메뉴정보를 담고 있는 객체 내 (HashMap)menuMap과 (HashMap)optionMap에서 price를 찾을 key값 준비
				String[] split_menu_option = menu_option.split("_");	// order_item의 결합전 형태 menu_option를 "_" 기준으로 절단 반환
//																	-> menu_option[0] = menu, menu_option[1] = option
				
				// 3-3) 위에서 구한 key로 value인 price 반환 후 더하기(+) 연산 
				int price_menu_option = selected_menu.getMenuMap().get(split_menu_option[0]) 	// 해당 menu의 price 반환
									+ selected_menu.getOptionMap().get(split_menu_option[1]);	// 해당 option의 price 반환
				
				// 3-4) 주문된 메뉴 정보를 저장하는 객체 생성
				String order_item = type+"_"+menu_option;					// 주문된 상품정보 type_menu_option을 나타내는 문자열 생성
				MNYMenu menu = new MNYMenu(order_item, price_menu_option);	// 주문 상품정보를 담는 menu객체 생성
				
				// 3-5) 주문된 객체와 이미 존재하는 주문인지 확인
//						존재 -> 기존 객체 수량 증가(set.count())
//						부재 -> 생성된 객체를 주문리스트(ArrayList)orderList에 추가
				boolean exist = false;		// orderList 내 동일한 내용의 주문객체가 존재하는지 확인할 변수 (존재 = true)
				for(MNYMenu m : orderList){ 					
					if(m.getOrder_item().equals(order_item)) {	// 동일 order_item의 객체 존재 확인
						m.setCount();		// 리스트에 동일 order_item 존재 -> setCount() 객체 내 수량을 의미하는 count 1증가
						
						exist = true;		// orderList 내 동일 객체 존재 여부 (boolean)exist의 값 true 부여
					}
				}
				if(!exist) orderList.add(menu);		// 리스트 내 동일 order_item을 지는 menu 객체 부재시 orderList에 추가
				
				// 주문된 메뉴 표현 영역
				// 1) 주문된 메뉴정보를 담고 있는 최신 orderList 가시화
				orderJList.setListData(orderList.toArray());
				
				// 2) 주문된 메뉴들의 금액 총합 표현
				int price = 0;
				for(MNYMenu mm :orderList) price += mm.getLast_price();		// 주문된 모든 메뉴 가격 합산
//				label_price.setText(String.valueOf(price)+"원");				// 주문된 메뉴들 총가격 label에 표현
				label_price.setText(String.valueOf(price)+"￥");				// 주문된 메뉴들 총가격 label에 표현
				
				// 주문을 가능하게하는 정확한 메뉴가 입력시 비로소 주문 버튼 활성화 
				if(orderList.size()>0) btn_order.setEnabled(true);
			}
			
			// 주문 내용 본사에 전송 
			else if(e.getSource()==btn_order){
				// 주문된 리스트를 본사에 전송
				if(sm.makeDeal(orderList)) {
					orderList.clear();
					orderJList.setListData(orderList.toArray());
//					JOptionPane.showMessageDialog(null, "[ 주문 완료! ]");
					JOptionPane.showMessageDialog(null, "[ 注文 完了! ]");
					label_price.setText("0");
					// 주문된 정보가 없으면 버튼 비활성화
					if(orderList.size()<=0) btn_order.setEnabled(false);
				}
//				else JOptionPane.showMessageDialog(null, "주문 실패\n(서버문제)");
				else JOptionPane.showMessageDialog(null, "注文 失敗\n(서버문제)");

			}
			// 거래창 종료버튼
			else if(e.getSource()==btn_cancel){
				sellingList = null; // 주문된 리스트를 비움
				dispose();
			}
		}

		private String find_order_item() {
			String menu_option = null;		// 주문된 menu와 option을 저장할 String 껍데기 설정
			
			// type1 라디오버튼 선택 중 -> 해당 menu와 option 반환
			if(radio_type1.isSelected()){
				ButtonModel menu = group_type1_menu.getSelection();
				ButtonModel option = group_type1_option.getSelection();
				// type1의 menu나 option 라디오버튼 미선택 시 오류 메세지 전달
//				if(menu==null||option==null) JOptionPane.showMessageDialog(null, "메뉴 선택을 정확하게 해주세요.");
				if(menu==null||option==null) JOptionPane.showMessageDialog(null, "メニューの選択を正確にしてください。");
				// type1의 menu나 option 선택된 라디오버튼으로 menu와 option을 결합한 (String)menu_option 반환 
				else menu_option = menu.getActionCommand()+"_"+option.getActionCommand();
			
			// type2 라디오버튼 선택 중 -> 해당 menu와 option 반환	
			}else if(radio_type2.isSelected()){
				ButtonModel menu = group_type2_menu.getSelection();
				ButtonModel option = group_type2_option.getSelection();
				// type2의 menu나 option 라디오버튼 미선택 시 오류 메세지 전달
//				if(menu==null||option==null) JOptionPane.showMessageDialog(null, "메뉴 선택을 정확하게 해주세요.");
				if(menu==null||option==null) JOptionPane.showMessageDialog(null, "メニューの選択を正確にしてください。");
				// type2의 menu나 option 선택된 라디오버튼으로 menu와 option을 결합한 (String)menu_option 반환 
				else menu_option = menu.getActionCommand()+"_"+option.getActionCommand();
			}
			
			return menu_option;
		}

		private void radioBtnSwitch() {
			// 라디오버튼 radio_type1 선택 여부 반환
			boolean selected = radio_type1.isSelected();
			// 라디오버튼 radio_type1 선택 여부(true/false)와 동일하게 type1과 관련된 menu와 option의 버튼들 설정
			for(JRadioButton r : type1_radList) r.setEnabled(selected);	 
			// 라디오버튼 radio_type1 선택 여부(true/false)와 반대로 type2과 관련된 menu와 option의 버튼들 설정
			for(JRadioButton r : type2_radList) r.setEnabled(!selected);
		}
		
	}
}
