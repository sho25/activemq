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
name|broker
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerFactory
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
name|command
operator|.
name|ActiveMQMessage
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
comment|/**  *   *   */
end_comment

begin_class
specifier|public
class|class
name|PluginBrokerTest
extends|extends
name|JmsTopicSendReceiveTest
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
name|PluginBrokerTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
name|createBroker
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
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|createBroker
argument_list|(
literal|"org/apache/activemq/util/plugin-broker.xml"
argument_list|)
return|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Loading broker configuration from the classpath with URI: "
operator|+
name|uri
argument_list|)
expr_stmt|;
return|return
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"xbean:"
operator|+
name|uri
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|void
name|assertMessageValid
parameter_list|(
name|int
name|index
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
comment|// check if broker path has been set
name|assertEquals
argument_list|(
literal|"localhost"
argument_list|,
name|message
operator|.
name|getStringProperty
argument_list|(
literal|"BrokerPath"
argument_list|)
argument_list|)
expr_stmt|;
name|ActiveMQMessage
name|amqMsg
init|=
operator|(
name|ActiveMQMessage
operator|)
name|message
decl_stmt|;
if|if
condition|(
name|index
operator|==
literal|7
condition|)
block|{
comment|// check custom expiration
name|assertTrue
argument_list|(
literal|"expiration is in range, depends on two distinct calls to System.currentTimeMillis"
argument_list|,
literal|1500
operator|<
name|amqMsg
operator|.
name|getExpiration
argument_list|()
operator|-
name|amqMsg
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|index
operator|==
literal|9
condition|)
block|{
comment|// check ceiling
name|assertTrue
argument_list|(
literal|"expiration ceeling is in range, depends on two distinct calls to System.currentTimeMillis"
argument_list|,
literal|59500
operator|<
name|amqMsg
operator|.
name|getExpiration
argument_list|()
operator|-
name|amqMsg
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// check default expiration
name|assertEquals
argument_list|(
literal|1000
argument_list|,
name|amqMsg
operator|.
name|getExpiration
argument_list|()
operator|-
name|amqMsg
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|assertMessageValid
argument_list|(
name|index
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|sendMessage
parameter_list|(
name|int
name|index
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|index
operator|==
literal|7
condition|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|producerDestination
argument_list|,
name|message
argument_list|,
name|Message
operator|.
name|DEFAULT_DELIVERY_MODE
argument_list|,
name|Message
operator|.
name|DEFAULT_PRIORITY
argument_list|,
literal|2000
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|index
operator|==
literal|9
condition|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|producerDestination
argument_list|,
name|message
argument_list|,
name|Message
operator|.
name|DEFAULT_DELIVERY_MODE
argument_list|,
name|Message
operator|.
name|DEFAULT_PRIORITY
argument_list|,
literal|200000
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|sendMessage
argument_list|(
name|index
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
