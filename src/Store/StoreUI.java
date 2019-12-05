package Store;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import vo.MNYMenu;

public class StoreUI extends JFrame{

	public static void main(String[] args) {
		StoreUI run = new StoreUI();
	}
	
	private JButton btn_menuSet, btn_trade, btn_refund, btn_exit, btn_GOODBYE;
	private StoreUI_trade store_trade;
	private StoreUI_refund store_refund;
	private BtnHandler mybtn = new BtnHandler();
	private StoreManager sm = new StoreManager();
	private ArrayList<MNYMenu> sellingList;			// 판매 내용 보관용

	
	
	public StoreUI() {
//		setTitle("매장관리 프로그램");
		setTitle("売り場管理のプログラム");
		
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();			// 스크린 사이즈 반환 보관
		int screenWidth = screenSize.width;					// 스크린 넓이 크기 보관
		int screenheight = screenSize.height;				// 스크린 높이 크기 보관
		setSize(screenSize.width/5, screenSize.height/5);
		setUndecorated(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
//		btn_trade = new JButton("거래");
		btn_trade = new JButton("取引");
		getContentPane().add(btn_trade, BorderLayout.CENTER);
		
//		btn_refund = new JButton("거래취소");
		btn_refund = new JButton("取引の取り消し");
		getContentPane().add(btn_refund, BorderLayout.EAST);
		
//		btn_exit = new JButton("종료");
		btn_exit = new JButton("終了");
		getContentPane().add(btn_exit, BorderLayout.SOUTH);
		
//		btn_menuSet = new JButton("개장준비");
		btn_menuSet = new JButton("開場準備");
		getContentPane().add(btn_menuSet, BorderLayout.WEST);
		
//		btn_GOODBYE = new JButton("계약해지");
		btn_GOODBYE = new JButton("契約キャンセル");
		getContentPane().add(btn_GOODBYE, BorderLayout.NORTH);
		
		btn_GOODBYE.addActionListener(mybtn);
		btn_menuSet.addActionListener(mybtn);
		btn_trade.addActionListener(mybtn);
		btn_refund.addActionListener(mybtn);
		btn_exit.addActionListener(mybtn);
		
		setVisible(true);
		
	}
	
	class BtnHandler implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// 상품 판매에 필요한 정보 setting
			if(e.getSource()==btn_menuSet){
				sellingList = sm.menuSetter(); // 서버에서 판매에 필요한 메뉴 정보 불러온다
				if(sellingList==null||sellingList.size()<=0){
					JOptionPane.showMessageDialog(null, "판매용 메뉴정보 setting 실패");
//				}else JOptionPane.showMessageDialog(null, "판매용 메뉴정보 setting 완료");
				}else JOptionPane.showMessageDialog(null, "メニュー の準備完了");
			}
			// 거래 실행창 생성
			else if(e.getSource()==btn_trade){
				reset();	// 다른 프레임 종료
				store_trade = new StoreUI_trade(sm, sellingList); // 판매 객체에 서버매니저, 판매정보 전달
			}
			// 환불 실행창 생성
			else if(e.getSource()==btn_refund){
				reset();	// 다른 프레임 종료
				store_refund = new StoreUI_refund(sm);			 // 환불 객체에 서버매니저 전달
			}
			else if(e.getSource()==btn_exit){
				reset();	// 다른 프레임 종료
				dispose();	// 프로그램 종료
			}
			// 계약해지 수행
			else if(e.getSource()==btn_GOODBYE){
				// 계약해지를 확인하는 부분. 해당 매장에 부여된 ID 입력 -> 본사 DB 해당 매장데이터 삭제 & 프로그램 이용제한
//				String answer = JOptionPane.showInputDialog("이용을 그만두시려면 \n매장ID를 입력하시오.");
				String answer = JOptionPane.showInputDialog("契約を解約したかったら\n売り場のアイディーを 入力してください。");
				if(sm.goodbye(answer)){
//					JOptionPane.showMessageDialog(null, "그동안 이용해주셔서 감사합니다.\n재사용을 원하시면 본사에 연락하세요.");
					JOptionPane.showMessageDialog(null, "その間利用していお疲れ様でした。\n再使用が欲しかったら本社に連絡してください。");
					try {
						Thread.sleep(2000);
						dispose();			// 종료
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}else{
//					JOptionPane.showMessageDialog(null, "ID입력 오류 혹은 미등록 매장입니다.");
					JOptionPane.showMessageDialog(null, "アイディー入力の間違いまたは未登録の売り場です。");

				}
			}
			
		}

		private void reset() {
			if(store_trade!=null) store_trade.dispose();
			if(store_refund!=null) store_refund.dispose();
		}
		
	}
	
	
}
