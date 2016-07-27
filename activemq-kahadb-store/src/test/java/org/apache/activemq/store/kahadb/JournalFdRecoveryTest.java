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
name|JMSException
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
name|MessageConsumer
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
name|javax
operator|.
name|management
operator|.
name|Attribute
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

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
name|IOException
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|assertNotEquals
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
specifier|public
class|class
name|JournalFdRecoveryTest
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
name|JournalFdRecoveryTest
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
specifier|protected
name|void
name|restartBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|dataDir
init|=
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
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
name|whackIndex
argument_list|(
name|dataDir
argument_list|)
expr_stmt|;
name|doStartBroker
argument_list|(
literal|false
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
name|policyEntry
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policyEntry
operator|.
name|setUseCache
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|policyMap
operator|.
name|setDefaultEntry
argument_list|(
name|policyEntry
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
comment|// speed up the test case, checkpoint an cleanup early and often
name|adapter
operator|.
name|setCheckpointInterval
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|adapter
operator|.
name|setCleanupInterval
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|adapter
operator|.
name|setCheckForCorruptJournalFiles
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|adapter
operator|.
name|setIgnoreMissingJournalfiles
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|adapter
operator|.
name|setPreallocationScope
argument_list|(
name|Journal
operator|.
name|PreallocationScope
operator|.
name|ENTIRE_JOURNAL_ASYNC
operator|.
name|name
argument_list|()
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
name|testStopOnPageInIOError
parameter_list|()
throws|throws
name|Exception
block|{
name|startBroker
argument_list|()
expr_stmt|;
name|int
name|sent
init|=
name|produceMessagesToConsumeMultipleDataFiles
argument_list|(
literal|50
argument_list|)
decl_stmt|;
name|int
name|numFiles
init|=
name|getNumberOfJournalFiles
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Num journal files: "
operator|+
name|numFiles
argument_list|)
expr_stmt|;
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
name|File
name|dataDir
init|=
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|2
init|;
name|i
operator|<
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|whackDataFile
argument_list|(
name|dataDir
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
specifier|final
name|CountDownLatch
name|gotShutdown
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|broker
operator|.
name|addShutdownHook
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|gotShutdown
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|int
name|received
init|=
name|tryConsume
argument_list|(
name|destination
argument_list|,
name|sent
argument_list|)
decl_stmt|;
name|assertNotEquals
argument_list|(
literal|"not all message received"
argument_list|,
name|sent
argument_list|,
name|received
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"broker got shutdown on page in error"
argument_list|,
name|gotShutdown
operator|.
name|await
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|whackDataFile
parameter_list|(
name|File
name|dataDir
parameter_list|,
name|int
name|i
parameter_list|)
throws|throws
name|Exception
block|{
name|whackFile
argument_list|(
name|dataDir
argument_list|,
literal|"db-"
operator|+
name|i
operator|+
literal|".log"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRecoveryAfterCorruption
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Num journal files: "
operator|+
name|numFiles
argument_list|)
expr_stmt|;
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
name|File
name|dataDir
init|=
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
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
name|long
name|afterStop
init|=
name|totalOpenFileDescriptorCount
argument_list|(
name|broker
argument_list|)
decl_stmt|;
name|whackIndex
argument_list|(
name|dataDir
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Num Open files with broker stopped: "
operator|+
name|afterStop
argument_list|)
expr_stmt|;
name|doStartBroker
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Journal read pool: "
operator|+
name|adapter
operator|.
name|getStore
argument_list|()
operator|.
name|getJournal
argument_list|()
operator|.
name|getAccessorPool
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"one entry in the pool on start"
argument_list|,
literal|1
argument_list|,
name|adapter
operator|.
name|getStore
argument_list|()
operator|.
name|getJournal
argument_list|()
operator|.
name|getAccessorPool
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|afterRecovery
init|=
name|totalOpenFileDescriptorCount
argument_list|(
name|broker
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Num Open files with broker recovered: "
operator|+
name|afterRecovery
argument_list|)
expr_stmt|;
block|}
specifier|private
name|long
name|totalOpenFileDescriptorCount
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
block|{
name|long
name|result
init|=
literal|0
decl_stmt|;
try|try
block|{
name|javax
operator|.
name|management
operator|.
name|AttributeList
name|list
init|=
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|getMBeanServer
argument_list|()
operator|.
name|getAttributes
argument_list|(
operator|new
name|ObjectName
argument_list|(
literal|"java.lang:type=OperatingSystem"
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"OpenFileDescriptorCount"
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|result
operator|=
operator|(
call|(
name|Long
call|)
argument_list|(
operator|(
name|Attribute
operator|)
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|getValue
argument_list|()
operator|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ignored
parameter_list|)
block|{         }
return|return
name|result
return|;
block|}
specifier|private
name|void
name|whackIndex
parameter_list|(
name|File
name|dataDir
parameter_list|)
throws|throws
name|Exception
block|{
name|whackFile
argument_list|(
name|dataDir
argument_list|,
literal|"db.data"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|whackFile
parameter_list|(
name|File
name|dataDir
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|indexToDelete
init|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Whacking index: "
operator|+
name|indexToDelete
argument_list|)
expr_stmt|;
name|indexToDelete
operator|.
name|delete
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
name|tryConsume
parameter_list|(
name|Destination
name|destination
parameter_list|,
name|int
name|numToGet
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|got
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
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
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
name|numToGet
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|consumer
operator|.
name|receive
argument_list|(
literal|4000
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// give up on timeout or error
break|break;
block|}
name|got
operator|++
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|ok
parameter_list|)
block|{         }
finally|finally
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|got
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
block|}
end_class

end_unit

