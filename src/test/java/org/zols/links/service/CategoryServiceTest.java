/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.links.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.sample.app.Application;
import org.zols.links.domain.Category;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;
    
    private Category category;

    /**
     * Test of create method, of class CategoryService.
     */
    @Test
    public void testCreate() {
        Category readCategory = categoryService.read(category.getName());
        assertThat("Created Category ", (readCategory != null));
    }

    @Test
    public void testUpdate() {
        Category readCategory = categoryService.read(category.getName());
        readCategory.setDescription("Description2");
        categoryService.update(readCategory);
        Category updatedCategory = categoryService.read(readCategory.getName());
        assertThat("Updated Category ", updatedCategory.getDescription(), equalTo("Description2"));
    }


    @Before
    public void setUp() {
        category = new Category();
        category.setName("header");
        category.setLabel("Header");
        category.setDescription("Description");
        categoryService.create(category);
    }

    @After
    public void after() {
        categoryService.delete(category.getName());
    }

}
