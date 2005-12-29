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
name|transport
operator|.
name|activeio
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
name|SocketException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|command
operator|.
name|AsyncCommandChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|net
operator|.
name|SocketMetadata
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
name|WireFormatInfo
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
name|management
operator|.
name|CountStatisticImpl
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
name|FutureResponse
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
name|transport
operator|.
name|TransportListener
import|;
end_import

begin_comment
comment|/**  * An implementation of the {@link Transport} interface using ActiveIO  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ActiveIOTransport
implements|implements
name|Transport
block|{
specifier|private
name|AsyncCommandChannel
name|commandChannel
decl_stmt|;
specifier|private
name|TransportListener
name|transportListener
decl_stmt|;
specifier|private
name|long
name|timeout
init|=
literal|2000
decl_stmt|;
specifier|private
name|int
name|minmumWireFormatVersion
init|=
literal|0
decl_stmt|;
specifier|private
name|long
name|maxInactivityDuration
init|=
literal|60000
decl_stmt|;
specifier|private
name|boolean
name|trace
init|=
literal|false
decl_stmt|;
specifier|private
name|long
name|stopTimeout
init|=
literal|2000
decl_stmt|;
specifier|private
name|CountStatisticImpl
name|readCounter
decl_stmt|;
specifier|private
name|CountStatisticImpl
name|writeCounter
decl_stmt|;
specifier|public
name|ActiveIOTransport
parameter_list|(
name|AsyncCommandChannel
name|commandChannel
parameter_list|)
block|{
name|this
operator|.
name|commandChannel
operator|=
name|commandChannel
expr_stmt|;
name|this
operator|.
name|commandChannel
operator|.
name|setCommandListener
argument_list|(
operator|new
name|org
operator|.
name|activeio
operator|.
name|command
operator|.
name|CommandListener
argument_list|()
block|{
specifier|public
name|void
name|onCommand
parameter_list|(
name|Object
name|command
parameter_list|)
block|{
if|if
condition|(
name|command
operator|.
name|getClass
argument_list|()
operator|==
name|WireFormatInfo
operator|.
name|class
condition|)
block|{
name|WireFormatInfo
name|info
init|=
operator|(
name|WireFormatInfo
operator|)
name|command
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|isTcpNoDelayEnabled
argument_list|()
condition|)
block|{
try|try
block|{
name|enableTcpNodeDelay
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SocketException
name|e
parameter_list|)
block|{
name|onError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|transportListener
operator|.
name|onCommand
argument_list|(
operator|(
name|Command
operator|)
name|command
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onError
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|IOException
condition|)
block|{
name|transportListener
operator|.
name|onException
argument_list|(
operator|(
name|IOException
operator|)
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|transportListener
operator|.
name|onException
argument_list|(
operator|(
name|IOException
operator|)
operator|new
name|IOException
argument_list|()
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|enableTcpNodeDelay
parameter_list|()
throws|throws
name|SocketException
block|{
name|SocketMetadata
name|sm
init|=
operator|(
name|SocketMetadata
operator|)
name|commandChannel
operator|.
name|getAdapter
argument_list|(
name|SocketMetadata
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|sm
operator|!=
literal|null
condition|)
block|{
name|sm
operator|.
name|setTcpNoDelay
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|oneway
parameter_list|(
name|Command
name|command
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|command
operator|.
name|getClass
argument_list|()
operator|==
name|WireFormatInfo
operator|.
name|class
condition|)
block|{
name|WireFormatInfo
name|info
init|=
operator|(
name|WireFormatInfo
operator|)
name|command
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|isTcpNoDelayEnabled
argument_list|()
condition|)
block|{
name|enableTcpNodeDelay
argument_list|()
expr_stmt|;
block|}
block|}
name|commandChannel
operator|.
name|writeCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
specifier|public
name|FutureResponse
name|asyncRequest
parameter_list|(
name|Command
name|command
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Unsupported Method"
argument_list|)
throw|;
block|}
specifier|public
name|Response
name|request
parameter_list|(
name|Command
name|command
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Unsupported Method"
argument_list|)
throw|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|commandChannel
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|commandChannel
operator|.
name|stop
argument_list|(
name|stopTimeout
argument_list|)
expr_stmt|;
name|commandChannel
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
comment|// Properties
comment|// -------------------------------------------------------------------------
specifier|public
name|TransportListener
name|getTransportListener
parameter_list|()
block|{
return|return
name|transportListener
return|;
block|}
specifier|public
name|void
name|setTransportListener
parameter_list|(
name|TransportListener
name|listener
parameter_list|)
block|{
name|this
operator|.
name|transportListener
operator|=
name|listener
expr_stmt|;
block|}
specifier|public
name|AsyncCommandChannel
name|getCommandChannel
parameter_list|()
block|{
return|return
name|commandChannel
return|;
block|}
specifier|public
name|long
name|getTimeout
parameter_list|()
block|{
return|return
name|timeout
return|;
block|}
specifier|public
name|void
name|setTimeout
parameter_list|(
name|long
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
block|}
specifier|public
name|Object
name|narrow
parameter_list|(
name|Class
name|target
parameter_list|)
block|{
if|if
condition|(
name|target
operator|.
name|isAssignableFrom
argument_list|(
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|int
name|getMinmumWireFormatVersion
parameter_list|()
block|{
return|return
name|minmumWireFormatVersion
return|;
block|}
specifier|public
name|void
name|setMinmumWireFormatVersion
parameter_list|(
name|int
name|minmumWireFormatVersion
parameter_list|)
block|{
name|this
operator|.
name|minmumWireFormatVersion
operator|=
name|minmumWireFormatVersion
expr_stmt|;
block|}
specifier|public
name|long
name|getMaxInactivityDuration
parameter_list|()
block|{
return|return
name|maxInactivityDuration
return|;
block|}
specifier|public
name|void
name|setMaxInactivityDuration
parameter_list|(
name|long
name|maxInactivityDuration
parameter_list|)
block|{
name|this
operator|.
name|maxInactivityDuration
operator|=
name|maxInactivityDuration
expr_stmt|;
block|}
specifier|public
name|long
name|getStopTimeout
parameter_list|()
block|{
return|return
name|stopTimeout
return|;
block|}
specifier|public
name|void
name|setStopTimeout
parameter_list|(
name|long
name|stopTimeout
parameter_list|)
block|{
name|this
operator|.
name|stopTimeout
operator|=
name|stopTimeout
expr_stmt|;
block|}
specifier|public
name|boolean
name|isTrace
parameter_list|()
block|{
return|return
name|trace
return|;
block|}
specifier|public
name|void
name|setTrace
parameter_list|(
name|boolean
name|trace
parameter_list|)
block|{
name|this
operator|.
name|trace
operator|=
name|trace
expr_stmt|;
block|}
specifier|public
name|void
name|setReadCounter
parameter_list|(
name|CountStatisticImpl
name|readCounter
parameter_list|)
block|{
name|this
operator|.
name|readCounter
operator|=
name|readCounter
expr_stmt|;
block|}
specifier|public
name|void
name|setWriteCounter
parameter_list|(
name|CountStatisticImpl
name|writeCounter
parameter_list|)
block|{
name|this
operator|.
name|writeCounter
operator|=
name|writeCounter
expr_stmt|;
block|}
specifier|public
name|CountStatisticImpl
name|getReadCounter
parameter_list|()
block|{
return|return
name|readCounter
return|;
block|}
specifier|public
name|CountStatisticImpl
name|getWriteCounter
parameter_list|()
block|{
return|return
name|writeCounter
return|;
block|}
block|}
end_class

end_unit

