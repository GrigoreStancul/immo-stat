package com.gst.immo.credit;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.gst.immo.grabber.Parser;
import com.gst.immo.utils.StringUtils;
import com.opencsv.CSVWriter;


public class CostsCalculator implements Closeable {

   public static Logger _log = Logger.getLogger(Parser.class);

   private CSVWriter    _csvWriter;

   private void calculate() throws IOException {

      _log.info("Start calculation");
      File targetDir = new File("logs");
      if ( !targetDir.exists() ) {
         targetDir.mkdirs();
      }
      _csvWriter = new CSVWriter(new FileWriter(targetDir.getAbsolutePath() + "\\costs.csv"), ';');
      _csvWriter.writeNext(new String[] { "Jahr", "Schuldenstand Vorjahr", "Schuldenstand am Jahresende", "Raten-zahlungen", "Sondern Tilgung", "davon Zinsen",
                                          "davon Tilgung", "davon Zinsen im Jahr", "davon Tilgung im Jahr", "Zinsen %", "Tilgung%" });

      double interestRate = 0.015;
      double initialRepaymentRate = 0.02;
      double specialRepayment = 6000;

      int duration = 15;
      double startSum = 250000;

      _csvWriter.writeNext(new String[] { "interestRate", "Schuldenstand Vorjahr" });

      double restSum;
      restSum = iterateYears(interestRate, initialRepaymentRate, specialRepayment, duration, startSum);
      _csvWriter.writeNext(new String[] {});

      interestRate = 0.10;
      duration = 10;
      startSum = restSum;
      iterateYears(interestRate, initialRepaymentRate, specialRepayment, duration, startSum);

      _log.info("Done.");
   }

   /**
    * @return the rest sum
    */
   private double iterateYears( double interestRate, double initialRepaymentRate, double specialRepayment, int duration, double startSum ) {
      // Calculate payment rate
      double paymentRate = startSum * (initialRepaymentRate + interestRate) / 12;

      for ( int year = 1; year <= duration; year++ ) {
         double interest = startSum * interestRate;
         double payment = paymentRate * 12 + specialRepayment;
         double endSum = startSum + interest - payment;
         _csvWriter.writeNext(new String[] { String.valueOf(year), StringUtils.formatDecimal(startSum), StringUtils.formatDecimal(endSum),
                                             StringUtils.formatDecimal(paymentRate), StringUtils.formatDecimal(specialRepayment),
                                             StringUtils.formatDecimal(interest / 12), StringUtils.formatDecimal((payment - interest) / 12),
                                             StringUtils.formatDecimal(interest), StringUtils.formatDecimal(payment - interest),
                                             StringUtils.formatDecimal(interest / payment), StringUtils.formatDecimal((payment - interest) / payment) });

         startSum = endSum;
      }
      return startSum;
   }

   @Override
   public void close() throws IOException {
      _csvWriter.close();
   }

   public static void main( String[] args ) throws IOException {
      CostsCalculator costsCalculator = new CostsCalculator();
      costsCalculator.calculate();
      costsCalculator.close();
   }

}
