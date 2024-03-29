begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/* ====================================================================    Licensed to the Apache Software Foundation (ASF) under one or more    contributor license agreements.  See the NOTICE file distributed with    this work for additional information regarding copyright ownership.    The ASF licenses this file to You under the Apache License, Version 2.0    (the "License"); you may not use this file except in compliance with    the License.  You may obtain a copy of the License at         http://www.apache.org/licenses/LICENSE-2.0     Unless required by applicable law or agreed to in writing, software    distributed under the License is distributed on an "AS IS" BASIS,    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.    See the License for the specific language governing permissions and    limitations under the License. ==================================================================== */
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
operator|.
name|amq1095
package|;
end_package

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
name|javax
operator|.
name|jms
operator|.
name|Topic
import|;
end_import

begin_comment
comment|/**  *<p>  * Test cases for various ActiveMQ functionalities.  *</p>  *  *<ul>  *<li>  *<p>  * Durable subscriptions are used.  *</p>  *</li>  *<li>  *<p>  * The Kaha persistence manager is used.  *</p>  *</li>  *<li>  *<p>  * An already existing Kaha directory is used. Everything runs fine if the  * ActiveMQ broker creates a new Kaha directory.  *</p>  *</li>  *</ul>  *  * @author Rainer Klute<a  *         href="mailto:rainer.klute@dp-itsolutions.de">&lt;rainer.klute@dp-itsolutions.de&gt;</a>  * @since 2007-08-09  * @version $Id: MessageSelectorTest.java 12 2007-08-14 12:02:02Z rke $  */
end_comment

begin_class
specifier|public
class|class
name|MessageSelectorTest
extends|extends
name|ActiveMQTestCase
block|{
specifier|private
name|MessageConsumer
name|consumer1
decl_stmt|;
specifier|private
name|MessageConsumer
name|consumer2
decl_stmt|;
comment|/**<p>Constructor</p> */
specifier|public
name|MessageSelectorTest
parameter_list|()
block|{}
comment|/**<p>Constructor</p>      * @param name the test case's name      */
specifier|public
name|MessageSelectorTest
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**      *<p>      * Tests whether message selectors work for durable subscribers.      *</p>      */
specifier|public
name|void
name|testMessageSelectorForDurableSubscribersRunA
parameter_list|()
block|{
name|runMessageSelectorTest
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      *<p>      * Tests whether message selectors work for durable subscribers.      *</p>      */
specifier|public
name|void
name|testMessageSelectorForDurableSubscribersRunB
parameter_list|()
block|{
name|runMessageSelectorTest
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      *<p>      * Tests whether message selectors work for non-durable subscribers.      *</p>      */
specifier|public
name|void
name|testMessageSelectorForNonDurableSubscribers
parameter_list|()
block|{
name|runMessageSelectorTest
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      *<p>      * Tests whether message selectors work. This is done by sending two      * messages to a topic. Both have an int property with different values. Two      * subscribers use message selectors to receive the messages. Each one      * should receive exactly one of the messages.      *</p>      */
specifier|private
name|void
name|runMessageSelectorTest
parameter_list|(
specifier|final
name|boolean
name|isDurableSubscriber
parameter_list|)
block|{
try|try
block|{
specifier|final
name|String
name|PROPERTY_CONSUMER
init|=
literal|"consumer"
decl_stmt|;
specifier|final
name|String
name|CONSUMER_1
init|=
literal|"Consumer 1"
decl_stmt|;
specifier|final
name|String
name|CONSUMER_2
init|=
literal|"Consumer 2"
decl_stmt|;
specifier|final
name|String
name|MESSAGE_1
init|=
literal|"Message to "
operator|+
name|CONSUMER_1
decl_stmt|;
specifier|final
name|String
name|MESSAGE_2
init|=
literal|"Message to "
operator|+
name|CONSUMER_2
decl_stmt|;
name|assertNotNull
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|destination
argument_list|)
expr_stmt|;
specifier|final
name|Session
name|producingSession
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
specifier|final
name|MessageProducer
name|producer
init|=
name|producingSession
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
specifier|final
name|Session
name|consumingSession1
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
specifier|final
name|Session
name|consumingSession2
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
if|if
condition|(
name|isDurableSubscriber
condition|)
block|{
name|consumer1
operator|=
name|consumingSession1
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|destination
argument_list|,
name|CONSUMER_1
argument_list|,
name|PROPERTY_CONSUMER
operator|+
literal|" = 1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|consumer2
operator|=
name|consumingSession2
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|destination
argument_list|,
name|CONSUMER_2
argument_list|,
name|PROPERTY_CONSUMER
operator|+
literal|" = 2"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|consumer1
operator|=
name|consumingSession1
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|,
name|PROPERTY_CONSUMER
operator|+
literal|" = 1"
argument_list|)
expr_stmt|;
name|consumer2
operator|=
name|consumingSession2
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|,
name|PROPERTY_CONSUMER
operator|+
literal|" = 2"
argument_list|)
expr_stmt|;
block|}
name|registerToBeEmptiedOnShutdown
argument_list|(
name|consumer1
argument_list|)
expr_stmt|;
name|registerToBeEmptiedOnShutdown
argument_list|(
name|consumer2
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|TextMessage
name|msg1
decl_stmt|;
name|TextMessage
name|msg2
decl_stmt|;
name|int
name|propertyValue
decl_stmt|;
name|String
name|contents
decl_stmt|;
comment|/* Try to receive any messages from the consumers. There shouldn't be any yet. */
name|msg1
operator|=
operator|(
name|TextMessage
operator|)
name|consumer1
operator|.
name|receive
argument_list|(
name|RECEIVE_TIMEOUT
argument_list|)
expr_stmt|;
if|if
condition|(
name|msg1
operator|!=
literal|null
condition|)
block|{
specifier|final
name|StringBuffer
name|msg
init|=
operator|new
name|StringBuffer
argument_list|(
literal|"The consumer read a message that was left over from a former ActiveMQ broker run."
argument_list|)
decl_stmt|;
name|propertyValue
operator|=
name|msg1
operator|.
name|getIntProperty
argument_list|(
name|PROPERTY_CONSUMER
argument_list|)
expr_stmt|;
name|contents
operator|=
name|msg1
operator|.
name|getText
argument_list|()
expr_stmt|;
if|if
condition|(
name|propertyValue
operator|!=
literal|1
condition|)
comment|// Is the property value as expected?
block|{
name|msg
operator|.
name|append
argument_list|(
literal|" That message does not match the consumer's message selector."
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|msg
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|propertyValue
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MESSAGE_1
argument_list|,
name|contents
argument_list|)
expr_stmt|;
block|}
name|msg2
operator|=
operator|(
name|TextMessage
operator|)
name|consumer2
operator|.
name|receive
argument_list|(
name|RECEIVE_TIMEOUT
argument_list|)
expr_stmt|;
if|if
condition|(
name|msg2
operator|!=
literal|null
condition|)
block|{
specifier|final
name|StringBuffer
name|msg
init|=
operator|new
name|StringBuffer
argument_list|(
literal|"The consumer read a message that was left over from a former ActiveMQ broker run."
argument_list|)
decl_stmt|;
name|propertyValue
operator|=
name|msg2
operator|.
name|getIntProperty
argument_list|(
name|PROPERTY_CONSUMER
argument_list|)
expr_stmt|;
name|contents
operator|=
name|msg2
operator|.
name|getText
argument_list|()
expr_stmt|;
if|if
condition|(
name|propertyValue
operator|!=
literal|2
condition|)
comment|// Is the property value as expected?
block|{
name|msg
operator|.
name|append
argument_list|(
literal|" That message does not match the consumer's message selector."
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|msg
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|propertyValue
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MESSAGE_2
argument_list|,
name|contents
argument_list|)
expr_stmt|;
block|}
comment|/* Send two messages. Each is targeted at one of the consumers. */
name|TextMessage
name|msg
decl_stmt|;
name|msg
operator|=
name|producingSession
operator|.
name|createTextMessage
argument_list|()
expr_stmt|;
name|msg
operator|.
name|setText
argument_list|(
name|MESSAGE_1
argument_list|)
expr_stmt|;
name|msg
operator|.
name|setIntProperty
argument_list|(
name|PROPERTY_CONSUMER
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|msg
operator|=
name|producingSession
operator|.
name|createTextMessage
argument_list|()
expr_stmt|;
name|msg
operator|.
name|setText
argument_list|(
name|MESSAGE_2
argument_list|)
expr_stmt|;
name|msg
operator|.
name|setIntProperty
argument_list|(
name|PROPERTY_CONSUMER
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
comment|/* Receive the messages that have just been sent. */
comment|/* Use consumer 1 to receive one of the messages. The receive()              * method is called twice to make sure there is nothing else in              * stock for this consumer. */
name|msg1
operator|=
operator|(
name|TextMessage
operator|)
name|consumer1
operator|.
name|receive
argument_list|(
name|RECEIVE_TIMEOUT
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|msg1
argument_list|)
expr_stmt|;
name|propertyValue
operator|=
name|msg1
operator|.
name|getIntProperty
argument_list|(
name|PROPERTY_CONSUMER
argument_list|)
expr_stmt|;
name|contents
operator|=
name|msg1
operator|.
name|getText
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|propertyValue
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MESSAGE_1
argument_list|,
name|contents
argument_list|)
expr_stmt|;
name|msg1
operator|=
operator|(
name|TextMessage
operator|)
name|consumer1
operator|.
name|receive
argument_list|(
name|RECEIVE_TIMEOUT
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|msg1
argument_list|)
expr_stmt|;
comment|/* Use consumer 2 to receive the other message. The receive()              * method is called twice to make sure there is nothing else in              * stock for this consumer. */
name|msg2
operator|=
operator|(
name|TextMessage
operator|)
name|consumer2
operator|.
name|receive
argument_list|(
name|RECEIVE_TIMEOUT
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|msg2
argument_list|)
expr_stmt|;
name|propertyValue
operator|=
name|msg2
operator|.
name|getIntProperty
argument_list|(
name|PROPERTY_CONSUMER
argument_list|)
expr_stmt|;
name|contents
operator|=
name|msg2
operator|.
name|getText
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|propertyValue
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MESSAGE_2
argument_list|,
name|contents
argument_list|)
expr_stmt|;
name|msg2
operator|=
operator|(
name|TextMessage
operator|)
name|consumer2
operator|.
name|receive
argument_list|(
name|RECEIVE_TIMEOUT
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|msg2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

