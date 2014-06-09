/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package redes2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


public class Servidor {

    
    public static void main(String[] args) {
            File f;
        int puerto=0;
        while(true){
        try
        {
            ServerSocket ser = new ServerSocket(9999);
            Socket sock = ser.accept();
            
            BufferedReader ed = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String tmp = ed.readLine();
             System.out.println("Recibido:"+tmp);
             String[] head_mensaje=tmp.split(":");
             if (head_mensaje[0].equals( "CONSULTA" )){
                 
                 f = new File("src/redes2/"+head_mensaje[1]+"-mensajes.txt");
                 if (f.exists()&& !f.isDirectory()){
                            try{
                       // Abrimos el archivo
                       FileInputStream fstream = new FileInputStream("src/redes2/"+head_mensaje[1]+"-mensajes.txt");
                       // Creamos el objeto de entrada
                       DataInputStream entrada = new DataInputStream(fstream);
                       // Creamos el Buffer de Lectura
                       BufferedReader buffer = new BufferedReader(new InputStreamReader(entrada));
                       String strLinea;
                       // Leer el archivo linea por linea
                       while ((strLinea = buffer.readLine()) != null)   {
                           // Imprimimos la línea por pantalla
                          System.out.println ("LINEA:"+strLinea);
                           String[] puerto_string=strLinea.split(":");
                           // MANDAR MENSAJE AL CLIENTE CUYO PUERTO CORRESPONDE
                            puerto= Integer.parseInt(puerto_string[0]);
                            sendMessage(puerto,"localhost",puerto+":"+puerto_string[1]);                               
                           //removeLineFromFile("src/redes2/mensajes.txt",strLinea)                      
                       }                 
                       
                       // Cerramos el archivo
                       fstream.close();
                       entrada.close();
                       
                       if (!f.delete()) {
                        System.out.println("Could not delete file");
                        return;
                        } 
                       
                   }catch (Exception e){ //Catch de excepciones
                       System.err.println("Ocurrio un error: " + e.getMessage());
                   }
             }
             }
             else{
                 f = new File("src/redes2/"+head_mensaje[0]+"-mensajes.txt");
            escribir(f,tmp);
             }
             PrintStream pr = new PrintStream(sock.getOutputStream());
             String str = "Yup I got it !!";
             pr.println(str);
             
        }
      catch(Exception ex){}
    }
    }
    
    public static void escribir(File f,String mensaje)

    {

//Escritura

    try{

    FileWriter w = new FileWriter(f,true);

    BufferedWriter bw = new BufferedWriter(w);

    PrintWriter wr = new PrintWriter(bw);  

    wr.println(mensaje);//escribimos en el archivo
        //ahora cerramos los flujos de canales de datos, al cerrarlos el archivo quedará guardado con información escrita
        //de no hacerlo no se escribirá nada en el archivo
    wr.close();

    bw.close();
    }catch(IOException e){};
 }

        public static void sendMessage(int port, String ip, String message)  throws IOException{
    try {
 
            // Create Socket address for configuring socket configuration
            SocketAddress sockaddr = new InetSocketAddress(ip, port);
 
            // Create socket Object
            Socket sock = new Socket();
 
            // if timeout is reached and no response is received, it will throw socket exception
            int timeoutMs = 2000;   // in milliseconds
 
            // Initiate socket connection to server
            sock.connect(sockaddr, timeoutMs);
            try {
                 
                // Create Buffered Writer object to write String or Byte to socket output stream
                BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
                String command = message;
                wr.write(command);
                System.out.println("Send String : "+command);
                 
                // Flushing the writer
                wr.flush();
 
            } catch (IOException e) {
                e.printStackTrace();
            }
           /* try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                String str;
                while ((str = rd.readLine()) != null) {
                    System.out.println(str);
                }
                rd.close();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
             
            // Close socket connection after finish receiving a response
            sock.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void removeLineFromFile(String file, String lineToRemove) {
 
    try {
 
      File inFile = new File(file);     
      if (!inFile.isFile()) {
        System.out.println("Parameter is not an existing file");
        return;
      }
       
      //Construct the new file that will later be renamed to the original filename. 
      File tempFile = new File(inFile.getAbsolutePath() + ".tmp");
      
      BufferedReader br = new BufferedReader(new FileReader(file));
      PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
      
      String line = null;
 
      //Read from the original file and write to the new 
      //unless content matches data to be removed.
      while ((line = br.readLine()) != null) {
        
        if (!line.trim().equals(lineToRemove)) {
 
          pw.println(line);
          pw.flush();
        }
      }
      pw.close();
      br.close();
      
      //Delete the original file
      if (!inFile.delete()) {
        System.out.println("Could not delete file");
        return;
      } 
      
      //Rename the new file to the filename the original file had.
      if (!tempFile.renameTo(inFile))
        System.out.println("Could not rename file");
      
    }
    catch (FileNotFoundException ex) {
      ex.printStackTrace();
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}