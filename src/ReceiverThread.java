import java.io.*;
import java.net.*;


public class ReceiverThread extends Thread
{
	private String _file = "";
	private String _key = "";
	private String _host = "";
	private int _port = -1;
	private long _size = -1;
	
	private Socket _client;
	
	public ReceiverThread(String file, String host, int port, String key, long fileSize)
	{
		this._file = file;
		this._key = key;
		this._host = host;
		this._port = port;
		this._size = fileSize;
	}
	
	public void run()
	{
		try
		{
			_client = new Socket(_host, _port);
			
			System.out.println("receiver key: " + _key);
			
			byte[] buffer = new byte[65536];
			int number;
	
			InputStream socketStream = _client.getInputStream();
			File f = new File(_file);
	
			OutputStream fileStream = new FileOutputStream(f);
			double total = 0;
			
			while ((number = socketStream.read(buffer)) != -1)
			{
				double prog = (number / (double) _size);
				total = total + (prog * 100);

				Client._downloadBar.setValue((int) total);
				Client._downloadBar.setStringPainted(true);
				
			    fileStream.write(buffer,0,number);
			}
		
			Client._chatLog.append("( done downloading " + _file +" )\n");
			fileStream.close();
			socketStream.close();
			
			Client._downloadBar.setValue(0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
