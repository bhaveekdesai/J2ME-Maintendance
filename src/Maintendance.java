import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import javax.microedition.rms.*;
import java.io.*;

public class Maintendance extends MIDlet implements CommandListener
{
	int nos=0; //number of subjects
	int enos=0; //extended number of subjects
	int i1=0; //iterator
	int attArr[], att_outtaArr[];
	String subs[],eSubs[];
	Display d;
	public RecordStore rs=null;
	public RecordEnumeration re=null;
	Form Fmenu, Fnew1, Fnew2, Finput, Fanal;
	Command CmenuExit, CmenuNew, CmenuInput, CmenuAnal, CnewBack1, CnewNext1, CnewNext2, CnewFinish, CinputBack, CinputFinish, CanalExit;
	TextField newRegT1, newRegT2, newRegT2F1, newRegT2F2;
	ChoiceGroup newRegCL, signC[];
	Alert a;
	public Maintendance()
	{
		// MENU FORM
		Fmenu=new Form("WELCOME");
		Fmenu.append("MAINTENDANCE!!\nAn easy way to track your attendance and to know whether you will be crowned defaulter or not!");
		
		CmenuExit=new Command("GET OUT", Command.EXIT, 0);
		CmenuNew=new Command("New Register", Command.SCREEN, 0);
		CmenuInput=new Command("Sign My Attendance", Command.SCREEN, 0);
		CmenuAnal=new Command("Analyze my shit", Command.SCREEN, 0);

		Fmenu.addCommand(CmenuExit);		
		Fmenu.addCommand(CmenuNew);		
		Fmenu.addCommand(CmenuInput);		
		Fmenu.addCommand(CmenuAnal);		
		Fmenu.setCommandListener(this);
		
		// NEW REGISTER FORM
		Fnew1=new Form("NEW REGISTER");
		
		CnewBack1=new Command("Back", Command.BACK, 0);
		CnewNext1=new Command("Next", Command.SCREEN, 0);
		
		Fnew1.addCommand(CnewBack1);		
		Fnew1.addCommand(CnewNext1);		
		Fnew1.setCommandListener(this);
		
	    	// NEW REGISTER FORM 2
		Fnew2=new Form("NEW REGISTER");
		
		CnewNext2=new Command("Next", Command.SCREEN, 0);
		CnewFinish=new Command("Done", Command.SCREEN, 0);
			
		Fnew2.addCommand(CnewNext2);		
		//Fnew2.addCommand(CnewFinish);		
		Fnew2.setCommandListener(this);
		
		// INPUT FORM
		Finput=new Form("SIGN MY ATTENDANCE");
		
		CinputBack=new Command("Back", Command.BACK,0);
		CinputFinish=new Command("SIGN IT!", Command.SCREEN, 0);

		Finput.addCommand(CinputBack);		
		Finput.addCommand(CinputFinish);		
		Finput.setCommandListener(this);
		
		// ANALYZE FORM
		Fanal=new Form("ANALYZE MY SHIT");
		
		CanalExit=new Command("Done", Command.EXIT, 0);

		Fanal.addCommand(CanalExit);		
		Fanal.setCommandListener(this);
		
		
	}
	
	public void startApp()
	{
		d=Display.getDisplay(this);
		d.setCurrent(Fmenu);
		openRec();
	}
	
	public void commandAction(Command c, Displayable d1)
	{
		
		// MENU COMMANDS
		if (c==CmenuExit)
		{
			closeRec();
			destroyApp(true);
			notifyDestroyed();
		}
		
		if (c==CmenuNew)
			newReg();
			
		if (c==CmenuInput)
			mySign();
			
		if (c==CmenuAnal)
			shitAnalyze();
			
		if (c==CnewBack1 || c==CinputBack || c==CanalExit)
			d.setCurrent(Fmenu);
		
		// NEW REGISTER COMMANDS
		if (c==CnewNext1)
		{
			nos=Integer.parseInt(newRegT1.getString());
			nos=nos*4;
			i1=0;
			subs=new String[nos];
		
			newReg2();
		}
		if (c==CnewNext2)
		{
			recordSubs();
			newReg2();
		}	
		// FINISH COMMANDS
		if (c==CnewFinish)
		{
			recordSubs();
			createDb();
		}
		
		if (c==CinputFinish)
		{
			signAtt();
		}
		
		
	}
	
	void openRec()
	{
		try
		{
			rs = RecordStore.openRecordStore("register",true);
		}
		catch (Exception e)
		{
			a = new Alert("Error creating", e.toString(), null, AlertType.WARNING);
			a.setTimeout(Alert.FOREVER);
			d.setCurrent(a);
		}

	}
	
	void delRec()
	{
		try
		{
			RecordStore.deleteRecordStore("register");
		}
	
		catch (Exception e)
		{
			a = new Alert("Error Removing old register",e.toString(), null, AlertType.WARNING);
			a.setTimeout(Alert.FOREVER);
			d.setCurrent(a);
		}
	}
	
	void closeRec()
	{
		try
		{
			rs.closeRecordStore();
		}
	
		catch (Exception e)
		{
			a = new Alert("Error Closing",
			e.toString(), null, AlertType.WARNING);
			a.setTimeout(Alert.FOREVER);
			d.setCurrent(a);
		}
	}
	
	void newReg()
	{	
		Fnew1.deleteAll();
		newRegT1= new TextField("No. of subs: ",null, 1, TextField.NUMERIC);
		
		Fnew1.append(newRegT1);
		d.setCurrent(Fnew1);
		
	}
	
	void newReg2()
	{

		Fnew2.deleteAll();
		newRegT2=new TextField("Sub name: ",null, 5, TextField.ANY);
		newRegT2F1=new TextField("Faculty name: ","", 5, TextField.ANY);
		newRegT2F2=new TextField("Faculty name: ","", 5, TextField.ANY);

		String lab_opt[]={"YES","NO"};
		newRegCL=new ChoiceGroup("Lab: ",ChoiceGroup.POPUP,lab_opt,null);
		
		Fnew2.append(newRegT2);
		Fnew2.append(newRegT2F1);
		Fnew2.append(newRegT2F2);
		Fnew2.append(newRegCL);
		
		if (i1==nos-4)
		{
			Fnew2.removeCommand(CnewNext2);
			Fnew2.addCommand(CnewFinish);
		}	
			
		d.setCurrent(Fnew2);
		
	}
	
	void mySign()
	{

		getSubs();
		Finput.deleteAll();
		String sign_opt[]={"NOT HELD","SINGLE BUNKED","DOUBLE BUNKED","SINGLE","DOUBLE"};
		
		signC=new ChoiceGroup [enos];
		
		for (int i=0; i<enos;i++)
		{
			signC[i]=new ChoiceGroup(eSubs[i],ChoiceGroup.POPUP,sign_opt,null);
			Finput.append(signC[i]);

		}		
		
		d.setCurrent(Finput);
	}
	
	void getSubs()
	{

		try
		{
			enos=rs.getNumRecords();
			eSubs=new String [enos];
			attArr=new int [enos];
			att_outtaArr=new int [enos];
			
			int k1=0, k2=0, k3=0;
			byte[] b = new byte[100];
			ByteArrayInputStream bis = new ByteArrayInputStream(b);
			DataInputStream ids =
			new DataInputStream(bis);
			re=rs.enumerateRecords(null,null,false);
			
			while(re.hasNextElement())
			
			{
				int rid=re.nextRecordId();
				
				rs.getRecord(rid,b,0);
				
				String s_name=ids.readUTF();
				int att=ids.readInt();
				int att_outta=ids.readInt();
				
				bis.reset();
				ids.reset();
				
				eSubs[k1]=s_name;
				attArr[k2]=att;
				att_outtaArr[k3]=att_outta;
				k1++;
				k2++;
				k3++;
				
			}
			


			bis.close();
			ids.close();
		}

			catch (Exception e)
			{
				a = new Alert("Error Extracting",
				e.toString(), null, AlertType.WARNING);
				a.setTimeout(Alert.FOREVER);
				d.setCurrent(a);
			}


		
	}


	void recordSubs()
	{
		subs[i1]=newRegT2.getString();
		subs[i1+1]=newRegT2F1.getString();
		subs[i1+2]=newRegT2F2.getString();
		subs[i1+3]=newRegCL.getString(newRegCL.getSelectedIndex());
		i1+=4;
		System.out.print("\n fac1: "+subs[1]+" \n fac2: "+subs[2]+"\n");
	}
		

	void createDb()
	{
		closeRec();
		delRec();
		openRec();
	
		for (int i=0; i<nos; i=i+4)
		{
		
		for (int j=1; j<4; j++)
		{
			try
			{
				
				byte [] oprecord;
				ByteArrayOutputStream bs=new ByteArrayOutputStream();
				DataOutputStream ds=new DataOutputStream(bs);
				
				
				
				if (j!=3 && subs[i+j].length()!=0)
				{
					
					ds.writeUTF(subs[i]+" ("+subs[i+j]+")");
					ds.writeInt(0);
					ds.writeInt(0);
					ds.flush();
					oprecord = bs.toByteArray();
					rs.addRecord(oprecord,0,oprecord.length);
					bs.reset();
					bs.close();
					ds.close();					
				
				}
				
				else if (j==3 && subs[i+j]=="YES")
				{
					ds.writeUTF(subs[i]+" (LAB)");
					ds.writeInt(0);
					ds.writeInt(0);
					ds.flush();
					oprecord = bs.toByteArray();
					rs.addRecord(oprecord,0,oprecord.length);
					bs.reset();
					bs.close();
					ds.close();
				}
				
								
				
				
								
				a=new Alert("Committed!", "REGISTER CREATED!", null, AlertType.WARNING);
				a.setTimeout(Alert.FOREVER);
				d.setCurrent(a,Fmenu);
			}
	
			catch (Exception e)
			{
				a = new Alert("ERROR CREATING", e.toString(), null, AlertType.WARNING);
				a.setTimeout(Alert.FOREVER);
				d.setCurrent(a);
			}
		}
		
		}
	}
	
	
	
	void signAtt()
	{
		getSubs();
		
		for (int i=0; i<enos; i++)
		{
			eSubs[i]=signC[i].getLabel();
			String tmp= signC[i].getString(signC[i].getSelectedIndex());
			if (tmp.equals("NOT HELD"))
			{
				attArr[i]+=0;
				att_outtaArr[i]+=0;
			}
			
			if (tmp.equals("SINGLE BUNKED"))
			{
				attArr[i]+=0;
				att_outtaArr[i]+=1;
			}
			
			if (tmp.equals("DOUBLE BUNKED"))
			{
				attArr[i]+=0;
				att_outtaArr[i]+=2;
			}
			
			if (tmp.equals("SINGLE"))
			{
				attArr[i]+=1;
				att_outtaArr[i]+=1;
			}
			
			if (tmp.equals("DOUBLE"))
			{
				attArr[i]+=2;
				att_outtaArr[i]+=2;
			}
		}
		
		for (int i=0; i<enos; i++)
		{
		
			try
			{
				byte [] oprecord;
				ByteArrayOutputStream bs=new ByteArrayOutputStream();
				DataOutputStream ds=new DataOutputStream(bs);
				ds.writeUTF(eSubs[i]);
				ds.writeInt(attArr[i]);
				ds.writeInt(att_outtaArr[i]);
			
				ds.flush();
				oprecord = bs.toByteArray();
				rs.setRecord(i+1,oprecord,0,oprecord.length);
				bs.reset();
				bs.close();
				ds.close();
			
				a=new Alert("Committed!", "SIGNED!", null, AlertType.WARNING);
				a.setTimeout(Alert.FOREVER);
				d.setCurrent(a,Fmenu);
			}
		
			catch (Exception e)
			{
				a = new Alert("Error committing", "The register got messed up. Sorry couldn't sign you up", null, AlertType.WARNING);
				a.setTimeout(Alert.FOREVER);
				d.setCurrent(a);
			}		
		}
	}
	
	void shitAnalyze()
	{

		getSubs();
		Fanal.deleteAll();
		Fanal.append("SUBJECT   <->   ATTENDANCE  <->  %\n\n");

		for (int i=0; i<enos; i++)
		{	
				float perc;
			
				if (att_outtaArr[i]==0)
					perc=0;
				else
					perc= 100*(attArr[i])/(att_outtaArr[i]);
			
				Fanal.append(eSubs[i]+"  <->  "+attArr[i]+"/"+att_outtaArr[i]+"  <->  "+perc+" %\n");
			
		}
		d.setCurrent(Fanal);
	}
	

	public void pauseApp(){}
	public void destroyApp(boolean unconditional){}
	
}
