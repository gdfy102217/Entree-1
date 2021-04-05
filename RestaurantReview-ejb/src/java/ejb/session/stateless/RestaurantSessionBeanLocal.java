/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Restaurant;
import entity.TableConfiguration;
import java.util.List;
import javax.ejb.Local;
import util.exception.BankAccountNotFoundException;
import util.exception.ChangePasswordException;
import util.exception.DishNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.RestaurantNotFoundException;
import util.exception.RestaurantUsernameExistException;
import util.exception.TableConfigurationExistException;
import util.exception.TableConfigurationNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author fengyuan
 */
@Local
public interface RestaurantSessionBeanLocal {

    public Long createNewRestaurant(Restaurant newRestaurant, TableConfiguration newTableConfiguration) throws UnknownPersistenceException, InputDataValidationException, RestaurantUsernameExistException, TableConfigurationExistException;

    public List<Restaurant> retrieveAllRestaurants();

    public Restaurant restaurantLogin(String username, String password) throws InvalidLoginCredentialException;

    public Restaurant retrieveRestaurantById(Long restaurantId) throws RestaurantNotFoundException;

    public List<Restaurant> searchRestaurantsByName(String searchString);

    public Restaurant retrieveRestaurantByEmail(String email) throws RestaurantNotFoundException;

    public Long updateRestaurant(Restaurant restaurant) throws RestaurantNotFoundException, InputDataValidationException, DishNotFoundException, BankAccountNotFoundException, TableConfigurationNotFoundException;

    public Restaurant changePassword(Long restaurantId, String newPassword) throws ChangePasswordException;
    
}
