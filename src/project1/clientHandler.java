
package project1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import static java.lang.Integer.parseInt;
import java.net.*;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

class clientHandle extends Thread {
    Socket c;

    public clientHandle (Socket c) {
        this.c = c;
    }
    
    public synchronized String ReadBalance(String e){
         String sub2 ="";
        try
        {
         FileReader file = new FileReader("Accounts2.txt");
         BufferedReader Reader = new BufferedReader(file);
       String Line = Reader.readLine();
        while (Line != null) {   
            String sub = Line.substring(0, Line.indexOf(","));
            if(sub.matches(e)){
                sub2 = Line.substring(Line.indexOf(",")+1);
                break;
            }
            Line = Reader.readLine();
        }
        Reader.close();
        
        }
        catch(Exception ex)
        {
            
            System.out.println("file not found");
        }      
       return sub2; 
    }

    public synchronized void replaceSelected(String replaceWith, String Line) {
        
        
         try
             {
             File file = new File("Accounts2.txt");
             BufferedReader reader = new BufferedReader(new FileReader(file));
             String line = "", oldtext = "";
             while((line = reader.readLine()) != null)
                 {
                 oldtext += line + "\r\n";
             }
             reader.close();

             String replacedtext  = oldtext.replaceAll(Line, replaceWith);
             FileWriter writer = new FileWriter("Accounts2.txt");
             writer.write(replacedtext);
             writer.close();

         }
         catch (IOException ioe)
             {
             ioe.printStackTrace();
         }
        
        
}
    
    public synchronized void WriteInFile(String user,String owner, String item, String price){
        
        String path = ""+user+"/buys.txt";
         File f = new File(path);
         try{
         f.createNewFile();
         }catch(Exception ex){
             System.out.println("File exists.");
         }
         
         try{
        FileWriter fileWriter=new FileWriter(f,true);
        BufferedWriter writer = new BufferedWriter(fileWriter);
              writer.append(item+","+price);
              writer.newLine();
                writer.close();
            
        }catch(IOException e){
            System.out.println("New line is not appended");
        }
         
         path = ""+owner+"/sold.txt";
         f = new File(path);
         try{
         //f.getParentFile().mkdirs(); 
         f.createNewFile();
         }catch(Exception ex){
             System.out.println("File exists.");
         }
         
         try{
        FileWriter fileWriter=new FileWriter(f,false);
        BufferedWriter writer = new BufferedWriter(fileWriter);
              writer.append(item+","+price);
              writer.newLine();
                writer.close();
            
        }catch(IOException e){
            System.out.println("New line is not appended");
        }
    }
    
    public synchronized int buyComponents(String owner,String item,int price,String user){
        
        int flag =0;
        try
        {
         FileReader file = new FileReader("Accounts2.txt");
         BufferedReader Reader = new BufferedReader(file);
       String Line = Reader.readLine();
        while (Line != null) { 
            String name = Line.substring(0, Line.indexOf(","));
            if(name.equals(user)){
                int m = parseInt(Line.substring(Line.indexOf(",")+1)) ; 
                if(m >= price){
                    m = m - price;
                    String r = String.valueOf(m);
                    replaceSelected(user + "," + r,Line);
                    flag = 1;
                }
                break;
            }
            
            Line = Reader.readLine();
        }
        
        Reader.close();
         if(flag == 1){
             
             String path = ""+owner+"/Sells.txt";
        File inputFile = new File(path);
        File tempFile = new File(""+owner+"/temp.txt");
        
        try{
            tempFile.createNewFile();
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String lineToRemove = item +","+ String.valueOf(price);
        String currentLine;

        while((currentLine = reader.readLine()) != null) {
              if(currentLine.equals(lineToRemove)) continue;
              writer.write(currentLine + System.getProperty("line.separator"));
        }
        writer.close(); 
        reader.close(); 
        inputFile.delete();
        tempFile.renameTo(inputFile);
        
        }catch(Exception e){
            System.out.println("Error in buyComponents Method!");
        }
             
             BufferedReader Reader2 = new BufferedReader(new FileReader("Accounts2.txt"));
             Line = Reader2.readLine();
             while(Line != null){
                 String name2 = Line.substring(0, Line.indexOf(","));
                 if(name2.equals(owner)){
                      int m = parseInt(Line.substring(Line.indexOf(",")+1));
                      m = m+price;
                      String r = String.valueOf(m);
                      replaceSelected(owner + "," + r,Line);
                      break;
                 }
                 Line = Reader2.readLine();
             }
         }
        
        
        }
        catch(Exception e)
        {
            
            System.out.println("file not found");
        }         
        
        return flag;
    
    }
    
    public synchronized  void FillTable(String a, int num){
        
        String line = null;
        String path ="";
        if( num == 1){
        path = ""+a+"/Sells.txt";
        }
        else if(num == 3){
            path = ""+a+"/Buys.txt";
        }
        else if(num == 2){
            path = ""+a+"/Sold.txt";
        }
        File f = new File(path);
        try {
            if(f.exists()){
            new DataOutputStream(c.getOutputStream()).writeUTF("Exists");
            BufferedReader br = new BufferedReader(new FileReader(path));

            while ((line = br.readLine()) != null) {
                new DataOutputStream(c.getOutputStream()).writeInt(1);
                Vector<String> data = new Vector();// this is important
                StringTokenizer st1 = new StringTokenizer(line, ",");
                while (st1.hasMoreTokens()) {
                    String nextToken = st1.nextToken();
                    data.add(nextToken);

                }
                new DataOutputStream(c.getOutputStream()).writeInt(data.size());
                for(int i=0;i<data.size();i++){
                    new DataOutputStream(c.getOutputStream()).writeUTF(data.get(i));
                }
            }
            new DataOutputStream(c.getOutputStream()).writeInt(0);

            br.close();
            }
            else new DataOutputStream(c.getOutputStream()).writeUTF("Not Exists");
        } catch (Exception ex) {
            System.out.println("JTable Error !");
        }
        
        
    }
    
    public synchronized  void FillTable2(String userName){
        
        
        try {
            FileReader file = new FileReader("Accounts.txt");
            BufferedReader Reader = new BufferedReader(file);
            String Line = Reader.readLine();
            while (Line != null) {
                int k = Line.indexOf(',');
                String sub = Line.substring(0, k);
                String path = ""+sub+"/Sells.txt";
                File f = new File(path);
                if(f.exists()&& !sub.matches(userName))
                {
                     
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line = null;
            while ((line = br.readLine()) != null) {
                new DataOutputStream(c.getOutputStream()).writeInt(1);
                Vector<String> data = new Vector();// this is important
                data.add(sub);
                StringTokenizer st1 = new StringTokenizer(line, ",");
                while (st1.hasMoreTokens()) {
                    String nextToken = st1.nextToken();
                    data.add(nextToken);
                }
                new DataOutputStream(c.getOutputStream()).writeInt(data.size());
                for(int i=0;i<data.size();i++){
                    new DataOutputStream(c.getOutputStream()).writeUTF(data.get(i));
                } 
            }
            br.close();

        } catch (Exception ex) {
            System.out.println("JTable Error !");
        }
                }
                Line = Reader.readLine();
                
            }
             new DataOutputStream(c.getOutputStream()).writeInt(0);
             Reader.close();
        } catch (IOException ex) {
            System.out.println("Error in table2");
        }
        
    }
    
    public synchronized int signUp(String pass,String userName,String deposite){
        
     //  String regax="[a-z]+|[A-Z]+[0-9]*@[a-z]+.com";
        int returned=0;
        //if(mail.matches(regax)){
         returned=2;
           File file=new File("Accounts.txt");
        try{
        FileWriter fileWriter=new FileWriter(file,true);
             BufferedWriter writer = new BufferedWriter(fileWriter);
                writer.append(userName+","+pass);
                writer.newLine();
                
                writer.close();
            
        }catch(IOException e){
            System.out.println("New line is not appended");
        }
        
        File file2=new File("Accounts2.txt");
        try{
        FileWriter fileWriter=new FileWriter(file2,true);
             BufferedWriter writer = new BufferedWriter(fileWriter);
                writer.append(userName+","+deposite);
                writer.newLine();
                writer.close();
            
        }catch(IOException e){
            System.out.println("New line is not appended");
        }
        
        

        return returned;
    }
    
    
    public synchronized int logIn(String em,String pass)
    {
        String Regex=em+","+pass;
        int returned=0;
         
        try
        {
         FileReader file = new FileReader("Accounts.txt");
         BufferedReader Reader = new BufferedReader(file);
       String Line = Reader.readLine();
        while (Line != null) {   
            if(Regex.matches(Line))
                    {
                        returned=1;
                        break;
                    }
            else
            {
                returned=0;   
            }
            Line = Reader.readLine();
        }
        Reader.close();
        
        }
        catch(Exception e)
        {
            
            System.out.println("file not found");
        }      
      return returned;  
        
    }    
    
     public synchronized void sellComponents(LinkedList<String> s,LinkedList<String> p,String n)
     {
          
        File theDir = new File(n);
// if the directory does not exist, create it
    if (!theDir.exists()) {
    
    try{
        theDir.mkdirs();
    } 
    catch(SecurityException se){
        System.out.println("ERROR!!");
    }   
    }   
         String path = ""+n+"/Sells.txt";
         File f = new File(path);
         try{
         f.createNewFile();
         }catch(Exception ex){
             System.out.println("File exists.");
         }
         
         try{
        FileWriter fileWriter=new FileWriter(f,false);
        BufferedWriter writer = new BufferedWriter(fileWriter);
        for(int i=0;i<s.size();i++)
         {
              writer.append(s.get(i)+","+p.get(i));
              writer.newLine();
         }
                writer.close();
            
        }catch(IOException e){
            System.out.println("New line is not appended");
        }
         
     
     }
    @Override
    public void run() {
        try {
            DataOutputStream dos = new DataOutputStream(c.getOutputStream());
            DataInputStream dis = new DataInputStream(c.getInputStream());
            String name="";
            while (true) {

                    int counter=dis.readInt();
            switch(counter)
            {
                case 1:
                    String email=dis.readUTF();
                    String password=dis.readUTF();
                   int found= logIn(email,password);
                   dos.writeInt(found);
                 break;
                 
                case 2:
                    
                    String userName=dis.readUTF();
                    String newPass=dis.readUTF();
                    String deposite=dis.readUTF();
                    int newSignUp=signUp(newPass,userName,deposite);
                    dos.writeInt(newSignUp);
                   
                    break;
                    
                case 3:

                    LinkedList<String> sells = new LinkedList<>();
                    LinkedList<String> price = new LinkedList<>();
                    String n = dis.readUTF();
                     int size=dis.readInt();
                     for(int i=0;i<size;i++)
                     {
                         sells.add(dis.readUTF());
                         price.add(dis.readUTF());
                     }
                     sellComponents(sells,price,n);
                     dos.writeInt(1);
                    break;
                    
                case 4:

                    String owner = dis.readUTF();
                    String item = dis.readUTF();
                    int pricee = parseInt(dis.readUTF());
                    String user = dis.readUTF();
                    int f = buyComponents(owner,item,pricee,user);
                    if (f == 1)
                    {
                        WriteInFile(user,owner,item,String.valueOf(pricee));
                    }
                    dos.writeInt(f);
                    break;
                    
                case 20:
                    
                    FillTable(dis.readUTF(),1);
                    break;
                    
                case 21:
                    
                    FillTable2(dis.readUTF());
                    break;
                    
                case 22:
                    String nn = dis.readUTF();
                    String b = ReadBalance(nn);
                    dos.writeUTF(b);
                    for(int ii=0;ii<3;ii++){
                        FillTable(nn,ii+1);
                    }
                    break;
 
                
            }
                
                
                
                
                
                
            }

        } catch (Exception e) {
            System.err.println("Something wrong with Client");
        }

    }

}

public class clientHandler {

    
    public static void main(String[] args) {
        try {
            ServerSocket s = new ServerSocket(1234);
            while (true) {
                System.out.println("waiting client ..");
                Socket c = s.accept();
                System.out.println("client arrived ..");
                clientHandle ch =  new clientHandle(c);
                ch.start();

            }

            //s.close();
        } catch (Exception e) {
            System.err.println("Something went wrong" + e.getMessage());
        }

    }

}
