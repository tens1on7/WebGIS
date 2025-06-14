package com.example.webgis;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;
import java.sql.*;

@WebServlet("/geojson/building")
public class BuildingServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");

        try (Connection conn = DBUtil.getConnection()) {
            PrintWriter out = response.getWriter();
            Statement stmt = conn.createStatement();

            // ✅ 获取查询参数
            String nameParam = request.getParameter("name");
            String sql;

            // ✅ 动态构造 SQL（生产环境建议用 PreparedStatement 防注入）
            if (nameParam != null && !nameParam.isEmpty()) {
                sql = "SELECT jsonb_build_object(" +
                        "  'type', 'FeatureCollection'," +
                        "  'features', jsonb_agg(" +
                        "    jsonb_build_object(" +
                        "      'type', 'Feature'," +
                        "      'geometry', ST_AsGeoJSON(geom)::jsonb," +
                        "      'properties', to_jsonb(building) - 'geom')" +
                        "  )" +
                        ") AS geojson " +
                        "FROM building WHERE name ILIKE '%" + nameParam + "%'";
            } else {
                sql = "SELECT jsonb_build_object(" +
                        "  'type', 'FeatureCollection'," +
                        "  'features', jsonb_agg(" +
                        "    jsonb_build_object(" +
                        "      'type', 'Feature'," +
                        "      'geometry', ST_AsGeoJSON(geom)::jsonb," +
                        "      'properties', to_jsonb(building) - 'geom')" +
                        "  )" +
                        ") AS geojson FROM building";
            }

            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                out.print(rs.getString("geojson"));
            } else {
                out.print("{\"type\":\"FeatureCollection\",\"features\":[]}");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
