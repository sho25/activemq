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
name|Before
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
name|BytesMessage
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

begin_class
specifier|public
class|class
name|AMQ6522Test
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
name|AMQ6522Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|connectionFactory
decl_stmt|;
specifier|private
specifier|final
name|Destination
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"large_message_queue"
argument_list|)
decl_stmt|;
specifier|private
name|String
name|connectionUri
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
name|initBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|initBroker
parameter_list|(
name|Boolean
name|deleteAllMessages
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
name|deleteAllMessages
argument_list|)
expr_stmt|;
name|connectionUri
operator|=
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|connectionFactory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connectionUri
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
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|PolicyEntry
name|policy
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policy
operator|.
name|setUseCache
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinationPolicy
argument_list|(
operator|new
name|PolicyMap
argument_list|()
argument_list|)
expr_stmt|;
name|broker
operator|.
name|getDestinationPolicy
argument_list|()
operator|.
name|setDefaultEntry
argument_list|(
name|policy
argument_list|)
expr_stmt|;
name|KahaDBPersistenceAdapter
name|kahadb
init|=
operator|new
name|KahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|kahadb
operator|.
name|setCheckForCorruptJournalFiles
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|kahadb
operator|.
name|setPreallocationScope
argument_list|(
name|Journal
operator|.
name|PreallocationScope
operator|.
name|NONE
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|kahadb
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|verifyMessageExceedsJournalRestartRecoveryCheck
parameter_list|()
throws|throws
name|Exception
block|{
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
operator|new
name|byte
index|[
literal|33
operator|*
literal|1024
operator|*
literal|1024
index|]
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|tearDown
argument_list|()
expr_stmt|;
name|initBroker
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connection
operator|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
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
name|assertNotNull
argument_list|(
literal|"Got message after restart"
argument_list|,
name|consumer
operator|.
name|receive
argument_list|(
literal|20000
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

