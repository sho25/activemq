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
name|camel
operator|.
name|component
operator|.
name|broker
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CopyOnWriteArrayList
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
name|broker
operator|.
name|inteceptor
operator|.
name|MessageInterceptor
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
name|inteceptor
operator|.
name|MessageInterceptorRegistry
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
name|apache
operator|.
name|camel
operator|.
name|Consumer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|MultipleConsumersSupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|Processor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|Producer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|Service
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|api
operator|.
name|management
operator|.
name|ManagedResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|impl
operator|.
name|DefaultEndpoint
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|spi
operator|.
name|Metadata
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|spi
operator|.
name|UriEndpoint
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|spi
operator|.
name|UriParam
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|spi
operator|.
name|UriPath
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|util
operator|.
name|UnsafeUriCharactersEncoder
import|;
end_import

begin_class
annotation|@
name|ManagedResource
argument_list|(
name|description
operator|=
literal|"Managed Camel Broker Endpoint"
argument_list|)
annotation|@
name|UriEndpoint
argument_list|(
name|scheme
operator|=
literal|"broker"
argument_list|,
name|syntax
operator|=
literal|"broker:destination"
argument_list|,
name|consumerClass
operator|=
name|BrokerConsumer
operator|.
name|class
argument_list|,
name|title
operator|=
literal|"Broker"
argument_list|,
name|label
operator|=
literal|"messaging"
argument_list|)
specifier|public
class|class
name|BrokerEndpoint
extends|extends
name|DefaultEndpoint
implements|implements
name|MultipleConsumersSupport
implements|,
name|Service
block|{
specifier|static
specifier|final
name|String
name|PRODUCER_BROKER_EXCHANGE
init|=
literal|"producerBrokerExchange"
decl_stmt|;
specifier|private
name|MessageInterceptorRegistry
name|messageInterceptorRegistry
decl_stmt|;
specifier|private
name|List
argument_list|<
name|MessageInterceptor
argument_list|>
name|messageInterceptorList
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|MessageInterceptor
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|UriPath
argument_list|(
name|name
operator|=
literal|"destination"
argument_list|)
annotation|@
name|Metadata
argument_list|(
name|required
operator|=
literal|"true"
argument_list|)
specifier|private
name|String
name|destinationName
decl_stmt|;
specifier|private
specifier|final
name|ActiveMQDestination
name|destination
decl_stmt|;
annotation|@
name|UriParam
specifier|private
specifier|final
name|BrokerConfiguration
name|configuration
decl_stmt|;
specifier|public
name|BrokerEndpoint
parameter_list|(
name|String
name|uri
parameter_list|,
name|BrokerComponent
name|component
parameter_list|,
name|String
name|destinationName
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|BrokerConfiguration
name|configuration
parameter_list|)
block|{
name|super
argument_list|(
name|UnsafeUriCharactersEncoder
operator|.
name|encode
argument_list|(
name|uri
argument_list|)
argument_list|,
name|component
argument_list|)
expr_stmt|;
name|this
operator|.
name|destinationName
operator|=
name|destinationName
expr_stmt|;
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
name|this
operator|.
name|configuration
operator|=
name|configuration
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Producer
name|createProducer
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerProducer
name|producer
init|=
operator|new
name|BrokerProducer
argument_list|(
name|this
argument_list|)
decl_stmt|;
return|return
name|producer
return|;
block|}
annotation|@
name|Override
specifier|public
name|Consumer
name|createConsumer
parameter_list|(
name|Processor
name|processor
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerConsumer
name|consumer
init|=
operator|new
name|BrokerConsumer
argument_list|(
name|this
argument_list|,
name|processor
argument_list|)
decl_stmt|;
name|configureConsumer
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
return|return
name|consumer
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSingleton
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMultipleConsumersSupported
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|ActiveMQDestination
name|getDestination
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
comment|/**      * The name of the JMS destination      */
specifier|public
name|String
name|getDestinationName
parameter_list|()
block|{
return|return
name|destinationName
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|doStart
argument_list|()
expr_stmt|;
name|messageInterceptorRegistry
operator|=
name|MessageInterceptorRegistry
operator|.
name|getInstance
argument_list|()
operator|.
name|get
argument_list|(
name|configuration
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|MessageInterceptor
name|messageInterceptor
range|:
name|messageInterceptorList
control|)
block|{
name|addMessageInterceptor
argument_list|(
name|messageInterceptor
argument_list|)
expr_stmt|;
block|}
name|messageInterceptorList
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doStop
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|doStop
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|addMessageInterceptor
parameter_list|(
name|MessageInterceptor
name|messageInterceptor
parameter_list|)
block|{
if|if
condition|(
name|isStarted
argument_list|()
condition|)
block|{
name|messageInterceptorRegistry
operator|.
name|addMessageInterceptor
argument_list|(
name|destination
argument_list|,
name|messageInterceptor
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|messageInterceptorList
operator|.
name|add
argument_list|(
name|messageInterceptor
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|removeMessageInterceptor
parameter_list|(
name|MessageInterceptor
name|messageInterceptor
parameter_list|)
block|{
name|messageInterceptorRegistry
operator|.
name|removeMessageInterceptor
argument_list|(
name|destination
argument_list|,
name|messageInterceptor
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|inject
parameter_list|(
name|ProducerBrokerExchange
name|producerBrokerExchange
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|Exception
block|{
name|ProducerBrokerExchange
name|pbe
init|=
name|producerBrokerExchange
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|message
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
if|if
condition|(
name|producerBrokerExchange
operator|!=
literal|null
operator|&&
name|producerBrokerExchange
operator|.
name|getRegionDestination
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|producerBrokerExchange
operator|.
name|getRegionDestination
argument_list|()
operator|.
name|getActiveMQDestination
argument_list|()
operator|.
name|equals
argument_list|(
name|destination
argument_list|)
condition|)
block|{
comment|//The message broker will create a new ProducerBrokerExchange with the
comment|//correct region broker set
name|pbe
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|messageInterceptorRegistry
operator|.
name|injectMessage
argument_list|(
name|pbe
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

