import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;

class ClientScreenshot {

//	public static Socket client;
	public static String img_name;

	public static void getscreen(String ip) throws Exception {

		System.out.println("Connectin to cleint...");
		Socket client = new Socket(ip, 5566);
		System.out.println("Connection Established with Server @ IP: \"" + ip + "\"");
		ObjectInputStream in = new ObjectInputStream(client.getInputStream());

		Rectangle size = (Rectangle) in.readObject();
		int[] rgbData = new int[(int) (size.getWidth() * size.getHeight())];

		for (int x = 0; x < rgbData.length; x++) {
			rgbData[x] = in.readInt();
		}

		BufferedImage screen = new BufferedImage((int) size.getWidth(), (int) size.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		screen.setRGB(0, 0, (int) size.getWidth(), (int) size.getHeight(), rgbData, 0, (int) size.getWidth());

		
		//<%=new SimpleDateFormat("MMMM dd, YYYY").format(rs.getDate("doe")) %>
		
		System.out.println(new SimpleDateFormat("MMMM_dd_YYYY_HH_MM_SS").format(new Timestamp(new java.util.Date().getTime())));
		
		img_name = (ip+"_screen"+new SimpleDateFormat("MMMM_dd_YYYY_HH_MM_SS").format(new Timestamp(new java.util.Date().getTime())).toString().replace('-', '_')+".png").replace(':','_');

		System.out.println("Image_Name: " + img_name);
		ImageIO.write(screen, "png", new File(img_name));
		System.out.println("File Created....");

		try {
			Thread.sleep(500);
		} catch (Exception e) {
			System.out.println("Thread Isseu...\n" + e);
		}

		new DataOutputStream(client.getOutputStream()).writeBytes(img_name.trim());
		System.out.println("File Name is sent to Server: " + img_name);

		in.close();
		client.close();
	}

	public static void sendFile(String ip) {
		String fileName = img_name.trim();

		try {
			Thread.sleep(1000);
			File file = new File(fileName.trim());
			Socket client = new Socket(ip, 5567);
			System.out.println("Connected to Server for Sending File");
			
			byte[] mybytearray = new byte[(int) file.length()];
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			bis.read(mybytearray, 0, mybytearray.length);
			OutputStream os = client.getOutputStream();
			os.write(mybytearray, 0, mybytearray.length);
			
			os.flush();
			os.close();
			System.out.println("File sent to server");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Thread() {
			public void run() {
				try {
					getscreen("127.0.0.1");
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Something went wrong at Client-Side while Execution FOR SCREENSHOT");
				}
			}
		}.start();

		new Thread() {
			public void run() {
				try {
					Thread.sleep(1000);
					sendFile("127.0.0.1");
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Something went wrong at Client-Side while Execution FOR SENDING FILE");
				}
			}
		}.start();
	}
}