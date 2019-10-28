package com.dynamsoft.dwt.common;


/// Capabilities
public interface EnumDWT_Cap {
  /// Nothing.
  int CAP_NONE = 0;
  /// The application is willing to accept this number of images.
  int CAP_XFERCOUNT = 1;
  /// Allows the application and Source to identify which compression schemes they have in
  /// common for Buffered Memory and File transfers.
  /// Note for File transfers:
  /// Since only certain file formats support compression; this capability must be negotiated after
  /// setting the desired file format with ICAP_IMAGEFILEFORMAT.
  int ICAP_COMPRESSION = 256;
  /// The type of pixel data that a Source is capable of acquiring (for example; black and white; gray; RGB; etc.).
  int ICAP_PIXELTYPE = 257;
  /// Unless a quantity is dimensionless or uses a specified unit of measure; ICAP_UNITS determines
  /// the unit of measure for all quantities.
  int ICAP_UNITS = 258;
  /// Allows the application and Source to identify which transfer mechanisms the source supports.
  int ICAP_XFERMECH = 259;
  /// The name or other identifying information about the Author of the image. It may include a copyright string.
  int CAP_AUTHOR = 4096;
  /// A general note about the acquired image.
  int CAP_CAPTION = 4097;
  /// If TRUE; Source must acquire data from the document feeder acquire area and other feeder 
  /// capabilities can be used. If FALSE; Source must acquire data from the non-feeder acquire area 
  /// and no other feeder capabilities can be used.
  int CAP_FEEDERENABLED = 4098;
  /// Reflect whether there are documents loaded in the Source's feeder.
  int CAP_FEEDERLOADED = 4099;
  /// The date and time the image was acquired.
  /// Stored in the form "YYYY/MM/DD HH:mm:SS.sss" where YYYY is the year; MM is the 
  /// numerical month; DD is the numerical day; HH is the hour; mm is the minute; SS is the second; 
  /// and sss is the millisecond.
  int CAP_TIMEDATE = 4100;
  /// Returns a list of all the capabilities for which the Source will answer inquiries. Does not indicate
  /// which capabilities the Source will allow to be set by the application. Some capabilities can only
  /// be set if certain setup work has been done so the Source cannot globally answer which
  /// capabilities are "set-able."
  int CAP_SUPPORTEDCAPS = 4101;
  /// Allows the application and Source to negotiate capabilities to be used in States 5 and 6.
  int CAP_EXTENDEDCAPS = 4102;
  /// If TRUE; the Source will automatically feed the next page from the document feeder after the
  /// number of frames negotiated for capture from each page are acquired. CAP_FEEDERENABLED
  /// must be TRUE to use this capability.
  int CAP_AUTOFEED = 4103;
  /// If TRUE; the Source will eject the current page being acquired from and will leave the feeder
  /// acquire area empty.
  /// If CAP_AUTOFEED is TRUE; a fresh page will be advanced.
  /// CAP_FEEDERENABLED must equal TRUE to use this capability.
  /// This capability must have been negotiated as an extended capability to be used in States 5 and 6.
  int CAP_CLEARPAGE = 4104;
  /// If TRUE; the Source will eject the current page and advance the next page in the document feeder
  /// into the feeder acquire area.
  /// If CAP_AUTOFEED is TRUE; the same action just described will occur and CAP_AUTOFEED will
  /// remain active.
  /// CAP_FEEDERENABLED must equal TRUE to use this capability.
  /// This capability must have been negotiated as an extended capability to be used in States 5 and 6.
  int CAP_FEEDPAGE = 4105;
  /// If TRUE; the Source will return the current page to the input side of the document feeder and
  /// feed the last page from the output side of the feeder back into the acquisition area.
  /// If CAP_AUTOFEED is TRUE; automatic feeding will continue after all negotiated frames from this
  /// page are acquired.
  /// CAP_FEEDERENABLED must equal TRUE to use this capability.
  /// This capability must have been negotiated as an extended capability to be used in States 5 and 6.
  int CAP_REWINDPAGE = 4106;
  /// If TRUE; the Source will display a progress indicator during acquisition and transfer; regardless
  /// of whether the Source's user interface is active. If FALSE; the progress indicator will be
  /// suppressed if the Source's user interface is inactive.
  /// The Source will continue to display device-specific instructions and error messages even with
  /// the Source user interface and progress indicators turned off.
  int CAP_INDICATORS = 4107;
  /// Returns a list of all the capabilities for which the Source will answer inquiries. Does not indicate
  /// which capabilities the Source will allow to be set by the application. Some capabilities can only
  /// be set if certain setup work has been done so the Source cannot globally answer which
  /// capabilities are "set-able."
  int CAP_SUPPORTEDCAPSEXT = 4108;
  /// This capability determines whether the device has a paper sensor that can detect documents on the ADF or Flatbed.
  int CAP_PAPERDETECTABLE = 4109;
  /// If TRUE; indicates that this Source supports acquisition with the UI disabled; i.e.;
  /// TW_USERINTERFACE's ShowUI field can be set to FALSE. If FALSE; indicates that this Source
  /// can only support acquisition with the UI enabled.
  int CAP_UICONTROLLABLE = 4110;
  /// If TRUE; the physical hardware (e.g.; scanner; digital camera; image database; etc.) that
  /// represents the image source is attached; powered on; and communicating.
  int CAP_DEVICEONLINE = 4111;
  /// This capability is intended to boost the performance of a Source. The fundamental assumption
  /// behind AutoScan is that the device is able to capture the number of images indicated by the
  /// value of CAP_XFERCOUNT without waiting for the Application to request the image transfers.
  /// This is only possible if the device has internal buffers capable of caching the images it captures.
  /// The default behavior is undefined; because some high volume devices are incapable of anything
  /// but CAP_AUTOSCAN being equal to TRUE. However; if a Source supports FALSE; it should use it
  /// as the mandatory default; since this best describes the behavior of pre-1.8 TWAIN Applications.
  int CAP_AUTOSCAN = 4112;
  /// Allows an application to request the delivery of thumbnail representations for the set of images
  /// that are to be delivered.
  /// Setting CAP_THUMBNAILSENABLED to TRUE turns on thumbnail mode. Images transferred
  /// thereafter will be sent at thumbnail size (exact thumbnail size is determined by the Data Source).
  /// Setting this capability to FALSE turns thumbnail mode off and returns full size images.
  int CAP_THUMBNAILSENABLED = 4113;
  /// This indicates whether the scanner supports duplex. If so; it further indicates whether one-path
  /// or two-path duplex is supported.
  int CAP_DUPLEX = 4114;
  /// The user can set the duplex option to be TRUE or FALSE. If TRUE; the scanner scans both sides
  /// of a paper; otherwise; the scanner will scan only one side of the image.
  int CAP_DUPLEXENABLED = 4115;
  /// Allows an application to query a source to see if it implements the new user interface settings dialog. 
  int CAP_ENABLEDSUIONLY = 4116;
  int CAP_CUSTOMDSDATA = 4117;
  /// Allows the application to specify the starting endorser / imprinter number. All other endorser/
  /// imprinter properties should be handled through the data source's user interface.
  /// The user can set the starting number for the endorser.
  int CAP_ENDORSER = 4118;
  /// Turns specific audible alarms on and off.
  int CAP_ALARMS = 4120;
  /// The volume of a device's audible alarm. Note that this control affects the volume of all alarms;
  /// no specific volume control for individual types of alarms is provided.
  int CAP_ALARMVOLUME = 4121;
  /// The number of images to automatically capture. This does not refer to the number of images to
  /// be sent to the Application; use CAP_XFERCOUNT for that.
  int CAP_AUTOMATICCAPTURE = 4122;
  /// For automatic capture; this value selects the number of milliseconds before the first picture is to
  /// be taken; or the first image is to be scanned.
  int CAP_TIMEBEFOREFIRSTCAPTURE = 4123;
  /// For automatic capture; this value selects the milliseconds to wait between pictures taken; or images scanned.
  int CAP_TIMEBETWEENCAPTURES = 4124;
  /// CapGet() reports the presence of data in the scanner's buffers. CapSet() with a value of TWCB_CLEAR immediately clears the buffers.
  int CAP_CLEARBUFFERS = 4125;
  /// Describes the number of pages that the scanner can buffer when CAP_AUTOSCAN is enabled.
  int CAP_MAXBATCHBUFFERS = 4126;
  /// The date and time of the device's clock.
  /// Managed in the form "YYYY/MM/DD HH:mm:SS:sss" where YYYY is the year; MM is the
  /// numerical month; DD is the numerical day; HH is the hour; mm is the minute; SS is the second;
  /// and sss is the millisecond.
  int CAP_DEVICETIMEDATE = 4127;
  /// CapGet() reports the kinds of power available to the device. CapGetCurrent() reports the current power supply in use.
  int CAP_POWERSUPPLY = 4128;
  /// This capability queries the Source for UI support for preview mode. If TRUE; the Source supports preview UI.
  int CAP_CAMERAPREVIEWUI = 4129;
  /// A string containing the serial number of the currently selected device in the Source. Multiple
  /// devices may all report the same serial number.
  int CAP_SERIALNUMBER = 4132;
  /// CapGet() returns the current list of available printer devices; along with the one currently being used for negotiation. 
  /// CapSet() selects the current device for negotiation; and optionally constrains the list.
  /// Top/Bottom refers to duplex devices; and indicates if the printer is writing on the top or the bottom of the sheet of paper. 
  /// Simplex devices use the top settings. Before/After indicates whether printing occurs before or after the sheet of paper has been scanned.
  int CAP_PRINTER = 4134;
  /// Turns the current CAP_PRINTER device on or off.
  int CAP_PRINTERENABLED = 4135;
  /// The User can set the starting number for the current CAP_PRINTER device.
  int CAP_PRINTERINDEX = 4136;
  /// Specifies the appropriate current CAP_PRINTER device mode.
  /// Note:
  /// â€? TWPM_SINGLESTRING specifies that the printed text will consist of a single string.
  /// â€? TWPM _MULTISTRING specifies that the printed text will consist of an enumerated list of
  /// strings to be printed in order.
  /// â€? TWPM _COMPOUNDSTRING specifies that the printed string will consist of a compound of a
  /// String followed by a value followed by a suffix string.
  int CAP_PRINTERMODE = 4137;
  /// Specifies the string(s) that are to be used in the string component when the current
  /// CAP_PRINTER device is enabled.
  int CAP_PRINTERSTRING = 4138;
  /// Specifies the string that shall be used as the current CAP_PRINTER device's suffix.
  int CAP_PRINTERSUFFIX = 4139;
  /// Allows Application and Source to identify which languages they have in common for the exchange of string data; 
  /// and to select the language of the internal UI. Since the TWLG_xxxx codes include language and country data; there is no separate 
  /// capability for selecting the country.
  int CAP_LANGUAGE = 4140;
  /// Helps the Application determine any special actions it may need to take when negotiating
  /// frames with the Source. Allowed values are listed in <see cref="TWCapFeederAlignment"/>.
  /// TWFA_NONE = The alignment is free-floating. Applications should assume
  /// that the origin for frames is on the left.
  /// TWFA_LEFT = The alignment is to the left.
  /// TWFA_CENTER = The alignment is centered. This means that the paper will
  /// be fed in the middle of the ICAP_PHYSICALWIDTH of the
  /// device. If this is set; then the Application should calculate
  /// any frames with a left offset of zero.
  /// TWFA_RIGHT = The alignment is to the right.
  int CAP_FEEDERALIGNMENT = 4141;
  /// TWFO_FIRSTPAGEFIRST if the feeder starts with the top of the first page.
  /// TWFO_LASTPAGEFIRST is the feeder starts with the top of the last page.
  int CAP_FEEDERORDER = 4142;
  /// Indicates whether the physical hardware (e.g. scanner; digital camera) is capable of acquiring
  /// multiple images of the same page without changes to the physical registration of that page.
  int CAP_REACQUIREALLOWED = 4144;
  /// The minutes of battery power remaining to the device.
  int CAP_BATTERYMINUTES = 4146;
  /// When used with CapGet(); return the percentage of battery power level on camera. If -1 is returned; it indicates that the battery is not present. 
  int CAP_BATTERYPERCENTAGE = 4147;
  /// Added 1.91 
  int CAP_CAMERASIDE = 4148;
  /// Added 1.91  
  int CAP_SEGMENTED = 4149;
  /// Added 2.0 
  int CAP_CAMERAENABLED = 4150;
  /// Added 2.0   
  int CAP_CAMERAORDER = 4151;
  /// Added 2.0 
  int CAP_MICRENABLED = 4152;
  /// Added 2.0  
  int CAP_FEEDERPREP = 4153;
  /// Added 2.0 
  int CAP_FEEDERPOCKET = 4154;
  /// Added 2.1 
  int CAP_AUTOMATICSENSEMEDIUM = 4155;
  /// Added 2.1 
  int CAP_CUSTOMINTERFACEGUID = 4156;
  /// TRUE enables and FALSE disables the Source's Auto-brightness function (if any).
  int ICAP_AUTOBRIGHT = 4352;
  /// The brightness values available within the Source.
  int ICAP_BRIGHTNESS = 4353;
  /// The contrast values available within the Source.
  int ICAP_CONTRAST = 4355;
  /// Specifies the square-cell halftone (dithering) matrix the Source should use to halftone the image.
  int ICAP_CUSTHALFTONE = 4356;
  /// Specifies the exposure time used to capture the image; in seconds.
  int ICAP_EXPOSURETIME = 4357;
  /// Describes the color characteristic of the subtractive filter applied to the image data. Multiple
  /// filters may be applied to a single acquisition.
  int ICAP_FILTER = 4358;
  /// Specifies whether or not the image was acquired using a flash.
  int ICAP_FLASHUSED = 4359;
  /// Gamma correction value for the image data.
  int ICAP_GAMMA = 4360;
  /// A list of names of the halftone patterns available within the Source.
  int ICAP_HALFTONES = 4361;
  /// Specifies which value in an image should be interpreted as the lightest "highlight." All values
  /// "lighter" than this value will be clipped to this value. Whether lighter values are smaller or
  /// larger can be determined by examining the Current value of ICAP_PIXELFLAVOR.
  int ICAP_HIGHLIGHT = 4362;
  /// Informs the application which file formats the Source can generate (CapGet()). Tells the Source which file formats the application can handle (CapSet()).
  /// TWFF_TIFF Used for document 
  /// TWFF_PICT Native Macintosh 
  /// TWFF_BMP Native Microsoft 
  /// TWFF_XBM Used for document 
  /// TWFF_JFIF Wrapper for JPEG 
  /// TWFF_FPX FlashPix; used with digital 
  /// TWFF_TIFFMULTI Multi-page TIFF files
  /// TWFF_PNG An image format standard intended for use on the web; replaces GIF
  /// TWFF_SPIFF A standard from JPEG; intended to replace JFIF; also supports JBIG
  /// TWFF_EXIF File format for use with digital cameras. 
  int ICAP_IMAGEFILEFORMAT = 4364;
  /// TRUE means the lamp is currently; or should be set to ON. Sources may not support CapSet() operations. 
  int ICAP_LAMPSTATE = 4365;
  /// Describes the general color characteristic of the light source used to acquire the image.
  int ICAP_LIGHTSOURCE = 4366;
  /// Defines which edge of the "paper" the image's "top" is aligned with. This information is used to adjust the frames to match the 
  /// scanning orientation of the paper. For instance; if an ICAP_SUPPORTEDSIZE of TWSS_ISOA4 has been negotiated; 
  /// and ICAP_ORIENTATION is set to TWOR_LANDSCAPE; then the Source must rotate the frame it downloads to the scanner to reflect the 
  /// orientation of the paper. Please note that setting ICAP_ORIENTATION does not affect the values reported by ICAP_FRAMES; 
  /// it just causes the Source to use them in a different way. The upper-left of the image is defined as the location where both the primary and 
  /// secondary scans originate. (The X axis is the primary scan direction and the Y axis is the secondary scan direction.)
  /// For a flatbed scanner; the light bar moves in the secondary scan direction. For a handheld scanner; the scanner is drug in the 
  /// secondary scan direction. For a digital camera; the secondary direction is the vertical axis when the viewed image is considered upright. 
  int ICAP_ORIENTATION = 4368;
  /// The maximum physical width (X-axis) the Source can acquire (measured in units of ICAP_UNITS).
  int ICAP_PHYSICALWIDTH = 4369;
  /// The maximum physical height (Y-axis) the Source can acquire (measured in units of ICAP_UNITS).
  int ICAP_PHYSICALHEIGHT = 4370;
  /// Specifies which value in an image should be interpreted as the darkest "shadow." All values 
  /// "darker" than this value will be clipped to this value.
  int ICAP_SHADOW = 4371;
  /// The list of frames the Source will acquire on each page.
  int ICAP_FRAMES = 4372;
  /// The native optical resolution along the X-axis of the device being controlled by the Source. Most
  /// devices will respond with a single value (TW_ONEVALUE).
  /// This is NOT a list of all resolutions that can be generated by the device. Rather; this is the
  /// resolution of the device's optics. Measured in units of pixels per unit as defined by
  /// ICAP_UNITS (pixels per TWUN_PIXELS yields dimensionless data).
  int ICAP_XNATIVERESOLUTION = 4374;
  /// The native optical resolution along the Y-axis of the device being controlled by the Source.
  /// Measured in units of pixels per unit as defined by ICAP_UNITS (pixels per TWUN_PIXELS
  /// yields dimensionless data).
  int ICAP_YNATIVERESOLUTION = 4375;
  /// All the X-axis resolutions the Source can provide.
  /// Measured in units of pixels per unit as defined by ICAP_UNITS (pixels per TWUN_PIXELS
  /// yields dimensionless data). That is; when the units are TWUN_PIXELS; both
  /// ICAP_XRESOLUTION and ICAP_YRESOLUTION shall report 1 pixel/pixel. Some data sources
  /// like to report the actual number of pixels that the device reports; but that response is more
  /// appropriate in ICAP_PHYSICALHEIGHT and ICAP_PHYSICALWIDTH.
  int ICAP_XRESOLUTION = 4376;
  /// All the Y-axis resolutions the Source can provide.
  /// Measured in units of pixels per unit as defined by ICAP_UNITS (pixels per TWUN_PIXELS
  /// yields dimensionless data). That is; when the units are TWUN_PIXELS; both
  /// ICAP_XRESOLUTION and ICAP_YRESOLUTION shall report 1 pixel/pixel. Some data sources
  /// like to report the actual number of pixels that the device reports; but that response is more
  /// appropriate in ICAP_PHYSICALHEIGHT and ICAP_PHYSICALWIDTH.
  int ICAP_YRESOLUTION = 4377;
  /// The maximum number of frames the Source can provide or the application can accept per page. 
  /// This is a bounding capability only. It does not establish current or future behavior.
  int ICAP_MAXFRAMES = 4378;
  /// This is used with buffered memory transfers. If TRUE; Source can provide application with tiled image data.
  int ICAP_TILES = 4379;
  /// Specifies how the bytes in an image are filled by the Source. TWBO_MSBFIRST indicates that the leftmost bit in the byte (usually bit 7) is 
  /// the byte's Most Significant Bit.
  int ICAP_BITORDER = 4380;
  /// Used for CCITT Group 3 2-dimensional compression. The 'K' factor indicates how often the
  /// new compression baseline should be re-established. A value of 2 or 4 is common in facsimile
  /// communication. A value of zero in this field will indicate an infinite K factorâ€”the baseline is
  /// only calculated at the beginning of the transfer.
  int ICAP_CCITTKFACTOR = 4381;
  /// Describes whether the image was captured transmissively or reflectively.
  int ICAP_LIGHTPATH = 4382;
  /// Sense of the pixel whose numeric value is zero (minimum data value). 
  int ICAP_PIXELFLAVOR = 4383;
  /// Allows the application and Source to identify which color data formats are available. There are
  /// two options; "planar" and "chunky."
  int ICAP_PLANARCHUNKY = 4384;
  /// How the Source can/should rotate the scanned image data prior to transfer. This doesn't use
  /// ICAP_UNITS. It is always measured in degrees. Any applied value is additive with any
  /// rotation specified in ICAP_ORIENTATION.
  int ICAP_ROTATION = 4385;
  /// For devices that support fixed frame sizes. 
  /// Defined sizes match typical page sizes. This specifies the size(s) the Source can/should use to acquire image data. 
  int ICAP_SUPPORTEDSIZES = 4386;
  /// Specifies the dividing line between black and white. This is the value the Source will use to
  /// threshold; if needed; when ICAP_PIXELTYPE:TWPT_BW.
  /// The value is normalized so there are no units of measure associated with this ICAP.
  int ICAP_THRESHOLD = 4387;
  /// All the X-axis scaling values available. A value of '1.0' is equivalent to 100% scaling. Do not use values less than or equal to zero.
  int ICAP_XSCALING = 4388;
  /// All the Y-axis scaling values available. A value of '1.0' is equivalent to 100% scaling. Do not use values less than or equal to zero. 
  /// There are no units inherent with this data as it is normalized to 1.0 being "unscaled."
  int ICAP_YSCALING = 4389;
  /// Used for CCITT data compression only. Indicates the bit order representation of the stored compressed codes.
  int ICAP_BITORDERCODES = 4390;
  /// Used only for CCITT data compression. Specifies whether the compressed codes' pixel "sense" 
  /// will be inverted from the Current value of ICAP_PIXELFLAVOR prior to transfer.
  int ICAP_PIXELFLAVORCODES = 4391;
  /// Allows the application and Source to agree upon a common set of color descriptors that are 
  /// made available by the Source. This ICAP is only useful for JPEG-compressed buffered memory image transfers.
  int ICAP_JPEGPIXELTYPE = 4392;
  /// Used only with CCITT data compression. Specifies the minimum number of words of compressed codes (compressed data) to be transmitted per line.
  int ICAP_TIMEFILL = 4394;
  /// Specifies the pixel bit depths for the Current value of ICAP_PIXELTYPE. For example; when
  /// using ICAP_PIXELTYPE:TWPT_GRAY; this capability specifies whether this is 8-bit gray or 4-bit gray.
  /// This depth applies to all the data channels (for instance; the R; G; and B channels will all have
  /// this same bit depth for RGB data).
  int ICAP_BITDEPTH = 4395;
  /// Specifies the Reduction Method the Source should use to reduce the bit depth of the data. Most
  /// commonly used with ICAP_PIXELTYPE:TWPT_BW to reduce gray data to black and white.
  int ICAP_BITDEPTHREDUCTION = 4396;
  /// If TRUE the Source will issue a MSG_XFERREADY before starting the scan.
  /// Note = The Source may need to scan the image before initiating the transfer. This is the case if
  /// the scanned image is rotated or merged with another scanned image.
  int ICAP_UNDEFINEDIMAGESIZE = 4397;
  /// Allows the application to query the data source to see if it supports extended image attribute capabilities; 
  /// such as Barcode Recognition; Shaded Area Detection and Removal; Skew detection and Removal; and so on.
  int ICAP_EXTIMAGEINFO = 4399;
  /// Allows the source to define the minimum height (Y-axis) that the source can acquire.
  int ICAP_MINIMUMHEIGHT = 4400;
  /// Allows the source to define theminimum width (X-axis) that the source can acquire.
  int ICAP_MINIMUMWIDTH = 4401;
  /// Use this capability to have the Source discard blank images. The Application never sees these
  /// images during the scanning session.
  /// TWBP_DISABLE â€? this must be the default state for the Source. It indicates that all images will
  /// be delivered to the Application; none of them will be discarded.
  /// TWBP_AUTO â€? if this is used; then the Source will decide if an image is blank or not and discard
  /// as appropriate.
  /// If the specified value is a positive number in the range 0 to 231â€?1; then this capability will use it
  /// as the byte size cutoff point to identify which images are to be discarded. If the size of the image
  /// is less than or equal to this value; then it will be discarded. If the size of the image is greater
  /// than this value; then it will be kept so that it can be transferred to the Application.
  int ICAP_AUTODISCARDBLANKPAGES = 4404;
  /// Flip rotation is used to properly orient images that flip orientation every other image.
  /// TWFR_BOOK The images to be scanned are viewed in book form; flipping each page from left to right or right to left.
  /// TWFR_FANFOLD The images to be scanned are viewed in fanfold paper style; flipping each page up or down. 
  int ICAP_FLIPROTATION = 4406;
  /// Turns bar code detection on and off.
  int ICAP_BARCODEDETECTIONENABLED = 4407;
  /// Provides a list of bar code types that can be detected by the current Data Source.
  int ICAP_SUPPORTEDBARCODETYPES = 4408;
  /// The maximum number of supported search priorities.
  int ICAP_BARCODEMAXSEARCHPRIORITIES = 4409;
  /// A prioritized list of bar code types dictating the order in which bar codes will be sought.
  int ICAP_BARCODESEARCHPRIORITIES = 4410;
  /// Restricts bar code searching to certain orientations; or prioritizes one orientation over the other.
  int ICAP_BARCODESEARCHMODE = 4411;
  /// Restricts the number of times a search will be retried if none are found on each page.
  int ICAP_BARCODEMAXRETRIES = 4412;
  /// Restricts the total time spent on searching for a bar code on each page.
  int ICAP_BARCODETIMEOUT = 4413;
  /// When used with CapGet(); returns all camera supported lens zooming range. 
  int ICAP_ZOOMFACTOR = 4414;
  /// Turns patch code detection on and off.
  int ICAP_PATCHCODEDETECTIONENABLED = 4415;
  /// A list of patch code types that may be detected by the current Data Source.
  int ICAP_SUPPORTEDPATCHCODETYPES = 4416;
  /// The maximum number of supported search priorities.
  int ICAP_PATCHCODEMAXSEARCHPRIORITIES = 4417;
  /// A prioritized list of patch code types dictating the order in which patch codes will be sought.
  int ICAP_PATCHCODESEARCHPRIORITIES = 4418;
  /// Restricts patch code searching to certain orientations; or prioritizes one orientation over the other.
  int ICAP_PATCHCODESEARCHMODE = 4419;
  /// Restricts the number of times a search will be retried if none are found on each page.
  int ICAP_PATCHCODEMAXRETRIES = 4420;
  /// Restricts the total time spent on searching for a patch code on each page.
  int ICAP_PATCHCODETIMEOUT = 4421;
  /// For devices that support flash. CapSet() selects the flash to be used (if any). CapGet() reports the current setting.
  /// This capability replaces ICAP_FLASHUSED; which is only able to negotiate the flash being on or off. 
  int ICAP_FLASHUSED2 = 4422;
  /// For devices that support image enhancement filtering. This capability selects the algorithm used to improve the quality of the image.
  int ICAP_IMAGEFILTER = 4423;
  /// For devices that support noise filtering. This capability selects the algorithm used to remove noise.
  int ICAP_NOISEFILTER = 4424;
  /// Overscan is used to scan outside of the boundaries described by ICAP_FRAMES; and is used to help acquire image data that 
  /// may be lost because of skewing.
  /// This is primarily of use for transport scanners which rely on edge detection to begin scanning. 
  /// If overscan is supported; then the device is capable of scanning in the inter-document gap to get the skewed image information. 
  int ICAP_OVERSCAN = 4425;
  /// Turns automatic border detection on and off.
  int ICAP_AUTOMATICBORDERDETECTION = 4432;
  /// Turns automatic deskew correction on and off.
  int ICAP_AUTOMATICDESKEW = 4433;
  /// When TRUE this capability depends on intelligent features within the Source to automatically 
  /// rotate the image to the correct position.
  int ICAP_AUTOMATICROTATE = 4434;
  /// Added 1.9 
  int ICAP_JPEGQUALITY = 4435;
  /// Added 1.91  
  int ICAP_FEEDERTYPE = 4436;
  /// Added 1.91 
  int ICAP_ICCPROFILE = 4437;
  /// Added 2.0  
  int ICAP_AUTOSIZE = 4438;
  /// Added 2.1 
  int ICAP_AUTOMATICCROPUSESFRAME = 4439;
  /// Added 2.1 
  int ICAP_AUTOMATICLENGTHDETECTION = 4440;
  /// Added 2.1 
  int ICAP_AUTOMATICCOLORENABLED = 4441;
  /// Added 2.1 
  int ICAP_AUTOMATICCOLORNONCOLORPIXELTYPE = 4442;
  /// Added 2.1 
  int ICAP_COLORMANAGEMENTENABLED = 4443;
  /// Added 2.1 
  int ICAP_IMAGEMERGE = 4444;
  /// Added 2.1 
  int ICAP_IMAGEMERGEHEIGHTTHRESHOLD = 4445;
  /// Added 2.1  
  int ICAP_SUPPORTEDEXTIMAGEINFO = 4446;
}
