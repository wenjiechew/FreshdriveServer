package nUtillities;

import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AESCipher {
	private static SecretKey key;
	//TODO: Save the password somewhere else, maybe in a text file and retrieve it
	private static char[] password = new char[]{'p','a','s','s','w','o','r','d'};
	
	public static byte[] generateSalt(){
		SecureRandom randomSalt = new SecureRandom();
		byte[] salt = new byte[8];
		randomSalt.nextBytes(salt);
		System.out.println("[Information] Salt: " + salt);
		return salt;
	}
	
	public static SecretKey getKey(byte[] salt){
		try {
			/* Derive the key, given password and salt. */
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			key = new SecretKeySpec(tmp.getEncoded(), "AES");
			System.out.println("[Information] Key: " + key.toString());
			return key;
		}
		catch (Exception ex)
		{
			System.err.println("[Error] getKey(): " + ex);
		}
		return null;
	}
	
	/*TODO: Save a copy and replace local_policy.jar and US_export_policy.jar in 
	 JRE/lib/security folder with the ones in FreshdriveServer resources. 
	 This updates the restricted policies to unlimited policies to allow for a larger key size.
	 
	 "InvalidKeyException: Illegal Key Size" error may occur if not done.*/
	public static void EncryptString (String filePath){
		try {
			/*Generate a 8 byte SecureRandom salt*/
			byte[] salt = generateSalt();
			
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, getKey(salt));
			AlgorithmParameters params = cipher.getParameters();
			byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
			byte[] ciphertext = cipher.doFinal(filePath.getBytes("UTF-8"));
			System.out.println("[Information] " + filePath + " has been encrypted.");
			System.out.println("[Information] IV: " + iv.toString());
			System.out.println("[Information] Cipher: " + ciphertext.toString());
			//TODO: Return salt, IV and encrypted string to save in database
			DecryptString(ciphertext, iv, salt); //Testing purposes only
		}
		catch (Exception ex)
		{
			System.err.println("[Error] EncryptString(): " + ex);
		}
	}
	
	public static void DecryptString (byte[] ciphertext, byte[] iv, byte[] salt){
		try {
			/* Decrypt the message, given derived key and initialization vector. */
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, getKey(salt), new IvParameterSpec(iv));
			String plaintext = new String(cipher.doFinal(ciphertext), "UTF-8");
			System.out.println("[Information] Decryption has been performed.");
			System.out.println("[Information] " + plaintext);
		}
		catch (Exception ex)
		{
			System.err.println("[Error] DecryptString(): " + ex);
		}
	}
}
