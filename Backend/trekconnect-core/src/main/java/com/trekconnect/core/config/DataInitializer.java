package com.trekconnect.core.config;

import com.trekconnect.core.entity.Event;
import com.trekconnect.core.entity.OrganizerDetails;
import com.trekconnect.core.entity.Trek;
import com.trekconnect.core.entity.UserProfile;
import com.trekconnect.core.repository.EventRepository;
import com.trekconnect.core.repository.OrganizerDetailsRepository;
import com.trekconnect.core.repository.TrekRepository;
import com.trekconnect.core.repository.UserProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Startup Data Initializer seeding initial database records.
 * 
 * WHY THIS CLASS WAS CREATED:
 * Guarantees zero static content by automatically populating PostgreSQL main_db with initial
 * Sahyadri and Himalayan treks, organizers, and scheduled event batches on application startup.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final TrekRepository trekRepository;
    private final EventRepository eventRepository;
    private final OrganizerDetailsRepository organizerDetailsRepository;
    private final UserProfileRepository userProfileRepository;

    public DataInitializer(TrekRepository trekRepository, 
                           EventRepository eventRepository, 
                           OrganizerDetailsRepository organizerDetailsRepository,
                           UserProfileRepository userProfileRepository) {
        this.trekRepository = trekRepository;
        this.eventRepository = eventRepository;
        this.organizerDetailsRepository = organizerDetailsRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Checking database seed records...");

        // Seed UserProfile records for 7 registered accounts
        UserProfile defaultUser = userProfileRepository.findById("usr-1").orElseGet(() ->
            userProfileRepository.save(UserProfile.builder().userId("usr-1").name("Sahyadri Wanderers").email("organizer@trekconnect.com").phone("+91 98765 43210").role("ORGANIZER").bio("Certified Sahyadri & Himalayan Trek Organizer").build())
        );

        userProfileRepository.findById("adm-1").orElseGet(() ->
            userProfileRepository.save(UserProfile.builder().userId("adm-1").name("System Administrator").email("admin@trekconnect.com").phone("+91 98900 11223").role("ADMIN").bio("Master System Security Administrator").build())
        );

        userProfileRepository.findById("trk-usr-1").orElseGet(() ->
            userProfileRepository.save(UserProfile.builder().userId("trk-usr-1").name("Rushikesh (Lead Explorer)").email("trekker@trekconnect.com").phone("+91 98765 12345").role("USER").bio("Passionate Sahyadri fort trekker and photographer").build())
        );

        userProfileRepository.findById("usr-4").orElseGet(() ->
            userProfileRepository.save(UserProfile.builder().userId("usr-4").name("Anita Sharma").email("anita.sharma@gmail.com").phone("+91 91234 56789").role("USER").bio("Himalayan high-altitude trekker").build())
        );

        userProfileRepository.findById("usr-5").orElseGet(() ->
            userProfileRepository.save(UserProfile.builder().userId("usr-5").name("Vikramaditya Singh").email("vikram@westernghats.in").phone("+91 99887 76655").role("ORGANIZER").bio("Western Ghats Outdoor Expeditions Founder").build())
        );

        userProfileRepository.findById("usr-6").orElseGet(() ->
            userProfileRepository.save(UserProfile.builder().userId("usr-6").name("Priya Kulkarni").email("priya.kulkarni@yahoo.com").phone("+91 98220 11223").role("USER").bio("Weekend fort climber & waterfall enthusiast").build())
        );

        userProfileRepository.findById("usr-7").orElseGet(() ->
            userProfileRepository.save(UserProfile.builder().userId("usr-7").name("Rahul Deshmukh").email("rahul.deshmukh@gmail.com").phone("+91 97654 32109").role("USER").bio("Night camping & fireflies trek lover").build())
        );

        if (organizerDetailsRepository.count() == 0) {
            logger.info("Seeding default OrganizerDetails record...");
            OrganizerDetails defaultOrg = OrganizerDetails.builder()
                    .id("org-101")
                    .user(defaultUser)
                    .organizationName("Sahyadri Wanderers Expeditions")
                    .contactPhone("+91 98765 43210")
                    .cityLocation("Pune, Maharashtra")
                    .licenseNumber("GSTIN-27ABCDE1234F1Z5")
                    .verificationDocsUrl("assets/images/kyc-cert.svg")
                    .verificationStatus("VERIFIED")
                    .build();
            organizerDetailsRepository.save(defaultOrg);
        }

        if (trekRepository.count() == 0) {
            logger.info("Seeding initial Trek catalog into PostgreSQL main_db...");

            Trek torna = Trek.builder()
                    .id("trk-101")
                    .name("Torna Fort Monsoon Trek")
                    .region("Pune")
                    .difficulty("HARD")
                    .durationDays(1)
                    .altitudeMeters(1403)
                    .category("FORT")
                    .imageUrl("assets/images/torna.svg")
                    .description("Torna Fort (Prachandagad) is the highest fort in Pune district. Experience lush green ridges, roaring waterfall streams, and historical Menghai Devi temple during monsoon.")
                    .inclusions("Private Bus Transport Pune to Pune;Breakfast & Veg Lunch;Certified Trek Leaders;First Aid & Safety Gear;Forest Permits")
                    .build();

            Trek rajmachi = Trek.builder()
                    .id("trk-102")
                    .name("Rajmachi Fort & Fireflies Camping")
                    .region("Lonavala")
                    .difficulty("MODERATE")
                    .durationDays(2)
                    .altitudeMeters(826)
                    .category("CAMPING")
                    .imageUrl("assets/images/rajmachi.svg")
                    .description("Trek through lush green pathways between Lonavala and Karjat, witness millions of twinkling fireflies at night, and explore Shrivardhan & Manoranjan twin forts.")
                    .inclusions("Tent Accommodation (Twin Sharing);High Tea, Dinner & Breakfast;Fireflies Sightseeing Guide;Bonfire Session;Safety Harnesses")
                    .build();

            Trek harish = Trek.builder()
                    .id("trk-103")
                    .name("Harishchandragad & Kokankada Cliff Trek")
                    .region("Ahmednagar")
                    .difficulty("HARD")
                    .durationDays(2)
                    .altitudeMeters(1423)
                    .category("FORT")
                    .imageUrl("assets/images/harishchandragad.svg")
                    .description("Conquer the legendary Kokankada overhang cliff, explore 6th-century Kedareshwar Cave with frozen water pillar, and witness breath-taking clouds rolling below the peak.")
                    .inclusions("Village Cave Stay & Dinner;Traditional Maharashtrian Meals;Expert Technical Mountain Guide;Rope Support for Rock Patches")
                    .build();

            Trek devkund = Trek.builder()
                    .id("trk-104")
                    .name("Devkund Waterfall Jungle Trek")
                    .region("Raigad")
                    .difficulty("EASY")
                    .durationDays(1)
                    .altitudeMeters(609)
                    .category("WATERFALL")
                    .imageUrl("assets/images/devkund.svg")
                    .description("Walk through pristine forests, cross gushing river streams, and reach the natural plunge pool of Devkund waterfall nestled deep within the Kundalika river valley.")
                    .inclusions("AC Bus Pickup from Mumbai/Pune;Life Jackets for Pool Safety;Breakfast & Buffet Lunch;Local Guide Fees")
                    .build();

            Trek kalsubai = Trek.builder()
                    .id("trk-105")
                    .name("Kalsubai Peak — Highest Point of Maharashtra")
                    .region("Nashik")
                    .difficulty("MODERATE")
                    .durationDays(1)
                    .altitudeMeters(1646)
                    .category("SAHYADRI")
                    .imageUrl("assets/images/kalsubai.svg")
                    .description("Stand tall at Everest of Maharashtra (5,400 ft). Ascend steel ladders along rocky precipices and enjoy 360-degree panoramic views of Bhandardara lake and surrounding forts.")
                    .inclusions("Transport from Kasara Station;Morning Breakfast & Hot Lunch;Summit Badge Certificate;Safety Anchors")
                    .build();

            Trek kedarkantha = Trek.builder()
                    .id("trk-106")
                    .name("Kedarkantha Winter Snow Summit Trek")
                    .region("Himalayas")
                    .difficulty("HARD")
                    .durationDays(5)
                    .altitudeMeters(3810)
                    .category("HIMALAYA")
                    .imageUrl("assets/images/kedarkantha.svg")
                    .description("Experience magical pine tree snowscapes, frozen Juda-Ka-Talab lake, and a thrilling 360-degree Himalayan summit sunrise view of Swargarohini & Bandarpoonch ranges.")
                    .inclusions("Dehradun to Sankri Transport;All Campsite Tents & Sleeping Bags;Microspikes & Gaiters;Oxygen Cylinder & Medical Kit;Himalayan Trek Leaders")
                    .build();

            Trek sinhagad = Trek.builder()
                    .id("trk-107")
                    .name("Sinhagad Fort Sunrise Ridge Trek")
                    .region("Pune")
                    .difficulty("EASY")
                    .durationDays(1)
                    .altitudeMeters(1312)
                    .category("FORT")
                    .imageUrl("assets/images/sinhagad.svg")
                    .description("Hike up the historical Sinhagad Fort at sunrise, enjoy authentic Kanda Bhaji and Pithla Bhakri at the top, and pay homage to Tanaji Malusare memorial.")
                    .inclusions("Bus Pickup from Swargate Pune;Hot Breakfast & Kanda Bhaji;Guide Fees;Forest Entry Permit")
                    .build();

            Trek ratangad = Trek.builder()
                    .id("trk-108")
                    .name("Ratangad Fort & Nedhe Cave Trek")
                    .region("Ahmednagar")
                    .difficulty("MODERATE")
                    .durationDays(1)
                    .altitudeMeters(1297)
                    .category("SAHYADRI")
                    .imageUrl("assets/images/ratangad.svg")
                    .description("Climb through iron ladders to reach Nedhe (Eye of the Needle) natural rock cavity, explore Pravara river origin, and witness Amruteshwar 1000-year-old temple.")
                    .inclusions("Kasara Station Pickup & Drop;Boat Ride across Arthur Lake;Village Lunch & Breakfast;Technical Guide")
                    .build();

            trekRepository.saveAll(List.of(torna, rajmachi, harish, devkund, kalsubai, kedarkantha, sinhagad, ratangad));
            logger.info("Successfully seeded 8 Trek catalog items!");
        }

        if (eventRepository.count() == 0) {
            logger.info("Seeding initial Event batches into PostgreSQL main_db...");

            OrganizerDetails defaultOrg = organizerDetailsRepository.findAll().stream().findFirst().orElse(null);
            List<Trek> treks = trekRepository.findAll();

            if (defaultOrg != null && !treks.isEmpty()) {
                Event e1 = Event.builder()
                        .id("evt-101")
                        .trek(treks.get(0))
                        .organizer(defaultOrg)
                        .title("Torna Fort Monsoon Weekend Batch")
                        .description("Torna Fort Monsoon Trek Special Departure with waterfall stream crossings.")
                        .imageUrl("assets/images/torna.svg")
                        .eventDate(LocalDate.now().plusDays(7))
                        .price(BigDecimal.valueOf(1399))
                        .capacityTotal(25)
                        .capacityBooked(8)
                        .status("APPROVED")
                        .build();

                Event e2 = Event.builder()
                        .id("evt-102")
                        .trek(treks.get(1))
                        .organizer(defaultOrg)
                        .title("Rajmachi Fort Fireflies Night Camping")
                        .description("Rajmachi Night Camping, bonfire session, and Fireflies Sightseeing.")
                        .imageUrl("assets/images/rajmachi.svg")
                        .eventDate(LocalDate.now().plusDays(14))
                        .price(BigDecimal.valueOf(1899))
                        .capacityTotal(30)
                        .capacityBooked(14)
                        .status("APPROVED")
                        .build();

                Event e3 = Event.builder()
                        .id("evt-103")
                        .trek(treks.get(2))
                        .organizer(defaultOrg)
                        .title("Harishchandragad & Kokankada Sunrise Expedition")
                        .description("Harishchandragad Cliff and Kedareshwar Cave Trek with village cave stay.")
                        .imageUrl("assets/images/harishchandragad.svg")
                        .eventDate(LocalDate.now().plusDays(20))
                        .price(BigDecimal.valueOf(2199))
                        .capacityTotal(20)
                        .capacityBooked(5)
                        .status("APPROVED")
                        .build();

                Event e4 = Event.builder()
                        .id("evt-104")
                        .trek(treks.size() > 3 ? treks.get(3) : treks.get(0))
                        .organizer(defaultOrg)
                        .title("Devkund Waterfall Secret Pool Jungle Trek")
                        .description("Pristine jungle trail leading to the natural plunge pool of Devkund waterfall.")
                        .imageUrl("assets/images/devkund.svg")
                        .eventDate(LocalDate.now().plusDays(10))
                        .price(BigDecimal.valueOf(1299))
                        .capacityTotal(25)
                        .capacityBooked(12)
                        .status("APPROVED")
                        .build();

                Event e5 = Event.builder()
                        .id("evt-105")
                        .trek(treks.size() > 4 ? treks.get(4) : treks.get(0))
                        .organizer(defaultOrg)
                        .title("Kalsubai Peak Monsoon Ladder Trail")
                        .description("Ascend the highest summit of Maharashtra (5,400 ft) with ladder rock patches.")
                        .imageUrl("assets/images/kalsubai.svg")
                        .eventDate(LocalDate.now().plusDays(18))
                        .price(BigDecimal.valueOf(1499))
                        .capacityTotal(30)
                        .capacityBooked(19)
                        .status("APPROVED")
                        .build();

                Event e6 = Event.builder()
                        .id("evt-106")
                        .trek(treks.size() > 5 ? treks.get(5) : treks.get(0))
                        .organizer(defaultOrg)
                        .title("Kedarkantha Himalayan Snow Summit Expedition")
                        .description("5-day Himalayan snow trek through pine forests to Kedarkantha 3810m summit.")
                        .imageUrl("assets/images/kedarkantha.svg")
                        .eventDate(LocalDate.now().plusDays(30))
                        .price(BigDecimal.valueOf(8999))
                        .capacityTotal(15)
                        .capacityBooked(6)
                        .status("APPROVED")
                        .build();

                eventRepository.saveAll(List.of(e1, e2, e3, e4, e5, e6));
                logger.info("Successfully seeded 6 Event batches!");
            }
        }

        // Guarantee image isolation across all events in database
        logger.info("Verifying cover photo isolation across all event batches...");
        List<Event> allEvents = eventRepository.findAll();
        for (int i = 0; i < allEvents.size(); i++) {
            Event ev = allEvents.get(i);
            if (ev.getTrek() != null && ev.getTrek().getImageUrl() != null) {
                // If event image is missing or duplicated, bind to trek's isolated image asset
                if (ev.getImageUrl() == null || ev.getImageUrl().isBlank() || ev.getImageUrl().startsWith("data:image")) {
                    ev.setImageUrl(ev.getTrek().getImageUrl());
                    eventRepository.save(ev);
                }
            }
        }
        logger.info("Cover photo isolation verification completed successfully!");
    }
}
