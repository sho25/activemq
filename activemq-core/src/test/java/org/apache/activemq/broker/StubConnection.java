begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Service
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
name|command
operator|.
name|ExceptionResponse
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
name|Response
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
name|ShutdownInfo
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
name|openwire
operator|.
name|OpenWireFormat
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
name|thread
operator|.
name|TaskRunnerFactory
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
name|DefaultTransportListener
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
name|Transport
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
name|JMSExceptionSupport
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
name|ServiceSupport
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
name|BlockingQueue
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
name|LinkedBlockingQueue
import|;
end_import

begin_class
specifier|public
class|class
name|StubConnection
implements|implements
name|Service
block|{
specifier|private
specifier|final
name|BlockingQueue
name|dispatchQueue
init|=
operator|new
name|LinkedBlockingQueue
argument_list|()
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|private
name|Transport
name|transport
decl_stmt|;
name|boolean
name|shuttingDown
init|=
literal|false
decl_stmt|;
specifier|private
name|OpenWireFormat
name|wireFormat
init|=
operator|new
name|OpenWireFormat
argument_list|()
decl_stmt|;
specifier|public
name|StubConnection
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
name|this
argument_list|(
name|broker
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|StubConnection
parameter_list|(
name|BrokerService
name|broker
parameter_list|,
name|TaskRunnerFactory
name|taskRunnerFactory
parameter_list|)
throws|throws
name|Exception
block|{
name|connection
operator|=
operator|new
name|AbstractConnection
argument_list|(
literal|null
argument_list|,
name|broker
operator|.
name|getBroker
argument_list|()
argument_list|,
literal|null
argument_list|)
block|{
specifier|protected
name|void
name|dispatch
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
try|try
block|{
name|StubConnection
operator|.
name|this
operator|.
name|dispatch
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|serviceException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|OpenWireFormat
name|getWireFormat
parameter_list|()
block|{
return|return
name|wireFormat
return|;
block|}
specifier|public
name|String
name|getRemoteAddress
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
expr_stmt|;
block|}
specifier|protected
name|void
name|dispatch
parameter_list|(
name|Command
name|command
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
name|dispatchQueue
operator|.
name|put
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
specifier|public
name|StubConnection
parameter_list|(
name|Connection
name|connection
parameter_list|)
block|{
name|this
operator|.
name|connection
operator|=
name|connection
expr_stmt|;
block|}
specifier|public
name|StubConnection
parameter_list|(
name|Transport
name|transport
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|transport
operator|=
name|transport
expr_stmt|;
name|transport
operator|.
name|setTransportListener
argument_list|(
operator|new
name|DefaultTransportListener
argument_list|()
block|{
specifier|public
name|void
name|onCommand
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|command
operator|.
name|getClass
argument_list|()
operator|==
name|ShutdownInfo
operator|.
name|class
condition|)
block|{
name|shuttingDown
operator|=
literal|true
expr_stmt|;
block|}
name|StubConnection
operator|.
name|this
operator|.
name|dispatch
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|onException
argument_list|(
operator|new
name|IOException
argument_list|(
literal|""
operator|+
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|onException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
if|if
condition|(
operator|!
name|shuttingDown
condition|)
block|{
name|error
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|transport
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|BlockingQueue
name|getDispatchQueue
parameter_list|()
block|{
return|return
name|dispatchQueue
return|;
block|}
specifier|public
name|void
name|send
parameter_list|(
name|Command
name|command
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|command
operator|instanceof
name|Message
condition|)
block|{
name|Message
name|message
init|=
operator|(
name|Message
operator|)
name|command
decl_stmt|;
name|message
operator|.
name|setProducerId
argument_list|(
name|message
operator|.
name|getMessageId
argument_list|()
operator|.
name|getProducerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|command
operator|.
name|setResponseRequired
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
name|Response
name|response
init|=
name|connection
operator|.
name|service
argument_list|(
name|command
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|!=
literal|null
operator|&&
name|response
operator|.
name|isException
argument_list|()
condition|)
block|{
name|ExceptionResponse
name|er
init|=
operator|(
name|ExceptionResponse
operator|)
name|response
decl_stmt|;
throw|throw
name|JMSExceptionSupport
operator|.
name|create
argument_list|(
name|er
operator|.
name|getException
argument_list|()
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|transport
operator|!=
literal|null
condition|)
block|{
name|transport
operator|.
name|oneway
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Response
name|request
parameter_list|(
name|Command
name|command
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|command
operator|instanceof
name|Message
condition|)
block|{
name|Message
name|message
init|=
operator|(
name|Message
operator|)
name|command
decl_stmt|;
name|message
operator|.
name|setProducerId
argument_list|(
name|message
operator|.
name|getMessageId
argument_list|()
operator|.
name|getProducerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|command
operator|.
name|setResponseRequired
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
name|Response
name|response
init|=
name|connection
operator|.
name|service
argument_list|(
name|command
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|!=
literal|null
operator|&&
name|response
operator|.
name|isException
argument_list|()
condition|)
block|{
name|ExceptionResponse
name|er
init|=
operator|(
name|ExceptionResponse
operator|)
name|response
decl_stmt|;
throw|throw
name|JMSExceptionSupport
operator|.
name|create
argument_list|(
name|er
operator|.
name|getException
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|response
return|;
block|}
elseif|else
if|if
condition|(
name|transport
operator|!=
literal|null
condition|)
block|{
name|Response
name|response
init|=
name|transport
operator|.
name|request
argument_list|(
name|command
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|!=
literal|null
operator|&&
name|response
operator|.
name|isException
argument_list|()
condition|)
block|{
name|ExceptionResponse
name|er
init|=
operator|(
name|ExceptionResponse
operator|)
name|response
decl_stmt|;
throw|throw
name|JMSExceptionSupport
operator|.
name|create
argument_list|(
name|er
operator|.
name|getException
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|response
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|Connection
name|getConnection
parameter_list|()
block|{
return|return
name|connection
return|;
block|}
specifier|public
name|Transport
name|getTransport
parameter_list|()
block|{
return|return
name|transport
return|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|shuttingDown
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|transport
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|transport
operator|.
name|oneway
argument_list|(
operator|new
name|ShutdownInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{             }
name|ServiceSupport
operator|.
name|dispose
argument_list|(
name|transport
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

