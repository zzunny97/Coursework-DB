import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.DriverManager;

public class JDBCexample {
    public static void main(String[] args){
        try { 
            Class.forName ("com.mysql.jdbc.Driver"); 
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbbnam", "bnam", "changethis"); 
            Statement stmt = conn.createStatement(); 
			/*
			PreparedStatement pstmt = conn.prepareSatement(
					"insert into instructor values(?,?,?,?)");
			pstmt.setString(1, "88877");
			pstmt.setString(2, "Perry");
			pstmt.setString(3, "Finance");
			pstmt.setInt(4, 125000);
			pstmt.executeUpdate();
			pstmt.setString(1, "88878");
			pstmt.executeUpdate();
			*/

            ResultSet rset = stmt.executeQuery(
                    "select ID, name, dept_name, salary " + 
                    "from instructor " + 
                    //"where name = '" + "John' or '1'='1" + "'" );
                    "where name = '" + args[0] + "'" );
			ResultSetMetaData rsmd = rset.getMetaData();
			for(int i=1; i<=rsmd.getColumnCount(); i++) {
				System.out.println(rsmd.getColumnName(i));
				System.out.println(rsmd.getColumnTypeName(i));

			}

            while (rset.next()) {
                System.out.printf("%10s %10s %12s %10.2f\n", 
                           rset.getString("ID"),                                 
                           rset.getString("name"),                                 
                           rset.getString("dept_name"),                                 
                           rset.getFloat(4));     
            }

            stmt.close();    
            conn.close();    
        }        
        catch (SQLException sqle) {         
            System.out.println("SQLException : " + sqle);        
        }
        catch (Exception e) {         
            System.out.println("Exception : " + e);        
        }
    }
}
