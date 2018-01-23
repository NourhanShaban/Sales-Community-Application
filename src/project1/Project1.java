
package project1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Project1 {

    
    
    static Socket socket;
    static DataInputStream dataInputStream;
    static DataOutputStream dataOutputStream;

    
    public static void main(String[] args) {
         java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NewJFrame1().setVisible(true);
            }
        });
         
         
          try {
              System.out.println("please enterserver ip");
              Scanner sc= new Scanner(System.in);
              String sip=sc.nextLine();
            socket = new Socket(sip, 1234);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

        } catch (Exception e) {
            System.out.println("client error");
        }
         
        
    }
    
}
