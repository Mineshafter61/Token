package mikeshafter.token
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.{Bukkit, Location, Material}

import java.sql.{Connection, DriverManager, SQLException}
import java.util.Objects

class SignalSql {
    final private val plugin = JavaPlugin.getPlugin(classOf[Token])
    private def connect() = {
        val url = plugin.getConfig.getString("database")
        var conn: Connection = null
        try {
            conn = DriverManager.getConnection(Objects.requireNonNull(url))
        } catch case e: SQLException => e.printStackTrace()
        conn
    }

    /** Initialise SQL tables
      */
    def initTables(): Unit = {
        val conn = this.connect()
        val statement = conn.createStatement
        try {
            statement.execute(
              "CREATE TABLE IF NOT EXISTS signals (signo TEXT, sigtype TEXT, w TEXT, x INTEGER, y INTEGER, z INTEGER, PRIMARY KEY (signo) ) "
            )
            statement.execute(
              "CREATE TABLE IF NOT EXISTS signal_states (signo TEXT, state INT, PRIMARY KEY (signo) ) "
            )
        } finally {
            if (conn != null) conn.close()
            if (statement != null) statement.close()
        }
    }

    /** Creates a new signal
      *
      * @param signal
      *   Signal number
      * @param sigtype
      *   Signal type
      * @param location
      *   Location
      */
    def newSignal(signal: String, sigtype: String, location: Location): Unit = {
        // convert location to wxyz
        val w = location.getWorld.getName
        val x = location.getBlockX
        val y = location.getBlockY
        val z = location.getBlockZ

        val sql =
            "INSERT INTO signals(signo, sigtype, w, x, y, z) VALUES(?, ?, ?, ?, ?, ?)  "
        val conn = this.connect()
        val statement = conn.prepareStatement(sql)
        try {
            statement.setString(1, signal)
            statement.setString(2, sigtype)
            statement.setString(3, w)
            statement.setInt(4, x)
            statement.setInt(5, y)
            statement.setInt(6, z)
            statement.executeUpdate
        } finally {
            if (conn != null) conn.close()
            if (statement != null) statement.close()
        }
    }

    def removeSignal(signal: String): Int = {
        val sql = "DELETE FROM signals WHERE signo = ?  "
        val conn = this.connect()
        val statement = conn.prepareStatement(sql)
        try {
            statement.setString(1, signal)
            statement.executeUpdate
        } finally {
            if (conn != null) conn.close()
            if (statement != null) statement.close()
        }
    }

    /** Gets the location of a signal
      *
      * @param signal
      *   Signal number
      * @return
      *   the signal's location
      */
    def getSignalLoc(signal: String): Option[Location] = {
        val sql = "SELECT w, x, y, z FROM signals WHERE signo = ?"
        val conn = this.connect()
        val statement = conn.prepareStatement(sql)
        try {
            statement.setString(1, signal)
            val rs = statement.executeQuery()
            val w = Bukkit.getServer.getWorld(rs.getString(1))
            val x = rs.getInt(2)
            val y = rs.getInt(3)
            val z = rs.getInt(4)
            if (conn != null) conn.close()
            if (statement != null) statement.close()
            Some(Location(w, x, y, z))
        } catch case _ => {
            if (conn != null) conn.close()
            if (statement != null) statement.close()
            None
        }
    }

    def setSignal(signal: String, aspect: Aspect.Value): Unit = {
        val signalLoc: Option[Location] = SignalSql().getSignalLoc(signal)
        if (signalLoc.isEmpty) return
        signalLoc.get.getChunk.setForceLoaded(true)
        aspect match
            case mikeshafter.token.Aspect.PROCEED => signalLoc.get.getBlock.setType(Material.LIME_WOOL)
            case mikeshafter.token.Aspect.CAUTION => signalLoc.get.getBlock.setType(Material.SHROOMLIGHT)
            case mikeshafter.token.Aspect.STOP => signalLoc.get.getBlock.setType(Material.REDSTONE_BLOCK)
    }
}
