/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Dish;
import java.util.List;
import javax.ejb.Local;
import util.exception.CreateNewDishException;
import util.exception.DeleteDishException;
import util.exception.DishExistException;
import util.exception.DishNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author fengyuan
 */
@Local
public interface DishSessionBeanLocal {

    public List<Dish> retrieveAllDishes();

    public Dish createNewDish(Dish newDish, Long restaurantId) throws UnknownPersistenceException, InputDataValidationException, CreateNewDishException, DishExistException;

    public Dish retrieveDishById(Long dishId) throws DishNotFoundException;

    public void deleteDish(Long dishId) throws DishNotFoundException, DeleteDishException;
    
    public List<Dish> retrieveAllDishesForParticularRestaurant(Long restaurantId);
    
    public void updateDish(Dish dish) throws DishNotFoundException, InputDataValidationException;
    
}
