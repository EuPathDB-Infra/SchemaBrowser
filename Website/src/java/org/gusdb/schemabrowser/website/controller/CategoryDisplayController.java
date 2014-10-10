/**
 * $Id:$
 */
package org.gusdb.schemabrowser.website.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

public class CategoryDisplayController extends SchemaBrowserController {

    @Override
    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        
        return new ModelAndView("categoryList", "database", getDatabaseFactory().getDatabase());
    }

}
