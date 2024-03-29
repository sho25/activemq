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
name|region
operator|.
name|virtual
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

begin_comment
comment|/**  * Represents a destination which is filtered using some predicate such as a selector  * so that messages are only dispatched to the destination if they match the filter.  *  * @org.apache.xbean.XBean  *  *  */
end_comment

begin_class
specifier|public
class|class
name|FilteredDestination
block|{
specifier|private
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|private
name|String
name|selector
decl_stmt|;
specifier|private
name|BooleanExpression
name|filter
decl_stmt|;
specifier|public
name|boolean
name|matches
parameter_list|(
name|MessageEvaluationContext
name|context
parameter_list|)
throws|throws
name|JMSException
block|{
name|BooleanExpression
name|booleanExpression
init|=
name|getFilter
argument_list|()
decl_stmt|;
if|if
condition|(
name|booleanExpression
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|booleanExpression
operator|.
name|matches
argument_list|(
name|context
argument_list|)
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
comment|/**      * The destination to send messages to if they match the filter      */
specifier|public
name|void
name|setDestination
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
block|}
specifier|public
name|String
name|getSelector
parameter_list|()
block|{
return|return
name|selector
return|;
block|}
comment|/**      * Sets the JMS selector used to filter messages before forwarding them to this destination      */
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
name|this
operator|.
name|selector
operator|=
name|selector
expr_stmt|;
name|setFilter
argument_list|(
name|SelectorParser
operator|.
name|parse
argument_list|(
name|selector
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|BooleanExpression
name|getFilter
parameter_list|()
block|{
return|return
name|filter
return|;
block|}
specifier|public
name|void
name|setFilter
parameter_list|(
name|BooleanExpression
name|filter
parameter_list|)
block|{
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
block|}
comment|/**      * Sets the destination property to the given queue name      */
specifier|public
name|void
name|setQueue
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
name|setDestination
argument_list|(
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|queue
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the destination property to the given topic name      */
specifier|public
name|void
name|setTopic
parameter_list|(
name|String
name|topic
parameter_list|)
block|{
name|setDestination
argument_list|(
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|topic
argument_list|,
name|ActiveMQDestination
operator|.
name|TOPIC_TYPE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|destination
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|destination
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|selector
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|selector
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|FilteredDestination
name|other
init|=
operator|(
name|FilteredDestination
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|destination
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|destination
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|destination
operator|.
name|equals
argument_list|(
name|other
operator|.
name|destination
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|selector
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|selector
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|selector
operator|.
name|equals
argument_list|(
name|other
operator|.
name|selector
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

