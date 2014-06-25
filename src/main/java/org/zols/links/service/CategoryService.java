/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.links.service;

import org.jodel.store.DataStore;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zols.links.domain.Category;

@Service
public class CategoryService {

    private static final Logger LOGGER = getLogger(CategoryService.class);

    @Autowired
    public DataStore dataStore;

    /**
     * Creates a new Category with given Object
     *
     * @param category Object to be Create
     * @return created Category object
     */
    public Category create(Category category) {
        Category createdCategory = null;
        if (category != null) {
            createdCategory = dataStore.create(Category.class, category);
            LOGGER.info("Created Category {}", createdCategory.getName());
        }
        return createdCategory;
    }

    /**
     * Get the Category with given String
     *
     * @param categoryName String to be Search
     * @return searched Category
     */
    public Category read(String categoryName) {
        LOGGER.info("Reading Category {}", categoryName);
        return dataStore.read(Category.class, categoryName);
    }

    /**
     * Update a Category with given Object
     *
     * @param category Object to be update
     * @return
     */
    public Boolean update(Category category) {
        Boolean updated = false;
        if (category != null) {
            LOGGER.info("Updating Category {}", category);
            updated = dataStore.update(category);
        }
        return updated;
    }

    /**
     * Delete a Category with given String
     *
     * @param categoryName String to be delete
     * @return
     */
    public Boolean delete(String categoryName) {
        LOGGER.info("deleting Category {}", categoryName);
        return dataStore.delete(Category.class, categoryName);
    }

}
