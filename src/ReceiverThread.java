import java.io.*;
import java.net.*;


public class ReceiverThread extends Thread
{
	private String _file = "";
	private String _path = "";
	private String _key = "";
	private String _host = "";
	private int _port = -1;
	
	private Socket _client;
	
	public ReceiverThread(String file, String host, int port, String key)
	{
		this._file = file;
		this._key = key;
		this._host = host;
		this._port = port;
	}
	
	public void run()
	{
		try
		{
			System.out.println("file " + _file);
			System.out.println("listening: ");
			_client = new Socket(_host, _port);
			System.out.println("connected");
			
			byte[] buffer = new byte[65536];
			int number;
	
			InputStream socketStream = _client.getInputStream();
			File f = new File(_file);
	
			OutputStream fileStream = new FileOutputStream(f);
			double total = 0;
			
			while ((number = socketStream.read(buffer)) != -1)
			{
				//double prog = (number / (double) fileSize);
				//total = total + (prog * 100);
				//System.out.println(total + "%");
			    fileStream.write(buffer,0,number);
			}
		

			fileStream.close();
			socketStream.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
