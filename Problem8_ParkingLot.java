import java.util.*;

/**
 * Problem 8: Parking Lot Management with Open Addressing
 * Concepts: Open addressing (linear probing), collision resolution, custom hash functions, load factor
 */
public class Problem8_ParkingLot {

    private enum SpotStatus { EMPTY, OCCUPIED, DELETED }

    private static class ParkingSpot {
        SpotStatus status = SpotStatus.EMPTY;
        String licensePlate;
        long entryTime;

        ParkingSpot() {}
    }

    private ParkingSpot[] spots;
    private final int SIZE;
    private int occupiedCount = 0;
    private int totalProbes = 0;
    private int parkingOps = 0;
    private HashMap<Integer, Integer> hourlyCount = new HashMap<>();

    public Problem8_ParkingLot(int size) {
        this.SIZE = size;
        this.spots = new ParkingSpot[size];
        for (int i = 0; i < size; i++) spots[i] = new ParkingSpot();
    }

    // Custom hash function for license plate
    private int hashLicensePlate(String plate) {
        int hash = 0;
        for (char c : plate.toCharArray()) {
            hash = (hash * 31 + c) % SIZE;
        }
        return Math.abs(hash);
    }

    public String parkVehicle(String licensePlate) {
        if (occupiedCount >= SIZE) return "Parking lot is full";

        int preferred = hashLicensePlate(licensePlate);
        int probes = 0;
        int idx = preferred;

        // Linear probing
        while (spots[idx].status == SpotStatus.OCCUPIED) {
            idx = (idx + 1) % SIZE;
            probes++;
        }

        spots[idx].status = SpotStatus.OCCUPIED;
        spots[idx].licensePlate = licensePlate;
        spots[idx].entryTime = System.currentTimeMillis();
        occupiedCount++;
        totalProbes += probes;
        parkingOps++;

        // Track hourly (simulated)
        int hour = (int)(System.currentTimeMillis() / 3600000) % 24;
        hourlyCount.merge(hour, 1, Integer::sum);

        return "Assigned spot #" + idx + " (" + probes + " probes)";
    }

    public String exitVehicle(String licensePlate) {
        for (int i = 0; i < SIZE; i++) {
            if (spots[i].status == SpotStatus.OCCUPIED &&
                    licensePlate.equals(spots[i].licensePlate)) {

                long duration = System.currentTimeMillis() - spots[i].entryTime;
                double hours = duration / 3600000.0;
                double fee = Math.max(hours * 5.0, 2.50); // $5/hr, $2.50 minimum
                int spotNum = i;

                spots[i].status = SpotStatus.DELETED; // Mark as deleted (not empty) for probing
                spots[i].licensePlate = null;
                occupiedCount--;

                return String.format("Spot #%d freed, Duration: %.0fm, Fee: $%.2f",
                        spotNum, hours * 60, fee);
            }
        }
        return "Vehicle not found";
    }

    public void getStatistics() {
        double occupancy = (occupiedCount * 100.0) / SIZE;
        double avgProbes = parkingOps > 0 ? (totalProbes * 1.0 / parkingOps) : 0;

        int peakHour = hourlyCount.isEmpty() ? -1 :
                Collections.max(hourlyCount.entrySet(), Map.Entry.comparingByValue()).getKey();

        System.out.printf("Occupancy: %.0f%%, Avg Probes: %.1f, Spots Used: %d/%d%n",
                occupancy, avgProbes, occupiedCount, SIZE);
    }

    public static void main(String[] args) throws InterruptedException {
        Problem8_ParkingLot lot = new Problem8_ParkingLot(500);

        System.out.println("=== Problem 8: Parking Lot with Open Addressing ===");
        System.out.println(lot.parkVehicle("ABC-1234"));
        System.out.println(lot.parkVehicle("ABC-1235"));
        System.out.println(lot.parkVehicle("XYZ-9999"));

        Thread.sleep(100); // simulate duration
        System.out.println(lot.exitVehicle("ABC-1234"));

        System.out.print("getStatistics() → ");
        lot.getStatistics();
    }
}
