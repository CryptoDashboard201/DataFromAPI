
package utilities;
import com.jaunt.*;
import com.jaunt.component.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.lang.Object;


//import com.mysql.cj.jdbc.PreparedStatement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WebScrapper {
	
	public static void main(String[] args) {  
	 	URL url;
	    HttpURLConnection conn;
	    BufferedReader br;
	    String line;
	    String result = "";
	    String [] array = new String[150];
	    double [] arrBalances = new double[150];
	    
		try{
			  UserAgent userAgent = new UserAgent();      //create new userAgent (headless browser)
			  userAgent.settings.autoSaveAsHTML = true;
			  userAgent.visit("https://bitinfocharts.com/top-100-richest-bitcoin-addresses.html");     
			  Elements links = userAgent.doc.findEvery("<td><a href=\"https://bitinfocharts.com/bitcoin/address/ >\"").findEvery("<a>");   //find search result links 
			  
			  int counter = 0;
			  for(Element address : links)  {
				counter++;
				 String s = address.innerHTML();
				 String wallet = "wallet:";
				 if (!(s.toLowerCase().contains(wallet.toLowerCase()))) {
					 array[counter] = address.innerHTML();
				 }
			  }
		    }
		catch(JauntException e){         //if an HTTP/connection error occurs, handle JauntException.
			System.err.println(e);
		}
		    try {   
		    	String s = "https://blockchain.info/balance?active=";
		    	
		    	for (int j = 0; j < 150; j++) {
		    		if (array[j] != null) {
		    			s += array[j];
			    		s+= "|";		    		
		    		}
		    		
		    	}
		    	if (array[100] != null) {
		    		s+= array[100];
		    	}
		    	
		    	url = new URL(s);
		        conn = (HttpURLConnection) url.openConnection();
		        conn.setRequestMethod("GET");
		        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		 
		        while ((line = br.readLine()) != null) {
		            result += line;
		        }
		        
		        JsonParser parser = new JsonParser();
		        JsonElement rootNode = parser.parse(result);
		        double tbalance = 0.0;
		        if (rootNode.isJsonObject()) {
		        	JsonObject address = rootNode.getAsJsonObject();
		        	int counterBalance = 0;
		        	for (int l = 0; l < 130; l++) {
		        		JsonObject addressO = address.getAsJsonObject(array[l]);
		        		if (addressO != null) {
		        			JsonElement balanceO = addressO.get("final_balance");
		        			String balanceS = balanceO.toString();
		        		    String b = balanceS.substring(0, balanceS.length()-8);
		        		    double bDouble = Double.parseDouble(b);
		        		    
		        		    if (bDouble != 0.0) {
		        		    	arrBalances[counterBalance] = bDouble;
		        		    	counterBalance++;
		        		    }
		        		   tbalance += bDouble; 	
		        		}	
		        	}
		        	calculatePercentage(arrBalances, tbalance);
		        }
		        br.close();
		 
		    } catch (Exception e) {
		       System.out.println(e.getMessage());
		    }
		    
		}
	
	public static double[] calculatePercentage(double[]array, double tBalance) {
		for (int i = 99; i >= 0; i-- ) {
			array[i] = array[i] / tBalance * 100;		
			if (i <100) {
				array[i] += array[i+1];
			}
			int c = 100 -i;
			//System.out.println("Array [ " + c + "]" + "  balance" + array[i]);
			JDBC jdbc = new JDBC();
			jdbc.saveLink(array[i], c);
			
		}
		return array;
	}

}
