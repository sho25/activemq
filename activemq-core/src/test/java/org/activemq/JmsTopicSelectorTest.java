begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a> * * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. * **/
end_comment

begin_package
package|package
name|org
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
comment|/**  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|JmsTopicSelectorTest
extends|extends
name|TestSupport
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
name|log
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
name|JmsTopicSelectorTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|Connection
name|connection
decl_stmt|;
specifier|protected
name|Session
name|session
decl_stmt|;
specifier|protected
name|MessageConsumer
name|consumer
decl_stmt|;
specifier|protected
name|MessageProducer
name|producer
decl_stmt|;
specifier|protected
name|Destination
name|consumerDestination
decl_stmt|;
specifier|protected
name|Destination
name|producerDestination
decl_stmt|;
specifier|protected
name|boolean
name|topic
init|=
literal|true
decl_stmt|;
specifier|protected
name|boolean
name|durable
init|=
literal|false
decl_stmt|;
specifier|protected
name|int
name|deliveryMode
init|=
name|DeliveryMode
operator|.
name|PERSISTENT
decl_stmt|;
specifier|public
name|JmsTopicSelectorTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|JmsTopicSelectorTest
parameter_list|(
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
specifier|public
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
name|connectionFactory
operator|=
name|createConnectionFactory
argument_list|()
expr_stmt|;
name|connection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
if|if
condition|(
name|durable
condition|)
block|{
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
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Created connection: "
operator|+
name|connection
argument_list|)
expr_stmt|;
name|session
operator|=
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
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Created session: "
operator|+
name|session
argument_list|)
expr_stmt|;
if|if
condition|(
name|topic
condition|)
block|{
name|consumerDestination
operator|=
name|session
operator|.
name|createTopic
argument_list|(
name|getConsumerSubject
argument_list|()
argument_list|)
expr_stmt|;
name|producerDestination
operator|=
name|session
operator|.
name|createTopic
argument_list|(
name|getProducerSubject
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|consumerDestination
operator|=
name|session
operator|.
name|createQueue
argument_list|(
name|getConsumerSubject
argument_list|()
argument_list|)
expr_stmt|;
name|producerDestination
operator|=
name|session
operator|.
name|createQueue
argument_list|(
name|getProducerSubject
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|log
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
name|log
operator|.
name|info
argument_list|(
literal|"Created  producer destination: "
operator|+
name|producerDestination
operator|+
literal|" of type: "
operator|+
name|producerDestination
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|producer
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|producerDestination
argument_list|)
expr_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|deliveryMode
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Created producer: "
operator|+
name|producer
operator|+
literal|" delivery mode = "
operator|+
operator|(
name|deliveryMode
operator|==
name|DeliveryMode
operator|.
name|PERSISTENT
condition|?
literal|"PERSISTENT"
else|:
literal|"NON_PERSISTENT"
operator|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|MessageConsumer
name|createConsumer
parameter_list|(
name|String
name|selector
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|durable
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Creating durable consumer"
argument_list|)
expr_stmt|;
return|return
name|session
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
argument_list|,
name|selector
argument_list|,
literal|false
argument_list|)
return|;
block|}
return|return
name|session
operator|.
name|createConsumer
argument_list|(
name|consumerDestination
argument_list|,
name|selector
argument_list|)
return|;
block|}
specifier|public
name|void
name|sendMessages
parameter_list|()
throws|throws
name|Exception
block|{
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"1"
argument_list|)
decl_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|message
operator|.
name|setJMSType
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"stringProperty"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setLongProperty
argument_list|(
literal|"longProperty"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|message
operator|.
name|setBooleanProperty
argument_list|(
literal|"booleanProperty"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|message
operator|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"2"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"id"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|message
operator|.
name|setJMSType
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"stringProperty"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setLongProperty
argument_list|(
literal|"longProperty"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|message
operator|.
name|setBooleanProperty
argument_list|(
literal|"booleanProperty"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|message
operator|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"3"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"id"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|message
operator|.
name|setJMSType
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"stringProperty"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setLongProperty
argument_list|(
literal|"longProperty"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|message
operator|.
name|setBooleanProperty
argument_list|(
literal|"booleanProperty"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|message
operator|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"4"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"id"
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|message
operator|.
name|setJMSType
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"stringProperty"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setLongProperty
argument_list|(
literal|"longProperty"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|message
operator|.
name|setBooleanProperty
argument_list|(
literal|"booleanProperty"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|message
operator|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"5"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"id"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|message
operator|.
name|setJMSType
argument_list|(
literal|"c"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"stringProperty"
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setLongProperty
argument_list|(
literal|"longProperty"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|message
operator|.
name|setBooleanProperty
argument_list|(
literal|"booleanProperty"
argument_list|,
literal|true
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
specifier|public
name|void
name|consumeMessages
parameter_list|(
name|int
name|remaining
parameter_list|)
throws|throws
name|Exception
block|{
name|consumer
operator|=
name|createConsumer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|remaining
condition|;
name|i
operator|++
control|)
block|{
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testPropertySelector
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|remaining
init|=
literal|5
decl_stmt|;
name|Message
name|message
init|=
literal|null
decl_stmt|;
name|consumer
operator|=
name|createConsumer
argument_list|(
literal|"stringProperty = 'a' and longProperty = 1 and booleanProperty = true"
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|message
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|String
name|text
init|=
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|text
operator|.
name|equals
argument_list|(
literal|"1"
argument_list|)
operator|&&
operator|!
name|text
operator|.
name|equals
argument_list|(
literal|"3"
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"unexpected message: "
operator|+
name|text
argument_list|)
expr_stmt|;
block|}
name|remaining
operator|--
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|remaining
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumeMessages
argument_list|(
name|remaining
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testJMSPropertySelector
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|remaining
init|=
literal|5
decl_stmt|;
name|Message
name|message
init|=
literal|null
decl_stmt|;
name|consumer
operator|=
name|createConsumer
argument_list|(
literal|"JMSType = 'a' and stringProperty = 'a'"
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|message
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|String
name|text
init|=
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|text
operator|.
name|equals
argument_list|(
literal|"1"
argument_list|)
operator|&&
operator|!
name|text
operator|.
name|equals
argument_list|(
literal|"2"
argument_list|)
operator|&&
operator|!
name|text
operator|.
name|equals
argument_list|(
literal|"3"
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"unexpected message: "
operator|+
name|text
argument_list|)
expr_stmt|;
block|}
name|remaining
operator|--
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|remaining
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumeMessages
argument_list|(
name|remaining
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

