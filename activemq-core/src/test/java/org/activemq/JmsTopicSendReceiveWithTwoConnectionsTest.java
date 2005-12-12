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
name|Session
import|;
end_import

begin_comment
comment|/**  * @version  */
end_comment

begin_class
specifier|public
class|class
name|JmsTopicSendReceiveWithTwoConnectionsTest
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
name|JmsTopicSendReceiveWithTwoConnectionsTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|Connection
name|sendConnection
decl_stmt|;
specifier|protected
name|Connection
name|receiveConnection
decl_stmt|;
specifier|protected
name|Session
name|receiveSession
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
name|sendConnection
operator|=
name|createSendConnection
argument_list|()
expr_stmt|;
name|sendConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|receiveConnection
operator|=
name|createReceiveConnection
argument_list|()
expr_stmt|;
name|receiveConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Created sendConnection: "
operator|+
name|sendConnection
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Created receiveConnection: "
operator|+
name|receiveConnection
argument_list|)
expr_stmt|;
name|session
operator|=
name|sendConnection
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
name|receiveSession
operator|=
name|receiveConnection
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
literal|"Created sendSession: "
operator|+
name|session
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Created receiveSession: "
operator|+
name|receiveSession
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
name|receiveSession
operator|.
name|createConsumer
argument_list|(
name|consumerDestination
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Started connections"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Connection
name|createReceiveConnection
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|createConnection
argument_list|()
return|;
block|}
specifier|protected
name|Connection
name|createSendConnection
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|createConnection
argument_list|()
return|;
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost?broker.persistent=false"
argument_list|)
return|;
block|}
specifier|protected
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
name|receiveSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|sendConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|receiveConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

