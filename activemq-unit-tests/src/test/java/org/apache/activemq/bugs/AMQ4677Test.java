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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
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
name|FilenameFilter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
import|import
name|javax
operator|.
name|jms
operator|.
name|*
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
name|leveldb
operator|.
name|LevelDBStore
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
name|leveldb
operator|.
name|LevelDBStoreViewMBean
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
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|rules
operator|.
name|TestName
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

begin_class
specifier|public
class|class
name|AMQ4677Test
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AMQ4677Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|BrokerService
name|brokerService
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|TestName
name|name
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
specifier|private
name|File
name|dataDirFile
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|dataDirFile
operator|=
operator|new
name|File
argument_list|(
literal|"target/LevelDBCleanupTest"
argument_list|)
expr_stmt|;
name|brokerService
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|setBrokerName
argument_list|(
literal|"LevelDBBroker"
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setDataDirectoryFile
argument_list|(
name|dataDirFile
argument_list|)
expr_stmt|;
name|LevelDBStore
name|persistenceFactory
init|=
operator|new
name|LevelDBStore
argument_list|()
decl_stmt|;
name|persistenceFactory
operator|.
name|setDirectory
argument_list|(
name|dataDirFile
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setPersistenceAdapter
argument_list|(
name|persistenceFactory
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|waitUntilStarted
argument_list|()
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
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSendAndReceiveAllMessages
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://LevelDBBroker"
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Destination
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|name
operator|.
name|toString
argument_list|()
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
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
specifier|final
name|LevelDBStoreViewMBean
name|levelDBView
init|=
name|getLevelDBStoreMBean
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|levelDBView
argument_list|)
expr_stmt|;
name|levelDBView
operator|.
name|compact
argument_list|()
expr_stmt|;
specifier|final
name|int
name|SIZE
init|=
literal|6
operator|*
literal|1024
operator|*
literal|5
decl_stmt|;
specifier|final
name|int
name|MSG_COUNT
init|=
literal|60000
decl_stmt|;
specifier|final
name|CountDownLatch
name|done
init|=
operator|new
name|CountDownLatch
argument_list|(
name|MSG_COUNT
argument_list|)
decl_stmt|;
name|byte
name|buffer
index|[]
init|=
operator|new
name|byte
index|[
name|SIZE
index|]
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
name|SIZE
condition|;
operator|++
name|i
control|)
block|{
name|buffer
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
literal|128
expr_stmt|;
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
name|MSG_COUNT
condition|;
operator|++
name|i
control|)
block|{
name|BytesMessage
name|message
init|=
name|session
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|writeBytes
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|i
operator|%
literal|1000
operator|)
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Sent message #{}"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finished sending all messages."
argument_list|)
expr_stmt|;
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
name|consumer
operator|.
name|setMessageListener
argument_list|(
operator|new
name|MessageListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
if|if
condition|(
operator|(
name|done
operator|.
name|getCount
argument_list|()
operator|%
literal|1000
operator|)
operator|==
literal|0
condition|)
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Received message #{}"
argument_list|,
name|MSG_COUNT
operator|-
name|done
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{                     }
block|}
name|done
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|done
operator|.
name|await
argument_list|(
literal|15
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finished receiving all messages."
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should< 3 logfiles left."
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
name|levelDBView
operator|.
name|compact
argument_list|()
expr_stmt|;
return|return
name|countLogFiles
argument_list|()
operator|<
literal|3
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|5
argument_list|)
argument_list|,
operator|(
name|int
operator|)
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|30
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|levelDBView
operator|.
name|compact
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Current number of logs {}"
argument_list|,
name|countLogFiles
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|long
name|countLogFiles
parameter_list|()
block|{
name|String
index|[]
name|logFiles
init|=
name|dataDirFile
operator|.
name|list
argument_list|(
operator|new
name|FilenameFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|.
name|endsWith
argument_list|(
literal|"log"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Current number of logs {}"
argument_list|,
name|logFiles
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|logFiles
operator|.
name|length
return|;
block|}
specifier|protected
name|LevelDBStoreViewMBean
name|getLevelDBStoreMBean
parameter_list|()
throws|throws
name|Exception
block|{
name|ObjectName
name|levelDbViewMBeanQuery
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName=LevelDBBroker,service=PersistenceAdapter,instanceName=LevelDB*"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|names
init|=
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|queryNames
argument_list|(
literal|null
argument_list|,
name|levelDbViewMBeanQuery
argument_list|)
decl_stmt|;
if|if
condition|(
name|names
operator|.
name|isEmpty
argument_list|()
operator|||
name|names
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|java
operator|.
name|lang
operator|.
name|IllegalStateException
argument_list|(
literal|"Can't find levelDB store name."
argument_list|)
throw|;
block|}
name|LevelDBStoreViewMBean
name|proxy
init|=
operator|(
name|LevelDBStoreViewMBean
operator|)
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|names
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|,
name|LevelDBStoreViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|proxy
return|;
block|}
block|}
end_class

end_unit

