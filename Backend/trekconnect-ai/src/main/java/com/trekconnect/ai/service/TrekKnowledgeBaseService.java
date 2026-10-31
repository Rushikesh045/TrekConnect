package com.trekconnect.ai.service;

import org.springframework.stereotype.Service;
import java.util.*;

/**
 * Enterprise Multi-Region Outdoor Trail Directory & Knowledge Base.
 * Supplies verified regional trail facts (Nashik, Pune, Mumbai, Himachal, Uttarakhand, Karnataka)
 * as grounded RAG context for Ollama LLM prompts.
 */
@Service
public class TrekKnowledgeBaseService {

    public static class TrekRecommendation {
        public final String name;
        public final String cityRegion;
        public final String difficulty;
        public final int altitudeFt;
        public final String distance;
        public final String highlights;

        public TrekRecommendation(String name, String cityRegion, String difficulty, int altitudeFt, String distance, String highlights) {
            this.name = name;
            this.cityRegion = cityRegion;
            this.difficulty = difficulty;
            this.altitudeFt = altitudeFt;
            this.distance = distance;
            this.highlights = highlights;
        }
    }

    private final List<TrekRecommendation> trekCatalog = new ArrayList<>();

    public TrekKnowledgeBaseService() {
        initTrekCatalog();
    }

    private void initTrekCatalog() {
        // NASHIK & IGATPURI REGION
        trekCatalog.add(new TrekRecommendation("Harihar Fort Trek", "Nashik, Maharashtra", "Moderate to Hard", 3676, "40 km from Nashik", "Famous for its 80-degree vertical rock-cut cliff staircase carved by the Seuna Yadava dynasty. Offers panoramic vistas of Vaitarna reservoir."));
        trekCatalog.add(new TrekRecommendation("Kalsubai Peak Trek", "Nashik/Igatpuri, Maharashtra", "Moderate", 5400, "65 km from Nashik", "The highest summit in Maharashtra ('Everest of Maharashtra'). Features safe iron ladders over sheer rock faces, sunrise temple at summit, and 360-degree views of Bhandardara."));
        trekCatalog.add(new TrekRecommendation("Anjaneri & Brahmagiri Hills", "Trimbak/Nashik, Maharashtra", "Easy to Moderate", 4264, "25 km from Nashik", "Mythological birthplace of Lord Hanuman and origin point of sacred Godavari river. Dotted with waterfalls, reverse waterfall phenomenon, and ancient caves."));
        trekCatalog.add(new TrekRecommendation("Ratangad Fort", "Bhandardara/Nashik, Maharashtra", "Moderate", 4255, "85 km from Nashik", "Known as the 'Jewel of the Forts' overlooking Arthur Lake, famous for its natural rock needle-hole eye ('Nedhe') and flowering Sonki blooms."));
        trekCatalog.add(new TrekRecommendation("Alang Madan Kulang (AMK)", "Igatpuri/Nashik belt", "Very Hard / Technical", 4852, "55 km from Nashik", "Considered the toughest expedition in the Western Ghats. Involves rock climbing, technical rappelling, and traversing three fortress plateaus."));
        trekCatalog.add(new TrekRecommendation("Salher & Salota Forts", "Baglan, Nashik", "Moderate to Hard", 5141, "110 km from Nashik", "The highest fort in Maharashtra and second-highest peak overall. Site of the legendary 1672 Battle of Salher between Maratha Empire and Mughal forces."));

        // PUNE REGION
        trekCatalog.add(new TrekRecommendation("Rajmachi Fort", "Pune/Lonavala, Maharashtra", "Easy to Moderate", 2710, "75 km from Pune", "Twin citadels of Shrivardhan and Manoranjan, monsoon fireflies sanctuary, and lush valleys overlooking the historic Bor Ghat trade route."));
        trekCatalog.add(new TrekRecommendation("Torna Fort (Prachandagad)", "Pune, Maharashtra", "Hard", 4603, "65 km from Pune", "The highest hill fort in Pune district. First fort captured by Chhatrapati Shivaji Maharaj at age 16. Features the dramatic knife-edge Zunjar Machi ridge."));
        trekCatalog.add(new TrekRecommendation("Sinhagad Fort", "Pune, Maharashtra", "Easy to Moderate", 4304, "30 km from Pune", "Rich historical battle site of Tanaji Malusare. Popular for morning endurance climbs, misty viewpoints, and authentic rural cuisine."));
        trekCatalog.add(new TrekRecommendation("Andharban Dark Forest Trek", "Tamhini Ghat, Pune", "Moderate", 2160, "70 km from Pune", "A primeval descent trail through dense, sunless canopy in Tamhini Ghat with cascading seasonal waterfalls and valley views of Kundalika River."));

        // MUMBAI & THANE REGION
        trekCatalog.add(new TrekRecommendation("Kalavantin Durg & Prabalgad", "Panvel, Maharashtra", "Moderate to Hard", 2300, "50 km from Mumbai", "Iconic pinnacle with stone-carved steps spiraling up the rock spire with sweeping vistas of Matheran and Mumbai skyline."));
        trekCatalog.add(new TrekRecommendation("Kothaligad (Peth Fort)", "Karjat, Maharashtra", "Moderate", 3100, "85 km from Mumbai", "Unique interior spiral rock staircase carved hollow inside a central stone chimney, topped with cannon fortifications."));

        // HIMALAYAS & NORTH INDIA
        trekCatalog.add(new TrekRecommendation("Kedarkantha Winter Summit", "Uttarkashi, Uttarakhand", "Moderate", 12500, "200 km from Dehradun", "India's premier winter snow summit offering a 360-degree panorama of 13 Himalayan peaks including Swargarohini, Bandarpoonch, and Black Peak."));
        trekCatalog.add(new TrekRecommendation("Hampta Pass Trek", "Kullu to Spiti, Himachal Pradesh", "Moderate", 14065, "Base: Manali", "A dramatic crossover trek transitioning from lush alpine pine valleys of Kullu to the stark, lunar cold desert of Spiti Valley."));
        trekCatalog.add(new TrekRecommendation("Triund Trek", "Dharamshala, Himachal Pradesh", "Easy to Moderate", 9350, "Base: McLeod Ganj", "Spectacular ridge walk providing up-close views of the towering snow-covered Dhauladhar ranges on one side and Kangra valley on the other."));
        trekCatalog.add(new TrekRecommendation("Chopta Tungnath & Chandrashila", "Rudraprayag, Uttarakhand", "Moderate", 13123, "Base: Rishikesh", "Hike to the highest Shiva temple in the world (Tungnath) followed by a 1-km summit climb to Chandrashila with views of Nanda Devi."));
        trekCatalog.add(new TrekRecommendation("Valley of Flowers", "Chamoli, Uttarakhand", "Moderate", 14100, "Base: Govindghat", "UNESCO World Heritage alpine floral park with over 500 species of wild Himalayan flowers blooming along the Pushpawati River."));

        // KARNATAKA & SOUTH INDIA
        trekCatalog.add(new TrekRecommendation("Kudremukh Trek", "Chikkamagaluru, Karnataka", "Moderate", 6207, "Base: Kalasa / Bangalore", "Trek across rolling green montane grasslands and shola forest patches inside a national park shaped like a horse's face."));
        trekCatalog.add(new TrekRecommendation("Skandagiri Dawn Trek", "Chikkaballapur, Karnataka", "Moderate", 4757, "60 km from Bangalore", "Night trek to historic hilltop fort ruins to witness the famed 'ocean of clouds' inversion at sunrise."));
        trekCatalog.add(new TrekRecommendation("Tadiandamol Peak", "Coorg, Karnataka", "Moderate", 5735, "Base: Kakkabe / Coorg", "Highest summit in Kodagu district surrounded by aromatic coffee plantations and sweeping Western Ghats ridges."));
    }

    /**
     * Finds recommended treks based on location keywords (e.g. "nashik", "pune", "mumbai", "himachal", "uttarakhand", "bangalore").
     */
    public List<TrekRecommendation> findRecommendedTreks(String query) {
        String q = query.toLowerCase(Locale.ENGLISH);
        List<TrekRecommendation> matches = new ArrayList<>();

        for (TrekRecommendation t : trekCatalog) {
            String fullInfo = (t.name + " " + t.cityRegion + " " + t.distance + " " + t.difficulty + " " + t.highlights).toLowerCase(Locale.ENGLISH);
            
            if (q.contains("nashik") && fullInfo.contains("nashik")) {
                matches.add(t);
            } else if (q.contains("pune") && fullInfo.contains("pune")) {
                matches.add(t);
            } else if (q.contains("mumbai") && (fullInfo.contains("mumbai") || fullInfo.contains("karjat") || fullInfo.contains("panvel") || fullInfo.contains("lonavala"))) {
                matches.add(t);
            } else if ((q.contains("himachal") || q.contains("manali") || q.contains("dharamshala")) && fullInfo.contains("himachal")) {
                matches.add(t);
            } else if ((q.contains("uttarakhand") || q.contains("kedarkantha") || q.contains("dehradun") || q.contains("rishikesh")) && fullInfo.contains("uttarakhand")) {
                matches.add(t);
            } else if ((q.contains("bangalore") || q.contains("karnataka") || q.contains("coorg") || q.contains("kudremukh")) && fullInfo.contains("karnataka")) {
                matches.add(t);
            }
        }

        // If no specific city matched, return top diverse recommendations across regions
        if (matches.isEmpty()) {
            if (q.contains("easy") || q.contains("beginner")) {
                for (TrekRecommendation t : trekCatalog) {
                    if (t.difficulty.toLowerCase().contains("easy")) matches.add(t);
                }
            } else if (q.contains("snow") || q.contains("winter") || q.contains("high altitude")) {
                for (TrekRecommendation t : trekCatalog) {
                    if (t.altitudeFt >= 9000) matches.add(t);
                }
            } else {
                // Return a balanced sample
                matches.add(trekCatalog.get(0)); // Harihar
                matches.add(trekCatalog.get(1)); // Kalsubai
                matches.add(trekCatalog.get(6)); // Rajmachi
                matches.add(trekCatalog.get(12)); // Kedarkantha
                matches.add(trekCatalog.get(17)); // Kudremukh
            }
        }

        return matches;
    }
}
