$Id: README.txt,v 1.44 2008/05/07 17:50:19 kameleono Exp $
          ----------------------------------
           SQL2JAVA 2.6.7 - README
           http://sql2java.sourceforge.net/
          ----------------------------------

SQL2JAVA is a FREE, SIMPLE, PRAGMATIC and POWERFUL code GENERATOR.
It maps any relational schema to a java API dedicated to access your
database schema. It supports Oracle, SQLServer, PostgreSQL, MySQL,
Sybase, HSQL, etc... 2 minutes to install/try...


REQUIREMENT:

    JDK 1.4.0 or later.

SUMMARY:

    SQL2JAVA enables Java developers to quickly map a
    relational database schema to a set of classes.
    These classes can be supplemented with your own methods
    and used within any Java application. The generated source
    code uses standard JDBC methods for persistence, and there
    is no runtime library required.

    SQL2JAVA supports virtually any database that can be accessed through a
    jdbc driver. This includes:

    + Oracle 8i, 9
    + SQL Server - MSSQL
    + PostgreSQL
    + MySQL
    + Sybase
    + HSQL Database Engine
    + Informix
    + DB2
    + Apache Derby
    + MaxDB (SapDB)
    + etc...

    Retrieval of auto-generated keys is also supported as long as your driver
    supports it.

    Adding support for new database is just a matter of
    editing sql2java.properties!

CHANGES LOG:

  ______________________________
  * Release Name: sql2java-2.6.7

  Changes:
 - Letting the DAOException expose its cause message
 - Correction of compilation errors in the 'web' template folder
 - Correction of compilation errors in the 'java5' template folder
 - New code writer option to turn on/off the columns' default value
 - New option to define the path and filename of the sql2java.xml
 - Make the java5 templates a complete alternative to the java ones
 - Column or field names (By: rjemmons (rjemmons) - 2008-04-21 18:04)
 - New methods loadBy${index.name}() and deleteBy${index.name}()

  Bugs:
 - [ 1935532 ] 2.6.6: incorrect data type mapping for mysql (By: mmuru08 - 2008-04-05 10:50)

  Patch:
 - [ 1901983 ] Doubles in MySQL (By:Nobody/Anonymous (nobody) - 2008-02-26 06:17)

  ______________________________
  * Release Name: sql2java-2.6.6

  Changes:
 - Multiple field foreign keys (By: David Goodenough (dfgdga) - 2007-10-12 08:09)
 - integrating the templates from http://sourceforge.net/projects/jsql2ibatis/
 - BigDecimal issue with MySQL numbers in 2.6.5 (By: nfdavenport (nfdavenport) - 2007-11-12 16:10)
 - Fixed bug in manager with 2 columns pk? (By: jelvira (jelvira) - 2007-11-23 12:38)
 - Bug in manager.java.vm (2.6.5) (By: David Goodenough (dfgdga) - 2007-12-03 04:26)
 
  Bugs:
 - [ 1773986 ] not ResultSet.TYPE_FORWARD_ONLY bug
 - [ 1794354 ] unclosed character literal
 - [ 1828295 ] NullPointerException in loadUsingTemplate
 - [ 1866921 ] classprefix not used

  ______________________________
  * Release Name: sql2java-2.6.5

  Changes:
 - now includes the JPetStore example database
 - generating calls to stored procedures (only tested with Oracle)
 - configuration for MaxDB (SapDB)
 - ship the MySQL 5.0.6 driver as default mysql.jar
 - filtering out tables and foreign keys (By: Grzegorz Pilarczyk (grzpil) - 2005-10-27 07:49)
 - Date support - null issue (By: Carl J. Mosca (carljmosca) - 2007-08-10 03:31)

  Feature Requests:
 - [ 1300716 ] data access methods by stored procedures
 
  Support Requests:
 - [ 1230178 ] MySql fails to generate source on Win32
 - [ 1280360 ] evaluation of SQL2JAVA
 
  ______________________________
  * Release Name: sql2java-2.6.4

  Changes:
 - let it generate code for tables with composite keys (some broken templates around)
 - align the Java5 templates with the modifications implemented in the Java templates

  Feature Requests:
 - [ 1093516 ] Support for database schema 'default's
 - [ 663997 ] Simple object caching functionality
 
  Bugs:
 - [ 1241274 ] View Generation fails Sybase 2 - with detail
 - [ 1353824 ] Filter tables in sql2java.properties is ignored
 - [ 1434766 ] is not compatible with postgres 8.1
 - [ 1499193 ] properties file not found during generate
 - [ 1533964 ] ability to set the ID pattern
 - [ 1534392 ] Count( expr ) without "as Something" generates bad Java
 - [ 1563717 ] PostgreSQL table inherritance bug with indices
 - [ 1748450 ] database.documentation.table.html.vm exception
 - [ 1748488 ] use StringBuilder for java 5 templates
 
  ______________________________
  * Release Name: sql2java-2.6.3

  Changes:
 - hsqldb 1.8.0.7 (By: Charl van Jaarsveldt (charlvj) - 2007-05-26 07:44)
 - eliminating the causes of the Velocity errors reported in velocity.log
 - eliminating the causes of the Javadoc errors in the generated sources
 - eliminating some warnings in generated sources when put in an eclipse project
 - more modularity in templates' folders
 - Java5 templates (By: The Marker (zxsec) - 2006-02-01 05:41)
  
  Support Requests:
 - [ 977657 ] Missing a listener
 
  Feature Requests:
 - [ 1697173 ] tables reference in perschema template
 - [ 1490034 ] join within the same table
 - [ 1472423 ] improve the way Informix Serial attribute is retrieved

  Bugs:
 - [ 1268208 ] Remove "import java.util.*;" from ManagerTemplate.vm
 - [ 1353818 ] SQLException in xManager using Oracle 10g w. SQL2Java 2.6.1
 - [ 1376764 ] PostgreSQL 8.0 xBean FORWARD_ONLY cursor issue
 - [ 1415820 ] Manager deadlock
 - [ 1437256 ] error with sequence name
 - [ 1472419 ] XXXusingTemplate methods does not support search for NULL
 - [ 1474089 ] wrong method name System.curentTimeMillis in xManager Templ.
 - [ 1527572 ] generated source, error
 - [ 1533961 ] deadlock
 - [ 1539465 ] Cannot use 'Driver' as a table name
 - [ 1554514 ] Database.loadPrimaryKeys() does not work correctly for MySQL
 - [ 1561284 ] User code overridden
 - [ 1565316 ] PostgreSQL 8.1 auto-generated key fails
 - [ 1584043 ] loadByWhere(String where, int[] fieldList) bug

  ______________________________
  * Release Name: sql2java-2.6.2

  Changes:
 - cleanup folder structure
 - cleanup distribution structure
 - cleanup documentation, add screenshots
 - added java templates
 - added documentation templates
 - added default web application

  Bug fixes:

  ______________________________
  * Release Name: sql2java-2.6.1

  Changes:
 - No longer try to create or saved unmodified bean (was a feature request)
 - include a patch submitted by 'mmondini' in order to specify several patterns in
   the 'jdbc.tablenamepattern' property at code generation time.
 - firebird settings were added

  Bug fixes:
 - fix regression bug: "in my properties file but the generated classes do not have the prefix."
   reported by 'hamvil'
 - fix '"z" prefix needs to be dropped on literal variables'
 - fix 'Erroroneous generation of script for MS SQL' reported by sanclementetech

  ______________________________
  * Release Name: sql2java-2.6.0

  Changes:
 - From now on code generation is done with Velocity (a huge thanks
   to Kelvin Nishikawa who sent us this incredible patch).
   The big plus is that you can modify these templates => and submit
   us patches :-)
 - Optimistic lock support (thanks to Federico Crivellaro)
 - Informix configuration added (thanks to masogato at sf.net )

  ______________________________
  * Release Name: sql2java-2.5.0

  Changes:
 - for consistency: deleteWhere method was renamed to deleteByWhere and now
   the 'where' keyword must be specified in deleteByWhere methods.
   For example, deleteWhere("name='john'")
   should be replaced by deleteByWhere("where name='john'")
  (thanks to greg1104 for his feedbacks)
 - deleteAll() method is now generated
 - removed useless c.setAutoCommit(true)
 - TODO tags and useless import were removed from generated code.
   (thanks to greg1104 for his feedbacks)
 - running the sample is now straightforward (we ship hsqldb for this purpose)
 - no longer shipping mysql jdbc driver.


  ______________________________
  * Release Name: sql2java-2.4.1
  Notes:
 - no longer shipping oracle jdbc driver. File is too large, we assume
 you have it.

  Bug fixes:
 - [ 954362 ] Generated code won't compile for types mapped to Boolean
 (thanks to mattshaw who reported it)

  ______________________________
  * Release Name: sql2java-2.4.0

  Notes:
 - we support any database as long as a JDBC driver is provided
 - updated htdoc/config/sql2java.properties
 - updated/improved javadoc
 - updated build.xml to fix link at footer of javadoc
 - updated working example documentation (doc/index.html)
 - release of new sql2java website
 - removed individual database writer classes
 - new file: this readme !

  Bug fixes:

 - [ 923075 ] error when generating of synonyms
 - We took into account the following feedback (thanks to ioly):
   "when calling loadByTemplate method or countUsingTemplate method,
    if the pObject is not initialized, no SQLException should be thrown,
    just return loadAll()"
 - We took into account the following feedback (thanks to ioly):
   "currently I use sql server 2000 and the microsoft jdbc driver.
    I found when a table contains fields of "image" type , each column of
    ResultSet returned by driver "cannot be re-read"(it seems that you can
    call rs.getXXX only once), so I change the code from
    pObject.setId(rs.getObject == null?(Integer)null:new Integer(rs.getInt(1))) to
    pObject.setId((Integer)rs.getObject(1)), and when "select @@identity", the code
    should be: pObject.setId(new Integer(rs.getInt(1)))"


  ______________________________
  * Release Name: sql2java-2-3-0

  Notes:

 - support for sql server (aka mssql)
 - support for postgresql
 - fix potential bugs with primitive type
 - improved javadoc in setter method
 - writers are now part of package com.netkernel.sql2java
   so for example we have
   writer.class=com.netkernel.sql2java
   instead of writer.class=com.netkernel.generation.sqlcode.java.AutoGeneratedKeyWriter
 - reorganized the cvs repository, source is in sql2java dir
   instead of sql2code
 - added support for all table types, including but not
   limited-to SYSTEM TABLE, VIEW, SYNONYM. This feature
   is configurable through the doc/sql2java.properties.
 - prevent failures when BLOB/CLOB are present.
   BLOB/CLOB is NOT supported, but the code is generated
   regardless.
 - you can now configure the java type for mapping of
   TIME, TIMESTAMP and DATE through
   doc/sql2java.properties.

 _____________________
 * Release Name: 2.2.1

 Notes:

 New Features:
 - support for auto generated key feature provided by certain jdbc driver

 Changes:

 - less classes generated
 - null can be forced in one shot
 - more javadoc

===============================================================

LEGAL:

   * This product uses Velocity: license is available here http://www.apache.org/licenses/LICENSE-2.0
   * For the example, we ship hsqldb in a binary form, but you do not need
     it to use sql2java against another database.
     "This product includes Hypersonic SQL.
     ORIGINAL LICENSE (a.k.a. "hypersonic_lic.txt")
     For content, code, and products originally developed by Thomas Mueller and the Hypersonic SQL Group:
     Copyright (c) 1995-2000 by the Hypersonic SQL Group. All rights reserved."
