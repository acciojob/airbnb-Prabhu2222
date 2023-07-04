package com.driver.Repository;

import com.driver.model.Booking;
import com.driver.model.Facility;
import com.driver.model.Hotel;
import com.driver.model.User;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class HotelManagementRepository {
    HashMap<String,Hotel>hotelDb=new HashMap<>();//stores hotel name vs hotel obj
    HashMap<Integer,User>userDb=new HashMap<>();//stores user adharcard no vs user
    HashMap<String,Booking>bookingDb=new HashMap<>();//stores booking id vs booking
      HashMap<Integer,List<Booking>>userBookingsDb=new HashMap<>(); //userAdharCardNo vs bookings database
    public String addHotel(Hotel hotel) {
        //Incase somebody is trying to add the duplicate hotelName return FAILURE
        if(hotelDb.containsKey(hotel.getHotelName())) return "FAILURE";
        hotelDb.put(hotel.getHotelName(),hotel);
        return "SUCCESS";
    }

    public Integer addUser(User user) {
        userDb.put(user.getaadharCardNo(),user);
        return user.getaadharCardNo();
    }

    public String getHotelWithMostFacilities() {
        //Out of all the hotels we have added so far, we need to find the hotelName with most no of facilities
        //Incase there is a tie return the lexicographically smaller hotelName
        //Incase there is not even a single hotel with atleast 1 facility return "" (empty string)
        List<String> list=new ArrayList<>();
        int max_facility=0;
        for(String name:hotelDb.keySet()){
            Hotel hotelObj=hotelDb.get(name);
            if(hotelObj.getFacilities().size()>=1){
                if(hotelObj.getFacilities().size()>max_facility){
                    max_facility=hotelObj.getFacilities().size();
                    list.clear();
                    list.add(name);
                }
                else if(hotelObj.getFacilities().size()==max_facility ){
                    list.add(name);
                }
            }
        }
        if(list.size()==0) return "";
        Collections.sort(list);
        return list.get(0);
    }

    public int bookARoom(Booking booking) {

        //The booking object coming from postman will have all the attributes except bookingId and amountToBePaid;
        //Have bookingId as a random UUID generated String
        //save the booking Entity and keep the bookingId as a primary key
        //Calculate the total amount paid by the person based on no. of rooms booked and price of the room per night.
        //If there arent enough rooms available in the hotel that we are trying to book return -1
        //in other case return total amount paid


        int noOfRoomTryingToBook=booking.getNoOfRooms();
        String hotelNameTryingToBook=booking.getHotelName();
        int noOfRoomAvailableInTheHotel=hotelDb.get(hotelNameTryingToBook).getAvailableRooms();
        if(noOfRoomTryingToBook>noOfRoomAvailableInTheHotel) return -1;

        int adharNo=booking.getBookingAadharCard();
        userBookingsDb.put(adharNo,new ArrayList<Booking>());
        userBookingsDb.get(adharNo).add(booking);

        String id= String.valueOf(UUID.randomUUID());
        booking.setBookingId(id);
        bookingDb.put(id,booking);

        //updating th available room in corresponding hotel
        Hotel hotelObj=hotelDb.get(hotelNameTryingToBook);
        hotelObj.setAvailableRooms(hotelObj.getAvailableRooms()-noOfRoomTryingToBook);


        int amount=noOfRoomTryingToBook*hotelDb.get(hotelNameTryingToBook).getPricePerNight();
        return amount;

    }

    public int getBookings(Integer aadharCard) {
        return userBookingsDb.get(aadharCard).size();
    }

    public Hotel updateFacilities(List<Facility> newFacilities, String hotelName) {
        //We are having a new facilites that a hotel is planning to bring.
        //If the hotel is already having that facility ignore that facility otherwise add that facility in the hotelDb
        //return the final updated List of facilities and also update that in your hotelDb
        //Note that newFacilities can also have duplicate facilities possible
        Set<Facility> set=new HashSet<>(newFacilities);
        Hotel hotelObj=hotelDb.get(hotelName);
        List<Facility> toBeAdded=new ArrayList<>(hotelObj.getFacilities());
        for(Facility ele:set){
            if(hotelObj.getFacilities().contains(ele)){
                //donothin
            }else{
                toBeAdded.add(ele);
            }
        }
        hotelObj.setFacilities(toBeAdded);
        return hotelObj;
    }
}
