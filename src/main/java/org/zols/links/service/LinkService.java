/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.links.service;

import java.util.List;
import org.jodel.store.DataStore;
import org.jodel.store.query.Filter;
import org.jodel.store.query.Query;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import static org.jodel.store.query.Filter.Operator.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.zols.links.domain.Link;

/**
 *
 * @author sathish_ku
 */
public class LinkService {

    private static final Logger LOGGER = getLogger(LinkService.class);

    @Autowired
    private DataStore dataStore;

    /**
     * Creates a new Link with given Object
     *
     * @param link Object to be Create
     * @return created Link object
     */
    public Link create(Link link) {
        Link createdLink = null;
        if (link != null) {
            createdLink = dataStore.create(Link.class, link);
            LOGGER.info("Created Link {}", createdLink.getName());
        }
        return createdLink;
    }

    /**
     * Get the Link with given String
     *
     * @param linkName String to be Search
     * @return searched Link
     */
    public Link read(String linkName) {
        LOGGER.info("Reading Link {}", linkName);
        return dataStore.read(Link.class, linkName);
    }

    /**
     * Update a Link with given Object
     *
     * @param link Object to be update
     * @return
     */
    public Boolean update(Link link) {
        Boolean updated = false;
        if (link != null) {
            LOGGER.info("Updating Link {}", link);
            updated = dataStore.update(link);
        }
        return updated;
    }

    /**
     * Delete a Link with given String
     *
     * @param linkName String to be delete
     * @return
     */
    public Boolean delete(String linkName) {
        LOGGER.info("Deleting Link {}", linkName);
        return dataStore.delete(Link.class, linkName);
    }

    /**
     * Get the list of first level links with given category name
     *
     * @param categoryName Object to be search
     * @return list of links
     */
    public List<Link> getFirstLevelLinks(String categoryName) {
        LOGGER.info("Getting first level links of category {}", categoryName);
        Query query = new Query();
        query.addFilter(new Filter<>("categoryName", EQUALS, categoryName));
        query.addFilter(new Filter<>("parentLinkName", IS_NULL, categoryName));
        return dataStore.list(Link.class, query);
    }

    /**
     * Get the list of link with given Parent name
     *
     * @param parentLinkName String to be search
     * @return list of links
     */
    public List<Link> getChildern(String parentLinkName) {
        LOGGER.info("Getting children of link {}", parentLinkName);
        Query query = new Query();
        query.addFilter(new Filter<>("parentLinkName", EQUALS, parentLinkName));
        return dataStore.list(Link.class, query);
    }

}
