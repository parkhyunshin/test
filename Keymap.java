import java.util.HashMap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;

public class keymap {
    public static HashMap<String,String> readmap(String gtime,String eclass,String table,String keyword){
	String gdate = gtime.substring(0,8);
	HashMap<String,String> mapInFile = new HashMap();
//	File toRead = new File("/db/Api/Test/Naver/"+gdate+"_"+eclass+".txt");
//	File toRead = new File("/usr/lib/hue/rep/tmp/"+gdate+"_"+eclass+".txt");
	File toRead = new File("/usr/lib/hue/rep/khnp/"+gdate+"_"+eclass+".txt");

	if (toRead.exists())
	{
		try{
			FileInputStream fis = new FileInputStream(toRead);
			ObjectInputStream ois = new ObjectInputStream(fis);

			mapInFile = (HashMap<String,String>)ois.readObject();
		
			ois.close();
			fis.close();
		
		}catch(Exception e){
			new db_insert().error_up(gtime,eclass,table,keyword,e.toString());
		}
	return mapInFile;
	}

	else
	{
		return mapInFile;
	}
    }

    public static void savemap(HashMap<String,String> map,String gtime,String eclass,String table,String keyword){
	String gdate = gtime.substring(0,8);

	try{
//		File fileTwo=new File("/db/Api/Test/Naver/"+gdate+"_"+eclass+".txt");
//		File fileTwo=new File("/usr/lib/hue/rep/tmp/"+gdate+"_"+eclass+".txt");
		File fileTwo = new File("/usr/lib/hue/rep/khnp/"+gdate+"_"+eclass+".txt");
		FileOutputStream fos = new FileOutputStream(fileTwo);
		ObjectOutputStream oos = new ObjectOutputStream(fos);

		oos.writeObject(map);
		oos.flush();
		oos.close();
		fos.close();
	}catch(Exception e){
		new db_insert().error_up(gtime,eclass,table,keyword,e.toString());
	}
    }
}
