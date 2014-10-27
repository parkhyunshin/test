import java.text.*;
import java.sql.*;
import java.math.*;

public class db_insert {

    static String durl = "jdbc:postgresql://192.168.50.170/uzeni";
    static String usr = "postgres";
    static String pwd = "trust";
    static String ertable = "error_catch_log";
    static PreparedStatement pst = null;

    public static void insert(String str,String ndate,String target,BigDecimal bid,String link,String body,String table,String keyword,String eclass,String borco,String colink, int copnum, int comnum){
	try {
		Class.forName("org.postgresql.Driver");
		Connection conn = DriverManager.getConnection(durl, usr, pwd);

                String sql = "insert into "+table+"(gettm,doctm,target,num,link,body,keyword,borco,colink,copnum,comnum) values(?,?,?,?,?,?,?,?,?,?,?)";
                pst = conn.prepareStatement(sql);

                pst.setString(1,str);
                pst.setString(2,ndate);
                pst.setString(3,target);
                pst.setBigDecimal(4,bid);
                pst.setString(5,link);
                pst.setString(6,body);
		pst.setString(7,keyword);
		pst.setString(8,borco);
		pst.setString(9,colink);
		pst.setInt(10,copnum);
		pst.setInt(11,comnum);
		
                pst.executeUpdate();
                conn.close();
            }catch(Exception ex){
	        error_up(str,eclass,table,keyword,ex.toString());
            }
    }

    public static void error_up(String edate,String eclass,String etable,String ekeyword, String etext) {
        String str = null;
        long curtime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        str = sdf.format(curtime);

        try {
                Class.forName("org.postgresql.Driver");
                Connection conn = DriverManager.getConnection(durl, usr, pwd);

                String sql = "insert into "+ertable+"(time,class,table_name,keyword,etext) values(?,?,?,?,?)";
                pst = conn.prepareStatement(sql);

                pst.setString(1,edate);
                pst.setString(2,eclass);
                pst.setString(3,etable);
                pst.setString(4,ekeyword);
                pst.setString(5,etext);

                pst.executeUpdate();
                conn.close();
                }catch(Exception ex){
                        error_up(edate,eclass,etable,ekeyword,ex.toString());
                }
    }
}
