import java.math.BigInteger;
import java.security.SecureRandom;

public final class SecurityTest
{
	private SecureRandom random = new SecureRandom();
	
	public String nextSessionId()
	{
		return new BigInteger(130, random).toString(32);
	}
	
	public static void main(String[] args)
	{		
		SecurityTest mal = new SecurityTest();
		System.out.println(mal.nextSessionId());
		
	}

}
