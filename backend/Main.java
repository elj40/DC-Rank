import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Random;
import java.util.Scanner;

class GenericHandler implements HttpHandler {
    byte[] responseBytes;

    GenericHandler(byte[] _responseBytes)
    {
        responseBytes = _responseBytes;
    }

    public void handle(HttpExchange exchange)
    {
        try {

        exchange.sendResponseHeaders(200, 0);
        OutputStream responseStream = exchange.getResponseBody();
        responseStream.write(responseBytes);
        responseStream.close();
        
        } catch (IOException e) { e.printStackTrace(); }
    }
}
class StaticFileHandler implements HttpHandler
{
    String filePath;
    StaticFileHandler(String _filePath)
    {
        filePath = _filePath;
    }
    public void handle(HttpExchange exchange)
    {

        try {

        FileInputStream fileInputStream = new FileInputStream(filePath);
        byte[] fileBytes = fileInputStream.readAllBytes();

        exchange.sendResponseHeaders(200, 0);
        OutputStream responseStream = exchange.getResponseBody();
        responseStream.write(fileBytes);
        responseStream.close();
        
        } catch (IOException e) { e.printStackTrace(); }
    };
}
class RandomDataFetchHandler implements HttpHandler
{
    String[][] database;
    RandomDataFetchHandler(String[][] db)
    {
        database = db;
    }
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

            String responseString = dbToStringBuilder(database).toString();
            GenericHandler rootHandler = new GenericHandler(responseString.getBytes());
            server.createContext("/", rootHandler);

            RandomDataFetchHandler rdfHandler = new RandomDataFetchHandler(database);
            HttpContext rdfContext = server.createContext("/random", rdfHandler);

            StaticFileHandler indexHtml = new StaticFileHandler("../frontend/index.html");
            server.createContext("/index", indexHtml);

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
