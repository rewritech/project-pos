package Store;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import vo.MNYMenu;

// 환불을 실행하는 창
public class StoreUI_refund extends JFrame{

	private JPanel panel_coffee;
	private JLabel label_orderInfo, label_orderNum, refund_infoTitle,
					label_refundNo, label_dealTime, label_dealPrice;
	private JButton btn_search,btn_refund, btn_cancel;
	private JTextField orderNo;
	
	private ButtonListener myBtn = new ButtonListener();

	private int dealNo;
	private ArrayList<String> dealInfo;
	private ArrayList<MNYMenu> dealList;
	private StoreManager sm;
	private JScrollPane scrollPane;
	private JList orderJList;
	
	public StoreUI_refund(StoreManager sm) {
		this.sm = sm;
		
		setTitle("\uBA54\uB274 \uC785\uB825");
		getContentPane().setLayout(null);
		
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();			// 스크린 사이즈 반환 보관
		int screenWidth = screenSize.width;					// 스크린 넓이 크기 보관
		int screenheight = screenSize.height;				// 스크린 높이 크기 보관
		setSize(screenSize.width/2, screenSize.height/2);
		setUndecorated(true);
		setLocationRelativeTo(null);
		
		panel_coffee = new JPanel();
		panel_coffee.setBounds(0, 0, 468, 396);
		getContentPane().add(panel_coffee);
		panel_coffee.setLayout(null);
		
		orderNo = new JTextField();
		orderNo.setBounds(14, 65, 218, 24);
		panel_coffee.add(orderNo);
		orderNo.setColumns(10);
		
//		JLabel label_orderNo = new JLabel("주문번호");
		JLabel label_orderNo = new JLabel("注文番号");
		label_orderNo.setBounds(14, 35, 62, 18);
		panel_coffee.add(label_orderNo);
		
//		btn_search = new JButton("조회");
		btn_search = new JButton("照会");
		btn_search.setBounds(296, 56, 99, 42);
		panel_coffee.add(btn_search);
		
//		refund_infoTitle = new JLabel("정보: ");
		refund_infoTitle = new JLabel("情報: ");
		refund_infoTitle.setFont(new Font("굴림", Font.BOLD, 20));
		refund_infoTitle.setBounds(14, 131, 381, 42);
		panel_coffee.add(refund_infoTitle);
		
//		label_refundNo = new JLabel("거래번호: ");
		label_refundNo = new JLabel("取引番号: ");
		label_refundNo.setFont(new Font("굴림", Font.BOLD, 20));
		label_refundNo.setBounds(14, 177, 381, 42);
		panel_coffee.add(label_refundNo);
		
//		label_dealTime = new JLabel("거래시간: ");
		label_dealTime = new JLabel("取引時間: ");
		label_dealTime.setFont(new Font("굴림", Font.BOLD, 20));
		label_dealTime.setBounds(14, 220, 381, 42);
		panel_coffee.add(label_dealTime);
		
//		label_dealPrice = new JLabel("거래금액: ");
		label_dealPrice = new JLabel("取引金額: ");
		label_dealPrice.setFont(new Font("굴림", Font.BOLD, 20));
		label_dealPrice.setBounds(14, 263, 381, 42);
		panel_coffee.add(label_dealPrice);
		
		JPanel panel = new JPanel();
		panel.setBounds(467, 0, 409, 396);
		getContentPane().add(panel);
		panel.setLayout(null);
		
//		label_orderInfo = new JLabel("주문정보 : ");
		label_orderInfo = new JLabel("注文情報 : ");
		label_orderInfo.setFont(new Font("굴림", Font.BOLD, 20));
		label_orderInfo.setBounds(14, 12, 118, 31);
		panel.add(label_orderInfo);
		
		label_orderNum = new JLabel("");
		label_orderNum.setBounds(97, 15, 286, 25);
		panel.add(label_orderNum);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(14, 81, 369, 303);
		panel.add(scrollPane);
		
		orderJList = new JList();
		scrollPane.setViewportView(orderJList);
		
//		btn_refund = new JButton("환불요청");
		btn_refund = new JButton("払い戻し");
		btn_refund.setEnabled(false);
		btn_refund.setBounds(640, 408, 100, 42);
		getContentPane().add(btn_refund);
		
//		btn_cancel = new JButton("취소");
		btn_cancel = new JButton("取り消し");
		btn_cancel.setBounds(750, 408, 100, 42);
		getContentPane().add(btn_cancel);
		
		btn_cancel.addActionListener(myBtn);
		btn_refund.addActionListener(myBtn);
		btn_search.addActionListener(myBtn);
		setVisible(true);
	}
	
	class ButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			// 주문한 거래를 확인하는 버튼
			if(e.getSource()==btn_search) {
				String dealNoStr = orderNo.getText();	// 사용자가 입력한 주문번호를 받환 받음
				if(dealNoStr.equals("")) {
//					JOptionPane.showMessageDialog(null, "숫자를 입력해주세요.");
					JOptionPane.showMessageDialog(null, "数字を入力してください。");
					return;
				}
				
				String[] strArrInput = dealNoStr.split("");// 사용자 입력 확인을 위해 문자 단위로 잘라 저장
				String strInput = "";					// 사용자가 입력한 주문번호가 숫자인지 확인할 빈 String 생성 
				for(String s : strArrInput){
					for(int i=0; i<10; i++){
						if(s.equals(String.valueOf(i))){// 사용자 입력 주문번호가 숫자일 시, 위의 (String)strInput에 추가
							strInput += s;
						}
					}
				}
				
				// 사용자 입력 주문번호가 숫자일 시, 위의 (String)strInput에 추가
				if(!dealNoStr.equals(strInput)) {		
//					JOptionPane.showMessageDialog(null, "숫자를 입력해주세요.");
					JOptionPane.showMessageDialog(null, "数字を入力してください。");
					return;
				}
				
				// 사용사 입력 문자 다시 int형으로 변환
				dealNo = Integer.valueOf(orderNo.getText());
				dealInfo = sm.checkDeal(dealNo);	// 본사에 해당번호의 주문 확인요청 
				if(dealInfo == null || dealInfo.size()<=0) {
//					JOptionPane.showMessageDialog(null, "해당 거래가 없습니다.");
					JOptionPane.showMessageDialog(null, "該当取引がないです。");
					orderNo.setText("");
//					label_refundNo.setText("거래번호: ");
					label_refundNo.setText("取引番号: ");
//					label_dealTime.setText("거래시간: ");
					label_dealTime.setText("取引時間: ");
//					label_dealPrice.setText("거래금액: ");
					label_dealPrice.setText("取引金額: ");
					dealList.clear();
					orderJList.setListData(dealList.toArray());
					btn_refund.setEnabled(false);
					return;
				}
				
				// 주문내역 존재시 레이블에 정보 반환
//				label_refundNo.setText("거래번호: "+dealInfo.get(0));
//				label_dealTime.setText("거래시간: "+dealInfo.get(1));
//				label_dealPrice.setText("거래금액: "+dealInfo.get(2));
				label_refundNo.setText("取引番号: "+dealInfo.get(0));
				label_dealTime.setText("取引時間: "+dealInfo.get(1));
				label_dealPrice.setText("取引金額: "+dealInfo.get(2));
				
				dealList = sm.findDeal(dealNo); // 주문번호에 해당하는 상세 거래 내역 리스트 요청
				orderJList.setListData(dealList.toArray());
				
				btn_refund.setEnabled(true); // 거래 정보 존재시 환불버튼 활성화
				
			}
			// 환불을 실행
			if(e.getSource()==btn_refund){
//				if(!sm.refundDeal(dealNo))JOptionPane.showMessageDialog(null, "거래취소 실패");
				if(!sm.refundDeal(dealNo))JOptionPane.showMessageDialog(null, "取引の取り消しの失敗");
				else {
//					JOptionPane.showMessageDialog(null, "거래취소 완료");
					JOptionPane.showMessageDialog(null, "取引の取り消しの完了");
//					refund_infoTitle.setText("정보: ");
					refund_infoTitle.setText("情報: ");
					dealList.clear();
					orderJList.setListData(dealList.toArray());
//					label_refundNo.setText("거래번호: ");
					label_refundNo.setText("取引番号: ");
//					label_dealTime.setText("거래시간: ");
					label_dealTime.setText("取引時間: ");
//					label_dealPrice.setText("거래금액: ");
					label_dealPrice.setText("取引金額: ");
					orderNo.setText("");
					btn_refund.setEnabled(false); // 환불 비활성화
				}
			}
			// 환불창 종료
			if(e.getSource()==btn_cancel){
				dispose();
			}
			
			
			
			
		}
		
		
		
		
		
	}
}
			
