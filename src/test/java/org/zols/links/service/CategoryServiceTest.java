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

    /**
     * Test of create method, of class CategoryService.
     */
    @Test
    public void testCreate() {
        Category category = new Category();
        category.setDescription("Description");
        Category createdCategory = categoryService.create(category);
        Category readCategory = categoryService.read(createdCategory.getName());
        assertThat("Created Category ", (readCategory != null));
    }

    @Test
    public void testUpdate() {
        Category category = new Category();
        category.setDescription("Description");
        Category createdCategory = categoryService.create(category);        
        createdCategory.setDescription("Description2");        
        categoryService.update(createdCategory);        
        Category readCategory = categoryService.read(createdCategory.getName());        
        assertThat("Updated Category ", readCategory.getDescription(),equalTo("Description2"));
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

}
