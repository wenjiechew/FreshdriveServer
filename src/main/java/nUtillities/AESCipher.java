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

import nConstants.AESConstants;

/**
 * This is the encryption class to encrypt strings in 256 bit.
 */

public class AESCipher {
	private static AESConstants aesConstants = AESConstants.getInstance();
	private static SecretKey key;
	private static char[] password = null;

	/**
	 * Using the securerandom class to generate a cryptographically strong
	 * random number, the salt is then stored as a byte
	 * 
	 * @return salt a 8 byte salt
	 */
	public static byte[] generateSalt() {
		SecureRandom randomSalt = new SecureRandom();
		byte[] salt = new byte[8];
		randomSalt.nextBytes(salt);
		return salt;
	}

	/**
	 * A random key is generated using a fixed constant password,and the salt
	 * generated being passed into the function
	 * 
	 * @param salt
	 *            the 8 byte salt being passed in
	 * @return SecretKey the key generated
	 */
	public static SecretKey getKey(byte[] salt) {
		try {
			password = aesConstants.getAESPass().toCharArray();
			/* Derive the key, given password and salt. */
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			key = new SecretKeySpec(tmp.getEncoded(), "AES");

			return key;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/*
	 * in JRE/lib/security folder with the ones in FreshdriveServer resources.
	 * This updates the restricted policies to unlimited policies to allow for a
	 * larger key size.
	 * 
	 * "InvalidKeyException: Illegal Key Size" error may occur if not done.
	 */
	/**
	 * This function encrypts the string being passed in,by first geting a salt
	 * and generating the key from it.It then returns the encrypted
	 * filepath,salt and the iv used for decrypting the filepath again
	 * 
	 * @param filePath
	 *            the filepath string needed to be encrypted
	 * @return fileInfo a 2d byte array storing the IV,encrypted string and salt
	 */
	public static byte[][] EncryptString(String filePath) {
		try {
			/* Generate a 8 byte SecureRandom salt */
			byte[] salt = generateSalt();

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, getKey(salt));
			AlgorithmParameters params = cipher.getParameters();
			byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
			byte[] ciphertext = cipher.doFinal(filePath.getBytes("UTF-8"));

			byte[][] fileInfo = { ciphertext, iv, salt };
			return fileInfo;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Using the ciphertext,iv and salt gotten from the database,this function
	 * would then decrypt the encrypted filepath
	 * 
	 * @param ciphertext
	 *            the encrypted filepath gotten from the DB
	 * @param iv
	 *            the iv of the encrypted filepath gotten from the DB
	 * @param salt
	 *            the salt needed to decrypt the filepath
	 * @return plaintext the actual filepath after decryption
	 */
	public static String DecryptString(byte[] ciphertext, byte[] iv, byte[] salt) {
		try {
			/*
			 * Decrypt the message, given derived key and initialization vector.
			 */
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, getKey(salt), new IvParameterSpec(iv));
			String plaintext = new String(cipher.doFinal(ciphertext), "UTF-8");
			return plaintext;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
