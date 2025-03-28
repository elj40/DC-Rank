import com.sun.net.httpserver.HttpServer; 
import com.sun.net.httpserver.HttpHandler; 
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import java.net.InetSocketAddress;

import java.util.Scanner;
import java.io.File;
import java.io.OutputStream;
import java.io.IOException;

import java.util.Random;

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

class RandomDataFetchHandler implements HttpHandler
{
    String[][] database;
    public void handle(HttpExchange exchange)
    {
        Random random = new Random();
        int index = random.nextInt(Main.DB_SIZE);

        try {

        String randomLine = Main.dbLineToStringBuilder(database, index).toString();
        exchange.sendResponseHeaders(200, 0);
        OutputStream responseStream = exchange.getResponseBody();
        responseStream.write(randomLine.getBytes());
        responseStream.close();
        
        } catch (IOException e) { e.printStackTrace(); }
    };
}
class Main {
    static int LINK_INDEX = 0;
    static int NAME_INDEX = 1;
    static int DB_SIZE = 100;

    public static void main(String[] args)
    {
        try{

            // Database where all the links and names are stored
            // Stored as [[link][name], [link2][name2]]
            
            String[][] database = new String[DB_SIZE][2];
            String pathname = "../Comic_List.txt";
            File file = new File(pathname); 
            Scanner scanner = new Scanner(file);

            System.out.println("Reading database from:" + pathname);

            for (int i = 0; scanner.hasNextLine(); i++)
            {
                int index = i/2;
                int dataType = i % 2;
                database[index][dataType] = scanner.nextLine();
            }

            InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8080);
            HttpServer server = HttpServer.create();
            server.bind(address, 0);


            RootHandler rootHandler = new RootHandler();
            HttpContext rootContext = server.createContext("/", rootHandler);
            rootHandler.responseData = dbToStringBuilder(database).toString();

            RandomDataFetchHandler rdfHandler = new RandomDataFetchHandler();
            HttpContext rdfContext = server.createContext("/random", rdfHandler);
            rdfHandler.database = database;

            server.start(); 
            System.out.println("Serving at :" + server.getAddress());

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

    static StringBuilder dbLineToStringBuilder(String[][] database, int index)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(database[index][NAME_INDEX]);
        sb.append(": ");
        sb.append(database[index][LINK_INDEX]);
        sb.append("\n");
        return sb;
    };
}
