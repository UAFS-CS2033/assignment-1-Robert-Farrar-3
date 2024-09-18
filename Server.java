import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        String type = "";
        while(buffer.length() != 0){
            String tokens[] = buffer.split(" ");
            if(tokens[0].equals("GET")){
                fileNames[files] = tokens[1];
                files++;
            }
            else if(tokens[0].equals("Accept:")){
                String kind[] = tokens[1].split(",");
                type = kind[0];
            }
            
            buffer = in.readLine();
        }
        files = 0;
        while(fileNames[files] != null){
            File file = new File("assignment-1-Robert-Farrar-3/docroot" + fileNames[files]);

            if(file.isFile()){
                long length = file.length();
                out.println("HTTP/1.1 200 OK");
                out.println("Content-Length: " + length);
                out.println("Content-Type: " + type);
                out.println();

                byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                OutputStream st = clientSocket.getOutputStream();

                st.write(bytes);
                st.flush();

                st.close();
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
