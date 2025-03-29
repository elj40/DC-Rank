import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.lang.Exception;
import java.io.OutputStream;

import java.util.Random;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

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

        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(200, 0);

        OutputStream responseStream = exchange.getResponseBody();
        responseStream.write(fileBytes);
        responseStream.close();
        
        } catch (IOException e) { e.printStackTrace(); }
    };
}

class ScoresheetHandler implements HttpHandler
{
    HashMap<String, Integer> scoresheet;
    static int c = 0;
    ScoresheetHandler(HashMap<String, Integer> ss)
    {
        scoresheet = ss;
    }
    public void handle(HttpExchange exchange)
    {
        //System.out.println("Updating scoresheet");
        try {

        InputStream requestBodyStream = exchange.getRequestBody();
        String requestBody = new String(requestBodyStream.readAllBytes());

        int newScore = 1;
        if (scoresheet.containsKey(requestBody)) newScore = scoresheet.get(requestBody)+1;
        scoresheet.put(requestBody, newScore);

        System.out.println(requestBody + ": " + newScore);

        exchange.sendResponseHeaders(200, 0);
        OutputStream responseStream = exchange.getResponseBody();
        responseStream.close();
        } catch (IOException e) { e.printStackTrace(); }  
        catch (Exception e) { e.printStackTrace(); } 
    }
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

        System.out.println("Printing scoresheet...");
        for (Map.Entry<String, Integer> entry : Main.scoresheet.entrySet()) {
            System.out.println("alskjdfha " +  entry.getKey() + ": " + entry.getValue());
        }
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

    static String INDEX_HTML_PATH = "../frontend/index.html";
    static String COMIC_LIST_PATH = "../Comic_List.txt";

    static HashMap<String, Integer> scoresheet;

    public static void main(String[] args)
    {
        try{

            // Database where all the links and names are stored
            // Stored as [[link][name], [link2][name2]]
            String[][] database = new String[DB_SIZE][2];
            scoresheet = new HashMap<String, Integer>();

            File file = new File(COMIC_LIST_PATH); 
            Scanner scanner = new Scanner(file);

            System.out.println("Reading database from:" + COMIC_LIST_PATH);

            for (int i = 0; scanner.hasNextLine(); i++)
            {
                int index = i/2;
                int dataType = i % 2;
                database[index][dataType] = scanner.nextLine();
            }

            //====================== Server stuff =================================
            InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8080);
            HttpServer server = HttpServer.create();
            server.bind(address, 0);

            StaticFileHandler rootHandler = new StaticFileHandler(INDEX_HTML_PATH);
            server.createContext("/", rootHandler);

            RandomDataFetchHandler rdfHandler = new RandomDataFetchHandler(database);
            HttpContext rdfContext = server.createContext("/random", rdfHandler);

            StaticFileHandler indexHtml = new StaticFileHandler("../frontend/index.html");
            server.createContext("/index", indexHtml);

            ScoresheetHandler scoresheetHandler = new ScoresheetHandler(scoresheet);
            server.createContext("/scoresheet", scoresheetHandler);

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
