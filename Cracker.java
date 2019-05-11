import javax.swing.plaf.synth.SynthScrollBarUI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CountDownLatch;

import static java.security.MessageDigest.*;

public class Cracker {
	// Array of chars used to produce strings
	public static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz0123456789.,-!".toCharArray();

	public static void main(String[] args) {
		if (args.length == 1) {
			String toHash = "!!!";
			try {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(toHash.getBytes());
				System.out.println(Cracker.hexToString(md.digest()));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}

		}else if(args.length == 3 ){
			String hash = "9a7b006d203b362c8cef6da001685678fc1d463a";// args[0];
			int maxSize = 3;//Integer.parseInt(args[1]);
			int workers = 38;//Integer.min(40 , Integer.parseInt(args[2]));
			try {
				hash = args[0];
				maxSize = Integer.parseInt(args[1]);
				workers = Integer.min(40, Integer.parseInt(args[2]));
			}catch (Exception e ){

			}
			int soloWork = CHARS.length/workers;
			CountDownLatch cd = new CountDownLatch(workers);
			System.out.println("starting  searching ....................");
			for(int i = 0 ; i<workers ; i++){
				Hacker h = new Hacker(i*soloWork ,
								i==workers-1 ?CHARS.length:soloWork ,maxSize , hash , cd);
				h.start();
			}
			try {
				cd.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("ended searching");

		}

	}
	private static class Hacker extends Thread {
		private  int start;
		private int soloWork;
		private   int length ;
		private  CountDownLatch cd ;
		private  String hash;
		private MessageDigest md = null;
		public Hacker(int start, int soloWork, int length, String hash , CountDownLatch cd) {
			this.start = start ;
			this.soloWork = soloWork;
			this.length = length;
			this.hash = hash;
			this.cd = cd ;
		}

		@Override
		public void run() {
			for(int i = start ; i<Integer.min(start+soloWork , CHARS.length) ; i++)
				rec(String.valueOf(CHARS[i]));
			cd.countDown();
		}

		private void rec(String str) {
			if(str.length()>length)return ;

			try {
				md = MessageDigest.getInstance("SHA");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			md.update(str.getBytes());
			String current = Cracker.hexToString(md.digest());
			if(current.equals(hash))
				System.out.println(str);
			for(int i = 0 ; i<CHARS.length ; i++)
				rec(str+CHARS[i]);

		}
	}
	
	/*
	 Given a byte[] array, produces a hex String,
	 such as "234a6f". with 2 chars for each byte in the array.
	 (provided code)
	*/
	public static String hexToString(byte[] bytes) {
		StringBuffer buff = new StringBuffer();
		for (int i=0; i<bytes.length; i++) {
			int val = bytes[i];
			val = val & 0xff;  // remove higher bits, sign
			if (val<16) buff.append('0'); // leading 0
			buff.append(Integer.toString(val, 16));
		}
		return buff.toString();
	}
	
	/*
	 Given a string of hex byte values such as "24a26f", creates
	 a byte[] array of those values, one byte value -128..127
	 for each 2 chars.
	 (provided code)
	*/



	public static byte[] hexToArray(String hex) {
		byte[] result = new byte[hex.length()/2];
		for (int i=0; i<hex.length(); i+=2) {
			result[i/2] = (byte) Integer.parseInt(hex.substring(i, i+2), 16);
		}
		return result;
	}



	// possible test values:
	// a 86f7e437faa5a7fce15d1ddcb9eaeaea377667b8
	// fm adeb6f2a18fe33af368d91b09587b68e3abcb9a7
	// a! 34800e15707fae815d7c90d49de44aca97e2d759
	// xyz 66b27417d37e024c46526c2f6d358a754fc552f3

}
