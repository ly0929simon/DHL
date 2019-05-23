//IO Classes
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * This class contains is a sample client class used to send request XML
 * messages to XML Shipping service of DHL
 * 
 *   @author Dhawal Jogi (Infosys)
 **/
public class  DHLClient{        
	
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(DHLClient.class);
	
	public static void main(String[] args) {

		try {
			String localIpAddress = Inet4Address.getLocalHost().getHostAddress();
			MDC.put("IP", localIpAddress);
		} catch (Exception e) {
			MDC.put("IP", "IP Unavailable");
		}
		MDC.put("Delimiter", "|");

		logger.info("START DHLClient");
		StopWatch sw = new StopWatch();
		sw.start();
		//long requestTime = new Date().getTime();
//		logger.info("Request received at :"+requestTime);
//		java.security.Security

		if(args.length == 4){
			DHLClient dhlClient = new DHLClient(args[0],args[1],args[2],args[3]);
		} else {
			System.out.println("Usage : java DHLClient Request XML MessagePath  httpURL ResponseXMLMessage Path FutureDate(true or false)\n");
			System.out.println(" where \n");
			System.out.println("Request XML MessagePath : The complete path of the request XML message to be send. E.g. C:\\RequestXML\\ShipmentValidateRequest.xml \n");
			System.out.println("httpURL : The complete url of the server. E.g. http://IP ADDRESS:PORT NUMBER//SERVLET PATH \n");
			System.out.println("ResponseXMLMessage : The complete directory path where the respose XML messages are to be saved. E.g. C:\\ResponseXML\\\n");
			System.exit(9);			
		}
		sw.stop();
		//long responseTime = new Date().getTime();
		System.out.println("\n Total time taken to process request and respond back to client" + " | " + sw.getTime());
		logger.info("Total time taken to process request and respond back to client" + " | " + sw.getTime());
		//logger.info("Total time taken to process request and respond back to client" + " | "
				//+ (responseTime - requestTime) + "ms");
		logger.info("END DHLClient");
	}  //end of main method

	/**
	 * Private method to write the response from the input stream to a file in local directory.
	 * @param     strResponse  The string format of the response XML message
	 **/
	private static void fileWriter(String strResponse , String responseMessagePath, boolean isUTF8Support) {

		DateFormat today = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_SSS");

		//String path = responseMessagePath;
		//String responseFileName = "Dhawal.xml";
		String responseFileName = checkForRootElement(strResponse, isUTF8Support)+"_"+today.format(new java.util.Date());

		String ufn = responseMessagePath + responseFileName;
		File resFile = new File(ufn+".xml");
		         
		int i=0;
		try {
			//create file and if it already exits
			//if file exist add counter to it
			while(!resFile.createNewFile()){
				resFile = new File(ufn + "_" + (++i) +".xml");
			}
						
			Writer out = null;
			if(isUTF8Support) {
				out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resFile), "UTF8"));
			} else {
				out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resFile)));
			}
			
    		try {
    		    out.write(strResponse);
    		} finally {
    		    out.close();
    		}
												
			logger.info("Response received and saved successfully at :" + responseMessagePath +"");
			logger.info("The file name is :" + resFile.getName());
			System.out.println("\n Response received and saved successfully at :" + responseMessagePath +"\n");
			System.out.println("The file name is :" + resFile.getName());
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
	}// end of  fileWriter method

	/**
	 * Returns the value of the root element of the response XML message send by DHL Server
	 * @param     strResponse  The string format of the response XML message
	 * @return      name of the root element of type string
	 **/
	private static String checkForRootElement(String strResponse, boolean isUTF8Support) {
		Element element = null;
		try
		{
	    	byte [] byteArray = null;
	    	if(isUTF8Support) {
	    		byteArray = strResponse.trim().getBytes("UTF-8");
	    	} else {
	    		byteArray = strResponse.trim().getBytes();
	    	}
		    	
			ByteArrayInputStream baip = new ByteArrayInputStream( byteArray);
			DocumentBuilderFactory factory       = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = factory.newDocumentBuilder();
			Document doc = documentBuilder.parse(baip); //Parsing the inputstream
			element = doc.getDocumentElement(); //getting the root element           

		} catch (Exception e) {
			System.out.println("Exception in checkForRootElement "+e.getMessage());
			logger.info("Exception in checkForRootElement "+e.getMessage());
		}
		String rootElement = element.getTagName();
		// Check if root element has res: as prefix

		if (rootElement.startsWith("res:") || rootElement.startsWith("req:") || rootElement.startsWith("err:")
				|| rootElement.startsWith("edlres:") || rootElement.startsWith("ilres:")) {

			int index = rootElement.indexOf(":");

			rootElement = rootElement.substring(index+1);
		}
		return rootElement; // returning the value of the root element
	} //end of checkForRootElement method

	/*
				       This constructor is used to do the following important operations
				        1) Read a request XML
				        2) Connect to Server
				        3) Send the request XML
				        4) Receive response XML message
				        5) Calls a private method to write the response XML message

				        @param requestMessagePath  The path of the request XML message to be send to server
				        @param httpURL The http URL to connect ot the server (e.g. http://<ip address>:<port>/application name/Servlet name)
				        @param responseMessagePath The path where the response XML message is to be stored
	 */
	public DHLClient(String requestMessagePath, String httpURL, String responseMessagePath, String futureDate) {

		try{
			/** XML PI 5.0 - To support 3DNS Fail Over/Fall Back scenarios  - START */
			processDHLClient(requestMessagePath, httpURL, responseMessagePath,futureDate);
			/** XML PI 5.0 - To support 3DNS Fail Over/Fall Back scenarios  - START */
		}
		catch(MalformedURLException mfURLex){
			System.out.println("MalformedURLException "+mfURLex.getMessage());
			logger.info("MalformedURLException "+mfURLex.getMessage());
			mfURLex.printStackTrace();
		}
		catch(IOException e){
			System.out.println("IOException "+e.getMessage() + "\n");
			logger.info("IOException "+e.getMessage() + "");
//			e.printStackTrace();
			/** XML PI 5.0 - To support 3DNS Fail Over/Fall Back scenarios - START */
			long timeToSleep = 60000;
			try {
				System.out.println("================= Please Wait for "   +timeToSleep/1000+" seconds; Retry in progress ...... ================= \n");
				logger.info("================= Please Wait for "   +timeToSleep/1000+" seconds; Retry in progress ...... =================");
				// Flush DNS Cache in user machine
				flushDNS();
				//	logger.info("Suspending current thread for 60 seconds to hanlde fail over/fall back scenarios ...... ");
				Thread.sleep(timeToSleep);
			} catch (InterruptedException e1) {
				System.out.println("Exception while suspending execution of current thread for"+ timeToSleep/1000 +" seconds :"+e1);
				logger.info("Exception while suspending execution of current thread for"+ timeToSleep/1000 +" seconds :"+e1);
				e1.printStackTrace();
			}
			for (int i=1; i<=3; i ++){
				System.out.println("RETRY =========> "+i);
				logger.info("RETRY =========> "+i);
				try{
					processDHLClient(requestMessagePath, httpURL, responseMessagePath,futureDate);
					break;
				}
				catch(MalformedURLException mfURLex){
					System.out.println("MalformedURLException "+mfURLex.getMessage());
					logger.info("MalformedURLException "+mfURLex.getMessage());
					continue;
				}
				catch(IOException ioe){
					logger.info("IOException "+ioe.getMessage() +"");
					if (i==3){
						System.out.println("=================    Three (3) retries are done - please contact DHL Support Team       ======================\n");
						logger.info("=================    Three (3) retries are done - please contact DHL Support Team       ======================");
					}
					continue;
				}
			}
			/** XML PI 5.0 - To support 3DNS Fail Over/Fall Back scenarios  - COMPLETED */
		}


	}

	public void processDHLClient (String requestMessagePath, String httpURL, String responseMessagePath, String futureDate) throws IOException {
		//		java.security.Security.setProperty( "networkaddress.cache.ttl","0");
		//		java.security.Security.setProperty( "networkaddress.cache.negative.ttl","0");
		//		java.security.Security.setProperty( "sun.net.inetaddr.ttl","0");
		//		java.security.Security.setProperty( "sun.net.inetaddr.negative.ttl","0");

		//		System.setProperty("networkaddress.cache.ttl","0");

		//		logger.info("TTL Value :"+java.security.Security.getProperty( "networkaddress.cache.ttl"));
		//		java.security.Security.setProperty( "networkaddress.cache.ttl","0");
		//		logger.info("TTL Value :"+java.security.Security.getProperty( "networkaddress.cache.ttl"));
		logger.info("futureDate set to :: "+futureDate);
		System.out.println("futureDate set to :: "+futureDate);
		System.out.println("OOpening the connection ..... :"+httpURL);

		//Preparing file inputstream from a file    
		String clientRequestXML = null;
		String responseXML = null;
		String messageReference = null;
		String siteId = null;
		String reqPassword = null;
		InputStream fis = null;
		try{
			fis =  new FileInputStream(requestMessagePath);
		}catch(Exception e){
			fis = null;
			e.printStackTrace();
			return;
		}

		InputStreamReader	reader = new 	InputStreamReader(fis, "UTF-8")  ;

		int ilength = fis.available();

		char[] c = new char[ilength];

		int i = reader.read(c);
		clientRequestXML = new String(c).trim();

		// Begin :: Kosani Banerjee | Printing Message Reference in Logs

		if (clientRequestXML != null) {
			int startTagMessageRef = clientRequestXML.indexOf("MessageReference");
			int startMsgRfNo = 0;
			int endMsgRfNo = 0;
			if (startTagMessageRef != -1) {
				startMsgRfNo = clientRequestXML.indexOf(">", startTagMessageRef);
				endMsgRfNo = clientRequestXML.indexOf("<", startMsgRfNo);
				messageReference = clientRequestXML.substring(startMsgRfNo + 1, endMsgRfNo);
			}
		}
		if(messageReference != null) {
		MDC.put("messageReference", messageReference);
		}
		// End :: Kosani Banerjee | Printing Message Reference in Logs
		
		boolean isUTF8Support = utfEnable(clientRequestXML); 
		    			
		if(!isUTF8Support) {
			reader.close();
			fis.close();
			InputStream utfFis = new FileInputStream(requestMessagePath);
			InputStreamReader utfReader = new 	InputStreamReader(utfFis);
			ilength = utfFis.available();
			c = new char[ilength];
			i = utfReader.read(c);
			clientRequestXML = new String(c);
			utfReader.close();
			utfFis.close();
		} 

		/* Preparing the URL and opening connection to the server*/
		URL servletURL = null;
		if(isUTF8Support) {
			String query = "isUTF8Support=true";
			servletURL = new URL(httpURL + "?" + query);
		} else {
			servletURL = new URL(httpURL);
		}

		/** XML PI 5.0 - To support 3DNS Fail Over/Fall Back scenarios  - START 
		InetAddress giriAddress[] = java.net.InetAddress.getAllByName(servletURL.getHost());
		for (int j=0; j<giriAddress.length; j++){
			String address = giriAddress[j].getHostAddress();
			logger.info("Resolved Ip Address :" + address);
		}
		/** XML PI 5.0 - To support 3DNS Fail Over/Fall Back scenarios  - COMPLETED */

		HttpURLConnection servletConnection = null;
		servletConnection = (HttpURLConnection)servletURL.openConnection();
		servletConnection.setDoOutput(true);  // to allow us to write to the URL
		servletConnection.setDoInput(true);
		servletConnection.setUseCaches(false); 
		servletConnection.setRequestMethod("POST");

		servletConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");	
		if(isUTF8Support) {
			servletConnection.setRequestProperty("Accept-Charset", "UTF-8");
		} 
		String len = Integer.toString(clientRequestXML.getBytes().length);
		servletConnection.setRequestProperty("Content-Length", len);
		servletConnection.setRequestProperty("futureDate",futureDate);

		/*Code for sending data to the server*/
		/*DataOutputStream dataOutputStream;
		                dataOutputStream = new DataOutputStream(servletConnection.getOutputStream());

		                ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();*/
		//servletConnection.setReadTimeout(10000);
		StopWatch swServerConn = new StopWatch();
		swServerConn.start();
		InetAddress address = InetAddress.getByName(new URL(httpURL).getHost());
		logger.info("Connecting to Server IP: "+address.getHostAddress()+" URL: "+httpURL);
		servletConnection.connect();
		swServerConn.stop();
		logger.info("Connected to IP: "+address.getHostAddress()+" URL: "+httpURL + " | " + swServerConn.getTime());
		StopWatch swXmlAppSend = new StopWatch();
		logger.info("Begin sending request to XML Appl");
		swXmlAppSend.start();
		OutputStreamWriter wr = null;
		if(isUTF8Support) {
			wr = new OutputStreamWriter(servletConnection.getOutputStream(), "UTF8");
		} else {
			wr = new OutputStreamWriter(servletConnection.getOutputStream());
		}
		wr.write(clientRequestXML);
		wr.flush();
		wr.close();
		swXmlAppSend.stop();
		logger.info("Finish sending request to XML Appl" + " | " + swXmlAppSend.getTime());

		/*Code for getting and processing response from DHL's servlet*/
		InputStreamReader isr = null;
		StopWatch swXmlAppRec = new StopWatch();
		swXmlAppRec.start();
		logger.info("Begin receiving reply from XML Appl");
		if(isUTF8Support) {
			isr = new InputStreamReader(servletConnection.getInputStream(),"UTF8");
		} else {
			isr = new InputStreamReader(servletConnection.getInputStream());
		}
		swXmlAppRec.stop();
		logger.info("Finished receiving reply from XML Appl" + " | " + swXmlAppRec.getTime());
		BufferedReader rd = new BufferedReader(isr);
		StringBuilder result = new StringBuilder();
		String line = "";

		while ((line = rd.readLine()) != null) {
			result.append(line).append("\n");
		}

		// System.out.println(response.toString());

		//Calling filewriter to write the response to a file
		fileWriter(result.toString(), responseMessagePath, isUTF8Support);

	}
	
	private boolean utfEnable(String clientRequestXML) {
		boolean isUTF8Support = false;
		List<String> utf8EnableList = new ArrayList<String>();
		utf8EnableList.add("ShipmentValidateRequest");
		utf8EnableList.add("ShipmentValidateRequestAP");
		utf8EnableList.add("ShipmentValidateRequestEA");
		utf8EnableList.add("ShipmentRequest");
		utf8EnableList.add("KnownTrackingRequest");
												
		//BEGIN :: Added below to support UTF-8 encoding for Routing, Pickup and capability and Quote service for XMLPI //Cyrillic Enhancement :: Jayachandra Pallamparthi :: 08-DEC-2014 | XMLPI Cyrillic Enhancement | XML_PI_v52_Cyrillic 
		utf8EnableList.add("BookPickupRequest");
		utf8EnableList.add("BookPickupRequestAP");
		utf8EnableList.add("BookPickupRequestEA");
		utf8EnableList.add("BookPURequest");
		
		utf8EnableList.add("ModifyPickupRequest");
		utf8EnableList.add("ModifyPickupRequestAP");
		utf8EnableList.add("ModifyPURequest");
		
		utf8EnableList.add("CancelPickupRequest");
		utf8EnableList.add("CancelPickupRequestAP");
		utf8EnableList.add("CancelPickupRequestEA");
		utf8EnableList.add("CancelPURequest");
		
		utf8EnableList.add("RoutingRequest");
		utf8EnableList.add("RoutingRequestAP");
		utf8EnableList.add("RoutingRequestEA");
		utf8EnableList.add("RouteRequest");		
		
		utf8EnableList.add("DCTRequest");
		//END :: Jayachandra Pallamparthi :: 08-DEC-2014 | XMLPI Cyrillic Enhancement | XML_PI_v52_Cyrillic
		
		String rootElement = getRootElement(clientRequestXML);

		if(utf8EnableList.contains(rootElement)) {
			isUTF8Support = true;
		}
		return isUTF8Support;
	}
		
	private String getRootElement( String message) {

		try	{
			String rootElement = null;
			StringTokenizer st = new StringTokenizer(message.trim(),"<>" ,true);
			String value = null;
			int index  = 0;
			while (st.hasMoreTokens())	{
				value = st.nextToken();

				if ( value.equals("<") ){
					rootElement = st.nextToken();

					if (!rootElement.startsWith("?") && !rootElement.startsWith("!")) {
						index = rootElement.indexOf(" ");
						if (index != -1) {
							rootElement = rootElement.substring(0, index);
						}
						index = rootElement.indexOf(":");
						if (index != -1){			
							rootElement = rootElement.substring(index + 1);
						}
						return rootElement;
					}
				}
			}
		}
		catch (Exception e)	{
			e.printStackTrace();
		}
		return "fail";
	}
	
	
	private static void flushDNS (){
		String OSName = getOSName();
		if ("MAC".equals(OSName)){
			String command = "dscacheutil -flushcache";
			String macOSxResponseText = runCommand(command);
			System.out.println("MAC OS ->  " + command+ "  -> "  + macOSxResponseText   + "\n");
			logger.info("MAC OS ->  " + command+ "  -> "  + macOSxResponseText   + "");

			command = "lookupd -flushcache";
			String macOSxLResponseText = runCommand(command);
			System.out.println("MAC OS ->  " + command+ "  -> "  + macOSxLResponseText   + "\n");
			logger.info("MAC OS ->  " + command+ "  -> "  + macOSxLResponseText   + "");

		}else if("WINDOWS".equalsIgnoreCase(OSName)){
			String command = "ipconfig /flushdns";
			String windowsResponseText = runCommand(command);
			System.out.println("WINDOWS OS ->  " + command+ "  -> "  + windowsResponseText + "\n");
			logger.info("WINDOWS OS ->  " + command+ "  -> "  + windowsResponseText + "");

		}else if ("UX".equalsIgnoreCase(OSName)){
			String command = "nscd -I hosts";
			String linuxResponseText = runCommand(command);
			System.out.println("UNIX/LINUX OS  ->  " + command+ "  -> "  + linuxResponseText   + "\n");
			logger.info("UNIX/LINUX OS  ->  " + command+ "  -> "  + linuxResponseText   + "");

			command = "dnsmasq restart";
			linuxResponseText = runCommand(command);
			System.out.println("UNIX/LINUX OS  ->  " + command+ "  -> "  + linuxResponseText   + "\n");
			logger.info("UNIX/LINUX OS  ->  " + command+ "  -> "  + linuxResponseText   + "");

			command="rndc restart";
			linuxResponseText = runCommand(command);
			System.out.println("UNIX/LINUX OS  ->  " + command+ "  -> "  + linuxResponseText   + "\n");
			logger.info("UNIX/LINUX OS  ->  " + command+ "  -> "  + linuxResponseText   + "");
		}
	}
	
	
	private static String runCommand(String command){
		try{
			Process process = Runtime.getRuntime().exec(command);
			process.waitFor();
			InputStream inputStream = process.getInputStream();
			InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8")  ;
			int ilength = inputStream.available();
			char[] c = new char[ilength];
			int i = reader.read(c);
			String responseText = new String(c);
			return responseText;
		}catch(Exception e){
			return "FLUSHDNS cannot be completed";
		}
	}
	
	
	private static String getOSName (){
		  String OS = System.getProperty("os.name", "generic").toLowerCase();
//		  System.out.println(OS);
		  if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
			  return "MAC";
		  } else if (OS.indexOf("win") >= 0) {
			 return "WINDOWS";
		  } else if (OS.indexOf("ux") >= 0) {
			 return "UX";
		  } else {
			 return "OTHERS";
		  }
	}

}					// End of Class DHLClient