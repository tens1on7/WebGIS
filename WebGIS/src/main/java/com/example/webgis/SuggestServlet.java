package com.example.webgis;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import org.json.JSONArray;

@WebServlet("/geojson/suggest")
public class SuggestServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (keyword == null || keyword.trim().isEmpty()) {
            out.print("[]");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT DISTINCT name FROM building WHERE name ILIKE ? AND name IS NOT NULL LIMIT 10";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "%" + keyword + "%");
                ResultSet rs = stmt.executeQuery();

                JSONArray jsonArray = new JSONArray();
                while (rs.next()) {
                    jsonArray.put(rs.getString("name"));
                }
                out.print(jsonArray.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("[]");
        }
    }
}
