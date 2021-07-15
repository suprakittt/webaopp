package io.muic.ooc.webapp.service;
import io.muic.ooc.webapp.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class UserService {

    private static final String INSERT_USER_SQL = "INSERT INTO tbl_user (username, password, display_name) VALUES (?, ?, ?); ";
    private static final String SELECT_USER_SQL = "SELECT * FROM tbl_user WHERE username = ?;";


    private DatabaseConnectionService databaseConnectionService;

    public void setDatabaseConnectionService(DatabaseConnectionService databaseConnectionService) {
        this.databaseConnectionService = databaseConnectionService;
    }

    // create new user
    public void createUser(String username, String password, String displayName) throws UserServiceException {

        try {
            Connection connection = databaseConnectionService.getConnection();
            PreparedStatement ps = connection.prepareStatement(INSERT_USER_SQL);
            ps.setString(1, username);
            ps.setString(2, BCrypt.hashpw(password, BCrypt.gensalt()));
            ps.setString(3, displayName);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLIntegrityConstraintViolationException e){
            throw new UsernameNotUniqueException(String.format("Username %s has been already taken.", username));
        } catch (SQLException throwables) {
            throw new UserServiceException(throwables.getMessage());
        }


    }

    public User findByUsername(String username){
        try {
            Connection connection = databaseConnectionService.getConnection();
            PreparedStatement ps = connection.prepareStatement(SELECT_USER_SQL);
            ps.setString(1, username);
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            return new User(
                    resultSet.getLong("id"),
                    resultSet.getString("username"),
                    resultSet.getString("password"),
                    resultSet.getString("display_name")
            );
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }

    }

    public static void main(String[] args) {
        UserService userService = new UserService();
        userService.setDatabaseConnectionService(new DatabaseConnectionService());
        User user = userService.findByUsername("suprakitt");
        System.out.println(user.getUsername());
    }
    // find user by username

    // delete user
    // list all user
    // update user by user id

/*    public static void main(String[] args) throws UserServiceException {
        UserService userService = new UserService();
        userService.setDatabaseConnectionService(new DatabaseConnectionService());
        userService.createUser("Suprakitt", "devpass", "Suprakit");

    }*/


}
