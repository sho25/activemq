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
name|usecases
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
name|xbean
operator|.
name|BrokerFactoryBean
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
name|springframework
operator|.
name|core
operator|.
name|io
operator|.
name|ClassPathResource
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|TwoBrokerTopicSendReceiveUsingTcpTest
extends|extends
name|TwoBrokerTopicSendReceiveTest
block|{
specifier|private
name|BrokerService
name|receiverBroker
decl_stmt|;
specifier|private
name|BrokerService
name|senderBroker
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerFactoryBean
name|brokerFactory
decl_stmt|;
name|brokerFactory
operator|=
operator|new
name|BrokerFactoryBean
argument_list|(
operator|new
name|ClassPathResource
argument_list|(
literal|"org/apache/activemq/usecases/receiver.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|brokerFactory
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
name|receiverBroker
operator|=
name|brokerFactory
operator|.
name|getBroker
argument_list|()
expr_stmt|;
name|brokerFactory
operator|=
operator|new
name|BrokerFactoryBean
argument_list|(
operator|new
name|ClassPathResource
argument_list|(
literal|"org/apache/activemq/usecases/sender.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|brokerFactory
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
name|senderBroker
operator|=
name|brokerFactory
operator|.
name|getBroker
argument_list|()
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
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
if|if
condition|(
name|receiverBroker
operator|!=
literal|null
condition|)
block|{
name|receiverBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|senderBroker
operator|!=
literal|null
condition|)
block|{
name|senderBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createReceiverConnectionFactory
parameter_list|()
throws|throws
name|JMSException
block|{
try|try
block|{
name|ActiveMQConnectionFactory
name|fac
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
operator|(
operator|(
name|TransportConnector
operator|)
name|receiverBroker
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getConnectUri
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|fac
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createSenderConnectionFactory
parameter_list|()
throws|throws
name|JMSException
block|{
try|try
block|{
name|ActiveMQConnectionFactory
name|fac
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
operator|(
operator|(
name|TransportConnector
operator|)
name|senderBroker
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getConnectUri
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|fac
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

