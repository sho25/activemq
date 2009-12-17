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
name|security
package|;
end_package

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
name|BrokerFilter
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
name|ActiveMQQueue
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
name|ActiveMQTopic
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
name|command
operator|.
name|DestinationInfo
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
name|command
operator|.
name|ProducerInfo
import|;
end_import

begin_comment
comment|/**  * Verifies if a authenticated user can do an operation against the broker using  * an authorization map.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|AuthorizationBroker
extends|extends
name|BrokerFilter
implements|implements
name|SecurityAdminMBean
block|{
specifier|private
specifier|final
name|AuthorizationMap
name|authorizationMap
decl_stmt|;
specifier|public
name|AuthorizationBroker
parameter_list|(
name|Broker
name|next
parameter_list|,
name|AuthorizationMap
name|authorizationMap
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|authorizationMap
operator|=
name|authorizationMap
expr_stmt|;
block|}
specifier|public
name|void
name|addDestinationInfo
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|DestinationInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
name|addDestination
argument_list|(
name|context
argument_list|,
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|addDestinationInfo
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Destination
name|addDestination
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|SecurityContext
name|securityContext
init|=
operator|(
name|SecurityContext
operator|)
name|context
operator|.
name|getSecurityContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|securityContext
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SecurityException
argument_list|(
literal|"User is not authenticated."
argument_list|)
throw|;
block|}
name|Destination
name|existing
init|=
name|this
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|get
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|existing
operator|!=
literal|null
condition|)
block|{
return|return
name|super
operator|.
name|addDestination
argument_list|(
name|context
argument_list|,
name|destination
argument_list|)
return|;
block|}
if|if
condition|(
operator|!
name|securityContext
operator|.
name|isBrokerContext
argument_list|()
condition|)
block|{
name|Set
argument_list|<
name|?
argument_list|>
name|allowedACLs
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|destination
operator|.
name|isTemporary
argument_list|()
condition|)
block|{
name|allowedACLs
operator|=
name|authorizationMap
operator|.
name|getAdminACLs
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|allowedACLs
operator|=
name|authorizationMap
operator|.
name|getTempDestinationAdminACLs
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|allowedACLs
operator|!=
literal|null
operator|&&
operator|!
name|securityContext
operator|.
name|isInOneOf
argument_list|(
name|allowedACLs
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SecurityException
argument_list|(
literal|"User "
operator|+
name|securityContext
operator|.
name|getUserName
argument_list|()
operator|+
literal|" is not authorized to create: "
operator|+
name|destination
argument_list|)
throw|;
block|}
block|}
return|return
name|super
operator|.
name|addDestination
argument_list|(
name|context
argument_list|,
name|destination
argument_list|)
return|;
block|}
specifier|public
name|void
name|removeDestination
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|SecurityContext
name|securityContext
init|=
operator|(
name|SecurityContext
operator|)
name|context
operator|.
name|getSecurityContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|securityContext
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SecurityException
argument_list|(
literal|"User is not authenticated."
argument_list|)
throw|;
block|}
name|Set
argument_list|<
name|?
argument_list|>
name|allowedACLs
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|destination
operator|.
name|isTemporary
argument_list|()
condition|)
block|{
name|allowedACLs
operator|=
name|authorizationMap
operator|.
name|getAdminACLs
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|allowedACLs
operator|=
name|authorizationMap
operator|.
name|getTempDestinationAdminACLs
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|securityContext
operator|.
name|isBrokerContext
argument_list|()
operator|&&
name|allowedACLs
operator|!=
literal|null
operator|&&
operator|!
name|securityContext
operator|.
name|isInOneOf
argument_list|(
name|allowedACLs
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SecurityException
argument_list|(
literal|"User "
operator|+
name|securityContext
operator|.
name|getUserName
argument_list|()
operator|+
literal|" is not authorized to remove: "
operator|+
name|destination
argument_list|)
throw|;
block|}
name|super
operator|.
name|removeDestination
argument_list|(
name|context
argument_list|,
name|destination
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Subscription
name|addConsumer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConsumerInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|SecurityContext
name|subject
init|=
operator|(
name|SecurityContext
operator|)
name|context
operator|.
name|getSecurityContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|subject
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SecurityException
argument_list|(
literal|"User is not authenticated."
argument_list|)
throw|;
block|}
name|Set
argument_list|<
name|?
argument_list|>
name|allowedACLs
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|info
operator|.
name|getDestination
argument_list|()
operator|.
name|isTemporary
argument_list|()
condition|)
block|{
name|allowedACLs
operator|=
name|authorizationMap
operator|.
name|getReadACLs
argument_list|(
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|allowedACLs
operator|=
name|authorizationMap
operator|.
name|getTempDestinationReadACLs
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|subject
operator|.
name|isBrokerContext
argument_list|()
operator|&&
name|allowedACLs
operator|!=
literal|null
operator|&&
operator|!
name|subject
operator|.
name|isInOneOf
argument_list|(
name|allowedACLs
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SecurityException
argument_list|(
literal|"User "
operator|+
name|subject
operator|.
name|getUserName
argument_list|()
operator|+
literal|" is not authorized to read from: "
operator|+
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
throw|;
block|}
name|subject
operator|.
name|getAuthorizedReadDests
argument_list|()
operator|.
name|put
argument_list|(
name|info
operator|.
name|getDestination
argument_list|()
argument_list|,
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
comment|/*          * Need to think about this a little more. We could do per message          * security checking to implement finer grained security checking. For          * example a user can only see messages with price>1000 . Perhaps this          * should just be another additional broker filter that installs this          * type of feature. If we did want to do that, then we would install a          * predicate. We should be careful since there may be an existing          * predicate already assigned and the consumer info may be sent to a          * remote broker, so it also needs to support being marshaled.          * info.setAdditionalPredicate(new BooleanExpression() { public boolean          * matches(MessageEvaluationContext message) throws JMSException { if(          * !subject.getAuthorizedReadDests().contains(message.getDestination()) ) {          * Set allowedACLs =          * authorizationMap.getReadACLs(message.getDestination());          * if(allowedACLs!=null&& !subject.isInOneOf(allowedACLs)) return          * false; subject.getAuthorizedReadDests().put(message.getDestination(),          * message.getDestination()); } return true; } public Object          * evaluate(MessageEvaluationContext message) throws JMSException {          * return matches(message) ? Boolean.TRUE : Boolean.FALSE; } });          */
return|return
name|super
operator|.
name|addConsumer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
return|;
block|}
specifier|public
name|void
name|addProducer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ProducerInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
name|SecurityContext
name|subject
init|=
operator|(
name|SecurityContext
operator|)
name|context
operator|.
name|getSecurityContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|subject
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SecurityException
argument_list|(
literal|"User is not authenticated."
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|subject
operator|.
name|isBrokerContext
argument_list|()
operator|&&
name|info
operator|.
name|getDestination
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|Set
argument_list|<
name|?
argument_list|>
name|allowedACLs
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|info
operator|.
name|getDestination
argument_list|()
operator|.
name|isTemporary
argument_list|()
condition|)
block|{
name|allowedACLs
operator|=
name|authorizationMap
operator|.
name|getWriteACLs
argument_list|(
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|allowedACLs
operator|=
name|authorizationMap
operator|.
name|getTempDestinationWriteACLs
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|allowedACLs
operator|!=
literal|null
operator|&&
operator|!
name|subject
operator|.
name|isInOneOf
argument_list|(
name|allowedACLs
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SecurityException
argument_list|(
literal|"User "
operator|+
name|subject
operator|.
name|getUserName
argument_list|()
operator|+
literal|" is not authorized to write to: "
operator|+
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
throw|;
block|}
name|subject
operator|.
name|getAuthorizedWriteDests
argument_list|()
operator|.
name|put
argument_list|(
name|info
operator|.
name|getDestination
argument_list|()
argument_list|,
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|addProducer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|send
parameter_list|(
name|ProducerBrokerExchange
name|producerExchange
parameter_list|,
name|Message
name|messageSend
parameter_list|)
throws|throws
name|Exception
block|{
name|SecurityContext
name|subject
init|=
operator|(
name|SecurityContext
operator|)
name|producerExchange
operator|.
name|getConnectionContext
argument_list|()
operator|.
name|getSecurityContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|subject
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SecurityException
argument_list|(
literal|"User is not authenticated."
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|subject
operator|.
name|isBrokerContext
argument_list|()
operator|&&
operator|!
name|subject
operator|.
name|getAuthorizedWriteDests
argument_list|()
operator|.
name|contains
argument_list|(
name|messageSend
operator|.
name|getDestination
argument_list|()
argument_list|)
condition|)
block|{
name|Set
argument_list|<
name|?
argument_list|>
name|allowedACLs
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|messageSend
operator|.
name|getDestination
argument_list|()
operator|.
name|isTemporary
argument_list|()
condition|)
block|{
name|allowedACLs
operator|=
name|authorizationMap
operator|.
name|getWriteACLs
argument_list|(
name|messageSend
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|allowedACLs
operator|=
name|authorizationMap
operator|.
name|getTempDestinationWriteACLs
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|allowedACLs
operator|!=
literal|null
operator|&&
operator|!
name|subject
operator|.
name|isInOneOf
argument_list|(
name|allowedACLs
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SecurityException
argument_list|(
literal|"User "
operator|+
name|subject
operator|.
name|getUserName
argument_list|()
operator|+
literal|" is not authorized to write to: "
operator|+
name|messageSend
operator|.
name|getDestination
argument_list|()
argument_list|)
throw|;
block|}
name|subject
operator|.
name|getAuthorizedWriteDests
argument_list|()
operator|.
name|put
argument_list|(
name|messageSend
operator|.
name|getDestination
argument_list|()
argument_list|,
name|messageSend
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|send
argument_list|(
name|producerExchange
argument_list|,
name|messageSend
argument_list|)
expr_stmt|;
block|}
comment|// SecurityAdminMBean interface
comment|// -------------------------------------------------------------------------
specifier|public
name|void
name|addQueueRole
parameter_list|(
name|String
name|queue
parameter_list|,
name|String
name|operation
parameter_list|,
name|String
name|role
parameter_list|)
block|{
name|addDestinationRole
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|queue
argument_list|)
argument_list|,
name|operation
argument_list|,
name|role
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addTopicRole
parameter_list|(
name|String
name|topic
parameter_list|,
name|String
name|operation
parameter_list|,
name|String
name|role
parameter_list|)
block|{
name|addDestinationRole
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
name|topic
argument_list|)
argument_list|,
name|operation
argument_list|,
name|role
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeQueueRole
parameter_list|(
name|String
name|queue
parameter_list|,
name|String
name|operation
parameter_list|,
name|String
name|role
parameter_list|)
block|{
name|removeDestinationRole
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|queue
argument_list|)
argument_list|,
name|operation
argument_list|,
name|role
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeTopicRole
parameter_list|(
name|String
name|topic
parameter_list|,
name|String
name|operation
parameter_list|,
name|String
name|role
parameter_list|)
block|{
name|removeDestinationRole
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
name|topic
argument_list|)
argument_list|,
name|operation
argument_list|,
name|role
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addDestinationRole
parameter_list|(
name|javax
operator|.
name|jms
operator|.
name|Destination
name|destination
parameter_list|,
name|String
name|operation
parameter_list|,
name|String
name|role
parameter_list|)
block|{     }
specifier|public
name|void
name|removeDestinationRole
parameter_list|(
name|javax
operator|.
name|jms
operator|.
name|Destination
name|destination
parameter_list|,
name|String
name|operation
parameter_list|,
name|String
name|role
parameter_list|)
block|{     }
specifier|public
name|void
name|addRole
parameter_list|(
name|String
name|role
parameter_list|)
block|{     }
specifier|public
name|void
name|addUserRole
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|role
parameter_list|)
block|{     }
specifier|public
name|void
name|removeRole
parameter_list|(
name|String
name|role
parameter_list|)
block|{     }
specifier|public
name|void
name|removeUserRole
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|role
parameter_list|)
block|{     }
block|}
end_class

end_unit

