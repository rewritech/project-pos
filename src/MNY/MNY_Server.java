package MNY;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class MNY_Server {
	
	private ArrayList<String> ipArr = new ArrayList<>();
	private static MNY_ServerManager sm = new MNY_ServerManager();	
	
	public static void main(String[] args) {
		int port = 6666;	// 포트 번호
		
		try{
			// 포트번호로 서버소켓을 생성한다
			ServerSocket ssocket = new ServerSocket(port);
			System.out.println("[INFO] 서버가 클라이언트를 기다리고 있습니다");
			
			// 계속하여 클라이언트로부터의 연결을 기다린다
			while(true) {
				// 클라이언트로부터의 연결을 오기를 기다린다
				Socket socket = ssocket.accept();
				String clientIp = socket.getInetAddress().toString(); // 접속자 IP 반환 후 String 변환
				System.out.println("접속자 IP: " + clientIp);

				// 등록된 서버의 접속이면, 오브젝트 인풋, 아웃풋 스트림을 생성
				ObjectInputStream nois = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream noos = new ObjectOutputStream(socket.getOutputStream());

				// Runnable을 구현하는 SESServerTread 생성 & 실행
				MNY_ServerThread thread = new MNY_ServerThread(sm, clientIp, nois, noos);
				Thread t = new Thread(thread);
				t.start();
			}
		} catch(Exception e) {
			System.out.println("[ERR] 알 수 없는 에러");
			e.printStackTrace();
		}
	}
}