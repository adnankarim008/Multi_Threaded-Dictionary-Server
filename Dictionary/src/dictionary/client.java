package dictionary;
import com.google.gson.Gson;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class client {
	private static int port;
	private static ClientUI gui;
	private static BufferedReader in;
	private static BufferedWriter out;
	private static Gson gson = new Gson();
	private static String address;

	public static void main(String[] args) 
	{
		try {
			//port = Integer.parseInt(args[1]);
			port = 10005;
			//address = args[0];
		}
		catch(Exception e){
			System.out.println("Please provide port and file location");
			System.exit(1);
		}




		Socket socket = null;
		try 
		{
			gui = new ClientUI();

			gui.addButton.addActionListener(e ->
			{

				try {
					var toSend = new CommandWrapper();
					toSend.command = "add";
					toSend.word = new Word();
					toSend.word.name = gui.textName.getText().toLowerCase();
					toSend.word.definition = gui.textDef.getText().toLowerCase();
					out.write(gson.toJson(toSend) + "\n");
					out.flush();

					ReadIncomingMessage();
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}

			});
			gui.updateButton.addActionListener(e ->
			{

				try {
					var toSend = new CommandWrapper();
					toSend.command = "update";
					toSend.word = new Word();
					toSend.word.name = gui.textName.getText().toLowerCase();
					toSend.word.definition = gui.textDef.getText().toLowerCase();
					out.write(gson.toJson(toSend) + "\n");
					out.flush();

					ReadIncomingMessage();
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}

			});

			gui.queryButton.addActionListener(e ->
			{

				try {
					var toSend = new CommandWrapper();
					toSend.command = "query";
					toSend.word = new Word();
					toSend.word.name = gui.textName.getText().toLowerCase();


					out.write(gson.toJson(toSend) + "\n");
					out.flush();

					ReadIncomingMessage();
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}

			});

			gui.removeButton.addActionListener(e ->
			{
				try {
					var toSend = new CommandWrapper();
					toSend.command = "remove";
					toSend.word = new Word();
					toSend.word.name = gui.textName.getText().toLowerCase();

					out.write(gson.toJson(toSend) + "\n");
					out.flush();

					ReadIncomingMessage();
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			});

			JFrame frame = new JFrame("ClientUI");
			frame.setSize(600, 450);
			frame.setContentPane(gui.contentPane);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			//frame.pack();
			frame.setVisible(true);
			//System.out.print(client.port);
			// Create a stream socket bounded to any port and connect it to the
			// socket bound to localhost on port 10001
			socket = new Socket(address, port);
			System.out.println("Welcome to dictionary! Please query, add or remove word!");

			// Get the input/output streams for reading/writing data from/to the socket
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

			Scanner scanner = new Scanner(System.in);
			String inputStr = null;

			//While the user input differs from "exit"
			while (!(inputStr = scanner.nextLine()).equals("exit"))
			{
				
				// Send the input string to the server by writing to the socket output stream
				out.write(inputStr + "\n");
				out.flush();
				System.out.println("Message sent");
				
				// Receive the reply from the server by reading from the socket input stream
				String received = in.readLine(); // This method blocks until there  is something to read from the
													// input stream
				System.out.println("Message received: " + received);
			}
			
			scanner.close();
			
		} 
		catch (UnknownHostException e)
		{
			//e.printStackTrace();
			System.out.print("A host error occured");
		}
		catch (IOException e)
		{
			//e.printStackTrace();
			System.out.print("An I/O error occured");
		} 
		finally
		{
			// Close the socket
			if (socket != null)
			{
				try
				{
					socket.close();
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}

	}

	private static void ReadIncomingMessage(){
		String received = null; // This method blocks until there  is something to read from the
		try {
			received = in.readLine();
		} catch (IOException e) {
			System.out.println("Error occurred");
		}
		// input stream
		System.out.println("Message received: " + received);
	}

	public int getPort() {
	    
		return port;
	}
    public static void setPort(int newPort) {
	    port = newPort;
	}	
}
