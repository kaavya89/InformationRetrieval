import java.io.*;
import java.lang.management.*;
import java.text.DecimalFormat;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class calwt
{
	public static int N=0;	   		
	public static HashMap<String , Integer> tdm = new HashMap<String,Integer>(); // To store individual file tokens
	public static HashMap<String , Double> wts = new HashMap<String,Double>(); // To store individual file tokens
	
	public static void main(String args[])
	{
		HashMap<Integer,HashMap<String,Integer>> doclists = new HashMap<Integer,HashMap<String,Integer>>();		
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long startTime = bean.getCurrentThreadCpuTime();
		long startTime2 = bean.getCurrentThreadUserTime();
		long endTime,endTime2;
		Document d;
		ArrayList<String> stopwords = new ArrayList<String>();
		int count=0;
		String tokenizer[],token,list="";
		HashMap<String , Integer> hm = new HashMap<String,Integer>(); // To store individual file tokens		
		HashMap<String,Integer> vocab = new HashMap<String,Integer>();		
		try
		{
			File f = new File(args[0]);
			File f3 = new File(args[2]);
			N=f.list().length;
			Scanner sc = new Scanner(new FileInputStream(f3));
			while(sc.hasNext())
			{
				stopwords.add(sc.next());
			}
			sc.close();
		    //Loops through all the files in the mentioned folder. 
			for(File name:f.listFiles())
			{
				hm = new HashMap<String,Integer>();
				d = Jsoup.parse(name,"UTF-8");
				count=count+1;
				list=d.text();
				//Output stream for writing into files.			
				list = list.toLowerCase();//Converts the document to lowe case.				
				tokenizer = list.split("\\s*\\W\\s*");// The regular expression to use for splitting.				
				for(int i=0;i<tokenizer.length;i++) //Puts all the tokens for the file into hashmap
				{					
					token = tokenizer[i];
					if(!token.isEmpty())
					{						
						if(!stopwords.contains(token)&&token.length()>1)
						{								
							if(hm.containsKey(token)) //checks if the token is already there. If yes,
							{
								hm.put(token,(hm.get(token).intValue())+1); // increment frequency value								
							}
							else
							{
								hm.put(token,1);//otherwise just put a default value as 1.								
							}
							if(vocab.containsKey(token)) //checks if the token is already there. If yes,
							{
								// increment frequency value
								vocab.put(token,(vocab.get(token).intValue())+1);
							}
							else
							{
								//otherwise just put a default value as 1.
								vocab.put(token,1);
							}
							if(count==1)
								tdm.put(token,1);
							//otherwise just put a default value as 1.							
						}					
					}
				}
				doclists.put(count, hm);
				if(count!=1)
					func(hm);				
			}			
			vocab=removeSingleWords(vocab);						
			calwts(args[1],vocab,doclists);
			endTime = bean.getCurrentThreadCpuTime();
			/********************Computes CPU,System,UserTime***************************/ //COmputes time for the entire parse
			System.out.println("CPU Time for "+N+" file is "+(endTime-startTime));
			endTime2 = bean.getCurrentThreadUserTime();
			System.out.println("User Time for "+N+" file is "+(endTime2-startTime2));				
			System.out.println("System Time for "+N+" file is "+(endTime-endTime2));
		}catch(Exception e)
		{
			System.out.println("Either you are not entering the folder names correct or you are missing a stopwords.txt");
			System.out.println("An example would be java IP OUT stopwords.txt");
			e.printStackTrace();	
		}		 
	}
	
	public static void func(HashMap<String,Integer> hm) // For storing the mapping between word and the number of document it occurs
	{		
		for(Map.Entry<String,Integer> val:hm.entrySet())
		{
			if(tdm.containsKey(val.getKey()))
				tdm.put(val.getKey(), tdm.get(val.getKey())+1);
			else
				tdm.put(val.getKey(), 1);
		}
	}
	
	public static void calwts(String ar,HashMap<String,Integer> vocab,HashMap<Integer,HashMap<String,Integer>> doclists)
	{
		DecimalFormat d = new DecimalFormat("##.####");
		File f;
		PrintWriter out;
		double wt;
		double val,max=0,sq=0.0;
		HashMap<String,Integer> hm=new HashMap<String, Integer>();
		HashMap<String,Double> norm=new HashMap<String, Double>();
		for(int i=1;i<=N;i++) //Loops through the entire document collection
		{
			//System.out.println(i);
			hm =new HashMap<String, Integer>();
			norm=new HashMap<String, Double>();
			try{
			f = new File(ar+"/calwts"+i+".txt");
			out = new PrintWriter(f);
			wt=0.0;//weight 
			sq=0.0;//Normalized weight
			//out.print(doclists.get(i));
			hm.putAll(doclists.get(i));
			for(Map.Entry<String,Integer> pair:hm.entrySet())//Calculates weights for each term  
			{		
				if(vocab.containsKey(pair.getKey()))
				{
					val = tdm.get(pair.getKey()).doubleValue();
					wt = 1+(Math.log(pair.getValue().doubleValue())/Math.log(2));					
					wt = wt*(Math.log(N/val)/Math.log(2));
					sq += wt*wt;//this is where the square of each value is taken					
					norm.put(pair.getKey(),wt);//puts the un-normalized weights computed in a map.
				}else
				{
					norm.put(pair.getKey(),0.00);
				}
			}
			for(Map.Entry<String,Double> pair:norm.entrySet())  
			{		
				if(pair.getValue()>0.0)
				{
					wt = pair.getValue()/Math.sqrt(sq); // normalizes each weight before writing
					out.print(pair.getKey()+",");//writes result into a file.
					out.print(d.format(wt));
					out.println();
				}
				else{
				out.print(pair.getKey()+",0.00");	
				out.println();}
			}
			out.flush();
			}catch(Exception e){System.out.println(e);}
		}		
	}

	
public static HashMap<String,Integer> removeSingleWords(HashMap<String,Integer> vocab)//Function to remove words from the vocab that occurs only once in the entire corpus
	{
		HashMap<String,Integer> v1 = new HashMap<String,Integer>();
		v1.putAll(vocab);
		try{
		for(Map.Entry<String,Integer> pair:v1.entrySet())  
		{								 
			if(pair.getValue()==1)
			{				
				if(vocab.containsKey(pair.getKey()))	//removes the token that occurs only one in the entire document. 
					vocab.remove(pair.getKey());				
			}
		}		
		}catch(Exception e){e.printStackTrace();}		
		return vocab;
	}
}