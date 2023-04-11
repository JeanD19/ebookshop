import java.io.IOException;

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

@WebServlet("/eshopquery")
public class EshopQueryServlet extends HttpServlet {

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

			//Step 3: Execute a SQL SELECT query

			String[] authors = request.getParameterValues("author");  // Returns an array of Strings

			if (authors == null) {
				out.println("<h2>No author selected. Please go back to select author(s)</h2><body></html>");
				return; // Exit doGet()
			} 


			String sqlStr = "SELECT * FROM books WHERE author IN (";
			for (int i = 0; i < authors.length; ++i) {
				if (i < authors.length - 1)
					sqlStr += "'" + authors[i] + "', ";
				else
					sqlStr += "'" + authors[i] + "'";
			}
			sqlStr += ") AND price < " + request.getParameter("price") + " AND qty > 0 ORDER BY author ASC, title ASC";

			out.println("<h3>Thank you for your query.</h3>");
			//out.println("<p>Your SQL statement is: " + sqlStr + "</p>"); // Echo for debugging
			ResultSet rset = stmt.executeQuery(sqlStr);  // Send the query to the server

			// Step 4: Process the query result
			// Print the <form> start tag
			out.println("<form method='get' action='eshoporder'>");

			// For each row in ResultSet, print one checkbox inside the <form>
//			while(rset.next()) {
//				out.println("<p><input type='checkbox' name='id' value="
//						+ "'" + rset.getString("id") + "' />"
//						+ rset.getString("author") + ", "
//						+ rset.getString("title") + ", $"
//						+ rset.getString("price") + "</p>");
//			}
			
			// For each row in ResultSet, print one checkbox inside the <form>
	         out.println("<table border='1'>");
	         out.println("<thead>");
	         out.println("<tr>");
	         out.println("<th></th>");
	         out.println("<th>Author</th>");
	         out.println("<th>Title</th>");
	         out.println("<th>Price</th>");
	         out.println("</tr>");
	         out.println("</thead>");
	         out.println("<tbody>");

	         while (rset.next()) {
	             out.println("<tr>");
	             out.println("<td><input type='checkbox' name='id' value='" + rset.getString("id") + "' /></td>");
	             out.println("<td>" + rset.getString("author") + "</td>");
	             out.println("<td>" + rset.getString("title") + "</td>");
	             out.println("<td>$" + rset.getString("price") + "</td>");
	             out.println("</tr>");
	         }

	         out.println("</tbody>");
	         out.println("</table>");
	         
			out.println("<p>Enter your Name: <input type='text' name='cust_name' /></p>");
			out.println("<p>Enter your Email: <input type='text' name='cust_email' /></p>");
			out.println("<p>Enter your Phone Number: <input type='text' name='cust_phone' /></p>");
			
			// Print the submit button and </form> end-tag
			out.println("<p><input type='submit' value='ORDER' />");
			out.println("</form>");

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