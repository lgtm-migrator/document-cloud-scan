package com.dynamsoft.dwt.common;


/// For query the operation that are supported by the data source on a capability .
/// Application gets these through DG_CONTROL/DAT_CAPABILITY/MSG_QUERYSUPPORT
public interface EnumDWT_MessageType{
  int TWQC_GET = 1;
  int TWQC_SET = 2;
  int TWQC_GETDEFAULT = 4;
  int TWQC_GETCURRENT = 8;
  int TWQC_RESET = 16;
}
