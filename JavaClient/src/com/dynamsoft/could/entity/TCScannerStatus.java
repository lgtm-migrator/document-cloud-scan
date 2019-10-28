package com.dynamsoft.could.entity;

/// <summary>
/// Scanner status response payload.
/// </summary>
public class TCScannerStatus
{
    /// <summary>
    /// Gets or sets the URL to use to connect to TWAIN Cloud MQTT broker.
    /// </summary>
    public String url;

    /// <summary>
    /// Gets or sets the name of MQTT topic to use for incoming messages.
    /// </summary>
    public String requestTopic;

    /// <summary>
    /// Gets or sets the name of MQTT topic to use for outgoing messages.
    /// </summary>
    public String responseTopic;
}