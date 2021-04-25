package dictionary;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.BindException;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;
import java.nio.file.Files;
import java.lang.reflect.Type;

public class server extends JFrame  {

    private static int port;
    private static ArrayList<Word> words = new ArrayList<Word>();
    private static Gson gson = new Gson();
    private static Path file = Paths.get("d:\\words.txt");
    private static ServerUI gui;
    private static String location;
	
	public static void main(String[] args) throws IOException {
        try {
            port = Integer.parseInt(args[0]);
            location = args[1];
        }
        catch(Exception e){
            System.out.println("Please provide port and file location");
            System.exit(1);
        }

        try {
            ReadWordsFile();
        } catch (IOException e) {
            //Create Words File if it does not exist
            Files.createFile(file);
            Files.writeString(file, "[]", StandardCharsets.ISO_8859_1);
            ReadWordsFile();
        }

        gui = new ServerUI();
        gui.list1.setListData(words.toArray());

        gui.addButton.addActionListener(e ->
        {
            AddWord(gui.textName.getText().toLowerCase(), gui.textDef.getText().toLowerCase());
        });

        gui.removeButton.addActionListener(e ->
        {
            RemoveWord(gui.textName.getText().toLowerCase());
        });

        JFrame frame = new JFrame("ServerUI");
        frame.setSize(600, 450);
        frame.setContentPane(gui.contentPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.pack();
        frame.setVisible(true);



		
		ServerSocket listeningSocket = null;
		Socket clientSocket = null;
		//System.out.println("Enter server port number (eg. 10000-40000)");

		//Scanner keyboard = new Scanner(System.in);
		//System.out.println("Enter an integer");
		//port = keyboard.nextInt();
		//System.out.println("Selected Port: " + port);
		client.setPort(port);
		try {
			//Create a server socket listening on port 4444
			listeningSocket = new ServerSocket(port);
			listeningSocket.setReuseAddress(true);
			int i = 0; //counter to keep track of the number of clients


			//Listen for incoming connections for ever
			while (true)
			{
				System.out.println("Server listening on port "+ port + " for connections");
				//Accept an incoming client connection request

				clientSocket = listeningSocket.accept(); //This method will block until a connection request is received
				i++;
				System.out.println("Client conection number " + i + " accepted:");
                ClientHandler clientSock = new ClientHandler(clientSocket, i);
                new Thread(clientSock).start();



            }
        }
        catch (BindException e) {
        System.out.println("The port is in use please exit it and try again."+"\n");
        return;}
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (listeningSocket != null) {
                try {
                    listeningSocket.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // ClientHandler class
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final int id;
  
        // Constructor
        public ClientHandler(Socket socket, int id)
        {
            this.clientSocket = socket;
            this.id = id;
        }
  
        public void run()
        {
            PrintWriter out = null;
            BufferedReader in = null;
            try {
                    
                  // get the outputstream of client
                out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
  
                  // get the inputstream of client
                in = new BufferedReader(
                    new InputStreamReader(
                        clientSocket.getInputStream()));
  
                String line;
                while ((line = in.readLine()) != null) {

                    System.out.println("Client " + id + " sent - " + line);

					//String[] command = line.split(",");
                    CommandWrapper data = gson.fromJson(line, CommandWrapper.class);

					if (data.command.equals("query")){

                        var exists = words.stream()
                                .anyMatch(x-> x.name.equals(data.word.name));

                        if(exists){
                            out.write(data.word.name +" definition: "+ words.stream()
                                .filter(x-> x.name.equals(data.word.name))
                                .findFirst()
                                .get().definition + "\n");
                        } else{
                            out.write("Word not found" + "\n");
                        }

						//out.write(query(command[1]));
						out.flush();
					}
					if (data.command.equals("add")){
						//out.write(add(command[1]+","+command[2]));

                        var result = AddWord(data.word.name, data.word.definition);
                        out.write(result + "\n");

						out.flush();
					}
                    if (data.command.equals("update")){
                        //out.write(add(command[1]+","+command[2]));

                        var result = UpdateWord(data.word.name, data.word.definition);
                        out.write(result + "\n");

                        out.flush();
                    }
					if (data.command.equals("remove")){
						//out.write(remove(command[1]));

                        var result = RemoveWord(data.word.name);
                        out.write(result + "\n");

						out.flush();
					}
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                        clientSocket.close();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String AddWord(String name, String definition) {
	    var result = "";

        var exists = words.stream()
                .anyMatch(x-> x.name.equals(name));

        if(exists){
            result = "Word already exists - " + name;
            return result;
        }

        Word word = new Word();
        word.name = name;
        word.definition = definition;
        words.add(word);
        WriteWordsFile();

        result = "Word added - " + name;
        System.out.println(result);
        return result;
    }

    public static String RemoveWord(String name){
        var result = "";

        var exists = words.stream()
                .anyMatch(x-> x.name.equals(name));

        if(exists){
            words.removeIf(x -> x.name.equals(name));
            result = "Word removed - " + name;
            WriteWordsFile();
        } else {
            result = "Word does not exist - " + name;

        }
        System.out.println(result);
        return result;
    }
    public static String UpdateWord(String name, String definition){
        var result = "";

        var exists = words.stream()
                .anyMatch(x-> x.name.equals(name));

        if(exists){
            words.removeIf(x -> x.name.equals(name));
            Word word = new Word();
            word.name = name;
            word.definition = definition;
            words.add(word);
            WriteWordsFile();
            result = "Word updated - " + name;
        }
        else{
            result =result = "Word does not exist - " + name;
        }


        System.out.println(result);
        return result;
    }
    
    private static void ReadWordsFile() throws IOException {
        java.lang.reflect.Type wordListType = new TypeToken<ArrayList<Word>>(){}.getType();
        words = gson.fromJson(Files.readString(file), wordListType);
    }

    private static void WriteWordsFile()  {
        try {
            Files.writeString(file, gson.toJson(words), StandardCharsets.ISO_8859_1);
            gui.list1.setListData(words.toArray());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while saving words file");
        }
    }
}	


