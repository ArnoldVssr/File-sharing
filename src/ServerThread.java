import java.io.*;
import java.net.*;
import java.util.Map;

/**
 * 
 * @author A Visser, 17224047
 * 		   T Butler, 17403812
 *
 */
public class ServerThread extends Thread
{
	private Socket _socket = null;
	private static byte[] _sendBuf = null;
	private static byte[] _recBuf = null;
    private static User _currentUser = new User();
	
    /**
     * Constructor of the ServerThread.
     * So that multiple Clients may connect.
     * 
     * @param socket : ServerSocket from Server.java.
     */
    public ServerThread(Socket socket)
    {
    	super();
    	this._socket = socket;
    }
    
    /**
     * Overriden run method for ServerThread.
     * This method handles the data received from clients
     * as well as the sending of data to clients.
     */
	public void run()
	{
        try
        {
        	int state;
        	
        	_sendBuf = new byte[_socket.getSendBufferSize()];
        	_recBuf = new byte[_socket.getReceiveBufferSize()];        	
        	
        	while (true)
        	{
        		state = _socket.getInputStream().read();
        		
	        	if (state == Message.USER)
	    		{
		        	_socket.getInputStream().read(_recBuf);
					_currentUser = (User) toObject(_recBuf);
		        	
					if (Server.users.contains(_currentUser))
					{
						Server._serverLog.append("Not unique.\n");
						_socket.getOutputStream().write(200);
						_socket.getOutputStream().flush();
					}
					else
					{
						Server._serverLog.append("Unique.\n");
			        	Server.users.add(_currentUser);
						Server.Maptest.put(_currentUser.getName(),_socket);
						Server.usernames.add(_currentUser.getName());
						Server.curLen = Server.users.size();
						System.out.println("added: " + _currentUser.getName());
						
						_socket.getOutputStream().write(500);
						_socket.getOutputStream().flush();
						break;
					}
	    		}
        	}
        	
        	while(true)
        	{
        		PrintUsers();
        		state = _socket.getInputStream().read();
        		
        		if (state == Message.WHISPER)
        		{
        			_socket.getInputStream().read(_recBuf);
        			Message Temp = (Message) toObject(_recBuf);
        			Socket rec = Server.Maptest.get(Temp.getRecipient());
        			_sendBuf = toByteArray(Temp);
        			
        			Server._serverLog.append(Temp.getOrigin() + " -> " + Temp.getRecipient() + "./n");
        			
        			if (Temp.getRecipient().equalsIgnoreCase(Temp.getOrigin()))
        			{
	        			rec = Server.Maptest.get(Temp.getOrigin());
	        			
	        			rec.getOutputStream().write(Message.ERROR);
	    				rec.getOutputStream().flush();
	        			
	        			rec.getOutputStream().write(_sendBuf);
	    				rec.getOutputStream().flush();
        			}
        			else if (rec != null)
        			{        				
        				rec.getOutputStream().write(Message.WHISPER);
        				rec.getOutputStream().flush();
        				
        				rec.getOutputStream().write(_sendBuf);
        				rec.getOutputStream().flush();
        				
        				rec = Server.Maptest.get(Temp.getOrigin());
            			
            			rec.getOutputStream().write(Message.WHISPER);
        				rec.getOutputStream().flush();
            			
            			rec.getOutputStream().write(_sendBuf);
        				rec.getOutputStream().flush();
        			}
        		}
        		else if (state == Message.LOBBY)
        		{
        			_socket.getInputStream().read(_recBuf);
        			Message Temp = (Message) toObject(_recBuf);
        			
        			Server._serverLog.append(Temp.getOrigin() + " -> all.\n");
        			
    				for(Map.Entry<String, Socket> entry: Server.Maptest.entrySet())
    				{
    					_sendBuf = toByteArray(Temp);
    					//lobby id
    					entry.getValue().getOutputStream().write(Message.LOBBY);
    					entry.getValue().getOutputStream().flush();
    					//message
    					entry.getValue().getOutputStream().write(_sendBuf);
    					entry.getValue().getOutputStream().flush();
    				}
        		}
        		else if (state == Message.HASHSET)
        		{
        			for(Map.Entry<String, Socket> entry: Server.Maptest.entrySet())
    				{
        				_sendBuf = toByteArray(Message.HASHSET);
    					entry.getValue().getOutputStream().write(_sendBuf);
    					entry.getValue().getOutputStream().flush();
        				
    					_sendBuf = toByteArray(Server.usernames);
    					entry.getValue().getOutputStream().write(_sendBuf);
    					entry.getValue().getOutputStream().flush();
    				}
        		}
        		else if (state == Message.BYE)
        		{
        			_socket.getInputStream().read(_recBuf);
        			Message Temp = (Message) toObject(_recBuf);
        			
        			Message send = new Message("server", "", Temp.getOrigin() + " disconnected...\n");
        			for(Map.Entry<String, Socket> entry: Server.Maptest.entrySet())
    				{
        				
        				if (!entry.getKey().equals(Temp.getOrigin()))
        				{
        					_sendBuf = toByteArray(send);
	    					
	    					entry.getValue().getOutputStream().write(Message.DC);
	    					entry.getValue().getOutputStream().flush();
	    					
	    					entry.getValue().getOutputStream().write(_sendBuf);
	    					entry.getValue().getOutputStream().flush();
        				}
    				}
        			
        			_sendBuf = toByteArray(Temp);
        			Socket rec = Server.Maptest.get(Temp.getOrigin());
        			
        			rec.getOutputStream().write(Message.REMOVED);
    				rec.getOutputStream().flush();   
        			
        			rec.getOutputStream().write(_sendBuf);
    				rec.getOutputStream().flush();      			
        			
        			User dc = new User(Temp.getOrigin(), null, 0);
        			Server.prevLen = Server.usernames.size();
            		Server.Maptest.remove(Temp.getOrigin());
            		Server.users.remove(dc);
            		Server.usernames.remove(Temp.getOrigin());
            		Server.curLen = Server.usernames.size();
            		PrintUsers();
            		break;
        		}
        	}
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
	
	/**
	 * Method that sends an updated userlist to all the
	 * clients whenever a user disconnects or a new
	 * user connects.
	 */
	public static void PrintUsers()
	{		
		if (Server.curLen == 0)
		{
			Server._serverLog.append("Users: \n");
			Server._serverLog.append(" none\n");
			
			try
			{
				for(Map.Entry<String, Socket> entry: Server.Maptest.entrySet())
				{
					Object[] onlineUsers = Server.usernames.toArray();
					_sendBuf = toByteArray(onlineUsers);
	
					entry.getValue().getOutputStream().write(Message.HASHSET);
					entry.getValue().getOutputStream().flush();
	
					entry.getValue().getOutputStream().write(_sendBuf);
					entry.getValue().getOutputStream().flush();
				}
				
				Server.prevLen = Server.curLen;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else if (Server.prevLen != Server.curLen)
		{
			Server._serverLog.append("Users: \n");
			for (int i = 0; i < Server.usernames.size(); i++)
			{
				Server._serverLog.append(" " + Server.usernames.get(i) + "\n"); 
			}
			
			try
			{
				for(Map.Entry<String, Socket> entry: Server.Maptest.entrySet())
				{
					Object[] onlineUsers = Server.usernames.toArray();
					_sendBuf = toByteArray(onlineUsers);
	
					entry.getValue().getOutputStream().write(Message.HASHSET);
					entry.getValue().getOutputStream().flush();
	
					entry.getValue().getOutputStream().write(_sendBuf);
					entry.getValue().getOutputStream().flush();
				}
				Server.prevLen = Server.curLen;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		Server._serverLog.append("\n");
	}
	
	/**
	 * Method that notifies all the clients that the server
	 * has been closed.
	 */
	public static void ServerClosed()
	{
		try
		{
			for(Map.Entry<String, Socket> entry: Server.Maptest.entrySet())
			{
				entry.getValue().getOutputStream().write(Message.SERVERDOWN);
				entry.getValue().getOutputStream().flush();
			}
			System.exit(0);
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