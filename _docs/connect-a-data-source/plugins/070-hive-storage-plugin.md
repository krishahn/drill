---
title: "Hive Storage Plugin"
date: 2016-01-07
parent: "Connect a Data Source"
---
Drill 1.1 and later supports Hive 1.0. To access Hive tables
using custom SerDes or InputFormat/OutputFormat, all nodes running Drillbits
must have the SerDes or InputFormat/OutputFormat `JAR` files in the 
`<drill_installation_directory>/jars/3rdparty` folder.

You can run Hive queries in the following ways by configuring the Hive storage plugin as described in this document:

* [Connect Drill to the Hive remote metastore]({{site.baseurl}}/docs/hive-storage-plugin/#connect-drill-to-the-hive-remote-metastore-directly)  
* [Connect to the Hive embedded metastore]({{site.baseurl}}/docs/hive-storage-plugin/#connect-to-the-hive-embedded-metastore)  

You update the Hive storage plugin by selecting the **Storage tab** on the [Drill Web Console]({{ site.baseurl }}/docs/plugin-configuration-basics/#using-the-drill-web-console). From the list of disabled storage plugins in the Drill Web Console, click **Update** next to `hive`.  The default Hive storage plugin configuration appears as follows:

        {
          "type": "hive",
          "enabled": false,
          "configProps": {
            "hive.metastore.uris": "",
            "javax.jdo.option.ConnectionURL": "jdbc:derby:;databaseName=../sample-data/drill_hive_db;create=true",
            "hive.metastore.warehouse.dir": "/tmp/drill_hive_wh",
            "fs.default.name": "file:///",
            "hive.metastore.sasl.enabled": "false"
          }
        }

## Connect Drill to the Hive Remote Metastore

The Hive metastore runs as a separate service outside
of Hive. Drill can query the Hive metastore through Thrift. The
metastore service communicates with the Hive database over JDBC. 

Follow the steps in the next section to point Drill
to the Hive metastore service address. Provide the connection parameters
in a Hive storage plugin configuration to configure a connection to Drill. At this point, if you query data sources that Drill supports other than HBase (or MapR), you are finished configuring the Hive storage plugin. If you query HBase using Hive, you need to add ZooKeeper quorum and port properties.  The HBaseStorageHandler requires these properties. Drill discovers HBase services using these properties. If you use the HBase storage plugin, the ZooKeeper quorum and port properties in the Hive storage plugin are the same as those in the HBase storage plugin, assuming you want to use the same HBase database. 

{% include startnote.html %}Verify that the Hive metastore service is running before you register the Hive metastore.{% include endnote.html %}  

### Hive Remote Metastore Configuration

To connect Drill to a remote Hive metastore:

1. Issue the following command to start the Hive metastore service on the system specified in the `hive.metastore.uris`:  
   `hive --service metastore`
2. In the [Drill Web Console]({{ site.baseurl }}/docs/plugin-configuration-basics/#using-the-drill-web-console), select the **Storage** tab.
3. In the list of disabled storage plugins in the Drill Web Console, click **Update** next to `hive`.  
4. In the configuration window, add the `Thrift URI` and port to `hive.metastore.uris`. For example:

          ...
             "configProps": {
             "hive.metastore.uris": "thrift://<host>:<port>",
          ...

5. Change the default location of files to suit your environment; for example, change `"fs.default.name"` property from `"file:///"` to `hdfs://` or `hdfs://<host name>:<port>`. The `"fs.default.name"` that includes a host name and port must point to the main control node. For example:

          {
            "type": "hive",
            "enabled": false,
            "configProps": {
            "hive.metastore.uris": "thrift://hdfs41:9083",
            "hive.metastore.sasl.enabled": "false",
            "fs.default.name": "hdfs://10.10.10.41/"
            }
          }

6. If you do not query HBase, skip this step. If you query HBase, in the configuration window, add the names of the ZooKeeper quorum hosts and the ZooKeeper port, for example 2181.  

        {
          "type": "hive",
          "enabled": false,
          "configProps": {
          .
          .
          .
            "hbase.zookeeper.quorum": "zkhost1,zkhost2,zkhost3",
            "hbase.zookeeper.property.clientPort:" "2181" 
          }
        }

7. Click **Enable**.  

## Connect to the Hive embedded metastore 

The Hive metastore configuration is embedded within the Drill process. Configure an embedded metastore only in a cluster that runs a single Drillbit and only for testing purposes. Do not embed the Hive metastore in production systems.

Provide the metastore database configuration settings in the Drill Web Console. Before you configure an embedded Hive metastore, verify that the driver you use to connect to the Hive metastore is in the Drill classpath located in `/<drill installation directory>/lib/.` If the driver is not there, copy the driver to `/<drill
installation directory>/lib` on the Drill node. For more information about storage types and configurations, refer to ["Hive Metastore Administration"](https://cwiki.apache.org/confluence/display/Hive/AdminManual+MetastoreAdmin).

### Hive Embedded Metastore Configuration

To configure an embedded Hive metastore, complete the following
steps:

1. In the [Drill Web Console]({{ site.baseurl }}/docs/plugin-configuration-basics/#using-the-drill-web-console), and select the **Storage** tab.
2. In the disabled storage plugin configurations section, click **Update** next to `hive`.
3. In the configuration window, add the database configuration settings.

    **Example**

          {
            "type": "hive",
            "enabled": false,
            "configProps": {
              "hive.metastore.uris": "",
              "javax.jdo.option.ConnectionURL": "jdbc:<database>://<host:port>/<metastore database>",
              "hive.metastore.warehouse.dir": "/tmp/drill_hive_wh",
              "fs.default.name": "file:///",
              "hive.metastore.sasl.enabled": "false"
            }
          }
5. Change the `"fs.default.name"` attribute to specify the default location of files. The value needs to be a URI that is available and capable of handling file system requests. For example, change the local file system URI `"file:///"` to the HDFS URI: `hdfs://`, or to the path on HDFS with a namenode: `hdfs://<hostname>:<port>`  
6. Click **Enable**.  

The "javax.jdo.option.ConnectionURL" might be a MySQL database that stores the metadata. The Hive metastore service provides access to the physical DB like MySQL. MySQL stores the metadata. 

After configuring a Hive storage plugin, you can [query Hive tables]({{ site.baseurl }}/docs/querying-hive/).
