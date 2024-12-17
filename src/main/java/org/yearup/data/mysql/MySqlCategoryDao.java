package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    @Autowired
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT * FROM categories";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                categories.add(mapRow(resultSet));
            }

        } catch (SQLException e) {

            e.printStackTrace();
            throw new RuntimeException("Error retrieving categories from the database", e);
        }

        return categories;
    }




    @Override
    public Category getById(int categoryId)
    {
        String query = "SELECT * FROM categories WHERE category_id = ?";

        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, categoryId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching category with ID: " + categoryId, e);
        }

        return null;
    }

    @Override
    public Category create(Category category)
    {
        String query = "INSERT INTO categories (category_id, name, description) VALUES (?,?,?)";
        // create a new category
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, category.getCategoryId());
            preparedStatement.setString(2, category.getName());
            preparedStatement.setString(3, category.getDescription());

            int rows = preparedStatement.executeUpdate();

            if (rows == 0) {
                throw new SQLException("Insert failed, no rows affected!");
            }
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedID = generatedKeys.getInt(1);
                    category.setCategoryId(generatedID);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return category;
    }

    @Override
    public void update(int categoryId, Category category)
    {
        // update category
        String query = "UPDATE categories SET name = ?, description = ? WHERE category_id = ?";

        try (Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.setInt(3, categoryId);

            int rows = preparedStatement.executeUpdate();

            if (rows == 0) {
                throw new SQLException("Update failed, no rows affected!");
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int categoryId)
    {
        // delete category
        String query = "DELETE FROM categories WHERE category_id = ?";

        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, categoryId);

            int rows = preparedStatement.executeUpdate();

            if (rows == 0) {
                throw new SQLException("Failed to delete category, no rows affected!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
