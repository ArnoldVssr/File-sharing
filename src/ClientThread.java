import java.awt.BorderLayout;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * 
 * @author A Visser, 17224047
 * 		   T Butler, 17403812
 *
 */
public class ClientThread extends Thread
{
	//global variables
	private static Socket _socket = null;
	private static ServerSocket _sendSocket;
	private static Socket _recieveSocket;
	private static byte[] _sendBuf = null;
	private byte[] _recBuf = null;
	public User _user = new User();
	public String _myName = "";
	public String _hostname = "";
	public String _portnum = "";
	private InetAddress _address;
	private int _port;
	
	//relog gui
	private JFrame _relogFrame;
	private JLabel _nameLabel;
	private JTextField _nameField;
	private JButton _confirmButton;
	
	/**
	 * constuctor to start a ClientThread
	 * 
	 * @param user : the chosen username of the client.
	 * 		  host : the hostname of the server.
	 * 		  port : the port that the server is on.
	 */
	public ClientThread(String user, String host, String port)
	{
		super();
		this._myName = user;
		this._hostname = host;
		this._portnum = port;
	}
	
	/**
	 * The overrided run method of ClientThread.
	 * This method handles all the data recieved from
	 * the server and displays it on the main gui.
	 */
	public void run()
	{
		int state = 1;
		
		try 
		{
			_socket = new Socket(_hostname, Integer.parseInt(_portnum));
						
			_sendBuf = new byte[_socket.getSendBufferSize()];
			_recBuf = new byte[_socket.getReceiveBufferSize()];
			
			_address = _socket.getInetAddress();
			_port = _socket.getPort();
			_user = new User(_myName, _address, _port);
			
			_sendBuf = toByteArray(_user);
			_socket.getOutputStream().write(Message.USER);
			_socket.getOutputStream().write(_sendBuf);
			_socket.getOutputStream().flush();
			
			state = _socket.getInputStream().read();
			
			while (state == 200)
			{
				buildRelog();
				state = _socket.getInputStream().read();
				System.out.println(state);
				_relogFrame.dispose();
				
				if (state != 200)
				{
					break;
				}
			}

			Client._mainFrame.setTitle("Cr@p Talk: " + _myName);
			Client._mainFrame.setVisible(true);
			
			
			while(true)
			{
				Message rec = new Message();
				try
				{
					state = _socket.getInputStream().read();
				}
				finally
				{
					if (state == Message.HASHSET)
					{
						System.out.println("doing hashset");
						_socket.getInputStream().read(_recBuf);
						Object[] online = (Object[]) toObject(_recBuf);
						String[] onlineList = new String[online.length];
						for (int i = 0; i < online.length; i++)
						{
							onlineList[i] = (String) online[i];
						}
						Client._userList.setListData(onlineList);
					}
					else if (state == Message.LOBBY)
					{
						System.out.println("doing lobby");
						_socket.getInputStream().read(_recBuf);
						rec = (Message) toObject(_recBuf);
						
						Client._chatLog.append("[" + rec.getOrigin() +"]: " + rec.getMessage() + "\n");
					}
					else if (state == Message.WHISPER)
					{
						System.out.println("doing whisper");
						_socket.getInputStream().read(_recBuf);
						rec = (Message) toObject(_recBuf);
						
						Client._chatLog.append("[" + rec.getOrigin() +"(whisp)]: " + rec.getMessage() + "\n");
					}
					else if (state == Message.SHARED)
					{
						System.out.println("doing shared");
						ArrayList<String> files = new ArrayList<String>();
						Scanner file = new Scanner(new File("shared.txt"));
						
				        while(file.hasNextLine())           
				        {
				            String line = file.nextLine();
				            files.add(line);
				        }
				        _sendBuf = toByteArray(files.toArray());
				        _socket.getOutputStream().write(Message.TEXT);
				        _socket.getOutputStream().write(_sendBuf);
						_socket.getOutputStream().flush();
						System.out.println("sent data");
				        
					}
					else if (state == Message.RESULTS)
					{
						_socket.getInputStream().read(_recBuf);
	        			Object[] lines = (Object[]) toObject(_recBuf);
	        			
	        			String[] mal = new String[lines.length];
	        			
	        			for (int i = 0; i < lines.length; i++)
	        			{
	        				mal[i] = (String) lines[i];
	        			}
	        			buildResultGUI(mal);
					}
					else if (state == Message.DC)
					{
						System.out.println("doing dc");
						_socket.getInputStream().read(_recBuf);
						rec = (Message) toObject(_recBuf);
						
						Client._chatLog.append(rec.getMessage());
					}
					else if (state == Message.SERVERDOWN)
					{
						System.out.println("doing serverdown");
						JOptionPane.showMessageDialog(null, "Server has gone down...");
						System.exit(0);
					}
					else if (state == Message.ERROR)
					{
						System.out.println("doing error");
						_socket.getInputStream().read(_recBuf);
						rec = (Message) toObject(_recBuf);
						JOptionPane.showMessageDialog(null, "It's bad to talk to yourself...");
					}
					else if (state == Message.REMOVED)
					{
						System.out.println("doing doing removed");
						_socket.getInputStream().read(_recBuf);
						rec = (Message) toObject(_recBuf);
						System.exit(0);
					}
				}
			}
		}
		catch (SocketException e) 
		{
			JOptionPane.showMessageDialog(null, "Server is not responding");
			System.exit(0);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		} 
	}
	
	public void buildResultGUI(String[] results)
	{
		JFrame frame = new JFrame("Results of search");

	    DefaultListModel model = new DefaultListModel();
	    model.ensureCapacity(results.length);
	    for (int i = 0; i < results.length; i++)
	    {
	    	model.addElement(results[i]);
	    }
	    JList jlist2 = new JList(model);
	    jlist2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    ListSelectionListener listener = new ListSelectionListener()
	    {
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting()) {
					JList source = (JList)e.getSource();
		            String selected = source.getSelectedValue().toString();
		            System.out.println("you want: " + selected);
	            }
				
			}
		};
	    jlist2.addListSelectionListener(listener);

	    
	    JScrollPane scrollPane2 = new JScrollPane(jlist2);
	    frame.add(scrollPane2, BorderLayout.CENTER);

	    frame.setSize(400, 350);
	    frame.setVisible(true);
	}
	
	/**
	 * Method that asks the user for a new username and
	 * sends it to the server.
	 * 
	 * @param address : clients InetAddress
	 * @param port : clients port number
	 */
	public void buildRelog()
	{
		_relogFrame = new JFrame();
		_relogFrame.setSize(300, 200);
		_relogFrame.setResizable(false);
		_relogFrame.setLayout(null);
		_relogFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		_nameLabel = new JLabel("Username:");
		_relogFrame.getContentPane().add(_nameLabel);
		_nameLabel.setBounds(50, 50, 100, 25);
		
		
		_nameField = new JTextField(30);
		_relogFrame.getContentPane().add(_nameField);
		_nameField.setBounds(100, 50, 100, 25);
		
		_confirmButton = new JButton("Confirm");
		_confirmButton.addActionListener(
				new java.awt.event.ActionListener()
				{
					public void actionPerformed(java.awt.event.ActionEvent event)
					{
						if (!_nameField.getText().equals(""))
						{
							try
							{
								_myName = _nameField.getText().trim();
								Client._userName = _myName;
								_user = new User(_myName, _address, _port);
								
								_sendBuf = toByteArray(_user);
								_socket.getOutputStream().write(Message.USER);
								_socket.getOutputStream().write(_sendBuf);
								_socket.getOutputStream().flush();		
								
							}
							catch (Exception e)
							{
								
							}
						}
					}
				});
		_relogFrame.getContentPane().add(_confirmButton);
		_confirmButton.setBounds(50, 100, 100, 25);
		
		_relogFrame.setVisible(true);
	}
	
	
	/**
	 * Method that is run when the send button is pressed.
	 * Sends the message object to the server.
	 * 
	 * @param message : the message object that is to be sent to
	 * 				    the server.
	 */
	public static void Send(Message message)
	{
		try
		{
			_sendBuf = toByteArray(message);

			//dc message
			if (message.getRecipient().equalsIgnoreCase("") &&
				message.getMessage().equalsIgnoreCase("%BYE%"))
			{
				_socket.getOutputStream().write(Message.BYE);
			}
			//lobby message
			else if (message.getRecipient().equalsIgnoreCase(""))
			{
				_socket.getOutputStream().write(Message.LOBBY);
			}
			//search request
			else if (message.getRecipient().equalsIgnoreCase("all"))
			{
				_socket.getOutputStream().write(Message.SEARCH);
			}
			//whisper message
			else
			{
				_socket.getOutputStream().write(Message.WHISPER);
			}
			_socket.getOutputStream().write(_sendBuf);
			_socket.getOutputStream().flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Method that converts an object to a byte array.
	 * 
	 * @param obj : Object that is to be converted to a byte array. 
	 * @return byte array of the object.
	 * @throws IOException : Exception that is thrown when the object
	 * 						 cannot be converted to a byte array.
	 */
    public static byte[] toByteArray(Object obj) throws IOException
    {
        byte[] bytes = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        
        try 
        {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
        }
        finally
        {
            if (oos != null)
            {
                oos.close();
            }
            if (bos != null)
            {
                bos.close();
            }
        }
        return bytes;
    }

    /**
     * Method that converts a byte array to an object.
     * 
     * @param bytes : the byte array to be converted  to an object.
     * @return Object of the byte array
     * @throws IOException : Exception that is thrown when the object
	 * 						 cannot be converted to a byte array.
     * @throws ClassNotFoundException : Exception that is thrown when
     * 									the object class is not available.
     */
    public static Object toObject(byte[] bytes) throws IOException, ClassNotFoundException
    {
        Object obj = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        
        try
        {
            bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);
            obj = ois.readObject();
        }
        finally
        {
            if (bis != null)
            {
                bis.close();
            }
            if (ois != null)
            {
                ois.close();
            }
        }
        return obj;
    }
}