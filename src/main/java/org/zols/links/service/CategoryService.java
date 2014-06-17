/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.links.service;

import org.jodel.store.DataStore;
import org.jodel.store.mongo.MongoDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.zols.links.domain.Category;

@Service
public class CategoryService {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CategoryService.class);

    public final DataStore dataStore;

    public CategoryService() {
        this.dataStore = new MongoDataStore();
    }

    /**
     * Creates a new Category with given Object
     *
     * @param category Object to be Create
     * @return created Category object
     */
    public Category create(Category category) {
        LOGGER.debug("Creating Category ", category.getName());
        Category createdCategory ;
        createdCategory = dataStore.create(Category.class, category);
        LOGGER.debug("Created Category ", createdCategory.getName());
        return createdCategory;
    }

    /**
     * Get the Category with given String
     *
     * @param categoryName String to be Search
     * @return searched Category
     */
    public Category read(String categoryName) {
        return dataStore.read(Category.class, categoryName);

    }

    /**
     * Update a Category with given Object
     *
     * @param category Object to be update
     * @return
     */
    public Boolean update(Category category) {
        return dataStore.update(category);
    }

    /**
     * Delete a Category with given String
     *
     * @param categoryName String to be delete
     * @return
     */
    public Boolean delete(String categoryName) {
        return dataStore.delete(Category.class, categoryName);
    }

}
