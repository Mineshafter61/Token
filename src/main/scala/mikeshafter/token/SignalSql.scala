package mikeshafter.token
import org.bukkit.plugin.java.JavaPlugin

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement
import java.util.Objects
import org.bukkit.Location

class SignalSql {
	final private val plugin = JavaPlugin.getPlugin(classOf[Token]);
	private def connect() = {
		// SQLite connection string
		// "jdbc:sqlite:IciwiCards.db"
		val url = plugin.getConfig.getString("database");
		var conn: Connection = null;
		try {
			conn = DriverManager.getConnection(Objects.requireNonNull(url)); 
		}
		catch {
			case e: SQLException => plugin.getLogger.warning(e.getLocalizedMessage);
		}
		conn;
	}

	/** Initialise SQL tables
	 */
	def initTables(): Unit = {
		val conn = this.connect();
		val statement = conn.createStatement;
		try {
			statement.execute( "CREATE TABLE IF NOT EXISTS signals (signo TEXT, sigtype TEXT, w TEXT, x INTEGER, y INTEGER, z INTEGER, PRIMARY KEY (signo) ); ")
			statement.execute( "CREATE TABLE IF NOT EXISTS signal_states (signo TEXT, state INT, PRIMARY KEY (signo) ); ")
		}
		catch {
			case e: SQLException => plugin.getLogger.warning(e.getLocalizedMessage);
		}
		finally {
			if (conn != null) conn.close();
			if (statement != null) statement.close();
		}
	}

	/** Creates a new signal
	 *
	 * @param signal Signal number
	 * @param sigtype Signal type
	 * @param location Location
	 */
	def newSignal(signal: String, sigtype: String, location: Location): Unit = {
		// convert location to wxyz
		val w = location.getWorld().getName();
		val x = location.getBlockX();
		val y = location.getBlockY();
		val z = location.getBlockZ();

		val sql = "INSERT INTO signals(signo, sigtype, w, x, y, z) VALUES(?, ?, ?, ?, ?, ?) ; ";
		val conn = this.connect();
		val statement = conn.prepareStatement(sql);
		try {
			statement.setString(1, signal);
			statement.setString(2, sigtype);
			statement.setString(3, w);
			statement.setInt(3, x);
			statement.setInt(5, y);
			statement.setInt(6, z);
			statement.executeUpdate;
		}
		catch {
			case e: SQLException => this.plugin.getLogger.warning(e.getLocalizedMessage);
		}
		finally {
			if (conn != null) conn.close();
			if (statement != null) statement.close();
		}
	}
}
