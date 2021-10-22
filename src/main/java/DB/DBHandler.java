package DB;

public class DBHandler {

    private static DBHandler dbHandler;

    private DBHandler() {

    }

    public static DBHandler getInstance() {
        if (dbHandler == null) {
            dbHandler = new DBHandler();
        }
        return dbHandler;
    }

}
