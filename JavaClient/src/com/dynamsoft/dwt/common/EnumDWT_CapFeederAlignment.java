package com.dynamsoft.dwt.common;


/// CAP_FEEDERALIGNMENT values.
public interface EnumDWT_CapFeederAlignment{
  /// The alignment is free-floating. Applications should assume that the origin for frames is on the left.
  int TWFA_NONE = 0;
  /// The alignment is to the left.
  int TWFA_LEFT = 1;
  /// The alignment is centered. This means that the paper will be fed in the middle of the ICAP_PHYSICALWIDTH of the 
  /// device. If this is set; then the Application should calculate any frames with a left offset of zero.
  int TWFA_CENTER = 2;
  /// The alignment is to the right.
  int TWFA_RIGHT = 3;
}