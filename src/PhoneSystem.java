import java.util.*;

public class PhoneSystem {
    private static Map<String, Call> activeCalls = new HashMap<>();
    private static Map<String, List<Call>> pausedCalls = new HashMap<>();
    private static List<ConferenceCall> conferenceCalls = new ArrayList<>();
    private static int uniqueCallCode = 100;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String command;

        while (true) {
            command = scanner.nextLine();
            if (command.startsWith("dial")) {
                String[] parts = command.split(" ");
                if (parts.length == 3) {
                    handleDial(parts[1], parts[2]);
                } else if (parts.length > 3) {
                    handleConferenceCall(Arrays.copyOfRange(parts, 1, parts.length));
                }
            } else if (command.startsWith("pause")) {
                handlePause(command.split(" ")[1]);
            } else if (command.startsWith("resume")) {
                handleResume(command.split(" ")[1]);
            } else if (command.startsWith("random")) {
                handleRandom();            }
             else if (command.startsWith("disconnect")) {
                handleDisconnect(command.split(" ")[1]);
            } else if (command.startsWith("monitor")) {
                handleMonitor();
            } else {
                System.out.println("Invalid command");
            }
        }
    }

    private static void handleDial(String from, String to) {
        if (activeCalls.containsKey(from) || activeCalls.containsKey(to)) {
            System.out.println("One of the cabins is busy. Choose IGNORE or DISCONNECT.");
            return;
        }
        String code = generateUniqueCode();
        Call call = new Call(from, to, code);
        activeCalls.put(from, call);
        activeCalls.put(to, call);
        System.out.println("CONNECTED → [" + code + "]");//mission successfull 0:)
    }

    private static void handleConferenceCall(String[] participants) {
        if (participants.length < 3) {
            System.out.println("Invalid number of participants for a conference call.");
            return;
        }
        for (String participant : participants) {
            if (activeCalls.containsKey(participant)) {
                System.out.println("One of the cabins is busy. Choose IGNORE or DISCONNECT.");
                return;
            }
        }
        String code = generateUniqueCode();
        ConferenceCall conferenceCall = new ConferenceCall(participants[0], code);
        for (String participant : participants) {
            conferenceCall.addParticipant(participant);
            activeCalls.put(participant, conferenceCall);
        }
        conferenceCalls.add(conferenceCall);
        System.out.println("CONNECTED → [" + code + "]");
    }

    private static void handlePause(String from) {
        if (!activeCalls.containsKey(from)) {
            System.out.println("No active call to pause.");
            return;
        }
        Call call = activeCalls.get(from);
        if (call instanceof ConferenceCall) {
            ConferenceCall conferenceCall = (ConferenceCall) call;
            for (String participant : conferenceCall.getParticipants()) {
                activeCalls.remove(participant);
            }
            pausedCalls.putIfAbsent(from, new ArrayList<>());
            pausedCalls.get(from).add(conferenceCall);
        } else {
            activeCalls.remove(call.getFrom());
            activeCalls.remove(call.getTo());
            pausedCalls.putIfAbsent(from, new ArrayList<>());
            pausedCalls.get(from).add(call);
        }
        System.out.println("Call paused.");
    }


    private static void handleResume(String from) {
        if (!pausedCalls.containsKey(from) || pausedCalls.get(from).isEmpty()) {
            System.out.println("No paused calls to resume.");
            return;
        }
        Call call = pausedCalls.get(from).remove(0);
        activeCalls.put(call.getFrom(), call);
        activeCalls.put(call.getTo(), call);
        System.out.println("Call resumed.");
    }

    private static void handleDisconnect(String code) {
        boolean isConferenceCall = false;

        // Check if the call with the given code is a regular call
        Call callToRemove = activeCalls.get(code);

        // If the call is not found in regular calls, check if it's a conference call
        if (callToRemove == null) {
            for (ConferenceCall conferenceCall : conferenceCalls) {
                if (conferenceCall.getCode().equals(code)) {
                    callToRemove = conferenceCall;
                    isConferenceCall = true;
                    break;
                }
            }
        }

//If there is a call =-= let's go
        if (callToRemove != null) {
            // If it's a regular call, remove both participants ppc
            if (!isConferenceCall) {
                activeCalls.remove(callToRemove.getFrom());
                activeCalls.remove(callToRemove.getTo());
                System.out.println("Call disconnected.");
            } else { // If it's a conference call, remove all participants sushto
                for (String participant : ((ConferenceCall) callToRemove).getParticipants()) {
                    activeCalls.remove(participant);
                }
                conferenceCalls.remove(callToRemove);
                System.out.println("Conference call disconnected.");
            }
        } else {
            System.out.println("No call found with the given code.");
        }
    }

    private static void handleMonitor() {//glorified printer
        System.out.println("Active calls:");
        Set<Call> uniqueCalls = new HashSet<>(activeCalls.values());
        for (Call call : uniqueCalls) {
            if (call instanceof ConferenceCall) {
                ConferenceCall confCall = (ConferenceCall) call;
                System.out.print(confCall.getHost() + " ↔ ");
                for (String participant : confCall.getParticipants()) {
                    System.out.print(participant + " <> ");
                }
                System.out.println();
            } else {
                System.out.println(call.getFrom() + " → " + call.getTo());
            }
        }
        System.out.println("Paused calls:");
        for (Map.Entry<String, List<Call>> entry : pausedCalls.entrySet()) {
            for (Call call : entry.getValue()) {
                if (call instanceof ConferenceCall) {
                    ConferenceCall confCall = (ConferenceCall) call;
                    System.out.print(confCall.getHost() + " ↔ ");
                    for (String participant : confCall.getParticipants()) {
                        System.out.print(participant + " <> ");
                    }
                    System.out.println();
                } else {
                    System.out.println(call.getFrom() + " → " + call.getTo() + " || " + call.getTo());
                }}
        }
    }


    private static String generateUniqueCode() {
        return String.valueOf(uniqueCallCode++);
    }

    private static void handleRandom() {
        if (conferenceCalls.isEmpty()) {
            System.out.println("No active conference calls available.");
            return;
        }

        ConferenceCall leastBusyConference = conferenceCalls.get(0);
        for (ConferenceCall conferenceCall : conferenceCalls) {
            if (conferenceCall.getParticipants().size() < leastBusyConference.getParticipants().size()) {
                leastBusyConference = conferenceCall;
            }
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter cabin to add to the conference call:");
        String randomCabin = scanner.nextLine();

        if (activeCalls.containsKey(randomCabin)) {
            System.out.println("The cabin is busy. Choose IGNORE or DISCONNECT.");
            return;
        }

        leastBusyConference.addParticipant(randomCabin);
        activeCalls.put(randomCabin, leastBusyConference);
        System.out.println("Cabin added to the conference call.");
    }
}

