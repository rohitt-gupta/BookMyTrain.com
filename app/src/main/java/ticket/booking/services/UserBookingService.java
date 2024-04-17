package ticket.booking.services;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Ticket;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.utils.UserServiceUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserBookingService {

    private User user;
    private List<User> userList;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String USERS_PATH = "app/src/main/java/ticket/booking/localDb/users.json";

    public UserBookingService(User user) throws IOException {
        this.user = user;
        // JSON -> Object is called deserialization
        userList = loadUsers();
        System.out.println("After load users 29");
    }

    public UserBookingService() throws IOException{
        userList = loadUsers();
        System.out.println("after load users 34");
    }

public List<User> loadUsers() {
    System.out.println("inside load users");
    File usersFile = new File(USERS_PATH);
    System.out.println("after creating file load users");

    ObjectMapper objectMapper = new ObjectMapper();
    List<User> users = null;

    try {
        users = objectMapper.readValue(usersFile, new TypeReference<List<User>>() {});
    } catch (FileNotFoundException e) {
        System.err.println("Error: The file was not found at " + USERS_PATH);
        e.printStackTrace();
    } catch (JsonParseException e) {
        System.err.println("Error: Problem parsing the JSON in the file.");
        e.printStackTrace();
    } catch (JsonMappingException e) {
        System.err.println("Error: JSON does not match User class structure.");
        e.printStackTrace();
    } catch (IOException e) {
        System.err.println("Error: An I/O error occurred.");
        e.printStackTrace();
    }

    if (users == null) {
        System.err.println("Failed to load users. Returning an empty list.");
        return users;
    }

    return users;
}


    public Boolean loginUser(){
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashPassword());
        }).findFirst();
        return foundUser.isPresent();
    }

    public Boolean signUp(User user1){
        try{
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }

    private void saveUserListToFile() throws IOException{
        File usersFile = new File(USERS_PATH);
        // serialization
        objectMapper.writeValue(usersFile, userList);
    }

    public void fetchBooking(){
        user.printTickets();
    }

    public Boolean cancelBooking(String ticketId){
        Optional<Ticket> foundTicket = user.getTicketsBooked().stream().filter(ticket -> {
            return ticket.getTicketId().equals(ticketId);
        }).findFirst();
        try {
            if(foundTicket.isPresent()){
                user.getTicketsBooked().remove(foundTicket.get());
                saveUserListToFile();
                return Boolean.TRUE;
            }
        }catch(IOException ex){
            return Boolean.FALSE;
        }
        return Boolean.FALSE;
    }

    public List<Train> getTrains(String source, String destination) {
        try{
            TrainService trainService = new TrainService();
            return trainService.searchTrains(source, destination);
        }catch (IOException ex){
            return null;
        }
    }
}
