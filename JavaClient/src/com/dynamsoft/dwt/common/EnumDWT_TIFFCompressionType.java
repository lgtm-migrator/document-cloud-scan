package com.dynamsoft.dwt.common;

/// TIFF file compression type.
public interface EnumDWT_TIFFCompressionType{
    /// Auto mode.
    int TIFF_AUTO = 0;
    /// Dump mode.
    int TIFF_NONE = 1;
    /// CCITT modified Huffman RLE.
    int TIFF_RLE = 2;
    /// CCITT Group 3 fax encoding.
    int TIFF_FAX3 = 3;
    /// CCITT T.4 (TIFF 6 name).
    int TIFF_T4 = 3;
    /// CCITT Group 4 fax encoding
    int TIFF_FAX4 = 4;
    /// CCITT T.6 (TIFF 6 name).
    int TIFF_T6 = 4;
    /// Lempel Ziv and Welch
    int TIFF_LZW = 5;
    int TIFF_JPEG = 7;
    int TIFF_PACKBITS = 32773;
}
