package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo
         if(webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName())!=null){
             throw new Exception("Series is already present");
         }

         WebSeries webSeries=new WebSeries();
         webSeries.setSeriesName(webSeriesEntryDto.getSeriesName());
         webSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
         webSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());
         webSeries.setRating(webSeriesEntryDto.getRating());


        ProductionHouse productionHouse;
         try{
              productionHouse=productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).get();

         }
         catch (Exception e){
             throw new Exception("production house not found");
            }

        webSeries.setProductionHouse(productionHouse);
         productionHouse.getWebSeriesList().add(webSeries);
          webSeriesRepository.save(webSeries);

          double oldRating=productionHouse.getRatings();
          double newRating=webSeries.getRating();
          int size=productionHouse.getWebSeriesList().size();

          double updateRating=oldRating+(newRating-oldRating)/size;
          productionHouse.setRatings(updateRating);
          productionHouseRepository.save(productionHouse);
          WebSeries updatedWebseries=webSeriesRepository.save(webSeries);
          return updatedWebseries.getId();
    }

}
