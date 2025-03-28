import com.sun.net.httpserver.HttpServer; 
import com.sun.net.httpserver.HttpHandler; 
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import java.net.InetSocketAddress;

import java.util.Scanner;
import java.io.File;
import java.io.OutputStream;
import java.io.IOException;

class RootHandler implements HttpHandler {
    String responseData;

    public void handle(HttpExchange exchange)
    {
        try {

        exchange.sendResponseHeaders(200, 0);
        OutputStream responseStream = exchange.getResponseBody();
        responseStream.write(responseData.getBytes());
        responseStream.close();
        
        } catch (IOException e) { e.printStackTrace(); }
    }
}

class Main {

    static int LINK_INDEX = 0;
    static int NAME_INDEX = 1;

    public static void main(String[] args)
    {
        try{

            System.out.println("Hello Worlds!");
            // Database where all the links and names are stored
            // Stored as [[link][name], [link2][name2]]
            
            String[][] database = new String[100][2];
            String pathname = "../Comic_List.txt";
            File file = new File(pathname); 
            Scanner scanner = new Scanner(file);

            for (int i = 0; scanner.hasNextLine(); i++)
            {
                int index = i/2;
                int dataType = i % 2;
                database[index][dataType] = scanner.nextLine();
            }

            InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8081);
            HttpServer server = HttpServer.create();
            server.bind(address, 0);

            RootHandler root = new RootHandler();
            root.responseData = dbToStringBuilder(database).toString();
            HttpContext context = server.createContext("/", root);

            server.start(); 

        } catch (Exception e) { e.printStackTrace(); };
    }

    static StringBuilder dbToStringBuilder(String[][] database)
    {
        StringBuilder sb = new StringBuilder();
        for (String[] record : database)
        {
            sb.append(record[NAME_INDEX]);
            sb.append(": ");
            sb.append(record[LINK_INDEX]);
            sb.append("\n");
        }
        return sb;
    };
}
