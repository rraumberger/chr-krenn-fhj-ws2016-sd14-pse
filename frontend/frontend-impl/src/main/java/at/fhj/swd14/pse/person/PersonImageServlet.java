package at.fhj.swd14.pse.person;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet to serve person images
 * @author Patrick Kainz
 *
 */
@WebServlet(name = "PersonImageServlet", urlPatterns = {"/personImage"})
public class PersonImageServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@EJB(name = "ejb/PersonService")
    private PersonService service;


    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        Long id =Long.parseLong(request.getParameter("id"));
        PersonImageDto img = service.getPersonImage(id);

        response.setContentType(img.getContentType());
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(img.getData());
        outputStream.close();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    
    @Override
    public String getServletInfo() {
        return "Person image retrieval service";
    }
	
}