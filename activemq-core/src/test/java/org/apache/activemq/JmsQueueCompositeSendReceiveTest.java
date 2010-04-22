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
package|;
end_package

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
name|Topic
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServerConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServerInvocationHandler
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
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXConnector
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXConnectorFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXServiceURL
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
name|jmx
operator|.
name|QueueViewMBean
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
name|ActiveMQDestination
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
name|test
operator|.
name|JmsTopicSendReceiveTest
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
class|class
name|JmsQueueCompositeSendReceiveTest
extends|extends
name|JmsTopicSendReceiveTest
block|{
specifier|private
specifier|static
specifier|final
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
name|LOG
init|=
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
operator|.
name|getLog
argument_list|(
name|JmsQueueCompositeSendReceiveTest
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Sets a test to have a queue destination and non-persistent delivery mode.      *      * @see junit.framework.TestCase#setUp()      */
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|topic
operator|=
literal|false
expr_stmt|;
name|deliveryMode
operator|=
name|DeliveryMode
operator|.
name|NON_PERSISTENT
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns the consumer subject.      *      * @return String - consumer subject      * @see org.apache.activemq.test.TestSupport#getConsumerSubject()      */
specifier|protected
name|String
name|getConsumerSubject
parameter_list|()
block|{
return|return
literal|"FOO.BAR.HUMBUG"
return|;
block|}
comment|/**      * Returns the producer subject.      *      * @return String - producer subject      * @see org.apache.activemq.test.TestSupport#getProducerSubject()      */
specifier|protected
name|String
name|getProducerSubject
parameter_list|()
block|{
return|return
literal|"FOO.BAR.HUMBUG,FOO.BAR.HUMBUG2"
return|;
block|}
comment|/**      * Test if all the messages sent are being received.      *      * @throws Exception      */
specifier|public
name|void
name|testSendReceive
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testSendReceive
argument_list|()
expr_stmt|;
name|messages
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Destination
name|consumerDestination
init|=
name|consumeSession
operator|.
name|createQueue
argument_list|(
literal|"FOO.BAR.HUMBUG2"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Created  consumer destination: "
operator|+
name|consumerDestination
operator|+
literal|" of type: "
operator|+
name|consumerDestination
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|durable
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating durable consumer"
argument_list|)
expr_stmt|;
name|consumer
operator|=
name|consumeSession
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|consumerDestination
argument_list|,
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|consumer
operator|=
name|consumeSession
operator|.
name|createConsumer
argument_list|(
name|consumerDestination
argument_list|)
expr_stmt|;
block|}
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|assertMessagesAreReceived
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|""
operator|+
name|data
operator|.
name|length
operator|+
literal|" messages(s) received, closing down connections"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDuplicate
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQDestination
name|queue
init|=
operator|(
name|ActiveMQDestination
operator|)
name|session
operator|.
name|createQueue
argument_list|(
literal|"TEST,TEST"
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
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|configureMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|verbose
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"About to send a message: "
operator|+
name|message
operator|+
literal|" with text: "
operator|+
name|data
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|producer
operator|.
name|send
argument_list|(
name|queue
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
name|JMXServiceURL
name|url
init|=
operator|new
name|JMXServiceURL
argument_list|(
literal|"service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi"
argument_list|)
decl_stmt|;
name|JMXConnector
name|connector
init|=
name|JMXConnectorFactory
operator|.
name|connect
argument_list|(
name|url
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|connector
operator|.
name|connect
argument_list|()
expr_stmt|;
name|MBeanServerConnection
name|connection
init|=
name|connector
operator|.
name|getMBeanServerConnection
argument_list|()
decl_stmt|;
name|ObjectName
name|queueViewMBeanName
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:Type=Queue,Destination=TEST,BrokerName=localhost"
argument_list|)
decl_stmt|;
name|QueueViewMBean
name|queueMbean
init|=
operator|(
name|QueueViewMBean
operator|)
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|connection
argument_list|,
name|queueViewMBeanName
argument_list|,
name|QueueViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|data
operator|.
name|length
argument_list|,
name|queueMbean
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|queueMbean
operator|.
name|purge
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queueMbean
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

