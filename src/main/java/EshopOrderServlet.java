import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.sql.*;
//import jakarta.servlet.*;            // Tomcat 10
//import jakarta.servlet.http.*;
//import jakarta.servlet.annotation.*;
import javax.servlet.*;            // Tomcat 9
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet("/eshoporder")
public class EshopOrderServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//response.getWriter().println("I AM HERE");

		// Set the MIME type for the response message
		response.setContentType("text/html");
		// Get a output writer to write the response message into the network socket
		PrintWriter out = response.getWriter();
		// Print an HTML page as the output of the query
		out.println("<html>");
		out.println("<head><title>Query Response</title></head>");
		out.println("<body>");

		try {
			// Step 1: Allocate a database 'Connection' object
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ebookshop", "postgres", "LiwangaSQL2023$");

			// Step 2: Allocate a 'Statement' object in the Connection
			Statement stmt = conn.createStatement();

			// Step 3 & 4: Execute a SQL SELECT query and Process the query result
			// Retrieve the books' id. Can order more than one books.
			String[] ids = request.getParameterValues("id");
			if (ids != null) {
				String sqlStr;
				int count;

				// Process each of the books
				for (int i = 0; i < ids.length; ++i) {
					// Update the qty of the table books
					sqlStr = "UPDATE books SET qty = qty - 1 WHERE id = " + ids[i];
					out.println("<p>" + sqlStr + "</p>");  // for debugging
					count = stmt.executeUpdate(sqlStr);
					out.println("<p>" + count + " record updated.</p>");
					
					// Update customer name
					String cust_name = request.getParameter("cust_name");
					String cust_email = request.getParameter("cust_email");
					String cust_phone = request.getParameter("cust_phone");

					// Create a transaction record
					sqlStr = "INSERT INTO order_records (id, qty_ordered, cust_name, cust_email, cust_phone) "
							+ "VALUES (" + ids[i] + ", 1, '" + cust_name + "', '"+ cust_email + "', '" + cust_phone + "')";
					out.println("<p>" + sqlStr + "</p>");  // for debugging
					count = stmt.executeUpdate(sqlStr);
					out.println("<p>" + count + " record inserted.</p>");
					out.println("<h3>Your order for book id=" + ids[i]
							+ " has been confirmed.</h3>");
				}
				out.println("<h3>Thank you.<h3>");
			} else { // No book selected
				out.println("<h3>Please go back and select a book...</h3>");
			}


		} catch(Exception ex) {
			out.println("<p>Error: " + ex.getMessage() + "</p>");
			out.println("<p>Check Tomcat console for details.</p>");
			ex.printStackTrace();
		}
		//Step 5: Close conn and stmt
		out.println("</body></html>");
		out.close();
	}



}