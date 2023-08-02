package com.solovev.model;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Class represents a bunch of addresses, and bacteria behaviour in the pot
 */
public class PetriDish {
    public static final int MAX_SIZE = 10; //todo out of heap for 12_113 now 6k is max
    private final int size;
    private final Map<Address, Bacteria> addresses = new HashMap<>();
    private long fill;
    private long days;
    private long deadBacteria;
    private final int STERILE_DAYS = 14; // after this age bacteria can not divide
    private final int DEAD_DAYS = 7; // after this age bacteria gets probability to die

    public record Address(int x, int y) { //todo white throws
    }

    /**
     * Class represent response of the calculation
     *
     * @param days         days needed to fill all the pot
     * @param deadBacteria dead bacteria occurred in the process
     */
    public record Response(long days, long deadBacteria) {
    }

    public PetriDish() {
        this(0);
    }

    /**
     * Creates N*N field of empty addresses
     *
     * @param size dimensions of the field, must be from 0 to MAX_SIZE
     */
    public PetriDish(int size) {
        if (size > MAX_SIZE || size < 0) {
            throw new IllegalArgumentException("Size must be > 0 and < " + MAX_SIZE);
        }
        this.size = size;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                addresses.put(new Address(i, j), null);
            }
        }
    }

    /**
     * Calculates days needed to fill all field with bacterias
     *
     * @param conf configuration of the bacteria behavior
     * @return days and dead bacteria needed to fill all pot depending on the conf; -1 if it will never be filled
     */
    public Response calculateDays(ConfigurationOfBacteriaBehavior conf) {
        if (conf.toNumber() == 0) {
            return new Response(-1, 0);
        }
        return new Response(days, deadBacteria);
    }

    /**
     * Puts random number of bacterias to the pot
     *
     * @param conf config to base putting on
     */
    private void putBacterias(ConfigurationOfBacteriaBehavior conf) {
        int numberOfBacterias = new Random().nextInt(conf.fromNumber() - 1, conf.toNumber()) + 1;


    }

    /**
     * Passes the day for this petri dish performing all necessary actions
     * first bacteria divides if not sterile, then it can die or becomes sterile, then new bacterias are put in the dish
     */
    private void dayPass() {
        days++;
        Set<Map.Entry<Address, Bacteria>> notEmptyNotDeadAddresses = addresses //todo this or store in memory?
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() != null
                        && entry.getValue().isAlive())
                .collect(Collectors.toSet());

        for (Map.Entry<Address, Bacteria> entry : notEmptyNotDeadAddresses) {

        }
    }

    /**
     * Goes through EMPTY neighbors of the given cell and fills it with the bacteriaSupplier based on the given supplier
     *
     * @param height           to check neighbors
     * @param width            to check neighbors
     * @param bacteriaSupplier providing bacteria
     */
    private void fillNotEmptyNeighbors(int height, int width, Supplier<Bacteria> bacteriaSupplier) { //todo refactor to the address
        int startHeight = Math.max(0, height - 1);
        int endHeight = Math.min(height + 1, size - 1);
        int startWidth = Math.max(0, width - 1);
        int endWidth = Math.min(width + 1, size - 1);

        for (int i = startHeight; i <= endHeight; i++) {
            for (int j = startWidth; j <= endWidth; j++) {
                if (!(i == height && j == width)) //checks it's not the same address
                {
                    putBacteria(new Address(i, j), bacteriaSupplier.get());
                }
            }
        }
    }

    public int getSize() {
        return size;
    }

    /**
     * gets bacteria of the given x and y
     *
     * @param x first part of address
     * @param y second part of address
     * @return bacteria on this address or null if empty
     */
    public Bacteria getBacteria(int x, int y) {
        return addresses.get(new Address(x, y));
    }

    public Map<Address, Bacteria> getBacterias() {
        return Collections.unmodifiableMap(addresses);
    }

    /**
     * Puts bacteria in the given place if it is not already filled
     *
     * @param address       to put the bacteria
     * @param bacteriaToPut bacteria to put to this place
     * @return true if bacteria was put, false if the place was already not empty
     */
    public boolean putBacteria(Address address, Bacteria bacteriaToPut) {
        if (bacteriaToPut != null
                && addresses.putIfAbsent(address, bacteriaToPut) == null) {
            fill++;
            return true;
        } else {
            return false;
        }
    }

}