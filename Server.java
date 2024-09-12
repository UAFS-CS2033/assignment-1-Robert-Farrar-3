import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private int portNo;

    public Server(int portNo){
        this.portNo=portNo;
    }

    private void processConnection() throws IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);

        //*** Application Protocol *****
        String[] fileNames = new String[25];
        int files = 0;
        String buffer = in.readLine();
        while(buffer.length() != 0){
            String tokens[] = buffer.split(" ");
            if(tokens[0].equals("GET")){
                fileNames[files] = tokens[1];
                files++;
            }
            
            buffer = in.readLine();
        }
        files = 0;
        while(fileNames[files] != null){
            File file = new File("/home/student/projects/assignment-1-Robert-Farrar-3/docroot" + fileNames[files]);

            if(file.isFile()){
                String fileType[] = fileNames[files].split("\\.");
                String type = "";
                if(fileType[1].equals("html")){
                    type = "text/html";
                }
                else if(fileType[1].equals("ico")){
                    type = "image/vnd.microsoft.icon";
                }
                else if(fileType[1].equals("css")){
                    type = "text/css";
                }
                else if(fileType[1].equals("gif")){
                    type = "image/gif";
                }
                else if(fileType[1].equals("jpeg") || fileType[1].equals("jpg")){
                    type = "image/jpeg";
                }
                else if(fileType[1].equals("png")){
                    type = "image/png";
                }
                long length = file.length();
                out.println("HTTP/1.1 200 OK");
                out.println("Content-Length: " + length);
                out.println("Content-Type: " + type);
                out.println();
                BufferedReader r = new BufferedReader(new FileReader(file));
                String string = "";
                while((string = r.readLine()) != null){
                    out.println(string);
                }
                r.close();
            }
            else{
                out.println("HTTP/1.1 404 Not Found");
                out.println();
            }
            files++;
        }
       
        
        in.close();
        out.close();
    }

    public void run() throws IOException{
        boolean running = true;
       
        serverSocket = new ServerSocket(portNo);
        System.out.printf("Listen on Port: %d\n",portNo);
        while(running){
            clientSocket = serverSocket.accept();
            //** Application Protocol
            processConnection();
            clientSocket.close();
        }
        serverSocket.close();
    }
    public static void main(String[] args0) throws IOException{
        Server server = new Server(8080);
        server.run();
    }
}
