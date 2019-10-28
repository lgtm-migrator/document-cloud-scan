package com.dynamsoft.dwt.common;


/// Image type
public interface EnumDWT_ImageType{
    /// Native Microsoft format.
    int IT_BMP = 0;
    /// JPEG format.
    int IT_JPG = 1;
    /// Tagged Image File Format.
    int IT_TIF = 2;
    /// An image format standard intended for use on the web; replaces GIF.
    int IT_PNG = 3;
    /// A file format from Adobe.
    int IT_PDF = 4;

    int IT_ALL = 5;
	
	
    int IT_MULTIPAGE_PDF = 7;
    int IT_MULTIPAGE_TIF = 8;
	
}
