package redes2;
/**
 * @author Cohax
 */
import java.io.*;
import java.net.*;
import java.util.*;

public class Redes2 {

    private static ServerSocket mainSocket;
    private static BufferedReader input;
    private static BufferedWriter output;


    public static void Server(List<String> request) throws IOException {

        
        
        String[] line1 = request.get(0).split(" ");
        String method = line1[0];
        String url = line1[1];
        
        System.out.println("metodo " + method);
        System.out.println("url: " + url);


        
        File f = new File("src/redes2"+url);
        if(f.exists() && !f.isDirectory()){
            if(method.equals("POST")){

                
                
                String datos;
                char[] tmp = new char[100];
                input.read(tmp);
                for (int i=0;i<100;i++)
                    if(tmp[i]==(char)0)
                        tmp[i]='*';
                datos=new String(tmp);
                datos = datos.replace("*","");
                System.out.println("Datos: " + datos);
                String f_contacts = "src/redes2/Contactos.txt";
                
                
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

    public static void main(String[] args) throws IOException {
        mainSocket = new ServerSocket(8080);
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
        while (true) {
            Socket connection = mainSocket.accept();
            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            String client;
            List<String> inputRequest = new ArrayList<String>();
            while ((client = input.readLine()) != null) {
                
                System.out.println("received: " + client);
                inputRequest.add(client); 
                if (client.isEmpty()){
                    
                    Server(inputRequest);
                    inputRequest = new ArrayList<String>(); 
                    break;
                }
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
        if(f.getName().equals("Contactos.html")){
            for(int x = 0; x < 43; x = x+1){
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
            while ((line = br.readLine()) != null) {
                output.write(line+"\n");
            }
        }
        br.close();
        output.close();

    }


}