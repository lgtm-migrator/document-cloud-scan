package com.dynamsoft.dwt.common;

/// ICAP_IMAGEFILTER values.
public interface EnumDWT_CapImageFilter{
int TWIF_NONE = 0;
int TWIF_AUTO = 1;
/// Good for halftone images.
int TWIF_LOWPASS = 2;
/// Good for improving text.
int TWIF_BANDPASS = 3;
/// Good for improving fine lines.
int TWIF_HIGHPASS = 4;
int TWIF_TEXT = 3;
int TWIF_FINELINE = 4;
}