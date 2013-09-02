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
name|inteceptor
package|;
end_package

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
name|Broker
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
name|MutableBrokerFilter
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
name|ProducerBrokerExchange
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
name|command
operator|.
name|Message
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

begin_class
specifier|public
class|class
name|MessageInterceptorRegistry
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
name|MessageInterceptorRegistry
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|BrokerService
name|brokerService
decl_stmt|;
specifier|private
name|MessageInterceptorFilter
name|filter
decl_stmt|;
specifier|public
name|MessageInterceptorRegistry
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
block|{
name|this
operator|.
name|brokerService
operator|=
name|brokerService
expr_stmt|;
block|}
specifier|public
name|MessageInterceptor
name|addMessageInterceptor
parameter_list|(
name|String
name|destinationName
parameter_list|,
name|MessageInterceptor
name|messageInterceptor
parameter_list|)
block|{
return|return
name|getFilter
argument_list|()
operator|.
name|addMessageInterceptor
argument_list|(
name|destinationName
argument_list|,
name|messageInterceptor
argument_list|)
return|;
block|}
specifier|public
name|void
name|removeMessageInterceptor
parameter_list|(
name|String
name|destinationName
parameter_list|,
name|MessageInterceptor
name|messageInterceptor
parameter_list|)
block|{
name|getFilter
argument_list|()
operator|.
name|removeMessageInterceptor
argument_list|(
name|destinationName
argument_list|,
name|messageInterceptor
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MessageInterceptor
name|addMessageInterceptorForQueue
parameter_list|(
name|String
name|destinationName
parameter_list|,
name|MessageInterceptor
name|messageInterceptor
parameter_list|)
block|{
return|return
name|getFilter
argument_list|()
operator|.
name|addMessageInterceptorForQueue
argument_list|(
name|destinationName
argument_list|,
name|messageInterceptor
argument_list|)
return|;
block|}
specifier|public
name|void
name|removeMessageInterceptorForQueue
parameter_list|(
name|String
name|destinationName
parameter_list|,
name|MessageInterceptor
name|messageInterceptor
parameter_list|)
block|{
name|getFilter
argument_list|()
operator|.
name|addMessageInterceptorForQueue
argument_list|(
name|destinationName
argument_list|,
name|messageInterceptor
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MessageInterceptor
name|addMessageInterceptorForTopic
parameter_list|(
name|String
name|destinationName
parameter_list|,
name|MessageInterceptor
name|messageInterceptor
parameter_list|)
block|{
return|return
name|getFilter
argument_list|()
operator|.
name|addMessageInterceptorForTopic
argument_list|(
name|destinationName
argument_list|,
name|messageInterceptor
argument_list|)
return|;
block|}
specifier|public
name|void
name|removeMessageInterceptorForTopic
parameter_list|(
name|String
name|destinationName
parameter_list|,
name|MessageInterceptor
name|messageInterceptor
parameter_list|)
block|{
name|getFilter
argument_list|()
operator|.
name|removeMessageInterceptorForTopic
argument_list|(
name|destinationName
argument_list|,
name|messageInterceptor
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MessageInterceptor
name|addMessageInterceptor
parameter_list|(
name|ActiveMQDestination
name|activeMQDestination
parameter_list|,
name|MessageInterceptor
name|messageInterceptor
parameter_list|)
block|{
return|return
name|getFilter
argument_list|()
operator|.
name|addMessageInterceptor
argument_list|(
name|activeMQDestination
argument_list|,
name|messageInterceptor
argument_list|)
return|;
block|}
specifier|public
name|void
name|removeMessageInterceptor
parameter_list|(
name|ActiveMQDestination
name|activeMQDestination
parameter_list|,
name|MessageInterceptor
name|interceptor
parameter_list|)
block|{
name|getFilter
argument_list|()
operator|.
name|removeMessageInterceptor
argument_list|(
name|activeMQDestination
argument_list|,
name|interceptor
argument_list|)
expr_stmt|;
block|}
comment|/**      * Re-inject into the Broker chain      */
specifier|public
name|void
name|injectMessage
parameter_list|(
name|ProducerBrokerExchange
name|producerExchange
parameter_list|,
specifier|final
name|Message
name|messageSend
parameter_list|)
throws|throws
name|Exception
block|{
name|getFilter
argument_list|()
operator|.
name|injectMessage
argument_list|(
name|producerExchange
argument_list|,
name|messageSend
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|synchronized
name|MessageInterceptorFilter
name|getFilter
parameter_list|()
block|{
if|if
condition|(
name|filter
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|MutableBrokerFilter
name|mutableBrokerFilter
init|=
operator|(
name|MutableBrokerFilter
operator|)
name|brokerService
operator|.
name|getBroker
argument_list|()
operator|.
name|getAdaptor
argument_list|(
name|MutableBrokerFilter
operator|.
name|class
argument_list|)
decl_stmt|;
name|Broker
name|next
init|=
name|mutableBrokerFilter
operator|.
name|getNext
argument_list|()
decl_stmt|;
name|filter
operator|=
operator|new
name|MessageInterceptorFilter
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|mutableBrokerFilter
operator|.
name|setNext
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to create MessageInterceptorFilter"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|filter
return|;
block|}
block|}
end_class

end_unit
