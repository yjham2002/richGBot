package DB;

/**
 * Created by a on 2017-04-03.
 */
public class DBConstManager {

    public static final String CONNECTOR = "jdbc";
    public static final String DBMS = "mysql";
    public static final String HOST = "182.161.118.74";
    public static final String PORT = "3306";
    public static final String DBNAME = "GBot";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "$#@!richware7";
    public boolean autoConnect = true;

    public String getConnectionInfo(){
        return CONNECTOR + ":" + DBMS + "://" + HOST + ":" + PORT + "/" + DBNAME + "?useUnicode=yes&amp;characterEncoding=UTF-8&amp;autoReconnect=" + autoConnect;
    }

    public boolean setAutuConnect(boolean value){
        this.autoConnect = value;
        return this.autoConnect;
    }
}
