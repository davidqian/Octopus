package com.my.Octopus.jettyservlet;

import com.my.Octopus.util.GZipUtils;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

/**
 * Created by davidqian on 2017/8/16.
 */
public class FileServlet extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getParameter("path");

        File file = new File(path);
        if (!file.exists()) {
            String errorStr = "{\"result\":null,\"error\":\"Can not find file\"}";

            response.setContentType("text/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.write(errorStr);
            out.flush();
            return;
        }

        byte[] bytes = Files.readAllBytes(file.toPath());
        bytes = GZipUtils.compress(bytes);

        PrintWriter out = response.getWriter();
        out.write(new String(bytes, "UTF-8"));
        out.flush();
    }
}
