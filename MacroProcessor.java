import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

class ALA
{
	String mName;
	
	Map<String,Integer> ala = new LinkedHashMap<String,Integer>();
	
	ALA(String mName)
	{
		this.mName = mName;
	//	this.alaIndex=alaIndex
	}
	
}
class ALARev
{
	String mName;
	
	Map<Integer,String> ala = new LinkedHashMap<Integer,String>();
	
	ALARev(String mName)
	{
		this.mName = mName;
	//	this.alaIndex=alaIndex
	}
	
}

class MNT
{
	Integer index,MDT_index;
	String mName;
	
	MNT(Integer index, Integer MDT_index, String mName)
	{	
		this.index = index;
		this.mName = mName;
		this.MDT_index = MDT_index;
	}
}

//class MDT
//{
//	Integer MDT_index;
//	String inst;
//	
//	MDT(Integer MDT_index, String inst)
//	{
//		this.MDT_index = MDT_index;
//		this.inst = inst;
//	}
//}



public class MacroProcessor {
	
	static Map<Integer,String> mdTable = new LinkedHashMap<Integer,String>();
	static Vector<MNT> mnTable = new Vector<MNT>();
	static Vector<String> ic = new Vector<String>();
	static Map<String,ALA> alaobjs = new LinkedHashMap<String,ALA>();
	static List<String>mIns = new ArrayList<String>();
	
	public static boolean isInteger( String input )
	{
   try
   {
      Integer.parseInt( input );
      return true;
   }
   catch( Exception e)
   {
      return false;
   }
	}
	public static void expandMacro(ALARev obj)
	{
		String mname = obj.mName;
		Integer mdtind=0;
		for(int i=0;i<mnTable.size();i++)
		{
			MNT mnt = mnTable.elementAt(i);
			if(mnt.mName.equals(mname))
			{
				mdtind = mnt.MDT_index;
				break;
			}
		}
		mdtind++;
		String currentLine =mdTable.get(mdtind);
		Integer val;
		while(!currentLine.equals("MEND"))
		{
			
			Vector<String> tokens = tokenSep(currentLine);
			int i =0;
//			String str="";
			String tab="\t";
			while(i<tokens.size())
			{
				
				String token = tokens.elementAt(i);
				token = token.trim();
				//System.out.println(token);
				if(token.equals(""))
					{
						i++;
						continue;
					}
				if(token.matches("#[\\d]*"))
				{
					val = Integer.parseInt(token.substring(1));
					System.out.print(obj.ala.get(val)+tab);
				}
				else if(token.equals(",") || isInteger(token) || token.equals(")") || token.equals("("))
					System.out.print(token);
				else
				{
					tab="";
					System.out.print(token+"\t");
				}
				i++;
			}
			System.out.println("");
			mdtind++;
			currentLine=mdTable.get(mdtind);
		}
		
		
	}
	
	public static Vector<String> tokenSep(String str)
	{
		//System.out.println(str);
		//str.concat(System.getProperty("line.separator"));
		str+=' ';
		Vector<String> tokens = new Vector<String>();
		Character ch;
		int i=0;
		ch=str.charAt(i);
		String str1="";
		while(i<str.length())
		{
			ch=str.charAt(i);
			//System.out.print("-"+ch+"-");
			if(ch ==','||ch==' '||ch =='\t' || ch =='\n' || ch=='(' || ch==')')
			{
				tokens.add(str1);
				if(ch==',' || ch=='(' || ch==')')
					tokens.add(ch.toString());
				
				str1="";
			}
			else
			{
				//System.out.println("IN else");
//				if(ch=='&')
//					
//					str1=str1.concat("&");
//				else
				//System.out.println("Character  "+ch);
				//System.out.println("String before concatenation "+str1);
				str1 =  str1 + ch.toString();
				//System.out.println("String  after concatenation "+str1);
			}
			i++;
			//System.out.println("");
		}
		return tokens;
	}
	
	public static void main(String args[])throws IOException
	{
		BufferedReader conio = new BufferedReader(new InputStreamReader(System.in));
		String fileName="";
		System.out.println("Enter the filename");
		fileName=conio.readLine();
		String currentLine;
		int mdtind=1;
		int mntind=1;
		//int alaCount=1;
		boolean mflag = false;
		boolean fline = false;
		
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		
		String mname ="M";
		System.out.println("Intermediate Code AFter Pass1:");
		while((currentLine = br.readLine()) != null )
		{
			currentLine = currentLine.trim();
			if(currentLine.equals(""))
				continue;
			else if(currentLine.equals("MACRO"))
			{
				mflag=true;
				fline=true;
				mname="M";
				alaobjs.put("M",new ALA("M"));
				continue;				
			}
			else if(mflag == true)
			{
				if(fline)
				{
					mdTable.put(mdtind,currentLine);
					String tokens[] = currentLine.split(" |,|\\t|\\n");
					int i =0;
					int aind=1;
					boolean mNameFlag = false;
					while(i<tokens.length)
					{
						String str=tokens[i];
						str=str.trim();
						if(str.equals(""))
							{i++;continue;}
						if(str.charAt(0)=='&')
						{
							if(mNameFlag==false)
							{
								mNameFlag=true;
								alaobjs.get(mname).ala.put(str,0);
							}
							else
							{
								alaobjs.get(mname).ala.put(str,aind);
								aind++;
							}
						}
						else
						{
							MNT mnt = new MNT(mntind,mdtind,str);
							mdtind++;
							mnTable.add(mnt);
							mntind++;
							alaobjs.put(str,alaobjs.get(mname));
							mname = str;
							mNameFlag=true;
						}
						i++;	
					}
					fline=false;
				}
				else
				{
					if(currentLine.equals("MEND"))
					{
						mdTable.put(mdtind,currentLine);
						mdtind++;
						mflag=false;
						continue;
					}
					Vector<String> tokens = tokenSep(currentLine);
					int i =0;
					String str="";
					String tab="\t";
					while(i<tokens.size())
					{
						
						String token = tokens.elementAt(i);
						token = token.trim();
						//System.out.println(token);
						if(token.equals(""))
							{
								i++;
								continue;
							}
						if(token.charAt(0)=='&')
						{
						//	System.out.println(token);
							str = str.concat("#"+alaobjs.get(mname).ala.get(token).toString()+tab);
						}
						else if(token.equals(",") ||isInteger(token) ||token.equals("(") || token.equals(")"))
							str=str.concat(token);
						else
						{
							tab="";
							str=str.concat(token+"\t");
						}
						i++;
					}
					mdTable.put(mdtind,str);
					str="";
					mdtind++;
				}
			}
			else
			{
				System.out.println(currentLine);
				ic.add(currentLine);
				
			}
		}
		
		System.out.println("Data Structures generated in Pass 1\n");
			System.out.println("MNT TABLE\n");
			System.out.println("Index\tMacro Name\tMDT Index");
			for(int i=0;i<mnTable.size();i++)
			{
				MNT mnt = mnTable.elementAt(i);
				System.out.println(mnt.index+"\t"+mnt.mName+"\t\t"+mnt.MDT_index);
				mIns.add(mnt.mName);
			}
			System.out.println("\nMDT Table\n");
			System.out.println("Index\tInstruction");
			for (Map.Entry<Integer, String> entry : mdTable.entrySet())
			{
				System.out.println(entry.getKey()+"\t"+entry.getValue());
			}
			System.out.println("\nArguement List Arrays");
			alaobjs.remove("M");
			
			for (Map.Entry<String, ALA> entry : alaobjs.entrySet())
			{
				System.out.println("\nALA for "+entry.getKey()+"\n");
				System.out.println("Index\tArguement");
				Map<String,Integer> x = alaobjs.get(entry.getKey()).ala;
				for (Map.Entry<String, Integer> entry1 : x.entrySet())
				{
					System.out.println(entry1.getValue()+"\t"+entry1.getKey());
				}
				
			}
			
			System.out.println("\nOutput after Pass 2:\n");
			//Pass 2:
			boolean mExpFlag=false;
			for(int i=0;i<ic.size();i++)
			{
				mExpFlag=false;
				String prev_card="";
				//int count_from=1;
				int arg_ind=0;
				mname="";
				ALARev obj = new ALARev(mname);
				
				currentLine = ic.elementAt(i);
				currentLine=currentLine.trim();
				if(currentLine.equals(""))
					continue;
				Vector<String> tokens = tokenSep(currentLine);
				int j =0;
				while(j<tokens.size())
				{
					String token = tokens.elementAt(j);
					token = token.trim();
					if(token.equals("")||token.equals(",") ||token.equals("(") || token.equals(")"))
						{j++;continue;}
					if(mIns.contains(token))
					{
						mExpFlag=true;
//						if(prev_card !="")
//							{
//								count_from = 0;
//							}
						obj.mName=token;
						mname=token;
						arg_ind=1;
					}
					else if(token.matches("&[[A-Z]|\\d]*=[[A-Z]|\\d]*")) 
					{
						Integer val=0;
						//System.out.println("Here token ="+token);
						String arr[]=token.split("=");
						int k = 0;
						String arg="";
						
						while(k<arr.length)
						{
							
							arg=arr[k].trim();
							if(arg.equals(""))
							{k++;continue;}
							if(arg.charAt(0)=='&')
							{
								//System.out.println("/*"+arg);
								//System.out.println(mname);
								 val=alaobjs.get(mname).ala.get(arg);
								 //System.out.println(val+"*/");
							}
							else
							{
								//System.out.println(arg);
								//System.out.println(val);
								obj.ala.put(val,arg);
							}
							k++;
						}
					}
					else if(token.matches("[[A-Z]|\\d]*"))
					{
						//System.out.println(token);
						obj.ala.put(arg_ind,token);
						arg_ind++;
					}
					prev_card=token;
					j++;
				}
				if(mExpFlag)
				{
					expandMacro(obj);
				}
				else
				{
					System.out.println(currentLine);
				}
				
			  }
	}
}
	