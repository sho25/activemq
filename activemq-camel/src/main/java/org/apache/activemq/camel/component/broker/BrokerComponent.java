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
name|ArrayList
import|;
end_import

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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|view
operator|.
name|MessageBrokerView
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
name|view
operator|.
name|MessageBrokerViewRegistry
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
name|camel
operator|.
name|ComponentConfiguration
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
name|Endpoint
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
name|component
operator|.
name|jms
operator|.
name|JmsConfiguration
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
name|UriEndpointComponent
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
name|EndpointCompleter
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|util
operator|.
name|ObjectHelper
operator|.
name|removeStartingCharacters
import|;
end_import

begin_class
specifier|public
class|class
name|BrokerComponent
extends|extends
name|UriEndpointComponent
implements|implements
name|EndpointCompleter
block|{
specifier|public
name|BrokerComponent
parameter_list|()
block|{
name|super
argument_list|(
name|BrokerEndpoint
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Endpoint
name|createEndpoint
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|remaining
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|parameters
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerConfiguration
name|brokerConfiguration
init|=
operator|new
name|BrokerConfiguration
argument_list|()
decl_stmt|;
name|setProperties
argument_list|(
name|brokerConfiguration
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
name|byte
name|destinationType
init|=
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
decl_stmt|;
if|if
condition|(
name|remaining
operator|.
name|startsWith
argument_list|(
name|JmsConfiguration
operator|.
name|QUEUE_PREFIX
argument_list|)
condition|)
block|{
name|remaining
operator|=
name|removeStartingCharacters
argument_list|(
name|remaining
operator|.
name|substring
argument_list|(
name|JmsConfiguration
operator|.
name|QUEUE_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|remaining
operator|.
name|startsWith
argument_list|(
name|JmsConfiguration
operator|.
name|TOPIC_PREFIX
argument_list|)
condition|)
block|{
name|destinationType
operator|=
name|ActiveMQDestination
operator|.
name|TOPIC_TYPE
expr_stmt|;
name|remaining
operator|=
name|removeStartingCharacters
argument_list|(
name|remaining
operator|.
name|substring
argument_list|(
name|JmsConfiguration
operator|.
name|TOPIC_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|remaining
operator|.
name|startsWith
argument_list|(
name|JmsConfiguration
operator|.
name|TEMP_QUEUE_PREFIX
argument_list|)
condition|)
block|{
name|destinationType
operator|=
name|ActiveMQDestination
operator|.
name|TEMP_QUEUE_TYPE
expr_stmt|;
name|remaining
operator|=
name|removeStartingCharacters
argument_list|(
name|remaining
operator|.
name|substring
argument_list|(
name|JmsConfiguration
operator|.
name|TEMP_QUEUE_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|remaining
operator|.
name|startsWith
argument_list|(
name|JmsConfiguration
operator|.
name|TEMP_TOPIC_PREFIX
argument_list|)
condition|)
block|{
name|destinationType
operator|=
name|ActiveMQDestination
operator|.
name|TEMP_TOPIC_TYPE
expr_stmt|;
name|remaining
operator|=
name|removeStartingCharacters
argument_list|(
name|remaining
operator|.
name|substring
argument_list|(
name|JmsConfiguration
operator|.
name|TEMP_TOPIC_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
block|}
name|ActiveMQDestination
name|destination
init|=
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|remaining
argument_list|,
name|destinationType
argument_list|)
decl_stmt|;
name|BrokerEndpoint
name|brokerEndpoint
init|=
operator|new
name|BrokerEndpoint
argument_list|(
name|uri
argument_list|,
name|this
argument_list|,
name|destination
argument_list|,
name|brokerConfiguration
argument_list|)
decl_stmt|;
return|return
name|brokerEndpoint
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|completeEndpointPath
parameter_list|(
name|ComponentConfiguration
name|componentConfiguration
parameter_list|,
name|String
name|completionText
parameter_list|)
block|{
name|String
name|brokerName
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|componentConfiguration
operator|.
name|getParameter
argument_list|(
literal|"brokerName"
argument_list|)
argument_list|)
decl_stmt|;
name|MessageBrokerView
name|messageBrokerView
init|=
name|MessageBrokerViewRegistry
operator|.
name|getInstance
argument_list|()
operator|.
name|lookup
argument_list|(
name|brokerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|messageBrokerView
operator|!=
literal|null
condition|)
block|{
name|String
name|destinationName
init|=
name|completionText
decl_stmt|;
name|Set
argument_list|<
name|?
extends|extends
name|ActiveMQDestination
argument_list|>
name|set
init|=
name|messageBrokerView
operator|.
name|getQueues
argument_list|()
decl_stmt|;
if|if
condition|(
name|completionText
operator|.
name|startsWith
argument_list|(
literal|"topic:"
argument_list|)
condition|)
block|{
name|set
operator|=
name|messageBrokerView
operator|.
name|getTopics
argument_list|()
expr_stmt|;
name|destinationName
operator|=
name|completionText
operator|.
name|substring
argument_list|(
literal|6
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|completionText
operator|.
name|startsWith
argument_list|(
literal|"queue:"
argument_list|)
condition|)
block|{
name|destinationName
operator|=
name|completionText
operator|.
name|substring
argument_list|(
literal|6
argument_list|)
expr_stmt|;
block|}
name|ArrayList
argument_list|<
name|String
argument_list|>
name|answer
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ActiveMQDestination
name|destination
range|:
name|set
control|)
block|{
if|if
condition|(
name|destination
operator|.
name|getPhysicalName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|destinationName
argument_list|)
condition|)
block|{
name|answer
operator|.
name|add
argument_list|(
name|destination
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|answer
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit
