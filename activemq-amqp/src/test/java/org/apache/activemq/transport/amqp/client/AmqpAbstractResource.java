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
name|client
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
name|client
operator|.
name|util
operator|.
name|AsyncResult
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
name|Symbol
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
name|AmqpError
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
name|Endpoint
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
name|EndpointState
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
comment|/**  * Abstract base for all AmqpResource implementations to extend.  *  * This abstract class wraps up the basic state management bits so that the concrete  * object don't have to reproduce it.  Provides hooks for the subclasses to initialize  * and shutdown.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AmqpAbstractResource
parameter_list|<
name|E
extends|extends
name|Endpoint
parameter_list|>
implements|implements
name|AmqpResource
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
name|AmqpAbstractResource
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|AsyncResult
name|openRequest
decl_stmt|;
specifier|protected
name|AsyncResult
name|closeRequest
decl_stmt|;
specifier|private
name|AmqpStateInspector
name|amqpStateInspector
init|=
operator|new
name|AmqpStateInspector
argument_list|()
decl_stmt|;
specifier|private
name|E
name|endpoint
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|open
parameter_list|(
name|AsyncResult
name|request
parameter_list|)
block|{
name|this
operator|.
name|openRequest
operator|=
name|request
expr_stmt|;
name|doOpen
argument_list|()
expr_stmt|;
name|getEndpoint
argument_list|()
operator|.
name|setContext
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isOpen
parameter_list|()
block|{
return|return
name|getEndpoint
argument_list|()
operator|.
name|getRemoteState
argument_list|()
operator|==
name|EndpointState
operator|.
name|ACTIVE
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|opened
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|openRequest
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|openRequest
operator|.
name|onSuccess
argument_list|()
expr_stmt|;
name|this
operator|.
name|openRequest
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|(
name|AsyncResult
name|request
parameter_list|)
block|{
comment|// If already closed signal success or else the caller might never get notified.
if|if
condition|(
name|getEndpoint
argument_list|()
operator|.
name|getLocalState
argument_list|()
operator|==
name|EndpointState
operator|.
name|CLOSED
operator|||
name|getEndpoint
argument_list|()
operator|.
name|getRemoteState
argument_list|()
operator|==
name|EndpointState
operator|.
name|CLOSED
condition|)
block|{
if|if
condition|(
name|getEndpoint
argument_list|()
operator|.
name|getLocalState
argument_list|()
operator|!=
name|EndpointState
operator|.
name|CLOSED
condition|)
block|{
comment|// Remote already closed this resource, close locally and free.
if|if
condition|(
name|getEndpoint
argument_list|()
operator|.
name|getLocalState
argument_list|()
operator|!=
name|EndpointState
operator|.
name|CLOSED
condition|)
block|{
name|doClose
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
name|request
operator|.
name|onSuccess
argument_list|()
expr_stmt|;
return|return;
block|}
name|this
operator|.
name|closeRequest
operator|=
name|request
expr_stmt|;
name|doClose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|getEndpoint
argument_list|()
operator|.
name|getLocalState
argument_list|()
operator|==
name|EndpointState
operator|.
name|CLOSED
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|closed
parameter_list|()
block|{
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
if|if
condition|(
name|this
operator|.
name|closeRequest
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|closeRequest
operator|.
name|onSuccess
argument_list|()
expr_stmt|;
name|this
operator|.
name|closeRequest
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|failed
parameter_list|()
block|{
name|failed
argument_list|(
operator|new
name|Exception
argument_list|(
literal|"Remote request failed."
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|failed
parameter_list|(
name|Exception
name|cause
parameter_list|)
block|{
if|if
condition|(
name|openRequest
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|endpoint
operator|!=
literal|null
condition|)
block|{
comment|// TODO: if this is a producer/consumer link then we may only be detached,
comment|// rather than fully closed, and should respond appropriately.
name|endpoint
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|openRequest
operator|.
name|onFailure
argument_list|(
name|cause
argument_list|)
expr_stmt|;
name|openRequest
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|closeRequest
operator|!=
literal|null
condition|)
block|{
name|closeRequest
operator|.
name|onFailure
argument_list|(
name|cause
argument_list|)
expr_stmt|;
name|closeRequest
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|remotelyClosed
parameter_list|(
name|AmqpConnection
name|connection
parameter_list|)
block|{
name|Exception
name|error
init|=
name|getRemoteError
argument_list|()
decl_stmt|;
if|if
condition|(
name|error
operator|==
literal|null
condition|)
block|{
name|error
operator|=
operator|new
name|IOException
argument_list|(
literal|"Remote has closed without error information"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|endpoint
operator|!=
literal|null
condition|)
block|{
comment|// TODO: if this is a producer/consumer link then we may only be detached,
comment|// rather than fully closed, and should respond appropriately.
name|endpoint
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Resource {} was remotely closed"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|connection
operator|.
name|fireClientException
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
specifier|public
name|E
name|getEndpoint
parameter_list|()
block|{
return|return
name|this
operator|.
name|endpoint
return|;
block|}
specifier|public
name|void
name|setEndpoint
parameter_list|(
name|E
name|endpoint
parameter_list|)
block|{
name|this
operator|.
name|endpoint
operator|=
name|endpoint
expr_stmt|;
block|}
specifier|public
name|AmqpStateInspector
name|getStateInspector
parameter_list|()
block|{
return|return
name|amqpStateInspector
return|;
block|}
specifier|public
name|void
name|setStateInspector
parameter_list|(
name|AmqpStateInspector
name|stateInspector
parameter_list|)
block|{
if|if
condition|(
name|stateInspector
operator|==
literal|null
condition|)
block|{
name|stateInspector
operator|=
operator|new
name|AmqpStateInspector
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|amqpStateInspector
operator|=
name|stateInspector
expr_stmt|;
block|}
specifier|public
name|EndpointState
name|getLocalState
parameter_list|()
block|{
if|if
condition|(
name|getEndpoint
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
name|EndpointState
operator|.
name|UNINITIALIZED
return|;
block|}
return|return
name|getEndpoint
argument_list|()
operator|.
name|getLocalState
argument_list|()
return|;
block|}
specifier|public
name|EndpointState
name|getRemoteState
parameter_list|()
block|{
if|if
condition|(
name|getEndpoint
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
name|EndpointState
operator|.
name|UNINITIALIZED
return|;
block|}
return|return
name|getEndpoint
argument_list|()
operator|.
name|getRemoteState
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasRemoteError
parameter_list|()
block|{
return|return
name|getEndpoint
argument_list|()
operator|.
name|getRemoteCondition
argument_list|()
operator|.
name|getCondition
argument_list|()
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Exception
name|getRemoteError
parameter_list|()
block|{
name|String
name|message
init|=
name|getRemoteErrorMessage
argument_list|()
decl_stmt|;
name|Exception
name|remoteError
init|=
literal|null
decl_stmt|;
name|Symbol
name|error
init|=
name|getEndpoint
argument_list|()
operator|.
name|getRemoteCondition
argument_list|()
operator|.
name|getCondition
argument_list|()
decl_stmt|;
if|if
condition|(
name|error
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|error
operator|.
name|equals
argument_list|(
name|AmqpError
operator|.
name|UNAUTHORIZED_ACCESS
argument_list|)
condition|)
block|{
name|remoteError
operator|=
operator|new
name|SecurityException
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|remoteError
operator|=
operator|new
name|Exception
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|remoteError
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getRemoteErrorMessage
parameter_list|()
block|{
name|String
name|message
init|=
literal|"Received unkown error from remote peer"
decl_stmt|;
if|if
condition|(
name|getEndpoint
argument_list|()
operator|.
name|getRemoteCondition
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|ErrorCondition
name|error
init|=
name|getEndpoint
argument_list|()
operator|.
name|getRemoteCondition
argument_list|()
decl_stmt|;
if|if
condition|(
name|error
operator|.
name|getDescription
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|error
operator|.
name|getDescription
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|message
operator|=
name|error
operator|.
name|getDescription
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|message
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|processRemoteOpen
parameter_list|(
name|AmqpConnection
name|connection
parameter_list|)
throws|throws
name|IOException
block|{
name|doOpenInspection
argument_list|()
expr_stmt|;
name|doOpenCompletion
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|processRemoteDetach
parameter_list|(
name|AmqpConnection
name|connection
parameter_list|)
throws|throws
name|IOException
block|{
name|doDetachedInspection
argument_list|()
expr_stmt|;
if|if
condition|(
name|isAwaitingClose
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"{} is now closed: "
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|closed
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|remotelyClosed
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|processRemoteClose
parameter_list|(
name|AmqpConnection
name|connection
parameter_list|)
throws|throws
name|IOException
block|{
name|doClosedInspection
argument_list|()
expr_stmt|;
if|if
condition|(
name|isAwaitingClose
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"{} is now closed: "
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|closed
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isAwaitingOpen
argument_list|()
condition|)
block|{
comment|// Error on Open, create exception and signal failure.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Open of {} failed: "
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|Exception
name|openError
decl_stmt|;
if|if
condition|(
name|hasRemoteError
argument_list|()
condition|)
block|{
name|openError
operator|=
name|getRemoteError
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|openError
operator|=
name|getOpenAbortException
argument_list|()
expr_stmt|;
block|}
name|failed
argument_list|(
name|openError
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|remotelyClosed
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|processDeliveryUpdates
parameter_list|(
name|AmqpConnection
name|connection
parameter_list|)
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|processFlowUpdates
parameter_list|(
name|AmqpConnection
name|connection
parameter_list|)
throws|throws
name|IOException
block|{     }
comment|/**      * Perform the open operation on the managed endpoint.  A subclass may      * override this method to provide additional open actions or configuration      * updates.      */
specifier|protected
name|void
name|doOpen
parameter_list|()
block|{
name|getEndpoint
argument_list|()
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
comment|/**      * Perform the close operation on the managed endpoint.  A subclass may      * override this method to provide additional close actions or alter the      * standard close path such as endpoint detach etc.      */
specifier|protected
name|void
name|doClose
parameter_list|()
block|{
name|getEndpoint
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      * Complete the open operation on the managed endpoint. A subclass may      * override this method to provide additional verification actions or configuration      * updates.      */
specifier|protected
name|void
name|doOpenCompletion
parameter_list|()
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"{} is now open: "
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|opened
argument_list|()
expr_stmt|;
block|}
comment|/**      * When aborting the open operation, and there isnt an error condition,      * provided by the peer, the returned exception will be used instead.      * A subclass may override this method to provide alternative behaviour.      */
specifier|protected
name|Exception
name|getOpenAbortException
parameter_list|()
block|{
return|return
operator|new
name|IOException
argument_list|(
literal|"Open failed unexpectedly."
argument_list|)
return|;
block|}
comment|// TODO - Fina a more generic way to do this.
specifier|protected
specifier|abstract
name|void
name|doOpenInspection
parameter_list|()
function_decl|;
specifier|protected
specifier|abstract
name|void
name|doClosedInspection
parameter_list|()
function_decl|;
specifier|protected
name|void
name|doDetachedInspection
parameter_list|()
block|{}
comment|//----- Private implementation utility methods ---------------------------//
specifier|private
name|boolean
name|isAwaitingOpen
parameter_list|()
block|{
return|return
name|this
operator|.
name|openRequest
operator|!=
literal|null
return|;
block|}
specifier|private
name|boolean
name|isAwaitingClose
parameter_list|()
block|{
return|return
name|this
operator|.
name|closeRequest
operator|!=
literal|null
return|;
block|}
block|}
end_class

end_unit

