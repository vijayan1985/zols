/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.web.config.controller;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.zols.datastore.DataStore;
import com.zols.datastore.domain.Entity;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CoreController {

    @Autowired
    private DataStore dataStore;

    @RequestMapping(value = "/controlpanel", method = GET)
    @ApiIgnore
    public String controlPanel() {
        return "controlpanel";
    }

    @RequestMapping(value = "/master/{name}", method = GET)
    @ResponseBody
    public List master(@PathVariable(value = "name") String name) {
        List masterList = null;
        if (name.equals("attributeType")) {
            masterList = dataStore.list(Entity.class);
            if (masterList == null) {
                masterList = new ArrayList(5);
            }
            Entity entity = null;
            entity = new Entity();
            entity.setName("String");
            entity.setLabel("String");
            masterList.add(0, entity);

            entity = new Entity();
            entity.setName("Integer");
            entity.setLabel("Integer");
            masterList.add(0, entity);

            entity = new Entity();
            entity.setName("Double");
            entity.setLabel("Double");
            masterList.add(0, entity);

            entity = new Entity();
            entity.setName("Float");
            entity.setLabel("Float");
            masterList.add(0, entity);

            entity = new Entity();
            entity.setName("Date");
            entity.setLabel("Date");
            masterList.add(0, entity);
            
            entity = new Entity();
            entity.setName("RichText");
            entity.setLabel("RichText");
            masterList.add(0, entity);
        }
        return masterList;
    }
}
