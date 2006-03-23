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
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|org
operator|.
name|activeio
operator|.
name|AcceptListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|AsyncChannelServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|Channel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|ChannelFactory
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
name|WireFormat
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
name|WireFormatFactory
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
name|ThreadPriorities
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
name|BrokerInfo
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
name|OpenWireFormatFactory
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
name|TransportAcceptListener
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
name|TransportServer
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
name|Executor
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
name|ScheduledThreadPoolExecutor
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
name|ThreadFactory
import|;
end_import

begin_class
specifier|public
class|class
name|ActiveIOTransportServer
implements|implements
name|TransportServer
block|{
specifier|private
name|AsyncChannelServer
name|server
decl_stmt|;
specifier|private
name|TransportAcceptListener
name|acceptListener
decl_stmt|;
specifier|private
name|WireFormatFactory
name|wireFormatFactory
init|=
operator|new
name|OpenWireFormatFactory
argument_list|()
decl_stmt|;
specifier|private
name|long
name|stopTimeout
init|=
literal|2000
decl_stmt|;
specifier|static
specifier|protected
specifier|final
name|Executor
name|BROKER_CONNECTION_EXECUTOR
init|=
operator|new
name|ScheduledThreadPoolExecutor
argument_list|(
literal|5
argument_list|,
operator|new
name|ThreadFactory
argument_list|()
block|{
specifier|public
name|Thread
name|newThread
parameter_list|(
name|Runnable
name|run
parameter_list|)
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
name|run
argument_list|)
decl_stmt|;
name|thread
operator|.
name|setPriority
argument_list|(
name|ThreadPriorities
operator|.
name|INBOUND_BROKER_CONNECTION
argument_list|)
expr_stmt|;
return|return
name|thread
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|/**      * @param location      * @throws IOException       */
specifier|public
name|ActiveIOTransportServer
parameter_list|(
name|URI
name|location
parameter_list|,
specifier|final
name|Map
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|server
operator|=
operator|new
name|ChannelFactory
argument_list|()
operator|.
name|bindAsyncChannel
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|server
operator|.
name|setAcceptListener
argument_list|(
operator|new
name|AcceptListener
argument_list|()
block|{
specifier|public
name|void
name|onAccept
parameter_list|(
name|Channel
name|c
parameter_list|)
block|{
if|if
condition|(
name|acceptListener
operator|==
literal|null
condition|)
block|{
name|c
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|WireFormat
name|format
init|=
operator|(
name|WireFormat
operator|)
name|wireFormatFactory
operator|.
name|createWireFormat
argument_list|()
decl_stmt|;
name|acceptListener
operator|.
name|onAccept
argument_list|(
name|ActiveIOTransportFactory
operator|.
name|configure
argument_list|(
name|c
argument_list|,
name|format
argument_list|,
name|options
argument_list|,
name|BROKER_CONNECTION_EXECUTOR
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|onAcceptError
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
if|if
condition|(
name|acceptListener
operator|!=
literal|null
condition|)
block|{
name|acceptListener
operator|.
name|onAcceptError
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setAcceptListener
parameter_list|(
name|TransportAcceptListener
name|acceptListener
parameter_list|)
block|{
name|this
operator|.
name|acceptListener
operator|=
name|acceptListener
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|server
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
name|server
operator|.
name|stop
argument_list|(
name|stopTimeout
argument_list|)
expr_stmt|;
name|server
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
specifier|public
name|URI
name|getConnectURI
parameter_list|()
block|{
return|return
name|server
operator|.
name|getConnectURI
argument_list|()
return|;
block|}
specifier|public
name|URI
name|getBindURI
parameter_list|()
block|{
return|return
name|server
operator|.
name|getBindURI
argument_list|()
return|;
block|}
specifier|public
name|WireFormatFactory
name|getWireFormatFactory
parameter_list|()
block|{
return|return
name|wireFormatFactory
return|;
block|}
specifier|public
name|void
name|setWireFormatFactory
parameter_list|(
name|WireFormatFactory
name|wireFormatFactory
parameter_list|)
block|{
name|this
operator|.
name|wireFormatFactory
operator|=
name|wireFormatFactory
expr_stmt|;
block|}
specifier|public
name|void
name|setBrokerInfo
parameter_list|(
name|BrokerInfo
name|brokerInfo
parameter_list|)
block|{     }
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
name|InetSocketAddress
name|getSocketAddress
parameter_list|()
block|{
comment|// TODO: need to drill into the server object to get the socket address
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

