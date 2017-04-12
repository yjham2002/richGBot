package DB;

import relations.KnowledgeBase;
import relations.KnowledgeFraction;
import relations.TypedPair;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by a on 2017-04-03.
 */
public class DBManager extends DBConstManager {

    private Connection connection = null;
    private Statement st = null;

    public DBManager(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection( getConnectionInfo() , USERNAME, PASSWORD);
            st = connection.createStatement();
        } catch (SQLException se1) {
            se1.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (st != null) st.close();
            } catch (SQLException se2) {}
            try {
                if (connection != null) connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public List<KnowledgeFraction> getKnowledges(){
        List<KnowledgeFraction> retVal = new ArrayList<>();
        try{
            connection = DriverManager.getConnection( getConnectionInfo() , USERNAME, PASSWORD);
            st = connection.createStatement();
            String sql = "SELECT word, refWord, SUM(frequency) AS frequency, `reverse` FROM tblKnowledgeLink GROUP BY word, refWord ORDER BY word;";
            ResultSet rs = st.executeQuery(sql);

            while(rs.next()){
                KnowledgeFraction know = new KnowledgeFraction();
                know.setWord(rs.getString("word"));
                know.setRefWord(rs.getString("refWord"));
//                if(rs.getInt("reverse") == 0){
//                    know.setWord(rs.getString("word"));
//                    know.setRefWord(rs.getString("refWord"));
//                }else{
//                    know.setRefWord(rs.getString("word"));
//                    know.setWord(rs.getString("refWord"));
//                }
                know.setFrequency(rs.getInt("frequency"));
                retVal.add(know);
            }

            rs.close();
            st.close();

        }catch(SQLException e){
            e.printStackTrace();
        }
        return retVal;
    }

    public int getFrequentBetween(TypedPair word, TypedPair ref){
        try {
            connection = DriverManager.getConnection( getConnectionInfo() , USERNAME, PASSWORD);
            st = connection.createStatement();
            String sql = "SELECT IFNULL(frequency, 0) AS frequency FROM `GBot`.`tblKnowledgeLink` WHERE word='" + word.getFirst() + "' AND refWord='" + ref.getFirst() + "' AND tag='" + word.getSecond() + "' AND refTag='" + ref.getSecond() + "';";
            ResultSet rs = st.executeQuery(sql);
            int rowCount = rs.last() ? rs.getRow() : 0;
            rs.close();
            st.close();

            connection.close();

            return rowCount;
        }catch(SQLException e){
            e.printStackTrace();

            return 0;
        }
    }

    public boolean saveKnowledgeLink(TypedPair word, TypedPair ref, boolean reverse){
        boolean retVal = false;

        TypedPair param1, param2;

        param1 = word;
        param2 = ref;

        try {
            String sql;

            if(getFrequentBetween(param1, param2) == 0) sql = "INSERT INTO `GBot`.`tblKnowledgeLink`(`word`,`refWord`,`tag`,`refTag`,`uptDate`,`regDate`, `reverse`) VALUES ('"
                    + param1.getFirst() + "','" + param2.getFirst() + "','" + param1.getSecond() + "','" + param2.getSecond() + "', NOW(), NOW(), " + (reverse? 1 : 0) + ");";
            else sql = "UPDATE `GBot`.`tblKnowledgeLink` SET `frequency` = (`frequency` + 1), `uptDate` = NOW() WHERE word='"
                    + param1.getFirst() + "' AND refWord='" + param2.getFirst() + "' AND tag='" + param1.getSecond() + "' AND refTag='" + param2.getSecond() + "';";

            connection = DriverManager.getConnection( getConnectionInfo() , USERNAME, PASSWORD);
            st = connection.createStatement();

            retVal = st.execute(sql);

            st.close();
            connection.close();

            return  retVal;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

}
