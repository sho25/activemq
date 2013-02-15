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
name|bugs
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
name|Session
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
name|KahaDBPersistenceAdapter
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
name|ConsumerThread
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
name|ProducerThread
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
name|Wait
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
specifier|public
class|class
name|AMQ4323Test
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
name|AMQ4323Test
operator|.
name|class
argument_list|)
decl_stmt|;
name|BrokerService
name|broker
init|=
literal|null
decl_stmt|;
name|File
name|kahaDbDir
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
literal|"q"
argument_list|)
decl_stmt|;
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
specifier|protected
name|void
name|startBroker
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
comment|//Start with a clean directory
name|kahaDbDir
operator|=
operator|new
name|File
argument_list|(
name|broker
operator|.
name|getBrokerDataDirectory
argument_list|()
argument_list|,
literal|"KahaDB"
argument_list|)
expr_stmt|;
name|deleteDir
argument_list|(
name|kahaDbDir
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setSchedulerSupport
argument_list|(
literal|false
argument_list|)
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
literal|false
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
name|map
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|PolicyEntry
name|entry
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|setUseCache
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|map
operator|.
name|setDefaultEntry
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinationPolicy
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|configurePersistence
argument_list|(
name|broker
argument_list|,
name|delete
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
parameter_list|,
name|boolean
name|deleteAllOnStart
parameter_list|)
throws|throws
name|Exception
block|{
name|KahaDBPersistenceAdapter
name|adapter
init|=
operator|(
name|KahaDBPersistenceAdapter
operator|)
name|brokerService
operator|.
name|getPersistenceAdapter
argument_list|()
decl_stmt|;
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
literal|500
argument_list|)
expr_stmt|;
name|adapter
operator|.
name|setCleanupInterval
argument_list|(
literal|500
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|deleteAllOnStart
condition|)
block|{
name|adapter
operator|.
name|setForceRecoverIndex
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|deleteDir
parameter_list|(
name|File
name|dir
parameter_list|)
block|{
if|if
condition|(
name|dir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|String
index|[]
name|children
init|=
name|dir
operator|.
name|list
argument_list|()
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
name|children
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|success
init|=
name|deleteDir
argument_list|(
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|children
index|[
name|i
index|]
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
name|dir
operator|.
name|delete
argument_list|()
return|;
block|}
specifier|private
name|int
name|getFileCount
parameter_list|(
name|File
name|dir
parameter_list|)
block|{
if|if
condition|(
name|dir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|String
index|[]
name|children
init|=
name|dir
operator|.
name|list
argument_list|()
decl_stmt|;
return|return
name|children
operator|.
name|length
return|;
block|}
return|return
literal|0
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCleanupOfFiles
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|messageCount
init|=
literal|500
decl_stmt|;
name|startBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|int
name|fileCount
init|=
name|getFileCount
argument_list|(
name|kahaDbDir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|fileCount
argument_list|)
expr_stmt|;
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
name|Session
name|producerSess
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
name|Session
name|consumerSess
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
name|ProducerThread
name|producer
init|=
operator|new
name|ProducerThread
argument_list|(
name|producerSess
argument_list|,
name|destination
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Message
name|createMessage
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|sess
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
decl_stmt|;
name|producer
operator|.
name|setMessageCount
argument_list|(
name|messageCount
argument_list|)
expr_stmt|;
name|ConsumerThread
name|consumer
init|=
operator|new
name|ConsumerThread
argument_list|(
name|consumerSess
argument_list|,
name|destination
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|setBreakOnNull
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setMessageCount
argument_list|(
name|messageCount
argument_list|)
expr_stmt|;
name|producer
operator|.
name|start
argument_list|()
expr_stmt|;
name|producer
operator|.
name|join
argument_list|()
expr_stmt|;
name|consumer
operator|.
name|start
argument_list|()
expr_stmt|;
name|consumer
operator|.
name|join
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"consumer got all produced messages"
argument_list|,
name|producer
operator|.
name|getMessageCount
argument_list|()
argument_list|,
name|consumer
operator|.
name|getReceived
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify cleanup
name|assertTrue
argument_list|(
literal|"gc worked"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|fileCount
init|=
name|getFileCount
argument_list|(
name|kahaDbDir
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"current filecount:"
operator|+
name|fileCount
argument_list|)
expr_stmt|;
return|return
literal|4
operator|==
name|fileCount
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
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
end_class

end_unit

