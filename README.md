# Document Cloud Scan Solution

![](http://www.codepool.biz/wp-content/uploads/2019/10/document-cloud-scan.jpg)

## Download and Installation
- [Dynamic Web TWAIN](https://www.dynamsoft.com/Downloads/WebTWAIN_Download.aspx)
- [Eclipse](https://www.eclipse.org/downloads/)
- [ActiveMQ Artemis](http://activemq.apache.org/components/artemis/download/)
- [Visual Studio](https://visualstudio.microsoft.com/downloads/)

## Configuration

### License
Get a free [trial license](https://www.dynamsoft.com/CustomerPortal/Portal/Triallicense.aspx).

### Visual Studio

Open `Server\TwainCloudServer\App_Start\AccountInitializer.cs` to create a new account:

```cs
var resources = new List<DbUser>
{
    new DbUser { name = "dynamsoft", password="1" ,email="support@dynamsoft.com"},
};
```

### ActiveMQ Artemis for Windows

Create a broker:

```
artemis create --allow-anonymous localhost
```

Start the broker by executing:

```
apache-artemis-2.10.1\bin\localhost\bin\artemis run
```

Run it in the background:

```
apache-artemis-2.10.1\bin\localhost\bin\artemis-service.exe install

apache-artemis-2.10.1\bin\localhost\bin\artemis-service.exe start
```

To stop the windows service:

```
apache-artemis-2.10.1\bin\localhost\bin\artemis-service.exe stop
```

To uninstall the windows service:

```
apache-artemis-2.10.1\bin\localhost\bin\artemis-service.exe uninstall
```

### Eclipse

Set the product key in `JavaClient\src\com\dynamsoft\dwt\Common.java`:

```java
public static final String DWT_ProductKey = "LICENSE-KEY";
```

## Deployment

Press F5 to run the server project in `Visual Studio`.

Open `http://localhost:14032/index.html` to see the main page.

![document cloud scan homepage](http://www.codepool.biz/wp-content/uploads/2019/10/document-cloud-homepage.png)

Run `artemis`:

```
artemis run
```

![artemis](http://www.codepool.biz/wp-content/uploads/2019/10/artemis.png)

Launch the Java client in Eclipse:

![scanner registration](http://www.codepool.biz/wp-content/uploads/2019/10/scanner-registration.png)

Visit `http://localhost:14032` to control the scanner via network.

![document cloud scan from windows](http://www.codepool.biz/wp-content/uploads/2019/10/document-cloud-scan-windows.png)

To access the IP address from other devices, configure `Server\.vs\config\applicationhost.config`:

```
<bindings>
    <binding protocol="http" bindingInformation="*:14032:localhost" />
    <binding protocol="http" bindingInformation="*:14032:192.168.8.85" />
</bindings>
```

Restart `IIS Express` with administrator privileges. 

You can now scan documents from any platform, such as `macOS`:

![document cloud scan from macOS](http://www.codepool.biz/wp-content/uploads/2019/10/document-cloud-scan-macos.png)


