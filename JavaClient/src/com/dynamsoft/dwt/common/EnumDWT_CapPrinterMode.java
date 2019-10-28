package com.dynamsoft.dwt.common;

/// CAP_PRINTERMODE values.
public interface EnumDWT_CapPrinterMode{
  /// Specifies that the printed text will consist of a single string.
  int TWPM_SINGLESTRING = 0;
  /// Specifies that the printed text will consist of an enumerated list of strings to be printed in order.
  int TWPM_MULTISTRING = 1;
  /// Specifies that the printed string will consist of a compound of a String followed by a value followed by a suffix string.
  int TWPM_COMPOUNDSTRING = 2;
}