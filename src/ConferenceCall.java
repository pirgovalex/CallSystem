import java.util.ArrayList;
import java.util.List;

public class ConferenceCall extends Call {
    private String host;
    private List<String> participants;

    public ConferenceCall(String host, String code) {
        super(host, null, code);//izvikvame call sus super(thank you stackoverflow)
        this.host = host;
        this.participants = new ArrayList<>();
    }

    public void addParticipant(String participant) {
        participants.add(participant);
    }

    public String getHost() {
        return host;
    }

    public List<String> getParticipants() {
        return participants;
    }
}
