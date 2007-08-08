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
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|test
operator|.
name|JmsTopicSendReceiveWithTwoConnectionsTest
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
name|ServiceSupport
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
name|springframework
operator|.
name|core
operator|.
name|io
operator|.
name|ClassPathResource
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|TwoBrokerTopicSendReceiveTest
extends|extends
name|JmsTopicSendReceiveWithTwoConnectionsTest
block|{
specifier|protected
name|ActiveMQConnectionFactory
name|sendFactory
decl_stmt|;
specifier|protected
name|ActiveMQConnectionFactory
name|receiveFactory
decl_stmt|;
specifier|protected
name|HashMap
name|brokers
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|sendFactory
operator|=
name|createSenderConnectionFactory
argument_list|()
expr_stmt|;
name|receiveFactory
operator|=
name|createReceiverConnectionFactory
argument_list|()
expr_stmt|;
comment|// Give server enough time to setup,
comment|// so we don't lose messages when connection fails
name|log
operator|.
name|info
argument_list|(
literal|"Waiting for brokers Initialize."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Brokers should be initialized by now.. starting test."
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createReceiverConnectionFactory
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|createConnectionFactory
argument_list|(
literal|"org/apache/activemq/usecases/receiver.xml"
argument_list|,
literal|"receiver"
argument_list|,
literal|"vm://receiver"
argument_list|)
return|;
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createSenderConnectionFactory
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|createConnectionFactory
argument_list|(
literal|"org/apache/activemq/usecases/sender.xml"
argument_list|,
literal|"sender"
argument_list|,
literal|"vm://sender"
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|brokers
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|BrokerService
name|broker
init|=
operator|(
name|BrokerService
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|ServiceSupport
operator|.
name|dispose
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|Connection
name|createReceiveConnection
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|receiveFactory
operator|.
name|createConnection
argument_list|()
return|;
block|}
specifier|protected
name|Connection
name|createSendConnection
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|sendFactory
operator|.
name|createConnection
argument_list|()
return|;
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|(
name|String
name|config
parameter_list|,
name|String
name|brokerName
parameter_list|,
name|String
name|connectUrl
parameter_list|)
throws|throws
name|JMSException
block|{
try|try
block|{
name|BrokerFactoryBean
name|brokerFactory
init|=
operator|new
name|BrokerFactoryBean
argument_list|(
operator|new
name|ClassPathResource
argument_list|(
name|config
argument_list|)
argument_list|)
decl_stmt|;
name|brokerFactory
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
name|BrokerService
name|broker
init|=
name|brokerFactory
operator|.
name|getBroker
argument_list|()
decl_stmt|;
name|brokers
operator|.
name|put
argument_list|(
name|brokerName
argument_list|,
name|broker
argument_list|)
expr_stmt|;
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connectUrl
argument_list|)
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
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

