/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.links.web;

import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import org.springframework.web.bind.annotation.RestController;
import org.zols.links.domain.Category;

@RestController
@RequestMapping(value = "/api/links")
public class LinkController {

    private static final Logger LOGGER = getLogger(LinkController.class);

    @RequestMapping(method = GET)
    public Category read() {
        Category category = new Category();
        category.setDescription("Desc");
        return category;
    }
}
