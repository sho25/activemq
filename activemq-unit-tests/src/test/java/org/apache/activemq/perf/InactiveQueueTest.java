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
name|perf
package|;
end_package

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
name|MapMessage
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|InactiveQueueTest
extends|extends
name|TestCase
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
name|InactiveQueueTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|0
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_PASSWORD
init|=
literal|""
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|USERNAME
init|=
literal|"testuser"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|QUEUE_NAME
init|=
literal|"testevent"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DELIVERY_MODE
init|=
name|javax
operator|.
name|jms
operator|.
name|DeliveryMode
operator|.
name|PERSISTENT
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DELIVERY_PRIORITY
init|=
name|javax
operator|.
name|jms
operator|.
name|Message
operator|.
name|DEFAULT_PRIORITY
decl_stmt|;
name|ActiveMQConnectionFactory
name|connectionFactory
decl_stmt|;
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|private
name|MessageProducer
name|publisher
decl_stmt|;
specifier|private
name|Destination
name|destination
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
comment|// broker.setPersistenceAdapter(new KahaPersistenceAdapter(new File
comment|// ("TEST_STUFD")));
comment|/*          * JournalPersistenceAdapterFactory factory = new          * JournalPersistenceAdapterFactory();          * factory.setDataDirectoryFile(broker.getDataDirectory());          * factory.setTaskRunnerFactory(broker.getTaskRunnerFactory());          * factory.setUseJournal(false); broker.setPersistenceFactory(factory);          */
name|broker
operator|.
name|addConnector
argument_list|(
name|ActiveMQConnectionFactory
operator|.
name|DEFAULT_BROKER_BIND_URL
argument_list|)
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
name|ActiveMQConnectionFactory
operator|.
name|DEFAULT_BROKER_URL
argument_list|)
expr_stmt|;
comment|/*          * Doesn't matter if you enable or disable these, so just leaving them          * out for this test case connectionFactory.setAlwaysSessionAsync(true);          * connectionFactory.setAsyncDispatch(true);          */
name|connectionFactory
operator|.
name|setUseAsyncSend
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testNoSubscribers
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|=
name|connectionFactory
operator|.
name|createConnection
argument_list|(
name|USERNAME
argument_list|,
name|DEFAULT_PASSWORD
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|session
operator|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|javax
operator|.
name|jms
operator|.
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|session
argument_list|)
expr_stmt|;
name|destination
operator|=
name|session
operator|.
name|createQueue
argument_list|(
name|QUEUE_NAME
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|publisher
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|publisher
argument_list|)
expr_stmt|;
name|MapMessage
name|msg
init|=
name|session
operator|.
name|createMapMessage
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|msg
operator|.
name|setString
argument_list|(
literal|"key1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|int
name|loop
decl_stmt|;
for|for
control|(
name|loop
operator|=
literal|0
init|;
name|loop
operator|<
name|MESSAGE_COUNT
condition|;
name|loop
operator|++
control|)
block|{
name|msg
operator|.
name|setInt
argument_list|(
literal|"key2"
argument_list|,
name|loop
argument_list|)
expr_stmt|;
name|publisher
operator|.
name|send
argument_list|(
name|msg
argument_list|,
name|DELIVERY_MODE
argument_list|,
name|DELIVERY_PRIORITY
argument_list|,
name|Message
operator|.
name|DEFAULT_TIME_TO_LIVE
argument_list|)
expr_stmt|;
if|if
condition|(
name|loop
operator|%
literal|500
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sent "
operator|+
name|loop
operator|+
literal|" messages"
argument_list|)
expr_stmt|;
block|}
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|loop
argument_list|,
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
name|publisher
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|stop
argument_list|()
expr_stmt|;
name|connection
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

