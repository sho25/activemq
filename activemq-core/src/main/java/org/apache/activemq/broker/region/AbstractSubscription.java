begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|region
package|;
end_package

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
name|JMSException
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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|ConnectionContext
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
name|ConsumerId
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
name|ConsumerInfo
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
name|DestinationFilter
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
name|LogicExpression
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
name|filter
operator|.
name|NoLocalExpression
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
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CopyOnWriteArrayList
import|;
end_import

begin_class
specifier|abstract
specifier|public
class|class
name|AbstractSubscription
implements|implements
name|Subscription
block|{
specifier|static
specifier|private
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|AbstractSubscription
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|Broker
name|broker
decl_stmt|;
specifier|protected
name|ConnectionContext
name|context
decl_stmt|;
specifier|protected
name|ConsumerInfo
name|info
decl_stmt|;
specifier|final
specifier|protected
name|DestinationFilter
name|destinationFilter
decl_stmt|;
specifier|private
name|BooleanExpression
name|selectorExpression
decl_stmt|;
specifier|private
name|ObjectName
name|objectName
decl_stmt|;
specifier|final
specifier|protected
name|CopyOnWriteArrayList
name|destinations
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|()
decl_stmt|;
specifier|public
name|AbstractSubscription
parameter_list|(
name|Broker
name|broker
parameter_list|,
name|ConnectionContext
name|context
parameter_list|,
name|ConsumerInfo
name|info
parameter_list|)
throws|throws
name|InvalidSelectorException
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
name|this
operator|.
name|destinationFilter
operator|=
name|DestinationFilter
operator|.
name|parseFilter
argument_list|(
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|selectorExpression
operator|=
name|parseSelector
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
specifier|static
specifier|private
name|BooleanExpression
name|parseSelector
parameter_list|(
name|ConsumerInfo
name|info
parameter_list|)
throws|throws
name|InvalidSelectorException
block|{
name|BooleanExpression
name|rc
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|getSelector
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|rc
operator|=
operator|new
name|SelectorParser
argument_list|()
operator|.
name|parse
argument_list|(
name|info
operator|.
name|getSelector
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|.
name|isNoLocal
argument_list|()
condition|)
block|{
if|if
condition|(
name|rc
operator|==
literal|null
condition|)
block|{
name|rc
operator|=
operator|new
name|NoLocalExpression
argument_list|(
name|info
operator|.
name|getConsumerId
argument_list|()
operator|.
name|getConnectionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rc
operator|=
name|LogicExpression
operator|.
name|createAND
argument_list|(
operator|new
name|NoLocalExpression
argument_list|(
name|info
operator|.
name|getConsumerId
argument_list|()
operator|.
name|getConnectionId
argument_list|()
argument_list|)
argument_list|,
name|rc
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|info
operator|.
name|getAdditionalPredicate
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|rc
operator|==
literal|null
condition|)
block|{
name|rc
operator|=
name|info
operator|.
name|getAdditionalPredicate
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|rc
operator|=
name|LogicExpression
operator|.
name|createAND
argument_list|(
name|info
operator|.
name|getAdditionalPredicate
argument_list|()
argument_list|,
name|rc
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|rc
return|;
block|}
specifier|public
name|boolean
name|matches
parameter_list|(
name|MessageReference
name|node
parameter_list|,
name|MessageEvaluationContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ConsumerId
name|targetConsumerId
init|=
name|node
operator|.
name|getTargetConsumerId
argument_list|()
decl_stmt|;
if|if
condition|(
name|targetConsumerId
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|targetConsumerId
operator|.
name|equals
argument_list|(
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
try|try
block|{
return|return
operator|(
name|selectorExpression
operator|==
literal|null
operator|||
name|selectorExpression
operator|.
name|matches
argument_list|(
name|context
argument_list|)
operator|)
operator|&&
name|this
operator|.
name|context
operator|.
name|isAllowedToConsume
argument_list|(
name|node
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Selector failed to evaluate: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
specifier|public
name|boolean
name|matches
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
return|return
name|destinationFilter
operator|.
name|matches
argument_list|(
name|destination
argument_list|)
return|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Destination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
name|destinations
operator|.
name|add
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|remove
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Destination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
name|destinations
operator|.
name|remove
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ConsumerInfo
name|getConsumerInfo
parameter_list|()
block|{
return|return
name|info
return|;
block|}
specifier|public
name|void
name|gc
parameter_list|()
block|{             }
specifier|public
name|boolean
name|isSlaveBroker
parameter_list|()
block|{
return|return
name|broker
operator|.
name|isSlaveBroker
argument_list|()
return|;
block|}
specifier|public
name|ConnectionContext
name|getContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
specifier|public
name|ConsumerInfo
name|getInfo
parameter_list|()
block|{
return|return
name|info
return|;
block|}
specifier|public
name|BooleanExpression
name|getSelectorExpression
parameter_list|()
block|{
return|return
name|selectorExpression
return|;
block|}
specifier|public
name|String
name|getSelector
parameter_list|()
block|{
return|return
name|info
operator|.
name|getSelector
argument_list|()
return|;
block|}
specifier|public
name|void
name|setSelector
parameter_list|(
name|String
name|selector
parameter_list|)
throws|throws
name|InvalidSelectorException
block|{
name|ConsumerInfo
name|copy
init|=
name|info
operator|.
name|copy
argument_list|()
decl_stmt|;
name|copy
operator|.
name|setSelector
argument_list|(
name|selector
argument_list|)
expr_stmt|;
name|BooleanExpression
name|newSelector
init|=
name|parseSelector
argument_list|(
name|copy
argument_list|)
decl_stmt|;
comment|// its valid so lets actually update it now
name|info
operator|.
name|setSelector
argument_list|(
name|selector
argument_list|)
expr_stmt|;
name|this
operator|.
name|selectorExpression
operator|=
name|newSelector
expr_stmt|;
block|}
specifier|public
name|ObjectName
name|getObjectName
parameter_list|()
block|{
return|return
name|objectName
return|;
block|}
specifier|public
name|void
name|setObjectName
parameter_list|(
name|ObjectName
name|objectName
parameter_list|)
block|{
name|this
operator|.
name|objectName
operator|=
name|objectName
expr_stmt|;
block|}
specifier|public
name|int
name|getPrefetchSize
parameter_list|()
block|{
return|return
name|info
operator|.
name|getPrefetchSize
argument_list|()
return|;
block|}
block|}
end_class

end_unit

