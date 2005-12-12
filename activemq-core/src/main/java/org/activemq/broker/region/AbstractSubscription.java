begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a> * * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. * **/
end_comment

begin_package
package|package
name|org
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
name|org
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
specifier|protected
specifier|final
name|Log
name|log
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
specifier|final
specifier|protected
name|BooleanExpression
name|selector
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
name|selector
operator|=
name|parseSelector
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|this
operator|.
name|log
operator|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"."
operator|+
name|info
operator|.
name|getConsumerId
argument_list|()
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
name|selector
operator|==
literal|null
operator|||
name|selector
operator|.
name|matches
argument_list|(
name|context
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
name|Throwable
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
name|Throwable
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
block|}
end_class

end_unit

