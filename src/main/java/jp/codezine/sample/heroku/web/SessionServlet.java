package jp.codezine.sample.heroku.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;
import jp.codezine.sample.heroku.util.TemplateEngine;
import jp.codezine.sample.heroku.util.Database;

@WebServlet(name="SessionServlet", urlPatterns={"/session"})
public class SessionServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String message = (String)req.getSession().getAttribute("message");
		if (message == null) {
			message = "";
		}
		String dyno = System.getenv("DYNO");
		if (dyno == null) {
			dyno = "null";
		}
		String sessionId = req.getSession().getId();
		System.out.println("SessionServlet#doPost: " + dyno + ": " + sessionId + ": " + message);
		Map<String, Object> params = new HashMap<>();
		params.put("message", message);
		params.put("dyno", dyno);
		params.put("sessionId", sessionId);
		params.put("title", "セッションサンプル");
		TemplateEngine.merge(res, "session/session.html", params);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		String message = req.getParameter("message");
		System.out.println("SessionServlet#doPost: " + System.getenv("DYNO") + ": " + req.getSession().getId() + ": " + message);
		req.getSession().setAttribute("message", message);
		res.sendRedirect("/session");
	}

}
