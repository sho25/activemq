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
name|transport
operator|.
name|amqp
operator|.
name|protocol
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|Command
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
name|transport
operator|.
name|amqp
operator|.
name|ResponseHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|transport
operator|.
name|ErrorCondition
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|engine
operator|.
name|Link
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|engine
operator|.
name|Sender
import|;
end_import

begin_comment
comment|/**  * Abstract AmqpLink implementation that provide basic Link services.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AmqpAbstractLink
parameter_list|<
name|LINK_TYPE
extends|extends
name|Link
parameter_list|>
implements|implements
name|AmqpLink
block|{
specifier|protected
specifier|final
name|AmqpSession
name|session
decl_stmt|;
specifier|protected
specifier|final
name|LINK_TYPE
name|endpoint
decl_stmt|;
specifier|protected
name|boolean
name|closed
decl_stmt|;
specifier|protected
name|boolean
name|opened
decl_stmt|;
specifier|protected
name|List
argument_list|<
name|Runnable
argument_list|>
name|closeActions
init|=
operator|new
name|ArrayList
argument_list|<
name|Runnable
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Creates a new AmqpLink type.      *      * @param session      *        the AmqpSession that servers as the parent of this Link.      * @param endpoint      *        the link endpoint this object represents.      */
specifier|public
name|AmqpAbstractLink
parameter_list|(
name|AmqpSession
name|session
parameter_list|,
name|LINK_TYPE
name|endpoint
parameter_list|)
block|{
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
name|this
operator|.
name|endpoint
operator|=
name|endpoint
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|open
parameter_list|()
block|{
if|if
condition|(
operator|!
name|opened
condition|)
block|{
name|getEndpoint
argument_list|()
operator|.
name|setContext
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|getEndpoint
argument_list|()
operator|.
name|open
argument_list|()
expr_stmt|;
name|opened
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|detach
parameter_list|()
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
if|if
condition|(
name|getEndpoint
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|getEndpoint
argument_list|()
operator|.
name|setContext
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|getEndpoint
argument_list|()
operator|.
name|detach
argument_list|()
expr_stmt|;
name|getEndpoint
argument_list|()
operator|.
name|free
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|(
name|ErrorCondition
name|error
parameter_list|)
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
if|if
condition|(
name|getEndpoint
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|getEndpoint
argument_list|()
operator|instanceof
name|Sender
condition|)
block|{
name|getEndpoint
argument_list|()
operator|.
name|setSource
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|getEndpoint
argument_list|()
operator|.
name|setTarget
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|getEndpoint
argument_list|()
operator|.
name|setCondition
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
if|if
condition|(
name|getEndpoint
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|getEndpoint
argument_list|()
operator|.
name|setContext
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|getEndpoint
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|getEndpoint
argument_list|()
operator|.
name|free
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Runnable
name|action
range|:
name|closeActions
control|)
block|{
name|action
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
name|closeActions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|opened
operator|=
literal|false
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|/**      * @return true if this link has already been opened.      */
specifier|public
name|boolean
name|isOpened
parameter_list|()
block|{
return|return
name|opened
return|;
block|}
comment|/**      * @return true if this link has already been closed.      */
specifier|public
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|closed
return|;
block|}
comment|/**      * @return the Proton Link type this link represents.      */
specifier|public
name|LINK_TYPE
name|getEndpoint
parameter_list|()
block|{
return|return
name|endpoint
return|;
block|}
comment|/**      * @return the parent AmqpSession for this Link instance.      */
specifier|public
name|AmqpSession
name|getSession
parameter_list|()
block|{
return|return
name|session
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addCloseAction
parameter_list|(
name|Runnable
name|action
parameter_list|)
block|{
name|closeActions
operator|.
name|add
argument_list|(
name|action
argument_list|)
expr_stmt|;
block|}
comment|/**      * Shortcut method to hand off an ActiveMQ Command to the broker and assign      * a ResponseHandler to deal with any reply from the broker.      *      * @param command      *        the Command object to send to the Broker.      */
specifier|protected
name|void
name|sendToActiveMQ
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
name|session
operator|.
name|getConnection
argument_list|()
operator|.
name|sendToActiveMQ
argument_list|(
name|command
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Shortcut method to hand off an ActiveMQ Command to the broker and assign      * a ResponseHandler to deal with any reply from the broker.      *      * @param command      *        the Command object to send to the Broker.      * @param handler      *        the ResponseHandler that will handle the Broker's response.      */
specifier|protected
name|void
name|sendToActiveMQ
parameter_list|(
name|Command
name|command
parameter_list|,
name|ResponseHandler
name|handler
parameter_list|)
block|{
name|session
operator|.
name|getConnection
argument_list|()
operator|.
name|sendToActiveMQ
argument_list|(
name|command
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

