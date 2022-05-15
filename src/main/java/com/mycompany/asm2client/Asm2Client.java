package com.mycompany.asm2client;

import java.io.*;
import java.net.Socket;
public class Asm2Client {
    public static void main(String argv[]) throws Exception
    {
        int accountAlert = 0;
        int passwordAlert = 0;
        int isExit = 0;
        
        //Inputstream from keyboard
        BufferedReader inFromUser =
            new BufferedReader(new InputStreamReader(System.in));

        //Socket to connect server
        Socket clientSocket = new Socket("127.0.0.1", 5555);
    
        //OutputStream with Socket
        DataOutputStream outToServer =
            new DataOutputStream(clientSocket.getOutputStream());
    
        //InputStream with Socket
        BufferedReader inFromServer =
            new BufferedReader(new
            InputStreamReader(clientSocket.getInputStream()));

        //Authen
        do{
            accountAlert = authentication(inFromUser, outToServer, inFromServer, "Account");
            if(accountAlert == 1)
            {
                passwordAlert = authentication(inFromUser, outToServer, inFromServer, "Password");
                if(passwordAlert == 1)
                {
                    System.out.println("Login successful");
                }
                else
                {
                    System.out.println("Password incorrect");
                }
            }
            else
            {
                System.out.println("Not found user");
            }
        }while(accountAlert == 0 || passwordAlert == 0);
        
        //Message with server
        do{
            isExit = messageWithServer(outToServer, inFromUser, inFromServer);
        }while(isExit == 0);
        
        //Close socket
        exit(clientSocket, outToServer, inFromServer);    
    } 
    
    //Authen
    private static int authentication(BufferedReader inFromUser, DataOutputStream outToServer, BufferedReader inFromServer, String checkedValue) throws IOException
    {
        System.out.print(checkedValue+ ": ");
        String accountInput = inFromUser.readLine();
        outToServer.writeBytes(checkedValue + " " + accountInput + '\n');
        return inFromServer.read();
    }
    
    //Message with server
    private static int messageWithServer(DataOutputStream outToServer, BufferedReader inFromUser, BufferedReader inFromServer) throws IOException
    {
        System.out.print("Send to server (Enter exit to halt program): ");
        String messageFrom = inFromUser.readLine();
        if(messageFrom.equals("exit"))
        {
            return 1;
        }
        else 
        {
            outToServer.writeBytes("ECHO " + messageFrom + '\n');
            String messageTo = inFromServer.readLine();
            System.out.println("Reply from server: " + messageTo);
            
            String[] values = messageTo.split(" ", 2);
            String header = values[0];
            
            if(header.equals("Error"))
            {
                return 1;
            }
            else
            {
                return 0;
            }
            
        }
    }
    
    //Close socket
    private static void exit(Socket clientSocket, DataOutputStream outToServer, BufferedReader inFromServer) throws IOException
    {
        outToServer.writeBytes("LOGOUT" + '\n');
        String messageTo = inFromServer.readLine();
        System.out.println("Reply from server: " + messageTo);
        clientSocket.close();
    }
}
