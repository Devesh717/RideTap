package com.cfs.RideTap.station.service.implementations;

import com.cfs.RideTap.station.model.Station;
import com.cfs.RideTap.station.repository.StationRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@Log4j2
public class MultiCityMetroNetworkSeeder {

  @Data
  @AllArgsConstructor
  public static class Edge {
    private String toStation;
    private String line;
    private String lineColor;
    private int weight;
  }

  @Data
  @AllArgsConstructor
  public static class LineDefinition {
    private String lineName;
    private String hexColor;
    private List<String> stations;
  }

  @Autowired
  private StationRepository stationRepository;

  private final Map<String, List<Edge>> adjacencyGraph = new HashMap<>();

  @PostConstruct
  @Transactional
  public void seedAllMetroNetworks() {
    Map<String, Station> stationMap = new LinkedHashMap<>();
    Map<String, List<Edge>> edges = new HashMap<>();

    // ==========================================
    // MULTI-CITY TRANSIT MAP CONFIGURATION
    // ==========================================
    Map<String, List<LineDefinition>> cityTransitMap = Map.of(
            "Delhi NCR", List.of(
                    new LineDefinition("Red Line", "#E53935", List.of("Shaheed Sthal (New Bus Adda)", "Hindon River", "Arthala", "Mohan Nagar", "Shyam Park", "Major Mohit Sharma Rajendra Nagar", "Raj Bagh", "Shaheed Nagar", "Dilshad Garden", "Jhilmil", "Mansarovar Park", "Shahdara", "Welcome", "Seelampur", "Shastri Park", "Kashmere Gate", "Tis Hazari", "Pulbangash", "Pratap Nagar", "Shastri Nagar", "Inderlok", "Kanhaiya Nagar", "Keshav Puram", "Netaji Subhash Place", "Kohat Enclave", "Pitampura", "Rohini East", "Rohini West", "Rithala")),
                    new LineDefinition("Yellow Line", "#FDD835", List.of("Samaypur Badli", "Rohini Sector 18, 19", "Haiderpur Badli Mor", "Jahangirpuri", "Adarsh Nagar", "Azadpur", "Model Town", "GTB Nagar", "Vishwavidyalaya", "Vidhan Sabha", "Civil Lines", "Kashmere Gate", "Chandni Chowk", "Chawri Bazar", "New Delhi", "Rajiv Chowk", "Patel Chowk", "Central Secretariat", "Udyog Bhawan", "Lok Kalyan Marg", "Jor Bagh", "Dilli Haat - INA", "AIIMS", "Green Park", "Hauz Khas", "Malviya Nagar", "Saket", "Qutab Minar", "Chhatarpur", "Sultanpur", "Ghitorni", "Arjan Garh", "Guru Dronacharya", "Sikanderpur", "MG Road", "IFFCO Chowk", "Millennium City Centre Gurugram")),
                    new LineDefinition("Blue Line", "#1E88E5", List.of("Dwarka Sector 21", "Dwarka Sector 8", "Dwarka Sector 9", "Dwarka Sector 10", "Dwarka Sector 11", "Dwarka Sector 12", "Dwarka Sector 13", "Dwarka Sector 14", "Dwarka", "Dwarka Mor", "Nawada", "Uttam Nagar West", "Uttam Nagar East", "Janakpuri West", "Janakpuri East", "Tilak Nagar", "Subhash Nagar", "Tagore Garden", "Rajouri Garden", "Ramesh Nagar", "Moti Nagar", "Kirti Nagar", "Shadipur", "Patel Nagar", "Rajendra Place", "Karol Bagh", "Jhandewalan", "Ramakrishna Ashram Marg", "Rajiv Chowk", "Barakhamba Road", "Mandi House", "Supreme Court", "Indraprastha", "Yamuna Bank", "Akshardham", "Mayur Vihar Phase-1", "Mayur Vihar Extension", "New Ashok Nagar", "Noida Sector 15", "Noida Sector 16", "Noida Sector 18", "Botanical Garden", "Golf Course", "Noida City Centre", "Noida Sector 34", "Noida Sector 52", "Noida Sector 61", "Noida Sector 59", "Noida Sector 62", "Noida Electronic City")),
                    new LineDefinition("Blue Line Branch", "#1565C0", List.of("Yamuna Bank", "Laxmi Nagar", "Nirman Vihar", "Preet Vihar", "Karkarduma", "Anand Vihar ISBT", "Kaushambi", "Vaishali")),
                    new LineDefinition("Green Line", "#43A047", List.of("Inderlok", "Ashok Park Main", "Punjabi Bagh", "Punjabi Bagh West", "Shivaji Park", "Madipur", "Paschim Vihar East", "Paschim Vihar West", "Peeragarhi", "Udyog Nagar", "Maharaja Surajmal Stadium", "Nangloi", "Nangloi Railway Station", "Rajdhani Park", "Mundka", "Mundka Industrial Area", "Ghevra Metro Station", "Tikri Kalan", "Tikri Border", "Pandit Shree Ram Sharma", "Bahadurgarh City", "Brigadier Hoshiar Singh")),
                    new LineDefinition("Green Line Branch", "#2E7D32", List.of("Ashok Park Main", "Satguru Ram Singh Marg", "Kirti Nagar")),
                    new LineDefinition("Violet Line", "#8E24AA", List.of("Kashmere Gate", "Lal Quila", "Jama Masjid", "Delhi Gate", "ITO", "M Mandi House", "Janpath", "Central Secretariat", "Khan Market", "Jawaharlal Nehru Stadium", "Jangpura", "Lajpat Nagar", "Moolchand", "Kailash Colony", "Nehru Place", "Kalkaji Mandir", "Govind Puri", "Harkesh Nagar Okhla", "Jasola Apollo", "Sarita Vihar", "Mohan Estate", "Tughlakabad Station", "Badarpur Border", "Sarai", "NHPC Chowk", "Mewala Maharajpur", "Sector 28", "Badkal Mor", "Old Faridabad", "Neelam Chowk Ajronda", "Bata Chowk", "Escorts Mujesar", "Sant Surdas (Sihi)", "Raja Nahar Singh (Ballabhgarh)")),
                    new LineDefinition("Pink Line", "#EC407A", List.of("Majlis Park", "Azadpur", "Shalimar Bagh", "Netaji Subhash Place", "Shakurpur", "Punjabi Bagh West", "ESI - Basaidarapur", "Rajouri Garden", "Mayapuri", "Naraina Vihar", "Delhi Cantt", "Durgabai Deshmukh South Campus", "Sir M. Vishweshwaraiah Moti Bagh", "Bhikaji Cama Place", "Sarojini Nagar", "Dilli Haat - INA", "South Extension", "Lajpat Nagar", "Vinobapuri", "Ashram", "Sarai Kale Khan - Nizamuddin", "Mayur Vihar Phase-1", "Mayur Vihar Pocket 1", "Trilokpuri - Sanjay Lake", "East Vinod Nagar - Mayur Vihar-II", "Mandawali - West Vinod Nagar", "IP Extension", "Anand Vihar ISBT", "Karkarduma", "Karkarduma Court", "Krishna Nagar", "East Azad Nagar", "Welcome", "Jaffrabad", "Maujpur - Babarpur", "Gokulpuri", "Johri Enclave", "Shiv Vihar")),
                    new LineDefinition("Magenta Line", "#D81B60", List.of("Janakpuri West", "Dabri Mor - Janakpuri South", "Dashrath Puri", "Palam", "Sadar Bazaar Cantonment", "Terminal 1-IGI Airport", "Shankar Vihar", "Vasant Vihar", "Munirka", "R.K. Puram", "IIT Delhi", "Hauz Khas", "Panchsheel Park", "Chirag Delhi", "Greater Kailash", "Nehru Enclave", "Kalkaji Mandir", "Okhla NSIC", "Sukhdev Vihar", "Jamia Millia Islamia", "Okhla Vihar", "Jasola Vihar Shaheen Bagh", "Kalindi Kunj", "Okhla Bird Sanctuary", "Botanical Garden")),
                    new LineDefinition("Orange Line (Airport Express)", "#FB8C00", List.of("New Delhi", "Shivaji Stadium", "Dhaula Kuan", "Delhi Aerocity", "IGI Airport (T-3)", "Dwarka Sector 21", "Yashobhoomi Dwarka Sector 25")),
                    new LineDefinition("Grey Line", "#78909C", List.of("Dwarka", "Nangli", "Najafgarh", "Dhansa Bus Stand")),
                    new LineDefinition("Aqua Line (Noida-Greater Noida)", "#00ACC1", List.of("Noida Sector 51", "Noida Sector 50", "Noida Sector 76", "Noida Sector 101", "Noida Sector 81", "NSEZ", "Noida Sector 83", "Noida Sector 137", "Noida Sector 142", "Noida Sector 143", "Noida Sector 144", "Noida Sector 145", "Noida Sector 146", "Noida Sector 147", "Noida Sector 148", "Knowledge Park II", "Pari Chowk", "Alpha 1", "Delta 1", "GNIDA Office", "Depot Station")),
                    new LineDefinition("Rapid Metro Gurugram", "#26A69A", List.of("Sector 55-56", "Sector 54 Chowk", "Sector 53-54", "Sector 42-43", "Phase 1", "Sikanderpur", "Phase 2", "Belvedere Towers", "Cyber City", "Moulsari Avenue", "Phase 3"))
            ),
            "Mumbai", List.of(
                    new LineDefinition("Mumbai Metro Line 1", "#EE3124", List.of("Versova", "D.N. Nagar", "Azad Nagar", "Andheri", "Western Express Highway", "Chakala", "Airport Road", "Marol Naka", "Saki Naka", "Asalpha", "Jagruti Nagar", "Ghatkopar")),
                    new LineDefinition("Mumbai Metro Line 2A", "#FFCD00", List.of("Dahisar East", "Anand Nagar", "Kandarpada", "Mandapeshwar", "Don Bosco", "Eksar", "Borivali West", "Pai Nagar", "Valnai", "Malad West", "Lower Malad", "Oshiwara", "Adarsh Nagar", "Western Express Highway", "Dahanukarwadi", "Kandivali West", "Charkop", "Ekta Nagar", "Bandra Kurla Complex")),
                    new LineDefinition("Mumbai Metro Line 3 (Aqua)", "#00A859", List.of("Cuffe Parade", "Vidhan Bhavan", "Churchgate", "Hutatma Chowk", "CSMT", "Bhendi Bazaar", "Nagpada", "Mumbai Central", "Mahalaxmi", "Science Museum", "Acharya Atre Chowk", "Worli", "Siddhivinayak", "Dadar", "Sheetal Wadi", "Dharavi", "BKC", "Bandra Colony", "Santacruz", "CSIA T1", "Sahara Road", "CSIA T2", "Marol Naka", "MIDC", "SEEPZ", "Aarey Colony")),
                    new LineDefinition("Mumbai Metro Line 7", "#E53935", List.of("Dahisar East", "Ovaripada", "National Park", "Devipada", "Magathane", "Poisar", "Akurli", "Kandivali East", "Samta Nagar", "Thakur Complex", "Mahadu Jadhav Nagar", "Dindoshi", "Aarey", "Goregaon East", "Jogeshwari East", "Shastri Nagar", "Oshiwara", "Gundavali")),
                    new LineDefinition("Mumbai Monorail", "#008000", List.of("Jacob Circle", "Sant Gadge Maharaj Chowk", "Parel Village", "Elphinstone Road", "Dadar TT", "Wadala Bridge", "Bhakti Park", "Mysore Colony", "Bharat Petroleum", "Chembur"))
            ),
            "Bengaluru", List.of(
                    new LineDefinition("Namma Metro Purple Line", "#68228B", List.of("Challaghatta", "Kengeri", "Kengeri Bus Terminal", "Pattanagere", "Jnanabharathi", "Rajarajeshwari Nagar", "Nayandahalli", "Mysore Road", "Deepanjali Nagar", "Attiguppe", "Vijayanagar", "Magadi Road", "City Railway Station", "Majestic", "Sir M. Visveshwaraya Station Central College", "MG Road", "Trinity", "Indiranagar", "Swami Vivekananda Road", "Baiyappanahalli", "Benniganahalli", "Krishnarajapura", "Singayyanapalya", "Garudacharapalya", "Hoodi", "Seetharampalya", "Kundalahalli", "Nallurhalli", "Sathya Sai Hospital", "Whitefield (Kadugodi)")),
                    new LineDefinition("Namma Metro Green Line", "#008000", List.of("Madavara", "Nagasandra", "Dasarahalli", "Jalahalli", "Peenya Industry", "Peenya", "Yeshwanthpur", "Sandal Soap Factory", "Mahalakshmi", "Rajajinagar", "Kuvempu Road", "Srirampura", "Majestic", "Chickpete", "Krishnaraj Market", "National College", "Lalbagh", "South End Circle", "Jayanagar", "Rashtreeya Vidyalaya Road", "Banashankari", "J.P. Nagar", "Yelachenahalli", "Konankunte Cross", "Doddakallasandra", "Vajarahalli", "Thalaghattapura", "Silk Institute")),
                    new LineDefinition("Namma Metro Yellow Line", "#F58220", List.of("RV Road", "Ragigudda", "BTM Layout", "Central Silk Board", "Bommanahalli", "Singasandra", "Hosa Road", "Beratena Agrahara", "Electronic City", "Jigani Link Road", "Bommasandra"))
            ),
            "Kolkata", List.of(
                    new LineDefinition("Kolkata Metro Line 1 (Blue)", "#00539F", List.of("Kavi Subhash", "Kavi Nazrul", "Gitanjali", "Masterda Surya Sen", "Netaji", "Tollygunge", "Kalighat", "Jatin Das Park", "Netaji Bhavan", "Rabindra Sadan", "Maidan", "Park Street", "Esplanade", "Chandni Chowk", "Central", "Mahatma Gandhi Road", "Girish Park", "Shyambazar", "Belgachhia", "Dum Dum", "Noapara", "Baranagar", "Dakshineswar")),
                    new LineDefinition("Kolkata Metro Line 2 (Green)", "#00A859", List.of("Howrah Maidan", "Howrah", "Mahakaran", "Esplanade", "Sealdah", "Phoolbagan", "Salt Lake Stadium", "City Centre", "Central Park", "Karunamoyee", "Salt Lake Sector V")),
                    new LineDefinition("Kolkata Metro Line 6 (Orange)", "#FF8C00", List.of("Kavi Subhash", "Satyajit Ray", "Jyotirindra Nandi", "Kavi Sukanta", "Hemanta Mukhopadhyay", "VIP Bazar", "Ritwik Ghatak", "Barun Sengupta", "Ballygunge Phari", "Park Circus", "Sir Gurudas Banerjee Halt", "Ultadanga", "V.I.P. Road", "Nagerbazar", "Dum Dum Cantonment", "Biman Bandar (Airport)"))
            ),
            "Chennai", List.of(
                    new LineDefinition("Chennai Metro Blue Line", "#00539F", List.of("Wimco Nagar Depot", "Wimco Nagar", "Tiruvotriyur", "Tiruvotriyur High Road", "New Washermanpet", "Tollgate", "Thangal", "Gourinath Thottam", "Tondiarpet", "Sir Theagaraya College", "Washermanpet", "Mannadi", "High Court", "Central Metro", "Government Estate", "LIC", "Thousand Lights", "AG-DMS", "Teynampet", "Nandanam", "Saidapet", "Little Mount", "Guindy", "Alandur", "Nanganallur Road", "Meenambakkam", "Chennai International Airport")),
                    new LineDefinition("Chennai Metro Green Line", "#008000", List.of("Chennai Central", "Egmore", "Nehru Park", "Kilpauk Medical College", "Pachaiyappa's College", "Chetpet", "Loyola College", "Shenoy Nagar", "Anna Nagar East", "Anna Nagar Tower", "Thirumangalam", "Koyambedu", "CMBT", "Arumbakkam", "Vadapalani", "Ashok Nagar", "Ekkattuthangal", "Alandur", "St. Thomas Mount"))
            ),
            "Hyderabad", List.of(
                    new LineDefinition("Hyderabad Metro Red Line", "#E53935", List.of("Miyapur", "JNTU College", "Kukatpally", "KPHB Colony", "Balanagar", "Moosapet", "Bharat Nagar", "Erragadda", "ESI Hospital", "S.R. Nagar", "Ameerpet", "Punjagutta", "Raj Bhavan", "Khairatabad", "Lakdi-ka-pul", "Assembly", "Nampally", "Gandhi Bhavan", "Osmania Medical College", "MGBS", "Malakpet", "New Market", "Musarambagh", "Dilsukhnagar", "Chaitanyapuri", "Victoria Memorial", "LB Nagar")),
                    new LineDefinition("Hyderabad Metro Blue Line", "#1E88E5", List.of("Nagole", "Uppal", "Stadium", "NGRI", "Habsiguda", "Tarnaka", "Mettuguda", "Secunderabad East", "Parade Ground", "Secunderabad West", "Gandhi Hospital", "Musheerabad", "RTC X Roads", "Chikkadpally", "Narayanaguda", "Sultan Bazaar", "MGBS", "Osmania University", "Kacheguda", "Ameerpet", "Madhura Nagar", "Prakash Nagar", "Begumpet", "Jubilee Hills Road No. 5", "Jubilee Hills Check Post", "Peddamma Gudi", "Madhapur", "Durgam Cheruvu", "Hitec City", "Raidurg")),
                    new LineDefinition("Hyderabad Metro Green Line", "#43A047", List.of("JBS Parade Ground", "Secunderabad West", "Gandhi Hospital", "Musheerabad", "RTC X Roads", "Chikkadpally", "Narayanaguda", "Sultan Bazaar", "MGBS"))
            ),
            "Pune", List.of(
                    new LineDefinition("Pune Metro Aqua Line", "#00ACC1", List.of("Vanaz", "Anand Nagar", "Ideal Colony", "Nal Stop", "Garware College", "Deccan Gymkhana", "Chhatrapati Sambhaji Maharaj Park", "PMC", "Civil Court", "Ruby Hall Clinic", "Bund Garden", "Yerawada", "Kalyani Nagar", "Ramwadi")),
                    new LineDefinition("Pune Metro Purple Line", "#8E24AA", List.of("PCMC", "Sant Tukaram Nagar", "Nashik Phata", "Kasarwadi", "Dapodi", "Bopodi", "Khadki", "Range Hill", "Shivajinagar", "Civil Court", "Budhwar Peth", "Mandai", "Swargate"))
            )
    );

    // Automatically loop through the Map configuration to populate cities, lines, and edges
    for (Map.Entry<String, List<LineDefinition>> entry : cityTransitMap.entrySet()) {
      String cityName = entry.getKey();
      for (LineDefinition line : entry.getValue()) {
        registerCityLine(stationMap, edges, cityName, line.getLineName(), line.getHexColor(), line.getStations());
      }
    }

    // Register cross-line walking interchanges per city
    addCityWalkingTransfer(edges, "Delhi NCR", "Noida Sector 52", "Noida Sector 51", "Aqua-Blue Skywalk", "#00ACC1");
    addCityWalkingTransfer(edges, "Delhi NCR", "Dhaula Kuan", "Durgabai Deshmukh South Campus", "Pink-Orange Skywalk", "#FB8C00");
    addCityWalkingTransfer(edges, "Delhi NCR", "Botanical Garden", "Botanical Garden", "Magenta-Blue Interchange", "#1E88E5");
    addCityWalkingTransfer(edges, "Mumbai", "Western Express Highway", "Gundavali", "WEH-Gundavali Skywalk", "#EE3124");
    addCityWalkingTransfer(edges, "Bengaluru", "Majestic", "Majestic", "Purple-Green Interchange", "#68228B");
    addCityWalkingTransfer(edges, "Bengaluru", "Rashtreeya Vidyalaya Road", "RV Road", "Green-Yellow Interchange", "#008000");
    addCityWalkingTransfer(edges, "Kolkata", "Esplanade", "Esplanade", "Blue-Green Interchange", "#00539F");
    addCityWalkingTransfer(edges, "Kolkata", "Kavi Subhash", "Kavi Subhash", "Blue-Orange Interchange", "#00539F");
    addCityWalkingTransfer(edges, "Chennai", "Central Metro", "Chennai Central", "Blue-Green Interchange", "#00539F");
    addCityWalkingTransfer(edges, "Chennai", "Alandur", "Alandur", "Blue-Green Interchange", "#00539F");
    addCityWalkingTransfer(edges, "Hyderabad", "Ameerpet", "Ameerpet", "Red-Blue Interchange", "#E53935");
    addCityWalkingTransfer(edges, "Hyderabad", "MGBS", "MGBS", "Red-Blue-Green Interchange", "#E53935");
    addCityWalkingTransfer(edges, "Hyderabad", "Parade Ground", "Parade Ground", "Blue-Green Interchange", "#1E88E5");
    addCityWalkingTransfer(edges, "Pune", "Civil Court", "Civil Court", "Aqua-Purple Interchange", "#00ACC1");

    // ==========================================
    // DATABASE SYNCHRONIZATION
    // ==========================================
    List<Station> existing = stationRepository.findAll();
    Map<String, Station> existingByKey = new HashMap<>();
    long maxSeq = 0L;
    for (Station dbSt : existing) {
      String existingCity = (dbSt.getCity() != null && !dbSt.getCity().isBlank())
              ? dbSt.getCity()
              : "Delhi NCR";
      String compKey = normalize(existingCity + "_" + dbSt.getName());
      existingByKey.put(compKey, dbSt);
      if (dbSt.getSequenceNumber() != null && dbSt.getSequenceNumber() > maxSeq) {
        maxSeq = dbSt.getSequenceNumber();
      }
    }

    List<Station> toSave = new ArrayList<>();
    for (Map.Entry<String, Station> entry : stationMap.entrySet()) {
      Station template = entry.getValue();
      Station dbSt = existingByKey.get(entry.getKey());
      if (dbSt == null) {
        template.setSequenceNumber(++maxSeq);
        template.setInterchange(template.getLines().size() > 1);
        toSave.add(template);
      } else {
        dbSt.setCity(template.getCity());
        dbSt.setLines(new ArrayList<>(template.getLines()));
        dbSt.setLineColors(new ArrayList<>(template.getLineColors()));
        dbSt.setInterchange(template.getLines().size() > 1);
        toSave.add(dbSt);
      }
    }

    if (!toSave.isEmpty()) {
      stationRepository.saveAll(toSave);
      log.info("Successfully synced and updated {} multi-city metro stations from Map config.", toSave.size());
    }

    List<Station> orphanStations = new ArrayList<>();
    for (Station dbSt : existing) {
      String existingCity = (dbSt.getCity() != null && !dbSt.getCity().isBlank())
              ? dbSt.getCity()
              : "Delhi NCR";
      String compKey = normalize(existingCity + "_" + dbSt.getName());
      if (!stationMap.containsKey(compKey)) {
        orphanStations.add(dbSt);
      }
    }

    if (!orphanStations.isEmpty()) {
      for (Station orphan : orphanStations) {
        try {
          stationRepository.delete(orphan);
          log.info("Removed obsolete unroutable station '{}' (id={}) from DB.", orphan.getName(), orphan.getId());
        } catch (Exception ex) {
          // If referenced by an existing Booking foreign key (like historical tickets),
          // automatically bridge it into the active graph so the ticket routes correctly!
          String city = (orphan.getCity() != null && !orphan.getCity().isBlank()) ? orphan.getCity().trim() : "Delhi NCR";
          String orphanNorm = normalize(orphan.getName());
          stationMap.values().stream()
                  .filter(s -> s.getCity().equalsIgnoreCase(city))
                  .filter(s -> normalize(s.getName()).contains(orphanNorm) || orphanNorm.contains(normalize(s.getName())))
                  .findFirst()
                  .ifPresent(target -> {
                    String keyA = normalize(city + "_" + orphan.getName());
                    String keyB = normalize(city + "_" + target.getName());
                    edges.computeIfAbsent(keyA, k -> new ArrayList<>())
                            .add(new Edge(target.getName(), target.getLines().get(0), target.getLineColors().get(0), 0));
                    edges.computeIfAbsent(keyB, k -> new ArrayList<>())
                            .add(new Edge(orphan.getName(), target.getLines().get(0), target.getLineColors().get(0), 0));
                    log.info("Bridged legacy booked station '{}' -> '{}' in {} graph.", orphan.getName(), target.getName(), city);
                  });
        }
      }
    }

    synchronized (adjacencyGraph) {
      adjacencyGraph.clear();
      adjacencyGraph.putAll(edges);
    }
  }

  public Map<String, List<Edge>> getAdjacencyGraph() {
    return Collections.unmodifiableMap(adjacencyGraph);
  }

  public synchronized void addDynamicEdge(String city, String from, String to, String line, String color) {
    String fKey = normalize(city + "_" + from);
    String tKey = normalize(city + "_" + to);
    adjacencyGraph.computeIfAbsent(fKey, k -> new ArrayList<>())
            .add(new Edge(to, line, color, 1));
    adjacencyGraph.computeIfAbsent(tKey, k -> new ArrayList<>())
            .add(new Edge(from, line, color, 1));
  }

  private void registerCityLine(
          Map<String, Station> stationMap,
          Map<String, List<Edge>> edges,
          String city,
          String lineName,
          String hexColor,
          List<String> stationNames) {

    for (int i = 0; i < stationNames.size(); i++) {
      String currentName = stationNames.get(i);
      String key = normalize(city + "_" + currentName);

      Station st = stationMap.computeIfAbsent(key, k -> {
        Station s = new Station();
        s.setName(currentName);
        s.setCity(city);
        s.setLines(new ArrayList<>());
        s.setLineColors(new ArrayList<>());
        return s;
      });

      if (!st.getLines().contains(lineName)) {
        st.getLines().add(lineName);
        st.getLineColors().add(hexColor);
      }

      if (i > 0) {
        String prevName = stationNames.get(i - 1);
        String prevKey = normalize(city + "_" + prevName);

        edges.computeIfAbsent(prevKey, k -> new ArrayList<>())
                .add(new Edge(currentName, lineName, hexColor, 1));
        edges.computeIfAbsent(key, k -> new ArrayList<>())
                .add(new Edge(prevName, lineName, hexColor, 1));
      }
    }
  }

  private void addCityWalkingTransfer(
          Map<String, List<Edge>> edges,
          String city,
          String stationA,
          String stationB,
          String transferLabel,
          String color) {
    String keyA = normalize(city + "_" + stationA);
    String keyB = normalize(city + "_" + stationB);
    edges.computeIfAbsent(keyA, k -> new ArrayList<>())
            .add(new Edge(stationB, transferLabel, color, 1));
    edges.computeIfAbsent(keyB, k -> new ArrayList<>())
            .add(new Edge(stationA, transferLabel, color, 1));
  }

  public static String normalize(String name) {
    if (name == null) return "";
    String s = name.trim().toLowerCase(Locale.ROOT);

    // Convert Roman numeral suffixes (-I, -II, -III, I, II) to digits (1, 2, 3)
    s = s.replaceAll("(?i)(?:[-\\s]+|\\b)iii\\b", " 3")
            .replaceAll("(?i)(?:[-\\s]+|\\b)ii\\b", " 2")
            .replaceAll("(?i)(?:[-\\s]+|\\b)i\\b", " 1");

    // Standardize "phase 1" / "ph-1" -> "1"
    s = s.replaceAll("(?i)\\b(?:phase|ph)[-\\s]*(\\d+)\\b", "$1");

    // Collapse punctuation, hyphens, parentheses, and multiple spaces into single underscores
    s = s.replaceAll("[^a-z0-9]+", "_");
    s = s.replaceAll("^_+|_+$", "");
    return s;
  }
}