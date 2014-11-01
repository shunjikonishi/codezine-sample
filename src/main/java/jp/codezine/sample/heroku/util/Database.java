package jp.codezine.sample.heroku.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.net.URI;
import java.net.URISyntaxException;

public class Database {

	private static boolean isPostgres(String url) {
		return url.startsWith("postgres://");
	}

	public static Connection createConnection() throws SQLException {
		String databaseUrl = System.getenv("DATABASE_URL");
		if (databaseUrl == null) {
			throw new IllegalStateException("DATABASE_URL is not specified.");
		}
		return createConnection(databaseUrl);
	}

	public static Connection createConnection(String databaseUrl) throws SQLException {
		if (isPostgres(databaseUrl)) {
			try {
				Class.forName("org.postgresql.Driver");
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException(e);
			}
			try {
				URI uri = new URI(databaseUrl);
				String host = uri.getHost();
				int port = uri.getPort();
				if (port > 0) {
					host += ":" + port;
				}
				String db = uri.getPath();
				String username = uri.getUserInfo();
				String password = null;
				int idx = username.indexOf(":");
				if (idx != -1) {
					password = username.substring(idx + 1);
					username = username.substring(0, idx);
				}
				String query = uri.getRawQuery();
				if (query == null) {
					query = "";
				} else {
					query = "?" + query;
				}
				Connection con = DriverManager.getConnection(
					"jdbc:postgresql://" + host + db + query, username, password);
				con.setAutoCommit(false);
				return con;
			} catch (URISyntaxException e) {
				throw new IllegalStateException(e);
			}
		} else {
			throw new IllegalStateException("Illegal DATABASE_URL: " + databaseUrl);
		}
	}
}