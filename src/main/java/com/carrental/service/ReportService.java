package com.carrental.service;

import com.carrental.entity.Booking;

import java.io.IOException;
import java.util.List;

/**
 * Service demonstrating Java I/O operations for generating reports and invoices.
 */
public interface ReportService {
    byte[] generateBookingReceipt(Booking booking) throws IOException;
    byte[] generateBookingsCsvReport(List<Booking> bookings) throws IOException;
}
