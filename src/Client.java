import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.SecureRandom;

import javax.swing.*;

/**
 * 
 * @author A Visser, 17224047
 * 		   T Butler, 17403812
 *
 */
public class Client implements ActionListener
{
	//global variables
	private ClientThread _myClient;
	public static String _userName = "";
	private String _hostName = "";
	private String _portNumber = "";
	private PrintWriter _out;
	public static String _key = "";
	private SecureRandom _random = new SecureRandom();
	public static boolean _isUploadPaused = false;
	public static boolean _isDownloadPaused = false;
	public static SenderThread _sending;
	public static ReceiverThread _receiving;
	
	//login gui
	public static JFrame _loginFrame;
	private JLabel _usernameLabel;
	public static JTextField _usernameField;
	private JLabel _portLabel;
	private JTextField _portField;
	private JLabel _hostLabel;
	private JTextField _hostField;
	private JLabel _loginHeader;
	private JButton _loginButton;
	
	//download gui
	private JFrame _downloadFrame;
	private JLabel _fileLabel;
	private JLabel _downloadHeader;
	private JTextField _fileField;
	private JButton _searchButton;
	
	//main gui
	public static JFrame _mainFrame;
	private JLabel _mainHeader;
	private JLabel _messageLabel;
	private JTextField _messageField;
	private JLabel _chatLabel;
	private JLabel _userLabel;
	private JButton _sendButton;
	private JButton _shareButton;
	private JButton _downloadButton;
	private JScrollPane _chatScroller;
	private JScrollPane _onlineScroller;
    private JFileChooser _fileBrowser;
    private JLabel _uploadLabel;
    public static JProgressBar _uploadBar;
    private JLabel _downloadLabel;
    public static JProgressBar _downloadBar;
    public static JList _userList;
    public static JTextArea _chatLog;
	
    /**
     * Client constructor.
     * This is an empty method to make use of
     * non-static variables.
     */
	public Client()
	{
		
	}
	
	/**
	 * Main of the client that builds the gui
	 * and starts a client thread.
	 * 
	 * @param args : empty
	 */
	public static void main(String[] args)
	{
		Client test = new Client();
		try {
			test._out = new PrintWriter("shared.txt");
			test._out.close();
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		test.buildMain();
	}
	
	public String nextSessionId()
	{
		return new BigInteger(130, _random).toString(32);
	}
	
	/**
	 * Closes login gui and shows main gui.
	 * Creates a client thread and starts it.
	 */
	public void connect()
	{
		try
		{			
			_loginFrame.setVisible(false);
			
			_myClient = new ClientThread(_userName, _hostName, _portNumber);
			_myClient.start();
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, "Server not responding.");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * Initializes main gui components and adds them
	 * to the main JFrame
	 */
	public void buildMain()
	{
		_mainFrame = new JFrame("Cr@p Talk");
		_mainFrame.setSize(800,450);
		_mainFrame.setLayout(null);
		_mainFrame.setResizable(false);
		_mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_mainFrame.addWindowListener(
				new WindowAdapter()
				{
					public void windowClosing(WindowEvent evt) 
					{
						Message bye = new Message(_userName, "", "%BYE%");
						ClientThread.Send(bye);
					}
				});
		
		_mainHeader = new JLabel("Welcome to Cr@p Talk Beta!");
		_mainFrame.getContentPane().add(_mainHeader);
		_mainHeader.setBounds(50, 10, 340, 25);
		
		_messageLabel = new JLabel("Message:");
		_mainFrame.getContentPane().add(_messageLabel);
		_messageLabel.setBounds(50, 50, 340, 25);
		
		_messageField = new JTextField(30);
		_messageField.requestFocus();
		_mainFrame.getContentPane().add(_messageField);
		_messageField.setBounds(130, 50, 340, 25);
		
		_sendButton = new JButton("Send");
		_sendButton.addActionListener(this);
		_mainFrame.getContentPane().add(_sendButton);
		_sendButton.setBounds(130, 80, 100, 25);
		
		_shareButton = new JButton("Share");
		_shareButton.addActionListener(this);
		_mainFrame.getContentPane().add(_shareButton);
		_shareButton.setBounds(240, 80, 100, 25);
		
		_downloadButton = new JButton("Pirate");
		_downloadButton.addActionListener(this);
		_mainFrame.getContentPane().add(_downloadButton);
		_downloadButton.setBounds(350, 80, 100, 25);
		
		
		_userLabel = new JLabel("Online users:");
		_mainFrame.getContentPane().add(_userLabel);
		_userLabel.setBounds(550, 20, 130, 25);
		
		_userList = new JList();
		
		_onlineScroller = new JScrollPane();
		_onlineScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		_onlineScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		_onlineScroller.setViewportView(_userList);
		_mainFrame.getContentPane().add(_onlineScroller);
		_onlineScroller.setBounds(550, 60 , 200, 240);
		
		
		_chatLabel = new JLabel("Chat Log:");
		_mainFrame.getContentPane().add(_chatLabel);
		_chatLabel.setBounds(50, 120, 340, 25);
		
		_chatLog = new JTextArea();
		_chatLog.setColumns(20);
		_chatLog.setLineWrap(true);
		_chatLog.setRows(5);
		_chatLog.setEditable(false);
		
		_chatScroller = new JScrollPane();
		_chatScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		_chatScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		_chatScroller.setViewportView(_chatLog);
		_mainFrame.getContentPane().add(_chatScroller);
		_chatScroller.setBounds(50, 150 , 450, 180);
			
		_uploadLabel = new JLabel("Upload:");
		_mainFrame.getContentPane().add(_uploadLabel);
		_uploadLabel.setBounds(50, 350, 340, 25);
		
		_uploadBar = new JProgressBar(0, 100);
		_uploadBar.addMouseListener(
				new MouseAdapter() 
				{            
					public void mouseClicked(MouseEvent e)
					{
						if (_uploadBar.getValue() > 0)
						{
							if (_isUploadPaused == false)
							{
								_sending.suspend();
								_isUploadPaused = true;
								System.out.println("paused");
							}
							else
							{
								_sending.resume();
								_isUploadPaused = false;
								System.out.println("resuming");
							}
						}
					}                                     
				});
		_uploadBar.setValue(0);
		_uploadBar.setStringPainted(true);
		_mainFrame.getContentPane().add(_uploadBar);
		_uploadBar.setBounds(130, 350, 620, 25);
		
		_downloadLabel = new JLabel("Download:");
		_mainFrame.getContentPane().add(_downloadLabel);
		_downloadLabel.setBounds(50, 395, 340, 25);
        
		_downloadBar = new JProgressBar(0, 100);
		_downloadBar.addMouseListener(
				new MouseAdapter() 
				{            
					public void mouseClicked(MouseEvent e)
					{
						if (_downloadBar.getValue() > 0)
						{
							if (_isDownloadPaused == false)
							{
								_receiving.suspend();
								_isDownloadPaused = true;
								System.out.println("paused");
							}
							else
							{
								_receiving.resume();
								_isDownloadPaused = false;
								System.out.println("resuming");
							}
						}
					}                                     
				});
		_downloadBar.setValue(0);
		_downloadBar.setStringPainted(true);
		_mainFrame.getContentPane().add(_downloadBar);
		_downloadBar.setBounds(130, 395, 620, 25);
		
        _fileBrowser = new JFileChooser();
        _fileBrowser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
        _messageField.requestFocusInWindow();
        buildLogin();
	}
	
	/**
	 * Initializes login gui components and displays the
	 * login gui.
	 */
	public void buildLogin()
	{	    
		_loginFrame = new JFrame("Login config");
		_loginFrame.setSize(400,300);
		_loginFrame.setLayout(null);
		_loginFrame.setResizable(false);
		_loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		_loginHeader = new JLabel("Please fill in login details.");
		_loginFrame.getContentPane().add(_loginHeader);
		_loginHeader.setBounds(100, 10, 340, 25);
		
		_usernameLabel = new JLabel("Username:");
		_loginFrame.getContentPane().add(_usernameLabel);
		_usernameLabel.setBounds(15, 60, 100, 25);
		
		_usernameField = new JTextField(30);
		_loginFrame.getContentPane().add(_usernameField);
		_usernameField.setBounds(115, 60, 200, 25);
		
		_portLabel = new JLabel("Port number:");
		_loginFrame.getContentPane().add(_portLabel);
		_portLabel.setBounds(15, 100, 100, 25);
		
		_portField = new JTextField("3000");
		_loginFrame.getContentPane().add(_portField);
		_portField.setBounds(115, 100, 200, 25);
		
		_hostLabel = new JLabel("Host name:");
		_loginFrame.getContentPane().add(_hostLabel);
		_hostLabel.setBounds(15, 140, 100, 25);
		
		_hostField = new JTextField("localhost");
		_loginFrame.getContentPane().add(_hostField);
		_hostField.setBounds(115, 140, 200, 25);
		
		_loginButton = new JButton("Login");
		_loginButton.addActionListener(this);
		_loginFrame.getContentPane().add(_loginButton);
		_loginButton.setBounds(115, 180, 100, 25);
		
		_loginFrame.setVisible(true);
		
	}
	
	public void buildDownloadGUI()
	{
		_downloadFrame= new JFrame("Search for Booty");
		_downloadFrame.setSize(400,200);
		_downloadFrame.setLayout(null);
		_downloadFrame.setResizable(false);
		
		_downloadHeader = new JLabel("Insert filename to search for.");
		_downloadFrame.getContentPane().add(_downloadHeader);
		_downloadHeader.setBounds(100, 10, 340, 25);
		
		_fileLabel = new JLabel("Filename:");
		_downloadFrame.getContentPane().add(_fileLabel);
		_fileLabel.setBounds(15, 60, 100, 25);
		
		_fileField = new JTextField(30);
		_downloadFrame.getContentPane().add(_fileField);
		_fileField.setBounds(115, 60, 200, 25);
		
		_searchButton = new JButton("Search");
		_searchButton.addActionListener(this);
		_downloadFrame.getContentPane().add(_searchButton);
		_searchButton.setBounds(115, 100, 100, 25);
		
		_downloadFrame.setVisible(true);
	}
	
	/**
	 * Action listener for the buttons in the main gui
	 * as well as the button in the login gui.
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == _loginButton)
		{
			if (!_usernameField.getText().equals("") && !_hostField.getText().equals("")
					&& !_portField.getText().equals(""))
			{
				_userName = _usernameField.getText().trim();
				_hostName = _hostField.getText().trim();
				_portNumber = _portField.getText().trim();
				connect();
			}
			else
			{
				if (_usernameField.getText().equals("") && _hostField.getText().equals("")
					&& _portField.getText().equals(""))
				{
					JOptionPane.showMessageDialog(null, "Please fill in all fields.");
				}
				else if (_usernameField.getText().equals(""))
				{
					JOptionPane.showMessageDialog(null, "Please enter a username.");
				}
				else if (_hostField.getText().equals(""))
				{
					JOptionPane.showMessageDialog(null, "Please provide a hostname.");
				}
				else if (_portField.getText().equals(""))
				{
					JOptionPane.showMessageDialog(null, "Please enter an available port.");
				}
			}
		}
		else if (e.getSource() == _sendButton)
		{
			if (!_messageField.getText().equals(""))
			{
				char isWhisp = _messageField.getText().trim().charAt(0);
				if (isWhisp == '@')
				{
					String temp = _messageField.getText().trim();
					int firstSpace = temp.indexOf(",");
					String toUser = temp.substring(1, firstSpace);
					String message = temp.substring(firstSpace +1);
					
					Message whisper = new Message(_userName, toUser, message.trim());
					ClientThread.Send(whisper);
					_messageField.setText("");
					_messageField.requestFocus();
					
				}
				else
				{
					Message lobby = new Message(_userName, "", _messageField.getText());
					ClientThread.Send(lobby);
					_messageField.setText("");
					_messageField.requestFocus();
				}
			}
		}
		else if (e.getSource() == _shareButton)
		{
	        int returnValue = _fileBrowser.showOpenDialog(null);
	        
	        if (returnValue == JFileChooser.APPROVE_OPTION)
	        {
				try
				{
					_out = new PrintWriter(new FileWriter("shared.txt", true));
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
	        	
	        	File selectedFile = _fileBrowser.getSelectedFile();
	        	String path = selectedFile.getPath();
	        	String name = selectedFile.getName();;
	        	_chatLog.append("( shared: " + name +" )\n");
	        	_out.print(name + "&&" + path + "\n");
	        	_out.flush();
	        	_out.close();
	        }
		}
		else if (e.getSource() == _downloadButton)
		{
			buildDownloadGUI();
		}
		else if (e.getSource() == _searchButton)
		{
			if (!_fileField.getText().equals(""))
			{
				_key = nextSessionId();
				Message search = new Message(_userName, "all", _fileField.getText().trim() + "$$" + _key);
				ClientThread.Send(search);
				_downloadFrame.dispose();
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Please enter a filename.");
			}
		}
	}
}
