begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|test
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
name|Session
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
name|JmsTopicSendReceiveTest
extends|extends
name|JmsSendReceiveTestSupport
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
name|JmsTopicSendReceiveTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|Connection
name|connection
decl_stmt|;
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
name|consumeSession
operator|=
name|createConsumerSession
argument_list|()
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
name|log
operator|.
name|info
argument_list|(
literal|"Created consumeSession: "
operator|+
name|consumeSession
argument_list|)
expr_stmt|;
name|producer
operator|=
name|session
operator|.
name|createProducer
argument_list|(
literal|null
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
name|consumer
operator|=
name|createConsumer
argument_list|()
expr_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Created connection: "
operator|+
name|connection
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Dumping stats..."
argument_list|)
expr_stmt|;
comment|//TODO
comment|//connectionFactory.getFactoryStats().dump(new IndentPrinter());
name|log
operator|.
name|info
argument_list|(
literal|"Closing down connection"
argument_list|)
expr_stmt|;
comment|/** TODO we should be able to shut down properly */
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
comment|/**      * Creates a session.       *       * @return session       * @throws JMSException      */
specifier|protected
name|Session
name|createConsumerSession
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|useSeparateSession
condition|)
block|{
return|return
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
return|;
block|}
else|else
block|{
return|return
name|session
return|;
block|}
block|}
comment|/**      * Creates a durable suscriber or a consumer.       *       * @return MessageConsumer - durable suscriber or consumer.      * @throws JMSException      */
specifier|protected
name|MessageConsumer
name|createConsumer
parameter_list|()
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
return|;
block|}
return|return
name|consumeSession
operator|.
name|createConsumer
argument_list|(
name|consumerDestination
argument_list|)
return|;
block|}
block|}
end_class

end_unit

