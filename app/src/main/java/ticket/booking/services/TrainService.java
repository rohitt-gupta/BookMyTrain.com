package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Train;
import ticket.booking.entities.Train;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class TrainService {

    private Train train;
    private List<Train> trainList;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String USERS_PATH = "app/src/main/java/ticket/booking/localDb/trains.json";

    public TrainService(Train train) throws IOException {
        this.train = train;
        // JSON -> Object is called deserialization
        trainList = loadTrains();
    }

    public TrainService() throws IOException{
        loadTrains();
    }

    public List<Train> loadTrains() throws IOException{
        File trains = new File(USERS_PATH);
        return objectMapper.readValue(trains, new TypeReference<List<Train>>() {});
    }

    public List<Train> searchTrains (String source, String destination) {
        return trainList.stream().filter(train -> validTrain(train, source, destination)).collect(Collectors.toList());
    }

    public Boolean validTrain(Train train, String source, String destination) {
        List<String> stationOrder = train.getStations();

        int sourceIndex = stationOrder.indexOf(source.toLowerCase());
        int destinationIndex = stationOrder.indexOf(destination.toLowerCase());

        return sourceIndex!=-1 && destinationIndex!=-1 && destinationIndex>sourceIndex;
    }
}
