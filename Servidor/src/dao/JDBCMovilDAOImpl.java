package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import model.Movil;

public class JDBCMovilDAOImpl implements MovilDAO {

	private Connection conn;
	private static final Logger logger = Logger.getLogger(JDBCMovilDAOImpl.class.getName());

	@Override
	public Movil get(long idmovil) {
		if (conn == null)
			return null;

		Movil movil = null;

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Public.TablaMovil WHERE idmovil =" + idmovil);
			if (!rs.next())
				return null;
			movil = new Movil();

			movil.setIdmovil(rs.getLong("idmovil"));
			movil.setDispositivo(rs.getString("dispositivo"));
			movil.setBateria(rs.getInt("bateria"));
			logger.info("fetching Movil by idm: "+idmovil+" -> "+ movil.getDispositivo() + " " +movil.getBateria());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return movil;
	}
	
	@Override
	public Movil get(String dispositivo) {
		if (conn == null)
			return null;

		Movil movil = null;

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Public.TablaMovil WHERE dispositivo ='" + dispositivo + "'");
			if (!rs.next())
				return null;
			movil = new Movil();

			movil.setIdmovil(rs.getLong("idmovil"));
			movil.setDispositivo(rs.getString("dispositivo"));
			movil.setBateria(rs.getInt("bateria"));
			logger.info("fetching Movil by idm: "+dispositivo+" -> "+ movil.getDispositivo() + " " +movil.getBateria());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return movil;
	}

	@Override
	public List<Movil> getAll() {

		if (conn == null)
			return null;

		ArrayList<Movil> moviles = new ArrayList<Movil>();
		try {
			Statement stmt;
			ResultSet rs;
			synchronized (conn) {
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT * FROM Public.TablaMovil");
			}
			while (rs.next()) {
				Movil movil = new Movil();
				movil.setIdmovil(rs.getLong("idmovil"));
				movil.setDispositivo(rs.getString("dispositivo"));
				movil.setBateria(rs.getInt("bateria"));

				moviles.add(movil);
				logger.info("fetching moviles: " + movil.getIdmovil() + " " + movil.getDispositivo() + " " + movil.getBateria());

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return moviles;
	}

	@Override
	public long add(Movil movil) {
		long idmovil = -1;
		if (conn != null) {

			Statement stmt;
			try {
				stmt = conn.createStatement();
				stmt.executeUpdate("INSERT INTO TablaMovil (bateria, dispositivo) VALUES('" + movil.getBateria() + "','" + movil.getDispositivo() + "')", Statement.RETURN_GENERATED_KEYS);

				ResultSet genKeys = stmt.getGeneratedKeys();

				if (genKeys.next())
					idmovil = genKeys.getInt(1);

				logger.info("creating Movil(" + idmovil + "): " + movil.getDispositivo() + " " + movil.getBateria());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return idmovil;
	}
	
	@Override
	public boolean save(Movil movil) {
		boolean done = false;
		if (conn != null){
			
			Statement stmt;
			try {
				stmt = conn.createStatement();
				stmt.executeUpdate("UPDATE TablaMovil SET bateria='"+movil.getBateria()
									+"', dispositivo='"+movil.getDispositivo()
									+"' WHERE idmovil = "+movil.getIdmovil());
				logger.info("updating movil: "+movil.getIdmovil()+" "+movil.getBateria());
				done= true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		return done;

	}

	@Override
	public boolean delete(long idmovil) {
		boolean done = false;
		if (conn != null) {

			Statement stmt;
			try {
				stmt = conn.createStatement();
				stmt.executeUpdate("DELETE FROM TablaMovil WHERE idmovil =" + idmovil);
				logger.info("deleting Movil: " + idmovil);
				done = true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return done;
	}
	
	@Override
	public boolean delete(String dispositivo) {
		boolean done = false;
		if (conn != null) {

			Statement stmt;
			try {
				stmt = conn.createStatement();
				stmt.executeUpdate("DELETE FROM TablaMovil WHERE dispositivo ='" + dispositivo +"'");
				logger.info("deleting Movil: " + dispositivo);
				done = true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return done;
	}

	@Override
	public void setConnection(Connection conn) {
		this.conn = conn;
	}

}
