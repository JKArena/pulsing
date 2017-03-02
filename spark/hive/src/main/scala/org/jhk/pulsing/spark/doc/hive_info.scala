/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jhk.pulsing.spark.doc

/**
 * In a traditional database, a table's schema is enforced at data load time. If the data being loaded doesn't conform to the 
 * schema, then it is rejected. This design is sometimes called schema on write because the data is checked against the schema when 
 * it is written into the database.
 * 
 * Hive on the other hand, doesn't verify the data when it is loaded, but rather when a query is issued. This is called a schema on read.
 * There are trade-offs between the two approaches. Schema on read makes for a very fast initial load, since the data does not have to be 
 * read, parsed, and serialized to disk in the database's internal format. The load operation is just a file copy or move. It is more flexible, 
 * too: consider having two schemas for the same underlying data, depending on the analysis being performed.
 * 
 * Schema on write makes query time performance faster because the database can index columns and perform compression on the data. The 
 * trade-off, however, is that it takes longer to load data into the database. Furthermore, there are many scenarios where the schema is not 
 * known at load time, so there are no indexes to apply, because the queries have not been formulated yet. These scenarios are where Hive 
 * shines.
 * 
 * HDFS does not provide in-place file updates, so changes resulting from inserts, updates, and deletes are stored in small delta files. 
 * Delta files are periodically merged into the base table files by MapReduce jobs that are run in the backgrounds by the metastore. These 
 * features only work in the context of transactions, so the tables they are being used on needs to have transactions enabled on it. Queries 
 * reading the table are guaranteed to see a consistent snapshot of the table.
 * 
 * Hive indexes can speed up queries in certain cases. A query such as SELECT * from t where x = a, for example, can take advantage of an 
 * index on column x, since only a small portion of the table's files need to be scanned. There are currently two index types: compact 
 * and bitmap. (The index implementation was designed to be pluggable, so it's expected that a variety of implementation will emerge for 
 * different use cases.)
 * 
 * Compact indexes store the HDFS block numbers of each value, rather than each file offset, so they don't take up much disk space but 
 * are still effective for the case where values are clustered together in nearby rows. Bitmap indexes use compressed bitsets to 
 * efficiently store the rows that a particular value appears in, and they are usually appropriate for low-cardinality columns 
 * (such as gender or country).
 * 
 * When you create a table in Hive, by default HIve will manage the data, which means that Hive moves the data into its warehouse directory. 
 * Alternatively, you may create an external table, which tells Hive to refer to the data that is at an existing location outside the 
 * warehouse directory. The difference between the two table types is seen in the LOAD and DROP semantics.
 * 
 * CREATE TABLE managed_table (dummy STRING);
 * LOAD DATA INPATH '/user/foo/data.txt' INTO table managed_table;
 * 
 * will move the file hdfs://user/foo/data.txt into HIve' warehouse directory for the managed_table, which is 
 * hdfs://user/hive/warehouse/managed_table
 * 
 * If DROP TABLE managed_table; 
 * the table including its metadata and its data, is deleted. It bears representing that since the initial LOAD performed a move operation, 
 * and the DROP performed a delete operation, the data no longer exists anywhere.
 * 
 * An external table behaves differently. You control the creation and deletion of the data. The location of the external data is 
 * specified at table creation time:
 * CREATE EXTERNAL TABLE external_table (dummy STRING) LOCATION '/user/foo/external_table';
 * LOAD DATA INPATH '/user/foo/data.txt' INTO TABLE external_table;
 * 
 * With the EXTERNAL keyword, HIve knows that it is not managing the data, so it doesn't move it to its warehouse directory. Indeed, 
 * it doesn't even check whether the external location exists at the time it is defined. This is a useful feature because it means you 
 * can create the data lazily after creating the table. When you drop an external table, Hive will leave the data untouched and only 
 * delete the metadata.
 * 
 * As a rule of thumb, if you are doing all your processing with Hive, then use managed tables, but if you wish to use Hive and other tools 
 * on the same dataset, then use external tables. A common pattern is to use an external table to access an initial dataset stored in HDFS 
 * (created by another process), then use a Hive transform to move the data into a managed Hive table. This works the other way around, too;
 * an external table (not necessarily on HDFS) can be used to export data from Hive for other applications to use. Another reason for 
 * using external tables is when you wish to associate multiple schemas with the same dataset.
 * 
 * Hive organizes tables into partitions based on the value of the partition column, such as a date. Using partitions can make it faster 
 * to do queries on slices of the data. Tables or partitions may be subdivided further into buckets to give extra structure to the data 
 * that may be used for more efficient queries. For example, bucketing by user ID means we can quickly evaluate a user-based query by 
 * running it on a randomized sample of the total set of users.
 * 
 * To take an example where partitions are commonly used, imagine logfiles where each record includes a timestamp. If we partition by 
 * date, then records for the same date will be stored in the same partition. The advantage to this scheme is that queries that are 
 * restricted to a particular date or set of dates can run much more efficiently, because they only need to scan the files in the partitions 
 * that the query pertains to. Notice that partitioning doesn't preclude more wide-ranging queries: it is still feasible to query the 
 * entire dataset across many partitions.
 * 
 * A table may be partitioned in multiple dimensions. For example, in addition to partitioning logs by date, we might also subpartition each 
 * date partition by country to permit efficient queries by location.
 * 
 * CREATE TABLE logs (ts BIGINT, line STRING)
 * PARTITIONED BY (dt STRING, country STRING);
 * 
 * When we load data into a partitioned table, the partition values are specified explicitly.
 * LOAD DATA LOCAL INPATH 'input/hive/partitions/file1' INTO TABLE logs PARTITION (dt='2001-01-01', country='GB');
 * 
 * At the filesystem level, partitions are simply nested subdirectories of the table directory.
 * /user/hive/warehouse/logs
 * |--|dt=2001-01-01
 * |  |--country=GB/
 * |  |  |--file1
 * |  |  |--file2
 * |  |--country=US/
 * |  |  |--file3
 * 
 * One thing to bear in mind is that the column definitions in the PARTITIONED BY clause are full fledged table columns, called partition 
 * columns; however, the datafiles do not contain values for these columns, since they are derived from the directory names.
 * 
 * You can use partition columns in SELECT statements in the usual way. Hive performs input pruning to scan only the relevant partitions. 
 * For example:
 * SELECT ts, dt, line 
 * FROM logs
 * WHERE country='GB';
 * 
 * Join of two tables that are bucketed on the same columns--which include the join columns--can be efficiently implemented as a map-side 
 * join. The second reason to bucket a table is to make sampling more efficient. When working with large datasets, it is very convenient to 
 * try out queries on a fraction of your dataset while you are in the process of developing or refining the.
 * 
 * CREATE TABLE bucketed_users (id INT, name STRING)
 * CLUSTERED BY (id) INTO 4 BUCKETS;
 * Hive determines the above bucket by hashing the value and reducing modulo the number of buckets. In the map-side join case, where the 
 * two tables are bucketed in the same way, a mapper processing a bucket of the left table knows that the matching rows in the right table 
 * are in its corresponding bucket, so it need only retrieve that bucket (which is a small fraction of all the data stored in the right 
 * side) to effect the join. This optimization also works when the number of buckets in the two tables are multiples of each other; they 
 * do not have to have exactly the same number of buckets.
 * 
 * The data within a bucket may additionally be sorted by one or more columns to provide more efficient map-side joins, since the join 
 * of each bucket becomes an efficient merge sort.
 * 
 * CREATE TABLE bucketed_users (id INT, name STRING)
 * CLUSTERED BY (id) SORTED BY (id ASC) INTO 4 BUCKETS;
 * 
 * Physically, each bucket is just a file in the table (or partition) directory. The filename is not important, but bucket n is the nth 
 * file when arranged in lexicographic order. In fact, buckets correspond to MapReduce output file partitions: a job will produce as many 
 * buckets (output files) as reduce tasks.
 * 
 * Sampling the table using the TABLESAMPLE clause (it's possible to sample a number of buckets by specifying a different proportion, which 
 * need not be an exact multiple of the number of buckets as sampling is not intended to be a precise operation)
 * SELECT * FROM bucketed_users TABLESAMPLE(BUCKET 1 OUT OF 4 ON id);
 * 
 * There are two dimensions that govern table storage in Hive: the row format and the file format. The row format dictates how rows, and 
 * the fields in a particular row, are stored. In Hive parlance, the row format is defined by a SerDe, a protmanteau word for 
 * Serializer-Deserializer.
 * 
 * The default row delimiter is not a tab character, but the Ctrl-A character from the set of ASCII control codes. There is no means 
 * for escaping delimiter characters in Hive, so it is important to choose ones that don't occur in data fields. The default collection item 
 * delimiter is a Ctrl-B character, used to delimit items in an ARRAY or STRUCT, or in key-value pairs in a MAP. The default map key 
 * delimiter is a Ctrl-C character, used to delimit the key and value in a MAP. Rows in a table are delimited by a newline character.
 * 
 * For nested types, level of the nesting determines the delimiter. For an array of arrays, the delimiters for the outer array are Ctrl-B 
 * characters but for the inner array they are Ctrl-C characters, the next delimiter in the list. Hive supports eight levels of delimiters, 
 * corresponding to ASCII codes 1, 2...8 but you can override only the first three.
 * 
 * INSERT OVERWRITE TABLE target PARTITION (dt='2001-01-01')
 * SELECT col1, col2 FROM source;
 * 
 * Multitable insert is more efficient than multiple INSERT statements because the source table needs to be scanned only once to produce 
 * the multiple disjoint outputs.
 * FROM records2
 * INSERT OVERWRITE TABLE stations_by_year
 * 	SELECT year, COUNT(DISTINCT station)
 * 	GROUP BY year
 * INSERT OVERWRITE TABLE records_by_year
 * 	SELECT year, COUNT(1)
 * 	GROUP BY year
 * INSERT OVERWRITE TABLE good_records_by_year
 * 	SELECT year, COUNT(1)
 * 	WHERE temperature != 9999 AND quality IN (0, 1, 4, 5, 6)
 * 	GROUP BY year;
 * 
 * There is a single source table (records2), but three tables to hold the results from three different queries over the source.
 * 
 * Sorting data in Hive can be achieved by using a standard ORDER BY clause. ORDER BY performs a parallel total sort of the input. When 
 * a globally sorted result is not required you can use Hive's nonstandard extension SORT BY instead. SORT By produces a sorted file 
 * per reducer.
 * 
 * @author Ji Kim
 */
