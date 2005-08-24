/**
 * $Id:$
 */
package org.gusdb.schemabrowser.website.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gusdb.schemabrowser.website.model.Documentation;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

/**
 * @author msaffitz
 */
public class DocumentationDAO extends HibernateDaoSupport {

    private Log  log = LogFactory.getLog( HibernateDaoSupport.class );
    private static HashMap docCache = new HashMap();
    
    public DocumentationDAO( ) {
        super( );
    }

    public String getDocumentation( String schema ) {
        if ( getDocumentationObject(schema, null, null ) == null ) return null;
        return getDocumentationObject(schema, null, null).getDocumentation();
    }

    public String getDocumentation( String schema, String table ) {
        if ( getDocumentationObject(schema, table, null ) == null ) return null;
        return getDocumentationObject(schema, table, null).getDocumentation();
    }

    public String getDocumentation( String schema, String table, String attribute ) {
        if ( getDocumentationObject(schema, table, attribute ) == null ) return null;
        return getDocumentationObject(schema, table, attribute).getDocumentation();
    }

    public Documentation getDocumentationObject( String schema, String table, String attribute ) {
        if ( docCache.isEmpty() ) cacheAll();

        String key = (schema + table + attribute).toLowerCase();
        return (Documentation) docCache.get( key );
    }

    /**
     * @param doc Documentation object to be persisted
     */
    public void saveDocumentationObject( Documentation doc ) {
        doc.setCreatedOn(new Date());
        getHibernateTemplate().save(doc);
        cacheObject(doc);
    }
    
    private String getKey(Documentation doc) {
        return (doc.getSchemaName() + doc.getTableName() + doc.getAttributeName()).toLowerCase();
    }
    
    private void cacheObject(Documentation doc) {
        String key = getKey(doc);
        log.debug("Caching: '" + key + "'");
        docCache.put(key, doc);
    }
    
    private void cacheAll() {
        log.info("Caching all Objects");
        
        List docColl = getHibernateTemplate().find("from Documentation order by createdon desc");
        if ( docColl.isEmpty()) {
            log.warn("Failed to cache any object objects when trying to cache all");
            return;
        }
        
        for ( Iterator i = docColl.iterator(); i.hasNext(); ) {
            Documentation doc = (Documentation) i.next();
            if ( ! docCache.containsKey( getKey(doc)) ) cacheObject(doc);
        }
    }

}
