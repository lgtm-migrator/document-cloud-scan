package com.dynamsoft.dwt.common;

/// Data source status.
public interface EnumDWT_DataSourceStatus{
/// Indicate the data source is closed. 
int TWDSS_CLOSED = 0;
/// Indicate the data source is opened.
int TWDSS_OPENED = 1;
/// Indicate the data source is enabled. 
int TWDSS_ENABLED = 2;
/// Indicate the data source is acquiring image.
int TWDSS_ACQUIRING = 3;
}
