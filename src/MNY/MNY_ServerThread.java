package MNY;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import vo.MNYMenu;


/**
 * SES 서버의 다중접속 환경을 구현하기 위한 스레드 클래스
 * SESServer에서 사용자의 접속이 이루어지면 SESServerThread 객체를 생성하여
 * run() 메서드에서 ObjectInputStream 와 ObjectOutputStream을 이용하여 클라이언트와 독립적인 통신을 수행한다.
 * */
public class MNY_ServerThread implements Runnable {
	
	private MNY_ServerManager sm;	
	private String clientIp;
	private ObjectInputStream nois;
	private ObjectOutputStream noos;
	private boolean exit = false;

	public MNY_ServerThread(MNY_ServerManager sm, String clientIp, ObjectInputStream nois, ObjectOutputStream noos) {
		this.sm = sm;
		this.clientIp = clientIp;
		this.nois = nois;
		this.noos = noos;
	}

	@Override
	public void run() {
		while(!exit){	
			try{
				// 오브젝트 스트림으로부터 오브젝트를 읽어들임
				Object[] readObejects = (Object[]) nois.readObject();
				
				String caseString = (String) readObejects[0];	// 명령어
				Object caseObject = readObejects[1];			// 오브젝트
				
				switch(caseString) {
//					case "판매준비":	
					case "販売準備":	
						noos.writeObject(sm.menuSetter());												
						break;
						
//					case "판매":
					case "販売":
						noos.writeObject(sm.makeDeal((ArrayList<MNYMenu>)caseObject, clientIp));											
						break;
						
//					case "거래확인":	
					case "取引確認":	
						noos.writeObject(sm.checkDeal((int)caseObject, clientIp));											
						break;
						
//					case "거래정보":	
					case "取引情報":	
						noos.writeObject(sm.findDeal((int)caseObject));											
						break;
					
//					case "거래취소":	
					case "取引の取り消し":	
						noos.writeObject(sm.cancelDeal((int)caseObject));
						break;
						
//					case "탈퇴":
					case "脱退":
						noos.writeObject(sm.goodbye((String)caseObject, clientIp));
					default:
				}
			} catch(IOException ioe) {
				exit = true;
				System.out.println(ioe.getMessage());
			} catch(ClassNotFoundException cce) {
				exit = true;
				System.out.println(cce.getMessage());
			}
		}//while
	}
}
