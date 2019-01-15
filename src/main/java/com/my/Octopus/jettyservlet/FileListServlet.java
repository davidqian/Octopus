package com.my.Octopus.jettyservlet;

import com.alibaba.fastjson.JSON;
import io.netty.handler.codec.http.HttpResponseStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by davidqian on 2017/8/16.
 */
public class FileListServlet extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String logPath = request.getParameter("path");
        if (logPath == null || logPath.trim().length() == 0) {
            String errorStr = "{\"result\":null,\"error\":\"Can not find path param\"}";

            response.setContentType("text/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.write(errorStr);
            out.flush();
            return;
        }

        File file = new File(logPath);
        if (!file.exists()) {
            String errorStr = "{\"result\":null,\"error\":\"Can not find path\"}";

            response.setContentType("text/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.write(errorStr);
            out.flush();
        }

        File[] files = file.listFiles();

        List<String> fileNames = new ArrayList<>();
        for (File f : files) {
            fileNames.add(f.getPath());
        }

        response.setContentType("text/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write(JSON.toJSONString(fileNames));
        out.flush();
    }
}
