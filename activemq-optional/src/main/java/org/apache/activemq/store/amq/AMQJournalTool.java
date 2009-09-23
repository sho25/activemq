begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|amq
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Scanner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQBlobMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQBytesMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQMapMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQObjectMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQStreamMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQTextMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|DataStructure
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|JournalQueueAck
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|JournalTopicAck
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|JournalTrace
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|JournalTransaction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|kaha
operator|.
name|impl
operator|.
name|async
operator|.
name|Location
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|kaha
operator|.
name|impl
operator|.
name|async
operator|.
name|ReadOnlyAsyncDataManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|openwire
operator|.
name|OpenWireFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|ByteSequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|wireformat
operator|.
name|WireFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|Template
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|VelocityContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|app
operator|.
name|Velocity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|app
operator|.
name|VelocityEngine
import|;
end_import

begin_import
import|import
name|org
operator|.
name|josql
operator|.
name|Query
import|;
end_import

begin_comment
comment|/**  * Allows you to view the contents of a Journal.  *   * @author<a href="http://hiramchirino.com">Hiram Chirino</a>  */
end_comment

begin_class
specifier|public
class|class
name|AMQJournalTool
block|{
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|File
argument_list|>
name|dirs
init|=
operator|new
name|ArrayList
argument_list|<
name|File
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|WireFormat
name|wireFormat
init|=
operator|new
name|OpenWireFormat
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|resources
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|String
name|messageFormat
init|=
literal|"${location.dataFileId},${location.offset}|${type}|${record.destination}|${record.messageId}|${record.properties}|${body}"
decl_stmt|;
specifier|private
name|String
name|topicAckFormat
init|=
literal|"${location.dataFileId},${location.offset}|${type}|${record.destination}|${record.clientId}|${record.subscritionName}|${record.messageId}"
decl_stmt|;
specifier|private
name|String
name|queueAckFormat
init|=
literal|"${location.dataFileId},${location.offset}|${type}|${record.destination}|${record.messageAck.lastMessageId}"
decl_stmt|;
specifier|private
name|String
name|transactionFormat
init|=
literal|"${location.dataFileId},${location.offset}|${type}|${record.transactionId}"
decl_stmt|;
specifier|private
name|String
name|traceFormat
init|=
literal|"${location.dataFileId},${location.offset}|${type}|${record.message}"
decl_stmt|;
specifier|private
name|String
name|unknownFormat
init|=
literal|"${location.dataFileId},${location.offset}|${type}|${record.class.name}"
decl_stmt|;
specifier|private
name|String
name|where
decl_stmt|;
specifier|private
name|VelocityContext
name|context
decl_stmt|;
specifier|private
name|VelocityEngine
name|velocity
decl_stmt|;
specifier|private
name|boolean
name|help
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|AMQJournalTool
name|consumerTool
init|=
operator|new
name|AMQJournalTool
argument_list|()
decl_stmt|;
name|String
index|[]
name|directories
init|=
name|CommandLineSupport
operator|.
name|setOptions
argument_list|(
name|consumerTool
argument_list|,
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|directories
operator|.
name|length
operator|<
literal|1
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Please specify the directories with journal data to scan"
argument_list|)
expr_stmt|;
return|return;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|directories
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|consumerTool
operator|.
name|getDirs
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|File
argument_list|(
name|directories
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|consumerTool
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|help
condition|)
block|{
name|showHelp
argument_list|()
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|getDirs
argument_list|()
operator|.
name|size
argument_list|()
operator|<
literal|1
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Invalid Usage: Please specify the directories with journal data to scan"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|showHelp
argument_list|()
expr_stmt|;
return|return;
block|}
for|for
control|(
name|File
name|dir
range|:
name|getDirs
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|dir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Invalid Usage: the directory '"
operator|+
name|dir
operator|.
name|getPath
argument_list|()
operator|+
literal|"' does not exist"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|showHelp
argument_list|()
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|dir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Invalid Usage: the argument '"
operator|+
name|dir
operator|.
name|getPath
argument_list|()
operator|+
literal|"' is not a directory"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|showHelp
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
name|context
operator|=
operator|new
name|VelocityContext
argument_list|()
expr_stmt|;
name|List
name|keys
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|context
operator|.
name|getKeys
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|iterator
init|=
name|System
operator|.
name|getProperties
argument_list|()
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
name|kv
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|name
init|=
operator|(
name|String
operator|)
name|kv
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|value
init|=
operator|(
name|String
operator|)
name|kv
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|keys
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|context
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|velocity
operator|=
operator|new
name|VelocityEngine
argument_list|()
expr_stmt|;
name|velocity
operator|.
name|setProperty
argument_list|(
name|Velocity
operator|.
name|RESOURCE_LOADER
argument_list|,
literal|"all"
argument_list|)
expr_stmt|;
name|velocity
operator|.
name|setProperty
argument_list|(
literal|"all.resource.loader.class"
argument_list|,
name|CustomResourceLoader
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|velocity
operator|.
name|init
argument_list|()
expr_stmt|;
name|resources
operator|.
name|put
argument_list|(
literal|"message"
argument_list|,
name|messageFormat
argument_list|)
expr_stmt|;
name|resources
operator|.
name|put
argument_list|(
literal|"topicAck"
argument_list|,
name|topicAckFormat
argument_list|)
expr_stmt|;
name|resources
operator|.
name|put
argument_list|(
literal|"queueAck"
argument_list|,
name|queueAckFormat
argument_list|)
expr_stmt|;
name|resources
operator|.
name|put
argument_list|(
literal|"transaction"
argument_list|,
name|transactionFormat
argument_list|)
expr_stmt|;
name|resources
operator|.
name|put
argument_list|(
literal|"trace"
argument_list|,
name|traceFormat
argument_list|)
expr_stmt|;
name|resources
operator|.
name|put
argument_list|(
literal|"unknown"
argument_list|,
name|unknownFormat
argument_list|)
expr_stmt|;
name|Query
name|query
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|where
operator|!=
literal|null
condition|)
block|{
name|query
operator|=
operator|new
name|Query
argument_list|()
expr_stmt|;
name|query
operator|.
name|parse
argument_list|(
literal|"select * from "
operator|+
name|Entry
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" where "
operator|+
name|where
argument_list|)
expr_stmt|;
block|}
name|ReadOnlyAsyncDataManager
name|manager
init|=
operator|new
name|ReadOnlyAsyncDataManager
argument_list|(
name|getDirs
argument_list|()
argument_list|)
decl_stmt|;
name|manager
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|Location
name|curr
init|=
name|manager
operator|.
name|getFirstLocation
argument_list|()
decl_stmt|;
while|while
condition|(
name|curr
operator|!=
literal|null
condition|)
block|{
name|ByteSequence
name|data
init|=
name|manager
operator|.
name|read
argument_list|(
name|curr
argument_list|)
decl_stmt|;
name|DataStructure
name|c
init|=
operator|(
name|DataStructure
operator|)
name|wireFormat
operator|.
name|unmarshal
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|Entry
name|entry
init|=
operator|new
name|Entry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|setLocation
argument_list|(
name|curr
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setRecord
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setData
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|process
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|curr
operator|=
name|manager
operator|.
name|getNextLocation
argument_list|(
name|curr
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|manager
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|showHelp
parameter_list|()
block|{
name|InputStream
name|is
init|=
name|AMQJournalTool
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"help.txt"
argument_list|)
decl_stmt|;
name|Scanner
name|scanner
init|=
operator|new
name|Scanner
argument_list|(
name|is
argument_list|)
decl_stmt|;
while|while
condition|(
name|scanner
operator|.
name|hasNextLine
argument_list|()
condition|)
block|{
name|String
name|line
init|=
name|scanner
operator|.
name|nextLine
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
name|scanner
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|process
parameter_list|(
name|Entry
name|entry
parameter_list|)
throws|throws
name|Exception
block|{
name|Location
name|location
init|=
name|entry
operator|.
name|getLocation
argument_list|()
decl_stmt|;
name|DataStructure
name|record
init|=
name|entry
operator|.
name|getRecord
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|record
operator|.
name|getDataStructureType
argument_list|()
condition|)
block|{
case|case
name|ActiveMQMessage
operator|.
name|DATA_STRUCTURE_TYPE
case|:
name|entry
operator|.
name|setType
argument_list|(
literal|"ActiveMQMessage"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setFormater
argument_list|(
literal|"message"
argument_list|)
expr_stmt|;
name|display
argument_list|(
name|entry
argument_list|)
expr_stmt|;
break|break;
case|case
name|ActiveMQBytesMessage
operator|.
name|DATA_STRUCTURE_TYPE
case|:
name|entry
operator|.
name|setType
argument_list|(
literal|"ActiveMQBytesMessage"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setFormater
argument_list|(
literal|"message"
argument_list|)
expr_stmt|;
name|display
argument_list|(
name|entry
argument_list|)
expr_stmt|;
break|break;
case|case
name|ActiveMQBlobMessage
operator|.
name|DATA_STRUCTURE_TYPE
case|:
name|entry
operator|.
name|setType
argument_list|(
literal|"ActiveMQBlobMessage"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setFormater
argument_list|(
literal|"message"
argument_list|)
expr_stmt|;
name|display
argument_list|(
name|entry
argument_list|)
expr_stmt|;
break|break;
case|case
name|ActiveMQMapMessage
operator|.
name|DATA_STRUCTURE_TYPE
case|:
name|entry
operator|.
name|setType
argument_list|(
literal|"ActiveMQMapMessage"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setFormater
argument_list|(
literal|"message"
argument_list|)
expr_stmt|;
name|display
argument_list|(
name|entry
argument_list|)
expr_stmt|;
break|break;
case|case
name|ActiveMQObjectMessage
operator|.
name|DATA_STRUCTURE_TYPE
case|:
name|entry
operator|.
name|setType
argument_list|(
literal|"ActiveMQObjectMessage"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setFormater
argument_list|(
literal|"message"
argument_list|)
expr_stmt|;
name|display
argument_list|(
name|entry
argument_list|)
expr_stmt|;
break|break;
case|case
name|ActiveMQStreamMessage
operator|.
name|DATA_STRUCTURE_TYPE
case|:
name|entry
operator|.
name|setType
argument_list|(
literal|"ActiveMQStreamMessage"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setFormater
argument_list|(
literal|"message"
argument_list|)
expr_stmt|;
name|display
argument_list|(
name|entry
argument_list|)
expr_stmt|;
break|break;
case|case
name|ActiveMQTextMessage
operator|.
name|DATA_STRUCTURE_TYPE
case|:
name|entry
operator|.
name|setType
argument_list|(
literal|"ActiveMQTextMessage"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setFormater
argument_list|(
literal|"message"
argument_list|)
expr_stmt|;
name|display
argument_list|(
name|entry
argument_list|)
expr_stmt|;
break|break;
case|case
name|JournalQueueAck
operator|.
name|DATA_STRUCTURE_TYPE
case|:
name|entry
operator|.
name|setType
argument_list|(
literal|"Queue Ack"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setFormater
argument_list|(
literal|"queueAck"
argument_list|)
expr_stmt|;
name|display
argument_list|(
name|entry
argument_list|)
expr_stmt|;
break|break;
case|case
name|JournalTopicAck
operator|.
name|DATA_STRUCTURE_TYPE
case|:
name|entry
operator|.
name|setType
argument_list|(
literal|"Topic Ack"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setFormater
argument_list|(
literal|"topicAck"
argument_list|)
expr_stmt|;
name|display
argument_list|(
name|entry
argument_list|)
expr_stmt|;
break|break;
case|case
name|JournalTransaction
operator|.
name|DATA_STRUCTURE_TYPE
case|:
name|entry
operator|.
name|setType
argument_list|(
name|getType
argument_list|(
operator|(
name|JournalTransaction
operator|)
name|record
argument_list|)
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setFormater
argument_list|(
literal|"transaction"
argument_list|)
expr_stmt|;
name|display
argument_list|(
name|entry
argument_list|)
expr_stmt|;
break|break;
case|case
name|JournalTrace
operator|.
name|DATA_STRUCTURE_TYPE
case|:
name|entry
operator|.
name|setType
argument_list|(
literal|"Trace"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setFormater
argument_list|(
literal|"trace"
argument_list|)
expr_stmt|;
name|display
argument_list|(
name|entry
argument_list|)
expr_stmt|;
break|break;
default|default:
name|entry
operator|.
name|setType
argument_list|(
literal|"Unknown"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setFormater
argument_list|(
literal|"unknown"
argument_list|)
expr_stmt|;
name|display
argument_list|(
name|entry
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
specifier|private
name|String
name|getType
parameter_list|(
name|JournalTransaction
name|record
parameter_list|)
block|{
switch|switch
condition|(
name|record
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|JournalTransaction
operator|.
name|XA_PREPARE
case|:
return|return
literal|"XA Prepare"
return|;
case|case
name|JournalTransaction
operator|.
name|XA_COMMIT
case|:
return|return
literal|"XA Commit"
return|;
case|case
name|JournalTransaction
operator|.
name|XA_ROLLBACK
case|:
return|return
literal|"XA Rollback"
return|;
case|case
name|JournalTransaction
operator|.
name|LOCAL_COMMIT
case|:
return|return
literal|"Commit"
return|;
case|case
name|JournalTransaction
operator|.
name|LOCAL_ROLLBACK
case|:
return|return
literal|"Rollback"
return|;
block|}
return|return
literal|"Unknown Transaction"
return|;
block|}
specifier|private
name|void
name|display
parameter_list|(
name|Entry
name|entry
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|entry
operator|.
name|getQuery
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|List
name|list
init|=
name|Collections
operator|.
name|singletonList
argument_list|(
name|entry
argument_list|)
decl_stmt|;
name|List
name|results
init|=
name|entry
operator|.
name|getQuery
argument_list|()
operator|.
name|execute
argument_list|(
name|list
argument_list|)
operator|.
name|getResults
argument_list|()
decl_stmt|;
if|if
condition|(
name|results
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
block|}
name|CustomResourceLoader
operator|.
name|setResources
argument_list|(
name|resources
argument_list|)
expr_stmt|;
try|try
block|{
name|context
operator|.
name|put
argument_list|(
literal|"location"
argument_list|,
name|entry
operator|.
name|getLocation
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"record"
argument_list|,
name|entry
operator|.
name|getRecord
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"type"
argument_list|,
name|entry
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|entry
operator|.
name|getRecord
argument_list|()
operator|instanceof
name|ActiveMQMessage
condition|)
block|{
name|context
operator|.
name|put
argument_list|(
literal|"body"
argument_list|,
operator|new
name|MessageBodyFormatter
argument_list|(
operator|(
name|ActiveMQMessage
operator|)
name|entry
operator|.
name|getRecord
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Template
name|template
init|=
name|velocity
operator|.
name|getTemplate
argument_list|(
name|entry
operator|.
name|getFormater
argument_list|()
argument_list|)
decl_stmt|;
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
name|System
operator|.
name|out
argument_list|)
decl_stmt|;
name|template
operator|.
name|merge
argument_list|(
name|context
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|CustomResourceLoader
operator|.
name|setResources
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setMessageFormat
parameter_list|(
name|String
name|messageFormat
parameter_list|)
block|{
name|this
operator|.
name|messageFormat
operator|=
name|messageFormat
expr_stmt|;
block|}
specifier|public
name|void
name|setTopicAckFormat
parameter_list|(
name|String
name|ackFormat
parameter_list|)
block|{
name|this
operator|.
name|topicAckFormat
operator|=
name|ackFormat
expr_stmt|;
block|}
specifier|public
name|void
name|setTransactionFormat
parameter_list|(
name|String
name|transactionFormat
parameter_list|)
block|{
name|this
operator|.
name|transactionFormat
operator|=
name|transactionFormat
expr_stmt|;
block|}
specifier|public
name|void
name|setTraceFormat
parameter_list|(
name|String
name|traceFormat
parameter_list|)
block|{
name|this
operator|.
name|traceFormat
operator|=
name|traceFormat
expr_stmt|;
block|}
specifier|public
name|void
name|setUnknownFormat
parameter_list|(
name|String
name|unknownFormat
parameter_list|)
block|{
name|this
operator|.
name|unknownFormat
operator|=
name|unknownFormat
expr_stmt|;
block|}
specifier|public
name|void
name|setQueueAckFormat
parameter_list|(
name|String
name|queueAckFormat
parameter_list|)
block|{
name|this
operator|.
name|queueAckFormat
operator|=
name|queueAckFormat
expr_stmt|;
block|}
specifier|public
name|String
name|getQuery
parameter_list|()
block|{
return|return
name|where
return|;
block|}
specifier|public
name|void
name|setWhere
parameter_list|(
name|String
name|query
parameter_list|)
block|{
name|this
operator|.
name|where
operator|=
name|query
expr_stmt|;
block|}
specifier|public
name|boolean
name|isHelp
parameter_list|()
block|{
return|return
name|help
return|;
block|}
specifier|public
name|void
name|setHelp
parameter_list|(
name|boolean
name|help
parameter_list|)
block|{
name|this
operator|.
name|help
operator|=
name|help
expr_stmt|;
block|}
comment|/** 	 * @return the dirs 	 */
specifier|public
name|ArrayList
argument_list|<
name|File
argument_list|>
name|getDirs
parameter_list|()
block|{
return|return
name|dirs
return|;
block|}
block|}
end_class

end_unit

