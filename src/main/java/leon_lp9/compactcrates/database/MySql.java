package leon_lp9.compactcrates.database;

import leon_lp9.compactcrates.CompactCrates;

import java.sql.*;

public class MySql {
    public String host;
    public Integer port;
    public String user;
    public String password;
    public String database;
    public String table;
    public Connection connection;

    public MySql() {
        this.connect();
    }

    public MySql(String host, Integer port, String user, String password, String database, String table) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.database = database;
        this.table = table;
        this.connect();
    }

    public void connect() {
        try {
            if (!this.isConnected()) {
                this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.user, this.password);
                System.out.println("[MySql] Successfully connected");
            }
        } catch (SQLException var2) {
            System.out.println("[MySql] " + var2.getMessage());
        }

    }

    public void disconnect() {
        if (this.isConnected()) {
            try {
                this.connection.close();
                this.connection = null;
                System.out.println("[MySql] Successfully disconnected");
            } catch (SQLException var2) {
                System.out.println("[MySql] " + var2.getMessage());
            }
        }

    }

    public boolean isConnected() {
        return this.connection != null;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public ResultSet sql(String sql) {
        try {
            PreparedStatement preparedStatement = this.getConnection().prepareStatement(sql);
            if (!sql.contains("UPDATE") && !sql.contains("DELETE") && !sql.contains("INSERT") && !sql.contains("CREATE TABLE") && !sql.contains("ALTER TABLE")) {
                return preparedStatement.executeQuery();
            } else {
                preparedStatement.executeUpdate();
                preparedStatement.close();
                return null;
            }
        }catch (SQLException e) {
            System.out.println("[MySql] " + e.getMessage());
            return null;
        }

    }

    public boolean tableExists(String table){
        try {
            DatabaseMetaData dbm = this.getConnection().getMetaData();
            ResultSet tables = dbm.getTables(null, null, table, null);
            if (tables.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println("[MySql] " + e.getMessage());
            return false;
        }
    }

    public void createTable(String table, String columns){
        try {
            CompactCrates.getInstance().getLogger().info("If table " + table + " not exist. Creating table...");
            this.sql("CREATE TABLE IF NOT EXISTS " + table + " (" + columns + ")");
            this.sql("ALTER TABLE " + table + " ADD PRIMARY KEY (uuid, crate)");

        } catch (Exception e) {
            System.out.println("[MySql] " + e.getMessage());
        }
    }

    public int getCrateAmount(String uuid, String crate){
        try {
            ResultSet rs = this.sql("SELECT * FROM " + table + " WHERE uuid='" + uuid + "' AND crate='" + crate + "'");
            if (rs.next()) {
                return rs.getInt("amount");
            } else {
                this.sql("INSERT INTO " + table + " (uuid, crate, amount) VALUES ('" + uuid + "', '" + crate + "', 0)");
                return 0;
            }
        } catch (SQLException e) {
            System.out.println("[MySql] " + e.getMessage());
            return 0;
        }
    }

    public void setCrateAmount(String uuid, String crate, int amount){
        getCrateAmount(uuid, crate);
        this.sql("UPDATE " + table + " SET amount=" + amount + " WHERE uuid='" + uuid + "' AND crate='" + crate + "'");
    }

    public void addCrateAmount(String uuid, String crate, int amount){
        int currentAmount = getCrateAmount(uuid, crate);
        setCrateAmount(uuid, crate, currentAmount + amount);
    }
}