package DB;

import relations.*;
import util.NumberUnit;
import util.TimeUnit;

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

    public List<KnowledgeFraction> getMetaphors(){
        List<KnowledgeFraction> retVal = new ArrayList<>();
        try{
            connection = DriverManager.getConnection( getConnectionInfo() , USERNAME, PASSWORD);
            st = connection.createStatement();
            String sql = "SELECT includer, includee, SUM(frequency) AS frequency FROM tblMetaphore GROUP BY includer, includee ORDER BY includer;";
            ResultSet rs = st.executeQuery(sql);

            while(rs.next()){
                KnowledgeFraction know = new KnowledgeFraction();
                know.setWord(rs.getString("includer"));
                know.setRefWord(rs.getString("includee"));
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

    public String getDirectResponse(String msg){
        try {
            connection = DriverManager.getConnection( getConnectionInfo() , USERNAME, PASSWORD);
            st = connection.createStatement();
            String sql = "SELECT static FROM tblStaticSentence WHERE serialWord=\'" + msg + "\';";
            ResultSet rs = st.executeQuery(sql);

            String res = "";

            while(rs.next()){
                res = rs.getString("static");
            }

            rs.close();
            st.close();

            connection.close();

            if(res == null) res = "";

            return res;
        }catch(SQLException e){
            e.printStackTrace();

            return "";
        }
    }

    public List<NumberUnit> getNumberDictionary(){
        List<NumberUnit> retVal = new ArrayList<>();
        try{
            connection = DriverManager.getConnection( getConnectionInfo() , USERNAME, PASSWORD);
            st = connection.createStatement();
            String sql = "SELECT `desc`, `value`, `tag` FROM tblNumber GROUP BY `desc` ORDER BY `desc`;";
            ResultSet rs = st.executeQuery(sql);

            while(rs.next()){
                NumberUnit time = new NumberUnit(rs.getString("desc"), rs.getString("tag"), rs.getInt("value"));
                retVal.add(time);
            }

            rs.close();
            st.close();

        }catch(SQLException e){
            e.printStackTrace();
        }
        return retVal;
    }

    public List<TimeUnit> getTimeDictionary(){
        List<TimeUnit> retVal = new ArrayList<>();
        try{
            connection = DriverManager.getConnection( getConnectionInfo() , USERNAME, PASSWORD);
            st = connection.createStatement();
            String sql = "SELECT `desc`, `standalone`, `meaning`, `diff` FROM tblTime GROUP BY `desc` ORDER BY `desc`;";
            ResultSet rs = st.executeQuery(sql);

            while(rs.next()){
                boolean sa = false;
                if(rs.getInt("standalone") == 1) sa = true;
                TimeUnit time = new TimeUnit(rs.getString("desc"), rs.getString("meaning"), rs.getInt("diff"), sa);
                retVal.add(time);
            }

            rs.close();
            st.close();

        }catch(SQLException e){
            e.printStackTrace();
        }
        return retVal;
    }

    public List<KnowledgeFraction> getStaticBases(){
        List<KnowledgeFraction> retVal = new ArrayList<>();
        try{
            connection = DriverManager.getConnection( getConnectionInfo() , USERNAME, PASSWORD);
            st = connection.createStatement();
            String sql = "SELECT serialWord, serialTag, intention, SUM(frequency) AS frequency FROM tblStaticSentence GROUP BY serialWord, serialTag ORDER BY serialWord;";
            ResultSet rs = st.executeQuery(sql);

            while(rs.next()){
                KnowledgeFraction know = new KnowledgeFraction();
                know.setWord(rs.getString("serialWord"));
                know.setRefWord(rs.getString("intention"));
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

    public List<ActionFraction> getActions(){
        List<ActionFraction> retVal = new ArrayList<>();
        try{
            connection = DriverManager.getConnection( getConnectionInfo() , USERNAME, PASSWORD);
            st = connection.createStatement();
            String sql = "SELECT *, CONCAT(objectSerial, '#', verbSerial) AS keyValue FROM tblRequestMapper GROUP BY CONCAT(objectSerial, '#', verbSerial) ORDER BY no;";
            ResultSet rs = st.executeQuery(sql);

            while(rs.next()){
                String original = rs.getString("original");
                String objectSerial = rs.getString("objectSerial");
                String verbSerial = rs.getString("verbSerial");
                String intentionCode = rs.getString("intentionCode");
                String desc = rs.getString("desc");
                int frequency = rs.getInt("frequency");
                String keyValue = rs.getString("keyValue");

                ActionFraction know = new ActionFraction(original, objectSerial, verbSerial, intentionCode, desc, frequency, keyValue);
                retVal.add(know);
            }

            rs.close();
            st.close();

        }catch(SQLException e){
            e.printStackTrace();
        }
        return retVal;
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

    public int getFrequentOfStatic(String sWord, String sTag, String intention){
        try {
            connection = DriverManager.getConnection( getConnectionInfo() , USERNAME, PASSWORD);
            st = connection.createStatement();
            String sql = "SELECT IFNULL(frequency, 0) AS frequency FROM `GBot`.`tblStaticSentence` WHERE serialWord='" + sWord + "' AND serialTag='" + sTag + "' AND intention='" + intention + "';";
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

    public int getFrequentBetweenForMeta(TypedPair word, TypedPair ref){
        try {
            connection = DriverManager.getConnection( getConnectionInfo() , USERNAME, PASSWORD);
            st = connection.createStatement();
            String sql = "SELECT IFNULL(frequency, 0) AS frequency FROM `GBot`.`tblMetaphore` WHERE includer='" + word.getFirst() + "' AND includee='" + ref.getFirst() + "' AND tag_r='" + word.getSecond() + "' AND tag_e='" + ref.getSecond() + "';";
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

    public boolean saveStaticSentence(String sWord, String sTag, String intention, String resp){
        boolean retVal = false;

        try {
            String sql;

            if(resp.equals("")) resp = "NULL";
            else resp = "\'" + resp + "\'";

            if(getFrequentOfStatic(sWord, sTag, intention) == 0) sql = "INSERT INTO `GBot`.`tblStaticSentence`(`serialWord`,`serialTag`,`intention`,`static`,`uptDate`,`regDate`) " +
                    "VALUES (\'" + sWord + "\',\'" + sTag + "\',\'" + intention + "\'," + resp + ",NOW(),NOW());";
            else sql = "UPDATE `GBot`.`tblStaticSentence` SET `frequency` = (`frequency` + 1), `uptDate` = NOW()  WHERE serialWord='" + sWord + "' AND serialTag='" + sTag + "' AND intention='" + intention + "';";

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

    public boolean saveMetaLink(TypedPair word, TypedPair ref){
        boolean retVal = false;

        TypedPair param1, param2;
        param1 = word;
        param2 = ref;

        if(getFrequentBetweenForMeta(param2, param1) > 0 || param1.equals(param2)){
            System.out.println("[WARN :: 재귀 탈출 조건에 위배되는 유의어/동의어 삽입 - 무시됨]");
            return false;
        }

        try {
            String sql;

            if(getFrequentBetweenForMeta(param1, param2) == 0) {
                sql = "INSERT INTO `GBot`.`tblMetaphore`(`includer`,`includee`,`tag_r`,`tag_e`,`uptDate`,`regDate`) VALUES ('"
                        + param1.getFirst() + "','" + param2.getFirst() + "','" + param1.getSecond() + "','" + param2.getSecond() + "', NOW(), NOW());";
            }
            else sql = "UPDATE `GBot`.`tblMetaphore` SET `frequency` = (`frequency` + 1), `uptDate` = NOW() WHERE includer='"
                    + param1.getFirst() + "' AND includee='" + param2.getFirst() + "' AND tag_r='" + param1.getSecond() + "' AND tag_e='" + param2.getSecond() + "';";

            connection = DriverManager.getConnection( getConnectionInfo() , USERNAME, PASSWORD);
            st = connection.createStatement();

            retVal = st.execute(sql);

            st.close();
            connection.close();

        }catch(SQLException e){
            e.printStackTrace();
        }

        return retVal;
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
