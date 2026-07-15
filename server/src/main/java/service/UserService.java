package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }
    public RegisterLoginResult register(RegisterRequest request) throws DataAccessException {
        if (request.username() == null || request.username().isBlank() ||
                request.password() == null || request.password().isBlank() ||
                request.email() == null || request.email().isBlank()) {
            throw new DataAccessException("bad request");
        }
        try {
            userDAO.getUser(request.username());
            throw new DataAccessException("already taken");
        } catch (DataAccessException e) {
            if (e.getMessage().equals("already taken")) {
                throw e;
            }
        }
        UserData newUser = new UserData(
                request.username(),
                request.password(),
                request.email()
        );
        userDAO.createUser(newUser);

        String authToken = java.util.UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());
        authDAO.createAuth(authData);

        return new RegisterLoginResult(request.username(), authToken);
    }

    public RegisterLoginResult login(LoginRequest request) throws DataAccessException {
        if (request.username() == null || request.username().isBlank() ||
                request.password() == null || request.password().isBlank()) {
            throw new DataAccessException("bad request");
        }
        UserData user;
        try {
            user = userDAO.getUser(request.username());
        } catch (DataAccessException e) {
            throw new DataAccessException("unauthorized");
        }
        if (!user.password().equals(request.password())) {
            throw new DataAccessException("unauthorized");
        }
        String authToken = java.util.UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());
        authDAO.createAuth(authData);

        return new RegisterLoginResult(request.username(), authToken);
    }
}
