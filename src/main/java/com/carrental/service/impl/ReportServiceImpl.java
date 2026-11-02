package com.carrental.service.impl;

import com.carrental.entity.Booking;
import com.carrental.service.ReportService;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ReportServiceImpl implements Java I/O operations.
 * Demonstrates streams, writers, buffers, and character encodings.
 */
@Service
public class ReportServiceImpl implements ReportService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

    @Override
    public byte[] generateBookingReceipt(Booking booking) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8))) {
            writer.write("=================================================================\n");
            writer.write("               CAR RENTAL SYSTEM - OFFICIAL RECEIPT              \n");
            writer.write("=================================================================\n\n");
            writer.write(String.format("Receipt Ref No   : %s\n", booking.getBookingNumber()));
            writer.write(String.format("Generated On     : %s\n", java.time.LocalDateTime.now().format(TIME_FMT)));
            writer.write(String.format("Booking Status   : %s\n\n", booking.getStatus()));

            writer.write("------------------- CUSTOMER DETAILS ----------------------------\n");
            writer.write(String.format("Name             : %s\n", booking.getUser().getName()));
            writer.write(String.format("Email            : %s\n", booking.getUser().getEmail()));
            writer.write(String.format("Phone            : %s\n\n", booking.getUser().getPhone()));

            writer.write("-------------------- VEHICLE DETAILS ----------------------------\n");
            writer.write(String.format("Vehicle          : %s\n", booking.getCar().getFullName()));
            writer.write(String.format("Category         : %s\n", booking.getCar().getCategory()));
            writer.write(String.format("License Plate    : %s\n", booking.getCar().getLicensePlate()));
            writer.write(String.format("Fuel / Trans.    : %s / %s\n\n", 
                    booking.getCar().getFuelType(), booking.getCar().getTransmission()));

            writer.write("-------------------- RENTAL DATES & CHARGES ---------------------\n");
            writer.write(String.format("Pickup Date      : %s\n", booking.getPickupDate().format(DATE_FMT)));
            writer.write(String.format("Return Date      : %s\n", booking.getReturnDate().format(DATE_FMT)));
            writer.write(String.format("Rental Duration  : %d Days\n", booking.getTotalDays()));
            writer.write(String.format("Daily Rate       : $%.2f / day\n", booking.getDailyRate()));
            writer.write("-----------------------------------------------------------------\n");
            writer.write(String.format("TOTAL CHARGED    : $%.2f\n", booking.getTotalPrice()));
            writer.write("=================================================================\n");
            writer.write(" Thank you for choosing Car Rental System! Drive Safely. \n");
            writer.write("=================================================================\n");
            writer.flush();
        }
        return baos.toByteArray();
    }

    @Override
    public byte[] generateBookingsCsvReport(List<Booking> bookings) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8))) {
            // CSV Header
            writer.write("Booking ID,Booking Number,Customer Name,Customer Email,Vehicle,Category,Pickup Date,Return Date,Duration (Days),Daily Rate ($),Total Price ($),Status\n");

            // CSV Rows
            for (Booking b : bookings) {
                writer.write(String.format("\"%d\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%d\",\"%.2f\",\"%.2f\",\"%s\"\n",
                        b.getId(),
                        b.getBookingNumber(),
                        b.getUser().getName().replace("\"", "\"\""),
                        b.getUser().getEmail().replace("\"", "\"\""),
                        b.getCar().getFullName().replace("\"", "\"\""),
                        b.getCar().getCategory(),
                        b.getPickupDate(),
                        b.getReturnDate(),
                        b.getTotalDays(),
                        b.getDailyRate(),
                        b.getTotalPrice(),
                        b.getStatus()
                ));
            }
            writer.flush();
        }
        return baos.toByteArray();
    }
}
