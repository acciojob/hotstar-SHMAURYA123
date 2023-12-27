package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
         User user =userRepository.findById(subscriptionEntryDto.getUserId()).get();
         Subscription subscription=new Subscription();
         int totalAmount=0;
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());

        if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.BASIC)){
            totalAmount=500+(subscriptionEntryDto.getNoOfScreensRequired()*200);
        }
        else if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.PRO)){
            totalAmount=800+(subscriptionEntryDto.getNoOfScreensRequired()*250);
        }
        else{
            totalAmount=1000+(subscriptionEntryDto.getNoOfScreensRequired()*350);
        }

        subscription.setTotalAmountPaid(totalAmount);
        subscription.setUser(user);
        user.setSubscription(subscription);
        userRepository.save(user);
        return totalAmount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user =userRepository.findById(userId).get();
        int prevPaid=user.getSubscription().getTotalAmountPaid();
        int currPaid=0;

        if(user.getSubscription().equals(SubscriptionType.ELITE)){
            throw new Exception("Already the best Subscription");
        }
        else if(user.getSubscription().equals(SubscriptionType.PRO)){
            currPaid=1000+(user.getSubscription().getNoOfScreensSubscribed()*350);
        }
        else{
          currPaid=800+(user.getSubscription().getNoOfScreensSubscribed()*250);
        }
        subscriptionRepository.save(user.getSubscription());
        return currPaid-prevPaid;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> revenueList=subscriptionRepository.findAll();
        Integer totalRevenue=0;

        for(Subscription revenue:revenueList){
            totalRevenue+=revenue.getTotalAmountPaid();
        }
        return totalRevenue;
    }

}
