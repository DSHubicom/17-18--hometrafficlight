package resources;

import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import dao.MovilDAO;
import exceptions.CustomBadRequestException;
import dao.JDBCMovilDAOImpl;
import model.Movil;

@Path("/moviles")
public class MovilesResource {

	@Context
	ServletContext sc;
	@Context
	UriInfo uriInfo;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Movil> getMoviles(@Context HttpServletRequest request) {

		Connection conn = (Connection) sc.getAttribute("dbConn");
		
		MovilDAO movilDAO = new JDBCMovilDAOImpl();
		movilDAO.setConnection(conn);

		List<Movil> moviles = movilDAO.getAll();

		return moviles;
	}
	
	@GET
	@Path("/{dispositivo: .+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Movil getMovilByDispositivo(@PathParam("dispositivo") String dispositivo, @Context HttpServletRequest request) {

		Connection conn = (Connection) sc.getAttribute("dbConn");

		MovilDAO movilDAO = new JDBCMovilDAOImpl();
		movilDAO.setConnection(conn);

		Movil movil = movilDAO.get(dispositivo);

		if (movil == null) {
			throw new CustomBadRequestException("Error in dispositivo");
		}

		return movil;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postMovil(Movil newMovil, @Context HttpServletRequest request) throws Exception {

		Response res;
		Connection conn = (Connection) sc.getAttribute("dbConn");

		MovilDAO movilDAO = new JDBCMovilDAOImpl();
		movilDAO.setConnection(conn);

		long id = movilDAO.add(newMovil);

		res = Response // return 201 and Location: /moviles/newid
				.created(uriInfo.getAbsolutePathBuilder().path(Long.toString(id)).build())
				.contentLocation(uriInfo.getAbsolutePathBuilder().path(Long.toString(id)).build()).build();

		return res;
	}


	// PUT que actualiza a partir del objeto recibido
	@PUT
	@Path("/{dispositivo: .+}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putMovil(Movil movilUpdate, @PathParam("dispositivo") String dispositivo,
			@Context HttpServletRequest request) throws Exception {
		Response response = null;

		Connection conn = (Connection) sc.getAttribute("dbConn");

		MovilDAO movilDAO = new JDBCMovilDAOImpl();
		movilDAO.setConnection(conn);

		Movil movil = movilDAO.get(dispositivo);
		
		if (movil != null) {
			movilUpdate.setIdmovil(movil.getIdmovil());
			movilDAO.save(movilUpdate);
		} else {
			throw new CustomBadRequestException("Error in dispositivo");
		}
		return response;
	}

	@DELETE
	@Path("/{dispositivo: .+}")
	public Response deleteMovil(@PathParam("dispositivo") String dispositivo, @Context HttpServletRequest request) {

		Connection conn = (Connection) sc.getAttribute("dbConn");

		MovilDAO movilDAO = new JDBCMovilDAOImpl();
		movilDAO.setConnection(conn);

		Movil movil = movilDAO.get(dispositivo);
		if (movil != null) {
			movilDAO.delete(dispositivo);
			return Response.noContent().build(); // 204 no content
		} else {
			throw new CustomBadRequestException("Error in user or id");
		}

	}

}
