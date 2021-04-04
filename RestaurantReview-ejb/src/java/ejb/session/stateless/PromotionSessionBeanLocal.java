/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Promotion;
import java.util.List;
import javax.ejb.Local;
import util.exception.CreateNewPromotionException;
import util.exception.InputDataValidationException;
import util.exception.PromotionExistException;
import util.exception.PromotionNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdatePromotionException;

/**
 *
 * @author fengyuan
 */
@Local
public interface PromotionSessionBeanLocal {

    public Promotion createNewPromotion(Promotion newPromotion, Long restaurantId) throws UnknownPersistenceException, InputDataValidationException, CreateNewPromotionException, PromotionExistException;

    public List<Promotion> retrieveAllPromotions();

    public Promotion retrievePromotionById(Long promotionId) throws PromotionNotFoundException;

    public void deletePromotion(Long promotionId) throws PromotionNotFoundException;

    public List<Promotion> retrievePromotionByRestaurantId(Long restaurantId);

    public void updatePromotion(Promotion promotion) throws PromotionNotFoundException, UpdatePromotionException, InputDataValidationException;
    
}
