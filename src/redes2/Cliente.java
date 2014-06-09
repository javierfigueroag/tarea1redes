/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package redes2;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;


public class Cliente {

   
    public static void main(String[] args) {
       
        try
        {
            Socket sock = new Socket("localhost",9999);
            
            PrintStream pr = new PrintStream(sock.getOutputStream());
            
            System.out.print("Enter Something : ");
          
            InputStreamReader rd = new InputStreamReader(System.in);
            BufferedReader ed = new BufferedReader(rd);
            
            String temp = ed.readLine();
            
            pr.println(temp);
        
            BufferedReader gt = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            
            String tm = gt.readLine();
            System.out.print(tm);
            
        }
        catch(Exception ex)
        {
            
        }
    }
}
