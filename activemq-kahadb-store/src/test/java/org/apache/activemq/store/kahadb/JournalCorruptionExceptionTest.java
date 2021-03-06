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
name|kahadb
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
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
name|broker
operator|.
name|BrokerService
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
name|broker
operator|.
name|region
operator|.
name|policy
operator|.
name|PolicyEntry
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
name|broker
operator|.
name|region
operator|.
name|policy
operator|.
name|PolicyMap
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
name|ActiveMQQueue
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
name|store
operator|.
name|kahadb
operator|.
name|disk
operator|.
name|journal
operator|.
name|DataFile
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
name|store
operator|.
name|kahadb
operator|.
name|disk
operator|.
name|journal
operator|.
name|Journal
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
name|util
operator|.
name|RecoverableRandomAccessFile
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|Collection
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|kahadb
operator|.
name|JournalCorruptionEofIndexRecoveryTest
operator|.
name|drain
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|JournalCorruptionExceptionTest
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JournalCorruptionExceptionTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|KAHADB_DIRECTORY
init|=
literal|"target/activemq-data/"
decl_stmt|;
specifier|private
specifier|final
name|String
name|payload
init|=
operator|new
name|String
argument_list|(
operator|new
name|byte
index|[
literal|1024
index|]
argument_list|)
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|cf
init|=
literal|null
decl_stmt|;
specifier|private
name|BrokerService
name|broker
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
name|Destination
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Test"
argument_list|)
decl_stmt|;
specifier|private
name|String
name|connectionUri
decl_stmt|;
specifier|private
name|KahaDBPersistenceAdapter
name|adapter
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameter
argument_list|(
literal|0
argument_list|)
specifier|public
name|byte
name|fill
init|=
name|Byte
operator|.
name|valueOf
argument_list|(
literal|"3"
argument_list|)
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameter
argument_list|(
literal|1
argument_list|)
specifier|public
name|int
name|fillLength
init|=
literal|10
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"fill=#{0},#{1}"
argument_list|)
specifier|public
specifier|static
name|Iterable
argument_list|<
name|Object
index|[]
argument_list|>
name|parameters
parameter_list|()
block|{
comment|// corruption can be valid record type values
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
name|Byte
operator|.
name|valueOf
argument_list|(
literal|"0"
argument_list|)
block|,
literal|6
block|}
block|,
block|{
name|Byte
operator|.
name|valueOf
argument_list|(
literal|"1"
argument_list|)
block|,
literal|8
block|}
block|,
block|{
name|Byte
operator|.
name|valueOf
argument_list|(
literal|"-1"
argument_list|)
block|,
literal|6
block|}
block|,
block|{
name|Byte
operator|.
name|valueOf
argument_list|(
literal|"0"
argument_list|)
block|,
literal|10
block|}
block|,
block|{
name|Byte
operator|.
name|valueOf
argument_list|(
literal|"1"
argument_list|)
block|,
literal|10
block|}
block|,
block|{
name|Byte
operator|.
name|valueOf
argument_list|(
literal|"2"
argument_list|)
block|,
literal|10
block|}
block|,
block|{
name|Byte
operator|.
name|valueOf
argument_list|(
literal|"-1"
argument_list|)
block|,
literal|10
block|}
block|}
argument_list|)
return|;
block|}
specifier|protected
name|void
name|startBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|doStartBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doStartBroker
parameter_list|(
name|boolean
name|delete
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
name|delete
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDataDirectory
argument_list|(
name|KAHADB_DIRECTORY
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
expr_stmt|;
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|PolicyEntry
name|defaultEntry
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|defaultEntry
operator|.
name|setUseCache
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|defaultEntry
operator|.
name|setExpireMessagesPeriod
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|policyMap
operator|.
name|setDefaultEntry
argument_list|(
name|defaultEntry
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
name|configurePersistence
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|connectionUri
operator|=
literal|"vm://localhost?create=false"
expr_stmt|;
name|cf
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connectionUri
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting broker.."
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|configurePersistence
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
throws|throws
name|Exception
block|{
name|adapter
operator|=
operator|(
name|KahaDBPersistenceAdapter
operator|)
name|brokerService
operator|.
name|getPersistenceAdapter
argument_list|()
expr_stmt|;
comment|// ensure there are a bunch of data files but multiple entries in each
name|adapter
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|1024
operator|*
literal|20
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIOExceptionOnCorruptJournalLocationRead
parameter_list|()
throws|throws
name|Exception
block|{
name|startBroker
argument_list|()
expr_stmt|;
name|produceMessagesToConsumeMultipleDataFiles
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|int
name|numFiles
init|=
name|getNumberOfJournalFiles
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"more than x files: "
operator|+
name|numFiles
argument_list|,
name|numFiles
operator|>
literal|4
argument_list|)
expr_stmt|;
name|corruptLocationAtDataFileIndex
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"missing one message"
argument_list|,
literal|50
argument_list|,
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTotalMessageCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Drain"
argument_list|,
literal|0
argument_list|,
name|drainQueue
argument_list|(
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"broker stopping"
argument_list|,
name|broker
operator|.
name|isStopping
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|corruptLocationAtDataFileIndex
parameter_list|(
name|int
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|Collection
argument_list|<
name|DataFile
argument_list|>
name|files
init|=
operator|(
operator|(
name|KahaDBPersistenceAdapter
operator|)
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
operator|)
operator|.
name|getStore
argument_list|()
operator|.
name|getJournal
argument_list|()
operator|.
name|getFileMap
argument_list|()
operator|.
name|values
argument_list|()
decl_stmt|;
name|DataFile
name|dataFile
init|=
operator|(
name|DataFile
operator|)
name|files
operator|.
name|toArray
argument_list|()
index|[
name|id
index|]
decl_stmt|;
name|RecoverableRandomAccessFile
name|randomAccessFile
init|=
name|dataFile
operator|.
name|openRandomAccessFile
argument_list|()
decl_stmt|;
specifier|final
name|ByteSequence
name|header
init|=
operator|new
name|ByteSequence
argument_list|(
name|Journal
operator|.
name|BATCH_CONTROL_RECORD_HEADER
argument_list|)
decl_stmt|;
name|byte
name|data
index|[]
init|=
operator|new
name|byte
index|[
literal|1024
operator|*
literal|20
index|]
decl_stmt|;
name|ByteSequence
name|bs
init|=
operator|new
name|ByteSequence
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|randomAccessFile
operator|.
name|read
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|offset
init|=
name|bs
operator|.
name|indexOf
argument_list|(
name|header
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|offset
operator|+=
name|Journal
operator|.
name|BATCH_CONTROL_RECORD_SIZE
expr_stmt|;
if|if
condition|(
name|fillLength
operator|>=
literal|10
condition|)
block|{
name|offset
operator|+=
literal|4
expr_stmt|;
comment|// location size
name|offset
operator|+=
literal|1
expr_stmt|;
comment|// location type
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Whacking batch record in file:"
operator|+
name|id
operator|+
literal|", at offset: "
operator|+
name|offset
operator|+
literal|" with fill:"
operator|+
name|fill
argument_list|)
expr_stmt|;
comment|// whack that record
name|byte
index|[]
name|bla
init|=
operator|new
name|byte
index|[
name|fillLength
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|bla
argument_list|,
name|fill
argument_list|)
expr_stmt|;
name|randomAccessFile
operator|.
name|seek
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|randomAccessFile
operator|.
name|write
argument_list|(
name|bla
argument_list|,
literal|0
argument_list|,
name|bla
operator|.
name|length
argument_list|)
expr_stmt|;
name|randomAccessFile
operator|.
name|getFD
argument_list|()
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
specifier|private
name|int
name|getNumberOfJournalFiles
parameter_list|()
throws|throws
name|IOException
block|{
name|Collection
argument_list|<
name|DataFile
argument_list|>
name|files
init|=
operator|(
operator|(
name|KahaDBPersistenceAdapter
operator|)
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
operator|)
operator|.
name|getStore
argument_list|()
operator|.
name|getJournal
argument_list|()
operator|.
name|getFileMap
argument_list|()
operator|.
name|values
argument_list|()
decl_stmt|;
name|int
name|reality
init|=
literal|0
decl_stmt|;
for|for
control|(
name|DataFile
name|file
range|:
name|files
control|)
block|{
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
block|{
name|reality
operator|++
expr_stmt|;
block|}
block|}
return|return
name|reality
return|;
block|}
specifier|private
name|int
name|produceMessages
parameter_list|(
name|Destination
name|destination
parameter_list|,
name|int
name|numToSend
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|sent
init|=
literal|0
decl_stmt|;
name|Connection
name|connection
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|broker
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getConnectUri
argument_list|()
argument_list|)
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numToSend
condition|;
name|i
operator|++
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|createMessage
argument_list|(
name|session
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|sent
operator|++
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|sent
return|;
block|}
specifier|private
name|int
name|produceMessagesToConsumeMultipleDataFiles
parameter_list|(
name|int
name|numToSend
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|produceMessages
argument_list|(
name|destination
argument_list|,
name|numToSend
argument_list|)
return|;
block|}
specifier|private
name|Message
name|createMessage
parameter_list|(
name|Session
name|session
parameter_list|,
name|int
name|i
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|session
operator|.
name|createTextMessage
argument_list|(
name|payload
operator|+
literal|"::"
operator|+
name|i
argument_list|)
return|;
block|}
specifier|private
name|int
name|drainQueue
parameter_list|(
name|int
name|max
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|drain
argument_list|(
name|cf
argument_list|,
name|destination
argument_list|,
name|max
argument_list|)
return|;
block|}
block|}
end_class

end_unit

