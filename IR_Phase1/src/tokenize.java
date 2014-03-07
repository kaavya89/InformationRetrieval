import java.io.*;
import java.lang.*;
import java.lang.management.*;
import java.nio.charset.*;
import java.util.*;

import org.apache.commons.io.*;
public class tokenize
{
	public static void main(String args[])
	{		
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		int count=0,value;
		long startTime = bean.getCurrentThreadCpuTime();
		long startTime2 = bean.getCurrentThreadUserTime();
		long endTime,endTime2;
		File f = new File(args[0]);			
		File f2;
		PrintWriter out;		
		String tokenizer[],fname,newname,token,list="";
		HashMap<String , Integer> hm = new HashMap<String,Integer>(); // To store individual file tokens
		TreeMap<String , Integer> tm = new TreeMap<String,Integer>(); // To store tokens from all files. Sorted
		try
		{			
		    //Loops through all the files in the mentioned folder. 
			for(File name:f.listFiles())
			{
				count++;					
				list= FileUtils.readFileToString(name, Charset.defaultCharset()); // Reads the entire content to a string. External Apache -jar is used. Package name FileUtils. 
				//System.out.println(list);				
				fname = name.getName();				
				newname = fname.substring(0,fname.indexOf(".")); //creates the file output name same as the input file.
				f2 = new File(args[1]+"/"+newname+".txt");
				out = new PrintWriter(new BufferedWriter(new FileWriter(f2)));	//Output stream for writing into files.			
				list = list.toLowerCase();//Converts the document to lowe case.				
				tokenizer = list.split("\\s*\\W\\s*");// The regular expression to use for splitting. 
				for(int i=0;i<tokenizer.length;i++) //Puts all the tokens for the file into hashmap
				{					
					token = tokenizer[i];
					if(!token.isEmpty())
					{
						//out.write(token);
						if(hm.containsKey(token)) //checks if the token is already there. If yes, 
							hm.put(token,(hm.get(token).intValue())+1); // increment frequency value
						else
							hm.put(token,1);//otherwise just put a default value as 1.
						//out.println();
					}
				}
				for(Map.Entry<String,Integer> keyvalue:hm.entrySet()) // This puts the tokens from the hashmap to the universal treemap. 
				{					
					token = keyvalue.getKey();
					value = keyvalue.getValue();
					out.write(token+", "+keyvalue.getValue().toString());
					out.println();
					if(tm.containsKey(token))
						tm.put(token,(tm.get(token).intValue())+value); // cumulative frequency is added.
					else
						tm.put(token,value);
				}
				out.flush();
				hm.clear();//clears the hashmap once a file is processed. The treemap is not cleared because it should contain tokens from all files.
				endTime = System.currentTimeMillis();
				endTime = bean.getCurrentThreadCpuTime();
				/********************Computes CPU,System,UserTime***************************/
				System.out.println("CPU Time for "+count+" file is "+(endTime-startTime));
				endTime2 = bean.getCurrentThreadUserTime();
				System.out.println("User Time for "+count+" file is "+(endTime2-startTime2));				
				System.out.println("System Time for "+count+" file is "+(endTime-endTime2));
			}
			//So now we have a hash map. Time to write a single file with sorted tokens.
			File tokensorted = new File(args[1]+"/sortedtoken.txt");
			PrintWriter out2 = new PrintWriter(new BufferedWriter(new FileWriter(tokensorted)));
			for(Map.Entry<String,Integer> keyvalue:tm.entrySet()) // loops through the entire word list for the whole corpus and writes to a single file.
			{
					out2.write(keyvalue.getKey());
					out2.write(",");
					out2.write(keyvalue.getValue().toString());	
					//System.out.println(keyvalue.getValue());
					out2.println();					
			}
			out2.flush();
			out2.close();
			TreeMap<String,Integer> tmfreq=sortByValues(tm);//calls a function to compare values based on the "value" attribute in treemap.
			File freqsorted = new File(args[1]+"/freqtoken.txt");
			PrintWriter out3 = new PrintWriter(new BufferedWriter(new FileWriter(freqsorted)));
			long starttime2 = System.currentTimeMillis();
			for(Map.Entry<String,Integer> keyvalue:tmfreq.entrySet())//Loops through the sorted result to write a single file.
			{
					out3.write(keyvalue.getKey());
					out3.write(",");
					out3.write(keyvalue.getValue().toString());	
					//System.out.println(keyvalue.getValue());
					out3.println();					
			}
			endTime = System.currentTimeMillis();
			System.out.println("Time to sort frequency file is "+(endTime-starttime2));			
			out3.flush();
			out3.close();
		}catch(Exception e)
		{
		System.out.println(e.getMessage());	
		}			
		
		 
	}	
	/******************Sorts the treemap values based on the frequencies******************************/
	public static <String, Integer extends Comparable<Integer>> TreeMap<String, Integer> sortByValues(final TreeMap<String, Integer> map) {
	    Comparator<String> valueComparator =  new Comparator<String>() {
	        public int compare(String i1, String i2) {
	            int compare = map.get(i2).compareTo(map.get(i1));
	            //System.out.println("Get value 1"+map.get(i2));
	            if (compare == 0) return 1;
	            else return compare;
	        }
	    };
	    TreeMap<String, Integer> sortedByValues = new TreeMap<String, Integer>(valueComparator);
	    sortedByValues.putAll(map);
	    return sortedByValues;
	}
	
}

/*tokenizer = new StringTokenizer(list," ~!@#$%^&*()_+=-`|\\\"'<>?/.,:;{}[]");
while(tokenizer.hasMoreTokens())
{	
	token = tokenizer.nextToken().replaceAll("\\s+","");
	System.out.println(token+":"+newname);
	out.write(token);
	if(hm.containsKey(token))
		hm.put(token,(hm.get(token).intValue())+1);
	else
		hm.put(token,1);
	out.println();					
}*/