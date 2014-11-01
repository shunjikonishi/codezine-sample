package jp.codezine.sample.heroku.web;

import java.io.IOException;
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

@WebServlet(name="BoardServlet", urlPatterns={"/board"})
public class BoardServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		List<Message> list = new ArrayList<Message>();
		try {
			Connection con = Database.createConnection();
			try {
				PreparedStatement stmt = con.prepareStatement(
					"SELECT nickname, message, post_date FROM message_board ORDER BY id DESC LIMIT 20"
				);
				try {
					ResultSet rs = stmt.executeQuery();
					try {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
						while(rs.next()) {
							Message msg = new Message();
							msg.nickname = rs.getString("nickname");
							msg.message = rs.getString("message").split("\n");
							msg.postDate = sdf.format(rs.getTimestamp("post_date"));
							list.add(msg);
						}
					} finally {
						rs.close();
					}
				} finally {
					stmt.close();
				}
			} finally {
				con.close();
			}
		} catch (SQLException e) {
			res.setStatus(500);
			res.getWriter().print(e.toString());
			return;
		}
		Map<String, Object> params = new HashMap<>();
		String nickname = getNicknameFromCookie(req);
		if (nickname != null) {
			setNicknameToCookie(res, nickname);
		}
		params.put("nickname", nickname == null ? "" : nickname);
		params.put("title", "掲示板");
		params.put("messages", list);
		TemplateEngine.merge(res, "board/board.html", params);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		String nickname = req.getParameter("nickname");
		String message = req.getParameter("message");
		if (nickname == null || message == null) {
			res.setStatus(401);
			res.getWriter().print("Bad Request");
			return;
		}
		try {
			Connection con = Database.createConnection();
			try {
				PreparedStatement stmt = con.prepareStatement(
					"INSERT INTO message_board (nickname,message) VALUES(?, ?)"
				);
				try {
					stmt.setString(1, nickname);
					stmt.setString(2, message);
					stmt.executeUpdate();
					con.commit();
				} finally {
					stmt.close();
				}
			} finally {
				con.close();
			}
			setNicknameToCookie(res, nickname);
			res.sendRedirect("/board");
		} catch (SQLException e) {
			res.setStatus(500);
			res.getWriter().print(e.toString());
			return;
		}
	}

	private String getNicknameFromCookie(HttpServletRequest req) {
		Cookie[] cookies = req.getCookies();
		if (cookies == null) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if ("nickname".equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		return null;
	}
	private void setNicknameToCookie(HttpServletResponse res, String nickname) {
		Cookie cookie = new Cookie("nickname", nickname);
		cookie.setMaxAge(60 * 60 * 24 * 30);// 1 month
		res.addCookie(cookie);
	}

	public static class Message {
		public String nickname;
		public String[] message;
		public String postDate;
	}
}
