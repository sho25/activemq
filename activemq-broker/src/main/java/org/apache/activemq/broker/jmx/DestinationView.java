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
name|jmx
package|;
end_package

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
name|Collections
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
name|InvalidSelectorException
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
name|management
operator|.
name|MalformedObjectNameException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeDataSupport
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenDataException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularDataSupport
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularType
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
name|jmx
operator|.
name|OpenTypeSupport
operator|.
name|OpenTypeFactory
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
name|region
operator|.
name|Destination
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
name|region
operator|.
name|Subscription
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
name|region
operator|.
name|policy
operator|.
name|AbortSlowConsumerStrategy
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
name|region
operator|.
name|policy
operator|.
name|SlowConsumerStrategy
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
name|command
operator|.
name|ActiveMQTextMessage
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
name|activemq
operator|.
name|filter
operator|.
name|BooleanExpression
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
name|filter
operator|.
name|MessageEvaluationContext
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
name|selector
operator|.
name|SelectorParser
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
name|URISupport
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
name|DestinationView
implements|implements
name|DestinationViewMBean
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
name|DestinationViewMBean
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|Destination
name|destination
decl_stmt|;
specifier|protected
specifier|final
name|ManagedRegionBroker
name|broker
decl_stmt|;
specifier|public
name|DestinationView
parameter_list|(
name|ManagedRegionBroker
name|broker
parameter_list|,
name|Destination
name|destination
parameter_list|)
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
block|}
specifier|public
name|void
name|gc
parameter_list|()
block|{
name|destination
operator|.
name|gc
argument_list|()
expr_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|public
name|void
name|resetStatistics
parameter_list|()
block|{
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
specifier|public
name|long
name|getEnqueueCount
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getEnqueues
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
specifier|public
name|long
name|getDequeueCount
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getDequeues
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
specifier|public
name|long
name|getDispatchCount
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getDispatched
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
specifier|public
name|long
name|getInFlightCount
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getInflight
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
specifier|public
name|long
name|getExpiredCount
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getExpired
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
specifier|public
name|long
name|getConsumerCount
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getConsumers
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
specifier|public
name|long
name|getQueueSize
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getMessages
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
specifier|public
name|long
name|getMessagesCached
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getMessagesCached
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
specifier|public
name|int
name|getMemoryPercentUsage
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
return|;
block|}
specifier|public
name|long
name|getMemoryUsageByteCount
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
return|;
block|}
specifier|public
name|long
name|getMemoryLimit
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getLimit
argument_list|()
return|;
block|}
specifier|public
name|void
name|setMemoryLimit
parameter_list|(
name|long
name|limit
parameter_list|)
block|{
name|destination
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
name|limit
argument_list|)
expr_stmt|;
block|}
specifier|public
name|double
name|getAverageEnqueueTime
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getProcessTime
argument_list|()
operator|.
name|getAverageTime
argument_list|()
return|;
block|}
specifier|public
name|long
name|getMaxEnqueueTime
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getProcessTime
argument_list|()
operator|.
name|getMaxTime
argument_list|()
return|;
block|}
specifier|public
name|long
name|getMinEnqueueTime
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getProcessTime
argument_list|()
operator|.
name|getMinTime
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isPrioritizedMessages
parameter_list|()
block|{
return|return
name|destination
operator|.
name|isPrioritizedMessages
argument_list|()
return|;
block|}
specifier|public
name|CompositeData
index|[]
name|browse
parameter_list|()
throws|throws
name|OpenDataException
block|{
try|try
block|{
return|return
name|browse
argument_list|(
literal|null
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvalidSelectorException
name|e
parameter_list|)
block|{
comment|// should not happen.
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|CompositeData
index|[]
name|browse
parameter_list|(
name|String
name|selector
parameter_list|)
throws|throws
name|OpenDataException
throws|,
name|InvalidSelectorException
block|{
name|Message
index|[]
name|messages
init|=
name|destination
operator|.
name|browse
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|CompositeData
argument_list|>
name|c
init|=
operator|new
name|ArrayList
argument_list|<
name|CompositeData
argument_list|>
argument_list|()
decl_stmt|;
name|MessageEvaluationContext
name|ctx
init|=
operator|new
name|MessageEvaluationContext
argument_list|()
decl_stmt|;
name|ctx
operator|.
name|setDestination
argument_list|(
name|destination
operator|.
name|getActiveMQDestination
argument_list|()
argument_list|)
expr_stmt|;
name|BooleanExpression
name|selectorExpression
init|=
name|selector
operator|==
literal|null
condition|?
literal|null
else|:
name|SelectorParser
operator|.
name|parse
argument_list|(
name|selector
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|messages
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
if|if
condition|(
name|selectorExpression
operator|==
literal|null
condition|)
block|{
name|c
operator|.
name|add
argument_list|(
name|OpenTypeSupport
operator|.
name|convert
argument_list|(
name|messages
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ctx
operator|.
name|setMessageReference
argument_list|(
name|messages
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|selectorExpression
operator|.
name|matches
argument_list|(
name|ctx
argument_list|)
condition|)
block|{
name|c
operator|.
name|add
argument_list|(
name|OpenTypeSupport
operator|.
name|convert
argument_list|(
name|messages
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// TODO DELETE ME
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
comment|// TODO DELETE ME
name|LOG
operator|.
name|warn
argument_list|(
literal|"exception browsing destination"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|CompositeData
name|rc
index|[]
init|=
operator|new
name|CompositeData
index|[
name|c
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|c
operator|.
name|toArray
argument_list|(
name|rc
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
comment|/**      * Browses the current destination returning a list of messages      */
specifier|public
name|List
argument_list|<
name|Object
argument_list|>
name|browseMessages
parameter_list|()
throws|throws
name|InvalidSelectorException
block|{
return|return
name|browseMessages
argument_list|(
literal|null
argument_list|)
return|;
block|}
comment|/**      * Browses the current destination with the given selector returning a list      * of messages      */
specifier|public
name|List
argument_list|<
name|Object
argument_list|>
name|browseMessages
parameter_list|(
name|String
name|selector
parameter_list|)
throws|throws
name|InvalidSelectorException
block|{
name|Message
index|[]
name|messages
init|=
name|destination
operator|.
name|browse
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|answer
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|MessageEvaluationContext
name|ctx
init|=
operator|new
name|MessageEvaluationContext
argument_list|()
decl_stmt|;
name|ctx
operator|.
name|setDestination
argument_list|(
name|destination
operator|.
name|getActiveMQDestination
argument_list|()
argument_list|)
expr_stmt|;
name|BooleanExpression
name|selectorExpression
init|=
name|selector
operator|==
literal|null
condition|?
literal|null
else|:
name|SelectorParser
operator|.
name|parse
argument_list|(
name|selector
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|messages
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|Message
name|message
init|=
name|messages
index|[
name|i
index|]
decl_stmt|;
name|message
operator|.
name|setReadOnlyBody
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|selectorExpression
operator|==
literal|null
condition|)
block|{
name|answer
operator|.
name|add
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ctx
operator|.
name|setMessageReference
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|selectorExpression
operator|.
name|matches
argument_list|(
name|ctx
argument_list|)
condition|)
block|{
name|answer
operator|.
name|add
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"exception browsing destination"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|answer
return|;
block|}
specifier|public
name|TabularData
name|browseAsTable
parameter_list|()
throws|throws
name|OpenDataException
block|{
try|try
block|{
return|return
name|browseAsTable
argument_list|(
literal|null
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvalidSelectorException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|TabularData
name|browseAsTable
parameter_list|(
name|String
name|selector
parameter_list|)
throws|throws
name|OpenDataException
throws|,
name|InvalidSelectorException
block|{
name|OpenTypeFactory
name|factory
init|=
name|OpenTypeSupport
operator|.
name|getFactory
argument_list|(
name|ActiveMQMessage
operator|.
name|class
argument_list|)
decl_stmt|;
name|Message
index|[]
name|messages
init|=
name|destination
operator|.
name|browse
argument_list|()
decl_stmt|;
name|CompositeType
name|ct
init|=
name|factory
operator|.
name|getCompositeType
argument_list|()
decl_stmt|;
name|TabularType
name|tt
init|=
operator|new
name|TabularType
argument_list|(
literal|"MessageList"
argument_list|,
literal|"MessageList"
argument_list|,
name|ct
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"JMSMessageID"
block|}
argument_list|)
decl_stmt|;
name|TabularDataSupport
name|rc
init|=
operator|new
name|TabularDataSupport
argument_list|(
name|tt
argument_list|)
decl_stmt|;
name|MessageEvaluationContext
name|ctx
init|=
operator|new
name|MessageEvaluationContext
argument_list|()
decl_stmt|;
name|ctx
operator|.
name|setDestination
argument_list|(
name|destination
operator|.
name|getActiveMQDestination
argument_list|()
argument_list|)
expr_stmt|;
name|BooleanExpression
name|selectorExpression
init|=
name|selector
operator|==
literal|null
condition|?
literal|null
else|:
name|SelectorParser
operator|.
name|parse
argument_list|(
name|selector
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|messages
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
if|if
condition|(
name|selectorExpression
operator|==
literal|null
condition|)
block|{
name|rc
operator|.
name|put
argument_list|(
operator|new
name|CompositeDataSupport
argument_list|(
name|ct
argument_list|,
name|factory
operator|.
name|getFields
argument_list|(
name|messages
index|[
name|i
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ctx
operator|.
name|setMessageReference
argument_list|(
name|messages
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|selectorExpression
operator|.
name|matches
argument_list|(
name|ctx
argument_list|)
condition|)
block|{
name|rc
operator|.
name|put
argument_list|(
operator|new
name|CompositeDataSupport
argument_list|(
name|ct
argument_list|,
name|factory
operator|.
name|getFields
argument_list|(
name|messages
index|[
name|i
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"exception browsing destination"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|rc
return|;
block|}
specifier|public
name|String
name|sendTextMessage
parameter_list|(
name|String
name|body
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|sendTextMessage
argument_list|(
name|Collections
operator|.
name|EMPTY_MAP
argument_list|,
name|body
argument_list|)
return|;
block|}
specifier|public
name|String
name|sendTextMessage
parameter_list|(
name|Map
name|headers
parameter_list|,
name|String
name|body
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|sendTextMessage
argument_list|(
name|headers
argument_list|,
name|body
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
name|String
name|sendTextMessage
parameter_list|(
name|String
name|body
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|sendTextMessage
argument_list|(
name|Collections
operator|.
name|EMPTY_MAP
argument_list|,
name|body
argument_list|,
name|user
argument_list|,
name|password
argument_list|)
return|;
block|}
specifier|public
name|String
name|sendTextMessage
parameter_list|(
name|Map
name|headers
parameter_list|,
name|String
name|body
parameter_list|,
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|brokerUrl
init|=
literal|"vm://"
operator|+
name|broker
operator|.
name|getBrokerName
argument_list|()
decl_stmt|;
name|ActiveMQDestination
name|dest
init|=
name|destination
operator|.
name|getActiveMQDestination
argument_list|()
decl_stmt|;
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerUrl
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|connection
operator|=
name|cf
operator|.
name|createConnection
argument_list|(
name|userName
argument_list|,
name|password
argument_list|)
expr_stmt|;
name|Session
name|session
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
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|ActiveMQTextMessage
name|msg
init|=
operator|(
name|ActiveMQTextMessage
operator|)
name|session
operator|.
name|createTextMessage
argument_list|(
name|body
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|headers
operator|.
name|entrySet
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
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|msg
operator|.
name|setObjectProperty
argument_list|(
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|msg
operator|.
name|getJMSDeliveryMode
argument_list|()
argument_list|)
expr_stmt|;
name|producer
operator|.
name|setPriority
argument_list|(
name|msg
operator|.
name|getPriority
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|ttl
init|=
name|msg
operator|.
name|getExpiration
argument_list|()
operator|-
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|producer
operator|.
name|setTimeToLive
argument_list|(
name|ttl
operator|>
literal|0
condition|?
name|ttl
else|:
literal|0
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
return|return
name|msg
operator|.
name|getJMSMessageID
argument_list|()
return|;
block|}
finally|finally
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|getMaxAuditDepth
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getMaxAuditDepth
argument_list|()
return|;
block|}
specifier|public
name|int
name|getMaxProducersToAudit
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getMaxProducersToAudit
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isEnableAudit
parameter_list|()
block|{
return|return
name|destination
operator|.
name|isEnableAudit
argument_list|()
return|;
block|}
specifier|public
name|void
name|setEnableAudit
parameter_list|(
name|boolean
name|enableAudit
parameter_list|)
block|{
name|destination
operator|.
name|setEnableAudit
argument_list|(
name|enableAudit
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setMaxAuditDepth
parameter_list|(
name|int
name|maxAuditDepth
parameter_list|)
block|{
name|destination
operator|.
name|setMaxAuditDepth
argument_list|(
name|maxAuditDepth
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setMaxProducersToAudit
parameter_list|(
name|int
name|maxProducersToAudit
parameter_list|)
block|{
name|destination
operator|.
name|setMaxProducersToAudit
argument_list|(
name|maxProducersToAudit
argument_list|)
expr_stmt|;
block|}
specifier|public
name|float
name|getMemoryUsagePortion
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getUsagePortion
argument_list|()
return|;
block|}
specifier|public
name|long
name|getProducerCount
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getProducers
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isProducerFlowControl
parameter_list|()
block|{
return|return
name|destination
operator|.
name|isProducerFlowControl
argument_list|()
return|;
block|}
specifier|public
name|void
name|setMemoryUsagePortion
parameter_list|(
name|float
name|value
parameter_list|)
block|{
name|destination
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|setUsagePortion
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setProducerFlowControl
parameter_list|(
name|boolean
name|producerFlowControl
parameter_list|)
block|{
name|destination
operator|.
name|setProducerFlowControl
argument_list|(
name|producerFlowControl
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isAlwaysRetroactive
parameter_list|()
block|{
return|return
name|destination
operator|.
name|isAlwaysRetroactive
argument_list|()
return|;
block|}
specifier|public
name|void
name|setAlwaysRetroactive
parameter_list|(
name|boolean
name|alwaysRetroactive
parameter_list|)
block|{
name|destination
operator|.
name|setAlwaysRetroactive
argument_list|(
name|alwaysRetroactive
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set's the interval at which warnings about producers being blocked by      * resource usage will be triggered. Values of 0 or less will disable      * warnings      *      * @param blockedProducerWarningInterval the interval at which warning about      *            blocked producers will be triggered.      */
specifier|public
name|void
name|setBlockedProducerWarningInterval
parameter_list|(
name|long
name|blockedProducerWarningInterval
parameter_list|)
block|{
name|destination
operator|.
name|setBlockedProducerWarningInterval
argument_list|(
name|blockedProducerWarningInterval
argument_list|)
expr_stmt|;
block|}
comment|/**      *      * @return the interval at which warning about blocked producers will be      *         triggered.      */
specifier|public
name|long
name|getBlockedProducerWarningInterval
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getBlockedProducerWarningInterval
argument_list|()
return|;
block|}
specifier|public
name|int
name|getMaxPageSize
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getMaxPageSize
argument_list|()
return|;
block|}
specifier|public
name|void
name|setMaxPageSize
parameter_list|(
name|int
name|pageSize
parameter_list|)
block|{
name|destination
operator|.
name|setMaxPageSize
argument_list|(
name|pageSize
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseCache
parameter_list|()
block|{
return|return
name|destination
operator|.
name|isUseCache
argument_list|()
return|;
block|}
specifier|public
name|void
name|setUseCache
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|destination
operator|.
name|setUseCache
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ObjectName
index|[]
name|getSubscriptions
parameter_list|()
throws|throws
name|IOException
throws|,
name|MalformedObjectNameException
block|{
name|List
argument_list|<
name|Subscription
argument_list|>
name|subscriptions
init|=
name|destination
operator|.
name|getConsumers
argument_list|()
decl_stmt|;
name|ObjectName
index|[]
name|answer
init|=
operator|new
name|ObjectName
index|[
name|subscriptions
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|ObjectName
name|objectName
init|=
name|broker
operator|.
name|getBrokerService
argument_list|()
operator|.
name|getBrokerObjectName
argument_list|()
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Subscription
name|subscription
range|:
name|subscriptions
control|)
block|{
name|String
name|connectionClientId
init|=
name|subscription
operator|.
name|getContext
argument_list|()
operator|.
name|getClientId
argument_list|()
decl_stmt|;
name|String
name|objectNameStr
init|=
name|ManagedRegionBroker
operator|.
name|getSubscriptionObjectName
argument_list|(
name|subscription
operator|.
name|getConsumerInfo
argument_list|()
argument_list|,
name|connectionClientId
argument_list|,
name|objectName
argument_list|)
decl_stmt|;
name|answer
index|[
name|index
operator|++
index|]
operator|=
operator|new
name|ObjectName
argument_list|(
name|objectNameStr
argument_list|)
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
specifier|public
name|ObjectName
name|getSlowConsumerStrategy
parameter_list|()
throws|throws
name|IOException
throws|,
name|MalformedObjectNameException
block|{
name|ObjectName
name|result
init|=
literal|null
decl_stmt|;
name|SlowConsumerStrategy
name|strategy
init|=
name|destination
operator|.
name|getSlowConsumerStrategy
argument_list|()
decl_stmt|;
if|if
condition|(
name|strategy
operator|!=
literal|null
operator|&&
name|strategy
operator|instanceof
name|AbortSlowConsumerStrategy
condition|)
block|{
name|result
operator|=
name|broker
operator|.
name|registerSlowConsumerStrategy
argument_list|(
operator|(
name|AbortSlowConsumerStrategy
operator|)
name|strategy
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|String
name|getOptions
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|options
init|=
name|destination
operator|.
name|getActiveMQDestination
argument_list|()
operator|.
name|getOptions
argument_list|()
decl_stmt|;
name|String
name|optionsString
init|=
literal|""
decl_stmt|;
try|try
block|{
if|if
condition|(
name|options
operator|!=
literal|null
condition|)
block|{
name|optionsString
operator|=
name|URISupport
operator|.
name|createQueryString
argument_list|(
name|options
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|ignored
parameter_list|)
block|{}
return|return
name|optionsString
return|;
block|}
block|}
end_class

end_unit

