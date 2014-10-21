import java.io.*;
import java.net.*;


public class SenderThread extends Thread
{
	private String _file = "";
	private String _path = "";
	private String _key = "";
	private int _port = -1;
	
	private ServerSocket _serverSocket;
	private Socket _client;
	
	public SenderThread(String fileName, String path, int port, String key)
	{
		this._file = fileName;
		this._path = path;
		this._key = key;
		this._port = port;
	}
	
	public void run()
	{
		File file = new File(_path);
		long fileSize = file.length();
		
		try
		{
			System.out.println("sendPort: " + _port);
			_serverSocket = new ServerSocket(_port);
			System.out.println("listening");
			_client = _serverSocket.accept();
			System.out.println("connected");
			DataInputStream fileInputStream = new DataInputStream(new FileInputStream(_path));
				
			byte[] buffer = new byte[65536];
			int number;
			//double total = 0;
			while ((number = fileInputStream.read(buffer)) != -1)
			{
				//double prog = (number / (double) fileSize);
				//total = total + (prog * 100);
				//System.out.println(total + "%");
			    _client.getOutputStream().write(buffer, 0, number);
			}
	
				_client.getOutputStream().close();
				fileInputStream.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
