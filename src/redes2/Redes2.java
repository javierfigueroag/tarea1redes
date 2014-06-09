package redes2;
/**
 * @author Cohax
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

public class Redes2 {

    private static ServerSocket mainSocket;
    private static BufferedReader input;
    private static BufferedWriter output;


    public static void Server(List<String> request) throws IOException {

        
        
        String[] line1 = request.get(0).split(" ");
        String method = line1[0],mensaje;
        String url = line1[1];
        
        System.out.println("metodo " + method);
        System.out.println("url: " + url);


        
        File f = new File("src/redes2"+url);
        if(f.exists() && !f.isDirectory()){
            if(method.equals("POST")){      
                
                String datos;
                int puerto;
                char[] tmp = new char[100];
                input.read(tmp);
                for (int i=0;i<100;i++)
                    if(tmp[i]==(char)0)
                        tmp[i]='*';
                datos=new String(tmp);
                datos = datos.replace("*","");
                System.out.println("Datos: " + datos);
                String f_contacts = "src/redes2/Contactos.txt";            
             
                String[] datos_array=datos.split("[=&]");
                
                System.out.println("Para: " + datos_array[1]);
                System.out.println("Mensaje: " + datos_array[3]);
                System.out.println("IP: " + datos_array[5]);
                
                mensaje=datos_array[1]+": "+datos_array[3];
                sendMessage(9999,datos_array[5],mensaje);
                
                try{
                    
                    File fc = new File(f_contacts);
                    FileWriter bw = new FileWriter(fc,true);
                    bw.write(datos + "\r\n");
                    bw.close();

                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            renderFile(f);
        }

        
        
        else if(url.equals("/")){
            File a = new File("src/redes2/Index.html");
            renderFile(a);
        }
        else
        {
            System.out.println("Error: 404");
            printText("404" + url);
        }
    }
    
    /**
     *
     * @param puerto
     */
    public static void ConsultaServidor(int puerto)
     {
         Timer timer = new Timer (5000, new ActionListener ()
         {
             @Override
             public void actionPerformed(ActionEvent e) {
                 try {
                     sendMessage(9999,"localhost","CONSULTA:"+puerto);
                 } catch (IOException ex) {
                     Logger.getLogger(Redes2.class.getName()).log(Level.SEVERE, null, ex);
                 }
             }
         });
         timer.start();
     }

    //////////////////// MAIN ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    public static void main(String[] args) throws IOException {
        
        int puerto=0, evento_consulta=1;
        String puerto_string="lala";
        System.out.println("Numero de Puerto a utilizar (distinto de 9999): ");
        
        File f;
 
	try{
	    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	    puerto_string = bufferRead.readLine();
 
	    System.out.println(puerto_string);
	}
	catch(IOException e)
	{
		e.printStackTrace();
	}
        
        puerto = Integer.parseInt(puerto_string);
        
        f = new File("src/redes2/Mis_Mensajes.txt");
        
        mainSocket = new ServerSocket(puerto);
        
        System.out.println("Iniciando Servidor");

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mainSocket.close();
                    System.out.println("Apagando Servidor");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }));
        
        // CONSULTA AL SERVIDOR SI HAY NUEVOS MENSAJES
        
        while (true) {
            Socket connection = mainSocket.accept();
            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            String client;
            List<String> inputRequest = new ArrayList<String>();
            while ((client = input.readLine()) != null) {
                
                String[] puerto_recv = client.split(":");
                if (puerto_string.equals(puerto_recv[0])){
                    System.out.println("Recibido Nuevo Mensaje: " + client);
                    escribir(f,client);
                }
                inputRequest.add(client); 
                if (client.isEmpty()){
                    
                    Server(inputRequest);
                    inputRequest = new ArrayList<String>(); 
                    break;
                }
            }
            if (evento_consulta == 1){
            ConsultaServidor(puerto);
            evento_consulta=0;
            }
            
        }
    }
    public static void printText(String text)  throws IOException
    {
        output.write("HTTP/1.1 200 OK\r\n");
        output.write("Date: Mon, 23 May 2005 22:38:34 GMT\r\n");
        output.write("Server: Apache/1.3.3.7 (Unix) (Red-Hat/Linux)\r\n");
        output.write("Content-Type: text/html; charset=UTF-8\r\n");
        output.write("Connection: close\r\n");
        output.write("\r\n");
        output.write("<p> "+ text+ " </p>");
        output.close();

    }

    public static void renderFile(File f)  throws IOException
    {
        output.write("HTTP/1.1 200 OK\r\n");
        output.write("Date: Mon, 23 May 2005 22:38:34 GMT\r\n");
        output.write("Server: Apache/1.3.3.7 (Unix) (Red-Hat/Linux)\r\n");
        output.write("Content-Type: text/html; charset=UTF-8\r\n");
        output.write("Connection: keep-alive\r\n");
 
        
        
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        System.out.println("get name: "+f.getName());
        if(f.getName().equals("Enviado.html")){
            for(int x = 0; x < 43; x = x+1){
                System.out.println("x:"+x);
                line = br.readLine();
                output.write(line+"\n");
            }

            BufferedReader reader = new BufferedReader(new FileReader("src/redes2/Contactos.txt"));
            String db_line = null;

            
            List<String> db_array = new ArrayList<String>();
            while ((db_line = reader.readLine()) != null) {
                db_array.add(db_line);
            }
            output.write("<table class=\"table\">\n");
            output.write("<tr><th>Name</th><th>IP</th><th>Port</th></tr>\n");
            
            for (int i=0; i<db_array.size(); i=i+1) {
                String[] line_read = db_array.get(i).split("&");
                output.write("<tr>\n");
                output.write("<td>"+ line_read[0].split("=")[1] +"</td>\n");
                output.write("<td>"+ line_read[1].split("=")[1] +"</td>\n");
                output.write("<td>"+ line_read[2].split("=")[1] +"</td>\n");
                output.write("</tr>\n");
            }
            output.write("</table>\n");
            while ((line = br.readLine()) != null) {
                output.write(line+"\n");
            }
        }
        else{
            if(f.getName().equals("Chat.html")){
                
                while ((line = br.readLine()) != null) {
                output.write(line+"\n");
            }
                // LEER EL TXT
                // Abrimos el archivo
                FileInputStream fstream = new FileInputStream("src/redes2/Mis_Mensajes.txt");
                // Creamos el objeto de entrada
                DataInputStream entrada = new DataInputStream(fstream);
                // Creamos el Buffer de Lectura
                BufferedReader buffer = new BufferedReader(new InputStreamReader(entrada));
                
                 String strLinea;
                       // Leer el archivo linea por linea
                       while ((strLinea = buffer.readLine()) != null)   {
                           // Imprimimos la línea por pantalla
                          output.write("<b><p style=\"color:#ffffff\">Para "+strLinea+"</p></b>");                     
                       }
                
            }
            else{
                while ((line = br.readLine()) != null) {
                    output.write(line+"\n");
                }
            }
        }
        br.close();
        output.close();

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
                PrintStream pr = new PrintStream(sock.getOutputStream());
                String command = message;
                wr.write(command);
                pr.println(command);
                System.out.println("Send String : "+command);
                 
                // Flushing the writer
                wr.flush();
 
            } catch (IOException e) {
                e.printStackTrace();
            }
 
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                String str;
                while ((str = rd.readLine()) != null) {
                    System.out.println(str);
                }
                rd.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
             
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

}