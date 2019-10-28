package com.dynamsoft.dwt.common;


/// ICAP_XFERMECH values.
public interface EnumDWT_TransferMode{
  /// Native transfers require the data to be transferred to a single large block of RAM. Therefore;
  /// they always face the risk of having an inadequate amount of RAM available to perform the transfer successfully.
  int TWSX_NATIVE = 0;
  /// Disk File Mode Transfers.
  int TWSX_FILE = 1;
  /// Buffered Memory Mode Transfers.
  int TWSX_MEMORY = 2;
}
