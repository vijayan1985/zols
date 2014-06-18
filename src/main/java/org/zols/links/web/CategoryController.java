/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.links.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zols.links.domain.Category;
import org.zols.links.service.CategoryService;

@RestController
@RequestMapping(value="/api/link_categories")
public class CategoryController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CategoryController.class);
    
    @Autowired
    private CategoryService categoryService;    

    @RequestMapping(method = POST)    
    public Category create(@RequestBody Category category) {
        LOGGER.info("Creating new categories {}", category);
        return categoryService.create(category);
    }

    @RequestMapping(value = "/api/link_categories/{name}", method = GET)    
    public Category read(@PathVariable(value = "name") String name) {
        LOGGER.info("Getting category ", name);
        return categoryService.read(name);
    }

    @RequestMapping(value = "/api/link_categories/{name}", method = PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "name") String name,
            @RequestBody Category category) {
        LOGGER.info("Updating categories with id {} with {}", name, category);
        if (name.equals(category.getName())) {
            categoryService.update(category);
        }
    }

    @RequestMapping(value = "/api/link_categories/{name}", method = DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "name") String name) {
        LOGGER.info("Deleting categories with id {}", name);
        categoryService.delete(name);
    }
}
