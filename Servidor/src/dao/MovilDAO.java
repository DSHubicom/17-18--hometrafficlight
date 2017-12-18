package dao;

import java.sql.Connection;
import java.util.List;

import model.Movil;


public interface MovilDAO {

	public void setConnection(Connection conn);

	public Movil get(long idmovil);
	
	public Movil get(String dispositivo);

	public List<Movil> getAll();

	public long add(Movil movil);

	public boolean save(Movil movil);

	public boolean delete(long idmovil);
	
	public boolean delete(String dispositivo);
}
