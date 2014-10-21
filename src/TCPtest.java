import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class TCPtest 
{
	private static ServerSocket serverSocket = null;
    private static Socket client = null;
	
	private static String fileToSend = "/home/arnold/Pictures/wolf-597-1280x800.jpg";
	//private static String fileToSend = "/home/arnold/Pictures/Asking Alexandria - A Lesson Never Learned.mp3";
	//private static String fileToSend = "/home/arnold/Pictures/bates1.mkv";
	
	private static String fileName = "wolf-597-1280x800.jpg";
	//private static String fileName = "Asking Alexandria - A Lesson Never Learned.mp3";
	//private static String fileName = "bates1.mkv";

	//relog gui
	private JFrame _relogFrame;
	private JLabel _nameLabel;
	private JTextField _nameField;
	private JButton _confirmButton;
	private JLabel _relogHeader;
	
	public void buildRelog()
	{
		_relogFrame = new JFrame();
		_relogFrame.setSize(300, 200);
		_relogFrame.setResizable(false);
		_relogFrame.setLayout(null);
		_relogFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		_nameLabel = new JLabel("Username:");
		_relogFrame.getContentPane().add(_nameLabel);
		_nameLabel.setBounds(20, 50, 100, 25);
		
		_relogHeader = new JLabel("Please enter new username.");
		_relogFrame.getContentPane().add(_relogHeader);
		_relogHeader.setBounds(35, 10, 250, 25);
		
		
		_nameField = new JTextField(30);
		_relogFrame.getContentPane().add(_nameField);
		_nameField.setBounds(105, 50, 150, 25);
		
		_confirmButton = new JButton("Confirm");
		_confirmButton.addActionListener(
				new java.awt.event.ActionListener()
				{
					public void actionPerformed(java.awt.event.ActionEvent event)
					{
						
						if (_confirmButton.getText().equalsIgnoreCase("Poes"))
						{
							_confirmButton.setText("Confirm");
						}
						else
						{
							_confirmButton.setText("Poes");
						}
						//_relogFrame.setVisible(false);
						//_relogFrame.getContentPane().add(_confirmButton);
						//_confirmButton.setBounds(105, 100, 100, 25);
						//_relogFrame.setVisible(true);
						
						
					}
				});
		_relogFrame.getContentPane().add(_confirmButton);
		_confirmButton.setBounds(105, 100, 100, 25);
		
		_relogFrame.setVisible(true);
	}
	
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException
	{
		
		TCPtest mal = new TCPtest();
		mal.buildRelog();
		
		
		
		
		
		
		
		/*
		 * 		_uploadBar.addMouseListener(
				new MouseAdapter() 
				{            
					public void mouseClicked(MouseEvent e)
					{
						if (_uploadBar.getValue() > 0)
						{
							if (_isUploadPaused == false)
							{
								try
								{
									_sending.wait();
								}
								catch (InterruptedException e1)
								{
									e1.printStackTrace();
								}
								_isUploadPaused = true;
							}
							else
							{
								_sending.notify();
								_isUploadPaused = false;
							}
						}
					}                                     
				});
		 */
		
		
		
		
		
		
		
		
		
		
		
		/*File file = new File(fileToSend);
		long fileSize = file.length();
		if (args[0].equals("1"))
		{
			serverSocket = new ServerSocket(3000);
			client = serverSocket.accept();
			DataInputStream fileInputStream = new DataInputStream(new FileInputStream(fileToSend));
			
			byte[] buffer = new byte[65536];
			int number;
			double total = 0;
			while ((number = fileInputStream.read(buffer)) != -1)
			{
				double prog = (number / (double) fileSize);
				total = total + (prog * 100);
				System.out.println(total + "%");
			    client.getOutputStream().write(buffer, 0, number);
			}

			client.getOutputStream().close();
			fileInputStream.close();

		}
		else
		{
			client = new Socket("localhost", 3000);			
			
			byte[] buffer = new byte[65536];
			int number;

			InputStream socketStream = client.getInputStream();
			File f = new File("test_" + fileName);

			OutputStream fileStream = new FileOutputStream(f);
			double total = 0;
			
			while ((number = socketStream.read(buffer)) != -1)
			{
				double prog = (number / (double) fileSize);
				total = total + (prog * 100);
				System.out.println(total + "%");
			    fileStream.write(buffer,0,number);
			}

			fileStream.close();
			socketStream.close();
			
		}*/
	}
	
	
	
	
	
	
	
	/*//sender
    private static ServerSocket serverSocket = null;
    private static Socket socket = null;
    private static ObjectInputStream inputStream = null;
    private static FileEvent serverFileEvent;
    private static File dstFile = null;
    private static FileOutputStream fileOutputStream = null;
    
    //receiver
    private static ObjectOutputStream outputStream = null;
    private static String sourceFilePath = "/home/arnold/Pictures/bates1.mkv";
    private static FileEvent clientFileEvent = null;
    private static String destinationPath = "bates1.mkv";
    
	
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException
	{
		if (args[0].equalsIgnoreCase("1"))
		{
			//sending
			serverSocket = new ServerSocket(3000);
            socket = serverSocket.accept();
            inputStream = new ObjectInputStream(socket.getInputStream());
            
            serverFileEvent = (FileEvent) inputStream.readObject();
            
            if (serverFileEvent.getStatus().equalsIgnoreCase("Error"))
            {
                System.out.println("Error occurred ..So exiting");
                System.exit(0);
            }
            
            String outputFile = serverFileEvent.getDestinationDirectory() + serverFileEvent.getFilename();
            if (!new File(serverFileEvent.getDestinationDirectory()).exists())
            {
                new File(serverFileEvent.getDestinationDirectory()).mkdirs();
            }
            dstFile = new File(outputFile);
            fileOutputStream = new FileOutputStream(dstFile);
            fileOutputStream.write(serverFileEvent.getFileData());
            fileOutputStream.flush();
            fileOutputStream.close();
            System.out.println("Output file : " + outputFile + " is successfully saved ");
            Thread.sleep(3000);
            System.exit(0);
		}
		else
		{
			//receiving
			socket = new Socket("localhost", 3000);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            
            clientFileEvent = new FileEvent();
            String fileName = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1, sourceFilePath.length());
            String path = sourceFilePath.substring(0, sourceFilePath.lastIndexOf("/") + 1);
            clientFileEvent.setDestinationDirectory("");
            clientFileEvent.setFilename(fileName);
            clientFileEvent.setSourceDirectory(sourceFilePath);
            File file = new File(sourceFilePath);
            
            DataInputStream diStream = new DataInputStream(new FileInputStream(file));
            long len = (int) file.length();
            byte[] fileBytes = new byte[(int) len];
            int read = 0;
            int numRead = 0;
            while (read < fileBytes.length && (numRead = diStream.read(fileBytes, read, fileBytes.length - read)) >= 0)
            {
            	System.out.println(read);
                read = read + numRead;
            }
            clientFileEvent.setFileSize(len);
            clientFileEvent.setFileData(fileBytes);
            clientFileEvent.setStatus("Success");
            
            outputStream.writeObject(clientFileEvent);
            System.out.println("Done...Going to exit");
            Thread.sleep(3000);
            System.exit(0);
		}
	}*/
}
