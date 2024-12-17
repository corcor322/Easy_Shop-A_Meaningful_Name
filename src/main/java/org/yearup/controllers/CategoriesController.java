package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.ArrayList;
import java.util.List;


// add the annotation to make this controller the endpoint for the following url
    // http://localhost:8080/categories

@RestController
@RequestMapping("/categories")
@CrossOrigin
public class CategoriesController
{
    private CategoryDao categoryDao;
    private ProductDao productDao;

    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao) {
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }

    @PreAuthorize("permitAll()")
    @GetMapping
    public List<Category> getAll()
    {

        try {
            return categoryDao.getAllCategories();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops...our bad");
        }
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{id}")
    public Category getById(@PathVariable int id)
    { Category category = null;
        try {
            category = categoryDao.getById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops...our bad");
        }
        if (category == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
            return category;
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{categoryId}/products")
    public List<Product> getProductsById(@PathVariable int categoryId)
    {
        return productDao.listByCategoryId(categoryId);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Category addCategory(@RequestBody Category category)
    {
        try {
            return categoryDao.create(category);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops...our bad");
        }
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        try {
            categoryDao.update(id, category);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops...our bad.");
        }

    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable int id)
    {
        try {
            Category category = categoryDao.getById(id);

            if (category == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category was not found.");
            }
            categoryDao.delete(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops...our bad.");
        }

    }
}
