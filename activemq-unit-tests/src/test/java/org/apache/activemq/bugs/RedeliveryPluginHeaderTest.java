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
name|DeliveryMode
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
name|jms
operator|.
name|TextMessage
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
name|RedeliveryPolicy
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
name|BrokerPlugin
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
name|TransportConnector
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
name|RedeliveryPolicyMap
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
name|util
operator|.
name|RedeliveryPlugin
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
name|IOHelper
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
comment|/**  * Testing if the the broker "sends" the message as expected after the redeliveryPlugin has redelivered the  * message previously.  */
end_comment

begin_class
specifier|public
class|class
name|RedeliveryPluginHeaderTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|TEST_QUEUE_ONE
init|=
literal|"TEST_QUEUE_ONE"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_QUEUE_TWO
init|=
literal|"TEST_QUEUE_TWO"
decl_stmt|;
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
name|RedeliveryPluginHeaderTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|String
name|transportURL
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
comment|/**      * Test      * - consumes message from Queue1      * - rolls back message to Queue1 and message is scheduled for redelivery to Queue1 by brokers plugin      * - consumes message from Queue1 again      * - sends same message to Queue2      * - expects to consume message from Queue2 immediately      */
specifier|public
name|void
name|testSendAfterRedelivery
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
name|this
operator|.
name|createBroker
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"***Broker started..."
argument_list|)
expr_stmt|;
comment|//pushed message to broker
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|transportURL
operator|+
literal|"?trace=true&jms.redeliveryPolicy.maximumRedeliveries=0&jms.redeliveryPolicy.preDispatchCheck=true"
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|factory
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
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
name|Destination
name|destinationQ1
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|TEST_QUEUE_ONE
argument_list|)
decl_stmt|;
name|Destination
name|destinationQ2
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|TEST_QUEUE_TWO
argument_list|)
decl_stmt|;
name|MessageProducer
name|producerQ1
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destinationQ1
argument_list|)
decl_stmt|;
name|producerQ1
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|Message
name|m
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"testMessage"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"*** send message to broker..."
argument_list|)
expr_stmt|;
name|producerQ1
operator|.
name|send
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|//consume message from Q1 and rollback to get it redelivered
name|MessageConsumer
name|consumerQ1
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destinationQ1
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"*** consume message from Q1 and rolled back.."
argument_list|)
expr_stmt|;
name|TextMessage
name|textMessage
init|=
operator|(
name|TextMessage
operator|)
name|consumerQ1
operator|.
name|receive
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"got redelivered: "
operator|+
name|textMessage
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"JMSRedelivered flag is not set"
argument_list|,
name|textMessage
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"*** consumed message from Q1 again and sending to Q2.."
argument_list|)
expr_stmt|;
name|TextMessage
name|textMessage2
init|=
operator|(
name|TextMessage
operator|)
name|consumerQ1
operator|.
name|receive
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"got: "
operator|+
name|textMessage2
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"JMSRedelivered flag is set"
argument_list|,
name|textMessage2
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
comment|//send message to Q2 and consume from Q2
name|MessageConsumer
name|consumerQ2
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destinationQ2
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer_two
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destinationQ2
argument_list|)
decl_stmt|;
name|producer_two
operator|.
name|send
argument_list|(
name|textMessage2
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|//Message should be available straight away on the queue_two
name|Message
name|textMessage3
init|=
name|consumerQ2
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"should have consumed a message from TEST_QUEUE_TWO"
argument_list|,
name|textMessage3
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"JMSRedelivered flag is not set"
argument_list|,
name|textMessage3
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
block|}
block|}
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|boolean
name|withJMX
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|schedulerDirectory
init|=
operator|new
name|File
argument_list|(
literal|"target/scheduler"
argument_list|)
decl_stmt|;
name|IOHelper
operator|.
name|mkdirs
argument_list|(
name|schedulerDirectory
argument_list|)
expr_stmt|;
name|IOHelper
operator|.
name|deleteChildren
argument_list|(
name|schedulerDirectory
argument_list|)
expr_stmt|;
name|BrokerService
name|answer
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|answer
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setDataDirectory
argument_list|(
literal|"target"
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setSchedulerDirectoryFile
argument_list|(
name|schedulerDirectory
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setSchedulerSupport
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setUseJmx
argument_list|(
name|withJMX
argument_list|)
expr_stmt|;
name|RedeliveryPlugin
name|redeliveryPlugin
init|=
operator|new
name|RedeliveryPlugin
argument_list|()
decl_stmt|;
name|RedeliveryPolicyMap
name|redeliveryPolicyMap
init|=
operator|new
name|RedeliveryPolicyMap
argument_list|()
decl_stmt|;
name|RedeliveryPolicy
name|defaultEntry
init|=
operator|new
name|RedeliveryPolicy
argument_list|()
decl_stmt|;
name|defaultEntry
operator|.
name|setInitialRedeliveryDelay
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|defaultEntry
operator|.
name|setMaximumRedeliveries
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|redeliveryPolicyMap
operator|.
name|setDefaultEntry
argument_list|(
name|defaultEntry
argument_list|)
expr_stmt|;
name|redeliveryPlugin
operator|.
name|setRedeliveryPolicyMap
argument_list|(
name|redeliveryPolicyMap
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setPlugins
argument_list|(
operator|new
name|BrokerPlugin
index|[]
block|{
name|redeliveryPlugin
block|}
argument_list|)
expr_stmt|;
name|TransportConnector
name|transportConnector
init|=
name|answer
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
decl_stmt|;
name|transportURL
operator|=
name|transportConnector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
expr_stmt|;
return|return
name|answer
return|;
block|}
block|}
end_class

end_unit

