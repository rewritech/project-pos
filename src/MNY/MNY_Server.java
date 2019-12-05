package MNY;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class MNY_Server {
	
	private ArrayList<String> ipArr = new ArrayList<>();
	private static MNY_ServerManager sm = new MNY_ServerManager();	
	
	public static void main(String[] args) {
		int port = 6666;	// ��Ʈ ��ȣ
		
		try{
			// ��Ʈ��ȣ�� ���������� �����Ѵ�
			ServerSocket ssocket = new ServerSocket(port);
			System.out.println("[INFO] ������ Ŭ���̾�Ʈ�� ��ٸ��� �ֽ��ϴ�");
			
			// ����Ͽ� Ŭ���̾�Ʈ�κ����� ������ ��ٸ���
			while(true) {
				// Ŭ���̾�Ʈ�κ����� ������ ���⸦ ��ٸ���
				Socket socket = ssocket.accept();
				String clientIp = socket.getInetAddress().toString(); // ������ IP ��ȯ �� String ��ȯ
				System.out.println("������ IP: " + clientIp);

				// ��ϵ� ������ �����̸�, ������Ʈ ��ǲ, �ƿ�ǲ ��Ʈ���� ����
				ObjectInputStream nois = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream noos = new ObjectOutputStream(socket.getOutputStream());

				// Runnable�� �����ϴ� SESServerTread ���� & ����
				MNY_ServerThread thread = new MNY_ServerThread(sm, clientIp, nois, noos);
				Thread t = new Thread(thread);
				t.start();
			}
		} catch(Exception e) {
			System.out.println("[ERR] �� �� ���� ����");
			e.printStackTrace();
		}
	}
}