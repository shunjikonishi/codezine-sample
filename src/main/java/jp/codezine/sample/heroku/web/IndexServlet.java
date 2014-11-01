package jp.codezine.sample.heroku.web;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;
import jp.codezine.sample.heroku.util.TemplateEngine;

@WebServlet(name="IndexServlet", urlPatterns={"/"})
public class IndexServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Map<String, Object> params = new HashMap<>();
		params.put("title", "Codezine Heroku Sample");
		TemplateEngine.merge(res, "index.html", params);
	}

}
